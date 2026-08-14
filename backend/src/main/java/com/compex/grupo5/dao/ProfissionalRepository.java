package com.compex.grupo5.dao;

import com.compex.grupo5.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, String> {
    boolean existsByCrm(String crm);
    List<Profissional> findAllByCpfIn(Collection<String> cpfs);
}

