package net.scit.DangoChan.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.ReplyDTO;
import net.scit.DangoChan.entity.CommunityEntity;
import net.scit.DangoChan.entity.ReplyEntity;
import net.scit.DangoChan.repository.CommunityRepository;
import net.scit.DangoChan.repository.ReplyRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReplyService {
	
	private final CommunityRepository communityRepository;
	private final ReplyRepository replyRepository;
	
	public void replyInsert(ReplyDTO replyDTO) {
		Optional<CommunityEntity> temp = communityRepository.findById(replyDTO.getBoardId());
		
		if(!temp.isPresent()) return;
		
		// 2) 부모글이 있다면 부모글 꺼내오기
		CommunityEntity communityEntity = temp.get();
		
		// 3) 두 개를 전달받아 entity 변환
		ReplyEntity replyEntity = ReplyEntity.toEntity(replyDTO, communityEntity);
		
		// 4) DB에 저장(save)
		replyRepository.save(replyEntity);
	}
	
	/**
	 * boardId에 해당하는 댓글 전체 조회  
	 * @param boardId
	 */
	public List<ReplyDTO> replyAll(Integer boardId) {
		// 1) 부모 글이 있는지 조회하기
		Optional<CommunityEntity> temp = communityRepository.findById(boardId);
		
		// 2) 댓글 조회하기 위한 Query Method
		List<ReplyEntity> entityList = 
				// 저장은 댓글에, 조회는 보드 기준
				replyRepository.findByCommunityEntity(temp, Sort.by(Sort.Direction.DESC, "replyId"));
		
		// 3) List<ReplyDTO>를 선언
		List<ReplyDTO> list = new ArrayList<>();
		// 4) Entity --> DTO (엔티티에 값 두 개 보내졌으니 여기서도 또 값 두 개 보내는 것)
		entityList.forEach((entity) -> list.add(ReplyDTO.toDTO(entity, boardId)));
		
		log.info("댓글 개수: {}", entityList.size());
		
		return list;
	}
	
	/**
	 * 그 게시글 댓글 전체 조회하기
	 * @param replyId
	 * */
	public ReplyDTO replySelectOne(Integer replyId) {
		Optional<ReplyEntity> temp = replyRepository.findById(replyId);
		
		if(!temp.isPresent()) return null;
		
		ReplyEntity entity = temp.get();
		
		ReplyDTO replyDTO = ReplyDTO.toDTO(
				entity, entity.getCommunityEntity().getBoardId()
				);
		
		return replyDTO;
	}
	
	/**
	 * 댓글 수정 처리
	 * @param replyId
	 * @param updateReply
	 * */
	public void replyUpdateProc(Integer replyId, String updateReply) {
		Optional<ReplyEntity> temp = replyRepository.findById(replyId);
		
		if(!temp.isPresent()) return;
		
		ReplyEntity entity = temp.get();
		
		entity.setContent(updateReply);
		
		replyRepository.save(entity);
		
	}
	
}
