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
public class CardDTO {
	// variables	
	private Long cardId;
    private Long deckId;
    private Long categoryId;
    private Long userId;
    private String word;
    private String pos;
    private String meaning;
    private String exampleJp;
    private String exampleKr;
    private Integer studyLevel;
	
    // Entity --> DTO
    public static CardDTO toDTO(CardEntity cardEntity) {
		return CardDTO.builder()
				.cardId(cardEntity.getCardId())
				.userId(cardEntity.getUserId())
				.categoryId(cardEntity.getCategoryId())
				.deckId(cardEntity.getDeckId())
				.word(cardEntity.getWord())
				.pos(cardEntity.getPos())
				.meaning(cardEntity.getMeaning())
				.exampleJp(cardEntity.getExampleJp())
				.exampleKr(cardEntity.getExampleKr())
				.studyLevel(cardEntity.getStudyLevel())
				.build();
    }

	// 한자 추출 (대괄호 제거)
	public String getKanji() {
		return word.replaceAll("\\[[^\\]]*\\]", ""); // [] 안의 히라가나 제거
	}

	// 히라가나 추출 ([] 안의 글자만 가져오기)
	public String getFurigana() {
		StringBuilder furigana = new StringBuilder();
		java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\[(.*?)\\]").matcher(word);
		while (matcher.find()) {
			furigana.append(matcher.group(1)); // [] 안의 히라가나 추출
		}
		return furigana.toString();
	}


}
