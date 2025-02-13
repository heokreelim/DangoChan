package net.scit.DangoChan.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import net.scit.DangoChan.dto.CommunityDTO;

@Service
public class CommunityService {

	public void insertBoard(CommunityDTO communityDTO) {
		
	}

	public Page<CommunityDTO> selectAll(Pageable pageable, String searchItem, String searchWord) {

		
		return null;
	}
	
	
}
