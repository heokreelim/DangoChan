package net.scit.DangoChan.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.DangoChan.entity.CommunityEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Builder
public class CommunityDTO {
	
	private Integer boardId;
	private Long userId;
	private String title;
	private Integer wordCount;
	private Integer views;
	private String boardContent;
	private String upload;
	private LocalDateTime createDate;
	private LocalDateTime modifyDate;
	// 첨부파일을 받는 타입
	private MultipartFile uploadFile;
	// 업로드 파일이 있을 경우 View에서 사용하기 위해
	private String originalFileName;
	
	private String savedFileName;
	
	// 댓글 개수(BoardEntity에서 @Formula를 이용해서 조회)
	private int replyCount;
	
	// 좋아요 개수
	private int likeCount;
	
	//entity -> DTO
	public static CommunityDTO toDTO(CommunityEntity communityEntity) {
		return CommunityDTO.builder()
				.boardId(communityEntity.getBoardId())
				.userId(communityEntity.getUserId())
				.title(communityEntity.getTitle())
				.wordCount(communityEntity.getWordCount())
				.views(communityEntity.getViews())
				.boardContent(communityEntity.getBoardContent())
				.createDate(communityEntity.getCreateDate())
				.modifyDate(communityEntity.getModifyDate())
				.originalFileName(communityEntity.getOriginalFileName())
				.savedFileName(communityEntity.getSavedFileName())
				.replyCount(communityEntity.getReplyCount())
				.build();
	}
	
}
