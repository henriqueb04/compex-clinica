package com.compex.grupo5.controller;

import com.compex.grupo5.dto.ClienteDto;
import com.compex.grupo5.model.Cliente;
import com.compex.grupo5.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    /*
     * Cadastra um novo cliente.
     */
    @PostMapping
    public ResponseEntity<ClienteDto> salvar(
            @Valid @RequestBody ClienteDto clienteDto) {

        Cliente cliente = clienteDto.toEntity();

        Cliente clienteSalvo = clienteService.salvar(cliente);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ClienteDto.fromEntity(clienteSalvo));
    }

    /*
     * Retorna todos os clientes cadastrados.
     */
    @GetMapping
    public ResponseEntity<List<ClienteDto>> listarTodos() {

        List<ClienteDto> clientes = clienteService.listarTodos()
                .stream()
                .map(ClienteDto::fromEntity)
                .toList();

        return ResponseEntity.ok(clientes);
    }

    /*
     * Busca um cliente através do CPF.
     */
    @GetMapping("/{cpf}")
    public ResponseEntity<ClienteDto> buscarPorCpf(
            @PathVariable String cpf) {

        Cliente cliente = clienteService.buscarPorCpf(cpf);

        return ResponseEntity.ok(
                ClienteDto.fromEntity(cliente)
        );
    }

    /*
     * Atualiza os dados de um cliente existente.
     */
    @PutMapping("/{cpf}")
    public ResponseEntity<ClienteDto> atualizar(
            @PathVariable String cpf,
            @Valid @RequestBody ClienteDto clienteDto) {

        Cliente cliente = clienteDto.toEntity();

        Cliente clienteAtualizado =
                clienteService.atualizar(cpf, cliente);

        return ResponseEntity.ok(
                ClienteDto.fromEntity(clienteAtualizado)
        );
    }

    /*
     * Exclui um cliente através do CPF.
     */
    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> excluir(
            @PathVariable String cpf) {

        clienteService.excluir(cpf);

        return ResponseEntity.noContent().build();
    }
}

