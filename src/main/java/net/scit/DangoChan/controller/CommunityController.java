package net.scit.DangoChan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {
	
	/**
	 * 게시글 조회
	 * 
	 * @return
	 * */
	@GetMapping("/communityBoardList")
	public String communityBoardList() {
		return "community/communityBoardList";
	}

	
	/**
	 * 게시글 쓰기 화면 요청
	 * 
	 * @return
	 * */
	@GetMapping("/communityWrite")
	public String boardWrite() {
		return "community/communityWrite";
	}
}
