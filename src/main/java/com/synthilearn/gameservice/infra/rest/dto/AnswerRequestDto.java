package com.synthilearn.gameservice.infra.rest.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerRequestDto {
    @NotBlank
    private String translate;
    @NotNull
    private Integer stage;
    @NotNull
    private UUID gameId;
    @NotNull
    private UUID workareaId;
}
