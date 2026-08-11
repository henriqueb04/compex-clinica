package com.compex.grupo5.service;

import com.compex.grupo5.dao.ClienteRepository;
import com.compex.grupo5.model.Cliente;
import com.compex.grupo5.misc.Sexo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.compex.grupo5.exception.BusinessException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        clienteService = new ClienteService(clienteRepository);
    }

    /**
     * Testa o cadastro de um cliente com dados válidos.
     *
     * Deve verificar se o Service consulta a existência do CPF
     * e, caso não exista, realiza o salvamento através do Repository.
     */
    @Test
    void deveSalvarClienteComDadosValidos() {
        Cliente cliente = criarClienteValido();

        when(clienteRepository.existsById(cliente.getCpf()))
                .thenReturn(false);

        when(clienteRepository.save(cliente))
                .thenReturn(cliente);

        Cliente resultado = clienteService.salvar(cliente);

        assertNotNull(resultado);
        assertEquals(cliente.getCpf(), resultado.getCpf());

        verify(clienteRepository).existsById(cliente.getCpf());
        verify(clienteRepository).save(cliente);
    }

    /**
     * Testa a regra que impede o cadastro de dois clientes
     * utilizando o mesmo CPF.
     */
    @Test
    void naoDeveCadastrarClienteComCpfExistente() {
        Cliente cliente = criarClienteValido();

        when(clienteRepository.existsById(cliente.getCpf()))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> clienteService.salvar(cliente)
        );

        verify(clienteRepository).existsById(cliente.getCpf());
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    /**
     * Testa a validação que impede o cadastro de um cliente
     * com CPF inválido.
     */
    @Test
    void naoDeveCadastrarClienteComCpfInvalido() {
        Cliente cliente = criarClienteValido();
        cliente.setCpf("123");

        assertThrows(
                IllegalArgumentException.class,
                () -> clienteService.salvar(cliente)
        );

        verifyNoInteractions(clienteRepository);
    }

    /**
     * Testa a validação que impede o cadastro de um cliente
     * com data de nascimento futura.
     */
    @Test
    void naoDeveCadastrarClienteComDataNascimentoFutura() {
        Cliente cliente = criarClienteValido();
        cliente.setDataNascimento(LocalDate.now().plusDays(1));

        assertThrows(
                IllegalArgumentException.class,
                () -> clienteService.salvar(cliente)
        );

        verifyNoInteractions(clienteRepository);
    }

    /**
     * Testa a busca de um cliente existente através do CPF.
     */
    @Test
    void deveBuscarClientePorCpf() {
        Cliente cliente = criarClienteValido();

        when(clienteRepository.findById(cliente.getCpf()))
                .thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.buscarPorCpf(cliente.getCpf());

        assertNotNull(resultado);
        assertEquals(cliente.getCpf(), resultado.getCpf());

        verify(clienteRepository).findById(cliente.getCpf());
    }

    /**
     * Testa a tentativa de buscar um cliente que não existe.
     */
    @Test
    void deveLancarExcecaoAoBuscarClienteInexistente() {
        String cpf = "12345678901";

        when(clienteRepository.findById(cpf))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> clienteService.buscarPorCpf(cpf)
        );

        verify(clienteRepository).findById(cpf);
    }

    /**
     * Testa a atualização dos dados de um cliente existente.
     */
    @Test
    void deveAtualizarCliente() {
        Cliente clienteExistente = criarClienteValido();

        Cliente dadosAtualizados = criarClienteValido();
        dadosAtualizados.setNomeCompleto("Maria da Silva");

        when(clienteRepository.findById(clienteExistente.getCpf()))
                .thenReturn(Optional.of(clienteExistente));

        when(clienteRepository.save(clienteExistente))
                .thenReturn(clienteExistente);

        Cliente resultado = clienteService.atualizar(
                clienteExistente.getCpf(),
                dadosAtualizados
        );

        assertNotNull(resultado);
        assertEquals("Maria da Silva", resultado.getNomeCompleto());

        verify(clienteRepository).findById(clienteExistente.getCpf());
        verify(clienteRepository).save(clienteExistente);
    }

    /**
     * Testa a regra que impede a alteração do CPF durante
     * a atualização de um cliente.
     */
    @Test
    void naoDevePermitirAlteracaoDoCpf() {
        Cliente clienteExistente = criarClienteValido();

        Cliente dadosAtualizados = criarClienteValido();
        dadosAtualizados.setCpf("98765432100");

        when(clienteRepository.findById(clienteExistente.getCpf()))
                .thenReturn(Optional.of(clienteExistente));

        assertThrows(
                IllegalArgumentException.class,
                () -> clienteService.atualizar(
                        clienteExistente.getCpf(),
                        dadosAtualizados
                )
        );

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    /**
     * Testa a exclusão de um cliente existente.
     */
    @Test
    void deveExcluirCliente() {
        String cpf = "12345678901";

        when(clienteRepository.existsById(cpf))
                .thenReturn(true);

        clienteService.excluir(cpf);

        verify(clienteRepository).existsById(cpf);
        verify(clienteRepository).deleteById(cpf);
    }

    /**
     * Testa a regra que impede a exclusão de um cliente
     * que não existe no banco de dados.
     */
    @Test
    void naoDeveExcluirClienteInexistente() {
        String cpf = "12345678901";

        when(clienteRepository.existsById(cpf))
                .thenReturn(false);

        assertThrows(
                RuntimeException.class,
                () -> clienteService.excluir(cpf)
        );

        verify(clienteRepository).existsById(cpf);
        verify(clienteRepository, never()).deleteById(cpf);
    }

    /**
     * Cria um cliente com todos os dados necessários para os testes.
     *
     * @return cliente válido para utilização nos cenários de teste.
     */
    private Cliente criarClienteValido() {
        Cliente cliente = new Cliente();

        cliente.setCpf("12345678901");
        cliente.setNomeCompleto("João da Silva");
        cliente.setDataNascimento(LocalDate.of(2000, 1, 10));
        cliente.setSexo(Sexo.MASCULINO);
        cliente.setEndereco("Rua Exemplo, 100");
        cliente.setTelefone("86999999999");

        return cliente;
    }
}