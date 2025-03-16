package net.scit.DangoChan.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
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
import net.scit.DangoChan.entity.UserEntity;
import net.scit.DangoChan.service.AchievementService;
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
	private final AchievementService achievementService;
	// private memberVariable end
	
	// PJB start	
	
	// 마이페이지 이동 
	@GetMapping("/mypage")
	public String mypage(@AuthenticationPrincipal LoginUserDetails user,
			Model model) {
		if (user != null) {
	        Long userId = user.getUserId();
	        model.addAttribute("userId", userId);
	        
	        // 추가: 로그인한 유저 정보 조회 (예시)
	        UserEntity userEntity = userService.findById(userId);
	        model.addAttribute("user", userEntity); 
			
			List<String> personalAchievements = achievementService.getPersonalAchievements(userId);
		    List<String> communityAchievements = achievementService.getCommunityAchievements(userId);
		    int attendanceStreak = achievementService.getAttendanceStreak(userId);
		    String todayStudyTimeFormatted = achievementService.getTodayStudyTimeFormatted(userId);

		    model.addAttribute("attendanceStreak", attendanceStreak);
		    model.addAttribute("todayStudyTimeFormatted", todayStudyTimeFormatted);
		    model.addAttribute("personalAchievements", personalAchievements);
		    model.addAttribute("communityAchievements", communityAchievements);
			
;		}
		
	return "user/mypage";	
	}
	
	// 프로필 이미지 변경 모달 실행
    @GetMapping("/changeProfileImage")
    public String changeProfileImagePage(@AuthenticationPrincipal LoginUserDetails user,
                                         Model model) {
        if (user == null) {
            return "redirect:/login";
        }

        Long userId = user.getUserId();
        Integer currentProfile = userService.getCurrentProfileImage(userId);

        model.addAttribute("currentProfile", currentProfile);

        // 기본 제공 프로필 목록 넘기기 (예: 0 ~ 9번)
        model.addAttribute("profileImages", getProfileImageList());

        return "user/changeProfileImage";
    }

    //  프로필 이미지 업데이트 처리
    @PostMapping("/updateProfileImage")
    @ResponseBody
    public ResponseEntity<String> updateProfileImage(@AuthenticationPrincipal LoginUserDetails user,
                                                     @RequestBody Map<String, Object> payload) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Long userId = user.getUserId();

        // 프론트에서 보낸 profileImageNumber 키 확인
        Integer profileImage = (Integer) payload.get("profileImageNumber");

        if (profileImage == null || !isValidProfileImage(profileImage)) {
            return ResponseEntity.badRequest().body("잘못된 이미지 번호입니다.");
        }

        boolean updated = userService.updateProfileImage(userId, profileImage);

        if (!updated) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("업데이트 실패");
        }

        return ResponseEntity.ok("프로필 이미지 변경 성공");
    }

    //  기본 프로필 이미지 번호 목록 반환
    private Integer[] getProfileImageList() {
        return new Integer[]{0, 1, 2, 3, 4, 5, 6, 7,8,9}; // 샘플: 프로필 이미지 번호 0~9
    }

    // 프로필 이미지 유효성 검사
    private boolean isValidProfileImage(Integer profileImage) {
        return profileImage != null && profileImage >= 0 && profileImage <= 9;
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
	public boolean isIdExist(@RequestParam(name="email") String email)
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
	
	@PostMapping("/nickNameChange")
	@ResponseBody
	public boolean nickNameChange(
				//@AuthenticationPrincipal LoginUserDetails userDetails, 
				@RequestParam(name="nickName") String nickName
			)
	{
		//return userService.editNickname(userDetails.getUserId(), nickName);
		return userService.editNickname(1L, nickName);
	}
	// LHR end
}
