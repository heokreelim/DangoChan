package net.scit.DangoChan;

import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import net.scit.DangoChan.entity.CommunityEntity;
import net.scit.DangoChan.entity.UserEntity;
import net.scit.DangoChan.repository.CommunityRepository;
import net.scit.DangoChan.repository.UserRepository;

@SpringBootTest
class DangoChanApplicationTests {

	  @Autowired
	    private CommunityRepository communityRepository;

	    @Autowired
	    private UserRepository userRepository;

	    @Test
	    void insertDummyCommunityData() {

	        // ✅ 실제 존재하는 사용자 ID (더미용 사용자)
	        Long userId = 1L; // UserEntity 기본 키 (PK)
	        
	        Optional<UserEntity> userOptional = userRepository.findById(userId);

	        if (userOptional.isEmpty()) {
	            System.out.println("테스트용 UserEntity가 없습니다. 유저부터 먼저 생성하세요!");
	            return;
	        }

	        UserEntity userEntity = userOptional.get();

	        // ✅ 더미 데이터 생성
	        String[] sampleTitles = {
	                "JLPT N1 덱 공유합니다!",
	                "일본 애니 단어장 추천",
	                "비즈니스 일본어 필수 단어",
	                "N3 시험 대비 단어장",
	                "한자 암기용 덱입니다."
	        };

	        String[] sampleContents = {
	                "이 덱으로 공부하시면 합격 확률 높습니다!",
	                "정리해둔 덱이에요. 참고하셔도 좋아요!",
	                "복습용으로 만든 덱입니다. 매일 반복 추천!",
	                "시험 전에 반드시 외워야 할 단어만 모았습니다.",
	                "기본부터 심화까지 전부 정리했습니다!"
	        };

	        String[] sampleFiles = {
	                "deck_sample_1.xlsx",
	                "deck_sample_2.xlsx",
	                "deck_sample_3.xlsx",
	                null
	        };

	        Random random = new Random();

	        // ✅ 반복문으로 게시글 생성
	        for (int i = 1; i <= 100; i++) {
	            String title = sampleTitles[random.nextInt(sampleTitles.length)];
	            String content = sampleContents[random.nextInt(sampleContents.length)];
	            String originalFile = sampleFiles[random.nextInt(sampleFiles.length)];
	            String savedFile = (originalFile != null) ? "saved_" + originalFile : null;

	            CommunityEntity entity = CommunityEntity.builder()
	                    .user(userEntity)
	                    .title(title + " - " + i)
	                    .wordCount(random.nextInt(100) + 1) // 단어 수 (1~100개)
	                    .views(random.nextInt(500))        // 조회수 (0~500)
	                    .boardContent(content)
	                    .originalFileName(originalFile)
	                    .savedFileName(savedFile)
	                    .build();

	            communityRepository.save(entity);
	        }

	        System.out.println("단고짱 커뮤니티 더미 데이터 생성 완료!");
	    }
	}
