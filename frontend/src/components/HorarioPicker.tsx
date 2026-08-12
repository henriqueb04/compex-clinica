import dayjs, { Dayjs } from "dayjs";
import { TimePicker } from "@mantine/dates";
import type { ScheduleSingleEventData } from "@mantine/schedule";

function HorarioPicker({
  events,
  i,
  onChange,
  label,
  target,
}: {
  events: ScheduleSingleEventData[];
  i: number;
  onChange: (
    callback: (events: ScheduleSingleEventData[]) => ScheduleSingleEventData[],
  ) => void;
  label: string;
  target: "start" | "end";
}) {
  const start = dayjs(events[i].start);
  const end = dayjs(events[i].end);
  const targetTime = target === "start" ? start : end;
  return (
    <TimePicker
      label={label}
      value={targetTime.format("HH:mm")}
      min={
        target === "end" ? start.add(5, "minute").format("HH:mm:00") : undefined
      }
      max={
        target === "start"
          ? end.subtract(5, "minute").format("HH:mm:00")
          : undefined
      }
      onChange={(t) => {
        const time = dayjs(t, "HH:mm");
        const newTime = targetTime.hour(time.hour()).minute(time.minute());
        if (
          target === "start" ? newTime.isBefore(end) : newTime.isAfter(start)
        ) {
          onChange((events) => {
            return [
              ...events.toSpliced(i),
              {
                ...events[i],
                ...{
                  start: target == "start" ? newTime.format() : start.format(),
                  end: target == "end" ? newTime.format() : end.format(),
                },
              },
            ] as ScheduleSingleEventData[];
          });
        }
      }}
    />
  );
}

export default HorarioPicker;
