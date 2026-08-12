import dayjs, { Dayjs } from "dayjs";
import { Box, Center, Flex, ScrollArea, Stack, Title } from "@mantine/core";
import { WeekView, type ScheduleSingleEventData } from "@mantine/schedule";
import { DatePicker } from "@mantine/dates";
import { useEffect, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import api from "./api";
import { SpinnerIcon } from "@phosphor-icons/react";
import HorarioPicker from "./components/HorarioPicker";

const DayOfWeek = {
  Sunday: "SUNDAY",
  Monday: "MONDAY",
  Tuesday: "TUESDAY",
  Wednesday: "WEDNESDAY",
  Thursday: "THURSDAY",
  Friday: "FRIDAY",
  Saturday: "SATURDAY",
};

const DEFAULT_COLOR = "cyan";
const SELECTED_COLOR = "yellow";

interface Horario {
  id: number | string;
  ano: number;
  diaSemana: (typeof DayOfWeek)[keyof typeof DayOfWeek];
  numeroSemana: number;
  comeco: Date;
  fim: Date;
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
  id: h.id,
  title: nome,
  start: dayjs(h.comeco).format("YYYY-MM-DD HH:mm"),
  end: dayjs(h.fim).format("YYYY-MM-DD HH:mm"),
  color: DEFAULT_COLOR,
});

function Horarios() {
  const profissional_cpf = "11111111111";
  const profissional_nome = "fulano";
  const [date, setDate] = useState<Dayjs>(dayjs());
  const [eventos, setEventos] = useState<ScheduleSingleEventData[]>([]);
  const [mudou, setMudou] = useState<boolean>(false);
  const [nNovos, setNNovos] = useState<number>(0);
  const [selectedEvent, setSelectedEvent] = useState<number | null>(null);

  const dataStr = date.format("YYYY-MM-DD");
  const semana = date.week();
  const ano = date.year();

  const {
    data: horarios,
    isLoading,
    error,
  } = useQuery({
    queryKey: ["horarios", profissional_cpf, ano, semana],
    queryFn: async () => await fetchHorarios(date, profissional_cpf),
    gcTime: 20 * 1000, // 20 segundos
  });

  // Atualiza sempre que a API é chamada
  useEffect(() => {
    setEventos(horarios?.map((h) => toEvent(h, profissional_nome)) ?? []);
  }, [horarios]);

  const selectDate = (date: string | null) => {
    if (date) {
      const novaData = dayjs(date);
      if (mudou && (novaData.week() !== semana || novaData.year() !== ano)) {
        // Confirmação
      }
      setDate(novaData);
    }
  };

  const novoEvento = ({
    slotStart,
    slotEnd,
  }: {
    slotStart: string;
    slotEnd: string;
  }) => {
    setEventos([
      ...eventos,
      {
        id: `novo-${nNovos}`,
        start: slotStart,
        end: slotEnd,
        title: profissional_nome,
        color: DEFAULT_COLOR,
      },
    ]);
    setNNovos((nNovos) => nNovos + 1);
    setMudou(true);
  };

  const selectEvent = ({ id }: { id: number | string }) => {
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

  if (error) {
    return `Erro ao buscar horários: ${error.message}`;
  }

  return (
    <Stack>
      <Title order={1}>Definição de horários</Title>
      <Flex w={1500} h={800} gap="md" style={{ position: "relative" }}>
        <ScrollArea h={800} scrollbarSize={2} style={{ flexGrow: 1 }}>
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
            onEventClick={selectEvent}
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
              <>
                <HorarioPicker
                  events={eventos}
                  i={selectedEvent}
                  label="Começo"
                  onChange={setEventos}
                  target="start"
                />
                <HorarioPicker
                  events={eventos}
                  i={selectedEvent}
                  label="Fim"
                  onChange={setEventos}
                  target="end"
                />
              </>
            )}
            <p>
              {selectedEvent !== null &&
                `${dayjs(eventos[selectedEvent].start).format()} - ${dayjs(eventos[selectedEvent].end).format()}`}
            </p>
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
              <SpinnerIcon size={16} />
            </Center>
          </Box>
        )}
      </Flex>
    </Stack>
  );
}

export default Horarios;
