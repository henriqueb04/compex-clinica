package com.compex.grupo5.service;

import com.compex.grupo5.dao.HorarioDisponivelRepository;
import com.compex.grupo5.dao.ProfissionalRepository;
import com.compex.grupo5.dto.HorarioDisponivelDto;
import com.compex.grupo5.exception.TimeRangeConflictException;
import com.compex.grupo5.model.HorarioDisponivel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HorarioDisponivelService {
    private final ProfissionalRepository profissionalRepository;
    private final HorarioDisponivelRepository horarioRepository;

    /*
     * Regista horarios e deleta os horarios com os ids providos
     */
    @Transactional
    public List<HorarioDisponivel> salvarHorarios(List<HorarioDisponivelDto> toSave, List<Long> toDelete) {
        if (toDelete != null && !toDelete.isEmpty()) {
            horarioRepository.deleteAllByIdInBatch(toDelete);
        }
        if (toSave == null || toSave.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> ignoreIds = (toDelete != null ? toDelete : List.of());
        toSave.stream()
                .map(HorarioDisponivelDto::id)
                .filter(Objects::nonNull)
                .forEach(ignoreIds::add);

        Map<String, List<HorarioDisponivelDto>> porMedico = toSave.stream().collect(Collectors.groupingBy(HorarioDisponivelDto::profissional_cpf));

        for (var entry : porMedico.entrySet()) {
            validarSobreposicao(entry.getValue());
            ZonedDateTime comeco = entry.getValue().stream().map(HorarioDisponivelDto::comeco).min(ZonedDateTime::compareTo).orElseThrow();
            ZonedDateTime fim = entry.getValue().stream().map(HorarioDisponivelDto::fim).max(ZonedDateTime::compareTo).orElseThrow();
            List<HorarioDisponivel> possiveisConflitos = horarioRepository.findConflicts(entry.getKey(), comeco, fim, ignoreIds);
            for (var h : entry.getValue()) {
                boolean conflito = possiveisConflitos.stream()
                        .anyMatch(existente ->
                                !existente.getId().equals(h.id()) &&
                                        existente.getIntervaloAtendimento().lower().isBefore(h.fim()) &&
                                        existente.getIntervaloAtendimento().upper().isAfter(h.comeco())
                        );
                if (conflito) {
                    throw new TimeRangeConflictException(entry.getKey(), comeco, fim);
                }
            }
        }

        return horarioRepository.saveAllAndFlush(toSave.stream().map(dto -> dto.toEntity(profissionalRepository)).toList());
    }

    /*
     * Valida se há sobreposição de intervalos dentre os horários providos
     */
    public void validarSobreposicao(List<HorarioDisponivelDto> horarios) {
        if (horarios == null)
            return;
        HorarioDisponivelDto[] sorted = horarios.stream().sorted(Comparator.comparing(HorarioDisponivelDto::comeco)).toArray(HorarioDisponivelDto[]::new);
        for (int i = 0; i < sorted.length - 1; i++) {
            if (sorted[i+1].comeco().isBefore(sorted[i].fim())) {
                throw new TimeRangeConflictException(sorted[i].profissional_cpf(), sorted[i].comeco(), sorted[i].fim());
            }
        }
    }

    /*
     * Pesquisa por horarios disponíveis de um profissional em uma semana específica
     */
    public List<HorarioDisponivel> horariosProfissionalEmSemana(String cpfProfissional, Integer ano, Integer numeroSemana) {
        return horarioRepository.findByAnoAndNumeroSemanaAndProfissional_Cpf(ano, numeroSemana, cpfProfissional);
    }
}
