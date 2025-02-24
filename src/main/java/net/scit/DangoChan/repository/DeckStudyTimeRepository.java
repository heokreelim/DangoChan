package net.scit.DangoChan.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import net.scit.DangoChan.entity.DeckStudyTimeEntity;

@Repository
public interface DeckStudyTimeRepository extends JpaRepository<DeckStudyTimeEntity, Long> {
	
	
	
	
	// PJB start
	// 특정 유저의 출석일자들을 반환 (유저 아이디, 시작일, 오늘까지)
		List<DeckStudyTimeEntity> findByUserIdAndDateBetween(Long userId, LocalDateTime atStartOfDay, LocalDateTime atTime);

    // 특정 유저가 오늘 공부한 단어의 수 (study_level이 1 또는 2에서 3으로 상승한 단어 수)
    //@Query("SELECT COUNT(c) FROM card c WHERE c.user_id = :user_id AND c.study_level = 3 AND c.date BETWEEN :startDate AND :endDate")
    //int countWordsLearnedToLevel3(Long userId, LocalDateTime startDate, LocalDateTime endDate);
	// PJB end
    
    // SYH start
    
    // SYH end
}
