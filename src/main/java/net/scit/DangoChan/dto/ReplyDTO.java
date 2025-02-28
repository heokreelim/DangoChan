package net.scit.DangoChan.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.DangoChan.entity.ReplyEntity;

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
	
	// 추가: 작성자 이름
	private String userName;
	
	// coId의 정체? 엔티티에서 DTO로 변환하면서 가져온 값 
	public static ReplyDTO toDTO(ReplyEntity entity, Integer boardId) {
		return ReplyDTO.builder()
				.replyId(entity.getReplyId())
				.boardId(boardId)
				.userId(entity.getUserId())
				.content(entity.getContent())
				.createAt(entity.getCreateAt())
				.parentReplyId(entity.getParentReplyId())
				.userName(entity.getUser() != null ? entity.getUser().getUserName() : null)
				.build();
	}

}
