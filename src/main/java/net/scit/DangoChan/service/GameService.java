package net.scit.DangoChan.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.entity.CardEntity;
import net.scit.DangoChan.entity.CategoryEntity;
import net.scit.DangoChan.entity.DeckEntity;
import net.scit.DangoChan.repository.CardRepository;
import net.scit.DangoChan.repository.CategoryRepository;
import net.scit.DangoChan.repository.DeckRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
	private final DeckRepository deckRepository;
	private final CardRepository cardRepository;
	private final CategoryRepository categoryRepository;
	
    // 모든 덱 중 무작위로 하나 선택하여 DeckDTO로 반환
    public List<Map<String, Object>> getRandomDeck(Long userId) {
        // 랜덤 카테고리에서 덱 리스트를 가져온 후 카드가 8장 이상인 덱만 필터링
        List<DeckEntity> decks = getRandomCategoryByUserId(userId)
        		.get()
        		.getDeckEntityList()
                .stream()
                .filter(deck -> deck.getCardEntityList() != null && deck.getCardEntityList().size() >= 8)
                .collect(Collectors.toList());

        // 조건에 맞는 덱이 없다면 null 또는 예외 처리
        if (decks == null || decks.isEmpty()) {
            return null; 
        }
        
        // 무작위 덱 선택
        Random random = new Random();
        DeckEntity randomDeck = decks.get(random.nextInt(decks.size()));
        
        // 선택된 덱의 카드 리스트 중 일부만 사용
        List<CardEntity> cardEntityList = getLimitedCardEntityList(randomDeck.getCardEntityList());
        
        // 카드 쌍으로 변환해서 반환
        return convertToPairs(cardEntityList);
    }
    
    /**
     * 해당 유저가 가진 카테고리 중 무작위 하나를 반환하는 메서드.
     * 카테고리가 없으면 Optional.empty()를 반환합니다.
     */
    public Optional<CategoryEntity> getRandomCategoryByUserId(Long userId) {
        List<CategoryEntity> categories = categoryRepository.findAllByUserEntity_UserId(userId);
        if (categories == null || categories.isEmpty()) {
            return Optional.empty();
        }
        Random random = new Random();
        CategoryEntity randomCategory = categories.get(random.nextInt(categories.size()));
        return Optional.of(randomCategory);
    }
    
    private List<CardEntity> getLimitedCardEntityList(List<CardEntity> cardEntityList) {
    	log.info("============ {}", cardEntityList);
        if (cardEntityList == null || cardEntityList.isEmpty()) {
            return Collections.emptyList();
        }
        if (cardEntityList.size() <= 8) {
            return new ArrayList<>(cardEntityList);
        } else {
            List<CardEntity> tempList = new ArrayList<>(cardEntityList);
            Collections.shuffle(tempList);
            // subList는 view를 반환하므로, 새로운 ArrayList로 감싸서 반환
            return new ArrayList<>(tempList.subList(0, 8));
        }
    }
    
    private List<Map<String, Object>> convertToPairs(List<CardEntity> cardEntityList) {
        List<Map<String, Object>> pairs = new ArrayList<>();
        if (cardEntityList == null || cardEntityList.isEmpty()) {
            return pairs;
        }

        for (CardEntity card : cardEntityList) {
            // ✅ word 분리
            String word = card.getWord();
            Map<String, String> extracted = extractKanjiAndFurigana(word);
            String kanji = extracted.get("kanji");
            String furigana = extracted.get("furigana");

            // ✅ 카드 ID와 word/kanji/furigana 정보를 담은 wordMap 생성
            Map<String, Object> wordMap = new HashMap<>();
            wordMap.put("id", card.getCardId());
            wordMap.put("word", word);         // 전체 word 그대로
            wordMap.put("kanji", kanji);       // 추출한 kanji
            wordMap.put("furigana", furigana); // 추출한 furigana

            pairs.add(wordMap);

            // ✅ 의미 카드도 추가 (기존 코드 유지)
            Map<String, Object> meaningMap = new HashMap<>();
            meaningMap.put("id", card.getCardId());
            meaningMap.put("content", card.getMeaning());

            pairs.add(meaningMap);
        }

        return pairs;
    }
    
    /**
     * word에서 kanji와 furigana를 추출
     * 예: 洗[せん]剤[ざい] → kanji=洗剤, furigana=せんざい
     */
    private Map<String, String> extractKanjiAndFurigana(String word) {
        StringBuilder kanjiBuilder = new StringBuilder();
        StringBuilder furiganaBuilder = new StringBuilder();

        boolean insideBracket = false;
        StringBuilder currentFurigana = new StringBuilder();

        for (char ch : word.toCharArray()) {
            if (ch == '[') {
                insideBracket = true;
                currentFurigana.setLength(0);  // reset
            } else if (ch == ']') {
                insideBracket = false;
                furiganaBuilder.append(currentFurigana);
            } else {
                if (insideBracket) {
                    currentFurigana.append(ch);
                } else {
                    kanjiBuilder.append(ch);
                }
            }
        }

        Map<String, String> result = new HashMap<>();
        result.put("kanji", kanjiBuilder.toString());
        result.put("furigana", furiganaBuilder.toString());
        return result;
    }

}
