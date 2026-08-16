import { useState } from "react";
import {
    Button,
    Modal,
    Select,
    Stack,
    Table,
    Text,
    TextInput,
    Title,
} from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { useForm } from "@mantine/form";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { notifications } from "@mantine/notifications";
import { modals } from "@mantine/modals";
import api from "./api";

type Sexo = "MASCULINO" | "FEMININO" | "OUTRO";

interface Cliente {
    cpf: string;
    nomeCompleto: string;
    dataNascimento: string;
    sexo: Sexo;
    endereco: string;
    telefone: string;
}

function Cliente() {
    const [modalAberto, { open: abrirModal, close: fecharModal }] =
        useDisclosure(false);

    const queryClient = useQueryClient();

    const [clienteEditando, setClienteEditando] =
        useState<Cliente | null>(null);

    const form = useForm({
        initialValues: {
            cpf: "",
            nomeCompleto: "",
            dataNascimento: "",
            sexo: "" as Sexo | "",
            endereco: "",
            telefone: "",
        },

        validate: {
            cpf: (value) =>
                /^\d{11}$/.test(value)
                    ? null
                    : "CPF deve possuir 11 dígitos",

            nomeCompleto: (value) =>
                value.trim().length >= 3
                    ? null
                    : "Nome deve possuir pelo menos 3 caracteres",

            dataNascimento: (value) =>
                value ? null : "Data de nascimento é obrigatória",

            sexo: (value) =>
                value ? null : "Sexo é obrigatório",

            endereco: (value) =>
                value.trim().length > 0
                    ? null
                    : "Endereço é obrigatório",

            telefone: (value) =>
                value.trim().length > 0
                    ? null
                    : "Telefone é obrigatório",
        },
    });

    const {
        data: clientes,
        isLoading,
        isError,
        error,
    } = useQuery<Cliente[]>({
        queryKey: ["clientes"],
        queryFn: async () => {
            const response = await api.get("/clientes");
            return response.data;
        },
    });

    const cadastrarCliente = async (valores: typeof form.values) => {
        try {
            await api.post("/clientes", {
                cpf: valores.cpf,
                nomeCompleto: valores.nomeCompleto,
                dataNascimento: valores.dataNascimento,
                sexo: valores.sexo,
                endereco: valores.endereco,
                telefone: valores.telefone,
            });

            notifications.show({
                title: "Cliente cadastrado",
                message: "O cliente foi cadastrado com sucesso.",
                color: "green",
            });

            form.reset();
            fecharModal();

            await queryClient.invalidateQueries({
                queryKey: ["clientes"],
            });
        } catch (error: any) {
            const mensagem =
                error.response?.data?.message ||
                error.response?.data ||
                "Não foi possível cadastrar o cliente.";

            notifications.show({
                title: "Erro ao cadastrar cliente",
                message: mensagem,
                color: "red",
            });
        }
    };

    const abrirEdicao = (cliente: Cliente) => {
        setClienteEditando(cliente);

        form.setValues({
            cpf: cliente.cpf,
            nomeCompleto: cliente.nomeCompleto,
            dataNascimento: cliente.dataNascimento,
            sexo: cliente.sexo,
            endereco: cliente.endereco,
            telefone: cliente.telefone,
        });

        abrirModal();
    };

    const atualizarCliente = async (valores: typeof form.values) => {
        if (!clienteEditando) {
            return;
        }

        try {
            await api.put(`/clientes/${clienteEditando.cpf}`, {
                cpf: valores.cpf,
                nomeCompleto: valores.nomeCompleto,
                dataNascimento: valores.dataNascimento,
                sexo: valores.sexo,
                endereco: valores.endereco,
                telefone: valores.telefone,
            });

            notifications.show({
                title: "Cliente atualizado",
                message: "Os dados do cliente foram atualizados com sucesso.",
                color: "green",
            });

            form.reset();
            setClienteEditando(null);
            fecharModal();

            await queryClient.invalidateQueries({
                queryKey: ["clientes"],
            });
        } catch (error: any) {
            const mensagem =
                error.response?.data?.message ||
                error.response?.data ||
                "Não foi possível atualizar o cliente.";

            notifications.show({
                title: "Erro ao atualizar cliente",
                message: mensagem,
                color: "red",
            });
        }
    };

    const excluirCliente = async (cpf: string) => {
        try {
            await api.delete(`/clientes/${cpf}`);

            notifications.show({
                title: "Cliente excluído",
                message: "O cliente foi excluído com sucesso.",
                color: "green",
            });

            await queryClient.invalidateQueries({
                queryKey: ["clientes"],
            });
        } catch (error: any) {
            const mensagem =
                error.response?.data?.message ||
                error.response?.data ||
                "Não foi possível excluir o cliente.";

            notifications.show({
                title: "Erro ao excluir cliente",
                message: mensagem,
                color: "red",
            });
        }
    };

    const confirmarExclusao = (cliente: Cliente) => {
        modals.openConfirmModal({
            title: "Excluir cliente",
            centered: true,
            children: (
                <Text>
                    Tem certeza que deseja excluir o cliente{" "}
                    <strong>{cliente.nomeCompleto}</strong>?
                </Text>
            ),
            labels: {
                confirm: "Excluir",
                cancel: "Cancelar",
            },
            confirmProps: {
                color: "red",
            },
            onConfirm: () => excluirCliente(cliente.cpf),
        });
    };

    const fecharFormulario = () => {
        form.reset();
        setClienteEditando(null);
        fecharModal();
    };

    return (
        <Stack>
            <Title order={1}>Clientes</Title>

            <Text>Gerenciamento de clientes</Text>

            <Button
                onClick={() => {
                    setClienteEditando(null);
                    form.reset();
                    abrirModal();
                }}
            >
                Novo cliente
            </Button>

            <Modal
                opened={modalAberto}
                onClose={fecharFormulario}
                title={
                    clienteEditando
                        ? "Editar cliente"
                        : "Novo cliente"
                }
                centered
            >
                <form
                    onSubmit={form.onSubmit(
                        clienteEditando
                            ? atualizarCliente
                            : cadastrarCliente
                    )}
                >
                    <Stack>
                        <TextInput
                            label="CPF"
                            placeholder="Digite os 11 dígitos do CPF"
                            disabled={clienteEditando !== null}
                            {...form.getInputProps("cpf")}
                        />

                        <TextInput
                            label="Nome completo"
                            placeholder="Digite o nome completo"
                            {...form.getInputProps("nomeCompleto")}
                        />

                        <TextInput
                            label="Data de nascimento"
                            type="date"
                            {...form.getInputProps("dataNascimento")}
                        />

                        <Select
                            label="Sexo"
                            placeholder="Selecione o sexo"
                            data={[
                                {
                                    value: "MASCULINO",
                                    label: "Masculino",
                                },
                                {
                                    value: "FEMININO",
                                    label: "Feminino",
                                },
                                {
                                    value: "OUTRO",
                                    label: "Outro",
                                },
                            ]}
                            {...form.getInputProps("sexo")}
                        />

                        <TextInput
                            label="Endereço"
                            placeholder="Digite o endereço"
                            {...form.getInputProps("endereco")}
                        />

                        <TextInput
                            label="Telefone"
                            placeholder="Digite o telefone"
                            {...form.getInputProps("telefone")}
                        />

                        <Button type="submit">
                            {clienteEditando
                                ? "Salvar alterações"
                                : "Cadastrar"}
                        </Button>
                    </Stack>
                </form>
            </Modal>

            {isLoading && <Text>Carregando clientes...</Text>}

            {isError && (
                <Text c="red">
                    Erro ao carregar clientes: {error.message}
                </Text>
            )}

            {!isLoading && !isError && (
                <Table striped highlightOnHover withTableBorder>
                    <Table.Thead>
                        <Table.Tr>
                            <Table.Th>CPF</Table.Th>
                            <Table.Th>Nome</Table.Th>
                            <Table.Th>Data de nascimento</Table.Th>
                            <Table.Th>Sexo</Table.Th>
                            <Table.Th>Endereço</Table.Th>
                            <Table.Th>Telefone</Table.Th>
                            <Table.Th>Ações</Table.Th>
                        </Table.Tr>
                    </Table.Thead>

                    <Table.Tbody>
                        {clientes?.map((cliente) => (
                            <Table.Tr key={cliente.cpf}>
                                <Table.Td>{cliente.cpf}</Table.Td>
                                <Table.Td>{cliente.nomeCompleto}</Table.Td>
                                <Table.Td>{cliente.dataNascimento}</Table.Td>
                                <Table.Td>{cliente.sexo}</Table.Td>
                                <Table.Td>{cliente.endereco}</Table.Td>
                                <Table.Td>{cliente.telefone}</Table.Td>
                                <Table.Td>
                                    <Stack gap={4}>
                                        <Button
                                            size="xs"
                                            variant="light"
                                            onClick={() => abrirEdicao(cliente)}
                                        >
                                            Editar
                                        </Button>

                                        <Button
                                            size="xs"
                                            variant="light"
                                            color="red"
                                            onClick={() => confirmarExclusao(cliente)}
                                        >
                                            Excluir
                                        </Button>
                                    </Stack>
                                </Table.Td>
                            </Table.Tr>
                        ))}
                    </Table.Tbody>
                </Table>
            )}
        </Stack>
    );
}

export default Cliente;