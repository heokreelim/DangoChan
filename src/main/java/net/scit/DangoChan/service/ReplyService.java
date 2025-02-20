package net.scit.DangoChan.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.repository.CommunityRepository;
import net.scit.DangoChan.repository.ReplyRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReplyService {
	
	private final CommunityRepository communityRepository;
	private final ReplyRepository replyRepository;
	
	/**
	 * 전달받은 값을 entity로 수정한 후에 DB에 저장
	 * @param replyDTO
	 * */
 

}
