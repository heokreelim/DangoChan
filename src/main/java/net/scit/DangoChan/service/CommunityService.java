package net.scit.DangoChan.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.CommunityDTO;
import net.scit.DangoChan.dto.LoginUserDetails;
import net.scit.DangoChan.entity.CommunityEntity;
import net.scit.DangoChan.entity.UserEntity;
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
		
		// 현재 인증된 사용자의 정보를 SecurityContextHolder에서 가져오기.
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 // LoginUserDetails에서 UserEntity 추출
		LoginUserDetails loginUserDetails = (LoginUserDetails) authentication.getPrincipal();
		
		// 최소한 userId만 세팅된 UserEntity 객체 생성
		UserEntity userEntity = new UserEntity();
		userEntity.setUserId(loginUserDetails.getUserId());

		 // 변경된 toEntity() 메서드를 사용하여 엔티티를 생성
		CommunityEntity entity = CommunityEntity.toEntity(communityDTO, userEntity);
		
//		log.info("파일 저장 경로: {}", uploadPath);
		
		communityRepository.save(entity);		
	}

	public Page<CommunityDTO> selectAll(
			Pageable pageable,
			String searchItem,
			String searchWord,
			String primarySortField,	// 추가 정렬 기준
			String primarySortOrder,	// 추가 정렬 순서
			String secondarySortField,
			String secondarySortOrder
			) {

		// -1을 한 이유: DB의 Page는 0부터 시작함. 사용자는 1을 요청하기 때문에 우리는 -1을 해줘야 함...
		int pageNumber = Math.max(0, pageable.getPageNumber() -1);

	    List<Sort.Order> orders = new ArrayList<>();

	    // Primary 정렬
	    Sort.Direction primaryDirection = "asc".equalsIgnoreCase(primarySortOrder) 
	            ? Sort.Direction.ASC 
	            : Sort.Direction.DESC;
	    orders.add(new Sort.Order(primaryDirection, primarySortField));

	    // Secondary 정렬이 지정되었다면 추가
	    if (secondarySortField != null && !secondarySortField.isEmpty()) {
	        Sort.Direction secondaryDirection = "asc".equalsIgnoreCase(secondarySortOrder) 
	                ? Sort.Direction.ASC 
	                : Sort.Direction.DESC;
	        orders.add(new Sort.Order(secondaryDirection, secondarySortField));
	    }
	    
	    // 예를 들어 기본 정렬(작성일 내림차순)을 항상 보조 정렬로 추가할 수도 있습니다.
	    // if (!"createDate".equals(primarySortField)) {
	    //     orders.add(new Sort.Order(Sort.Direction.DESC, "createDate"));
	    // }

	    Sort compositeSort = Sort.by(orders);
		
		
//		// 만약에 대비한 -- "정렬 순서 결정"
//	    Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
//	    // 실제 사용 가능한 정렬 필드인지 검증하는 로직을 추가하는 것이 좋습니다.
//	    Sort sort = Sort.by(direction, sortField);
		
		// 2) 검색 조회
		Page<CommunityEntity> temp;
		
		switch (searchItem) {
			case "title": 
				temp = communityRepository.findByTitleContains(
						searchWord,
						PageRequest.of(pageNumber, pageLimit, compositeSort));
				break;
			case "boardContent":
				temp = communityRepository.findByBoardContentContains(
						searchWord,
						PageRequest.of(pageNumber, pageLimit, compositeSort));
				break;
			case "userName":
				temp = communityRepository.findByUser_UserNameContains(
						searchWord,
						PageRequest.of(pageNumber, pageLimit, compositeSort));
				break;
	        default:
	            // 정의되지 않은 검색 조건은 전체 조회 처리
	            temp = communityRepository.findAll(
	            		PageRequest.of(pageNumber, pageLimit, compositeSort));
	            break;
		}

	    return temp.map(CommunityDTO::toDTO);
		
		// 1) 단순 조회
//		List<BoardEntity> temp = boardRepository.findAll(Sort.by(Sort.Direction.DESC, "createDate"));
				
//		Page<CommunityDTO> list = null;
//		
//		// 2) Lambda 객체, Stream : List, Set, Map (등등 데이터 묶음을 처리하기 위한 용도!) 
//		
//		list = temp.map((entity) -> CommunityDTO.toDTO(entity));

//		
//		return list;
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
	    MultipartFile file = communityDTO.getUploadFile();
	    String newFile = !file.isEmpty() ? file.getOriginalFilename() : null;

	    Optional<CommunityEntity> temp = communityRepository.findById(communityDTO.getBoardId());
	    if (!temp.isPresent()) {
	        throw new RuntimeException("수정할 게시글이 존재하지 않습니다.");
	    }
	    CommunityEntity entity = temp.get();
	    
	    // 첨부파일 검증: 기존 파일이 없고 새 파일도 없는 경우 에러 발생
	    if (newFile == null && entity.getSavedFileName() == null) {
	        throw new RuntimeException("게시글은 반드시 첨부파일을 포함해야 합니다.");
	    }
	    
	    // 새 파일이 업로드되면 파일 저장 및 기존 파일 삭제 처리
	    if (newFile != null) {
	        // 기존 첨부파일 정보 저장 (물리 파일 삭제를 위해)
	        String oldFile = entity.getSavedFileName();
	        String savedFileName = FileService.saveFile(file, uploadPath);
	        entity.setSavedFileName(savedFileName);
	        entity.setOriginalFileName(newFile);
	        
	        if (oldFile != null) {
	            String fullPath = uploadPath + "/" + oldFile;
	            FileService.deleteFile(fullPath);
	        }
	    }
	    
	    // 제목과 내용 업데이트
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
