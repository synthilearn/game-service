package com.synthilearn.gameservice.infra.rest.dto;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllGamesRequest {
    @NotNull
    private UUID workareaId;
    @NotNull
    @Min(0)
    private Integer page;
    @NotNull
    @Min(1)
    private Integer size;
}
