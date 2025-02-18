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
import net.scit.DangoChan.dto.CategoryDTO;

@Entity
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

//	@Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    @Column
	private Long userId;

//    @ManyToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
//    private UserEntity userEntity;

    @Column(nullable = false, length = 50)
    private String categoryName;

	
    
    // DTO --> Entity
    public static CategoryEntity toEntity(CategoryDTO categoryDTO) {
		return CategoryEntity.builder()
				.categoryId(categoryDTO.getCategoryId())
//				임시값
				.userId(1L)
				.categoryName(categoryDTO.getCategoryName())
				.build();
	}

}
