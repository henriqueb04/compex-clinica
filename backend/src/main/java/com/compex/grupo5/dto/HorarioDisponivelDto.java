package com.compex.grupo5.dto;

import com.compex.grupo5.dao.ProfissionalRepository;
import com.compex.grupo5.model.HorarioDisponivel;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;

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
    public HorarioDisponivel toEntity(ProfissionalRepository profissionalRepository) {
        return new HorarioDisponivel(
            this.id,
            this.ano,
            this.diaSemana,
            this.numeroSemana,
            Range.closed(this.comeco, this.fim),
            profissionalRepository.findByCpfIs(this.profissional_cpf)
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