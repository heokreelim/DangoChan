package net.scit.DangoChan.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.CardDTO;
import net.scit.DangoChan.dto.CategoryDTO;
import net.scit.DangoChan.dto.DeckDTO;
import net.scit.DangoChan.dto.DeckInfoDTO;
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
				Optional<UserEntity> temp = userRepository.findById(categoryDTO.getUserId());
				
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
//userId 를 전달하여 해당 유저가 소유한 Category 를 리스트로 반환
public List<CategoryDTO> getCategoryListByUser(Long userId) {
	List<CategoryEntity> temp = categoryRepository.findAllByUserEntity_UserId(userId);
	
	log.info("CategoryEntityList Size ==={}", temp.size());
    List<CategoryDTO> categoryList = new ArrayList<>();
    
    for (CategoryEntity categoryEntity : temp) {
    	// 1) categoryEntity 안에 있는 deckEntityList를 get
    			List<DeckEntity> deckEntityList = categoryEntity.getDeckEntityList();
    			
    	// 2) List<DeckInfoDTO> deckInfoDTOList 객체를 new로 하나 만들어 지역변수에 할당
    			List<DeckInfoDTO> deckInfoDTOList = new ArrayList<>();
    			
    	// 3) deckEntityList foreach 문 만들기 
    			for (DeckEntity deckEntity : deckEntityList) {
    				// 각 deckEntity에서 deckId를 get => DeckInfoDTO의 deckId 값
    				Long deckId = deckEntity.getDeckId();
    				// 각 deckEntity에서 deckName을 get => DeckInfoDTO의 deckName 값
    				String deckName = deckEntity.getDeckName();
    				// deckEntity의 cardEntityList의 size를 get => DeckInfoDTO의 deckCardCount 값
    				List<CardEntity> cardEntityList = deckEntity.getCardEntityList();
    				Integer deckCardCount = cardEntityList.size();
    				
    				// studiedCardCount 각 값 선언
    				Integer studiedCardCountOk = 0;
    				Integer studiedCardCountYet = 0;
    				Integer studiedCardCountNo = 0;
    				
    				// deckEntity의 cardEntityList foreach 문
    				for (CardEntity cardEntity : cardEntityList) {
    					// 각 카드의 studyLevel 값에 따라 카운트 증가 (가정: 3 = ○, 2 = △, 1 = ×)
    					Integer studyLevel = cardEntity.getStudyLevel();
    					if (studyLevel != null) {
    						if (studyLevel == 3) {
    							studiedCardCountOk++;
    						} else if (studyLevel == 2) {
    							studiedCardCountYet++;
    						} else if (studyLevel == 1) {
    							studiedCardCountNo++;
    						}
    					}
    				} // deckEntity의 cardEntityList foreach 문 끝 ---
    				
    				// (studiedCardCountOk, studiedCardCountYet, studiedCardCountNo)의 합을 deckCardCount으로 나눈 값을 구함 => DeckInfoDTO의 cardStudyRate 값
    				Double cardStudyRate = 0.0;
    				if (deckCardCount > 0) {
    					cardStudyRate = (studiedCardCountOk + studiedCardCountYet + studiedCardCountNo) / (double) deckCardCount;
    				}
    				
    				// DeckInfoDTO.toDTO()를 호출하여 DeckInfoDTO 객체 생성 후 deckInfoDTOList에 추가
    				DeckInfoDTO deckInfoDTO = DeckInfoDTO.toDTO(deckId, deckName, deckCardCount, studiedCardCountOk, studiedCardCountYet, studiedCardCountNo, cardStudyRate);
    				deckInfoDTOList.add(deckInfoDTO);
    			} // deckEntityList foreach 문 끝 -----
    			
    			// CategoryDTO.toDTO()를 호출할 때 deckInfoDTOList를 전달하여 CategoryDTO 객체 생성 후 categoryList에 추가
    			categoryList.add(CategoryDTO.toDTO(categoryEntity, deckInfoDTOList));
    		}
    	    
    	    return categoryList;
    	}
			
		// categoryId 를 전달받아 해당 카테고리를 DB에서 삭제
			public void deleteCategory(Long categoryId) {
				categoryRepository.deleteById(categoryId);
			}


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
		// deckId 를 전달하여 해당 덱을 DB에서 삭제
			public void deleteDeck(Long deckId) {
				
				deckRepository.deleteById(deckId);
			}

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
				
				log.info("카드 갯수 : {}", cardList.size());
				
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
	List<ExportCardDTO> newCards = cards.stream()
		    .filter(card -> card.getCardId() == null)
		    .collect(Collectors.toList());

		List<ExportCardDTO> existingCards = cards.stream()
		    .filter(card -> card.getCardId() != null)
		    .collect(Collectors.toList());

		// 새 카드 저장
		for (ExportCardDTO card : newCards) {
		    Optional<DeckEntity> tempDeck = deckRepository.findById(card.getDeckId());
		    tempDeck.ifPresent(deckEntity -> cardRepository.save(CardEntity.toEntity(card, deckEntity)));
		}

		// 기존 카드 업데이트
		for (ExportCardDTO card : existingCards) {
		    Optional<CardEntity> temp = cardRepository.findById(card.getCardId());
		    temp.ifPresent(entity -> {
		        entity.setWord(card.getWord());
		        entity.setPos(card.getPos());
		        entity.setMeaning(card.getMeaning());
		        entity.setExampleJp(card.getExampleJp());
		        entity.setExampleKr(card.getExampleKr());
		        cardRepository.save(entity);
		    });
		}
}


public void deleteCard(List<Long> deletedCardIds) {
	for (Long cardId : deletedCardIds) {
		System.out.println(cardId);
		cardRepository.deleteById(cardId);
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
