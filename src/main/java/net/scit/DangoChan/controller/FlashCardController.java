package net.scit.DangoChan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/flashcard")
@RequiredArgsConstructor
@Slf4j
public class FlashCardController {
	
	//private final Service variable start
	
	//private final Service variable end
	
	//category start
	
	//AYH start
	
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
