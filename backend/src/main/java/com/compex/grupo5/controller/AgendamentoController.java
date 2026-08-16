package com.compex.grupo5.controller;

import com.compex.grupo5.dto.AgendamentoDto;
import com.compex.grupo5.service.AgendamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    /*
     * Retorna os próximos agendamentos ativos, ordenados por data/hora crescente.
     */
    @GetMapping
    public ResponseEntity<List<AgendamentoDto>> listarProximos() {
        List<AgendamentoDto> lista = agendamentoService.listarProximos()
                .stream()
                .map(AgendamentoDto::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }
    /*
     * Retorna todos os agendamentos de um cliente pelo CPF.
     */
    @GetMapping("/cliente/{cpf}")
    public ResponseEntity<List<AgendamentoDto>> listarPorCliente(
            @PathVariable String cpf) {
        List<AgendamentoDto> lista = agendamentoService.listarPorCliente(cpf)
                .stream()
                .map(AgendamentoDto::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }

    /*
     * Retorna todos os agendamentos de um profissional pelo CPF.
     */
    @GetMapping("/profissional/{cpf}")
    public ResponseEntity<List<AgendamentoDto>> listarPorProfissional(
            @PathVariable String cpf) {
        List<AgendamentoDto> lista = agendamentoService.listarPorProfissional(cpf)
                .stream()
                .map(AgendamentoDto::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);

    }

    /*
     * Cancela um agendamento pelo ID.
     * Retorna o agendamento atualizado com o novo status.
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AgendamentoDto> cancelar(@PathVariable Long id) {
        AgendamentoDto dto = AgendamentoDto.fromEntity(
                agendamentoService.cancelar(id)
        );
        return ResponseEntity.ok(dto);
    }
}
