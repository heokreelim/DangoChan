package net.scit.DangoChan.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.LoginUserDetails;
import net.scit.DangoChan.dto.UserDTO;
import net.scit.DangoChan.service.AchievementService;
import net.scit.DangoChan.service.GameService;
import net.scit.DangoChan.service.UserService;

@Controller
@RequestMapping("/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {
	
	private final GameService gameService;
	private final UserService userService;
	private final AchievementService achievementService;
	
	@GetMapping("/matchCard")
	public String matchCard(@AuthenticationPrincipal LoginUserDetails loginUser, Model model) {
		// PJB edit start		
		// 로그한 유저의 정보를 가져 오기위 한 LoginUserDetails, 출력을 위한 Model import
		Long userId = loginUser.getUserId();
		model.addAttribute("userId", userId);
    	// 사용자 정보를 가져와 DTO로 변환
        UserDTO userDTO = userService.findUserById(userId);
        model.addAttribute("userInfo", userDTO);
		
      	 //  업적 및 출석 데이터 추가
        List<String> personalAchievements = achievementService.getPersonalAchievements(userId);
        List<String> communityAchievements = achievementService.getCommunityAchievements(userId);
        int attendanceStreak = achievementService.getAttendanceStreak(userId);
        String todayStudyTimeFormatted = achievementService.getTodayStudyTimeFormatted(userId);

        model.addAttribute("personalAchievements", personalAchievements);
        model.addAttribute("communityAchievements", communityAchievements);
        model.addAttribute("attendanceStreak", attendanceStreak);
        model.addAttribute("todayStudyTimeFormatted", todayStudyTimeFormatted);
		// PJB edit end		
		return "game/matchCard";
	}
	
	// JSON 데이터를 반환하는 REST API 엔드포인트 추가
	@GetMapping("/matchCard/data")
	@ResponseBody
	public List<Map<String, Object>> getMatchCardData(
			@AuthenticationPrincipal LoginUserDetails loginUser
			) {
		return gameService.getRandomDeck(loginUser.getUserId());
	}
	

}
