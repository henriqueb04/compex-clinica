package com.compex.grupo5.dao;

import com.compex.grupo5.model.HorarioDisponivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioDisponivelRepository extends JpaRepository<HorarioDisponivel, Long> {
    List<HorarioDisponivel> findByNumeroSemana(Integer numeroSemana);

    @Query("select h from HorarioDisponivel h where h.numeroSemana = ?1 and h.profissional.especialidade = ?2")
    List<HorarioDisponivel> findByNumeroSemanaAndProfissional_Especialidade(Integer numeroSemana, String especialidade);

    @Query("select h from HorarioDisponivel h where h.ano = ?1 and h.numeroSemana = ?2 and h.profissional.cpf = ?3")
    List<HorarioDisponivel> findByAnoAndNumeroSemanaAndProfissional_Cpf(Integer ano, Integer numeroSemana, String cpf);
}
