package net.scit.DangoChan.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.scit.DangoChan.entity.DeckStudyTimeEntity;

@Repository
public interface DeckStudyTimeRepository extends JpaRepository<DeckStudyTimeEntity, Long> {
	
	
	
	
	// PJB start
	// 하루 단위로 그룹화, 총 학습시간의 합 출력
	@Query("SELECT DATE(d.date), SUM(d.studyTime) " +
		       "FROM DeckStudyTimeEntity d " +
		       "WHERE d.deckEntity.deckId IN :deckIds " +
		       "GROUP BY DATE(d.date)")
		List<Object[]> findDailyTotalStudyTime(@Param("deckIds") List<Long> deckIds);
		
		
		@Query("SELECT SUM(d.studyTime) " +
			       "FROM DeckStudyTimeEntity d " +
			       "WHERE d.deckEntity.deckId IN :deckIds " +
			       "AND d.date BETWEEN :start AND :end")
			Integer sumStudyTimeByDeckIdsAndDateRange(@Param("deckIds") List<Long> deckIds,
			                                          @Param("start") LocalDateTime start,
			                                          @Param("end") LocalDateTime end);
	
	// PJB end
    
    // SYH start

    // SYH end
}
