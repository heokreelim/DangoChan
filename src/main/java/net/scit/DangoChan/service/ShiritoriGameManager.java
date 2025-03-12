package net.scit.DangoChan.service;

import lombok.RequiredArgsConstructor;
import net.scit.DangoChan.dto.ChatMessage;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
public class ShiritoriGameManager {
    private final SimpMessageSendingOperations messagingTemplate;

    private ConcurrentHashMap<String, Boolean> gameStarted = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> lastWordByRoom = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, List<String>> turnOrderByRoom = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> currentTurnIndexByRoom = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ScheduledFuture<?>> turnTimers = new ConcurrentHashMap<>();
    // 추가: 각 방별로 사용된 단어(정규화된 형태)를 저장할 맵
    private ConcurrentHashMap<String, Set<String>> usedWordsByRoom = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public synchronized void startGame(String roomId, Set<String> playersSet) {
        if (gameStarted.getOrDefault(roomId, false)) return;
        gameStarted.put(roomId, true);
        List<String> players = new ArrayList<>(playersSet);
        Collections.shuffle(players);
        turnOrderByRoom.put(roomId, players);
        currentTurnIndexByRoom.put(roomId, 0);
        lastWordByRoom.put(roomId, ""); // 초기 단어 없음
        usedWordsByRoom.put(roomId, new HashSet<>());
        startTurnTimer(roomId);
    }

    /**
     * 끝말잇기 규칙(이전 단어의 마지막 글자와 새 단어의 첫 글자 비교)을 검증하고,
     * 단어가 유효하면 사용된 단어 집합에 추가합니다.
     */
    public synchronized boolean isValidWord(String roomId, String newWord) {
        if (newWord == null || newWord.isEmpty()) return false;
        if (newWord.charAt(newWord.length() - 1) == 'ん') {
            return false;
        }
        String lastWord = lastWordByRoom.get(roomId);
        boolean valid;
        if (lastWord == null || lastWord.isEmpty()) {
            valid = true;
        } else {
            char expected = normalizeChar(lastWord.charAt(lastWord.length() - 1));
            char actual = normalizeChar(newWord.charAt(0));
            valid = (expected == actual);
        }
        if (valid) {
            lastWordByRoom.put(roomId, newWord);
            // 단어를 정규화하여 사용된 단어 집합에 추가
            String normalized = removeProlongedSoundMark(convertToHiragana(newWord));
            Set<String> used = usedWordsByRoom.get(roomId);
            if (used == null) {
                used = new HashSet<>();
                usedWordsByRoom.put(roomId, used);
            }
            used.add(normalized);
        }
        return valid;
    }

    /**
     * 입력한 단어(newWord)를 정규화한 후, 해당 방에서 이미 사용된 단어인지 확인합니다.
     */
    public synchronized boolean isWordAlreadyUsed(String roomId, String newWord) {
        String normalized = removeProlongedSoundMark(convertToHiragana(newWord));
        Set<String> used = usedWordsByRoom.get(roomId);
        return used != null && used.contains(normalized);
    }

    private char normalizeChar(char c) {
        switch (c) {
            case 'ぁ': return 'あ';
            case 'ぃ': return 'い';
            case 'ぅ': return 'う';
            case 'ぇ': return 'え';
            case 'ぉ': return 'お';
            case 'ゃ': return 'や';
            case 'ゅ': return 'ゆ';
            case 'ょ': return 'よ';
        }
        if (c == 'が' || c == 'ぎ' || c == 'ぐ' || c == 'げ' || c == 'ご') return (char)(c - 1);
        if (c == 'ざ' || c == 'じ' || c == 'ず' || c == 'ぜ' || c == 'ぞ') return (char)(c - 1);
        if (c == 'だ' || c == 'ぢ' || c == 'づ' || c == 'で' || c == 'ど') return (char)(c - 1);
        if (c == 'ば' || c == 'び' || c == 'ぶ' || c == 'べ' || c == 'ぼ') return (char)(c - 2);
        if (c == 'ぱ' || c == 'ぴ' || c == 'ぷ' || c == 'ぺ' || c == 'ぽ') return (char)(c - 2);
        return c;
    }

    public synchronized void advanceTurn(String roomId) {
        ScheduledFuture<?> future = turnTimers.get(roomId);
        if (future != null) {
            future.cancel(true);
        }
        List<String> players = turnOrderByRoom.get(roomId);
        if (players == null || players.isEmpty()) return;
        int currentIndex = currentTurnIndexByRoom.get(roomId);
        int nextIndex = findNextActivePlayer(players, currentIndex);
        if (nextIndex == -1) {
            gameStarted.put(roomId, false);
        } else {
            currentTurnIndexByRoom.put(roomId, nextIndex);
            startTurnTimer(roomId);
        }
    }

    public synchronized void eliminatePlayer(String roomId) {
        List<String> players = turnOrderByRoom.get(roomId);
        if (players == null || players.isEmpty()) return;
        int currentIndex = currentTurnIndexByRoom.get(roomId);
        String eliminated = players.get(currentIndex);
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                ChatMessage.builder()
                        .type(ChatMessage.MessageType.SYSTEM)
                        .roomId(roomId)
                        .sender("system")
                        .message(eliminated + "님 탈락입니다.")
                        .build());
        players.set(currentIndex, null);
        long remaining = players.stream().filter(Objects::nonNull).count();
        if (remaining <= 1) {
            scheduler.schedule(() -> {
                for (String player : players) {
                    if (player != null) {
                        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                                ChatMessage.builder()
                                        .type(ChatMessage.MessageType.SYSTEM)
                                        .roomId(roomId)
                                        .sender("system")
                                        .message(player + "님 우승 축하합니다!")
                                        .build());
                        break;
                    }
                }
            }, 500, TimeUnit.MILLISECONDS);
            gameStarted.put(roomId, false);
        } else {
            int nextIndex = findNextActivePlayer(players, currentIndex);
            if (nextIndex == -1) {
                gameStarted.put(roomId, false);
            } else {
                currentTurnIndexByRoom.put(roomId, nextIndex);
                startTurnTimer(roomId);
                String nextPlayer = getCurrentPlayer(roomId);
                if (nextPlayer != null) {
                    messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                            ChatMessage.builder()
                                    .type(ChatMessage.MessageType.SYSTEM)
                                    .roomId(roomId)
                                    .sender("system")
                                    .message(nextPlayer + "님 차례입니다.")
                                    .build());
                }
            }
        }
    }

    private int findNextActivePlayer(List<String> players, int currentIndex) {
        int n = players.size();
        for (int i = 1; i < n; i++) {
            int idx = (currentIndex + i) % n;
            if (players.get(idx) != null) return idx;
        }
        return -1;
    }

    public synchronized String getCurrentPlayer(String roomId) {
        List<String> players = turnOrderByRoom.get(roomId);
        Integer index = currentTurnIndexByRoom.get(roomId);
        if (players == null || index == null) return null;
        return players.get(index);
    }

    public synchronized boolean isGameStarted(String roomId) {
        return gameStarted.getOrDefault(roomId, false);
    }

    public synchronized void resetGame(String roomId) {
        gameStarted.remove(roomId);
        lastWordByRoom.remove(roomId);
        turnOrderByRoom.remove(roomId);
        currentTurnIndexByRoom.remove(roomId);
        ScheduledFuture<?> future = turnTimers.remove(roomId);
        if (future != null) future.cancel(true);
        usedWordsByRoom.remove(roomId);
    }

    private synchronized void startTurnTimer(String roomId) {
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            eliminatePlayer(roomId);
        }, 30, TimeUnit.SECONDS);
        turnTimers.put(roomId, future);
    }

    /**
     * 히라가나 문자열에서 장음 기호(ー)를 제거합니다.
     */
    private static String removeProlongedSoundMark(String text) {
        return text.replaceAll("ー", "");
    }

    /**
     * 카타카나 문자를 히라가나로 변환합니다.
     * (예: "マフラー" → "まふらー")
     */
    private static String convertToHiragana(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 0x30A1 && c <= 0x30F6) {
                c = (char) (c - 0x60);
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
