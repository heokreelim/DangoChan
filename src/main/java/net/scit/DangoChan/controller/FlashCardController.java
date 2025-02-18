package net.scit.DangoChan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.CategoryDTO;
import net.scit.DangoChan.service.FlashCardService;

import net.scit.DangoChan.service.FlashCardService;
import net.scit.DangoChan.dto.CardDTO;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
	 *
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
	@GetMapping("/flashcard")
	public String flashcard(@RequestParam(required = false) Long deckId, Model model) {
		if (deckId == null) {
			deckId = 1L; // 기본 덱 ID 설정
		}

		// 덱 ID 기반으로 카드 데이터 가져오기
		CardDTO card = flashCardService.getCardByDeckId(deckId);
		model.addAttribute("flashcard", card);
		return "flashcard/flashcard";
	}
	//SYH end
	
	//card end
	
}
