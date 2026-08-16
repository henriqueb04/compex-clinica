package com.compex.grupo5.dto;

import com.compex.grupo5.misc.StatusAgendamento;
import com.compex.grupo5.model.Agendamento;
import com.compex.grupo5.model.Cliente;
import com.compex.grupo5.model.Profissional;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * DTO for {@link com.compex.grupo5.model.Agendamento}
 */
public record AgendamentoDto(

        Long id,
        @NotNull @Pattern(regexp = "^\\d{11}$", message = "Formato de CPF inválido")
        String clienteCpf,
        @NotNull @NotBlank String clienteNome,
        @NotNull @Pattern(regexp = "^\\d{11}$", message = "Formato de CPF inválido")
        String profissionalCpf,
        @NotNull @NotBlank String profissionalNome,
        @NotNull ZonedDateTime comeco,
        @NotNull ZonedDateTime fim,
        @NotNull StatusAgendamento statusAgendamento

) implements Serializable {
    public Agendamento toEntity(Profissional profissional, Cliente cliente) {
        return new Agendamento(
                this.id,
                cliente,
                profissional,
                this.comeco.getYear(),
                this.comeco.get(WeekFields.of(Locale.US).weekOfYear()),
                Range.closed(this.comeco, this.fim),
                this.statusAgendamento
        );
    }
    public static AgendamentoDto fromEntity(Agendamento a) {
        return new AgendamentoDto(
                a.getId(),
                a.getCliente().getCpf(),
                a.getCliente().getNomeCompleto(),
                a.getProfissional().getCpf(),
                a.getProfissional().getNomeCompleto(),
                a.getIntervaloAtendimento().lower(),
                a.getIntervaloAtendimento().upper(),
                a.getStatusAgendamento()
        );
    }
}
