import { useEffect, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Title, Button, Stack, Table, Group } from "@mantine/core";
import type { Agendamento, Cliente, Profissional } from "../tipos";
import api from "../api";
import CpfInput from "./CpfInput";
import { notifications } from "@mantine/notifications";
import { modals } from "@mantine/modals";
import dayjs from "dayjs";

function MarcarFrame({
  agen,
  profissional,
  onSuccess,
}: {
  agen: Agendamento;
  profissional: Profissional;
  onSuccess: () => void;
}) {
  const [cpfInput, setCpfInput] = useState<string>("");
  const [podeConfirmar, setPodeConfirmar] = useState<boolean>(true);

  const {
    data: clienteData,
    isLoading: clienteIsLoading,
    isRefetching: clienteIsRefetching,
    isError: clienteIsError,
    error: clienteError,
    refetch: refetchCliente,
  } = useQuery({
    enabled: false,
    queryKey: ["fetch_cliente", cpfInput],
    queryFn: async () => {
      return (await api.get(`/clientes/${cpfInput.replaceAll(/\D/g, "")}`))
        .data as Cliente;
    },
    gcTime: 0,
  });
  const clienteLoading = clienteIsLoading || clienteIsRefetching;

  const {
    isLoading: marcarIsLoading,
    isRefetching: marcarIsRefetching,
    isError: marcarIsError,
    error: marcarError,
    refetch: refetchMarcar,
  } = useQuery({
    enabled: false,
    queryKey: ["marcar_agendamento"],
    queryFn: async () => {
      if (clienteData) {
        return (
          await api.post(`/agendamentos/marcar`, {
            profissionalCpf: profissional.cpf,
            clienteCpf: clienteData.cpf,
            comeco: agen.comeco,
            fim: agen.fim,
            statusAgendamento: "AGENDADO",
          } as Agendamento)
        ).data as Agendamento;
      } else {
        return null;
      }
    },
  });
  const marcarLoading = marcarIsLoading || marcarIsRefetching;

  const marcar = () => {
    refetchCliente();
  };

  useEffect(() => {
    if (clienteData !== undefined && !clienteLoading && !clienteIsError) {
      setPodeConfirmar(true);
      modals.open({
        title: "Confirmar agendamento?",
        centered: true,
        children: (
          <Stack>
            <Title order={3}>Agendamento</Title>
            <Table variant="vertical">
              <Table.Tbody>
                <Table.Tr>
                  <Table.Th w={100}>Horário</Table.Th>
                  <Table.Td>
                    {dayjs(agen.comeco).format("HH:mm")}
                    {" - "}
                    {dayjs(agen.fim).format("HH:mm")}
                  </Table.Td>
                </Table.Tr>
                <Table.Tr>
                  <Table.Th>Profissional</Table.Th>
                  <Table.Td>{profissional?.nomeCompleto}</Table.Td>
                </Table.Tr>
              </Table.Tbody>
            </Table>
            <Title order={3}>Cliente</Title>
            <Table variant="vertical">
              <Table.Tbody>
                <Table.Tr>
                  <Table.Th w={100}>CPF</Table.Th>
                  <Table.Td>{clienteData.cpf}</Table.Td>
                </Table.Tr>
                <Table.Tr>
                  <Table.Th>Nome</Table.Th>
                  <Table.Td>{clienteData.nomeCompleto}</Table.Td>
                </Table.Tr>
                <Table.Tr>
                  <Table.Th>Data de Nascimento</Table.Th>
                  <Table.Td>
                    {dayjs(clienteData.dataNascimento).format("DD/MM/YYYY")}
                  </Table.Td>
                </Table.Tr>
              </Table.Tbody>
            </Table>
            <Group justify="right">
              <Button
                color="red"
                onClick={() => modals.closeAll()}
                loading={marcarLoading && !podeConfirmar}
              >
                Cancelar
              </Button>
              <Button
                onClick={() => {
                  setPodeConfirmar(false);
                  refetchMarcar();
                }}
                loading={marcarLoading && !podeConfirmar}
              >
                Confirmar
              </Button>
            </Group>
          </Stack>
        ),
      });
    }
  }, [clienteData, clienteIsLoading, clienteIsRefetching]);

  useEffect(() => {
    if (!marcarLoading) {
      modals.closeAll();
      onSuccess();
      setPodeConfirmar(true);
    }
  }, [marcarIsLoading, marcarIsRefetching, marcarIsError]);

  useEffect(() => {
    if (clienteError && !clienteLoading) {
      notifications.show({
        title: "Cliente inválido",
        message: `Verifique o CPF e tente novamente.\n\n(${clienteError.message})`,
        color: "red",
      });
    }
  }, [clienteIsError, clienteError, clienteIsRefetching]);

  useEffect(() => {
    if (marcarError && !marcarLoading) {
      notifications.show({
        title: "Erro ao marcar",
        message: `Algo deu errado.\n\n(${marcarError.message})`,
        color: "red",
      });
    }
  }, [marcarIsError, marcarError, marcarIsRefetching]);

  return (
    <>
      <Title order={5}>Marcar</Title>
      <CpfInput
        value={cpfInput}
        setValue={setCpfInput}
        loading={clienteLoading}
      />
      <Button
        variant="light"
        disabled={
          clienteLoading || !cpfInput.replaceAll(/\D/g, "").match(/^\d{11}$/)
        }
        loading={clienteLoading}
        onClick={marcar}
      >
        Confirmar
      </Button>
    </>
  );
}

export default MarcarFrame;
