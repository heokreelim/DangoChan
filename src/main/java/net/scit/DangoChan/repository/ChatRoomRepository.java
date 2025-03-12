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

    public List<ChatRoom> findAllRooms() {
        Map<Object, Object> result = redisTemplate.boundHashOps(CHAT_ROOMS).entries();
        if (result == null) return new ArrayList<>();
        List<ChatRoom> rooms = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : result.entrySet()) {
            rooms.add((ChatRoom) entry.getValue());
        }
        rooms.sort(Comparator.comparing(ChatRoom::getRoomId));
        return rooms;
    }

    public ChatRoom findRoomById(String roomId) {
        return (ChatRoom) redisTemplate.boundHashOps(CHAT_ROOMS).get(roomId);
    }

    public ChatRoom createChatRoom(String name, String roomType, int maxParticipants) {
        ChatRoom room = ChatRoom.create(name, roomType, maxParticipants);
        redisTemplate.boundHashOps(CHAT_ROOMS).put(room.getRoomId(), room);
        return room;
    }

    public void updateChatRoom(ChatRoom room) {
        redisTemplate.boundHashOps(CHAT_ROOMS).put(room.getRoomId(), room);
    }

    public void deleteChatRoom(String roomId) {
        redisTemplate.boundHashOps(CHAT_ROOMS).delete(roomId);
    }
}
