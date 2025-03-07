package net.scit.DangoChan.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import net.scit.DangoChan.entity.CardEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.scit.DangoChan.dto.CardDTO;
import net.scit.DangoChan.dto.CardUpdateRequest;
import net.scit.DangoChan.dto.CategoryDTO;
import net.scit.DangoChan.dto.DeckAndCardsRequest;
import net.scit.DangoChan.dto.DeckDTO;
import net.scit.DangoChan.dto.DeckResponseDTO;
import net.scit.DangoChan.dto.ExportCardDTO;
import net.scit.DangoChan.service.DeckStudyTimeService;
import net.scit.DangoChan.service.FlashCardService;

@Controller
@RequestMapping("/flashcard")
@RequiredArgsConstructor
@Slf4j
public class FlashCardController {
	
	//private final Service variable start
	private final FlashCardService flashCardService;
	private final DeckStudyTimeService deckStudyTimeService;
	//private final Service variable end
	
	//category start
	
	//AYH start
	
	@GetMapping("/modal")
	public String modal()
	{
		return "modal"; 
	}
	/**
	 *	카테고리 등록 요청
	 * @param categoryDTO
	 * @return
	 */
	@PostMapping("/insertCategory")
	public String insertCategory(@ModelAttribute CategoryDTO categoryDTO) {

//		DB 등록
		System.out.println(categoryDTO.toString());
		flashCardService.insertCategory(categoryDTO);

		log.info("카테고리가 추가되었습니다.");
		return "redirect:/home";
	}

	/**
	 *	카테고리 수정 요청
	 * @param categoryDTO
	 * @return
	 */
	@GetMapping("/updateCategory")
	public String updateCategory(@ModelAttribute CategoryDTO categoryDTO) {
		flashCardService.updateCategory(categoryDTO);
		
		return "redirect:/home";
	}


	
	//AYH end
	
	//PJB start
	// 카테고리 삭제 요청
	@GetMapping("/deleteCategory")
	public String deleteCategory (
			@RequestParam(name = "categoryId") Long categoryId
			) {
		flashCardService.deleteCategory(categoryId);
		
		return "redirect:/home";
	}
	
	//PJB end
	
	//category end
	
	//deck start
	
	//AYH start
	/**
	 * 덱을 저장하면서 함께 입력한 카드도 함께 저장되는 코드
	 * @param request
	 * @return
	 */
	@ResponseBody
	@PostMapping("/importDeck")
    public String importDeck(@RequestBody DeckAndCardsRequest request) {
        DeckDTO deckDTO = request.getDeckDTO();
        List<CardDTO> cardDTOList = request.getCardDTOList();

        System.out.println("▶ Deck 저장: " + deckDTO);
        DeckDTO savedDeckId = flashCardService.insertDeck(deckDTO);
        for (CardDTO cardDTO : cardDTOList) {
        	cardDTO.setDeckId(savedDeckId.getDeckId()); // 백엔드에서 저장 후 ID 업데이트 필요
            System.out.println("▶ 카드 저장: " + cardDTO);
            flashCardService.insertCard(cardDTO);
        }
     // TODO: DB 저장 로직 추가 (Service & Repository 호출)
        return "Deck and Cards saved successfully!";
	}
	
	
	// 덱 내보내기 요청
	/**
	 * 덱 내보내기 요청 
	 * 요청받은 카드 DB를 xlsx파일로 변환하여 저장
	 * @param deckId
	 * @param response
	 * @throws IOException
	 */
	@GetMapping("/exportDeck")
	public void exportDeckToExcel(@RequestParam(name = "deckId") Long deckId, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=deck_" + deckId + ".xlsx");

        List<ExportCardDTO> cardList = flashCardService.getCardsByDeckId(deckId);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Flashcards");

        // 헤더 작성
        Row headerRow = sheet.createRow(0);
        String[] columns = {"단어", "품사", "뜻", "예문 (일본어)", "예문 (한국어)"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        // 데이터 입력
        int rowNum = 1;
        for (ExportCardDTO card : cardList) {
        	System.out.println(card.toString());
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(card.getWord());
            row.createCell(1).setCellValue(card.getPos());
            row.createCell(2).setCellValue(card.getMeaning());
            row.createCell(3).setCellValue(card.getExampleJp());
            row.createCell(4).setCellValue(card.getExampleKr());
        }

        // 자동 열 너비 조정
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
	
	// 덱 편집 요청
	@ResponseBody
	@GetMapping("/getDeck")
    public DeckResponseDTO getDeck(@RequestParam(name="deckId") Long deckId) {
        DeckDTO deck = flashCardService.getDeckByDeckId(deckId);
        List<ExportCardDTO> cardList = flashCardService.getCardsByDeckId(deckId);
        return new DeckResponseDTO(deck, cardList);
    }

	// 덱 편집 내역 저장
	@ResponseBody
	@PutMapping("/updateDeck")
    public String updateDeck(@RequestBody DeckDTO deckDTO) {
		System.out.println(deckDTO.toString());
		flashCardService.updateDeck(deckDTO);
        return "덱 정보가 성공적으로 수정되었습니다.";
    }

	// 카드 편집
	// 덱 편집 안의 단어 수정 내역 저장
	@ResponseBody
    @PutMapping("/updateCards")
    public String updateCards(@RequestBody CardUpdateRequest request) {
		List<ExportCardDTO> updatedCards = request.getUpdatedCards();
	    List<Long> deletedCardIds = request.getDeletedCardIds();
	    
	 // 수정된 카드 출력 (디버깅용)
	    for (ExportCardDTO exportCardDTO : updatedCards) {
	        System.out.println("수정된 카드: " + exportCardDTO.toString());
	    }

	    // 삭제할 카드 ID 출력 (디버깅용)
	    if (deletedCardIds != null && !deletedCardIds.isEmpty()) {
	        System.out.println("삭제할 카드 ID 목록: " + deletedCardIds);
	        flashCardService.deleteCard(deletedCardIds);
	    }

	    flashCardService.updateCards(updatedCards);
	    return "카드 목록이 성공적으로 수정되었습니다.";
	    
    }
	
	//AYH end
		
	//PJB start
		
	// 덱 삭제 요청
		@GetMapping("/deleteDeck")
		public String deleteDeck (
				@RequestParam(name = "deckId") Long deckId
				) {
			flashCardService.deleteDeck(deckId);
			
			return "redirect:/home";
		}
	//PJB end
	
	//deck end
	
	//card start
	
	//AYH start
	
	//AYH end
	
	//SYH start
	// ✅ 플래시카드 페이지
	@GetMapping("/flashcard")
	public String flashcard(@RequestParam(name = "deckId") Long deckId, Model model) {

		Optional<CardDTO> card = flashCardService.getRandomNewCard(deckId);	//랜덤 카드 가져오기

		System.out.println("📌 [DEBUG] 가져온 카드: " + card); // ✅ 카드가 null인지 확인

		if (card.isEmpty()) {
			model.addAttribute("flashcard", null);
		} else {
			model.addAttribute("flashcard", card.get());
		}

		model.addAttribute("deckId", deckId);
		return "flashcard/flashcard";
	}

	// ✅ AJAX 요청 (랜덤 단어 반환)
	// ✅ JSON 데이터 반환 (랜덤 단어 가져오기)
	@GetMapping(value = "/json", produces = "application/json")
	public ResponseEntity<CardDTO> getRandomFlashcard(@RequestParam(name = "deckId") Long deckId) {

		// ✅ DTO 변환된 데이터 가져오기
		Optional<CardDTO> card = flashCardService.getRandomNewCard(deckId);
		// ✅ 디버깅 로그 추가 (JSON 응답 확인)
		System.out.println("🔥 [DEBUG] 응답 JSON: " + card);

		return ResponseEntity.ok(card.get());
	}

	@PostMapping("/resetStudyData")
	public ResponseEntity<String> resetStudyData(@RequestParam Long deckId) {
		boolean allStudied = flashCardService.isAllCardsStudied(deckId);

		if (allStudied) {
			flashCardService.resetStudyData(deckId);
			return ResponseEntity.ok("✅ 모든 단어 학습 완료! 스터디 데이터 초기화됨.");
		} else {
			return ResponseEntity.ok("📌 아직 학습이 완료되지 않음.");
		}
	}

	// ✅ study_level 업데이트 API (AJAX 요청 처리)
	@PostMapping("/updateStudyLevel")
	public ResponseEntity<String> updateStudyLevel(@RequestParam Long cardId, @RequestParam Integer studyLevel) {

		flashCardService.updateStudyLevel(cardId, studyLevel);
		return ResponseEntity.ok("✅ study_level 업데이트 성공");
	}

	@PostMapping("/saveStudyTime")
	public ResponseEntity<String> saveStudyTime(@RequestParam(name = "deckId") Long deckId,
												@RequestParam Integer studyTime) {
		if (deckId == null || studyTime == null) {
			return ResponseEntity.badRequest().body("❌ deckId 또는 studyTime이 누락됨");
		}
		deckStudyTimeService.saveStudyTime(deckId, studyTime);
		return ResponseEntity.ok("✅ Study time 저장 완료");
	}

	//SYH end
	
	//card end
	
}
