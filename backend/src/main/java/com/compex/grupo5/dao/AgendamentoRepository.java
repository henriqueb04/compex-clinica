package com.compex.grupo5.dao;

import com.compex.grupo5.model.Agendamento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    /*
     * Retorna todos os agendamentos com status AGENDADO
     * a partir da data/hora informada.
     *
     * A função lower() é do PostgreSQL para o tipo tstzrange.
     * Por isso esta consulta precisa ser nativa.
     */
    @EntityGraph(attributePaths = {"cliente", "profissional"})
    @Query(value = """
            SELECT a.*
            FROM agendamento a
            WHERE a.status_agendamento = 'AGENDADO'
              AND lower(a.intervalo_atendimento) >= :agora
            ORDER BY lower(a.intervalo_atendimento) ASC
            """, nativeQuery = true)
    List<Agendamento> findProximosAgendamentos(
            @Param("agora") ZonedDateTime agora
    );

    /*
     * Retorna todos os agendamentos de um cliente específico.
     */
    @EntityGraph(attributePaths = {"cliente", "profissional"})
    @Query(value = """
            SELECT a.*
            FROM agendamento a
            WHERE a.cliente_cpf = :cpf
            ORDER BY lower(a.intervalo_atendimento) ASC
            """, nativeQuery = true)
    List<Agendamento> findByClienteCpf(
            @Param("cpf") String cpf
    );

    /*
     * Retorna todos os agendamentos de um profissional específico.
     */
    @EntityGraph(attributePaths = {"cliente", "profissional"})
    @Query(value = """
            SELECT a.*
            FROM agendamento a
            WHERE a.profissional_cpf = :cpf
            ORDER BY lower(a.intervalo_atendimento) ASC
            """, nativeQuery = true)
    List<Agendamento> findByProfissionalCpf(
            @Param("cpf") String cpf
    );

    /*
     * Busca um agendamento por ID já com cliente e profissional carregados.
     * Necessário para o cancelamento, pois as relações são LAZY e o
     * findById padrão do JPA não faz o fetch delas.
     */
    @Query("""
            SELECT a
            FROM Agendamento a
            JOIN FETCH a.cliente
            JOIN FETCH a.profissional
            WHERE a.id = :id
            """)
    Optional<Agendamento> findByIdComRelacoes(
            @Param("id") Long id
    );

    /*
     * Busca todos os agendamentos sem o status CANCELADO em um intervalo.
     * nativeQuery porque usa o operador && nativo do PostgreSQL.
     */
    @Query(value = """
        SELECT * FROM agendamento a
        WHERE a.profissional_cpf = (:profissional_cpf) and
        a.intervalo_atendimento && tstzrange(:comeco, :fim, '()') and
        a.status_agendamento != 'CANCELADO'
    """, nativeQuery = true)
    List<Agendamento> agendamentosEmIntervalo(
            @Param("profissional_cpf") String profissionalCpf,
            @Param("comeco") ZonedDateTime comeco,
            @Param("fim") ZonedDateTime fim
    );

    /*
     * Busca todos os agendamentos não cancelados em um ano e semana específicos.
     */
    @Query("""
        SELECT a FROM Agendamento a
        WHERE a.ano = ?1 and
        a.numeroSemana = ?2 and
        a.statusAgendamento != StatusAgendamento.CANCELADO
    """)
    List<Agendamento> agendadosByAnoESemana(Integer ano, Integer numeroSemana);
}

