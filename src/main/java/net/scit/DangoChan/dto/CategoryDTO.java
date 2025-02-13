package net.scit.DangoChan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class CategoryDTO {
	// variables
	private Long categoryId;
	private Long userId;
	private String categoryName;
	
	
	// Entity --> DTO
	
}
