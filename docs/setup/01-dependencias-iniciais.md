# Dependências Iniciais do Projeto

## Contexto

Estas são as dependências recomendadas para a criação inicial do projeto no Spring Initializr, alinhadas à `Task 01 - Bootstrap do Projeto`.

## Dependências recomendadas

- `Spring Web`
  Motivo: implementar a API HTTP com Spring MVC, conforme exigido no enunciado.

- `Spring Data JPA`
  Motivo: persistir `Room` e `Reservation` em banco relacional com suporte a repositórios e transações.

- `Validation`
  Motivo: validar payloads de entrada e rejeitar entradas inválidas já na camada HTTP.

- `PostgreSQL Driver`
  Motivo: o banco de produção será PostgreSQL, então a aplicação já deve nascer compatível com esse ambiente.

- `Flyway Migration`
  Motivo: versionar e reproduzir a criação e evolução do esquema do banco.

- `Spring Boot Actuator`
  Motivo: expor endpoint de health check, que faz parte da entrega esperada.

- `Spring Boot Starter Test`
  Motivo: fornecer a base de testes unitários e de integração com suporte do ecossistema Spring.

## Dependências opcionais

- `Lombok`
  Motivo: reduzir código repetitivo em DTOs e classes simples. Pode ser evitado se o projeto preferir explicitude.

- `Spring Boot DevTools`
  Motivo: melhorar a experiência de desenvolvimento local com reinicialização mais rápida.

- `H2 Database`
  Motivo: facilitar o perfil `dev` sem depender de PostgreSQL local. Se a prioridade for máxima proximidade com produção, esse item pode ser omitido.

## Dependências que não devem entrar agora

- `Spring Security`
  Motivo: autenticação e autorização estão fora do escopo desta entrega.

- `Redis`, mensageria ou cache distribuído
  Motivo: o enunciado proíbe adicionar infraestrutura extra para resolver concorrência.

- `Spring Data JDBC`
  Motivo: `Spring Data JPA` já cobre melhor a necessidade atual de persistência relacional com transações.

## Conjunto mínimo recomendado

- `Spring Web`
- `Spring Data JPA`
- `Validation`
- `PostgreSQL Driver`
- `Flyway Migration`
- `Spring Boot Actuator`
- `Spring Boot Starter Test`
