package net.scit.DangoChan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import net.scit.DangoChan.entity.CategoryEntity;
import net.scit.DangoChan.entity.DeckEntity;

public interface DeckRepository extends JpaRepository<DeckEntity, Long> {

	// 2번 사용 - 예시) 파라미터로 id를 받아 첫번째테이블의 데이터를 조회하는 메서드
		@EntityGraph(attributePaths = {"categoryEntity", "cardEntityList"})
	    List<CategoryEntity> findAllByCategoryEntity_CategoryId(Long categoryId);
}
