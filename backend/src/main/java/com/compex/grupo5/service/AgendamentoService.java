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
}

