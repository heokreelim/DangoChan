package net.scit.DangoChan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.DangoChan.entity.BoardLikesEntity;
import net.scit.DangoChan.entity.UserEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

@Builder
public class BoardLikesDTO {
	
	private Long boardLikeId;
	private Integer boardId;
	private Long userId;
	
	private Integer likeCount;
	
	public static BoardLikesDTO toDTO(BoardLikesEntity boardLikesEntity, UserEntity userEntity) {
		return BoardLikesDTO.builder()
				.boardLikeId(boardLikesEntity.getBoardLikeId())
				.boardId(boardLikesEntity.getCommunityEntity().getBoardId())
				.userId(boardLikesEntity.getUserEntity().getUserId())
				.build();
	}

}
