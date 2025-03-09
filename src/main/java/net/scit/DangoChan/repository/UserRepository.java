package net.scit.DangoChan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.scit.DangoChan.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	boolean existsByEmail(String email);
	
	boolean existsByAuthTypeAndProviderId(String authType, String providerId);

	Optional<UserEntity> findByEmail(String email);
	
	Optional<UserEntity> findByAuthTypeAndProviderId(String authType, String providerId);
}
