package com.synthilearn.gameservice.infra.persistence.jpa.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.synthilearn.gameservice.domain.GameResult;
import com.synthilearn.gameservice.domain.GameStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table("game")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GameEntity implements Persistable<UUID> {

    @Id
    private UUID id;
    @Column("workarea_id")
    private UUID workareaId;
    @Column("creation_date")
    private LocalDateTime creationDate;
    private GameResult result;
    private GameStatus status;

    @Transient
    private boolean newRecord;

    @Override
    public boolean isNew() {
        return this.newRecord;
    }
}
