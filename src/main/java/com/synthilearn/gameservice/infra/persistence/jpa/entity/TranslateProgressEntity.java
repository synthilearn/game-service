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

@Table("translate_progress")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TranslateProgressEntity implements Persistable<UUID> {

    @Id
    private UUID id;
    @Column("game_id")
    private UUID gameId;
    @Column("old_progress")
    private Integer oldProgress;
    @Column("new_progress")
    private Integer newProgress;
    @Column("translate_id")
    private UUID translateId;
    @Column("translate_text")
    private String translateText;
    @Column("true_answer")
    private String trueAnswer;
    private Boolean correct;

    @Transient
    private boolean newRecord;

    @Override
    public boolean isNew() {
        return this.newRecord;
    }
}
