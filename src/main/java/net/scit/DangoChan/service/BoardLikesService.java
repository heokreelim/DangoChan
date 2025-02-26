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
	
	private final CommunityRepository communityRepository;
	private final BoardLikesRepository boardLikesRepository;
	private final UserRepository userRepository;
	
	/**
	 * 좋아요 추가
	 * 특정 게시글(boardId)에 대해 특정 사용자(userId)가 좋아요를 추가.
	 * 이미 좋아요가 존재하면, 추가하지 않음
	 * */ 
	@Transactional
	public boolean addLike(Integer boardId, Long userId) {
		// 게시글 존재 여부 확인
		Optional<CommunityEntity> board = communityRepository.findById(boardId);

		if(board.isEmpty()) return false;
		
		CommunityEntity testBoard = board.get(); // 02.26 임시 수정
		// 이미 좋아요를 눌렀다면 추가하지 않음
//		Optional<BoardLikesEntity> existingLike = boardLikesRepository.findByBoard_BoardIdAndUserId(boardId, userId);
//		if (existingLike.isPresent()) {
//            return false;
//        } // 02.26 임시 수정
		// 02.26 임시 수정
		Optional<UserEntity> user = userRepository.findById(1L); // 02.26 임시 수정

		if(user.isEmpty()) return false;
		UserEntity testUser = user.get(); // 02.26 임시 수정
		
		BoardLikesEntity newLike = BoardLikesEntity.toBoardLikesEntity(testUser, testBoard); // 02.26 임시 수정
		
		boardLikesRepository.save(newLike);
		return true;
	}
	
    /**
     * 좋아요 취소
     * : 특정 게시글(boardId)에 대해 특정 사용자(userId)의 좋아요를 삭제.
     */
    @Transactional
    public boolean removeLike(Integer boardId, Long userId) {  // 02.26 임시 수정
//        Optional<BoardLikesEntity> like = boardLikesRepository.findByBoard_BoardIdAndUserId(boardId, userId);
        
//        if (like.isPresent()) {
//            boardLikesRepository.delete(like.get());
//            return true;
//        }
        return false;
    }

    /**
     * 특정 게시글의 좋아요 개수 조회
     */
    public Integer getLikeCount(Integer boardId) {
        return boardLikesRepository.countByBoardId(boardId);
    }

}
