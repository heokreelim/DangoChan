package net.scit.DangoChan.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.LoginUserDetails;
import net.scit.DangoChan.dto.UserDTO;
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
	
	// 마이페이지 이동 
	@GetMapping("/mypage")
	public String mypage(@AuthenticationPrincipal LoginUserDetails user,
			Model model) {
		if (user != null) {
			Long userId = user.getUserId();
			model.addAttribute("userId",userId);
			// ? 이전에 userId, userPwd 를 받아 검증 후 이동
			// guest, social Login 의 경우, 토큰을 확인 후 이동하는 방법 고려중
			// UserDTO userDTO = userService.
			
;		}
		
	return "user/mypage";	
	}
	
	// PJB end
	
	// LHR start
	
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
		@PostMapping("/joinProc")
		public String registerProc(@ModelAttribute UserDTO dto) {
			String returnPage;
			
			dto.setAuthType("Local");
			
			if (userService.registerUser(dto))
			{
				// 회원가입 성공
				returnPage = "redirect:/user/login";
			}
			else
			{
				// 회원가입 실패
				returnPage = "redirect:/user/join";
			}
			
			return returnPage;
		}
	
	// LHR end
}
