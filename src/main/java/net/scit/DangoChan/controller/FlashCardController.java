package net.scit.DangoChan.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.CardDTO;
import net.scit.DangoChan.dto.CategoryDTO;
import net.scit.DangoChan.dto.DeckAndCardsRequest;
import net.scit.DangoChan.dto.DeckDTO;
import net.scit.DangoChan.service.FlashCardService;

@Controller
@RequestMapping("/flashcard")
@RequiredArgsConstructor
@Slf4j
public class FlashCardController {
	
	//private final Service variable start
		private final FlashCardService flashCardService;
	//private final Service variable end
	
	//category start
	
	//AYH start
	

	/**
	 *	카테고리 등록 요청
	 * @param categoryDTO
	 * @return
	 */
	@GetMapping("/insertCategory")
	public String insertCategory(@ModelAttribute CategoryDTO categoryDTO) {

//		DB 등록
		flashCardService.insertCategory(categoryDTO);

		log.info("카테고리가 추가되었습니다.");
		return "redirect:/";
	}

	/**
	 *	카테고리 수정 요청
	 * @param categoryDTO
	 * @return
	 */
	@GetMapping("/updateCategory")
	public String updateCategory(@ModelAttribute CategoryDTO categoryDTO) {
		flashCardService.updateCategory(categoryDTO);
		
		return "redirect:/";
	}


	
	//AYH end
	
	//PJB start
	
	//PJB end
	
	//category end
	
	//deck start
	
	//AYH start
	
	@ResponseBody
	@PostMapping("/importDeck")
    public String importDeck(@RequestBody DeckAndCardsRequest request) {
        DeckDTO deckDTO = request.getDeckDTO();
        List<CardDTO> cardDTOList = request.getCardDTOList();

        System.out.println("▶ Deck 저장: " + deckDTO);
        DeckDTO savedDeckId = flashCardService.insertDeck(deckDTO);
        for (CardDTO cardDTO : cardDTOList) {
        	cardDTO.setDeckId(savedDeckId.getDeckId()); // 백엔드에서 저장 후 ID 업데이트 필요
            System.out.println("▶ 카드 저장: " + cardDTO);
            flashCardService.insertCard(cardDTO);
        }
     // TODO: DB 저장 로직 추가 (Service & Repository 호출)
        return "Deck and Cards saved successfully!";
	}
	//AYH end
		
	//PJB start
		
	//PJB end
	
	//deck end
	
	//card start
	
	//AYH start
	
	//AYH end
	
	//SYH start
	// ✅ 플래시카드 페이지 (HTML 렌더링)
	@GetMapping("/flashcard") // ❗ 변경: /flashcard/flashcard 로 맞춤
	public String flashcard(@RequestParam(required = false) Long deckId, Model model) {
		if (deckId == null) {
			deckId = 1L; // 기본 덱 ID 설정
		}
//		CardDTO card = flashCardService.getCardByDeckId(deckId);
		CardDTO card = new CardDTO();
		model.addAttribute("flashcard", card);
		return "flashcard/flashcard"; // ✅ 템플릿: src/main/resources/templates/flashcard/flashcard.html
	}

	// ✅ AJAX 요청 (랜덤 단어 반환)
	// ✅ JSON 데이터 반환 (랜덤 단어 가져오기)
	@GetMapping(value = "/json", produces = "application/json")
	public ResponseEntity<CardDTO> getRandomFlashcard(@RequestParam(required = false) Long deckId) {
		if (deckId == null) {
			deckId = 1L; // 기본 덱 ID 설정
		}

		// ✅ DTO 변환된 데이터 가져오기
//		CardDTO card = flashCardService.getCardByDeckId(deckId);
		CardDTO card = new CardDTO();
		// ✅ 디버깅 로그 추가 (JSON 응답 확인)
		System.out.println("🔥 [DEBUG] 응답 JSON: " + card);

		return ResponseEntity.ok(card);
	}

	// ✅ study_level 업데이트 API (AJAX 요청 처리)
	@PostMapping("/updateStudyLevel")
	public ResponseEntity<String> updateStudyLevel(@RequestParam Long cardId, @RequestParam Integer studyLevel) {
		flashCardService.updateStudyLevel(cardId, studyLevel);
		return ResponseEntity.ok("✅ study_level 업데이트 성공");
	}
	//SYH end
	
	//card end
	
}
