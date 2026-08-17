## Compex Clínica — Sistema de Agendamento
Sistema web para gestão de agendamentos de uma clínica de estética fictícia, desenvolvido como desafio prático da segunda fase do processo seletivo do COMPEX (UFPI).

O sistema permite cadastrar clientes e profissionais, definir os horários de atendimento disponíveis, agendar e cancelar horários, e visualizar os próximos agendamentos — impedindo que dois atendimentos sejam marcados em conflito de horário para o mesmo profissional.

## Sumário
- [Objetivo](#objetivo)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Arquitetura do projeto](#arquitetura-do-projeto)
- [Funcionalidades implementadas](#funcionalidades-implementadas)
- [Modelo de dados](#modelo-de-dados)
- [Instruções para execução](#instruções-para-execução)
- [Endpoints da API](#endpoints-da-api)
- [Fluxo de trabalho da equipe](#fluxo-de-trabalho-da-equipe)
- [Principais dificuldades encontradas](#principais-dificuldades-encontradas)
- [Equipe](#equipe)

## Objetivo

Substituir o controle manual de horários da clínica — feito hoje em planilhas ou agendas físicas — por um sistema que organize clientes, profissionais, horários disponíveis e agendamentos em um único lugar, evitando conflitos de horário, esquecimentos e falta de visibilidade sobre os próximos atendimentos.

## Tecnologias utilizadas
 
**Backend**
- Java 25
- Spring Boot 4 (Web MVC, Spring Data JPA, Bean Validation)
- PostgreSQL (com o tipo nativo `tstzrange`, via [Hypersistence Utils](https://github.com/vladmihalcea/hypersistence-utils), para representar intervalos de tempo)
- Lombok
- Maven (com Maven Wrapper)
- JUnit 5 / Spring Boot Test

**Frontend**
- React 19 + TypeScript
- Vite
- Mantine (`core`, `dates`, `form`, `hooks`, `modals`, `notifications`, `schedule`) para UI e para a visualização semanal de horários
- TanStack Query para cache e sincronização de dados com a API
- Axios para requisições HTTP
- React Router
- Day.js para manipulação de datas
- pnpm como gerenciador de pacotes

## Arquitetura do projeto
 
```
compex-clinica/
├── backend/                 # API REST em Spring Boot
│   └── src/main/java/com/compex/grupo5/
│       ├── controller/       # Endpoints REST
│       ├── service/          # Regras de negócio e validações
│       ├── dao/              # Repositórios (Spring Data JPA)
│       ├── dto/              # Objetos de transporte entre API e frontend
│       ├── model/            # Entidades JPA
│       ├── exception/        # Exceções de negócio e tratamento global
│       ├── misc/             # Enums de domínio
│       └── config/           # Configurações (CORS, etc.)
└── frontend/                 # Aplicação React (SPA)
    └── src/
        ├── Cliente.tsx        # Cadastro/gestão de clientes
        ├── Profissional.tsx   # Cadastro/gestão de profissionais
        ├── Horarios.tsx       # Definição dos horários disponíveis (visão semanal)
        ├── Agendamentos.tsx   # Marcação de agendamentos (visão semanal de slots livres/ocupados)
        ├── Listagem.tsx       # Listagem dos próximos agendamentos
        └── components/        # Componentes reutilizáveis (seletor de profissional, de horário, campo de CPF, etc.)
```
 
A comunicação entre frontend e backend é feita via REST/JSON, com o frontend consumindo a API em `http://localhost:8080` (configurado em `frontend/src/api.ts`) e o backend liberando CORS para o frontend local (`backend/.../config/CorsConfig.java`).

## Funcionalidades implementadas
 
O sistema é capaz de:
 
**Gestão de clientes**
- Cadastrar novos clientes, com validação dos dados informados;
- Listar todos os clientes cadastrados;
- Atualizar os dados de um cliente já cadastrado;
- Excluir um cliente.

**Gestão de profissionais**
- Cadastrar profissionais, definindo especialidade e o tempo médio de duração de cada consulta;
- Listar todos os profissionais cadastrados;
- Atualizar os dados de um profissional já cadastrado;
- Excluir um profissional.

**Definição de horários de atendimento**
- Cadastrar os horários em que cada profissional está disponível para atender, em uma visão semanal;
- Remover horários de atendimento já cadastrados;
- Impedir, automaticamente, o cadastro de dois horários de atendimento que se sobreponham para o mesmo profissional.

**Marcação de agendamentos**
- Visualizar, para um profissional e uma semana escolhidos, quais horários estão livres e quais já estão ocupados, com os horários livres já divididos em intervalos de acordo com o tempo médio de consulta do profissional;
- Marcar um novo agendamento para um cliente em um desses horários livres;
- Impedir automaticamente que um agendamento seja marcado fora dos horários de atendimento definidos, com duração diferente da consulta do profissional, ou em conflito com outro agendamento já existente para o mesmo horário.

**Cancelamento de agendamentos**
- Cancelar um agendamento existente, sem apagar o histórico — o registro permanece salvo, apenas com o status atualizado para cancelado.

**Consulta e acompanhamento**
- Consultar todos os agendamentos (passados e futuros) de um cliente específico, através da API (`GET /agendamentos/cliente/{cpf}`);
- Consultar todos os agendamentos de um profissional específico, através da API (`GET /agendamentos/profissional/{cpf}`);
- O backend já expõe a listagem dos próximos agendamentos ativos, em ordem cronológica (`GET /agendamentos`) — porém a tela de listagem do frontend (`Listagem.tsx`) ainda chama uma rota (`/listagem`) que não existe na API, então essa tela não funciona no estado atual (ver aviso na seção de [Endpoints da API](#endpoints-da-api)).

**Confiabilidade dos dados**
- Validação dos dados enviados em todos os formulários, com mensagens de erro claras quando alguma informação obrigatória está ausente ou inválida;
- Respostas de erro padronizadas em toda a API (ex.: cliente ou profissional não encontrado, conflito de horário, agendamento fora do horário de atendimento), facilitando o tratamento desses casos pelo frontend.

## Modelo de dados
 
As principais entidades do sistema são:
 
- **Cliente** — dados pessoais do cliente (CPF como identificador de negócio, nome, contato, sexo).
- **Profissional** — dados do profissional que realiza os atendimentos, com especialidade (`Especialidade`) e tempo médio de consulta em minutos.
- **HorarioDisponivel** — intervalo de tempo (`tstzrange`) em que um profissional está disponível para atendimento em uma semana específica.
- **Agendamento** — vincula um `Cliente` a um `Profissional` em um intervalo de tempo (`tstzrange`), com um `StatusAgendamento` (`AGENDADO`, `CANCELADO`, `CONCLUIDO`).

Ao marcar um agendamento, o backend valida, nessa ordem: (1) se o cliente e o profissional existem; (2) se a duração do intervalo corresponde ao tempo médio de consulta do profissional; (3) se o intervalo está totalmente contido em um horário de atendimento cadastrado; (4) se o horário de início está alinhado com os slots gerados a partir desse horário de atendimento; e (5) se não há conflito com nenhum agendamento já existente para aquele profissional. O uso do tipo de intervalo nativo do PostgreSQL (`tstzrange`) permite que parte dessas checagens de sobreposição sejam feitas pelo próprio banco de dados, reduzindo a chance de condições de corrida.

## Instruções para execução
 
### Pré-requisitos
 
- JDK 25
- PostgreSQL em execução localmente
- Node.js 20+ e [pnpm](https://pnpm.io/)
### 1. Banco de dados
 
Crie um banco e um usuário PostgreSQL compatíveis com a configuração de `backend/src/main/resources/application.properties`:
 
```sql
CREATE DATABASE "compex-clinica";
CREATE USER "compex-clinica" WITH PASSWORD '<defina uma senha>';
GRANT ALL PRIVILEGES ON DATABASE "compex-clinica" TO "compex-clinica";
```
 
> Ajuste `spring.datasource.username` e `spring.datasource.password` em `application.properties` (ou substitua por variáveis de ambiente) conforme as credenciais do seu ambiente. O Hibernate está configurado com `ddl-auto=update`, então as tabelas são criadas/atualizadas automaticamente na primeira execução.
 
### 2. Backend
 
```bash
cd backend
./mvnw spring-boot:run
```
 
A API sobe por padrão em `http://localhost:8080`.
 
Para rodar os testes automatizados:
 
```bash
./mvnw test
```
 
### 3. Frontend
 
```bash
cd frontend
pnpm install
pnpm dev
```
 
A aplicação fica disponível em `http://localhost:5173` (porta padrão do Vite) e já está configurada para consumir a API em `http://localhost:8080`.
 
## Endpoints da API
 
| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/clientes` | Cadastra um novo cliente |
| `GET` | `/clientes` | Lista todos os clientes |
| `GET` | `/clientes/{cpf}` | Busca um cliente pelo CPF |
| `PUT` | `/clientes/{cpf}` | Atualiza um cliente |
| `DELETE` | `/clientes/{cpf}` | Exclui um cliente |
| `POST` | `/profissionais` | Cadastra um novo profissional |
| `GET` | `/profissionais` | Lista todos os profissionais |
| `GET` | `/profissionais/{cpf}` | Busca um profissional pelo CPF |
| `PUT` | `/profissionais/{cpf}` | Atualiza um profissional |
| `DELETE` | `/profissionais/{cpf}` | Exclui um profissional |
| `POST` | `/api/horario/salvar` | Cria/remove horários disponíveis de profissionais, validando sobreposição |
| `GET` | `/api/horario/profissional/{cpf}?ano=&numeroSemana=` | Lista os horários disponíveis de um profissional em uma semana específica |
| `GET` | `/agendamentos` | Lista os próximos agendamentos com status `AGENDADO` |
| `GET` | `/agendamentos/cliente/{cpf}` | Lista os agendamentos de um cliente |
| `GET` | `/agendamentos/profissional/{cpf}` | Lista os agendamentos de um profissional |
| `GET` | `/agendamentos/profissional/{cpf}/semana?ano=&numeroSemana=` | Lista os slots livres e os já agendados de um profissional em uma semana específica |
| `POST` | `/agendamentos/marcar` | Marca um novo agendamento para um cliente em um horário disponível |
| `PATCH` | `/agendamentos/{id}/cancelar` | Cancela um agendamento (status → `CANCELADO`) |
 
> A tela de listagem (`Listagem.tsx`) atualmente consome a rota `GET /listagem`, que ainda não existe no backend (a rota correta é `GET /agendamentos`). Vale ajustar essa chamada antes da entrega final para que a tela funcione corretamente.

## Fluxo de trabalho da equipe
 
- O planejamento e o acompanhamento das tarefas foram feitos em um **quadro Trello** ([acessar quadro](https://trello.com/invite/b/6a75372152e8b8ae603b0758/ATTIe5780ac09c9bffe0d34e357344afdc68AD659F19/compex-projeto)), com cards representando cada funcionalidade e seu responsável, permitindo visualizar o andamento do trabalho de cada integrante ao longo do desafio.
- Os identificadores de tarefa usados nos títulos dos pull requests (ex.: `CLI-01`, `PRO-02`, `HOR-03`, `LIS-01`) correspondem diretamente aos cards do Trello, ligando o planejamento à implementação.
- Cada funcionalidade foi desenvolvida em uma branch própria e integrada via **pull request** no repositório principal.
- Os pull requests foram abertos e revisados por integrantes diferentes da equipe.
- Commits foram feitos de forma incremental, acompanhando o progresso de cada tarefa.

## Principais dificuldades encontradas
 
- Modelar corretamente a validação de conflito de horários, garantindo que a checagem de sobreposição fosse consistente tanto no cadastro de horários disponíveis quanto no agendamento, e decidir em qual camada (banco de dados vs. aplicação) essa regra deveria viver.
- Trabalhar com o tipo de intervalo `tstzrange` do PostgreSQL a partir do JPA/Hibernate, o que exigiu uma biblioteca adicional (Hypersistence Utils) e consultas nativas em alguns pontos.
- Coordenar o desenvolvimento em paralelo de backend e frontend por integrantes diferentes, mantendo os contratos da API (DTOs) sincronizados entre as duas pontas.
- Organizar branches, revisões de pull request e distribuição de tarefas entre os três integrantes de forma equilibrada ao longo do prazo do desafio.
## Equipe
 
| Integrante | GitHub |
|---|---|
| José Henrique Brito Oliveira | [@henriqueb04](https://github.com/henriqueb04) |
| José Nelson Fernandes da Silva | [@josenelson2](https://github.com/josenelson2) |
| José Felipe Lopes da Paz | [@josef-lp](https://github.com/josef-lp) |
 
---
 
Projeto desenvolvido para a segunda fase do processo seletivo do **COMPEX**.
