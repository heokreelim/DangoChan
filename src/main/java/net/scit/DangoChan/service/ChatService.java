package net.scit.DangoChan.service;

import lombok.RequiredArgsConstructor;
import net.scit.DangoChan.dto.ChatMessage;
import net.scit.DangoChan.dto.ChatRoom;
import net.scit.DangoChan.repository.ChatRoomRepository;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    // 일반 채팅 메시지 전송 (끝말잇기 모드일 경우 검증 추가)
    public void sendChatMessage(ChatMessage chatMessage) {
        ChatRoom room = chatRoomRepository.findRoomById(chatMessage.getRoomId());
        if (room == null) return;

        // 끝말잇기 모드이면 규칙 검증
        if ("shiritori".equals(room.getRoomType())) {
            String newWord = chatMessage.getMessage().trim();
            if (newWord.isEmpty()) return;
            if (room.getLastWord() == null || room.getLastWord().isEmpty()) {
                // 첫 단어이면 그냥 수락
                room.setLastWord(newWord);
                chatRoomRepository.updateChatRoom(room);
            } else {
                String lastWord = room.getLastWord();
                String lastChar = lastWord.substring(lastWord.length() - 1);
                String firstChar = newWord.substring(0, 1);
                if (!firstChar.equals(lastChar)) {
                    // 규칙 위반: 시스템 메시지 전송 후 리턴
                    ChatMessage systemMsg = ChatMessage.builder()
                            .type(ChatMessage.MessageType.SYSTEM)
                            .roomId(room.getRoomId())
                            .sender("system")
                            .message("끝말잇기 규칙 위반: \"" + newWord + "\"은(는) \"" + lastChar + "\"(으)로 시작해야 합니다.")
                            .build();
                    messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), systemMsg);
                    return;
                } else {
                    // 규칙에 맞으면 업데이트
                    room.setLastWord(newWord);
                    chatRoomRepository.updateChatRoom(room);
                }
            }
        }
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
    }

    // 파일 메시지 전송
    public void sendFileMessage(ChatMessage chatMessage) {
        ChatRoom room = chatRoomRepository.findRoomById(chatMessage.getRoomId());
        if (room == null) return;
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
    }

    // 사용자 입장
    public void enterUser(String sessionId, String roomId) {
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        if (room != null) {
            room.getUserSet().add(sessionId);
            chatRoomRepository.updateChatRoom(room);
            sendSystemMessage(roomId, sessionId + "님이 들어왔습니다.");
        }
    }

    // 사용자 퇴장
    public void leaveUser(String sessionId, String roomId) {
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        if (room != null) {
            room.getUserSet().remove(sessionId);
            if (room.getUserSet().isEmpty()) {
                chatRoomRepository.deleteChatRoom(roomId);
            } else {
                chatRoomRepository.updateChatRoom(room);
            }
            sendSystemMessage(roomId, sessionId + "님이 나갔습니다.");
        }
    }

    // 시스템 메시지 전송
    private void sendSystemMessage(String roomId, String text) {
        ChatMessage systemMessage = ChatMessage.builder()
                .type(ChatMessage.MessageType.SYSTEM)
                .roomId(roomId)
                .sender("system")
                .message(text)
                .build();
        messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, systemMessage);
    }
}
