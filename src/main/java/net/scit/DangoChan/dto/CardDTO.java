package net.scit.DangoChan.dto;

import lombok.*;
import net.scit.DangoChan.entity.CardEntity;

import java.time.LocalDate;
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
	// 변수들
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

	private LocalDate studiedAt;

	private String kanji;
	private String furigana;
	private String formattedFurigana; // ✅ `<span>` 기반 후리가나 필드로 변경

	// Entity --> DTO 변환
	public static CardDTO toDTO(CardEntity cardEntity) {
		return CardDTO.builder()
				.cardId(cardEntity.getCardId())
				.categoryId(cardEntity.getDeckEntity().getCategoryEntity().getCategoryId())
				.deckId(cardEntity.getDeckEntity().getDeckId())
				.word(cardEntity.getWord())
				.kanji(getKanji(cardEntity.getWord()))
				.furigana(getFurigana(cardEntity.getWord()))
				.formattedFurigana(generateFuriganaHtml(cardEntity.getWord())) // ✅ `<span>` 기반 후리가나 생성
				.pos(cardEntity.getPos())
				.meaning(cardEntity.getMeaning())
				.exampleJp(cardEntity.getExampleJp())
				.exampleKr(cardEntity.getExampleKr())
				.studyLevel(cardEntity.getStudyLevel())
				.studiedAt(cardEntity.getStudiedAt())
				.build();
	}

	// ✅ 한자 추출 (대괄호 제거)
	public static String getKanji(String word) {
		return word.replaceAll("\\[[^\\]]*\\]", ""); // [] 안의 히라가나 제거
	}

	// ✅ 히라가나 추출 ([] 안의 글자만 가져오기)
	public static String getFurigana(String word) {
		StringBuilder furigana = new StringBuilder();
		Matcher matcher = Pattern.compile("\\[(.*?)\\]").matcher(word);
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

	// ✅ <span> 태그 기반 후리가나 생성 메서드
	public static String generateFuriganaHtml(String word) {
		if (word == null || word.trim().isEmpty()) {
			return "";
		}

		// ✅ 한자 + 후리가나가 있는 경우 (예: "繰[く]り返[かえ]す")
		if (word.contains("[")) {
			Matcher matcher = Pattern.compile("([一-龯]+)\\[([^\\]]+)\\]").matcher(word);
			StringBuilder html = new StringBuilder("<div class=\"furigana\">");
			int lastIndex = 0;

			while (matcher.find()) {
				int matchStart = matcher.start();

				// ✅ 한자가 나오기 전까지의 히라가나/문자 그대로 추가
				if (lastIndex < matchStart) {
					html.append(word, lastIndex, matchStart); // 태그 없이 그대로 추가
				}

				String kanjiPart = matcher.group(1);
				String furiganaPart = matcher.group(2);
				List<String> furiganaList = splitFurigana(kanjiPart, furiganaPart);

				// ✅ 한자 + 후리가나를 <span>으로 감싸기
				html.append("<span class=\"furigana-item\">");
				for (int i = 0; i < kanjiPart.length(); i++) {
					html.append("<span class=\"furigana-text\">").append(kanjiPart.charAt(i)).append("</span>");
					html.append("<span class=\"furigana-kana\">").append(furiganaList.get(i)).append("</span>");
				}
				html.append("</span>");

				lastIndex = matcher.end();
			}

			// ✅ 마지막 남은 히라가나/문자 처리 (태그 없이 추가)
			if (lastIndex < word.length()) {
				html.append(word.substring(lastIndex));
			}

			html.append("</div>");
			return html.toString();
		}

		// ✅ 히라가나만 있는 경우 (한자가 없음) → 태그 없이 그대로 반환
		if (word.matches("[ぁ-ゔァ-ヴー々〆〤]+")) {
			return "<div class=\"furigana\">" + word + "</div>";
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
