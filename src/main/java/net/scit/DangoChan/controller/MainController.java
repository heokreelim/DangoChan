package net.scit.DangoChan.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.LoginUserDetails;

@Slf4j
@Controller
public class MainController {
	// 주석 테스트, comment test, コメントのテスト（検討）
	@GetMapping("/")
	public String index(@AuthenticationPrincipal LoginUserDetails userDetails)
	{
		if (userDetails == null) 
		{
	        log.error("=============== 로그인 안함! 로그인 페이지로 이동");
	        return "redirect:/user/login";
	    }
		else
		{
			log.info("============ 로그인 됨. 유저 정보: {}", userDetails.toString());
			return "redirect:/home"; 
		}	    
	}
}
