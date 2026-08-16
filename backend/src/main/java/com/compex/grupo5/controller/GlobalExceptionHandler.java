package com.compex.grupo5.controller;

import com.compex.grupo5.exception.AgendamentoNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.compex.grupo5.exception.BusinessException;
import com.compex.grupo5.exception.ClienteNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public record CamposInvalidos(Map<String, String> errors) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CamposInvalidos> excecoesValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (var error : ex.getBindingResult().getAllErrors()) {
            errors.put(
                    ((FieldError) error).getField(),
                    error.getDefaultMessage()
            );
        }
        return new ResponseEntity<>(new CamposInvalidos(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ClienteNotFoundException.class)
    public ResponseEntity<String> clienteNaoEncontrado(
            ClienteNotFoundException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> erroDeNegocio(
            BusinessException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }

    @ExceptionHandler(AgendamentoNotFoundException.class)
    public ResponseEntity<String> agendamentoNaoEncontrado(AgendamentoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> argumentoInvalido(
            IllegalArgumentException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
