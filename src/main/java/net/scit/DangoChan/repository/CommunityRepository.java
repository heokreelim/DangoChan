package net.scit.DangoChan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import net.scit.DangoChan.entity.CommunityEntity;

public interface CommunityRepository extends JpaRepository<CommunityEntity, Integer> {

	Page<CommunityEntity> findByTitleContains(String searchWord, PageRequest of);

	Page<CommunityEntity> findByBoardContentContains(String searchWord, PageRequest of);

}
