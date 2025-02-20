package net.scit.DangoChan.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.service.BoardLikesService;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/community")
public class BoardLikesController {
	
	private final BoardLikesService boardLikesService;
	
    /**
     * 좋아요 추가
     * 클라이언트가 boardId와 userId를 보내면, 해당 게시글에 좋아요를 추가하고  
     * 성공 여부와 최신 좋아요 개수를 JSON으로 반환
     */
	
	@PostMapping("/like")
	public ResponseEntity<Map<String, Object>> likePost (
			@RequestParam(name="boardId") Integer boardId,
			@RequestParam(name="userId") Long userId
			) {
		
		boolean success = boardLikesService.addLike(boardId, userId);
		int likeCount = boardLikesService.getLikeCount(boardId);
		
		Map<String, Object> response = new HashMap<>();
		response.put("success", success);
		response.put("likeCount", likeCount);
		
		return ResponseEntity.ok(response);

	}
//	/**
//	 * 좋아요 취소
//	 * 클라이언트가 boardId와 userId를 보내면, 해당 게시글에 대해 좋아요를 취소하고
//	 * 성공 여부와 최신 좋아요 개수를 JSON으로 반환
//	 * */
//	@PostMapping("/unlike")
//	public ResponseEntity<Map<String, Object>> unlikePost(
//			@ModelAttribute
//			@RequestParam(name="boardId") Integer boardId,
//			@RequestParam(name="userId") Long userId
//			) {
//		
//		boolean success = boardLikesService.removeLike(boardId, userId);
//		int likeCount = boardLikesService.getLikeCount(boardId);
//		
//		Map<String, Object> response = new HashMap<>();
//		response.put("success", success);
//		response.put("likeCount", likeCount);
//		
//		return ResponseEntity.ok(response);
//	}
//
}
