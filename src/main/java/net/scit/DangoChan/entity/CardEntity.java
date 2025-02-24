package net.scit.DangoChan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.DangoChan.dto.CardDTO;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Table(name="card")
public class CardEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long cardId;

//	@Column(name = "user_id", nullable = false, insertable = false, updatable = false)
//	private Long userId;
//	
//	@Column(name = "category_id", nullable = false, insertable = false, updatable = false)
//    private Long categoryId;
//
//	@Column(name = "deck_id", nullable = false, insertable = false, updatable = false)
//    private Long deckId;
//	
//    @ManyToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
//    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
//    @JoinColumn(name = "deck_id", referencedColumnName = "deck_id")
//    private DeckEntity deckEntity;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns({
  		@JoinColumn(name = "category_id", referencedColumnName = "category_id"),
  		@JoinColumn(name = "user_id", referencedColumnName = "user_id"),
		@JoinColumn(name = "deck_id", referencedColumnName = "deckId")		
})
  private DeckEntity deckEntity;
	
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
    private Integer studyLevel;
    
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
				.build();
	}
}
