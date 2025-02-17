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
import net.scit.DangoChan.dto.DeckDTO;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Table(name="deck")
public class DeckEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deckId;

    @Column(name = "category_id", nullable = false, insertable = false, updatable = false)
    private Long categoryId;

    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Long userId;

//    @ManyToOne
//    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
//    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
//    private CategoryEntity categoryEntity;


    @Column(nullable = false, length = 50)
    private String deckName;

    
 // DTO --> Entity
    public static DeckEntity toEntity(DeckDTO deckDTO) {
		return DeckEntity.builder()
				.deckId(deckDTO.getDeckId())
				//	임시값 : 1
				.categoryId(1L)
				//	임시값 : 1
				.userId(1L)
				.deckName(deckDTO.getDeckName())
				.build();
    }
}
