package net.scit.DangoChan.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.LoginUserDetails;
import net.scit.DangoChan.dto.UserDTO;
import net.scit.DangoChan.service.LoginUserDetailsService;
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
	private final LoginUserDetailsService loginUserDetailsService;
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
		
		dto.setAuthType("LOCAL");
		
		if (userService.registerUser(dto))
		{
			// 회원가입 성공
			// 자동 로그인 처리
            UserDetails userDetails = loginUserDetailsService.loadUserByUsername(dto.getEmail()); // UserDetails 로드
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // Authentication 객체 생성
            SecurityContextHolder.getContext().setAuthentication(authentication); // SecurityContext에 Authentication 설정

            returnPage = "redirect:/home"; // 로그인 후 이동할 페이지
		}
		else
		{
			// 회원가입 실패
			returnPage = "redirect:/user/join";
		}
		
		return returnPage;
	}
	
	@PostMapping("/idCheck")
	@ResponseBody
	public boolean IsIdExist(@RequestParam(name="email") String email)
	{
		return userService.idDuplCheck(email);
	}
	
	@PostMapping("/guestlogin")
	@ResponseBody
    public ResponseEntity<UUID> guestLogin(@RequestBody(required = false) UUID guestLoginKey) {
        
		LoginUserDetails userDetails = loginUserDetailsService.loadUserByGuestLoginKey(guestLoginKey);
		
		String stringKey = userDetails.getProviderId();
		
		log.info("========== 게스트 번호 : {}", stringKey);
		
		return ResponseEntity.ok(UUID.fromString(stringKey));
    }
	// LHR end
}
