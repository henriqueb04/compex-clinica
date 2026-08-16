package com.compex.grupo5.dao;

import com.compex.grupo5.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
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

    /*
     * Retorna todos os agendamentos de um cliente específico,
     */
    @Query("""
            SELECT a FROM Agendamento a
            JOIN FETCH a.cliente
            JOIN FETCH a.profissional
            WHERE a.cliente.cpf = :cpf
            ORDER BY lower(a.intervaloAtendimento) ASC
            """)
    List<Agendamento> findByClienteCpf(@Param("cpf") String cpf);

    /*
     * Retorna todos os agendamentos de um profissional específico,
     */
    @Query("""
            SELECT a FROM Agendamento a
            JOIN FETCH a.cliente
            JOIN FETCH a.profissional
            WHERE a.profissional.cpf = :cpf
            ORDER BY lower(a.intervaloAtendimento) ASC
            """)
    List<Agendamento> findByProfissionalCpf(@Param("cpf") String cpf);
    /*
     * Busca um agendamento por ID já com cliente e profissional carregados.
     * Necessário para o cancelamento, pois as relações são LAZY e o
     * findById padrão do JPA não faz o fetch delas.
     */
    @Query("""
            SELECT a FROM Agendamento a
            JOIN FETCH a.cliente
            JOIN FETCH a.profissional
            WHERE a.id = :id
            """)
    Optional<Agendamento> findByIdComRelacoes(@Param("id") Long id);

    @Query(value = """
        SELECT * FROM agendamento a
        WHERE a.profissional_cpf = (:profissional_cpf) and
        a.intervalo_atendimento && tstzrange(:comeco, :fim, '()')
    """, nativeQuery = true)
    List<Agendamento> agendamentosEmIntervalo(
            @Param("profissional_cpf") String profissionalCpf,
            @Param("comeco") ZonedDateTime comeco,
            @Param("fim") ZonedDateTime fim
    );
    List<Agendamento> findAllByNumeroSemana(Integer numeroSemana);
}

