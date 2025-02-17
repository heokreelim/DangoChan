package net.scit.DangoChan.service;

import net.scit.DangoChan.entity.CardEntity;
import net.scit.DangoChan.repository.CardRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.CategoryDTO;
import net.scit.DangoChan.entity.CategoryEntity;
import net.scit.DangoChan.repository.CategoryRepository;
import net.scit.DangoChan.dto.CardDTO;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashCardService {

	//private final Repository variable start
	private final CategoryRepository categoryRepository;
	private final CardRepository cardRepository;
	//private final Repository variable end
	
	//category start
	
		//AYH start

	/**
	 * insertCategory
	 * user가 입력한 카테고리의 이름을 저장하는 service
	 * @param categoryDTO
	 */
public void insertCategory(CategoryDTO categoryDTO) {

		CategoryEntity entity = CategoryEntity.toEntity(categoryDTO);
		log.info("카테고리 저장 {}", entity);

		categoryRepository.save(entity);

	}

		//AYH end
		
		//PJB start
		
		//PJB end
	
	//category end
	
	//deck start
	
		//AYH start
		
		//AYH end
			
		//PJB start
			
		//PJB end
	
	//deck end
	
	//card start
	
		//AYH start
		
		//AYH end
		
		//SYH start
        // 덱 ID를 기반으로 랜덤 카드 가져오기
        public CardDTO getCardByDeckId(Long deckId) {
            Optional<CardEntity> cardOpt = cardRepository.findCardByDeckId(deckId);

            return cardOpt.map(card -> new CardDTO(
                    card.getCardId(),
                    card.getDeckId(),
                    card.getCategoryId(),
                    card.getUserId(),
                    card.getWord(),
                    card.getPos(),
                    card.getMeaning(),
                    card.getExampleJp(),
                    card.getExampleKr(),
                    card.getStudyLevel()
            )).orElse(null);
        }
		//SYH end
	
	//card end
	
}
