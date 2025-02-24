package net.scit.DangoChan.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    
    @Column(name = "deck_id")
    private Long deckId;
   
    @Column(name = "user_id")
    private Long userId;
/*
    // deckId를 DeckEntity로 변경
    @ManyToOne
    @JoinColumn(name = "deck_id") // foreign key
    private DeckEntity deck; // DeckEntity로 연결

    // userId를 UserEntity로 변경
    @ManyToOne
    @JoinColumn(name = "user_id") // foreign key
    private UserEntity user; // UserEntity로 연결
*/
    @Column(name = "study_time")
    private LocalTime studyTime;  

    @Column(name = "date")
    private LocalDateTime date; 

    // DTO -> Entity 
    public static DeckStudyTimeEntity toEntity(DeckStudyTimeDTO dto, DeckEntity deck, UserEntity user) {
        return DeckStudyTimeEntity.builder()
                .studyTimeId(dto.getStudyTimeId())
                .deckId(dto.getDeckId())
                .userId(dto.getUserId())
                .studyTime(dto.getStudyTime())
                .date(dto.getDate())
                .build();
    }
}
