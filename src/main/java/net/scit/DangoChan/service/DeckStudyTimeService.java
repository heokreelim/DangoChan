package net.scit.DangoChan.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.scit.DangoChan.entity.DeckEntity;
import net.scit.DangoChan.entity.DeckStudyTimeEntity;
import net.scit.DangoChan.repository.CardRepository;
import net.scit.DangoChan.repository.DeckRepository;
import net.scit.DangoChan.repository.DeckStudyTimeRepository;
import net.scit.DangoChan.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class DeckStudyTimeService {
    private final DeckStudyTimeRepository deckStudyTimeRepository;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    // SYH start ====
    /**
     * 📌 특정 Deck에 study_time 저장 (deckId만 사용)
     */
    @Transactional
    public void saveStudyTime(Long deckId, int studyTimeSeconds) {
        //deckId로 DeckEntity 찾기
        DeckEntity deckEntity = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        //새로운 DeckStudyTimeEntity 저장
        DeckStudyTimeEntity studyTimeEntity = DeckStudyTimeEntity.builder()
                .deckEntity(deckEntity)  // ✅ DeckEntity 저장
                .studyTime(studyTimeSeconds)
                .date(LocalDateTime.now()) // ✅ 현재 시간 저장
                .build();

        deckStudyTimeRepository.save(studyTimeEntity);
    }
    
    // SYH end ====
    
    // PJB start =====
    @Transactional
    public void saveStudyTimeByUser(Long userId, int studyTimeSeconds) {

        // 유저가 만든 모든 Deck 조회
        List<DeckEntity> deckEntities = deckRepository.findByCategoryEntity_UserEntity_UserId(userId);

        // Deck이 하나도 없을 경우 예외 처리
        if (deckEntities.isEmpty()) {
            throw new EntityNotFoundException("Deck not found");
        }

        // 첫 번째 Deck을 사용하거나 로직에 따라 반복문 사용
        DeckEntity firstDeck = deckEntities.get(0);

        // 새로운 DeckStudyTimeEntity 저장
        DeckStudyTimeEntity studyTimeEntity = DeckStudyTimeEntity.builder()
            .deckEntity(firstDeck)
            .studyTime(studyTimeSeconds)
            .date(LocalDateTime.now())
            .build();

        deckStudyTimeRepository.save(studyTimeEntity);
    }
    
    @Transactional
    public List<LocalDate> getAttendanceDates(Long userId) {

    	// 1. 날짜 기준은 Asia/Seoul
        ZoneId zoneId = ZoneId.of("Asia/Seoul");

        // 2. 오늘 날짜 가져오기 (한국 시간 기준)
        LocalDate today = LocalDate.now(zoneId);

        // 3. 유저가 생성한 모든 Deck의 deckId 조회
        // - category -> userEntity -> userId를 기준으로 해당 유저가 만든 Deck 조회
        List<Long> deckIds = deckRepository.findByCategoryEntity_UserEntity_UserId(userId)
                .stream()
                .map(DeckEntity::getDeckId)
                .toList();

        // deckIds가 비어있으면 학습 데이터가 없는 것이므로 출석 데이터도 없음
        if (deckIds.isEmpty()) {
            return Collections.emptyList();
        }

        // ===  일일 학습 시간 계산 (단위: 초) ===
        // 4. 특정 유저의 날짜별 총 학습 시간 조회
        // - deck_study_time 테이블에서 해당 deckIds 기준으로 date별로 study_time을 합산
        // - 반환 결과: List<Object[]> -> [날짜(date), 총학습시간(sum(study_time))]
        List<Object[]> dailyStudyTimes = deckStudyTimeRepository.findDailyTotalStudyTime(deckIds);

        // 5. 날짜별 학습 시간(초)을 Map에 저장
        Map<LocalDate, Integer> studyTimeMap = new HashMap<>();
        for (Object[] row : dailyStudyTimes) {
        	LocalDate date = ((Date) row[0]).toLocalDate();
        	Integer totalTime = ((Number) row[1]).intValue();
        	studyTimeMap.put(date, totalTime);
        }

        // === 일일 학습 단어 개수 계산 ===
        // 6. study_level이 3으로 변화한 단어 수를 날짜별로 조회
        // - card 테이블 기준으로 userId로 조회 (studied_at 날짜 사용)
        // - study_level이 3으로 갱신된 레코드 기준
        // - 반환 결과: List<Object[]> -> [날짜(studied_at), 학습단어 개수(count)]
        List<Object[]> dailyLearnedWords = cardRepository.findDailyLearnedWords(userId);

        // 7. 날짜별 학습한 단어 수를 Map에 저장
        Map<LocalDate, Integer> learnedWordMap = new HashMap<>();
        for (Object[] row : dailyLearnedWords) {
            LocalDate date = ((Date) row[0]).toLocalDate();
            Integer wordCount = ((Number) row[1]).intValue();
            learnedWordMap.put(date, wordCount);
        }

        // === 출석 인정 날짜 계산 ===
        // 8. 출석 인정 조건을 만족하는 날짜들을 저장할 Set 생성 (중복 방지)
        Set<LocalDate> attendanceDates = new HashSet<>();

        // 조건 1: 학습 시간 60분(3600초) 이상인 날짜
        studyTimeMap.forEach((date, totalTime) -> {
            if (totalTime >= 3600) {
                attendanceDates.add(date);
            }
        });

        // 조건 2: 학습 단어 200개 이상인 날짜
        learnedWordMap.forEach((date, wordCount) -> {
            if (wordCount >= 200) {
                attendanceDates.add(date);
            }
        });

     // 최종 출석 날짜 리스트 반환 (정렬해서 리턴)
        return attendanceDates.stream()
            .sorted()                     // 오름차순 정렬
            .collect(Collectors.toList());
   }
    
    // 이전, 다음 달 출석 데이터 조회
    @Transactional
    public List<LocalDate> getAttendanceDatesByMonth(Long userId, int year, int month) {
        List<LocalDate> allDates = getAttendanceDates(userId);

        return allDates.stream()
                .filter(date -> date.getYear() == year && date.getMonthValue() == month)
                .sorted()
                .collect(Collectors.toList());
    }

    // PJB end =====
}
