package net.scit.DangoChan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.DangoChan.entity.CardEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
				.formattedRuby(generateRubyTag(cardEntity.getWord()))
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

	// ✅ <ruby> 태그 자동 생성 메서드 (각 한자에 후리가나 개별 적용)
	public static String generateRubyTag(String word) {
		if (word == null || word.trim().isEmpty()) {
			return "";
		}

		// ✅ 한자 + 후리가나가 있는 경우 (ex: "日本[にほん]")
		if (word.contains("[")) {
			Matcher matcher = Pattern.compile("([一-龯]+)\\[([^\\]]+)\\]").matcher(word);
			StringBuilder rubyHtml = new StringBuilder("<ruby>");
			int lastIndex = 0;

			while (matcher.find()) {
				int matchStart = matcher.start();

				// ✅ 한자 앞의 히라가나, 특수문자 유지
				if (lastIndex < matchStart) {
					rubyHtml.append("<rb>").append(word, lastIndex, matchStart).append("</rb>");
				}

				String kanjiPart = matcher.group(1);
				String furiganaPart = matcher.group(2);
				List<String> furiganaList = splitFurigana(kanjiPart, furiganaPart);

				for (int i = 0; i < kanjiPart.length(); i++) {
					rubyHtml.append("<rb>").append(kanjiPart.charAt(i)).append("</rb>");
					rubyHtml.append("<rt>").append(furiganaList.get(i)).append("</rt>");
				}

				lastIndex = matcher.end();
			}

			// ✅ 마지막 남은 부분 처리
			if (lastIndex < word.length()) {
				rubyHtml.append("<rb>").append(word.substring(lastIndex)).append("</rb>");
			}

			rubyHtml.append("</ruby>");
			return rubyHtml.toString();
		}

		// ✅ 히라가나 또는 카타카나만 있는 경우 (한자가 없는 단어)
		if (word.matches("[ぁ-ゔァ-ヴー々〆〤]+")) {
			return "<ruby><rb>" + word + "</rb></ruby>";
		}

		// ✅ 일본어가 아닌 경우 (그대로 반환)
		return word;
	}


	// ✅ 한자 개수보다 후리가나 개수가 많을 경우 보정
	private static List<String> splitFurigana(String kanji, String furigana) {
		List<String> result = new ArrayList<>();
		int kanjiLen = kanji.length();
		int furiganaLen = furigana.length();

		// ✅ 한자 개수와 후리가나 길이가 다를 때 보정
		if (kanjiLen == furiganaLen) {
			for (int i = 0; i < kanjiLen; i++) {
				result.add(String.valueOf(furigana.charAt(i)));
			}
		} else {
			int avgSize = furiganaLen / kanjiLen;
			int remainder = furiganaLen % kanjiLen;
			int index = 0;

			for (int i = 0; i < kanjiLen; i++) {
				int size = avgSize + (i < remainder ? 1 : 0); // 나머지를 앞쪽에 분배
				result.add(furigana.substring(index, index + size));
				index += size;
			}
		}

		return result;
	}
}
