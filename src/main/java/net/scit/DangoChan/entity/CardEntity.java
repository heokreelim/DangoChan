package net.scit.DangoChan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

	@Column(name = "user_id", nullable = false, insertable = false, updatable = false)
	private Long userId;
	
	@Column(name = "category_id", nullable = false, insertable = false, updatable = false)
    private Long categoryId;

	@Column(name = "deck_id", nullable = false, insertable = false, updatable = false)
    private Long deckId;
	
//    @ManyToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
//    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
//    @JoinColumn(name = "deck_id", referencedColumnName = "deck_id")
//    private DeckEntity deckEntity;

    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "furigana")
    private String furigana;

    @Column(name = "pos")
    private String pos;

    @Column(name = "meaning")
    private String meaning;

    @Column(name = "example")
    private String example;

    @Column(name = "study_level")
    private Integer studyLevel;
    
// DTO --> Entity

}
