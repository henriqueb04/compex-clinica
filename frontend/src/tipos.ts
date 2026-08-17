export const DayOfWeek = [
  "SUNDAY",
  "MONDAY",
  "TUESDAY",
  "WEDNESDAY",
  "THURSDAY",
  "FRIDAY",
  "SATURDAY",
];

export const PessoaSexo = ["MASCULINO", "FEMININO", "OUTRO"];

export const StatusAgendamento = ["AGENDADO", "CANCELADO", "CONCLUIDO"];

export interface Horario {
  id: number | undefined;
  ano: number;
  diaSemana: (typeof DayOfWeek)[keyof typeof DayOfWeek];
  numeroSemana: number;
  comeco: string;
  fim: string;
  profissional_cpf: string;
}

export interface Agendamento {
  id?: number;
  clienteCpf?: string;
  clienteNome?: string;
  profissionalCpf: string;
  profissionalNome?: string;
  comeco: string;
  fim: string;
  statusAgendamento: (typeof PessoaSexo)[keyof typeof PessoaSexo];
}

export interface Cliente {
  cpf: string;
  nomeCompleto: string;
  dataNascimento: string;
  sexo: (typeof PessoaSexo)[keyof typeof PessoaSexo];
}

export interface Profissional {
  cpf: string;
  nomeCompleto: string;
  dataNascimento: string;
  sexo: string;
  endereco: string;
  crm: string;
  especialidade: string;
  tempoMedioConsulta: number;
}
