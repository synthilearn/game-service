package com.synthilearn.gameservice.infra.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranslateProgress {
    private String translate;
    private Integer oldProgress;
    private Integer newProgress;
}
