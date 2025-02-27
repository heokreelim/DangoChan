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
import net.scit.DangoChan.dto.CategoryDTO;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Table(name="category")
public class CategoryEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
	
	@Column(nullable = false, length = 50)
	private String categoryName;
    
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity userEntity;  // categoryEntity(N) -> userEntity(1)

    @OneToMany(mappedBy = "categoryEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude	// CategoryEntity와 DeckEntity의 toString() 무한 호출문제 해결
    private List<DeckEntity> deckEntityList;  // categoryEntity(1) -> deckEntityList(N)

    
    // DTO --> Entity
    public static CategoryEntity toEntity(CategoryDTO categoryDTO, UserEntity entity) {
		return CategoryEntity.builder()
				.categoryId(categoryDTO.getCategoryId())
				.categoryName(categoryDTO.getCategoryName())
				.userEntity(entity)
				.build();
	}

}
