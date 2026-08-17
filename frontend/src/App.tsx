import type { ComponentProps } from "react";
import {
  BrowserRouter,
  Route,
  Routes,
  NavLink as RouterLink,
} from "react-router";
import { useDisclosure } from "@mantine/hooks";
import {
  Burger,
  Center,
  Card,
  Drawer,
  NavLink as MantineLink,
  type NavLinkProps,
} from "@mantine/core";
import { CalendarDotsIcon, ClockIcon, HouseIcon, ListBulletsIcon, UsersIcon } from "@phosphor-icons/react";
import Home from "./Home";
import "./App.css";
import Listagem from "./Listagem";
import Horarios from "./Horarios";
import Cliente from "./Cliente";
import Profissional from "./Profissional";
import Agendamentos from "./Agendamentos";

function NavLink({
  href,
  end,
  ...props
}: NavLinkProps & ComponentProps<"a"> & { end?: boolean }) {
  return (
    <MantineLink
      href={href}
      renderRoot={(props) => <RouterLink to={href} end={end} {...props} />}
      {...props}
    />
  );
}

function App() {
  const [drawerOpened, { open: openDrawer, close: closeDrawer }] =
    useDisclosure();
  return (
    <>
      <Burger className="menu" onClick={openDrawer} />
      <BrowserRouter>
        <Drawer opened={drawerOpened} onClose={closeDrawer}>
          <NavLink
            href="/"
            label="Início"
            leftSection={<HouseIcon size={16} />}
          />
          <NavLink
              href="/clientes"
              label="Clientes"
              leftSection={<UsersIcon size={16} />}
          />

          <NavLink
              href="/profissionais"
              label="Profissionais"
              leftSection={<UsersIcon size={16} />}
          />

          <NavLink
              href="/horarios"
              label="Definir horários de atendimento"
              leftSection={<ClockIcon size={16} />}
          />
          <NavLink
            href="/agendamentos"
            label="Marcar agendamentos"
            leftSection={<CalendarDotsIcon size={16} />}
          />
        </Drawer>

        <Center className="centro">
          <Card withBorder>
            <Routes>
              <Route path="/">
                <Route index element={<Home />} />
                <Route path="/horarios" element={<Horarios />} />
                <Route path="/clientes" element={<Cliente />} />
                <Route path="/profissionais" element={<Profissional />} />
                <Route path="/agendamentos" element={<Agendamentos />} />
              </Route>
            </Routes>
          </Card>
        </Center>
      </BrowserRouter>
    </>
  );
}

export default App;
