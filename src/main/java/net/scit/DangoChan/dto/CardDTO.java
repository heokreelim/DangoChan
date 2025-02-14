package net.scit.DangoChan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class CardDTO {
	// variables	
	private Long cardId;
    private Long deckId;
    private Long categoryId;
    private Long userId;
    private String word;
    private String furigana;
    private String pos;
    private String meaning;
    private String example;
    private Integer studyLevel;
	
    // Entity --> DTO
	
}
