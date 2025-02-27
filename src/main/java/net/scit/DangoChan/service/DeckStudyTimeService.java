package net.scit.DangoChan.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.scit.DangoChan.entity.DeckEntity;
import net.scit.DangoChan.entity.DeckStudyTimeEntity;
import net.scit.DangoChan.entity.UserEntity;
import net.scit.DangoChan.repository.DeckRepository;
import net.scit.DangoChan.repository.DeckStudyTimeRepository;
import net.scit.DangoChan.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DeckStudyTimeService {
    private final DeckStudyTimeRepository deckStudyTimeRepository;
    private final DeckRepository deckRepository;

    /**
     * 📌 특정 Deck에 study_time 저장 (deckId만 사용)
     */
    @Transactional
    public void saveStudyTime(Long deckId, int studyTimeSeconds) {
        // 🟢 deckId로 DeckEntity 찾기
        DeckEntity deckEntity = deckRepository.findById(deckId)
                .orElseThrow(() -> new RuntimeException("Deck not found"));

        // 🟢 studyTime(초)을 LocalTime으로 변환
        LocalTime studyTime = LocalTime.ofSecondOfDay(studyTimeSeconds);

        // 🟢 새로운 DeckStudyTimeEntity 저장
        DeckStudyTimeEntity studyTimeEntity = DeckStudyTimeEntity.builder()
                .deckEntity(deckEntity)  // ✅ DeckEntity 저장
                .studyTime(studyTime)    // ✅ LocalTime 변환 후 저장
                .date(LocalDateTime.now()) // ✅ 현재 시간 저장
                .build();

        deckStudyTimeRepository.save(studyTimeEntity);
    }
}
