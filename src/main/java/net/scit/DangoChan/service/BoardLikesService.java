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

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardLikesService {
	
	private final BoardLikesRepository boardLikesRepository;
	
	// 좋아요 등록
	@Transactional
	public void addLike(UserEntity userEntity, CommunityEntity communityEntity) {
		Optional<BoardLikesEntity> existingLike = boardLikesRepository.findByCommunityEntityAndUserEntity(communityEntity, userEntity);
		
		if(existingLike.isPresent()) {
			throw new RuntimeException("이미 '좋아요'를 하신 덱입니다.");
		}
		
		BoardLikesEntity boardLike = BoardLikesEntity.toBoardLikesEntity(userEntity, communityEntity);
		boardLikesRepository.save(boardLike);
	}
	
	// 좋아요 수 조회
	public Long countLikes(CommunityEntity communityEntity) {
		return boardLikesRepository.countByCommunityEntity(communityEntity);
		
		
	}
}
