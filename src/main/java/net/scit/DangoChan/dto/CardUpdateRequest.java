package net.scit.DangoChan.dto;

import java.util.List;

import lombok.Data;

@Data
public class CardUpdateRequest {
    private List<ExportCardDTO> updatedCards;
    private List<Long> deletedCardIds;
}