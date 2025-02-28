package net.scit.DangoChan.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import net.scit.DangoChan.dto.ReplyDTO;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Builder
@Entity
@Table(name="reply")
public class ReplyEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="reply_id")
	private Integer replyId;
	
	// 중요한 것: 게시글 하나에 댓글은 여러 개 달릴 수 있다.
	// 그러니까 댓글의 입장에서는 게시글과 다대일 관계인 것이지.
	// 다대일의 관계(=자식)
	@ManyToOne(fetch = FetchType.LAZY) // 이렇게 manytoone 설정했으면 나중에 부모표 가서도 onetomany 설정해 줘야 한다! 잊지 말 것
	@JoinColumn(name="board_id")
	private CommunityEntity communityEntity;
	
//	private Integer boardId;
	
	@Column(name="user_id")
	private Long userId;
	
	@Column(name="content", length = 100)
	private String content;

	@Column(name="created_at")
	@CreationTimestamp
	private LocalDateTime createAt;
	
	@Column(name="parent_reply_id")
	private Integer parentReplyId;
	
	// 추가: 사용자 정보와의 연관 관계
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", insertable = false, updatable = false)
	private UserEntity user;
	
	//엔티티로 값이 두 개가 넘어온다는 것이 포인트.
	// DTO -> Entity; toEntity();
	public static ReplyEntity toEntity(ReplyDTO replyDTO, CommunityEntity entity) {
		return ReplyEntity.builder()
				.replyId(replyDTO.getReplyId())
				.communityEntity(entity)
				.userId(replyDTO.getUserId())
				.content(replyDTO.getContent())
				.parentReplyId(replyDTO.getParentReplyId())
				.build();
	}

}
