package net.scit.DangoChan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {
	
	@GetMapping("/matchCard")
	public String matchCard() {
		return "game/matchCard";
	}

}
