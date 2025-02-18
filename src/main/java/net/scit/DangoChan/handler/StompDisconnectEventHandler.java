package net.scit.DangoChan.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.ChatRoom;
import net.scit.DangoChan.repository.ChatRoomRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompDisconnectEventHandler {

    private final ChatRoomRepository chatRoomRepository;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        log.info("Disconnected sessionId: {}", sessionId);

        // 모든 채팅방에서 해당 세션 제거
        List<ChatRoom> rooms = chatRoomRepository.findAllRooms();
        for (ChatRoom room : rooms) {
            if (room.getUserSet().contains(sessionId)) {
                room.getUserSet().remove(sessionId);
                if (room.getUserSet().isEmpty()) {
                    // 방에 아무도 없으면 삭제
                    chatRoomRepository.deleteChatRoom(room.getRoomId());
                } else {
                    chatRoomRepository.updateChatRoom(room);
                }
            }
        }
    }
}
