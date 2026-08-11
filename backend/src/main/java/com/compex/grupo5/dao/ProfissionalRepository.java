package com.compex.grupo5.dao;

import com.compex.grupo5.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, String> {
    boolean existsByCrm(String crm);
    Profissional findByCpfIs(String profissionalCpf);
}

