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
