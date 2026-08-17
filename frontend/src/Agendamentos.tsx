import { useEffect, useState } from "react";
import {
  ActionIcon,
  Box,
  Center,
  Flex,
  Loader,
  ScrollArea,
  Stack,
  Title,
  Table,
  Select,
} from "@mantine/core";
import { WeekView, type ScheduleSingleEventData } from "@mantine/schedule";
import { DatePicker } from "@mantine/dates";
import { notifications } from "@mantine/notifications";
import { useQuery } from "@tanstack/react-query";
import { XCircleIcon } from "@phosphor-icons/react";
import dayjs, { type Dayjs } from "dayjs";
import { v7 as uuidv7 } from "uuid";
import api from "./api";
import { Especialidades, type Agendamento, type Horario, type Profissional } from "./tipos";
import ProfissionalPicker from "./components/ProfissionalPicker";
import MarcarFrame from "./components/MarcarFrame";

const BACK_COLOR = "gray";
const SELECTED_COLOR = "yellow";
const AGENDADO_COLOR = "blue";
const LIVRE_COLOR = "cyan";

const fetchHorarios = async (data: Dayjs, cpf: string) => {
  const horarios = (
    await api.get(
      `/api/horario/profissional/${cpf}?numeroSemana=${data.isoWeek()}&ano=${data.isoWeekYear()}`,
    )
  ).data as Horario[];
  return horarios;
};

const fetchAgendamentos = async (data: Dayjs, cpf: string) => {
  const agens = (
    await api.get(
      `/agendamentos/profissional/${cpf}/semana?numeroSemana=${data.isoWeek()}&ano=${data.isoWeekYear()}`,
    )
  ).data as Agendamento[];
  return agens;
};

const horarioToEvent = (h: Horario, nome: string): ScheduleSingleEventData => ({
  id: uuidv7(),
  title: nome,
  start: dayjs(h.comeco).format("YYYY-MM-DD HH:mm"),
  end: dayjs(h.fim).format("YYYY-MM-DD HH:mm"),
  display: "background",
  color: BACK_COLOR,
});

const agenToEvent = (
  a: Agendamento,
  nome: string,
): ScheduleSingleEventData => ({
  id: a.id ?? uuidv7(),
  title: `|${nome}|${a.clienteNome ? `\n(${a.clienteNome})` : ""}`,
  start: dayjs(a.comeco).format("YYYY-MM-DD HH:mm"),
  end: dayjs(a.fim).format("YYYY-MM-DD HH:mm"),
  display: "default",
  color: a.id != undefined ? AGENDADO_COLOR : LIVRE_COLOR,
});

function Agendamentos() {
  const [date, setDate] = useState<Dayjs>(dayjs());
  const [horariosDisponiveis, setHorariosDisponiveis] = useState<
    ScheduleSingleEventData[]
  >([]);
  const [agendamentos, setAgendamentos] = useState<ScheduleSingleEventData[]>(
    [],
  );
  const [selectedEvent, setSelectedEvent] = useState<number | null>(null);
  const [profissional, setProfissional] = useState<Profissional | null>(null);
  const [especialidade, setEspecialidade] = useState<string | null>(null);

  const dataStr = date.format("YYYY-MM-DD");
  const numeroSemana = date.isoWeek();
  const ano = date.isoWeekYear();

  const clearWeekState = () => {
    setSelectedEvent(null);
  };

  const {
    data: horarios,
    isLoading: horariosIsLoading,
    isError: horariosIsError,
    error: horariosError,
    refetch: refetchHorarios,
  } = useQuery({
    queryKey: ["horarios", profissional?.cpf, ano, numeroSemana],
    queryFn: async () =>
      profissional ? await fetchHorarios(date, profissional.cpf) : [],
  });

  useEffect(() => {
    setHorariosDisponiveis(
      horarios?.map((h) =>
        horarioToEvent(h, profissional?.nomeCompleto ?? ""),
      ) ?? [],
    );
  }, [horarios]);

  const {
    data: agens,
    isLoading: agensIsLoading,
    isError: agensIsError,
    error: agensError,
    refetch: refetchAgendamentos,
  } = useQuery({
    queryKey: ["agens", profissional?.cpf, ano, numeroSemana],
    queryFn: async () =>
      profissional ? await fetchAgendamentos(date, profissional.cpf) : [],
    gcTime: 1,
  });

  useEffect(() => {
    setAgendamentos(
      agens?.map((a) => agenToEvent(a, profissional?.nomeCompleto ?? "")) ?? [],
    );
  }, [agens]);

  const selectDate = (date: string | null) => {
    if (date) {
      const novaData = dayjs(date);
      setDate(novaData);
      clearWeekState();
    }
  };

  const selectEvento = ({ id }: { id: number | string }) => {
    const i = agendamentos.findIndex((event) => event.id === id);
    if (i < 0) return;
    setSelectedEvent(i);
    setAgendamentos((agendamentos) =>
      agendamentos.map((a) => ({
        ...a,
        color:
          a.id == id
            ? SELECTED_COLOR
            : typeof a.id !== "string"
              ? AGENDADO_COLOR
              : LIVRE_COLOR,
      })),
    );
  };

  useEffect(() => {
    const err = horariosError || agensError;
    if (err) {
      console.error(err);
      notifications.show({
        title: "Erro",
        message: `${err.message}${(err.cause !== undefined && ` "${err.cause}"`) || ""}`,
        color: "red",
      });
    }
  }, [horariosIsError, horariosError, agensIsError, agensError]);

  const isLoading = horariosIsLoading || agensIsLoading;

  const events = [...agendamentos, ...horariosDisponiveis];

  return (
    <Stack>
      <Flex gap={8}>
        <Title order={1} style={{ flexGrow: 1 }}>
          Agendamento
        </Title>
        <Select
          placeholder="Especialidades..."
          value={especialidade}
          onChange={setEspecialidade}
          data={Especialidades}
        />
        <ProfissionalPicker
          especialidade={especialidade}
          value={profissional}
          onChange={(a) => {
            clearWeekState();
            setProfissional(a);
          }}
        />
      </Flex>
      <Flex w={1500} h={800} gap="md" style={{ position: "relative" }}>
        <ScrollArea
          h={800}
          scrollbarSize={2}
          style={{ flexGrow: 1, paddingRight: 4 }}
        >
          <WeekView
            w="100%"
            date={dataStr}
            onDateChange={selectDate}
            events={events}
            onEventClick={selectEvento}
            slotHeight={80}
            firstDayOfWeek={1}
            startTime="06:00"
            intervalMinutes={15}
            withAllDaySlots={false}
            viewSelectProps={{ display: "none" }}
          />
        </ScrollArea>
        <Stack>
          <DatePicker
            mih={300}
            value={dataStr}
            onChange={selectDate}
            firstDayOfWeek={1}
            withWeekNumbers
            highlightToday
          />
          <Stack>
            {selectedEvent !== null &&
              agendamentos[selectedEvent] &&
              agens?.[selectedEvent] && (
                <Stack>
                  <Title order={3}>Agendamento</Title>
                  <Table variant="vertical">
                    <Table.Tbody>
                      <Table.Tr>
                        <Table.Th w={100}>Horário</Table.Th>
                        <Table.Td>
                          {dayjs(agendamentos[selectedEvent].start).format(
                            "HH:mm",
                          )}
                          {" - "}
                          {dayjs(agendamentos[selectedEvent].end).format(
                            "HH:mm",
                          )}
                        </Table.Td>
                      </Table.Tr>
                      <Table.Tr>
                        <Table.Th>Profissional</Table.Th>
                        <Table.Td>{profissional?.nomeCompleto}</Table.Td>
                      </Table.Tr>
                      {agens?.[selectedEvent]?.clienteNome != null && (
                        <>
                          <Table.Tr>
                            <Table.Th>Cliente</Table.Th>
                            <Table.Td>
                              {agens[selectedEvent].clienteNome}
                            </Table.Td>
                          </Table.Tr>
                          <Table.Tr>
                            <Table.Th>CPF Cliente</Table.Th>
                            <Table.Td>
                              {agens[selectedEvent].clienteCpf}
                            </Table.Td>
                          </Table.Tr>
                        </>
                      )}
                    </Table.Tbody>
                  </Table>
                  {agens[selectedEvent]?.clienteNome == null &&
                    profissional && (
                      <MarcarFrame
                        agen={agens[selectedEvent]}
                        profissional={profissional}
                        onSuccess={() => {
                          refetchAgendamentos();
                          refetchHorarios();
                        }}
                      />
                    )}
                  {agens?.[selectedEvent]?.clienteNome != null && (
                    <ActionIcon
                      // onClick={() => deletarEvento(selectedEvent)}
                      variant="outline"
                      color="red"
                      aria-label="Deletar horário"
                    >
                      <XCircleIcon size={18} />
                    </ActionIcon>
                  )}
                </Stack>
              )}
          </Stack>
        </Stack>
        {isLoading && (
          <Box
            style={{
              position: "absolute",
              inset: 0,
              backgroundColor: "Background",
              opacity: 0.5,
              zIndex: 20,
            }}
          >
            <Center style={{ height: "100%" }}>
              <Loader size={16} />
            </Center>
          </Box>
        )}
      </Flex>
    </Stack>
  );
}

export default Agendamentos;
