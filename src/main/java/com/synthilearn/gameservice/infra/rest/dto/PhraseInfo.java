package com.synthilearn.gameservice.infra.rest.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhraseInfo {
    private String answer;
    private Boolean correct;
    private UUID correctAnswerId;
    private String correctAnswerText;
    private Integer oldProgress;
    private Integer newProgress;
}
