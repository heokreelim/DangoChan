package net.scit.DangoChan.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

//DTO 묶음 클래스 (DeckDTO + List<CardDTO>)
@Getter
@Setter
public class DeckAndCardsRequest {
	private DeckDTO deckDTO;
    private List<CardDTO> cardDTOList;
}
