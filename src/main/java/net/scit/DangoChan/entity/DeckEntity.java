package net.scit.DangoChan.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.scit.DangoChan.dto.DeckDTO;

@Data
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

    @Column(nullable = false, length = 50)
    private String deckName;
    
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "categoryId", nullable = false)
    private CategoryEntity categoryEntity;  // deckEntity(N) -> categoryEntity(1)
    
    @OneToMany(mappedBy = "deckEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude	// DeckEntity와 CardEntity의 toString() 무한 호출문제 해결
    private List<CardEntity> cardEntityList;  // deckEntity(1) -> cardEntityList(N)
    
    
 // DTO --> Entity
    public static DeckEntity toEntity(DeckDTO deckDTO, CategoryEntity entity) {
		return DeckEntity.builder()
				.deckId(deckDTO.getDeckId())
				.deckName(deckDTO.getDeckName())
				.categoryEntity(entity)
				.build();
    }
}
