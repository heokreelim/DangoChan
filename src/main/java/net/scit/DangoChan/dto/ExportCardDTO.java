package net.scit.DangoChan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.DangoChan.entity.CardEntity;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class ExportCardDTO {
	// variables	
	private Long cardId;
    private String word;
    private String pos;
    private String meaning;
    private String exampleJp;
    private String exampleKr;
	private Long deckId;
	private Integer studyLevel;
	
    // Entity --> DTO
    public static ExportCardDTO toDTO(CardEntity cardEntity) {
		return ExportCardDTO.builder()
				.cardId(cardEntity.getCardId())
				.word(cardEntity.getWord())
				.pos(cardEntity.getPos())
				.meaning(cardEntity.getMeaning())
				.exampleJp(cardEntity.getExampleJp())
				.exampleKr(cardEntity.getExampleKr())
				.build();
    }
}
