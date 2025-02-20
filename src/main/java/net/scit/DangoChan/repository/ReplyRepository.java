package net.scit.DangoChan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.scit.DangoChan.entity.ReplyEntity;

public interface ReplyRepository extends JpaRepository<ReplyEntity, Integer> {

}
