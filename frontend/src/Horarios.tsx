import dayjs from "dayjs";
import { Flex, ScrollArea, Stack } from "@mantine/core";
import { WeekView } from "@mantine/schedule";
import { DatePicker } from "@mantine/dates";
import { useState } from "react";

function Horarios() {
  const [date, setDate] = useState<string>(dayjs().format("YYYY-MM-DD"));
  const selectDate = (date: string | null) => {
    if (date) {
      setDate(date);
    }
  };
  return (
    <Flex w={1500} h={800} gap="md">
      <ScrollArea h={800} scrollbarSize={2} style={{ flexGrow: 1 }}>
        <WeekView
          w="100%"
          date={date}
          withAllDaySlots={false}
          onDateChange={selectDate}
          firstDayOfWeek={0}
          startTime="06:00"
        />
      </ScrollArea>
      <Stack>
        <DatePicker
          value={date}
          onChange={selectDate}
          firstDayOfWeek={0}
          withWeekNumbers
          mih={300}
        />
        <Stack h="50%">
          <h3>Seletect</h3>
          <p>{date}</p>
        </Stack>
      </Stack>
    </Flex>
  );
}

export default Horarios;
