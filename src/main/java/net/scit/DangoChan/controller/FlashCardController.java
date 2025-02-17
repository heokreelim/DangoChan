package net.scit.DangoChan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
	public String index()
	{
		return "flashcard/flashcard"; 
	}
	//SYH end
	
	//card end
	
}
