package com.compex.grupo5.service;

import com.compex.grupo5.dao.AgendamentoRepository;
import com.compex.grupo5.model.Agendamento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;

    /*
     * Retorna os próximos agendamentos ativos, ordenados por data/hora crescente.
     */
    @Transactional(readOnly = true)
    public List<Agendamento> listarProximos() {
        return agendamentoRepository.findProximosAgendamentos(ZonedDateTime.now());
    }
    /*
     * Retorna todos os agendamentos de um cliente pelo CPF,
     */
    @Transactional(readOnly = true)
    public List<Agendamento> listarPorCliente(String cpf) {
        return agendamentoRepository.findByClienteCpf(cpf);
    }

    /*
     * Retorna todos os agendamentos de um profissional pelo CPF,
     */
    @Transactional(readOnly = true)
    public List<Agendamento> listarPorProfissional(String cpf) {
        return agendamentoRepository.findByProfissionalCpf(cpf);
    }
}

