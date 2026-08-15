package com.compex.grupo5.dao;

import com.compex.grupo5.model.HorarioDisponivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface HorarioDisponivelRepository extends JpaRepository<HorarioDisponivel, Long> {
    @Query("select h from HorarioDisponivel h where h.ano = ?1 and h.numeroSemana = ?2 and h.profissional.cpf = ?3")
    List<HorarioDisponivel> findByAnoAndNumeroSemanaAndProfissional_Cpf(Integer ano, Integer numeroSemana, String cpf);

    @Query(value = """
            select * from horarios_disponiveis h
            where h.id_profissional = (:profissional) and
            h.intervalo_atendimento && tstzrange(:comeco, :fim, '[]') and
            h.id NOT IN (:ignoredIds)
""", nativeQuery = true)
    List<HorarioDisponivel> findConflicts(
            @Param("profissional") String profissional,
            @Param("comeco") ZonedDateTime comeco,
            @Param("fim") ZonedDateTime fim,
            @Param("ignoredIds") List<Long> ignoredIds
            );
}
