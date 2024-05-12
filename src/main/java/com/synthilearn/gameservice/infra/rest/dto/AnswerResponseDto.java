package com.synthilearn.gameservice.infra.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerResponseDto {
    private Boolean isCorrect;
    private String rightTranslate;
}
