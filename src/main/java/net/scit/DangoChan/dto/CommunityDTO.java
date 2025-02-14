package net.scit.DangoChan.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

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
public class CommunityDTO {
	
	private Integer boardId;
	private Long userId;
	private Integer wordCount;
	private Integer views;
	private String upload;
	private LocalDateTime createDate;
	private LocalDateTime modifyDate;
	// 첨부파일을 받는 타입
	private MultipartFile uploadFile;
	// 업로드 파일이 있을 경우 View에서 사용하기 위해
	private String originalFileName;
	
	private String savedFileName;
	
	private int replyCount;
	
	//entity -> DTO
	
	
}
