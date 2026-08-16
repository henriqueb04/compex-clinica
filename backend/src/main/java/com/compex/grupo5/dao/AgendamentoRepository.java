package com.compex.grupo5.dao;

import com.compex.grupo5.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    /*
     * Retorna todos os agendamentos com status AGENDADO a partir da data de inicio
     */
    @Query("""
            SELECT a FROM Agendamento a
            JOIN FETCH a.cliente
            JOIN FETCH a.profissional
            WHERE a.statusAgendamento = com.compex.grupo5.misc.StatusAgendamento.AGENDADO
              AND lower(a.intervaloAtendimento) >= :agora
            ORDER BY lower(a.intervaloAtendimento) ASC
            """)
    List<Agendamento> findProximosAgendamentos(@Param("agora") ZonedDateTime agora);
}
