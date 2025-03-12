package net.scit.DangoChan.service;

import lombok.RequiredArgsConstructor;
import net.scit.DangoChan.dto.ChatMessage;
import net.scit.DangoChan.dto.ChatRoom;
import net.scit.DangoChan.repository.ChatRoomRepository;
import net.scit.DangoChan.util.JishoValidator;
import net.scit.DangoChan.util.JapaneseValidator;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ShiritoriGameManager shiritoriGameManager;

    public void sendChatMessage(ChatMessage chatMessage) {
        ChatRoom room = chatRoomRepository.findRoomById(chatMessage.getRoomId());
        if (room == null) return;

        // 끝말잇기 모드 및 게임이 시작된 경우
        if ("shiritori".equals(room.getRoomType()) && shiritoriGameManager.isGameStarted(room.getRoomId())) {
            String currentPlayer = shiritoriGameManager.getCurrentPlayer(room.getRoomId());
            // 현재 턴 플레이어가 아닌 경우 – 입력자에게만 프라이빗 메시지 전송
            if (!chatMessage.getSender().equals(currentPlayer)) {
                ChatMessage systemMsg = ChatMessage.builder()
                        .type(ChatMessage.MessageType.SYSTEM)
                        .roomId(room.getRoomId())
                        .sender("system")
                        .message("현재는 " + currentPlayer + "님 차례입니다.")
                        .build();
                messagingTemplate.convertAndSendToUser(chatMessage.getSender(), "/queue/private", systemMsg);
                return;
            }
            String newWord = chatMessage.getMessage().trim();
            if (newWord.isEmpty()) return;
            // 히라가나만 허용 검증
            if (!JapaneseValidator.validateHiragana(newWord)) {
                ChatMessage systemMsg = ChatMessage.builder()
                        .type(ChatMessage.MessageType.SYSTEM)
                        .roomId(room.getRoomId())
                        .sender("system")
                        .message("입력된 단어는 히라가나만 허용됩니다.")
                        .build();
                messagingTemplate.convertAndSendToUser(chatMessage.getSender(), "/queue/private", systemMsg);
                return;
            }
            // 'ん'으로 끝나면 즉시 패배 처리
            if (newWord.charAt(newWord.length() - 1) == 'ん') {
                ChatMessage systemMsg = ChatMessage.builder()
                        .type(ChatMessage.MessageType.SYSTEM)
                        .roomId(room.getRoomId())
                        .sender("system")
                        .message(chatMessage.getSender() + "님, 'ん'으로 끝나는 단어를 말하여 패배입니다.")
                        .build();
                messagingTemplate.convertAndSend("/sub/chat/room/" + room.getRoomId(), systemMsg);
                shiritoriGameManager.eliminatePlayer(room.getRoomId());
                return;
            }
            // 실제 존재하는 명사 단어인지 검증 (JishoValidator 사용)
            if (!JishoValidator.isValidJapaneseNoun(newWord)) {
                ChatMessage systemMsg = ChatMessage.builder()
                        .type(ChatMessage.MessageType.SYSTEM)
                        .roomId(room.getRoomId())
                        .sender("system")
                        .message("입력된 단어는 실제 일본어 명사로 확인되지 않습니다.")
                        .build();
                messagingTemplate.convertAndSendToUser(chatMessage.getSender(), "/queue/private", systemMsg);
                return;
            }
            // 끝말잇기 규칙 검증
            boolean valid = shiritoriGameManager.isValidWord(room.getRoomId(), newWord);
            if (!valid) {
                ChatMessage systemMsg = ChatMessage.builder()
                        .type(ChatMessage.MessageType.SYSTEM)
                        .roomId(room.getRoomId())
                        .sender("system")
                        .message("끝말잇기 규칙 위반: \"" + newWord + "\"은(는) 이전 단어의 마지막 글자와 맞지 않습니다.")
                        .build();
                // 규칙 위반 메시지도 프라이빗으로 전송
                messagingTemplate.convertAndSendToUser(chatMessage.getSender(), "/queue/private", systemMsg);
                return;
            }
            // 올바른 단어 입력 시 단어 메시지 전체 방송
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);

            // 턴 전환: 약간의 지연 후에 "XX님 차례입니다." 메시지 전송
            shiritoriGameManager.advanceTurn(room.getRoomId());
            String nextPlayer = shiritoriGameManager.getCurrentPlayer(room.getRoomId());
            if (nextPlayer != null && !nextPlayer.equals(chatMessage.getSender())) {
                ChatMessage turnMsg = ChatMessage.builder()
                        .type(ChatMessage.MessageType.SYSTEM)
                        .roomId(room.getRoomId())
                        .sender("system")
                        .message(nextPlayer + "님 차례입니다.")
                        .build();
                // 새 스레드를 통해 200ms 후 전송 (딜레이)
                new Thread(() -> {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    messagingTemplate.convertAndSend("/sub/chat/room/" + room.getRoomId(), turnMsg);
                }).start();
            }
            return;
        }
        // 일반 채팅 모드
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
    }

    public void sendFileMessage(ChatMessage chatMessage) {
        ChatRoom room = chatRoomRepository.findRoomById(chatMessage.getRoomId());
        if (room == null) return;
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
    }

    public void enterUser(String sessionId, String roomId) {
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        if (room != null) {
            if ("shiritori".equals(room.getRoomType()) && shiritoriGameManager.isGameStarted(roomId)) {
                messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                        ChatMessage.builder()
                                .type(ChatMessage.MessageType.SYSTEM)
                                .roomId(roomId)
                                .sender("system")
                                .message("게임이 시작되어 입장할 수 없습니다.")
                                .build());
                return;
            }
            if (room.getUserSet().size() >= room.getMaxParticipants()) {
                messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                        ChatMessage.builder()
                                .type(ChatMessage.MessageType.SYSTEM)
                                .roomId(roomId)
                                .sender("system")
                                .message("정원이 가득 찼습니다.")
                                .build());
                return;
            }
            room.getUserSet().add(sessionId);
            chatRoomRepository.updateChatRoom(room);
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                    ChatMessage.builder()
                            .type(ChatMessage.MessageType.SYSTEM)
                            .roomId(roomId)
                            .sender("system")
                            .message(sessionId + "님이 들어왔습니다.")
                            .build());
        }
    }

    public void leaveUser(String sessionId, String roomId) {
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        if (room != null) {
            room.getUserSet().remove(sessionId);
            if (room.getUserSet().isEmpty()) {
                chatRoomRepository.deleteChatRoom(roomId);
                shiritoriGameManager.resetGame(roomId);
            } else {
                chatRoomRepository.updateChatRoom(room);
            }
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                    ChatMessage.builder()
                            .type(ChatMessage.MessageType.SYSTEM)
                            .roomId(roomId)
                            .sender("system")
                            .message(sessionId + "님이 나갔습니다.")
                            .build());
        }
    }

    public void startGame(String roomId) {
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        if (room == null) return;
        if (!"shiritori".equals(room.getRoomType())) return;
        shiritoriGameManager.startGame(roomId, room.getUserSet());
        String currentPlayer = shiritoriGameManager.getCurrentPlayer(roomId);
        String message = "게임이 시작되었습니다.";
        if (currentPlayer != null) {
            message += " " + currentPlayer + "님 차례입니다.";
        }
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                ChatMessage.builder()
                        .type(ChatMessage.MessageType.SYSTEM)
                        .roomId(roomId)
                        .sender("system")
                        .message(message)
                        .build());
    }
}
