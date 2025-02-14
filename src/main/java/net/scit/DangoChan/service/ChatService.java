package net.scit.DangoChan.service;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatService {
    private static final String CHAT_ROOMS = "chat:rooms";
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;

    public ChatService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash(); // ★ 여기서 바로 초기화!
    }

    // 채팅방 생성
    public String createRoom(String name) {
        String roomId = UUID.randomUUID().toString();
        hashOperations.put(CHAT_ROOMS, roomId, name);
        return roomId;
    }

    // 채팅방 목록 조회
    public Map<String, String> findAllRooms() {
        return hashOperations.entries(CHAT_ROOMS);
    }

    // 채팅방 삭제
    public void deleteRoom(String roomId) {
        hashOperations.delete(CHAT_ROOMS, roomId);
    }
}
