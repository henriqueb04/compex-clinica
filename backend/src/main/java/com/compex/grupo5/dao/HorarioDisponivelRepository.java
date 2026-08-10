package com.compex.grupo5.dao;

import com.compex.grupo5.model.HorarioDisponivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HorarioDisponivelRepository extends JpaRepository<HorarioDisponivel, Long> {
    List<HorarioDisponivel> findByNumeroSemana(Integer numeroSemana);

    @Query("select h from HorarioDisponivel h where h.numeroSemana = ?1 and h.profissional.cpf = ?2")
    List<HorarioDisponivel> findByNumeroSemanaAndProfissional_Cpf(Integer numeroSemana, String cpf);

    @Query("select h from HorarioDisponivel h where h.numeroSemana = ?1 and h.profissional.especialidade = ?2")
    List<HorarioDisponivel> findByNumeroSemanaAndProfissional_Especialidade(Integer numeroSemana, String especialidade);
}
