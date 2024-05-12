package com.synthilearn.gameservice.infra.rest.dto;

import java.util.List;

import com.synthilearn.gameservice.domain.Game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllGamesResultDto {
    private List<Game> games;
    private int totalPages;
}
