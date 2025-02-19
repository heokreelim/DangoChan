package net.scit.DangoChan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.CardDTO;
import net.scit.DangoChan.dto.CategoryDTO;
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
		CardDTO card = flashCardService.getCardByDeckId(deckId);
		model.addAttribute("flashcard", card);
		return "flashcard/flashcard"; // ✅ 템플릿: src/main/resources/templates/flashcard/flashcard.html
	}

	// ✅ AJAX 요청 (랜덤 단어 반환)
	@GetMapping(value = "/json", produces = "application/json")
	public ResponseEntity<CardDTO> getRandomFlashcard(@RequestParam(required = false) Long deckId) {
		if (deckId == null) {
			deckId = 1L; // 기본 덱 ID 설정
		}
		CardDTO card = flashCardService.getCardByDeckId(deckId);
		return ResponseEntity.ok(card);
	}
	//SYH end
	
	//card end
	
}
