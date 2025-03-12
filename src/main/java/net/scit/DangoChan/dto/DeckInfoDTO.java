package net.scit.DangoChan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

@Builder
public class DeckInfoDTO {
	
	private Long deckId;
	private String deckName;									// 덱 이름
	private Integer deckCardCount;					// 덱의 모든 카드 수
	private Integer studiedCardCountOk;		// ○, 3
	private Integer studiedCardCountYet;		// △, 3
	private Integer studiedCardCountNo;		// ×, 1
	private Integer newCard;									// ？, 0
	private Double cardStudyRate;						//  진행률 
	
	
	// Entity -> DTO
	public static DeckInfoDTO toDTO (
			Long deckId,
			String deckName,
			Integer deckCardCount,
			Integer studiedCardCountOk,
			Integer studiedCardCountYet,
			Integer studiedCardCountNo,
			Integer newCard,
			Double cardStudyRate
			) {
		
		return DeckInfoDTO.builder()
				.deckId(deckId)
				.deckName(deckName)
				.deckCardCount(deckCardCount)
				.studiedCardCountOk(studiedCardCountOk)
				.studiedCardCountYet(studiedCardCountYet)
				.studiedCardCountNo(studiedCardCountNo)
				.newCard(newCard)
				.cardStudyRate(cardStudyRate)
				.build();
	}
	
}
