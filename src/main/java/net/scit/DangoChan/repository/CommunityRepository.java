package net.scit.DangoChan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import net.scit.DangoChan.entity.CommunityEntity;

public interface CommunityRepository extends JpaRepository<CommunityEntity, Integer> {
	// KSY START
	Page<CommunityEntity> findByTitleContains(String searchWord, Pageable pageable);

	Page<CommunityEntity> findByBoardContentContains(String searchWord, Pageable pageable);
	// UserEntity의 userName으로 검색하기
	// (연관 엔티티의 필드를 사용할 때는 _를 사용)
	Page<CommunityEntity> findByUser_UserNameContains(String searchWord, Pageable pageable);
	// KSY END
	
	// PJB START	
    @Query("SELECT COUNT(c) FROM CommunityEntity c WHERE c.user.userId = :userId")
    int countCommunityByUserId(@Param("userId") Long userId);
	// PJB END
}
