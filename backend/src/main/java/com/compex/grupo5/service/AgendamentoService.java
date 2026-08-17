package com.compex.grupo5.service;

import com.compex.grupo5.dao.AgendamentoRepository;
import com.compex.grupo5.dao.ClienteRepository;
import com.compex.grupo5.dao.HorarioDisponivelRepository;
import com.compex.grupo5.dao.ProfissionalRepository;
import com.compex.grupo5.dto.AgendamentoDto;
import com.compex.grupo5.exception.*;
import com.compex.grupo5.misc.StatusAgendamento;
import com.compex.grupo5.model.Agendamento;
import com.compex.grupo5.model.Cliente;
import com.compex.grupo5.model.HorarioDisponivel;
import com.compex.grupo5.model.Profissional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    /*
     * Retorna os próximos agendamentos ativos, ordenados por data/hora crescente.
     */
    @Transactional(readOnly = true)
    public List<Agendamento> listarProximos() {
        return agendamentoRepository.findProximosAgendamentos(ZonedDateTime.now());
    }

    /*
     * Retorna todos os agendamentos de um cliente pelo CPF,
     */
    @Transactional(readOnly = true)
    public List<Agendamento> listarPorCliente(String cpf) {
        return agendamentoRepository.findByClienteCpf(cpf);
    }

    /*
     * Retorna todos os agendamentos de um profissional pelo CPF,
     */
    @Transactional(readOnly = true)
    public List<Agendamento> listarPorProfissional(String cpf) {
        return agendamentoRepository.findByProfissionalCpf(cpf);
    }

    /*
     * Cancela um agendamento pelo ID.
     * - O registro não é deletado — apenas o status é alterado
     *   para CANCELADO (soft delete de estado).
     */
    @Transactional
    public Agendamento cancelar(Long id) {
        Agendamento agendamento = agendamentoRepository
                .findByIdComRelacoes(id)
                .orElseThrow(() -> new AgendamentoNotFoundException(id));

        if (agendamento.getStatusAgendamento() != StatusAgendamento.AGENDADO) {
            throw new BusinessException(
                    "Apenas agendamentos com status AGENDADO podem ser cancelados. " +
                    "Status atual: " + agendamento.getStatusAgendamento()
            );
        }

        agendamento.setStatusAgendamento(StatusAgendamento.CANCELADO);

        return agendamentoRepository.save(agendamento);
    }

    /*
     * Gera uma lista de horários em uma semana. Tanto AGENDADOS quando livres em um horário de atendimento.
     */
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
        List<AgendamentoDto> res = new ArrayList<>();
        // Gera lista de horários livres, evitando sobreposição com os que já foram agendados
        for (var horario : horarios) {
            ZonedDateTime inicioTurno = horario.getIntervaloAtendimento().lower();
            ZonedDateTime fimTurno = horario.getIntervaloAtendimento().upper();

            // Pega os já agendados no intervalo de um horário de atendimento
            List<Agendamento> agendados = agendamentoRepository.agendamentosEmIntervalo(
                    cpfProfissional, horario.getIntervaloAtendimento().lower(),
                    horario.getIntervaloAtendimento().upper()
            );

            ZonedDateTime tempoAtual = inicioTurno;
            while (!tempoAtual.isAfter(fimTurno)) {
                ZonedDateTime inicioSlot = tempoAtual;
                ZonedDateTime fimSlot = tempoAtual.plusMinutes(profissional.getTempoMedioConsulta());
                if (fimSlot.isAfter(fimTurno)) {
                    break;
                }
                // Se não houver conflitos com nenhum que já esteja agendado
                if (agendados.stream().noneMatch(a ->
                        a.getIntervaloAtendimento().lower().isBefore(fimSlot) &&
                        a.getIntervaloAtendimento().upper().isAfter(inicioSlot))) {
                    res.add(new AgendamentoDto(
                            null,
                            null,
                            null,
                            cpfProfissional,
                            profissional.getNomeCompleto(),
                            inicioSlot,
                            fimSlot,
                            null
                    ));
                }
                tempoAtual = fimSlot.plusMinutes(GAP_AGENDAMENTOS);
            }
        }
        res.addAll(agendamentoRepository.agendadosByAnoESemana(ano, numeroSemana)
                .stream()
                .map(AgendamentoDto::fromEntity)
                .toList());
        return res;
    }

    /*
     * Salva um agendamento novo se for válido.
     */
    @Transactional
    public Agendamento salvarAgendamento(AgendamentoDto agendamentoDto) {
        // Verifica se chaves estrangeiras são válidas
        Profissional profissional =
                profissionalRepository.findById(agendamentoDto.profissionalCpf())
                        .orElseThrow(() -> new ProfissionalNotFoundException(
                                "Profissional não encontrado para o CPF informado."));
        Cliente cliente =
                clienteRepository.findById(agendamentoDto.clienteCpf())
                        .orElseThrow(() -> new ProfissionalNotFoundException(
                                "Cliente não encontrado para o CPF informado."));
        // Verifica se está em horário adequado
        if (ChronoUnit.MINUTES.between(agendamentoDto.comeco(), agendamentoDto.fim()) !=
            profissional.getTempoMedioConsulta()) {
            throw new IllegalArgumentException("Duração inválida para agendamento");
        }
        // Verifica se está em um horário de atendimento
        HorarioDisponivel horario = horarioDisponivelRepository.findContains(
                agendamentoDto.comeco(),
                agendamentoDto.fim()
        ).orElseThrow(() -> new AgendamentoOutOfBounds(
                agendamentoDto.profissionalCpf(), agendamentoDto.comeco(),
                agendamentoDto.fim()
        ));
        // Verifica se está alinhado com o começo do intervalo de atendimento
        if (ChronoUnit.MINUTES.between(horario.getIntervaloAtendimento().lower(), agendamentoDto.comeco()) %
            (profissional.getTempoMedioConsulta() + GAP_AGENDAMENTOS) != 0) {
            throw new IllegalArgumentException("Tempo de começo não alinhado com horários disponíveis.");
        }
        // Verifica se conflita com algum agendado existente
        if (!agendamentoRepository.agendamentosEmIntervalo(
                profissional.getCpf(),
                agendamentoDto.comeco(),
                agendamentoDto.fim()
        ).isEmpty()) {
            throw new TimeRangeConflictException(profissional.getCpf(), agendamentoDto.comeco(), agendamentoDto.fim());
        }
        return agendamentoRepository.save(agendamentoDto.toEntity(profissional, cliente));
    }
}

