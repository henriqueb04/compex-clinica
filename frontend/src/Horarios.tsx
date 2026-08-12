import dayjs, { Dayjs } from "dayjs";
import { Box, Center, Flex, ScrollArea, Stack, Title } from "@mantine/core";
import { WeekView, type ScheduleSingleEventData } from "@mantine/schedule";
import { DatePicker } from "@mantine/dates";
import { useEffect, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import api from "./api";
import { SpinnerIcon } from "@phosphor-icons/react";

const DayOfWeek = {
  Sunday: "SUNDAY",
  Monday: "MONDAY",
  Tuesday: "TUESDAY",
  Wednesday: "WEDNESDAY",
  Thursday: "THURSDAY",
  Friday: "FRIDAY",
  Saturday: "SATURDAY",
};

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
  console.table(request);
  const horarios = (await api.post("/api/horario/profissional", request))
    .data as Horario[];
  return horarios;
};

const toEvent = (h: Horario, nome: string): ScheduleSingleEventData => ({
  id: h.id,
  title: nome,
  start: dayjs(h.comeco).format("YYYY-MM-DD HH:mm"),
  end: dayjs(h.fim).format("YYYY-MM-DD HH:mm"),
  color: "blue",
});

function Horarios() {
  const profissional_cpf = "11111111111";
  const profissional_nome = "fulano";
  const [date, setDate] = useState<Dayjs>(dayjs());
  const [mudou, setMudou] = useState<boolean>(false);
  const [nNovos, setNNovos] = useState<number>(0);

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

  const [eventos, setEventos] = useState<ScheduleSingleEventData[]>([]);

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
        id: nNovos.toString(),
        start: slotStart,
        end: slotEnd,
        title: profissional_nome,
        color: "blue",
      },
    ]);
    setNNovos(nNovos + 1);
    setMudou(true);
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
            onTimeSlotClick={novoEvento}
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
          <Stack h="50%">
            <h3>Seletect</h3>
            <p>{dataStr}</p>
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
