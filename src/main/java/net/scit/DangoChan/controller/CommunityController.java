package net.scit.DangoChan.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.CommunityDTO;
import net.scit.DangoChan.service.CommunityService;
import net.scit.DangoChan.util.PageNavigator;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {
	
	private final CommunityService communityService;
	
	@Value("${user.board.pageLimit}")
	private int pageLimit;
	
	/**
	 * 게시글 조회 
	 * @param model 
	 * @return
	 * */
	@GetMapping("/communityBoardList")
	public String communityBoardList(
			@PageableDefault(page=1) Pageable pageable,
			@RequestParam(name="searchItem", defaultValue = "boardTitle") String searchItem,
			@RequestParam(name="searchWord", defaultValue = "") String searchWord,
			Model model
			) {
		
		// 2) 페이징 기능 + 검색 기능
		Page<CommunityDTO> list = communityService.selectAll(pageable, searchItem, searchWord);
		
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
		model.addAttribute("navi", navi);
		
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
		communityService.insertBoard(communityDTO);
		
		return "redirect:/community/communityBoardList";
	}
	
	/**
	 * 게시글 상세 보기 화면 요청 & 조회수 증가
	 * 
	 * @param model
	 * @return
	 * 
	 * */
	
}
