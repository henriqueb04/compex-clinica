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
import { HouseIcon, ListBulletsIcon } from "@phosphor-icons/react";
import Home from "./Home";
import "./App.css";
import Listagem from "./Listagem";
import type { ComponentProps } from "react";

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
            href="/listagem"
            label="Listagem"
            leftSection={<ListBulletsIcon size={16} />}
          />
        </Drawer>

        <Center className="centro">
          <Card withBorder>

            <Routes>
              <Route path="/">
                <Route index element={<Home />} />
                <Route path="/listagem" element={<Listagem />} />
              </Route>
            </Routes>

          </Card>
        </Center>

      </BrowserRouter>
    </>
  );
}

export default App;
