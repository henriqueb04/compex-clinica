package com.compex.grupo5.controller;

import com.compex.grupo5.dto.HorarioDisponivelDto;
import com.compex.grupo5.model.HorarioDisponivel;
import com.compex.grupo5.service.HorarioDisponivelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/horario")
public class HorarioDisponivelController {
    private final HorarioDisponivelService horarioService;

    @PostMapping("/salvar")
    public ResponseEntity<List<HorarioDisponivelDto>> salvarHorarios(@RequestBody @Valid Salvar msg) {
        var horarios = horarioService.salvarHorarios(msg.horarios, msg.excluidos);
        return new ResponseEntity<>(
                horarios.stream().map(HorarioDisponivelDto::fromEntity).toList(),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/profissional/{cpf}")
    public ResponseEntity<List<HorarioDisponivelDto>> horariosProfissional(
            @PathVariable @Pattern(regexp = "^\\d{11}$") String cpf,
            @RequestParam @NotNull Integer ano,
            @RequestParam @NotNull Integer numeroSemana
    ) {
        List<HorarioDisponivel> horarios = horarioService.horariosProfissionalEmSemana(cpf, ano, numeroSemana);
        return new ResponseEntity<>(
                horarios.stream().map(HorarioDisponivelDto::fromEntity).toList(),
                HttpStatus.ACCEPTED
        );
    }

    public record Salvar(
            @Valid List<HorarioDisponivelDto> horarios,
            List<Long> excluidos
    ) {
    }
}
