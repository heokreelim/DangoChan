package net.scit.DangoChan.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

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
        List<DeckEntity> decks = getRandomCategoryByUserId(userId).get().getDeckEntityList();
        if (decks == null || decks.isEmpty()) {
            return null; // 혹은 예외 처리
        }
        Random random = new Random();
        DeckEntity randomDeck = decks.get(random.nextInt(decks.size()));
        
        List<CardEntity> cardEntityList = getLimitedCardEntityList(randomDeck.getCardEntityList());
        
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
        if(cardEntityList == null || cardEntityList.isEmpty()) {
            return pairs;
        }
        
        for (CardEntity card : cardEntityList) {
            // word 객체 생성
            Map<String, Object> wordMap = new HashMap<>();
            wordMap.put("id", card.getCardId());
            wordMap.put("content", card.getWord());
            pairs.add(wordMap);
            
            // meaning 객체 생성
            Map<String, Object> meaningMap = new HashMap<>();
            meaningMap.put("id", card.getCardId());
            meaningMap.put("content", card.getMeaning());
            pairs.add(meaningMap);
        }
        return pairs;
    }

}
