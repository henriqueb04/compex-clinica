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
    NumberInput,
} from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { useForm } from "@mantine/form";
import { useQuery } from "@tanstack/react-query";
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

function Profissional() {
    const [modalAberto, { open: abrirModal, close: fecharModal }] =
        useDisclosure(false);

    const [profissionalEditando, setProfissionalEditando] =
        useState<Profissional | null>(null);

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
    });

    /*
     * Busca todos os profissionais.
     */
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
     * Fecha o formulário e limpa os dados.
     */
    const fecharFormulario = () => {
        form.reset();
        setProfissionalEditando(null);
        fecharModal();
    };

    return (
        <Stack>
            <Title order={1}>Profissionais</Title>

            <Text>Gerenciamento de profissionais</Text>

            <Button
                onClick={() => {
                    setProfissionalEditando(null);
                    form.reset();
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
                <form>
                    <Stack>
                        <TextInput
                            label="CPF"
                            placeholder="123.456.789-01"
                        />

                        <TextInput
                            label="Nome completo"
                            placeholder="Digite o nome completo"
                        />

                        <TextInput
                            label="Data de nascimento"
                            type="date"
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
                        />

                        <TextInput
                            label="Endereço"
                            placeholder="Digite o endereço"
                        />

                        <TextInput
                            label="Telefone"
                            placeholder="(81) 9 9999-9999"
                        />

                        <TextInput
                            label="CRM"
                            placeholder="Digite o CRM"
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
                        />

                        <NumberInput
                            label="Tempo médio de consulta"
                            placeholder="Digite o tempo em minutos"
                            min={1}
                        />

                        <Button type="submit">
                            {profissionalEditando
                                ? "Salvar alterações"
                                : "Cadastrar"}
                        </Button>
                    </Stack>
                </form>
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
                            <Table.Th>
                                Tempo médio de consulta
                            </Table.Th>
                            <Table.Th>Ações</Table.Th>
                        </Table.Tr>
                    </Table.Thead>

                    <Table.Tbody>
                        {profissionais?.map((profissional) => (
                            <Table.Tr key={profissional.cpf}>
                                <Table.Td>
                                    {profissional.cpf}
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
                                    {profissional.telefone}
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
                                                setProfissionalEditando(
                                                    profissional
                                                )
                                            }
                                        >
                                            Editar
                                        </Button>

                                        <Button
                                            size="xs"
                                            variant="light"
                                            color="red"
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