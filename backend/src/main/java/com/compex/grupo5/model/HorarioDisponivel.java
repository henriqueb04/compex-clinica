package com.compex.grupo5.model;

import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "horarios_disponiveis")
public class HorarioDisponivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ano", nullable = false)
    private Integer ano;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 15)
    private DayOfWeek diaSemana;

    @Column(name = "numero_semana", nullable = false)
    private Integer numeroSemana;

    @Type(PostgreSQLRangeType.class)
    @Column(name = "intervalo_atendimento", columnDefinition = "tstzrange", nullable = false)
    private Range<ZonedDateTime> intervaloAtendimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_profissional", nullable = false)
    private Profissional profissional;
}