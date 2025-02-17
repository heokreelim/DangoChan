package net.scit.DangoChan.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.CategoryDTO;
import net.scit.DangoChan.entity.CategoryEntity;
import net.scit.DangoChan.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlashCardService {

	

	//private final Repository variable start
	private final CategoryRepository categoryRepository;
	
	//private final Repository variable end
	
	//category start
	
		//AYH start
	
	/**
	 * insertCategory
	 * user가 입력한 카테고리의 이름을 저장하는 service
	 * @param categoryDTO
	 */
public void insertCategory(CategoryDTO categoryDTO) {
		
		CategoryEntity entity = CategoryEntity.toEntity(categoryDTO);
		log.info("카테고리 저장 {}", entity);
		
		categoryRepository.save(entity);
		
	}
	
		//AYH end
		
		//PJB start
		
		//PJB end
	
	//category end
	
	//deck start
	
		//AYH start
		
		//AYH end
			
		//PJB start
			
		//PJB end
	
	//deck end
	
	//card start
	
		//AYH start
		
		//AYH end
		
		//SYH start
	
		//SYH end
	
	//card end
	
}
