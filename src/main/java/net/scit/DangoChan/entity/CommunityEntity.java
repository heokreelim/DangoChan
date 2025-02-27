package net.scit.DangoChan.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable = false)
	private UserEntity user;
	
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
	public static CommunityEntity toEntity(CommunityDTO communityDTO, UserEntity userEntity) {
		return CommunityEntity.builder()
				.boardId(communityDTO.getBoardId())
				.user(userEntity)
				.title(communityDTO.getTitle())
				.wordCount(communityDTO.getWordCount())
				.views(communityDTO.getViews())
				.boardContent(communityDTO.getBoardContent())
				.originalFileName(communityDTO.getOriginalFileName())
				.savedFileName(communityDTO.getSavedFileName())
				.build();
	}

}
