package net.scit.DangoChan.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.scit.DangoChan.entity.DeckEntity;
import net.scit.DangoChan.entity.DeckStudyTimeEntity;
import net.scit.DangoChan.repository.DeckRepository;
import net.scit.DangoChan.repository.DeckStudyTimeRepository;
import org.springframework.stereotype.Service;

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

        // 🟢 새로운 DeckStudyTimeEntity 저장
        DeckStudyTimeEntity studyTimeEntity = DeckStudyTimeEntity.builder()
                .deckEntity(deckEntity)  // ✅ DeckEntity 저장
                .studyTime(studyTimeSeconds)
                .date(LocalDateTime.now()) // ✅ 현재 시간 저장
                .build();

        deckStudyTimeRepository.save(studyTimeEntity);
    }
}
