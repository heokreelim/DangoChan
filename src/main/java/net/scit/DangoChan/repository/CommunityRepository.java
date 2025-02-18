package net.scit.DangoChan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.scit.DangoChan.entity.CommunityEntity;

public interface CommunityRepository extends JpaRepository<CommunityEntity, Integer> {
	

}
