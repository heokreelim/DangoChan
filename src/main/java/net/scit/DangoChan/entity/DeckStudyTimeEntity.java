package net.scit.DangoChan.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.scit.DangoChan.dto.DeckStudyTimeDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "deck_study_time")
public class DeckStudyTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_time_id")
    private Long studyTimeId;

    @Column(name = "study_time")
    private LocalTime studyTime;  

    @Column(name = "date")
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "deck_id", nullable = false)
    private DeckEntity deckEntity;  // 어떤 Deck에서 학습했는지 저장

    // DTO -> Entity
    public static DeckStudyTimeEntity toEntity(DeckStudyTimeDTO dto, DeckEntity deckEntity) {
        return DeckStudyTimeEntity.builder()
                .studyTimeId(dto.getStudyTimeId())
                .deckEntity(deckEntity)
                //.deckId(dto.getDeckId())
                //.userId(dto.getUserId())
                .studyTime(dto.getStudyTime())
                .date(dto.getDate())
                .build();
    }
}
