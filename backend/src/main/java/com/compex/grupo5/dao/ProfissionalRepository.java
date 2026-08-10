package com.compex.grupo5.dao;

import com.compex.grupo5.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

// Rascunho
public interface ProfissionalRepository extends JpaRepository<Profissional, Integer> {
    Profissional findByCpfIs(String profissionalCpf);
}
