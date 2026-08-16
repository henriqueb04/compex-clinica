package com.compex.grupo5.dto;

import com.compex.grupo5.misc.StatusAgendamento;
import com.compex.grupo5.model.Agendamento;

import java.io.Serializable;
import java.time.ZonedDateTime;

public record AgendamentoDto(

        Long id,
        String clienteCpf,
        String clienteNome,
        String profissionalCpf,
        String profissionalNome,
        ZonedDateTime inicio,
        ZonedDateTime fim,
        StatusAgendamento statusAgendamento

) implements Serializable {

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
