package net.scit.DangoChan.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessage {

    public enum MessageType {
        ENTER, TALK, FILE, USER_UPDATE, SYSTEM
    }

    private MessageType type;  // 메시지 종류 (ENTER, TALK, FILE, USER_UPDATE, SYSTEM)
    private String roomId;     // 채팅방 ID
    private String sender;     // 메시지 보낸 사람 (시스템 메시지의 경우 "system")
    private String message;    // 메시지 내용 또는 파일 URL, 또는 시스템 알림 메시지
    private String fileName;   // 파일 메시지일 경우 파일 이름 (일반 메시지에는 사용하지 않음)
}
