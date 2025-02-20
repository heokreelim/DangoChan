package net.scit.DangoChan.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.service.ReplyService;

@Slf4j
@RestController
@RequestMapping("/reply")
@RequiredArgsConstructor
public class ReplyController {
	
	private final ReplyService replyService;
	
	/**
	 * 댓글 등록 메서드
	 * @param replyDTO
	 * @param loginUserDetails
	 * @return
	 * */

}
