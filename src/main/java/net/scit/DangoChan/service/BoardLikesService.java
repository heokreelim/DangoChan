package net.scit.DangoChan.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.entity.BoardLikesEntity;
import net.scit.DangoChan.entity.CommunityEntity;
import net.scit.DangoChan.entity.UserEntity;
import net.scit.DangoChan.repository.BoardLikesRepository;
import net.scit.DangoChan.repository.CommunityRepository;
import net.scit.DangoChan.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardLikesService {
	
	private final BoardLikesRepository boardLikesRepository;
	private final CommunityRepository communityRepository;
	private final UserRepository userRepository;
	
	// 좋아요 수 조회
	public Long countLikes(CommunityEntity communityEntity) {
		return boardLikesRepository.countByCommunityEntity(communityEntity);
		
	}

	public boolean isLiked(Integer boardId, Long userId) {
		// boardId로 게시글 조회
		CommunityEntity communityEntity = communityRepository.findById(boardId)
				.orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다"));
		// userId로 사용자 조회
		UserEntity userEntity = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
		
		// 게시글과 사용자 조합에 해당하는 좋아요가 존재하면 true, 없으면 false 반환
		return boardLikesRepository.existsByCommunityEntityAndUserEntity(communityEntity, userEntity);

	}
	
	@Transactional
	public boolean toggleLike(UserEntity userEntity, CommunityEntity communityEntity) {
	    // 좋아요 상태 확인
	    boolean isLiked = boardLikesRepository.existsByCommunityEntityAndUserEntity(communityEntity, userEntity);
	    
	    if (isLiked) {
	        // 좋아요가 이미 등록되어 있다면 삭제(취소)
	        BoardLikesEntity boardLike = boardLikesRepository.findByCommunityEntityAndUserEntity(communityEntity, userEntity)
	                .orElseThrow(() -> new RuntimeException("좋아요 정보가 존재하지 않습니다."));
	        boardLikesRepository.delete(boardLike);
	        return false; // 좋아요 취소됨
	    } else {
	        // 좋아요가 없는 상태라면 새로 등록
	        BoardLikesEntity boardLike = BoardLikesEntity.toBoardLikesEntity(userEntity, communityEntity);
	        boardLikesRepository.save(boardLike);
	        return true; // 좋아요 등록됨
	    }
	}

}
