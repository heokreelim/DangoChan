package net.scit.DangoChan.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ChatRoom implements Serializable {
    private String roomId;
    private String name;
    private String roomType; // "chat", "shiritori", "quiz"
    private String lastWord; // 끝말잇기에서 마지막 단어
    private int maxParticipants; // 최대 참가 인원
    private Set<String> userSet = new HashSet<>();

    // 방 생성 메서드: 끝말잇기 모드이면 lastWord는 빈 문자열로 초기화
    public static ChatRoom create(String name, String roomType, int maxParticipants) {
        ChatRoom room = new ChatRoom();
        room.roomId = UUID.randomUUID().toString();
        room.name = name;
        room.roomType = roomType;
        room.lastWord = "";
        room.maxParticipants = maxParticipants;
        return room;
    }
}
