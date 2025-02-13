package net.scit.DangoChan.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ReplyDTO {
	
	private Integer replyId;
	private Integer boardId;
	private Long userId;
	private String content;
	private LocalDateTime createAt;
	private Integer parentReplyId;
	

}
