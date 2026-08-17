package com.compex.grupo5.dto;

import com.compex.grupo5.model.HorarioDisponivel;
import com.compex.grupo5.model.Profissional;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;

/**
 * DTO for {@link com.compex.grupo5.model.HorarioDisponivel}
 */
public record HorarioDisponivelDto(
        Long id,
        @NotNull Integer ano,
        @NotNull DayOfWeek diaSemana,
        @NotNull Integer numeroSemana,
        @NotNull ZonedDateTime comeco,
        @NotNull ZonedDateTime fim,
        @NotNull
        @Pattern(regexp = "^\\d{11}$", message = "Formato de CPF inválido")
        String profissional_cpf
) implements Serializable {
    public HorarioDisponivel toEntity(Profissional profissional) {
        return new HorarioDisponivel(
            this.id,
            this.comeco.get(IsoFields.WEEK_BASED_YEAR),
            this.comeco.getDayOfWeek(),
            this.comeco.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),
            Range.closedOpen(this.comeco, this.fim),
            profissional
        );
    }

    public static HorarioDisponivelDto fromEntity(HorarioDisponivel horario) {
        return new HorarioDisponivelDto(
                horario.getId(),
                horario.getAno(),
                horario.getDiaSemana(),
                horario.getNumeroSemana(),
                horario.getIntervaloAtendimento().lower(),
                horario.getIntervaloAtendimento().upper(),
                horario.getProfissional().getCpf()
        );
    }
}