package net.scit.DangoChan.repository;

import lombok.RequiredArgsConstructor;
import net.scit.DangoChan.dto.ChatRoom;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CHAT_ROOMS = "CHAT_ROOM";

    // 모든 채팅방 조회
    public List<ChatRoom> findAllRooms() {
        Map<Object, Object> result = redisTemplate.boundHashOps(CHAT_ROOMS).entries();
        if (result == null) return new ArrayList<>();
        List<ChatRoom> rooms = new ArrayList<>();
        for (Map.Entry<Object, Object> e : result.entrySet()) {
            rooms.add((ChatRoom) e.getValue());
        }
        rooms.sort(Comparator.comparing(ChatRoom::getRoomId));
        return rooms;
    }

    // 특정 채팅방 조회
    public ChatRoom findRoomById(String roomId) {
        return (ChatRoom) redisTemplate.boundHashOps(CHAT_ROOMS).get(roomId);
    }

    // 채팅방 생성: roomType도 전달받습니다.
    public ChatRoom createChatRoom(String name, String roomType) {
        ChatRoom room = ChatRoom.create(name, roomType);
        redisTemplate.boundHashOps(CHAT_ROOMS).put(room.getRoomId(), room);
        return room;
    }

    // 채팅방 삭제
    public void deleteChatRoom(String roomId) {
        redisTemplate.boundHashOps(CHAT_ROOMS).delete(roomId);
    }

    // 채팅방 업데이트
    public void updateChatRoom(ChatRoom room) {
        redisTemplate.boundHashOps(CHAT_ROOMS).put(room.getRoomId(), room);
    }
}
