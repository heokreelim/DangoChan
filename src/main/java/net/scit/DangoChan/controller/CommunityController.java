package net.scit.DangoChan.controller;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.CommunityDTO;
import net.scit.DangoChan.dto.LoginUserDetails;
import net.scit.DangoChan.dto.UserDTO;
import net.scit.DangoChan.service.AchievementService;
import net.scit.DangoChan.service.BoardLikesService;
import net.scit.DangoChan.service.CommunityService;
import net.scit.DangoChan.service.UserService;
import net.scit.DangoChan.util.PageNavigator;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {
	
	private final CommunityService communityService;
	private final BoardLikesService boardLikesService;
	private final UserService userService;
	private final AchievementService achievementService;
	
	@Value("${spring.servlet.multipart.location}")
	private String uploadPath;
	
	@Value("${user.board.pageLimit}")	
	private int pageLimit;
	
	/**
	 * 게시글 조회 
	 * @param model 
	 * @return
	 * */
	@GetMapping("/communityBoardList")
	public String communityBoardList(
			@AuthenticationPrincipal LoginUserDetails loginUser,
			@PageableDefault(page=1) Pageable pageable,
			@RequestParam(name="searchItem", defaultValue = "title") String searchItem,
			@RequestParam(name="searchWord", defaultValue = "") String searchWord,
	        @RequestParam(name = "primarySortField", defaultValue = "createDate") String primarySortField,
	        @RequestParam(name = "primarySortOrder", defaultValue = "desc") String primarySortOrder,
	        @RequestParam(name = "secondarySortField", defaultValue = "") String secondarySortField,
	        @RequestParam(name = "secondarySortOrder", defaultValue = "") String secondarySortOrder,
			Model model
			) {
		// PJB edit start
		Long userId = loginUser.getUserId();
		model.addAttribute("userId",userId);
		
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
        
		//PJB edit end 
		
		// 2) 페이징 기능 + 검색 기능
		Page<CommunityDTO> list = communityService.selectAll(
				pageable, searchItem, searchWord,
				primarySortField, primarySortOrder,
				secondarySortField, secondarySortOrder
				);
		
	    if (list == null) {
	        list = Page.empty(); // 빈 페이지 객체를 반환하여 NullPointerException 방지
	    }
		
		int totalPages = list.getTotalPages();
		int page = pageable.getPageNumber();
		
		PageNavigator navi = new PageNavigator(pageLimit, page, totalPages);
		// 1) 검색 기능 추가
		
		model.addAttribute("list", list);
		model.addAttribute("searchItem", searchItem);
		model.addAttribute("searchWord", searchWord);
	    model.addAttribute("primarySortField", primarySortField);
	    model.addAttribute("primarySortOrder", primarySortOrder);
	    model.addAttribute("secondarySortField", secondarySortField);
	    model.addAttribute("secondarySortOrder", secondarySortOrder);
		model.addAttribute("navi", navi);
		
		//로그인 했니 안 했니 확인해서, 로그인하면 그 사람의 이름 출력
		if(loginUser != null) {
			model.addAttribute(loginUser.getUserName());
		}
		
		return "community/communityBoardList";
	}

	
	/**
	 * 게시글 쓰기 화면 요청
	 * 
	 * @return
	 * */
	@GetMapping("/communityWrite")
	public String communityWrite() {
		return "community/communityWrite";
	}
	
	/**
	 * 게시글 등록 요청
	 * 
	 * @param communityDTO
	 * @return
	 * 
	 * */
	@PostMapping("/communityWrite")
	public String communityWrite(@ModelAttribute CommunityDTO communityDTO) {
		
//		//유저정보 임시처리
//		communityDTO.setUserId(1l);
		
		communityService.insertBoard(communityDTO);
		
		return "redirect:/community/communityBoardList";
	}
	
	/**
	 * 게시글 상세 보기 화면 요청 & 조회수 증가
	 * 
	 * @param boardId
	 * @param model
	 * @return
	 * 
	 * */
	@GetMapping("/communityDetail")
	public String communityDetail(
			@AuthenticationPrincipal LoginUserDetails loginUser,
			@RequestParam(name="searchItem", defaultValue = "communityTitle") String searchItem,
			@RequestParam(name="searchWord", defaultValue = "") String searchWord,
			@RequestParam(name="boardId") Integer boardId,
			Model model) {
		//DB에 boardId에 해당하는 하나의 게시글을 조회
		CommunityDTO communityDTO = communityService.selectOne(boardId);
		communityService.incrementViews(boardId);
		
		Boolean isLikedByMe = boardLikesService.isLiked(boardId, loginUser.getUserId());
		
		model.addAttribute("community", communityDTO);
		model.addAttribute("searchItem", searchItem);
		model.addAttribute("searchWord", searchWord);
		model.addAttribute("isLikedByMe", isLikedByMe);
		
		return "community/communityDetail";	
		
	}
	
	/**
	 * boardId 번호에 해당하는 게시글 데이터 삭제
	 * 
	 * @param boardId
	 * @return
	 */
	@GetMapping("/communityDelete")
	public String communityDelete(
			@RequestParam(name="searchItem", defaultValue = "title") String searchItem,
			@RequestParam(name="searchWord", defaultValue = "") String searchWord,
			@RequestParam(name="boardId") Integer boardId,
			RedirectAttributes reat) {
		communityService.deleteOne(boardId);
		reat.addAttribute("searchItem", searchItem);
		reat.addAttribute("searchWord", searchWord);
		
		return "redirect:/community/communityBoardList";
	}
	
	/**
	 * 게시글 수정화면 요청
	 * boardId 번호에 해당하는 데이터 조회 후 수정화면에 반영
	 * 
	 * @param boardId
	 * @param model
	 * @return
	 */
	@GetMapping("/communityUpdate")
	public String communityUpdate(
			@RequestParam(name="searchItem", defaultValue = "title") String searchItem,
			@RequestParam(name="searchWord", defaultValue = "") String searchWord,
			@RequestParam(name="boardId") Integer boardId,
			Model model) {
		
		CommunityDTO communityDTO = communityService.selectOne(boardId);
		
		model.addAttribute("community", communityDTO);
		model.addAttribute("searchItem", searchItem);
		model.addAttribute("searchWord", searchWord);
		
		return "community/communityUpdate";
	}
	
	/**
	 * 게시글 수정 처리 요청 
	 * @param communityDTO
	 * @return
	 */	
	@PostMapping("/communityUpdate")
	public String communityUpdate(
			@ModelAttribute CommunityDTO communityDTO,
			@RequestParam(name="searchItem", defaultValue = "title") String searchItem,
			@RequestParam(name="searchWord", defaultValue = "") String searchWord,
			RedirectAttributes reat) {
		
		log.info("==== 수정데이터: {}", communityDTO.toString());
		
	    try {
	        communityService.updateBoard(communityDTO);
	    } catch (RuntimeException e) {
	        reat.addFlashAttribute("errorMessage", e.getMessage());
	        return "redirect:/community/communityUpdate?boardId=" + communityDTO.getBoardId()
	               + "&searchItem=" + searchItem + "&searchWord=" + searchWord;
	    }
//		
//		communityService.updateBoard(communityDTO);
		
		reat.addAttribute("searchItem", searchItem);
		reat.addAttribute("searchWord", searchWord);
		
		return "redirect:/community/communityBoardList";
		
	}
	
	/**
	 * 파일 다운로드
	 * @param boardId
	 * @param response
	 * @return
	 * */
	@GetMapping("/download")
	public String download(
			@RequestParam(name="boardId") Integer boardId,
			HttpServletResponse response) {
		CommunityDTO communityDTO = communityService.selectOne(boardId);
		
		String originalFileName = communityDTO.getOriginalFileName();
		String savedFileName = communityDTO.getSavedFileName();
		String fullpath = uploadPath + "/" + savedFileName;

		try {
			String tempName = URLEncoder.encode(originalFileName
					, StandardCharsets.UTF_8.toString());
			
			response.setHeader("Content-Disposition", "attachment;filename=" + tempName);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		FileInputStream fin = null; 			// 로컬에서  input : file
		ServletOutputStream fout = null;		// 네트워크로 output: servlet
		
		try {
			fin = new FileInputStream(fullpath);
			fout = response.getOutputStream();
			
			FileCopyUtils.copy(fin, fout);
			
			fout.close();
			fin.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
}
