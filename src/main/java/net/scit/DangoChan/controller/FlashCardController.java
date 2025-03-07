package net.scit.DangoChan.controller;

import java.io.IOException;
import java.util.List;

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
    public String updateCards(@RequestBody List<ExportCardDTO> cards) {
    	for (ExportCardDTO exportCardDTO : cards) {
			System.out.println(exportCardDTO.toString());
		}
        flashCardService.updateCards(cards);
        return "카드 목록이 성공적으로 수정되었습니다.";
    }
	
	//AYH end
		
	//PJB start
		
	//PJB end
	
	//deck end
	
	//card start
	
	//AYH start
	
	//AYH end
	
	//SYH start
	// ✅ 플래시카드 페이지 (HTML 렌더링)
	@GetMapping("/flashcard") // ❗ 변경: /flashcard/flashcard 로 맞춤
	public String flashcard(@RequestParam(name = "deckId", defaultValue = "1") Long deckId, Model model) {
//		if (deckId == null) {
//			deckId = 1L; // 기본 덱 ID 설정
//		}
		CardDTO card = flashCardService.getCardByDeckId(deckId);
		System.out.println(card.toString());
		model.addAttribute("flashcard", card);
		return "flashcard/flashcard"; // ✅ 템플릿: src/main/resources/templates/flashcard/flashcard.html
	}

	// ✅ AJAX 요청 (랜덤 단어 반환)
	// ✅ JSON 데이터 반환 (랜덤 단어 가져오기)
	@GetMapping(value = "/json", produces = "application/json")
	public ResponseEntity<CardDTO> getRandomFlashcard(@RequestParam(name = "deckId", defaultValue = "26") Long deckId) {
		if (deckId == null) {
			deckId = 26L; // 기본 덱 ID 설정
		}

		// ✅ DTO 변환된 데이터 가져오기
		CardDTO card = flashCardService.getCardByDeckId(deckId);
		// ✅ 디버깅 로그 추가 (JSON 응답 확인)
		System.out.println("🔥 [DEBUG] 응답 JSON: " + card);

		return ResponseEntity.ok(card);
	}

	// ✅ study_level 업데이트 API (AJAX 요청 처리)
	@PostMapping("/updateStudyLevel")
	public ResponseEntity<String> updateStudyLevel(@RequestParam Long cardId, @RequestParam Integer studyLevel) {
		flashCardService.updateStudyLevel(cardId, studyLevel);
		return ResponseEntity.ok("✅ study_level 업데이트 성공");
	}

	@PostMapping("/saveStudyTime")
	public ResponseEntity<String> saveStudyTime(@RequestParam(required = false) Long deckId,
												@RequestParam(required = false) Integer studyTime) {
		if (deckId == null || studyTime == null) {
			return ResponseEntity.badRequest().body("❌ deckId 또는 studyTime이 누락됨");
		}
		deckStudyTimeService.saveStudyTime(deckId, studyTime);
		return ResponseEntity.ok("✅ Study time 저장 완료");
	}

	//SYH end
	
	//card end
	
}
