package com.synthilearn.gameservice.infra.persistence.jpa.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.synthilearn.gameservice.domain.PartOfSpeech;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("translate_in_game")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TranslateInGameEntity implements Persistable<UUID> {

    @Id
    private UUID id;
    @Column("game_id")
    private UUID gameId;
    @Column("translate_id")
    private UUID translateId;
    @Column("translate_text")
    private String translateText;
    @Column("translate_part_of_speech")
    private PartOfSpeech transaltePartOfSpeech;
    @Column("old_progress")
    private Integer oldProgress;
    @Column("new_progress")
    private Integer newProgress;
    private Integer question;
    private Boolean correct;
    private Boolean answer;
    @Column("answer_time")
    private ZonedDateTime answerTime;

    @Transient
    private boolean newRecord;

    @Override
    public boolean isNew() {
        return this.newRecord;
    }
}
