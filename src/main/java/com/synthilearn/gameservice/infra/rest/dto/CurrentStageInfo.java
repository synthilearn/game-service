package com.synthilearn.gameservice.infra.rest.dto;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentStageInfo {

    private Integer stage;
    private ZonedDateTime endStageTime;
}
