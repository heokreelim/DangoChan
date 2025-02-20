package net.scit.DangoChan.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Builder
public class ReplyDTO {
	
	private Integer replyId;
	private Integer boardId;
	private Long userId;
	private String content;
	private LocalDateTime createAt;
	private Integer parentReplyId;
	
	// coId의 정체? 엔티티에서 DTO로 변환하면서 가져온 값 
	

}
