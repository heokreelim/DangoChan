package net.scit.DangoChan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.entity.CommunityEntity;
import net.scit.DangoChan.entity.UserEntity;
import net.scit.DangoChan.repository.CommunityRepository;
import net.scit.DangoChan.repository.UserRepository;
import net.scit.DangoChan.service.BoardLikesService;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/boardLikes")
public class BoardLikesController {
	
	private final BoardLikesService boardLikesService;
	private final CommunityRepository communityRepository;
	private final UserRepository userRepository;
		
	// 게시글 좋아요 수 조회
	@GetMapping("/{boardId}/count")
	@ResponseBody
	public ResponseEntity<Long> countLikes(
			@PathVariable(name="boardId") Integer boardId
			) {
		CommunityEntity communityEntity = communityRepository.findById(boardId)
				.orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
		
		Long count = boardLikesService.countLikes(communityEntity);
		
		return ResponseEntity.ok(count);
	}
	// 토글로 좋아요 등록하기
	@PostMapping("/{boardId}/toggle")
	public ResponseEntity<String> toggleLike(
	        @PathVariable(name="boardId") Integer boardId,
	        @RequestParam(name="userId") Long userId) {
	    CommunityEntity communityEntity = communityRepository.findById(boardId)
	            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
	    UserEntity userEntity = userRepository.findById(userId)
	            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
	    
	    boolean isLiked = boardLikesService.toggleLike(userEntity, communityEntity);
	    
	    if (isLiked) {
	        return ResponseEntity.ok("좋아요 등록 완료!");
	    } else {
	        return ResponseEntity.ok("좋아요 취소 완료!");
	    }
	}


}
