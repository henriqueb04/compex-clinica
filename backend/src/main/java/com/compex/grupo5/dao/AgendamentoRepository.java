package com.compex.grupo5.dao;

import com.compex.grupo5.model.Agendamento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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
}