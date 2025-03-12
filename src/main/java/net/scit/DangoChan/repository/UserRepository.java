package net.scit.DangoChan.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import net.scit.DangoChan.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	// LHR start
	boolean existsByEmail(String email);
	
	boolean existsByAuthTypeAndProviderId(String authType, String providerId);
	
	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByEmailAndAuthType(String email, String authType);
	
	Optional<UserEntity> findByAuthTypeAndProviderId(String authType, String providerId);
	
	@Query(value = "SELECT COUNT(*) FROM users WHERE user_name REGEXP '^Guest_[0-9]+$'", nativeQuery = true)
	long countGuestUsersWithNumbers();
	// LHR end
	
	// PJB start
	
    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.profileImage = :profileImage WHERE u.userId = :userId")
    int updateProfileImage(@Param("userId") Long userId, @Param("profileImage") Integer profileImage);

	
	// PJB end
}
