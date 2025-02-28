package net.scit.DangoChan.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.DangoChan.entity.CategoryEntity;
import net.scit.DangoChan.entity.DeckEntity;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class CategoryDTO {
		// variables
	private Long categoryId;
	private Long userId;
	private String categoryName;
	private List<DeckEntity> deckEntityList;
	
	// Entity --> DTO
    public static CategoryDTO toDTO(CategoryEntity categoryEntity) {
		return CategoryDTO.builder()
				.categoryId(categoryEntity.getCategoryId())
				.userId(categoryEntity.getUserEntity().getUserId())
				.categoryName(categoryEntity.getCategoryName())
				.deckEntityList(categoryEntity.getDeckEntityList())
				.build();
    }
}
