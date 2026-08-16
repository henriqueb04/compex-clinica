package com.compex.grupo5.dao;

import com.compex.grupo5.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
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
