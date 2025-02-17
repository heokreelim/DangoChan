package net.scit.DangoChan.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

		
		return null;
	}
	
	
}
