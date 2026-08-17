package com.compex.grupo5.model;

import com.compex.grupo5.misc.StatusAgendamento;
import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private Profissional profissional;

    @Column(name = "ano", nullable = false)
    private Integer ano;

    @Column(name = "numero_semana", nullable = false)
    private Integer numeroSemana;

    @Type(PostgreSQLRangeType.class)
    @Column(columnDefinition = "tstzrange", nullable = false)
    private Range<ZonedDateTime> intervaloAtendimento;

    @Enumerated(EnumType.STRING)
    @Column(length = 9, nullable = false)
    private StatusAgendamento statusAgendamento;
}