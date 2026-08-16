import { useState } from "react";
import {
    Button,
    Modal,
    NumberInput,
    Select,
    Stack,
    Table,
    Text,
    TextInput,
    Title,
} from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import {
    useMutation,
    useQuery,
    useQueryClient,
} from "@tanstack/react-query";
import api from "./api";

type Sexo = "MASCULINO" | "FEMININO" | "OUTRO";

type Especialidade =
    | "ESTETICISTA"
    | "BIOMEDICO"
    | "ENFERMEIRO"
    | "FISIOTERAPEUTA"
    | "NUTRICIONISTA"
    | "DERMATOLOGISTA"
    | "CIRURGIAO_PLASTICO";

interface Profissional {
    cpf: string;
    nomeCompleto: string;
    dataNascimento: string;
    sexo: Sexo;
    endereco: string;
    telefone: string;
    crm: string;
    especialidade: Especialidade;
    tempoMedioConsulta: number;
}

const formatarCpf = (valor: string) => {
    const numeros = valor.replace(/\D/g, "").slice(0, 11);

    if (numeros.length <= 3) return numeros;

    if (numeros.length <= 6) {
        return `${numeros.slice(0, 3)}.${numeros.slice(3)}`;
    }

    if (numeros.length <= 9) {
        return `${numeros.slice(0, 3)}.${numeros.slice(3, 6)}.${numeros.slice(6)}`;
    }

    return `${numeros.slice(0, 3)}.${numeros.slice(3, 6)}.${numeros.slice(
        6,
        9,
    )}-${numeros.slice(9)}`;
};

const formatarTelefone = (valor: string) => {
    const numeros = valor.replace(/\D/g, "").slice(0, 11);

    if (numeros.length <= 2) return numeros;

    if (numeros.length <= 3) {
        return `(${numeros.slice(0, 2)}) ${numeros.slice(2)}`;
    }

    if (numeros.length <= 7) {
        return `(${numeros.slice(0, 2)}) ${numeros.slice(2, 3)} ${numeros.slice(
            3,
        )}`;
    }

    return `(${numeros.slice(0, 2)}) ${numeros.slice(2, 3)} ${numeros.slice(
        3,
        7,
    )}-${numeros.slice(7)}`;
};

const validarDataNascimento = (valor: string) => {
    if (!valor) {
        return "Data de nascimento é obrigatória";
    }

    const data = new Date(`${valor}T00:00:00`);

    if (Number.isNaN(data.getTime())) {
        return "Data de nascimento inválida";
    }

    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);

    if (data > hoje) {
        return "Data de nascimento não pode ser futura";
    }

    return null;
};

function Profissional() {
    const [modalAberto, { open: abrirModal, close: fecharModal }] =
        useDisclosure(false);

    const [profissionalEditando, setProfissionalEditando] =
        useState<Profissional | null>(null);

    const queryClient = useQueryClient();

    const form = useForm({
        initialValues: {
            cpf: "",
            nomeCompleto: "",
            dataNascimento: "",
            sexo: "" as Sexo | "",
            endereco: "",
            telefone: "",
            crm: "",
            especialidade: "" as Especialidade | "",
            tempoMedioConsulta: 0,
        },

        validate: {
            cpf: (valor) =>
                valor.replace(/\D/g, "").length !== 11
                    ? "CPF deve conter 11 dígitos"
                    : null,

            nomeCompleto: (valor) =>
                valor.trim().length === 0
                    ? "Nome completo é obrigatório"
                    : null,

            dataNascimento: validarDataNascimento,

            sexo: (valor) =>
                !valor ? "Sexo é obrigatório" : null,

            endereco: (valor) =>
                valor.trim().length === 0
                    ? "Endereço é obrigatório"
                    : null,

            telefone: (valor) =>
                valor.replace(/\D/g, "").length < 10
                    ? "Telefone inválido"
                    : null,

            crm: (valor) =>
                valor.trim().length === 0
                    ? "CRM é obrigatório"
                    : null,

            especialidade: (valor) =>
                !valor ? "Especialidade é obrigatória" : null,

            tempoMedioConsulta: (valor) =>
                !valor || valor <= 0
                    ? "Tempo médio de consulta deve ser maior que zero"
                    : null,
        },
    });

    const {
        data: profissionais,
        isLoading,
        isError,
        error,
    } = useQuery<Profissional[]>({
        queryKey: ["profissionais"],
        queryFn: async () => {
            const response = await api.get("/profissionais");
            return response.data;
        },
    });

    /*
     * Cadastra um novo profissional.
     */
    const cadastrarProfissional = useMutation({
        mutationFn: async () => {
            const valores = form.values;

            const profissional = {
                cpf: valores.cpf.replace(/\D/g, ""),
                nomeCompleto: valores.nomeCompleto,
                dataNascimento: valores.dataNascimento,
                sexo: valores.sexo,
                endereco: valores.endereco,
                telefone: valores.telefone.replace(/\D/g, ""),
                crm: valores.crm,
                especialidade: valores.especialidade,
                tempoMedioConsulta: valores.tempoMedioConsulta,
            };

            return api.post("/profissionais", profissional);
        },

        onSuccess: () => {
            notifications.show({
                title: "Sucesso",
                message: "Profissional cadastrado com sucesso!",
                color: "green",
            });

            form.reset();
            setProfissionalEditando(null);
            fecharModal();

            queryClient.invalidateQueries({
                queryKey: ["profissionais"],
            });
        },

        onError: (error: any) => {
            const mensagem =
                error?.response?.data?.message ||
                error?.response?.data?.erro ||
                "Não foi possível cadastrar o profissional.";

            notifications.show({
                title: "Erro",
                message: mensagem,
                color: "red",
            });
        },
    });

    /*
     * Atualiza um profissional existente.
     */
    const atualizarProfissional = useMutation({
        mutationFn: async () => {
            const valores = form.values;

            const profissional = {
                cpf: valores.cpf.replace(/\D/g, ""),
                nomeCompleto: valores.nomeCompleto,
                dataNascimento: valores.dataNascimento,
                sexo: valores.sexo,
                endereco: valores.endereco,
                telefone: valores.telefone.replace(/\D/g, ""),
                crm: valores.crm,
                especialidade: valores.especialidade,
                tempoMedioConsulta: valores.tempoMedioConsulta,
            };

            return api.put(
                `/profissionais/${profissional.cpf}`,
                profissional,
            );
        },

        onSuccess: () => {
            notifications.show({
                title: "Sucesso",
                message: "Profissional atualizado com sucesso!",
                color: "green",
            });

            form.reset();
            setProfissionalEditando(null);
            fecharModal();

            queryClient.invalidateQueries({
                queryKey: ["profissionais"],
            });
        },

        onError: (error: any) => {
            const mensagem =
                error?.response?.data?.message ||
                error?.response?.data?.erro ||
                "Não foi possível atualizar o profissional.";

            notifications.show({
                title: "Erro",
                message: mensagem,
                color: "red",
            });
        },
    });

    const cadastrar = () => {
        const resultado = form.validate();

        if (!resultado.hasErrors) {
            cadastrarProfissional.mutate();
        }
    };

    const atualizar = () => {
        const resultado = form.validate();

        if (!resultado.hasErrors) {
            atualizarProfissional.mutate();
        }
    };

    /*
     * Abre o formulário para edição.
     */
    const editarProfissional = (profissional: Profissional) => {
        setProfissionalEditando(profissional);

        form.setValues({
            cpf: profissional.cpf,
            nomeCompleto: profissional.nomeCompleto,
            dataNascimento: profissional.dataNascimento,
            sexo: profissional.sexo,
            endereco: profissional.endereco,
            telefone: profissional.telefone,
            crm: profissional.crm,
            especialidade: profissional.especialidade,
            tempoMedioConsulta: profissional.tempoMedioConsulta,
        });

        abrirModal();
    };

    const fecharFormulario = () => {
        form.reset();
        setProfissionalEditando(null);
        fecharModal();
    };

    const salvando =
        cadastrarProfissional.isPending ||
        atualizarProfissional.isPending;

    return (
        <Stack>
            <Title order={1}>Profissionais</Title>

            <Text>Gerenciamento de profissionais</Text>

            <Button
                onClick={() => {
                    form.reset();
                    setProfissionalEditando(null);
                    abrirModal();
                }}
            >
                Novo profissional
            </Button>

            <Modal
                opened={modalAberto}
                onClose={fecharFormulario}
                title={
                    profissionalEditando
                        ? "Editar profissional"
                        : "Novo profissional"
                }
                centered
            >
                <Stack>
                    <TextInput
                        label="CPF"
                        placeholder="000.000.000-00"
                        value={formatarCpf(form.values.cpf)}
                        onChange={(event) => {
                            form.setFieldValue(
                                "cpf",
                                event.currentTarget.value.replace(/\D/g, ""),
                            );
                        }}
                        error={form.errors.cpf}
                        disabled={profissionalEditando !== null}
                        maxLength={14}
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
                        placeholder="(81) 9 9999-9999"
                        value={formatarTelefone(form.values.telefone)}
                        onChange={(event) => {
                            form.setFieldValue(
                                "telefone",
                                event.currentTarget.value.replace(/\D/g, ""),
                            );
                        }}
                        error={form.errors.telefone}
                        maxLength={16}
                    />

                    <TextInput
                        label="CRM"
                        placeholder="Digite o CRM"
                        {...form.getInputProps("crm")}
                        disabled={profissionalEditando !== null}
                    />

                    <Select
                        label="Especialidade"
                        placeholder="Selecione a especialidade"
                        data={[
                            {
                                value: "ESTETICISTA",
                                label: "Esteticista",
                            },
                            {
                                value: "BIOMEDICO",
                                label: "Biomédico",
                            },
                            {
                                value: "ENFERMEIRO",
                                label: "Enfermeiro",
                            },
                            {
                                value: "FISIOTERAPEUTA",
                                label: "Fisioterapeuta",
                            },
                            {
                                value: "NUTRICIONISTA",
                                label: "Nutricionista",
                            },
                            {
                                value: "DERMATOLOGISTA",
                                label: "Dermatologista",
                            },
                            {
                                value: "CIRURGIAO_PLASTICO",
                                label: "Cirurgião Plástico",
                            },
                        ]}
                        {...form.getInputProps("especialidade")}
                    />

                    <NumberInput
                        label="Tempo médio de consulta"
                        placeholder="Digite o tempo em minutos"
                        min={1}
                        {...form.getInputProps("tempoMedioConsulta")}
                    />

                    <Button
                        onClick={
                            profissionalEditando
                                ? atualizar
                                : cadastrar
                        }
                        loading={salvando}
                    >
                        {profissionalEditando
                            ? "Salvar alterações"
                            : "Cadastrar"}
                    </Button>
                </Stack>
            </Modal>

            {isLoading && (
                <Text>Carregando profissionais...</Text>
            )}

            {isError && (
                <Text c="red">
                    Erro ao carregar profissionais: {error.message}
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
                            <Table.Th>CRM</Table.Th>
                            <Table.Th>Especialidade</Table.Th>
                            <Table.Th>Tempo médio</Table.Th>
                            <Table.Th>Ações</Table.Th>
                        </Table.Tr>
                    </Table.Thead>

                    <Table.Tbody>
                        {profissionais?.map((profissional) => (
                            <Table.Tr key={profissional.cpf}>
                                <Table.Td>
                                    {formatarCpf(profissional.cpf)}
                                </Table.Td>

                                <Table.Td>
                                    {profissional.nomeCompleto}
                                </Table.Td>

                                <Table.Td>
                                    {profissional.dataNascimento}
                                </Table.Td>

                                <Table.Td>
                                    {profissional.sexo}
                                </Table.Td>

                                <Table.Td>
                                    {profissional.endereco}
                                </Table.Td>

                                <Table.Td>
                                    {formatarTelefone(profissional.telefone)}
                                </Table.Td>

                                <Table.Td>
                                    {profissional.crm}
                                </Table.Td>

                                <Table.Td>
                                    {profissional.especialidade}
                                </Table.Td>

                                <Table.Td>
                                    {profissional.tempoMedioConsulta} min
                                </Table.Td>

                                <Table.Td>
                                    <Stack gap={4}>
                                        <Button
                                            size="xs"
                                            variant="light"
                                            onClick={() =>
                                                editarProfissional(profissional)
                                            }
                                        >
                                            Editar
                                        </Button>

                                        <Button
                                            size="xs"
                                            variant="light"
                                            color="red"
                                            disabled
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

export default Profissional;