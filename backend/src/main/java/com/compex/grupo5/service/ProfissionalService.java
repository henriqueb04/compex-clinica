package com.compex.grupo5.service;

import com.compex.grupo5.dao.ProfissionalRepository;
import com.compex.grupo5.model.Profissional;
import com.compex.grupo5.exception.BusinessException;
import com.compex.grupo5.exception.ProfissionalNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;

    /*
     * Retorna todos os profissionais cadastrados no sistema.
     */
    public List<Profissional> listarTodos() {
        return profissionalRepository.findAll();
    }

    /*
     * Busca um profissional através do seu CPF.
     */
    public Profissional buscarPorCpf(String cpf) {
        validarCpf(cpf);

        return profissionalRepository.findById(cpf)
                .orElseThrow(() ->
                        new ProfissionalNotFoundException(
                                "Profissional não encontrado para o CPF informado."
                        ));
    }

    /*
     * Cadastra um novo profissional.
     */
    public Profissional salvar(Profissional profissional) {
        validarProfissional(profissional);

        String cpf = profissional.getCpf();
        String crm = profissional.getCrm();

        if (profissionalRepository.existsById(cpf)) {
            throw new BusinessException(
                    "Já existe um profissional cadastrado com o CPF informado."
            );
        }

        if (profissionalRepository.existsByCrm(crm)) {
            throw new BusinessException(
                    "Já existe um profissional cadastrado com o CRM informado."
            );
        }

        return profissionalRepository.save(profissional);
    }

    /*
     * Atualiza os dados de um profissional existente.
     */
    public Profissional atualizar(
            String cpf,
            Profissional profissional) {

        validarCpf(cpf);
        validarProfissional(profissional);

        Profissional profissionalExistente =
                profissionalRepository.findById(cpf)
                        .orElseThrow(() ->
                                new ProfissionalNotFoundException(
                                        "Profissional não encontrado para atualização."
                                ));

        if (!cpf.equals(profissional.getCpf())) {
            throw new IllegalArgumentException(
                    "O CPF do profissional não pode ser alterado."
            );
        }

        if (!profissionalExistente.getCrm()
                .equals(profissional.getCrm())) {

            throw new IllegalArgumentException(
                    "O CRM do profissional não pode ser alterado."
            );
        }

        profissionalExistente.setNomeCompleto(
                profissional.getNomeCompleto()
        );

        profissionalExistente.setDataNascimento(
                profissional.getDataNascimento()
        );

        profissionalExistente.setSexo(
                profissional.getSexo()
        );

        profissionalExistente.setEndereco(
                profissional.getEndereco()
        );

        profissionalExistente.setTelefone(
                profissional.getTelefone()
        );

        profissionalExistente.setEspecialidade(
                profissional.getEspecialidade()
        );

        profissionalExistente.setTempoMedioConsulta(
                profissional.getTempoMedioConsulta()
        );

        return profissionalRepository.save(profissionalExistente);
    }

    /*
     * Exclui um profissional através do CPF.
     */
    public void excluir(String cpf) {
        validarCpf(cpf);

        if (!profissionalRepository.existsById(cpf)) {
            throw new ProfissionalNotFoundException(
                    "Não é possível excluir: profissional não encontrado."
            );
        }

        profissionalRepository.deleteById(cpf);
    }

    /*
     * Valida os dados obrigatórios do profissional.
     */
    private void validarProfissional(Profissional profissional) {

        if (profissional == null) {
            throw new IllegalArgumentException(
                    "Profissional não pode ser nulo."
            );
        }

        validarCpf(profissional.getCpf());

        if (profissional.getNomeCompleto() == null ||
                profissional.getNomeCompleto().isBlank()) {
            throw new IllegalArgumentException(
                    "Nome completo é obrigatório."
            );
        }

        if (profissional.getDataNascimento() == null) {
            throw new IllegalArgumentException(
                    "Data de nascimento é obrigatória."
            );
        }

        if (profissional.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Data de nascimento não pode ser futura."
            );
        }

        if (profissional.getSexo() == null) {
            throw new IllegalArgumentException(
                    "Sexo é obrigatório."
            );
        }

        if (profissional.getEndereco() == null ||
                profissional.getEndereco().isBlank()) {
            throw new IllegalArgumentException(
                    "Endereço é obrigatório."
            );
        }

        if (profissional.getTelefone() == null ||
                profissional.getTelefone().isBlank()) {
            throw new IllegalArgumentException(
                    "Telefone é obrigatório."
            );
        }

        if (profissional.getCrm() == null ||
                profissional.getCrm().isBlank()) {
            throw new IllegalArgumentException(
                    "CRM é obrigatório."
            );
        }

        if (profissional.getEspecialidade() == null) {
            throw new IllegalArgumentException(
                    "Especialidade é obrigatória."
            );
        }

        if (profissional.getTempoMedioConsulta() == null ||
                profissional.getTempoMedioConsulta() <= 0) {
            throw new IllegalArgumentException(
                    "Tempo médio de consulta deve ser maior que zero."
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
