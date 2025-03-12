package net.scit.DangoChan.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

public class JishoValidator {

    private static final String API_URL = "https://jisho.org/api/v1/search/words?keyword=";
    private static final RestTemplate restTemplate = new RestTemplate();
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Jisho.org API를 호출하여 입력된 단어가 사전에 존재하며,
     * 하나 이상의 sense에 "Noun" (명사)이 포함되어 있는지 확인합니다.
     *
     * @param word 입력 단어 (히라가나만 허용된 상태여야 함)
     * @return 실제 존재하는 명사이면 true, 아니면 false
     */
    public static boolean isValidJapaneseNoun(String word) {
        try {
            String url = API_URL + word;
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = mapper.readTree(response);
            JsonNode data = root.get("data");
            if (data == null || !data.isArray() || data.size() == 0) {
                return false;
            }
            // 여러 결과 중 하나라도 명사("Noun")가 포함되면 true 반환
            for (JsonNode entry : data) {
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
        } catch (Exception e) {
            // API 호출 또는 파싱 중 문제가 발생하면, 콘솔에 에러 출력 후 false 반환
            e.printStackTrace();
        }
        return false;
    }
}
