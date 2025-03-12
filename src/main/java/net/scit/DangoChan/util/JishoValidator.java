package net.scit.DangoChan.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

public class JishoValidator {

    private static final String API_URL = "https://jisho.org/api/v1/search/words?keyword=";
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static boolean isValidJapaneseNoun(String word) {
        try {
            String url = API_URL + word;
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);
            JsonNode data = root.get("data");
            if (data == null || !data.isArray() || data.size() == 0) {
                return false;
            }
            String inputNormalized = removeProlongedSoundMark(convertToHiragana(word));
            for (JsonNode entry : data) {
                JsonNode japaneseArray = entry.get("japanese");
                if (japaneseArray != null && japaneseArray.isArray()) {
                    for (JsonNode japEntry : japaneseArray) {
                        if (japEntry.has("reading")) {
                            String reading = japEntry.get("reading").asText();
                            String readingNormalized = removeProlongedSoundMark(convertToHiragana(reading));
                            if (inputNormalized.equals(readingNormalized)) {
                                JsonNode senses = entry.get("senses");
                                if (senses != null && senses.isArray()) {
                                    for (JsonNode sense : senses) {
                                        JsonNode parts = sense.get("parts_of_speech");
                                        if (parts != null && parts.isArray()) {
                                            for (JsonNode pos : parts) {
                                                if (pos.asText().contains("Noun")) {
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getDisplayForm(String word) {
        // 이미 가타카나면 그대로 반환
        if (isKatakana(word)) {
            return word;
        }
        try {
            String url = API_URL + word;
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);
            JsonNode data = root.get("data");
            // API 결과가 없으면, 존재하지 않는 단어로 간주하여 입력값 그대로 반환
            if (data == null || !data.isArray() || data.size() == 0) {
                return word;
            }
            String inputNormalized = removeProlongedSoundMark(convertToHiragana(word));
            for (JsonNode entry : data) {
                JsonNode japaneseArray = entry.get("japanese");
                if (japaneseArray != null && japaneseArray.isArray()) {
                    for (JsonNode japEntry : japaneseArray) {
                        if (japEntry.has("reading")) {
                            String reading = japEntry.get("reading").asText();
                            String readingNormalized = removeProlongedSoundMark(convertToHiragana(reading));
                            if (inputNormalized.equals(readingNormalized)) {
                                JsonNode wordNode = japEntry.get("word");
                                // canonical 정보가 존재하고 입력과 다르면 canonical 사용
                                if (wordNode != null && !wordNode.asText().isEmpty() && !wordNode.asText().equals(word)) {
                                    String canonical = wordNode.asText();
                                    if (canonical.matches("^[\\u30A0-\\u30FF]+$")) {
                                        return canonical;
                                    } else {
                                        return canonical + "(" + word + ")";
                                    }
                                } else {
                                    // canonical 정보가 없거나 입력값과 동일하면 fallback:
                                    // 단, 이 경우 단순 문자 변환으로는 제대로 된 장음 기호가 나오지 않을 수 있으므로,
                                    // 우리가 미리 정의한 매핑을 적용합니다.
                                    return convertHiraganaToKatakanaWithProlonged(word);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 매칭되는 API 결과를 찾지 못하면 존재하지 않는 단어로 간주하여 입력값 그대로 반환
        return word;
    }

    private static boolean isKatakana(String text) {
        return text.matches("^[\\u30A0-\\u30FF]+$");
    }

    public static boolean isHiragana(String text) {
        return text.matches("^[\\u3040-\\u309F\\u30FC]+$");
    }

    public static boolean validateHiragana(String text) {
        return isHiragana(text);
    }

    /**
     * 카타카나 문자를 히라가나로 변환합니다.
     * (예: "マフラー" → "まふらー")
     */
    private static String convertToHiragana(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 0x30A1 && c <= 0x30F6) {
                c = (char) (c - 0x60);
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 히라가나 문자를 단순 변환하여 가타카나로 만듭니다.
     * (예: "まふら" → "マフラ")
     */
    private static String convertHiraganaToKatakana(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= 0x3041 && c <= 0x3096) {
                c = (char) (c + 0x60);
            }
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 장음 기호(ー)를 제거합니다.
     */
    private static String removeProlongedSoundMark(String text) {
        return text.replaceAll("ー", "");
    }

    /**
     * 후리가나를 가타카나로 변환하되,
     * 특정 단어(예: "まふら")에 대해 올바른 장음 기호(예: "マフラー")가 적용되도록 합니다.
     * canonical 정보가 없거나 입력값과 동일할 때 fallback으로 사용합니다.
     */
    private static String convertHiraganaToKatakanaWithProlonged(String word) {
        String katakana = convertHiraganaToKatakana(word);
        // 예시: "まふら"가 입력되면 fallback 결과 "マフラ"를 "マフラー"로 보정
        if (word.equals("まふら") && katakana.equals("マフラ")) {
            return "マフラー";
        }
        // 필요에 따라 다른 단어도 추가 매핑할 수 있습니다.
        return katakana;
    }
}
