package net.scit.DangoChan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import net.scit.DangoChan.dto.CardDTO;
import net.scit.DangoChan.dto.ExportCardDTO;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Data
@Table(name="card")
public class CardEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;
	
	@Column(name = "word", nullable = false)
    private String word;

    @Column(name = "pos")
    private String pos;

    @Column(name = "meaning")
    private String meaning;

    @Column(name = "example_jp")
    private String exampleJp;

    @Column(name = "example_kr")
    private String exampleKr;
    
    @Column(name = "study_level")
	@ColumnDefault("0")
	@Builder.Default
    private Integer studyLevel = 0;

	@Column(name = "studied_at")
	private LocalDate studiedAt;

	@ManyToOne
    @JoinColumn(name = "deck_id", referencedColumnName = "deckId", nullable = false)
    private DeckEntity deckEntity;  // cardEntity(N) -> deckEntity(1)
    
// DTO --> Entity
    public static CardEntity toEntity(CardDTO cardDTO, DeckEntity entity) {
		return CardEntity.builder()
				.cardId(cardDTO.getCardId())
				.deckEntity(entity)
				.word(cardDTO.getWord())
				.pos(cardDTO.getPos())				
				.meaning(cardDTO.getMeaning())				
				.exampleJp(cardDTO.getExampleJp())				
				.exampleKr(cardDTO.getExampleKr())				
				.studyLevel(cardDTO.getStudyLevel())
				.studiedAt(cardDTO.getStudiedAt())
				.build();
	}

	public static CardEntity toEntity(ExportCardDTO card, DeckEntity deckEntity2) {
		return CardEntity.builder()
				.cardId(card.getCardId())
				.deckEntity(deckEntity2)
				.word(card.getWord())
				.pos(card.getPos())				
				.meaning(card.getMeaning())				
				.exampleJp(card.getExampleJp())				
				.exampleKr(card.getExampleKr())				
				.studyLevel(card.getStudyLevel())				
				.build();
	}

}
