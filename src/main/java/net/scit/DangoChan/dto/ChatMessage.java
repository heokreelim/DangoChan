package net.scit.DangoChan.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessage {

    public enum MessageType {
        ENTER, TALK, FILE
    }

    private MessageType type;
    private String roomId;
    private String sender;     // 보낸 사람(브라우저별 세션ID 등)
    private String message;    // 텍스트 or 파일 URL
    private String fileName;   // 파일 이름 (파일 메시지일 경우)
}
