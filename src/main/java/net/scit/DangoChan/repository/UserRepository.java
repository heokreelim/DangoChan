package net.scit.DangoChan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.scit.DangoChan.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	// LHR start
	boolean existsByEmail(String email);

	Optional<UserEntity> findByEmail(String email);
	// LHR end
	
	// PJB start
	
	// PJB end
}
