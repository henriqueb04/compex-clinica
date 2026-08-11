package com.compex.grupo5.service;

import com.compex.grupo5.dao.ProfissionalRepository;
import com.compex.grupo5.model.Profissional;
import com.compex.grupo5.misc.Especialidade;
import com.compex.grupo5.misc.Sexo;
import com.compex.grupo5.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfissionalServiceTest {

    @Mock
    private ProfissionalRepository profissionalRepository;

    private ProfissionalService profissionalService;

    @BeforeEach
    void setUp() {
        profissionalService = new ProfissionalService(profissionalRepository);
    }

    /**
     * Testa o cadastro de um profissional com dados válidos.
     *
     * Deve verificar se o Service consulta a existência do CPF
     * e do CRM e, caso não existam, realiza o salvamento através
     * do Repository.
     */
    @Test
    void deveSalvarProfissionalComDadosValidos() {
        Profissional profissional = criarProfissionalValido();

        when(profissionalRepository.existsById(profissional.getCpf()))
                .thenReturn(false);

        when(profissionalRepository.existsByCrm(profissional.getCrm()))
                .thenReturn(false);

        when(profissionalRepository.save(profissional))
                .thenReturn(profissional);

        Profissional resultado = profissionalService.salvar(profissional);

        assertNotNull(resultado);
        assertEquals(profissional.getCpf(), resultado.getCpf());
        assertEquals(profissional.getCrm(), resultado.getCrm());

        verify(profissionalRepository)
                .existsById(profissional.getCpf());

        verify(profissionalRepository)
                .existsByCrm(profissional.getCrm());

        verify(profissionalRepository)
                .save(profissional);
    }

    /**
     * Testa a regra que impede o cadastro de dois profissionais
     * utilizando o mesmo CPF.
     */
    @Test
    void naoDeveCadastrarProfissionalComCpfExistente() {
        Profissional profissional = criarProfissionalValido();

        when(profissionalRepository.existsById(profissional.getCpf()))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> profissionalService.salvar(profissional)
        );

        verify(profissionalRepository)
                .existsById(profissional.getCpf());

        verify(profissionalRepository, never())
                .existsByCrm(anyString());

        verify(profissionalRepository, never())
                .save(any(Profissional.class));
    }

    /**
     * Testa a regra que impede o cadastro de dois profissionais
     * utilizando o mesmo CRM.
     */
    @Test
    void naoDeveCadastrarProfissionalComCrmExistente() {
        Profissional profissional = criarProfissionalValido();

        when(profissionalRepository.existsById(profissional.getCpf()))
                .thenReturn(false);

        when(profissionalRepository.existsByCrm(profissional.getCrm()))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> profissionalService.salvar(profissional)
        );

        verify(profissionalRepository)
                .existsById(profissional.getCpf());

        verify(profissionalRepository)
                .existsByCrm(profissional.getCrm());

        verify(profissionalRepository, never())
                .save(any(Profissional.class));
    }

    /**
     * Testa a validação que impede o cadastro de um profissional
     * com CPF inválido.
     */
    @Test
    void naoDeveCadastrarProfissionalComCpfInvalido() {
        Profissional profissional = criarProfissionalValido();
        profissional.setCpf("123");

        assertThrows(
                IllegalArgumentException.class,
                () -> profissionalService.salvar(profissional)
        );

        verifyNoInteractions(profissionalRepository);
    }

    /**
     * Testa a validação que impede o cadastro de um profissional
     * com data de nascimento futura.
     */
    @Test
    void naoDeveCadastrarProfissionalComDataNascimentoFutura() {
        Profissional profissional = criarProfissionalValido();

        profissional.setDataNascimento(
                LocalDate.now().plusDays(1)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> profissionalService.salvar(profissional)
        );

        verifyNoInteractions(profissionalRepository);
    }

    /**
     * Testa a validação que impede o cadastro de um profissional
     * sem CRM.
     */
    @Test
    void naoDeveCadastrarProfissionalSemCrm() {
        Profissional profissional = criarProfissionalValido();
        profissional.setCrm(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> profissionalService.salvar(profissional)
        );

        verifyNoInteractions(profissionalRepository);
    }

    /**
     * Testa a validação que impede o cadastro de um profissional
     * sem especialidade.
     */
    @Test
    void naoDeveCadastrarProfissionalSemEspecialidade() {
        Profissional profissional = criarProfissionalValido();
        profissional.setEspecialidade(null);

        assertThrows(
                IllegalArgumentException.class,
                () -> profissionalService.salvar(profissional)
        );

        verifyNoInteractions(profissionalRepository);
    }

    /**
     * Testa a validação que impede o cadastro de um profissional
     * com tempo médio de consulta igual a zero.
     */
    @Test
    void naoDeveCadastrarProfissionalComTempoMedioConsultaInvalido() {
        Profissional profissional = criarProfissionalValido();
        profissional.setTempoMedioConsulta(0);

        assertThrows(
                IllegalArgumentException.class,
                () -> profissionalService.salvar(profissional)
        );

        verifyNoInteractions(profissionalRepository);
    }

    /**
     * Testa a validação que impede o cadastro de um profissional
     * com tempo médio de consulta negativo.
     */
    @Test
    void naoDeveCadastrarProfissionalComTempoMedioConsultaNegativo() {
        Profissional profissional = criarProfissionalValido();
        profissional.setTempoMedioConsulta(-30);

        assertThrows(
                IllegalArgumentException.class,
                () -> profissionalService.salvar(profissional)
        );

        verifyNoInteractions(profissionalRepository);
    }

    /**
     * Testa a busca de um profissional existente através do CPF.
     */
    @Test
    void deveBuscarProfissionalPorCpf() {
        Profissional profissional = criarProfissionalValido();

        when(profissionalRepository.findById(profissional.getCpf()))
                .thenReturn(Optional.of(profissional));

        Profissional resultado =
                profissionalService.buscarPorCpf(profissional.getCpf());

        assertNotNull(resultado);
        assertEquals(
                profissional.getCpf(),
                resultado.getCpf()
        );

        verify(profissionalRepository)
                .findById(profissional.getCpf());
    }

    /**
     * Testa a tentativa de buscar um profissional que não existe.
     */
    @Test
    void deveLancarExcecaoAoBuscarProfissionalInexistente() {
        String cpf = "12345678901";

        when(profissionalRepository.findById(cpf))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> profissionalService.buscarPorCpf(cpf)
        );

        verify(profissionalRepository).findById(cpf);
    }

    /**
     * Testa a atualização dos dados de um profissional existente.
     */
    @Test
    void deveAtualizarProfissional() {
        Profissional profissionalExistente =
                criarProfissionalValido();

        Profissional dadosAtualizados =
                criarProfissionalValido();

        dadosAtualizados.setNomeCompleto(
                "Maria da Silva"
        );

        dadosAtualizados.setEspecialidade(
                Especialidade.DERMATOLOGISTA
        );

        dadosAtualizados.setTempoMedioConsulta(60);

        when(profissionalRepository.findById(
                profissionalExistente.getCpf()))
                .thenReturn(Optional.of(profissionalExistente));

        when(profissionalRepository.save(profissionalExistente))
                .thenReturn(profissionalExistente);

        Profissional resultado =
                profissionalService.atualizar(
                        profissionalExistente.getCpf(),
                        dadosAtualizados
                );

        assertNotNull(resultado);
        assertEquals(
                "Maria da Silva",
                resultado.getNomeCompleto()
        );
        assertEquals(
                Especialidade.DERMATOLOGISTA,
                resultado.getEspecialidade()
        );
        assertEquals(
                60,
                resultado.getTempoMedioConsulta()
        );

        verify(profissionalRepository)
                .findById(profissionalExistente.getCpf());

        verify(profissionalRepository)
                .save(profissionalExistente);
    }

    /**
     * Testa a regra que impede a alteração do CPF durante
     * a atualização de um profissional.
     */
    @Test
    void naoDevePermitirAlteracaoDoCpf() {
        Profissional profissionalExistente =
                criarProfissionalValido();

        Profissional dadosAtualizados =
                criarProfissionalValido();

        dadosAtualizados.setCpf("98765432100");

        when(profissionalRepository.findById(
                profissionalExistente.getCpf()))
                .thenReturn(Optional.of(profissionalExistente));

        assertThrows(
                IllegalArgumentException.class,
                () -> profissionalService.atualizar(
                        profissionalExistente.getCpf(),
                        dadosAtualizados
                )
        );

        verify(profissionalRepository, never())
                .save(any(Profissional.class));
    }

    /**
     * Testa a regra que impede a alteração do CRM durante
     * a atualização de um profissional.
     */
    @Test
    void naoDevePermitirAlteracaoDoCrm() {
        Profissional profissionalExistente =
                criarProfissionalValido();

        Profissional dadosAtualizados =
                criarProfissionalValido();

        dadosAtualizados.setCrm("CRM999999");

        when(profissionalRepository.findById(
                profissionalExistente.getCpf()))
                .thenReturn(Optional.of(profissionalExistente));

        assertThrows(
                IllegalArgumentException.class,
                () -> profissionalService.atualizar(
                        profissionalExistente.getCpf(),
                        dadosAtualizados
                )
        );

        verify(profissionalRepository, never())
                .save(any(Profissional.class));
    }

    /**
     * Testa a exclusão de um profissional existente.
     */
    @Test
    void deveExcluirProfissional() {
        String cpf = "12345678901";

        when(profissionalRepository.existsById(cpf))
                .thenReturn(true);

        profissionalService.excluir(cpf);

        verify(profissionalRepository).existsById(cpf);
        verify(profissionalRepository).deleteById(cpf);
    }

    /**
     * Testa a regra que impede a exclusão de um profissional
     * que não existe no banco de dados.
     */
    @Test
    void naoDeveExcluirProfissionalInexistente() {
        String cpf = "12345678901";

        when(profissionalRepository.existsById(cpf))
                .thenReturn(false);

        assertThrows(
                RuntimeException.class,
                () -> profissionalService.excluir(cpf)
        );

        verify(profissionalRepository).existsById(cpf);

        verify(profissionalRepository, never())
                .deleteById(cpf);
    }

    /**
     * Cria um profissional com todos os dados necessários
     * para os testes.
     *
     * @return profissional válido para utilização nos cenários
     * de teste.
     */
    private Profissional criarProfissionalValido() {
        Profissional profissional = new Profissional();

        profissional.setCpf("12345678901");
        profissional.setNomeCompleto("João da Silva");
        profissional.setDataNascimento(
                LocalDate.of(2000, 1, 10)
        );
        profissional.setSexo(Sexo.MASCULINO);
        profissional.setEndereco("Rua Exemplo, 100");
        profissional.setTelefone("86999999999");

        profissional.setCrm("CRM123456");
        profissional.setEspecialidade(
                Especialidade.ESTETICISTA
        );
        profissional.setTempoMedioConsulta(45);

        return profissional;
    }
}
