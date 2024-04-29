package com.synthilearn.gameservice.infra.persistence.jpa.entity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("game_statistic")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameStatisticEntity implements Persistable<UUID> {

    @Id
    private UUID id;
    @Column("translates_in_game")
    private List<UUID> translatesInGame;
    @Column("correct_translates")
    private Integer correctTranslates;
    @Column("incorrect_translates")
    private Integer incorrectTranslates;
    @Column("translates_lack_time")
    private Integer translatesLackTime;
    @Column("game_current_time")
    private Integer gameCurrentTime;
    @Column("game_full_time")
    private Integer gameFullTime;

    @Transient
    private boolean newRecord;

    @Override
    public boolean isNew() {
        return this.newRecord;
    }
}
