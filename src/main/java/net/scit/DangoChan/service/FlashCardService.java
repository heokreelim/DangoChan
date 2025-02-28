package net.scit.DangoChan.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.CardDTO;
import net.scit.DangoChan.dto.CategoryDTO;
import net.scit.DangoChan.dto.DeckDTO;
import net.scit.DangoChan.dto.ExportCardDTO;
import net.scit.DangoChan.entity.CardEntity;
import net.scit.DangoChan.entity.CategoryEntity;
import net.scit.DangoChan.entity.DeckEntity;
import net.scit.DangoChan.entity.UserEntity;
import net.scit.DangoChan.repository.CardRepository;
import net.scit.DangoChan.repository.CategoryRepository;
import net.scit.DangoChan.repository.DeckRepository;
import net.scit.DangoChan.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashCardService {

	//private final Repository variable start
	private final UserRepository userRepository;
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
	@Transactional
	public void insertCategory(CategoryDTO categoryDTO) {
		// 1) 수정하려는 카테고리가 있는지 확인
				Optional<UserEntity> temp = userRepository.findById(1L);

				if (!temp.isPresent()) {
					return;
				}
				// 2) 있으면 dto -> entity로 변환
				// 3) 이름을 변경하여 데이터 베이스에 저장한다
				UserEntity entity = temp.get();
		
		CategoryEntity categoryEntity = CategoryEntity.toEntity(categoryDTO, entity);
		log.info("카테고리 저장 {}", categoryEntity);

		categoryRepository.save(categoryEntity);

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
	categoryRepository.save(entity);
}

		//AYH end
		
		//PJB start
		
		//PJB end
	
	//category end
	
	//deck start
	
		//AYH start

/**
 * 덱 생성과, 덱 불러오기 기능을 사용하였을 때 동작하는 코드
 * @param deckDTO
 * @return
 */
@Transactional
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


@Transactional
public DeckDTO getDeckByDeckId(Long deckId) {
	// 1) 수정하려는 데이터가 있는지 확인
	Optional<DeckEntity> temp = deckRepository.findById(deckId);

	if (!temp.isPresent()) {
		return null;
	}
	// 2) 있으면 dto -> entity로 변환
	// 3) 이름을 변경하여 데이터 베이스에 저장한다
	DeckEntity entity = temp.get();
//	entity.setCategoryName(categoryDTO.getCategoryName());
	return DeckDTO.toDTO(entity);
}


@Transactional
public void updateDeck(DeckDTO deckDTO) {
	// 1) 수정하려는 데이터가 있는지 확인
	Optional<DeckEntity> temp = deckRepository.findById(deckDTO.getDeckId());

	if (!temp.isPresent()) {
		return;
	}
	// 2) 있으면 dto -> entity로 변환
	// 3) 이름을 변경하여 데이터 베이스에 저장한다
	DeckEntity entity = temp.get();
	entity.setDeckName(deckDTO.getDeckName());
	deckRepository.save(entity);
}


		//AYH end
			
		//PJB start
			
		//PJB end
	
	//deck end
	
	//card start
	
		//AYH start
/**
 * 덱을 생성하면서 입력된 카드 속성을 저장
 * @param cardDTO
 */
@Transactional
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

public List<ExportCardDTO> getCardsByDeckId(Long deckId) {
	// 1) 수정하려는 카테고리가 있는지 확인
				Optional<DeckEntity> temp = deckRepository.findById(deckId);
			
				List<CardEntity> cardList = cardRepository.findAllByDeckEntity(temp);
				
				log.info("댓글 갯수 : {}", cardList.size());
				
				List<ExportCardDTO> cardDTOList = new ArrayList<>();
				
				cardList.forEach((entity) -> cardDTOList.add(ExportCardDTO.toDTO(entity)));
				for (ExportCardDTO exportCardDTO : cardDTOList) {
					System.out.println(exportCardDTO.toString());
				}

				// DTO로 변환
				return cardDTOList;				
}

/**
 * 덱 편집시 카드 편집 내용을 저장하는 메서드
 * @param cards
 */
@Transactional
public void updateCards(List<ExportCardDTO> cards) {
	for (ExportCardDTO card : cards) {
        Optional<CardEntity> temp = cardRepository.findById(card.getCardId());
        
        if (!temp.isPresent()) {
			return;
		}
		// 2) 있으면 dto -> entity로 변환
		// 3) 이름을 변경하여 데이터 베이스에 저장한다
		CardEntity entity = temp.get();
		entity.setWord(card.getWord());
		entity.setPos(card.getPos());
		entity.setMeaning(card.getMeaning());
		entity.setExampleJp(card.getExampleJp());
		entity.setExampleKr(card.getExampleKr());
		System.out.println(entity.toString());
		cardRepository.save(entity);
    }
}

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
