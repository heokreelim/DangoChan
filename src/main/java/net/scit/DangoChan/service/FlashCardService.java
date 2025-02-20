package net.scit.DangoChan.service;

import jakarta.transaction.Transactional;
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
	// ✅ Entity에서 데이터를 가져와 DTO로 변환
	public CardDTO getCardByDeckId(Long deckId) {
		// ✅ DB에서 `CardEntity` 가져오기
		CardEntity cardEntity = cardRepository.findCardByDeckId(deckId)
				.orElseThrow(() -> new RuntimeException("해당 덱에 카드가 없습니다."));

		// ✅ 디버깅 로그 추가
		System.out.println("🔥 [DEBUG] 랜덤으로 가져온 CardEntity: " + cardEntity);

		// ✅ Entity → DTO 변환 (CardDTO의 toDTO() 메서드 활용)
		CardDTO cardDTO = CardDTO.toDTO(cardEntity);

		// ✅ DTO로 변환된 데이터 확인
		System.out.println("🔥 [DEBUG] 변환된 CardDTO: " + cardDTO);

		return cardDTO;
	}

	@Transactional
    public void updateStudyLevel(Long cardId, Integer studyLevel) {
		Optional<CardEntity> cardEntity = cardRepository.findById(cardId); //optional로 null 체크
		cardEntity.ifPresent(card -> {
			card.setStudyLevel(studyLevel);
			cardRepository.save(card);
		});
    }
    //SYH end
	
	//card end
	
}
