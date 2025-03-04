package net.scit.DangoChan.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.CategoryDTO;
import net.scit.DangoChan.dto.DeckDTO;
import net.scit.DangoChan.dto.DeckInfoDTO;
import net.scit.DangoChan.dto.DeckStudyTimeDTO;
import net.scit.DangoChan.dto.LoginUserDetails;
import net.scit.DangoChan.dto.UserDTO;
import net.scit.DangoChan.entity.DeckEntity;
import net.scit.DangoChan.service.DeckStudyTimeService;
import net.scit.DangoChan.service.FlashCardService;
import net.scit.DangoChan.service.UserService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {
	
	private final UserService userService;
	private final FlashCardService flashCardService;
	private final DeckStudyTimeService deckStudyTimeService;
	
	// Home 화면으로 이동
	@GetMapping("/home")
	public String home (@AuthenticationPrincipal LoginUserDetails user,
		Model model) {
		if (user != null) { 
            Long userId = user.getUserId();
            model.addAttribute("userId", userId);
            
            // 서비스에서 해당 유저의 전체 카테고리 목록을 가져온다고 가정
    	    List<CategoryDTO> categoryList = flashCardService.getCategoryListByUser(userId);
    	    model.addAttribute("categoryList", categoryList);
    	    
    	    log.info("categoryList ==={}", categoryList.size());
    	    
    	    // 데이터 확인
    	    for (CategoryDTO categoryDTO : categoryList) {
    	    	// 카테고리 명 
    	    	log.info("category name ====== {}", categoryDTO.getCategoryName());
    	    	
    	    	// 해당 카테고리에 속한 덱 정보
				for (DeckInfoDTO deckInfoDTO : categoryDTO.getDeckInfoList()) {
					log.info("deckName			=== {}", deckInfoDTO.getDeckName());
					log.info("deckCardCount		=== {}", deckInfoDTO.getDeckCardCount());
					log.info("studiedCardCount	=== ○ : {}, △ : {}, ×  : {} ", 
								deckInfoDTO.getStudiedCardCountOk(), deckInfoDTO.getStudiedCardCountYet(), deckInfoDTO.getStudiedCardCountNo());
					log.info("cardStudyRate		=== {}", deckInfoDTO.getCardStudyRate());
				}
			}
			
        }	    
		return "home"; 
	}
	

	// userId 를 전달하여 해당 유저의 출석(학습시간) 기록 정보를 가져온다.
//	@GetMapping("/attendance")
//	@ResponseBody
//	public List<DeckStudyTimeDTO> getAttendance(@RequestParam(name = "userId") Long userId) {
//	    log.info("🔹 유저 출석 요청: userId = {}", userId);
//	    // 테스트용으로 userId를 1로 고정
//	    userId = 1L;
//	    List<DeckStudyTimeDTO> attendanceList = deckStudyTimeService.getUserAttendance(userId);
//
//	    if (attendanceList.isEmpty()) {
//	        log.warn("⚠️ 출석 데이터 없음: userId = {}", userId);
//	    } else {
//	        log.info("✅ 출석 데이터 반환: {}개", attendanceList.size());
//	    }
//	    
//	    return attendanceList;
//	}

}
