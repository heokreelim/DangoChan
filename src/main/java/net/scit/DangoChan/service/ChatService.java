package net.scit.DangoChan.service;

import lombok.RequiredArgsConstructor;
import net.scit.DangoChan.dto.ChatMessage;
import net.scit.DangoChan.dto.ChatRoom;
import net.scit.DangoChan.repository.ChatRoomRepository;
import net.scit.DangoChan.util.JishoValidator;
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

        if ("shiritori".equals(room.getRoomType()) && shiritoriGameManager.isGameStarted(room.getRoomId())) {
            String currentPlayer = shiritoriGameManager.getCurrentPlayer(room.getRoomId());
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
            if (!JishoValidator.validateHiragana(newWord)) {
                ChatMessage systemMsg = ChatMessage.builder()
                        .type(ChatMessage.MessageType.SYSTEM)
                        .roomId(room.getRoomId())
                        .sender("system")
                        .message("입력된 단어는 히라가나만 허용됩니다.")
                        .build();
                messagingTemplate.convertAndSendToUser(chatMessage.getSender(), "/queue/private", systemMsg);
                return;
            }
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
            if (shiritoriGameManager.isWordAlreadyUsed(room.getRoomId(), newWord)) {
                ChatMessage systemMsg = ChatMessage.builder()
                        .type(ChatMessage.MessageType.SYSTEM)
                        .roomId(room.getRoomId())
                        .sender("system")
                        .message("이미 사용한 단어입니다.")
                        .build();
                messagingTemplate.convertAndSendToUser(chatMessage.getSender(), "/queue/private", systemMsg);
                return;
            }
            boolean valid = shiritoriGameManager.isValidWord(room.getRoomId(), newWord);
            if (!valid) {
                ChatMessage systemMsg = ChatMessage.builder()
                        .type(ChatMessage.MessageType.SYSTEM)
                        .roomId(room.getRoomId())
                        .sender("system")
                        .message("끝말잇기 규칙 위반: \"" + newWord + "\"은(는) 이전 단어의 마지막 글자와 맞지 않습니다.")
                        .build();
                messagingTemplate.convertAndSendToUser(chatMessage.getSender(), "/queue/private", systemMsg);
                return;
            }
            String displayWord = JishoValidator.getDisplayForm(newWord);
            chatMessage.setMessage(displayWord);
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);

            shiritoriGameManager.advanceTurn(room.getRoomId());
            String nextPlayer = shiritoriGameManager.getCurrentPlayer(room.getRoomId());
            if (nextPlayer != null && !nextPlayer.equals(chatMessage.getSender())) {
                ChatMessage turnMsg = ChatMessage.builder()
                        .type(ChatMessage.MessageType.SYSTEM)
                        .roomId(room.getRoomId())
                        .sender("system")
                        .message(nextPlayer + "님 차례입니다.")
                        .build();
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
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
    }

    public void sendFileMessage(ChatMessage chatMessage) {
        ChatRoom room = chatRoomRepository.findRoomById(chatMessage.getRoomId());
        if (room == null) return;
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
    }

    // 수정: displayName(사용자 이름) 사용
    public void enterUser(String displayName, String roomId) {
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
            room.getUserSet().add(displayName);
            chatRoomRepository.updateChatRoom(room);
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId,
                    ChatMessage.builder()
                            .type(ChatMessage.MessageType.SYSTEM)
                            .roomId(roomId)
                            .sender("system")
                            .message(displayName + "님이 들어왔습니다.")
                            .build());
        }
    }

    // 수정: displayName(사용자 이름) 사용
    public void leaveUser(String displayName, String roomId) {
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        if (room != null) {
            room.getUserSet().remove(displayName);
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
                            .message(displayName + "님이 나갔습니다.")
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
