package net.scit.DangoChan.repository;

import net.scit.DangoChan.dto.CardDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import net.scit.DangoChan.entity.CardEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long> {
    // 덱 ID에 해당하는 카드 중 랜덤으로 하나 가져오기
    @Query("SELECT c FROM CardEntity c WHERE c.deckId = :deckId ORDER BY RAND() LIMIT 1")
    Optional<CardEntity> findCardByDeckId(Long deckId);

}
