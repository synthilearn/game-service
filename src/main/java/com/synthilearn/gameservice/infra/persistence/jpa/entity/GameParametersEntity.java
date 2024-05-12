package com.synthilearn.gameservice.infra.persistence.jpa.entity;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.synthilearn.gameservice.domain.TypeOfGame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("game_parameters")
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameParametersEntity implements Persistable<UUID> {
    @Id
    private UUID id;
    @Column("translates_amount")
    private Integer translatesAmount;
    @Column("available_part_of_speech")
    private String partsOfSpeech;
    @Column("phrase_types")
    private String phraseTypes;
    @Column("date_from")
    private LocalDate dateFrom;
    @Column("date_to")
    private LocalDate dateTo;
    @Column("time_on_word")
    private Integer timeOnWord;
    @Column("type_of_game")
    private TypeOfGame typeOfGame;

    @Transient
    private boolean newRecord;

    @Override
    public boolean isNew() {
        return this.newRecord;
    }
}
