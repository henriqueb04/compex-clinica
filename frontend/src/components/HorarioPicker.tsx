import dayjs from "dayjs";
import { TimePicker } from "@mantine/dates";
import type { ScheduleSingleEventData } from "@mantine/schedule";

function HorarioPicker({
  events,
  index,
  onChange,
  label,
  target,
}: {
  events: ScheduleSingleEventData[];
  index: number;
  onChange: (
    callback: (events: ScheduleSingleEventData[]) => ScheduleSingleEventData[],
  ) => void;
  label: string;
  target: "start" | "end";
}) {
  const start = dayjs(events[index].start);
  const end = dayjs(events[index].end);
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
          onChange((events) =>
            events.map((evento, i) =>
              index == i
                ? {
                    ...evento,
                    ...{
                      start:
                        target == "start" ? newTime.format() : start.format(),
                      end: target == "end" ? newTime.format() : end.format(),
                    },
                  }
                : evento,
            ),
          );
        }
      }}
    />
  );
}

export default HorarioPicker;
