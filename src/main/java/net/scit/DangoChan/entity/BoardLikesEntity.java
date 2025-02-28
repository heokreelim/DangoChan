package net.scit.DangoChan.entity;

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
import net.scit.DangoChan.dto.BoardLikesDTO;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Builder
@Entity
@Table(name="board_likes")
public class BoardLikesEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="boardLike_id")
	private Long boardLikeId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="board_id", nullable = false)
	private CommunityEntity communityEntity; // 게시글 N:1 관계
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable= false)
	private UserEntity userEntity; // 좋아요 누른 사용자 ID
	
	// DTO -> entity
	public static BoardLikesEntity toBoardLikesEntity(UserEntity userEntity, CommunityEntity communityEntity) {
		
		BoardLikesEntity boardLikesEntity = new BoardLikesEntity();
		
		boardLikesEntity.setUserEntity(userEntity);
		boardLikesEntity.setCommunityEntity(communityEntity);
		
		return boardLikesEntity;
		
	}

}
