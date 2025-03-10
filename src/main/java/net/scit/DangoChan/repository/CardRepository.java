package net.scit.DangoChan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.scit.DangoChan.entity.CardEntity;
import net.scit.DangoChan.entity.DeckEntity;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long> {

    // ✅ 새로운 카드 (스터디 레벨 0) 목록 가져오기
    @Query("SELECT c FROM CardEntity c WHERE c.studyLevel = 0 AND c.deckEntity.deckId = :deckId")
    List<CardEntity> findNewCardsByDeckId(@Param("deckId") Long deckId);

    // ✅ 복습할 카드 (스터디 레벨 1 또는 2) 목록 가져오기
    @Query("SELECT c FROM CardEntity c WHERE c.studyLevel IN (1, 2) AND c.deckEntity.deckId = :deckId")
    List<CardEntity> findReviewCardsByDeckId(@Param("deckId") Long deckId);

    // ✅ 특정 덱에서 studyLevel이 0인 카드 개수 조회
    int countByDeckEntity_DeckIdAndStudyLevel(Long deckId, int studyLevel);

    // ✅ 특정 덱의 모든 카드 가져오기
    List<CardEntity> findByDeckEntity_DeckId(Long deckId);





	List<CardEntity> findAllByDeckEntity(Optional<DeckEntity> temp);
}
