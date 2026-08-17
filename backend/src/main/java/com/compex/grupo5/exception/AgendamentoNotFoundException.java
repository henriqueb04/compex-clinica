package com.compex.grupo5.exception;

public class AgendamentoNotFoundException extends RuntimeException {
    public AgendamentoNotFoundException(Long id) {
        super("Agendamento não encontrado para o ID: " + id);
    }
}
