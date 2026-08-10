package com.compex.grupo5.service;

import com.compex.grupo5.dao.HorarioDisponivelRepository;
import com.compex.grupo5.dao.ProfissionalRepository;
import com.compex.grupo5.dto.HorarioDisponivelDto;
import com.compex.grupo5.model.HorarioDisponivel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HorarioDisponivelService {
    private final ProfissionalRepository profissionalRepository;
    private final HorarioDisponivelRepository horarioRepository;

    /*
     * Regista horarios e deleta os horarios com os ids providos
     */
    @Transactional
    public List<HorarioDisponivel> salvarHorarios(List<HorarioDisponivelDto> horariosDto, List<Long> idsHorariosDeletados) {
        List<HorarioDisponivel> horarios = new ArrayList<>();
        horarioRepository.deleteAllById(idsHorariosDeletados);
        for (var horarioDto : horariosDto) {
            horarios.add(horarioDto.toEntity(profissionalRepository));
        }
        return horarioRepository.saveAll(horarios);
    }

    /*
     * Pesquisa por horarios disponíveis de um profissional em uma semana específica
     */
    @Transactional
    public List<HorarioDisponivel> horariosProfissionalEmSemana(String cpfProfissional, Integer numeroSemana) {
        return horarioRepository.findByNumeroSemanaAndProfissional_Cpf(numeroSemana, cpfProfissional);
    }

    /*
     * Pesquisa por horarios disponíveis de todos os profissionais de uma especialidade em uma semana específica
     */
    @Transactional
    public List<HorarioDisponivel> horariosEspecialidadeEmSemana(String especialidade, Integer numeroSemana) {
        return horarioRepository.findByNumeroSemanaAndProfissional_Especialidade(numeroSemana, especialidade);
    }
}
