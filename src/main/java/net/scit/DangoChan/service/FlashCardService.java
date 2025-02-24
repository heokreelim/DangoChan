package net.scit.DangoChan.service;


import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.CardDTO;
import net.scit.DangoChan.dto.CategoryDTO;
import net.scit.DangoChan.dto.DeckDTO;
import net.scit.DangoChan.entity.CardEntity;
import net.scit.DangoChan.entity.CategoryEntity;
import net.scit.DangoChan.entity.DeckEntity;
import net.scit.DangoChan.repository.CardRepository;
import net.scit.DangoChan.repository.CategoryRepository;
import net.scit.DangoChan.repository.DeckRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashCardService {

	//private final Repository variable start
	private final CategoryRepository categoryRepository;
	private final DeckRepository deckRepository;
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

	/**
	 * 카테고리 이름 수정 처리
	 * @param categoryDTO
	 */
@Transactional
public void updateCategory(CategoryDTO categoryDTO) {
	// 1) 수정하려는 데이터가 있는지 확인
	Optional<CategoryEntity> temp = categoryRepository.findById(categoryDTO.getCategoryId());

	if (!temp.isPresent()) {
		return;
	}
	// 2) 있으면 dto -> entity로 변환
	// 3) 이름을 변경하여 데이터 베이스에 저장한다
	CategoryEntity entity = temp.get();
	entity.setCategoryName(categoryDTO.getCategoryName());
}

		//AYH end
		
		//PJB start
		
		//PJB end
	
	//category end
	
	//deck start
	
		//AYH start
		
public DeckDTO insertDeck(DeckDTO deckDTO) {
	// 1) 수정하려는 카테고리가 있는지 확인
		Optional<CategoryEntity> temp = categoryRepository.findById(deckDTO.getCategoryId());

		if (!temp.isPresent()) {
			return DeckDTO.toDTO(null);
		}
		// 2) 있으면 dto -> entity로 변환
		// 3) 이름을 변경하여 데이터 베이스에 저장한다
		CategoryEntity entity = temp.get();
	
	DeckEntity deckentity = DeckEntity.toEntity(deckDTO, entity);
	log.info("덱 저장 {}", deckentity);
	deckRepository.save(deckentity);
	
	return DeckDTO.toDTO(deckentity);
}



public void insertCard(CardDTO cardDTO) {
	// 1) 수정하려는 덱이 있는지 확인
			Optional<DeckEntity> temp = deckRepository.findById(cardDTO.getDeckId());

			if (!temp.isPresent()) {
				return;
			} else {
				
			}
			// 2) 있으면 dto -> entity로 변환
			// 3) 이름을 변경하여 데이터 베이스에 저장한다
			DeckEntity entity = temp.get();
		
		CardEntity cardEntity = CardEntity.toEntity(cardDTO, entity);
		log.info("카드 저장 {}", cardEntity);
		cardRepository.save(cardEntity);
}




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
