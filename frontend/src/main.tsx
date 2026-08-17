import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { MantineProvider } from "@mantine/core";
import { ModalsProvider } from "@mantine/modals";
import { Notifications } from "@mantine/notifications"
import "@mantine/core/styles.css";
import "@mantine/dates/styles.css";
import "@mantine/schedule/styles.css";
import "@mantine/notifications/styles.css";
import dayjs from "dayjs";
import isoWeek from "dayjs/plugin/isoWeek"
import timezone from "dayjs/plugin/timezone";
import customParserFormat from "dayjs/plugin/customParseFormat";
import utc from "dayjs/plugin/utc";
import "./global.css";
import App from "./App.tsx";

dayjs.extend(isoWeek);
dayjs.extend(timezone);
dayjs.extend(utc);
dayjs.extend(customParserFormat);

const queryClient = new QueryClient();

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <MantineProvider defaultColorScheme="light">
        <Notifications />
        <ModalsProvider>
          <App />
        </ModalsProvider>
      </MantineProvider>
    </QueryClientProvider>
  </StrictMode>,
);
