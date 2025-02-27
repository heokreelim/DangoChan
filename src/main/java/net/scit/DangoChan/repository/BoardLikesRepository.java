package net.scit.DangoChan.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import net.scit.DangoChan.entity.BoardLikesEntity;
import net.scit.DangoChan.entity.CommunityEntity;
import net.scit.DangoChan.entity.UserEntity;

public interface BoardLikesRepository extends JpaRepository<BoardLikesEntity, Long> {

    // 특정 게시글에 대해 특정 사용자가 좋아요를 했는지 확인
//    Optional<BoardLikesEntity> findByCommunityEntityAndUserEntity(CommunityEntity communityEntity, UserEntity userEntity);
	boolean existsByCommunityEntityAndUserEntity(CommunityEntity communityEntity, UserEntity userEntity);

    // 게시글의 좋아요 개수를 조회 (카운트 쿼리)
    Long countByCommunityEntity(CommunityEntity communityEntity);

}
