package com.compex.grupo5.model;

import com.compex.grupo5.misc.StatusAgendamento;
import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Agendamento {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Medico medico;

    @Type(PostgreSQLRangeType.class)
    @Column(columnDefinition = "tstzrange", nullable = false)
    private Range<ZonedDateTime> intervaloAtendimento;

    @Enumerated(EnumType.STRING)
    @Column(length = 9, nullable = false)
    private StatusAgendamento statusAgendamento;
}