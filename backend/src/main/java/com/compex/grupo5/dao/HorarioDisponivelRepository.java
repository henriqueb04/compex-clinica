package com.compex.grupo5.dao;

import com.compex.grupo5.model.HorarioDisponivel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HorarioDisponivelRepository extends JpaRepository<HorarioDisponivel, UUID> {
}
