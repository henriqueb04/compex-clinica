package com.compex.grupo5.controller;

import com.compex.grupo5.dto.HorarioDisponivelDto;
import com.compex.grupo5.model.HorarioDisponivel;
import com.compex.grupo5.service.HorarioDisponivelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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

    public record Salvar(
            @Valid List<HorarioDisponivelDto> horarios,
            List<Long> excluidos
    ) {}
    @PostMapping("/salvar")
    public ResponseEntity<List<HorarioDisponivelDto>> salvarHorarios(@RequestBody @Valid Salvar msg) {
        var horarios = horarioService.salvarHorarios(msg.horarios, msg.excluidos);
        return new ResponseEntity<>(horarios.stream().map(HorarioDisponivelDto::fromEntity).toList(), HttpStatus.CREATED);
    }

    public record PorProfissional(
            @NotNull @Positive Integer ano,
            @NotNull @Positive Integer numeroSemana,
            @NotNull @Pattern(regexp = "^\\d{11}$") String cpf
    ) {}
    @PostMapping("/profissional")
    public ResponseEntity<List<HorarioDisponivelDto>> horariosProfissional(@RequestBody @Valid PorProfissional porProfissional) {
        List<HorarioDisponivel> horarios = horarioService.horariosProfissionalEmSemana(porProfissional.cpf, porProfissional.ano, porProfissional.numeroSemana);
        return new ResponseEntity<>(horarios.stream().map(HorarioDisponivelDto::fromEntity).toList(), HttpStatus.ACCEPTED);
    }
}
