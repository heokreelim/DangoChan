package net.scit.DangoChan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import net.scit.DangoChan.entity.BoardLikesEntity;

public interface BoardLikesRepository extends JpaRepository<BoardLikesEntity, Long> {
	
//	Optional<BoardLikesEntity> findByBoard_BoardIdAndUserId(Integer boardId, Long userId); // 02.26 임시 수정

    @Query("SELECT COUNT(b) FROM BoardLikesEntity b WHERE b.communityEntity.boardId = :boardId")
    Integer countByBoardId(@Param("boardId") Integer boardId);

//    @Modifying // 02.26 임시 수정
//    @Transactional
//    @Query("DELETE FROM BoardLikesEntity b WHERE b.communityEntity.boardId = :boardId AND b.userId = :userId")
//    void deleteByBoardIdAndUserId(@Param("boardId") Integer boardId, @Param("userId") Long userId);

}
