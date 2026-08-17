package com.compex.grupo5.exception;

import java.time.ZonedDateTime;

public class AgendamentoOutOfBounds extends RuntimeException {
    public AgendamentoOutOfBounds(String cpfProfissional, ZonedDateTime comeco, ZonedDateTime fim) {
        super(String.format(
                "%s - %s não estão em nenhum horário de atendimento do médico de CPF %s",
                comeco,
                fim,
                cpfProfissional
        ));
    }
}
