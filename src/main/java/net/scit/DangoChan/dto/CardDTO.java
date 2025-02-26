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

	private String kanji;
	private String furigana;
	private String formattedRuby; // ✅ 한자+후리가나 자동 생성 필드
	
    // Entity --> DTO
    public static CardDTO toDTO(CardEntity cardEntity) {
		return CardDTO.builder()
				.cardId(cardEntity.getCardId())
//				.userId(cardEntity.getDeckEntity().getCategoryEntity().getUserId()) // 02.26 DDL 수정으로 주석 처리
				.categoryId(cardEntity.getDeckEntity().getCategoryEntity().getCategoryId())
				.deckId(cardEntity.getDeckEntity().getDeckId())
				.word(cardEntity.getWord())
				.kanji(getKanji(cardEntity.getWord()))
				.furigana(getFurigana(String.valueOf(cardEntity.getWord())))
//				.formattedRuby(formatRuby(cardEntity.getWord()))
				.pos(cardEntity.getPos())
				.meaning(cardEntity.getMeaning())
				.exampleJp(cardEntity.getExampleJp())
				.exampleKr(cardEntity.getExampleKr())
				.studyLevel(cardEntity.getStudyLevel())
				.build();
    }

	// 한자 추출 (대괄호 제거)
	public static String getKanji(String word) {
		return word.replaceAll("\\[[^\\]]*\\]", ""); // [] 안의 히라가나 제거
	}

	// 히라가나 추출 ([] 안의 글자만 가져오기)
	public static String getFurigana(String word) {
		StringBuilder furigana = new StringBuilder();
		java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\[(.*?)\\]").matcher(word);
		while (matcher.find()) {
			furigana.append(matcher.group(1)); // [] 안의 히라가나 추출
		}
		return furigana.toString();
	}

	// ✅ Thymeleaf에서 `kanji`와 `furigana`를 제대로 읽을 수 있도록 Getter 추가
	public String getKanji() {
		if (this.kanji == null || this.kanji.isEmpty()) {
			this.kanji = getKanji(this.word);
		}
		return this.kanji;
	}

	public String getFurigana() {
		if (this.furigana == null || this.furigana.isEmpty()) {
			this.furigana = getFurigana(this.word);
		}
		return this.furigana;
	}

	// ✅ <ruby> 태그 자동 생성 메서드 (각 한자에 후리가나 개별 적용)
//	public static String formatRuby(String word) {
//		StringBuilder rubyHtml = new StringBuilder("<ruby>");
//		java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("([^\\[]+)(?:\\[([^\\]]+)\\])?").matcher(word);
//
//		while (matcher.find()) {
//			String kanjiPart = matcher.group(1);    // 한자 부분
//			String furiganaPart = matcher.group(2); // 후리가나 부분 (있을 경우만)
//
//			rubyHtml.append("<rb>").append(kanjiPart).append("</rb>");
//			if (furiganaPart != null) {
//				rubyHtml.append("<rt>").append(furiganaPart).append("</rt>");
//			}
//		}
//		rubyHtml.append("</ruby>");
//		return rubyHtml.toString();
//	}
}
