package com.compex.grupo5.controller;

import com.compex.grupo5.dto.AgendamentoDto;
import com.compex.grupo5.service.AgendamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendamento")
@RequiredArgsConstructor
public class AgendamentoController {
    private final AgendamentoService agendamentoService;

    @PostMapping("/profissional")
    public ResponseEntity<List<AgendamentoDto>> getAgendamentos(@RequestBody @Valid Listar listar) {
        return new ResponseEntity<>(
                agendamentoService.agendamentosProfissionalEmSemana(listar.cpfProfissional, listar.ano,
                        listar.numeroSemana),
                HttpStatus.OK
        );
    }

    @PostMapping("/marcar")
    public ResponseEntity<AgendamentoDto> marcarAgendamento(@RequestBody @Valid AgendamentoDto agendamentoDto) {
        return new ResponseEntity<>(
                AgendamentoDto.fromEntity(agendamentoService.salvarAgendamento(agendamentoDto)),
                HttpStatus.CREATED
        );
    }

//    @GetMapping("/deletar/{id}")
//    public ResponseEntity<AgendamentoDto> deletarAgendamento(@PathVariable int id) {
//        return new ResponseEntity<>(
//                AgendamentoDto.fromEntity(agendamentoService.salvarAgendamento(agendamentoDto)),
//                HttpStatus.CREATED
//        );
//    }

    public record Listar(
            @NotNull @Pattern(regexp = "^\\d{11}$", message = "Formato de CPF inválido")
            String cpfProfissional,
            @NotNull Integer ano,
            @NotNull Integer numeroSemana
    ) {
    }
}
