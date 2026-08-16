package com.compex.grupo5.controller;

import com.compex.grupo5.dto.AgendamentoDto;
import com.compex.grupo5.service.AgendamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
