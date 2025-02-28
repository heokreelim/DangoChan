package net.scit.DangoChan.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.scit.DangoChan.entity.DeckEntity;
import net.scit.DangoChan.entity.DeckStudyTimeEntity;
import net.scit.DangoChan.entity.UserEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DeckStudyTimeDTO {

    private Long studyTimeId;
    private Long deckId;
    private int studyTime;
    private LocalDateTime date;

    
    // Entity -> DTO
    public static DeckStudyTimeDTO toDTO(DeckStudyTimeEntity entity) {
    	
    	return DeckStudyTimeDTO.builder()
    					.studyTimeId(entity.getStudyTimeId())
    					.deckId(entity.getDeckEntity().getDeckId())
    					.studyTime(entity.getStudyTime())
    					.date(entity.getDate())
    					.build();
    }
}