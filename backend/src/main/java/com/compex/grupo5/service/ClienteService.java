package com.compex.grupo5.service;

import com.compex.grupo5.dao.ClienteRepository;
import com.compex.grupo5.model.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.compex.grupo5.exception.BusinessException;
import com.compex.grupo5.exception.ClienteNotFoundException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    /*
     * Retorna todos os clientes cadastrados no sistema.
     */
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    /*
     * Busca um cliente através do seu CPF.
     */
    public Cliente buscarPorCpf(String cpf) {
        validarCpf(cpf);

        return clienteRepository.findById(cpf)
                .orElseThrow(() ->
                        new ClienteNotFoundException("Cliente não encontrado para o CPF informado."));
    }

    /*
     * Cadastra um novo cliente.
     */
    public Cliente salvar(Cliente cliente) {
        validarCliente(cliente);

        String cpf = cliente.getCpf();

        if (clienteRepository.existsById(cpf)) {
            throw new BusinessException("Já existe um cliente cadastrado com o CPF informado.");
        }

        return clienteRepository.save(cliente);
    }

    /*
     * Atualiza os dados de um cliente existente.
     */
    public Cliente atualizar(String cpf, Cliente cliente) {
        validarCpf(cpf);
        validarCliente(cliente);

        Cliente clienteExistente = clienteRepository.findById(cpf)
                .orElseThrow(() ->
                        new ClienteNotFoundException("Cliente não encontrado para atualização."));
        if (!cpf.equals(cliente.getCpf())) {
            throw new IllegalArgumentException("O CPF do cliente não pode ser alterado.");
        }

        clienteExistente.setNomeCompleto(cliente.getNomeCompleto());
        clienteExistente.setDataNascimento(cliente.getDataNascimento());
        clienteExistente.setSexo(cliente.getSexo());
        clienteExistente.setEndereco(cliente.getEndereco());
        clienteExistente.setTelefone(cliente.getTelefone());

        return clienteRepository.save(clienteExistente);
    }

    /*
     * Exclui um cliente através do seu CPF.
     */
    public void excluir(String cpf) {
        validarCpf(cpf);

        if (!clienteRepository.existsById(cpf)) {
            throw new ClienteNotFoundException("Não é possível excluir: cliente não encontrado.");
        }

        clienteRepository.deleteById(cpf);
    }

    /*
     * Valida os dados obrigatórios para o cadastro de um cliente.
     */
    private void validarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo.");
        }

        validarCpf(cliente.getCpf());

        if (cliente.getNomeCompleto() == null ||
                cliente.getNomeCompleto().isBlank()) {
            throw new IllegalArgumentException(
                    "Nome completo é obrigatório."
            );
        }

        if (cliente.getDataNascimento() == null) {
            throw new IllegalArgumentException(
                    "Data de nascimento é obrigatória."
            );
        }

        if (cliente.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Data de nascimento não pode ser futura."
            );
        }

        if (cliente.getSexo() == null) {
            throw new IllegalArgumentException(
                    "Sexo é obrigatório."
            );
        }

        if (cliente.getEndereco() == null ||
                cliente.getEndereco().isBlank()) {
            throw new IllegalArgumentException(
                    "Endereço é obrigatório."
            );
        }

        if (cliente.getTelefone() == null ||
                cliente.getTelefone().isBlank()) {
            throw new IllegalArgumentException(
                    "Telefone é obrigatório."
            );
        }
    }

    /*
     * Valida o formato básico do CPF.
     */
    private void validarCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new IllegalArgumentException(
                    "CPF deve conter exatamente 11 dígitos numéricos."
            );
        }
    }
}