package com.synthilearn.gameservice.infra.adapter;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synthilearn.commonstarter.GenericResponse;
import com.synthilearn.gameservice.app.config.properties.WebClientProperties;
import com.synthilearn.gameservice.domain.Phrase;
import com.synthilearn.gameservice.infra.adapter.dto.AllPhraseRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class DictionaryClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final WebClientProperties webClientProperties;

    public Mono<List<Phrase>> getPhrases(AllPhraseRequestDto requestDto) {
        return webClient.post()
                .uri(webClientProperties.getDictionaryHost() + "/phrase/all")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestDto), AllPhraseRequestDto.class)
                .retrieve()
                .bodyToMono(String.class)
                .map(jsonString -> {
                    try {
                        JsonNode phrases =
                                objectMapper.readTree(jsonString).get("resultData").get("phrases");
                        List<Phrase> response = objectMapper.readValue(phrases.toString(),
                                new TypeReference<>() {
                                });
                        return response;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onErrorResume(error -> {
                    log.error("Error has occurred while save data for customer: {}, exception: {}",
                            requestDto, error);
                    GenericResponse<?> response =
                            ((WebClientResponseException) error).getResponseBodyAs(
                                    GenericResponse.class);
                    throw new RuntimeException(response.getMessage());
                });
    }
}
