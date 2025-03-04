package net.scit.DangoChan.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import net.scit.DangoChan.entity.CategoryEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {


	
	/*
	 * DB로부터 데이터를 Join을 통해 조회할 경우
	 * 1. JPQL을 사용하여 아래와 같은 코드를 만들기(쿼리 직접 작성)
	 * @Query("SELECT jt FROM JoinTest jt " +
           "JOIN FETCH jt.ajaxMember " +
           "LEFT JOIN FETCH jt.joinTest2List " +
           "WHERE jt.ajaxMember.name = :name")
       List<JoinTest> findByAjaxMemberName(String name);
       
     * 2. @EntityGraph 사용(Join 자동화)
     * JPA에서 N+1 문제를 방지하면서 연관된 엔티티를 한 번의 쿼리로 가져오는 방법
	 */
	
	// 2번 사용 - 예시) 파라미터로 id를 받아 첫번째테이블의 데이터를 조회하는 메서드
//	@EntityGraph(attributePaths = {"userEntity", "deckEntityList"})
    List<CategoryEntity> findAllByUserEntity_UserId(Long userId);


	//PJB start
		// userId 를 전달하여 해당 유저가 소유한 카테고리를 선택
	//  Spring Data JPA 네이밍 규칙 활용
    Optional<CategoryEntity> findByCategoryIdAndUserEntity_UserId(Long categoryId, Long userId);
    Optional<CategoryEntity> findByUserEntity_UserId(Long userId);

    // @Query("SELECT c FROM CategoryEntity c WHERE c.categoryId = :categoryId AND c.userEntity.userId = :userId")
    // Optional<CategoryEntity> findByCategoryIdAndUserId(@Param("categoryId") Long categoryId, @Param("userId") Long userId);
// PJB end

}
