package net.scit.DangoChan.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.LoginUserDetails;
import net.scit.DangoChan.dto.ReplyDTO;
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
	@PostMapping("/replyInsert")
	public String replyInsert(
			@ModelAttribute ReplyDTO replyDTO
			, @AuthenticationPrincipal LoginUserDetails loginUser
			) {
		
		Long loginId = loginUser.getUserId();
		replyDTO.setUserId(loginId);
		
		replyService.replyInsert(replyDTO);
		
		return "success";
	}
	
	/**
	 * boardId에 해당하는 댓글 전체 조회 
	 * @param boardId
	 * @return
	 */
	@GetMapping("/replyAll")
	public List<ReplyDTO> replyAll(
			@RequestParam(name="boardId") Integer boardId
			) {
		List<ReplyDTO> list = replyService.replyAll(boardId);
		
		return list;
	}
	
	/**
	 * replySeq 댓글 데이터 삭제 
	 * @param replySeq
	 * @return
	 */
	@GetMapping("/replyDelete")
	public ReplyDTO replyDelete(
			@RequestParam(name="replyId") Integer replyId
			) {
		ReplyDTO replyDTO = replyService.replySelectOne(replyId);
		
		return replyDTO;
	}
	
	/**
	 * 댓글 수정 처리
	 * @param replyId
	 * @param updateReply
	 * @return
	 */
	@PostMapping("/replyUpdateProc")
	public String replyUpdateProc(
			@RequestParam(name="replyId") Integer replyId,
			@RequestParam(name="updatedReply") String updateReply
			) {
		
		replyService.replyUpdateProc(replyId, updateReply);
		
		return "Updated";
	}
	
}
