package net.scit.DangoChan.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import net.scit.DangoChan.entity.CommunityEntity;
import net.scit.DangoChan.entity.ReplyEntity;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {

	List<ReplyEntity> findByCommunityEntity(Optional<CommunityEntity> temp, Sort by);

}
