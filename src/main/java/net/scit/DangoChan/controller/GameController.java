package net.scit.DangoChan.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.LoginUserDetails;
import net.scit.DangoChan.service.GameService;

@Controller
@RequestMapping("/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {
	
	private final GameService gameService;
	
	@GetMapping("/matchCard")
	public String matchCard() {
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
