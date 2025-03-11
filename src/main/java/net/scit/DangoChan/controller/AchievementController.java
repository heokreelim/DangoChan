package net.scit.DangoChan.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.scit.DangoChan.service.DeckStudyTimeService;
import net.scit.DangoChan.service.FlashCardService;
import net.scit.DangoChan.service.UserService;


@RestController
@RequiredArgsConstructor
public class AchievementController {
	
	private final UserService userService;
	private final FlashCardService flashCardService;
	private final DeckStudyTimeService deckStudyTimeService;
	
	// 출석정보 조회
	@GetMapping("/attendance")
	public ResponseEntity<List<String>> getAttendance(
	        @RequestParam(name = "userId") Long userId,
	        @RequestParam(name = "year", required = false) Integer year,
	        @RequestParam(name = "month", required = false) Integer month) {

	    List<LocalDate> dates;

	    if (year != null && month != null) {
	        // 특정 연도와 월 기준으로 조회 (특정 월만 조회하는 메서드가 필요하면 추가!)
	        dates = deckStudyTimeService.getAttendanceDatesByMonth(userId, year, month);
	    } else {
	        // 기본 전체 조회
	        dates = deckStudyTimeService.getAttendanceDates(userId);
	    }

	    List<String> dateStrList = dates.stream()
	            .map(LocalDate::toString)
	            .toList();

	    return ResponseEntity.ok(dateStrList);
	}

    
}
