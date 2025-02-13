package net.scit.DangoChan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.scit.DangoChan.entity.DeckEntity;

public interface DeckRepository extends JpaRepository<DeckEntity, Long> {

}
