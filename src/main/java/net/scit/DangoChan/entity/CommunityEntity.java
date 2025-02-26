package net.scit.DangoChan.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.DangoChan.dto.CommunityDTO;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Builder

@Entity
@Table(name="board")
@EntityListeners(AuditingEntityListener.class)

public class CommunityEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="board_id")
	private Integer boardId;
	
	
	@Column(name="user_id", nullable = false)
	private Long userId;
	
	@Column(name="title", nullable = false)
	private String title;
	
	@Column(name="word_count")
	private Integer wordCount;
	
	@Column(name="views")
	private Integer views;
	
	@Column(name="board_content")
	private String boardContent;
	
	@Column(name="original_file_name")
	private String originalFileName;
	
	@Column(name="saved_file_name")	
	private String savedFileName;
	
	@Column(name="create_date")
	@CreationTimestamp
	private LocalDateTime createDate;
	
	@Column(name="modify_date")
	@LastModifiedDate
	private LocalDateTime modifyDate;
	
	@Formula("(SELECT count(1) from reply r where board_id = r.board_id)")
	private int replyCount;

	//DTO -> entity
	public static CommunityEntity toEntity(CommunityDTO communityDTO) {
		return CommunityEntity.builder()
				.boardId(communityDTO.getBoardId())
				.userId(1L)	// 02.26 임시 수정
				.title(communityDTO.getTitle())
				.wordCount(communityDTO.getWordCount())
				.views(communityDTO.getViews())
				.boardContent(communityDTO.getBoardContent())
				.originalFileName(communityDTO.getOriginalFileName())
				.savedFileName(communityDTO.getSavedFileName())
				.build();
	}

}
