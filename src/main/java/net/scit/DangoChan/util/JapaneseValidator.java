package net.scit.DangoChan.util;

public class JapaneseValidator {

    // 입력 텍스트가 오직 히라가나(Unicode 범위: U+3040 - U+309F)만으로 이루어졌는지 검사
    public static boolean isHiragana(String text) {
        return text.matches("^[\\u3040-\\u309F\\u30FC]+$");
    }

    // 히라가나 검증 메서드 (추후 확장이 필요하면 여기서 다른 조건도 추가할 수 있음)
    public static boolean validateHiragana(String text) {
        return isHiragana(text);
    }
}
