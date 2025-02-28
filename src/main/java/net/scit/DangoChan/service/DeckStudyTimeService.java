package net.scit.DangoChan.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.scit.DangoChan.dto.DeckStudyTimeDTO;
import net.scit.DangoChan.dto.UserDTO;
import net.scit.DangoChan.entity.DeckEntity;
import net.scit.DangoChan.entity.DeckStudyTimeEntity;
import net.scit.DangoChan.entity.UserEntity;
import net.scit.DangoChan.repository.DeckRepository;
import net.scit.DangoChan.repository.DeckStudyTimeRepository;
import net.scit.DangoChan.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class DeckStudyTimeService {
    private final DeckStudyTimeRepository deckStudyTimeRepository;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;

    // SYH start ====
    /**
     * 📌 특정 Deck에 study_time 저장 (deckId만 사용)
     */
    @Transactional
    public void saveStudyTime(Long deckId, int studyTimeSeconds) {
        // 🟢 deckId로 DeckEntity 찾기
        DeckEntity deckEntity = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        // 🟢 새로운 DeckStudyTimeEntity 저장
        DeckStudyTimeEntity studyTimeEntity = DeckStudyTimeEntity.builder()
                .deckEntity(deckEntity)  // ✅ DeckEntity 저장
                .studyTime(studyTimeSeconds)
                .date(LocalDateTime.now()) // ✅ 현재 시간 저장
                .build();

        deckStudyTimeRepository.save(studyTimeEntity);
    }
    
    // SYH end ====
    
    // PJB start =====

	// 전달 받은 UserDTO를 DeckStudyTimeDTO 의 출석정보를 반환
	public List<DeckStudyTimeDTO> checkAttendance(UserDTO userDTO) {
		
		// User 정보 조회
		Optional<UserEntity> userEntity = userRepository.findById(userDTO.getUserId());
		LocalDateTime atStartOfDay = userEntity.get().getCreatedAt();
		LocalDateTime atTime = LocalDateTime.now();
		
		// 출석일을 조회할 리스트 생성
		List<DeckStudyTimeEntity> temp = null;
		
		// 전달받은 아이디로 해당 유저의 출석을 조회, 리스트로 반환
//		temp  = deckStudyTimeRepository.findByUserIdAndDateBetween(
//				userDTO.getUserId(), 
//				atStartOfDay,
//				atTime);
		
		// Entity -> DTO 로 변환
	    List<DeckStudyTimeDTO> attendanceList = temp.stream()
	            .map(DeckStudyTimeDTO::toDTO)
	            .collect(Collectors.toList());
	    
		return attendanceList;
	}
    // 유저 ID로  출석 데이터 가져오기
	public List<DeckStudyTimeDTO> getUserAttendance(Long userId) {
	    // deckStudyTimeRepository에서 유저의 학습 기록 조회 (userId 필드 기준)
	    List<DeckStudyTimeEntity> entities = null;//deckStudyTimeRepository.findAllByUserId(userId);
	    return entities.stream()
	                   .map(DeckStudyTimeDTO::toDTO)
	                   .collect(Collectors.toList());
	}
    // PJB end =====
}
