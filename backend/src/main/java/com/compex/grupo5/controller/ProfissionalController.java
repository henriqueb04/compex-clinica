package com.compex.grupo5.controller;

import com.compex.grupo5.dto.ProfissionalDto;
import com.compex.grupo5.model.Profissional;
import com.compex.grupo5.service.ProfissionalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profissionais")
@RequiredArgsConstructor
public class ProfissionalController {

    private final ProfissionalService profissionalService;

    /*
     * Cadastra um novo profissional.
     */
    @PostMapping
    public ResponseEntity<ProfissionalDto> salvar(
            @Valid @RequestBody ProfissionalDto profissionalDto) {

        Profissional profissional = profissionalDto.toEntity();

        Profissional profissionalSalvo =
                profissionalService.salvar(profissional);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ProfissionalDto.fromEntity(profissionalSalvo));
    }

    /*
     * Retorna todos os profissionais cadastrados.
     */
    @GetMapping
    public ResponseEntity<List<ProfissionalDto>> listarTodos() {

        List<ProfissionalDto> profissionais =
                profissionalService.listarTodos()
                        .stream()
                        .map(ProfissionalDto::fromEntity)
                        .toList();

        return ResponseEntity.ok(profissionais);
    }

    /*
     * Busca um profissional através do CPF.
     */
    @GetMapping("/{cpf}")
    public ResponseEntity<ProfissionalDto> buscarPorCpf(
            @PathVariable String cpf) {

        Profissional profissional =
                profissionalService.buscarPorCpf(cpf);

        return ResponseEntity.ok(
                ProfissionalDto.fromEntity(profissional)
        );
    }

    /*
     * Atualiza os dados de um profissional existente.
     */
    @PutMapping("/{cpf}")
    public ResponseEntity<ProfissionalDto> atualizar(
            @PathVariable String cpf,
            @Valid @RequestBody ProfissionalDto profissionalDto) {

        Profissional profissional =
                profissionalDto.toEntity();

        Profissional profissionalAtualizado =
                profissionalService.atualizar(
                        cpf,
                        profissional
                );

        return ResponseEntity.ok(
                ProfissionalDto.fromEntity(profissionalAtualizado)
        );
    }

    /*
     * Exclui um profissional através do CPF.
     */
    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> excluir(
            @PathVariable String cpf) {

        profissionalService.excluir(cpf);

        return ResponseEntity.noContent().build();
    }
}