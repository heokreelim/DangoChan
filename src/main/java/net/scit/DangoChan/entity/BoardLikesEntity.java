package net.scit.DangoChan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name="boardLikes")
public class BoardLikesEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="boardLike_id")
	private Long boardLikeId;
	
	@ManyToOne
	@JoinColumn(name="board_id", nullable = false)
	private CommunityEntity board; // 게시글 N:1 관계
	
	@Column(name="user_id", nullable= false)
	private Long userId; // 좋아요 누른 사용자 ID
	
	// DTO -> entity
	public static BoardLikesEntity toEntity(BoardLikesDTO boardLikesDTO, CommunityEntity board) {
		return BoardLikesEntity.builder()
				.board(board)
				.userId(boardLikesDTO.getUserId())
				.build();
	}

}
