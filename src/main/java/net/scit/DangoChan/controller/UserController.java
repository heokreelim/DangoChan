package net.scit.DangoChan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.service.UserService;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	// public memberVariable start
	
	// public memberVariable end
	
	// private memberVariable start
	private final UserService userService;
	// private memberVariable end
	
	// PJB start
	
	// 로그인 페이지로 이동
	@GetMapping("/login")
	public String login() {
		
		return "user/login";
	}
	
	// 회원가인 페이지로 이동
	@GetMapping("/join")
	public String join() {
		
		return "user/join";
	}
	
	// 로그인 처리요청
	@PostMapping("/loginProc")
	public String loginProc() {
		// 
		return "redirect:/";
	}
	
	// 마이페이지 이동 
	@GetMapping("/mypage")
	public String mypage() {
		
	return "user/mypage";	
	}
	
	// PJB end
	
	// LHR start
	
	// LHR end
}
