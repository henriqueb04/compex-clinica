import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { MantineProvider } from "@mantine/core";
import "@mantine/core/styles.css";
import "@mantine/dates/styles.css";
import "@mantine/schedule/styles.css";
import dayjs from "dayjs";
import weekOfYear from "dayjs/plugin/weekOfYear";
import timezone from "dayjs/plugin/timezone";
import utc from "dayjs/plugin/utc";
import "./global.css";
import App from "./App.tsx";

dayjs.extend(weekOfYear);
dayjs.extend(timezone);
dayjs.extend(utc);

const queryClient = new QueryClient();

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <MantineProvider defaultColorScheme="light">
        <App />
      </MantineProvider>
    </QueryClientProvider>
  </StrictMode>,
);
