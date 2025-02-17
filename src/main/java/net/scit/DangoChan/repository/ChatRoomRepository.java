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
    // Redis Hash 키
    private static final String CHAT_ROOMS = "CHAT_ROOM";

    // 모든 채팅방 조회
    public List<ChatRoom> findAllRooms() {
        Map<Object, Object> resultMap = redisTemplate.boundHashOps(CHAT_ROOMS).entries();
        if (resultMap == null) return new ArrayList<>();

        List<ChatRoom> chatRooms = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : resultMap.entrySet()) {
            chatRooms.add((ChatRoom) entry.getValue());
        }
        // roomId 기준 정렬(선택)
        chatRooms.sort(Comparator.comparing(ChatRoom::getRoomId));
        return chatRooms;
    }

    // 특정 채팅방 조회
    public ChatRoom findRoomById(String roomId) {
        return (ChatRoom) redisTemplate.boundHashOps(CHAT_ROOMS).get(roomId);
    }

    // 채팅방 생성
    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        redisTemplate.boundHashOps(CHAT_ROOMS).put(chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    // 채팅방 삭제
    public void deleteChatRoom(String roomId) {
        redisTemplate.boundHashOps(CHAT_ROOMS).delete(roomId);
    }

    // 채팅방 업데이트 (userSet 갱신 등)
    public void updateChatRoom(ChatRoom chatRoom) {
        redisTemplate.boundHashOps(CHAT_ROOMS).put(chatRoom.getRoomId(), chatRoom);
    }
}
