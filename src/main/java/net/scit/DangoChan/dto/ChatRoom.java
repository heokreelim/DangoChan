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
    private String roomType; // "chat", "shiritori", "quiz" 등
    private String lastWord; // 끝말잇기 모드일 경우 마지막 단어
    private Set<String> userSet = new HashSet<>();

    // 방 생성 메서드: 끝말잇기 모드이면 lastWord를 초기화
    public static ChatRoom create(String name, String roomType) {
        ChatRoom room = new ChatRoom();
        room.roomId = UUID.randomUUID().toString();
        room.name = name;
        room.roomType = roomType;
        room.lastWord = ""; // 처음엔 빈 문자열
        return room;
    }
}
