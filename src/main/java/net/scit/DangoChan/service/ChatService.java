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

    // 일반 채팅 메시지 전송
    public void sendChatMessage(ChatMessage chatMessage) {
        ChatRoom room = chatRoomRepository.findRoomById(chatMessage.getRoomId());
        if (room == null) return;

        // 메시지 저장 없이, 바로 WebSocket으로 브로드캐스트
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
    }

    // 파일 메시지 전송
    public void sendFileMessage(ChatMessage chatMessage) {
        ChatRoom room = chatRoomRepository.findRoomById(chatMessage.getRoomId());
        if (room == null) return;

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
    }

    // 유저가 방에 들어옴
    public void enterUser(String sessionId, String roomId) {
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        if (room != null) {
            room.getUserSet().add(sessionId);
            chatRoomRepository.updateChatRoom(room);
        }
    }

    // 유저가 방을 나감
    // 방에 사람이 아무도 없으면 삭제
    public void leaveUser(String sessionId, String roomId) {
        ChatRoom room = chatRoomRepository.findRoomById(roomId);
        if (room != null) {
            room.getUserSet().remove(sessionId);
            if (room.getUserSet().isEmpty()) {
                chatRoomRepository.deleteChatRoom(roomId);
            } else {
                chatRoomRepository.updateChatRoom(room);
            }
        }
    }
}
