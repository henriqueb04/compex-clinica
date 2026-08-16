package com.compex.grupo5.exception;

import java.time.ZonedDateTime;

public class TimeRangeConflictException extends RuntimeException {
    public TimeRangeConflictException(String profissional, ZonedDateTime comeco, ZonedDateTime fim) {
        super(String.format("Conflito ao adicionar horário (%s - %s) para o profissional de CPF %s", comeco, fim, profissional));
    }
}
