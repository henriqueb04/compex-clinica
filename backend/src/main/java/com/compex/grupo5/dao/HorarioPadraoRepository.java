package com.compex.grupo5.dao;

import com.compex.grupo5.model.HorarioPadrao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HorarioPadraoRepository extends JpaRepository<HorarioPadrao, UUID> {
}
