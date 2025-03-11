package net.scit.DangoChan.service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.entity.DeckEntity;
import net.scit.DangoChan.repository.CardRepository;
import net.scit.DangoChan.repository.CommunityRepository;
import net.scit.DangoChan.repository.DeckRepository;
import net.scit.DangoChan.repository.DeckStudyTimeRepository;
import net.scit.DangoChan.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class AchievementService {
	
	private final DeckStudyTimeService deckStudyTimeService;
    private final DeckStudyTimeRepository deckStudyTimeRepository;
    private final DeckRepository deckRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final CommunityRepository communityRepository;
    

 // 유저의 개인영역 업적 조회
 	public List<String> getPersonalAchievements(Long userId) {
         List<String> achievements = new ArrayList<>();

      // 연속 출석 일수
         int consecutiveAttendance = getConsecutiveAttendance(userId);
         if (consecutiveAttendance >= 7) achievements.add("7일 연속 출석 달성!");
         if (consecutiveAttendance >= 30) achievements.add("30일 연속 출석 달성!");

         // 누적 출석 일수
         int totalAttendance = getTotalAttendance(userId);
         if (totalAttendance >= 100) achievements.add("누적 100일 출석 달성!");
         if (totalAttendance >= 365) achievements.add("누적 365일 출석 달성!");

         // 일일 학습 단어 수
         int todayLearnedWords = getTodayLearnedWords(userId);
         if (todayLearnedWords >= 100) achievements.add("일일 100개 단어 학습 달성!");
         if (todayLearnedWords >= 300) achievements.add("일일 300개 단어 학습 달성!");

         // 누적 학습 단어 수
         int totalLearnedWords = getTotalLearnedWords(userId);
         if (totalLearnedWords >= 500) achievements.add("누적 500개 단어 학습 달성!");
         if (totalLearnedWords >= 1000) achievements.add("누적 1000개 단어 학습 달성!");

         return achievements;
     }
 	
 	// 유저의 커뮤니티영역 업적 조회
     public List<String> getCommunityAchievements(Long userId) {
         List<String> achievements = new ArrayList<>();

         int boardCount = communityRepository.countCommunityByUserId(userId);

         if (boardCount >= 1) achievements.add("애기당고 - 게시글 1개 달성!");
         if (boardCount >= 10) achievements.add("당고유저 - 게시글 10개 달성!");
         if (boardCount >= 20) achievements.add("당고짱 - 게시글 20개 달성!");

         return achievements;
     }
     
     // ********************* 서비스 내 사용될 내부 로직 ************************ // 
     // ********************* 1. 연속 출석 일수 조회 (업적 조회용) ************************** // 
     public int getConsecutiveAttendance(Long userId) {

         List<LocalDate> attendanceDates = deckStudyTimeService.getAttendanceDates(userId);
         if (attendanceDates.isEmpty()) {
             return 0;
         }

         attendanceDates.sort(Collections.reverseOrder()); // 최신 날짜부터 정렬
         int consecutiveDays = 1;

         LocalDate previousDate = attendanceDates.get(0);

         for (int i = 1; i < attendanceDates.size(); i++) {
             LocalDate currentDate = attendanceDates.get(i);

             // 날짜 차이가 1일이면 연속
             if (previousDate.minusDays(1).equals(currentDate)) {
                 consecutiveDays++;
                 previousDate = currentDate;
             } else {
                 // 연속이 끊기면 종료
                 break;
             }
         }

         return consecutiveDays;
     }
     
     // ********************* 2. 누적 출석 일수 조회 ************************** // 
     public int getTotalAttendance(Long userId) {

         List<LocalDate> attendanceDates = deckStudyTimeService.getAttendanceDates(userId);

         return attendanceDates.size();
     }
    
     // ********************* 3. 일일 학습한 단어 수 조회 ************************** // 
     public int getTodayLearnedWords(Long userId) {

         ZoneId zoneId = ZoneId.of("Asia/Seoul");
         LocalDate today = LocalDate.now(zoneId);

         // card 테이블에서 study_level이 3이면서 studied_at이 오늘인 단어 수를 카운트
         List<Object[]> dailyLearnedWords = cardRepository.findDailyLearnedWords(userId);

         for (Object[] row : dailyLearnedWords) {
             LocalDate date = ((Date) row[0]).toLocalDate();
             if (date.equals(today)) {
                 return ((Number) row[1]).intValue();
             }
         }

         return 0;
     }
     
     // ********************* 4. 학습한 단어 누적 수 조회 ************************** // 
     public int getTotalLearnedWords(Long userId) {

         // card 테이블에서 study_level이 3인 개수를 전부 카운트
         return cardRepository.countByDeckEntity_DeckIdAndStudyLevel(userId, 3);
     }
     
  // ********************* 5. 금일 학습 시간 조회 ************************** // 
     public String getTodayStudyTimeFormatted(Long userId) {
    	    List<Long> deckIds = deckRepository.findByCategoryEntity_UserEntity_UserId(userId)
    	            .stream().map(DeckEntity::getDeckId).toList();

    	    if (deckIds.isEmpty()) return "0분";

    	    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
    	    LocalDateTime startOfDay = today.atStartOfDay();
    	    LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1);

    	    Integer totalStudySeconds = deckStudyTimeRepository.sumStudyTimeByDeckIdsAndDateRange(deckIds, startOfDay, endOfDay);

    	    if (totalStudySeconds == null || totalStudySeconds == 0) {
    	        return "0분";
    	    }

    	    int totalMinutes = totalStudySeconds / 60; // 초를 분으로 내림 처리
    	    int hours = totalMinutes / 60;
    	    int minutes = totalMinutes % 60;

    	    // 포맷 처리
    	    if (hours == 0) {
    	        return minutes + "분";
    	    } else if (minutes == 0) {
    	        return hours + "시간";
    	    } else {
    	        return hours + "시간 " + minutes + "분";
    	    }
    	}
     
     // ********************* 6. 연속 출석 일수  조회 (화면 표시용)************************** // 
     public int getAttendanceStreak(Long userId) {
    	    List<LocalDate> attendanceDates = deckStudyTimeService.getAttendanceDates(userId);

    	    // KST 기준
    	    ZoneId zoneId = ZoneId.of("Asia/Seoul");
    	    LocalDate today = LocalDate.now(zoneId);
    	    LocalDate yesterday = today.minusDays(1);

    	    if (attendanceDates.isEmpty()) {
    	        return 0;
    	    }

    	    // ✅ attendanceDates는 정렬되어 있다고 가정
    	    int streak = 1;
    	    LocalDate streakDate = attendanceDates.get(attendanceDates.size() - 1);

    	    // 1. 오늘 출석이 되어있으면 streak 유지 (+1일)
    	    if (streakDate.equals(today)) {
    	        streak = calculateStreak(attendanceDates, today);
    	    }

    	    // 2. 오늘 출석이 없더라도 어제까지 streak가 유지되고 있다면 streak 유지
    	    else if (streakDate.equals(yesterday)) {
    	        streak = calculateStreak(attendanceDates, yesterday);
    	    }

    	    // 3. 연속 출석이 어제에서 끊긴 경우
    	    else {
    	        streak = 0;
    	    }

    	    return streak;
    	}
     
     // ********************* 7.  streak 계산 메서드 ************************** // 
     private int calculateStreak(List<LocalDate> attendanceDates, LocalDate endDate) {
    	    int streak = 1;
    	    LocalDate current = endDate;

    	    for (int i = attendanceDates.size() - 1; i >= 0; i--) {
    	        LocalDate date = attendanceDates.get(i);
    	        if (date.equals(current)) {
    	            current = current.minusDays(1);
    	            if (!date.equals(endDate)) {
    	                streak++;
    	            }
    	        } else if (date.isBefore(current)) {
    	            break;
    	        }
    	    }

    	    return streak;
    	}

}
