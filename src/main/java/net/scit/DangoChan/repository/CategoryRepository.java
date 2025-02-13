package net.scit.DangoChan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.scit.DangoChan.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

}
