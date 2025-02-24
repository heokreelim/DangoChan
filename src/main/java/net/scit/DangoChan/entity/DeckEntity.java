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

//    @Column(name = "category_id", nullable = false, insertable = false, updatable = false)
//    private Long categoryId;
//
//    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
//    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    @JoinColumns({
    		@JoinColumn(name = "category_id", referencedColumnName = "categoryId"),
    		@JoinColumn(name = "user_id", referencedColumnName = "userId")})
    private CategoryEntity categoryEntity;


    @Column(nullable = false, length = 50)
    private String deckName;

    
 // DTO --> Entity
    public static DeckEntity toEntity(DeckDTO deckDTO, CategoryEntity entity) {
		return DeckEntity.builder()
				.deckId(deckDTO.getDeckId())
				.categoryEntity(entity)
				.deckName(deckDTO.getDeckName())
				.build();
    }
}
