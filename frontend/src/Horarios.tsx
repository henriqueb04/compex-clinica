import { useEffect, useState } from "react";
import {
  ActionIcon,
  Box,
  Button,
  Center,
  Flex,
  Loader,
  ScrollArea,
  Stack,
  Title,
  Text,
} from "@mantine/core";
import { WeekView, type ScheduleSingleEventData } from "@mantine/schedule";
import { DatePicker } from "@mantine/dates";
import { modals } from "@mantine/modals";
import { notifications } from "@mantine/notifications";
import { useQuery } from "@tanstack/react-query";
import { TrashIcon } from "@phosphor-icons/react";
import dayjs, { type Dayjs } from "dayjs";
import api from "./api";
import HorarioPicker from "./components/HorarioPicker";
import ProfissionalPicker, { type Profissional } from "./components/ProfissionalPicker";

const DayOfWeek = [
  "SUNDAY",
  "MONDAY",
  "TUESDAY",
  "WEDNESDAY",
  "THURSDAY",
  "FRIDAY",
  "SATURDAY",
];

const DEFAULT_COLOR = "cyan";
const SELECTED_COLOR = "yellow";

interface Horario {
  id: number | undefined;
  ano: number;
  diaSemana: (typeof DayOfWeek)[keyof typeof DayOfWeek];
  numeroSemana: number;
  comeco: string;
  fim: string;
  profissional_cpf: string;
}

const fetchHorarios = async (data: Dayjs, cpf: string) => {
  const request = {
    ano: data.get("year"),
    numeroSemana: data.week(),
    cpf: cpf,
  };
  const horarios = (await api.post("/api/horario/profissional", request))
    .data as Horario[];
  return horarios;
};

const toEvent = (h: Horario, nome: string): ScheduleSingleEventData => ({
  id: h.id!,
  title: nome,
  start: dayjs(h.comeco).format("YYYY-MM-DD HH:mm"),
  end: dayjs(h.fim).format("YYYY-MM-DD HH:mm"),
  color: DEFAULT_COLOR,
});

const hasOverlap = (eventos: ScheduleSingleEventData[]): boolean => {
  const ranges = eventos
    .map((e) => ({ start: dayjs(e.start), end: dayjs(e.end) }))
    .sort((a, b) => a.start.diff(b.start));
  for (let i = 0; i < ranges.length - 1; i++) {
    if (ranges[i + 1].start.isBefore(ranges[i].end)) {
      return true;
    }
  }
  return false;
};

function Horarios() {
  const [date, setDate] = useState<Dayjs>(dayjs());
  const [eventos, setEventos] = useState<ScheduleSingleEventData[]>([]);
  const [selectedEvent, setSelectedEvent] = useState<number | null>(null);
  const [mudou, setMudou] = useState<boolean>(false);
  const [nNovos, setNNovos] = useState<number>(0);
  const [excluidos, setExcluidos] = useState<number[]>([]);
  const [profissional, setProfissional] = useState<Profissional | null>(null)

  const dataStr = date.format("YYYY-MM-DD");
  const numeroSemana = date.week();
  const ano = date.year();

  const clearWeekState = () => {
    setSelectedEvent(null);
    setNNovos(0);
    setExcluidos([]);
    setMudou(false);
  };

  const {
    data: horarios,
    isLoading: horariosIsLoading,
    isError: horariosIsError,
    error: horariosError,
    refetch: refetchHorarios,
  } = useQuery({
    queryKey: ["horarios", profissional?.cpf, ano, numeroSemana],
    queryFn: async () => (profissional ? await fetchHorarios(date, profissional!.cpf) : []),
    gcTime: 20 * 1000, // 20 segundos
  });

  const {
    isLoading: salvarIsLoading,
    isError: salvarIsError,
    error: salvarError,
    refetch: refetchSalvar,
  } = useQuery({
    queryKey: ["salvar_horarios"],
    enabled: false,
    queryFn: async () => {
      const salvar: Horario[] = eventos.map((e) => {
        const start = dayjs(e.start);
        const end = dayjs(e.end);
        return {
          id: typeof e.id === "number" ? e.id : undefined,
          ano,
          numeroSemana,
          comeco: start.format(),
          fim: end.format(),
          diaSemana: DayOfWeek[start.day()],
          profissional_cpf: profissional!.cpf,
        };
      }) as Horario[];
      const data = (
        await api.post("api/horario/salvar", {
          horarios: salvar,
          excluidos: excluidos,
        })
      ).data;
      clearWeekState();
      await refetchHorarios();
      return data;
    },
  });

  const salvar = async () => {
    if (hasOverlap(eventos)) {
      notifications.show({
        title: "Dados incorretos",
        message: "Os horários possuem intervalos conflitantes!",
        color: "red",
      });
    } else {
      refetchSalvar();
    }
  };

  // Atualiza eventos sempre que a API é chamada
  useEffect(() => {
    setEventos(horarios?.map((h) => toEvent(h, profissional?.nomeCompleto ?? "")) ?? []);
  }, [horarios]);

  const selectDate = (date: string | null) => {
    if (date) {
      const novaData = dayjs(date);
      if (
        mudou &&
        (novaData.week() !== numeroSemana || novaData.year() !== ano)
      ) {
        modals.openConfirmModal({
          title: "Descartar mudanças?",
          centered: true,
          children: (
            <Text>
              Você realizou mudanças, mas não ainda não salvou. Tem certeza que
              quer ver outra semana e descartar as mudanças nessa?
            </Text>
          ),
          labels: { confirm: "Sim, descartar", cancel: "Não, manter" },
          confirmProps: { color: "red" },
          onConfirm: () => {
            setDate(novaData);
            clearWeekState();
          },
        });
      } else {
        setDate(novaData);
        clearWeekState();
      }
    }
  };

  const novoEvento = ({
    slotStart,
    slotEnd,
  }: {
    slotStart: string;
    slotEnd: string;
  }) => {
    if (profissional) {
      const len = eventos.length;
      setEventos([
        ...eventos.map((e) => ({ ...e, color: DEFAULT_COLOR })),
        {
          id: `novo-${nNovos}`,
          title: profissional.nomeCompleto,
          start: slotStart,
          end: slotEnd,
          color: SELECTED_COLOR,
        },
      ]);
      setNNovos((nNovos) => nNovos + 1);
      setSelectedEvent(len);
      setMudou(true);
    }
  };

  const selectEvento = ({ id }: { id: number | string }) => {
    const i = eventos.findIndex((event) => event.id === id);
    if (i < 0) return;
    setSelectedEvent(i);
    setEventos((eventos) =>
      eventos.map((e) => ({
        ...e,
        color: e.id === id ? SELECTED_COLOR : DEFAULT_COLOR,
      })),
    );
  };

  const deletarEvento = (i: number) => {
    const evento = eventos[i];
    if (evento) {
      setEventos((eventos) => eventos.toSpliced(i, 1));
      setSelectedEvent(null);
      setMudou(true);
      if (typeof evento.id === "number") {
        setExcluidos((deletados) => [...deletados, evento.id as number]);
      }
    }
  };

  useEffect(() => {
    const err = salvarError || horariosError;
    if (err) {
      notifications.show({
        title: "Erro",
        message: `${err.message}${(err.cause !== undefined && ` "${err.cause}"`) || ""}`,
        color: "red",
      });
    }
  }, [salvarIsError, salvarError, horariosIsError, horariosError]);

  return (
    <Stack>
      <Flex gap={8}>
        <Title order={1} style={{ flexGrow: 1 }}>
          Definição de horários
        </Title>
        <ProfissionalPicker value={profissional} onChange={setProfissional} />
        <Button variant="filled" disabled={!mudou || salvarIsLoading} onClick={salvar}>
          Salvar
        </Button>
      </Flex>
      <Flex w={1500} h={800} gap="md" style={{ position: "relative" }}>
        <ScrollArea h={800} scrollbarSize={2} style={{ flexGrow: 1, paddingRight: 4 }}>
          <WeekView
            w="100%"
            date={dataStr}
            onDateChange={selectDate}
            events={eventos}
            firstDayOfWeek={0}
            startTime="06:00"
            intervalMinutes={15}
            withAllDaySlots={false}
            viewSelectProps={{ display: "none" }}
            onTimeSlotClick={novoEvento}
            onEventClick={selectEvento}
          />
        </ScrollArea>
        <Stack>
          <DatePicker
            mih={300}
            value={dataStr}
            onChange={selectDate}
            firstDayOfWeek={0}
            withWeekNumbers
            highlightToday
          />
          <Stack>
            {selectedEvent !== null && eventos[selectedEvent] && (
              <Stack>
                <HorarioPicker
                  events={eventos}
                  index={selectedEvent}
                  label="Começo"
                  onChange={(eventos) => {
                    setEventos(eventos);
                    setMudou(true);
                  }}
                  target="start"
                />
                <HorarioPicker
                  events={eventos}
                  index={selectedEvent}
                  label="Fim"
                  onChange={(eventos) => {
                    setEventos(eventos);
                    setMudou(true);
                  }}
                  target="end"
                />
                <ActionIcon
                  onClick={() => deletarEvento(selectedEvent)}
                  variant="outline"
                  color="red"
                  aria-label="Deletar horário"
                >
                  <TrashIcon size={18} />
                </ActionIcon>
              </Stack>
            )}
          </Stack>
        </Stack>
        {(horariosIsLoading || salvarIsLoading) && (
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

export default Horarios;
