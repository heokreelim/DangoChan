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

	private String deckName;									// 덱 이름
	private Integer deckCardCount;					// 덱의 모든 카드 수
	private Integer studiedCardCountOk;		// ○, 3
	private Integer studiedCardCountYet;		// △, 3
	private Integer studiedCardCountNo;		// ×, 1
	private Double cardStudyRate;						//  진행률 
	
	
	// Entity -> DTO
	public static DeckInfoDTO toDTO (
			String deckName,
			Integer deckCardCount,
			Integer studiedCardCountOk,
			Integer studiedCardCountYet,
			Integer studiedCardCountNo,
			Double cardStudyRate
			) {
		
		return DeckInfoDTO.builder()
				.deckName(deckName)
				.deckCardCount(deckCardCount)
				.studiedCardCountOk(studiedCardCountOk)
				.studiedCardCountYet(studiedCardCountYet)
				.studiedCardCountNo(studiedCardCountNo)
				.cardStudyRate(cardStudyRate)
				.build();
	}
	
}
