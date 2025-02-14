package net.scit.DangoChan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.scit.DangoChan.entity.CardEntity;

public interface CardRepository extends JpaRepository<CardEntity, Long> {

}
