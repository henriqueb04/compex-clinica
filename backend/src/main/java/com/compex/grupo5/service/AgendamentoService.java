package com.compex.grupo5.service;

import com.compex.grupo5.dao.AgendamentoRepository;
import com.compex.grupo5.dao.ClienteRepository;
import com.compex.grupo5.dao.HorarioDisponivelRepository;
import com.compex.grupo5.dao.ProfissionalRepository;
import com.compex.grupo5.dto.AgendamentoDto;
import com.compex.grupo5.exception.AgendamentoOutOfBounds;
import com.compex.grupo5.exception.ProfissionalNotFoundException;
import com.compex.grupo5.exception.TimeRangeConflictException;
import com.compex.grupo5.model.Agendamento;
import com.compex.grupo5.model.Cliente;
import com.compex.grupo5.model.HorarioDisponivel;
import com.compex.grupo5.model.Profissional;
import io.hypersistence.utils.hibernate.type.range.Range;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AgendamentoService {
    public static final int GAP_AGENDAMENTOS = 5;
    private final HorarioDisponivelRepository horarioDisponivelRepository;
    private final ClienteRepository clienteRepository;
    private final HorarioDisponivelService horarioDisponivelService;
    private final AgendamentoRepository agendamentoRepository;
    private final ProfissionalRepository profissionalRepository;

    public List<AgendamentoDto> agendamentosProfissionalEmSemana(
            String cpfProfissional,
            Integer ano, Integer numeroSemana
    ) {
        Profissional profissional =
                profissionalRepository.findById(cpfProfissional).orElseThrow(() -> new ProfissionalNotFoundException(
                        "Profissional não encontrado para o CPF informado"));
        List<HorarioDisponivel> horarios = horarioDisponivelService.horariosProfissionalEmSemana(
                cpfProfissional, ano,
                numeroSemana
        );
        Integer tempoMedio = profissional.getTempoMedioConsulta();
        List<Range<ZonedDateTime>> validos = new ArrayList<>();
        List<AgendamentoDto> res = new ArrayList<>();
        for (var horario : horarios) {
            List<Agendamento> agendados = agendamentoRepository.agendamentosEmIntervalo(
                    cpfProfissional, horario.getIntervaloAtendimento().lower(),
                    horario.getIntervaloAtendimento().upper()
            );
            List<Timestamp> tempos = new ArrayList<>();
            for (var a : agendados) {
                tempos.add(new Timestamp(a.getId(), a.getIntervaloAtendimento().lower(), true, true));
                tempos.add(new Timestamp(a.getId(), a.getIntervaloAtendimento().upper(), false, true));
//                res.add(AgendamentoDto.fromEntity(a));
            }
            var tempo = horario.getIntervaloAtendimento().lower();
            ArrayList<Range<ZonedDateTime>> possiveis = new ArrayList<>();
            long i = 0;
            while (tempo.isBefore(horario.getIntervaloAtendimento().upper())) {
                ZonedDateTime fim = tempo.plusMinutes(tempoMedio);
                if (fim.isAfter(horario.getIntervaloAtendimento().upper())) {
                    break;
                }
                possiveis.add(Range.closedOpen(tempo, fim));
                tempos.add(new Timestamp(i, tempo, true, false));
                tempos.add(new Timestamp(i, fim, false, false));
                tempo = fim.plusMinutes(GAP_AGENDAMENTOS);
                i++;
            }
            tempos.sort(Comparator.comparing(Timestamp::tempo));
            boolean agendado = false;
            boolean aberto = false;
            boolean valido = true;
            for (var t : tempos) {
                if (!t.agendado) {
                    // Possíveis
                    if (t.comeco) {
                        aberto = true;
                    } else {
                        aberto = false;
                        if (valido) {
                            validos.add(possiveis.get(t.id.intValue()));
                        }
                        if (!agendado) {
                            valido = true;
                        }
                    }
                } else {
                    // Agendados
                    if (t.comeco) {
                        agendado = true;
                        valido = false;
                    } else {
                        agendado = false;
                        valido = !aberto;
                    }
                }
            }
        }
        res.addAll(agendamentoRepository.findAllByNumeroSemana(numeroSemana)
                .stream()
                .map(AgendamentoDto::fromEntity)
                .toList());
        res.addAll(validos.stream().map(h -> new AgendamentoDto(
                null,
                null,
                null,
                cpfProfissional,
                h.lower(),
                h.upper(),
                null
        )).toList());
        return res;
    }

    public Agendamento salvarAgendamento(AgendamentoDto agendamentoDto) {
        Profissional profissional =
                profissionalRepository.findById(agendamentoDto.profissional_cpf())
                        .orElseThrow(() -> new ProfissionalNotFoundException(
                                "Profissional não encontrado para o CPF informado."));
        Cliente cliente =
                clienteRepository.findById(agendamentoDto.cliente_cpf())
                        .orElseThrow(() -> new ProfissionalNotFoundException(
                                "Cliente não encontrado para o CPF informado."));
        if (ChronoUnit.MINUTES.between(agendamentoDto.comeco(), agendamentoDto.fim()) !=
            profissional.getTempoMedioConsulta()) {
            throw new IllegalArgumentException("Duração inválida para agendamento");
        }
        HorarioDisponivel horario = horarioDisponivelRepository.findContains(
                agendamentoDto.comeco(),
                agendamentoDto.fim()
        ).orElseThrow(() -> new AgendamentoOutOfBounds(
                agendamentoDto.profissional_cpf(), agendamentoDto.comeco(),
                agendamentoDto.fim()
        ));
        if (ChronoUnit.MINUTES.between(horario.getIntervaloAtendimento().lower(), agendamentoDto.comeco()) %
            (profissional.getTempoMedioConsulta() + GAP_AGENDAMENTOS) != 0) {
            throw new IllegalArgumentException("Tempo de começo não alinhado com horários disponíveis.");
        }
        if (!agendamentoRepository.agendamentosEmIntervalo(
                profissional.getCpf(),
                agendamentoDto.comeco(),
                agendamentoDto.fim()
        ).isEmpty()) {
            throw new TimeRangeConflictException(profissional.getCpf(), agendamentoDto.comeco(), agendamentoDto.fim());
        }
        return agendamentoRepository.save(agendamentoDto.toEntity(profissional, cliente));
    }

    record Timestamp(
            Long id,
            ZonedDateTime tempo,
            boolean comeco,
            boolean agendado
    ) {
    }
}
