package com.compex.grupo5.service;

import com.compex.grupo5.dao.HorarioDisponivelRepository;
import com.compex.grupo5.dao.ProfissionalRepository;
import com.compex.grupo5.dto.HorarioDisponivelDto;
import com.compex.grupo5.exception.ProfissionalNotFoundException;
import com.compex.grupo5.exception.TimeRangeConflictException;
import com.compex.grupo5.model.HorarioDisponivel;
import com.compex.grupo5.model.Profissional;
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

        List<Long> ignoreIds = new ArrayList<>();
        if (toDelete != null) {
            ignoreIds.addAll(toDelete);
        }
        toSave.stream()
                .map(HorarioDisponivelDto::id)
                .filter(Objects::nonNull)
                .forEach(ignoreIds::add);
        if (ignoreIds.isEmpty()) {
            ignoreIds.add(-1L);
        }

        Map<String, List<HorarioDisponivelDto>> porMedico = toSave.stream()
                .collect(Collectors.groupingBy(HorarioDisponivelDto::profissional_cpf));
        Map<String, Profissional> profissionaisMap = profissionalRepository.findAllByCpfIn(porMedico.keySet())
                .stream()
                .collect(Collectors.toMap(Profissional::getCpf, p -> p));

        List<HorarioDisponivel> entidadesParaSalvar = new ArrayList<>();

        for (var entry : porMedico.entrySet()) {
            String profissional_cpf = entry.getKey();
            List<HorarioDisponivelDto> horariosDto = entry.getValue();

            // Exception se for inválido
            validarSobreposicao(entry.getValue());

            ZonedDateTime comeco = entry.getValue()
                    .stream()
                    .map(HorarioDisponivelDto::comeco)
                    .min(ZonedDateTime::compareTo)
                    .orElseThrow();
            ZonedDateTime fim = entry.getValue()
                    .stream()
                    .map(HorarioDisponivelDto::fim)
                    .max(ZonedDateTime::compareTo)
                    .orElseThrow();

            Profissional profissional = profissionaisMap.get(entry.getKey());
            if (profissional == null) {
                throw new ProfissionalNotFoundException("Profissional não encontrado para o CPF " + profissional_cpf);
            }

            List<HorarioDisponivel> possiveisConflitos = horarioRepository.findConflicts(
                    profissional_cpf,
                    comeco,
                    fim,
                    ignoreIds
            );
            for (var h : horariosDto) {
                boolean conflito = possiveisConflitos.stream()
                        .anyMatch(existente -> !existente.getId().equals(h.id()) &&
                                               existente.getIntervaloAtendimento().lower().isBefore(h.fim()) &&
                                               existente.getIntervaloAtendimento().upper().isAfter(h.comeco()));
                if (conflito) {
                    throw new TimeRangeConflictException(profissional_cpf, h.comeco(), h.fim());
                }
                entidadesParaSalvar.add(h.toEntity(profissional));
            }
        }

        return horarioRepository.saveAllAndFlush(entidadesParaSalvar);
    }

    /*
     * Valida se há sobreposição de intervalos dentre os horários providos
     */
    public void validarSobreposicao(List<HorarioDisponivelDto> horarios) {
        if (horarios == null)
            return;
        HorarioDisponivelDto[] sorted = horarios.stream()
                .sorted(Comparator.comparing(HorarioDisponivelDto::comeco))
                .toArray(HorarioDisponivelDto[]::new);
        for (int i = 0; i < sorted.length - 1; i++) {
            if (sorted[i + 1].comeco().isBefore(sorted[i].fim())) {
                throw new TimeRangeConflictException(sorted[i].profissional_cpf(), sorted[i].comeco(), sorted[i].fim());
            }
        }
    }

    /*
     * Pesquisa por horarios disponíveis de um profissional em uma semana específica
     */
    public List<HorarioDisponivel> horariosProfissionalEmSemana(
            String cpfProfissional, Integer ano, Integer numeroSemana) {
        return horarioRepository.findByAnoAndNumeroSemanaAndProfissional_Cpf(ano, numeroSemana, cpfProfissional);
    }
}
