package net.scit.DangoChan.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.CommunityDTO;
import net.scit.DangoChan.entity.CommunityEntity;
import net.scit.DangoChan.repository.CommunityRepository;
import net.scit.DangoChan.util.FileService;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommunityService {
	private final CommunityRepository communityRepository;
	
	@Value("${spring.servlet.multipart.location}")
	private String uploadPath;
	
	@Value("${user.board.pageLimit}")
	private int pageLimit;		

	public void insertBoard(CommunityDTO communityDTO) {
		// dto에서 전체 파일명과 저장 경로를 보내고, 저장될 파일명을 반환받음
		MultipartFile uploadFile = communityDTO.getUploadFile();
		
		String savedFileName = null;
		String originalFileName = null;
		
		if(!uploadFile.isEmpty()) {
			savedFileName =  FileService.saveFile(uploadFile, uploadPath);
			originalFileName = uploadFile.getOriginalFilename();
		}

		communityDTO.setSavedFileName(savedFileName);
		communityDTO.setOriginalFileName(originalFileName);
		
		CommunityEntity entity = CommunityEntity.toEntity(communityDTO);
				
//				log.info("파일 저장 경로: {}", uploadPath);
		
		communityRepository.save(entity);		
	}

	public Page<CommunityDTO> selectAll(Pageable pageable, String searchItem, String searchWord) {

		// -1을 한 이유: DB의 Page는 0부터 시작함. 사용자는 1을 요청하기 때문에 우리는 -1을 해줘야 함...
		int pageNumber = pageable.getPageNumber() -1;
		
		// 2) 검색 조회
		Page<CommunityEntity> temp = null;
		
		switch (searchItem) {
		case "title": 
			temp = communityRepository.findByTitleContains(
					searchWord,
					PageRequest.of(pageNumber, pageLimit, Sort.by(Sort.Direction.DESC, "createDate")));
			break;			
		case "boardContent":
			temp = communityRepository.findByBoardContentContains(
					searchWord,
					PageRequest.of(pageNumber, pageLimit, Sort.by(Sort.Direction.DESC, "createDate")));
			break;
		}
			
		// 1) 단순 조회
//		List<BoardEntity> temp = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "createDate"));
				
		Page<CommunityDTO> list = null;
		
		// 2) Lambda 객체, Stream : List, Set, Map (등등 데이터 묶음을 처리하기 위한 용도!) 
		
		list = temp.map((entity) -> CommunityDTO.toDTO(entity));
		/* 아래 내용으로 정보 파악하기
		log.info("getSize: {}", list.getSize());
		// 한 페이지당 조회된 글 개수
		log.info("getTotalPages: {}", list.getTotalPages());
		// 총 페이지 개수
		log.info("getTotalElement: {}", list.getTotalElements());
		// 전체 게시글 개수
		log.info("getNumber: {}", list.getNumber());
		// 내가 요청한 페이지가 몇 번째 페이지인가?
		log.info("getNumberOfElements: {}", list.getNumberOfElements());
		// 현재 페이지의 총 글개수
		log.info("isFirst: {}", list.isFirst());
		// (전체 페이지 중에) 첫 번째 페이지인가요?
		log.info("isLast: {}", list.isLast());
		// 전체 페이지 중에 마지막 페이지인가요?
		log.info("getContent().get(0): {}", list.getContent().get(0));
		// 지금 보고 있는 페이지에서의 0번째 index, 즉 첫 번재 게시글의 내용(BoardDTO) 싹 불러오기.
		*/
		
		return list;
	}

	public CommunityDTO selectOne(Integer boardId) {
		Optional<CommunityEntity> temp = communityRepository.findById(boardId);
		
		if (!temp.isPresent())
			return null;
		
		return CommunityDTO.toDTO(temp.get());
	}
	
	/**
	 * boardId에 대한 조회수 증가
	 * save() : 데이터를 다른 값으로 바꾸는 메서드 
	 * update from board set hit_count = hit_count + 1 where board_seq = ?;
	 * 
	 * jpa에는 update 메서드가 없다. 
	 * 1) 조회 : findById(boardId) 
	 * 2) views를 getter로 가져온 후 + 1 
	 * 3) 변경된 값을 다시 setter를 통해 삽입 
	 * @param boardId
	 */
	@Transactional
	public void incrementViews(Integer boardId) {
		Optional<CommunityEntity> temp = communityRepository.findById(boardId);
		if(temp.isEmpty()) return;
		CommunityEntity entity = temp.get();
		
		int currentViews = (entity.getViews() != null ? entity.getViews() : 0);
		entity.setViews((currentViews + 1));
		
	}
	
	/**
	 * DB에 게시글을 삭제 + 게시글 지우면 첨부파일도 날려버리기
	 * @param boardId
	 */
	@Transactional
	public void deleteOne(Integer boardId) {
		Optional<CommunityEntity> temp = communityRepository.findById(boardId);
		
		// 글만 삭제하는 방식
		if(temp.isEmpty()) return;
		
		//temp에서 검색해보고, savedFileName이 존재하면 물리적으로 삭제하기
		if(temp.isPresent()) {
			// (temp를 get하고, getSavedFileName 해놓기)
			String savedFileName = temp.get().getSavedFileName();
			
			if (savedFileName != null) {
				String fullPath = uploadPath + "/" + savedFileName;
				FileService.deleteFile(fullPath);
			}
			
		communityRepository.deleteById(boardId);
		}
		
	}
	
	/**
	 * DB에 수정 처리 
	 * @param boardDTO
	 */
	@Transactional
	public void updateBoard(CommunityDTO communityDTO) {
		/*
	 	- 파일이 있는 경우
			- 글만 수정, 첨부파일은 그대로!
			- 글도 수정, 파일 추가 - 기존파일은?? 물리적으로 삭제, 글 수정, 파일명도 수정
		- 파일이 없는 경우
			- 글만 수정(이전 작업)
			- 글도 수정, 파일 추가 - 글 수정, 파일명도 추가
	 */
		
		MultipartFile file = communityDTO.getUploadFile();
		
		// 1) 첨부파일이 있나요? 확인하기
		String newFile = 
				!file.isEmpty() ? file.getOriginalFilename() : null;
		
		// 1. 수정하려는 데이터가 있는지 확인 
		Optional<CommunityEntity> temp = communityRepository.findById(communityDTO.getBoardId());
		
		// 2. 없으면 return
		if(!temp.isPresent()) return;
		
		// 3. 있으면 dto -> entity로 변환
		CommunityEntity entity = temp.get();
		
		// 기존파일이 DB에 저장되어있는지 확인하기.
		String oldFile = entity.getSavedFileName();
		
		// (1) 기존 파일이 있고 업로드한 파일도 있다면,
		//	-- 하드 디스크에서는 기존 파일 삭제, 업로드한 파일을 저장 (물리적)
		//	-- DB에는 업로드한 파일로 두 개의 칼럼을 업데이트	
		// (2) 기존 파일이 없고 업로드한 파일은 있다면,
		//	-- 하드 디스크에서 삭제할 건 없지? 업로드한 파일을 저장 (물리적)
		//	-- DB에는 업로드한 파일로 두 개의 칼럼을 업데이트
		
		// 업로드한 파일이 있다면
		if(newFile != null) {
			String savedFileName = null;
			savedFileName = FileService.saveFile(file, uploadPath);
			// 파일 이름은 newFile에서 가져올 수 있지?
			entity.setSavedFileName(savedFileName);
			entity.setOriginalFileName(newFile);
			
			// 새 파일은 없고... 예전 파일이 있는 상태라면?~
			if(oldFile != null) {
				String fullPath = uploadPath + "/" + oldFile;
				FileService.deleteFile(fullPath);
			}
			
		}
		// 4. 저장(update)
		entity.setTitle(communityDTO.getTitle());
		entity.setBoardContent(communityDTO.getBoardContent());
	}
	
	/**
	 * file 관련 두 개의 컬럼 값을 null로 변경
	 * @param boardId
	 * */
	@Transactional
	public void deleleFile(Integer boardId) {
		Optional<CommunityEntity> temp = communityRepository.findById(boardId);
		
		if (temp.isPresent()) {
			CommunityEntity entity = temp.get();
			
			entity.setOriginalFileName(null);
			entity.setSavedFileName(null);
		}
	}
	

}
