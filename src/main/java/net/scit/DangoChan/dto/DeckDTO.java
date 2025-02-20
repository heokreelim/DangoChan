package net.scit.DangoChan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.DangoChan.entity.DeckEntity;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class DeckDTO {
	// variables
	private Long deckId;
    private Long categoryId;
    private Long userId;
    private String deckName;
	
 // Entity --> DTO
    public static DeckDTO toDTO(DeckEntity deckEntity) {
		return DeckDTO.builder()
				.deckId(deckEntity.getDeckId())
				.categoryId(deckEntity.getCategoryEntity().getCategoryId())
				.userId(deckEntity.getCategoryEntity().getUserId())
				.deckName(deckEntity.getDeckName())
				.build();
    }
}
