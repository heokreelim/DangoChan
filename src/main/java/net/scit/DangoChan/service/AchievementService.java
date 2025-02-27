//package net.scit.DangoChan.service;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.hibernate.dialect.function.CaseLeastGreatestEmulation;
//import org.springframework.stereotype.Service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import net.scit.DangoChan.dto.DeckDTO;
//import net.scit.DangoChan.dto.DeckStudyTimeDTO;
//import net.scit.DangoChan.dto.UserDTO;
//import net.scit.DangoChan.entity.DeckEntity;
//import net.scit.DangoChan.entity.DeckStudyTimeEntity;
//import net.scit.DangoChan.entity.UserEntity;
//import net.scit.DangoChan.repository.DeckRepository;
//import net.scit.DangoChan.repository.DeckStudyTimeRepository;
//import net.scit.DangoChan.repository.UserRepository;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class AchievementService {
//
//    private DeckStudyTimeRepository deckStudyTimeRepository;
//    private DeckRepository deckRepository;
//    private UserRepository userRepository;
//
//    // 7일 연속 출석  업적 메서드
//    public boolean 	checkAttendanceAchievementSevenDays (Long userId) {
//
//    	// 오늘 날짜 및 최근 7일간 출석 데이터 확인
//    	LocalDate today = LocalDate.now();
//        LocalDate sevenDaysAgo = today.minusDays(6);
//
//        List<DeckStudyTimeEntity> temp = deckStudyTimeRepository
//                .findByUserIdAndDateBetween(userId, sevenDaysAgo.atStartOfDay(), today.atTime(23, 59, 59));
//
//        List<DeckStudyTimeDTO> dtoList = new ArrayList<>();
//
//        // Entity -> DTO
//        for (DeckStudyTimeEntity studyTime : temp) {
//        	dtoList.add(DeckStudyTimeDTO.toDTO(studyTime));
//		}
//
//        return false;
//    }
//
//
//
//
//}
