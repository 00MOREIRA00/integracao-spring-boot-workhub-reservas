# Task 01 - Bootstrap do Projeto

## Status

Concluída.

## Objetivo

Preparar a base da aplicação Spring Boot 3 com Java 21 para desenvolvimento local e evolução incremental.

## Escopo

- Criar o projeto base com Spring MVC.
- Definir estrutura inicial de pacotes.
- Configurar build, dependências e plugins.
- Adicionar actuator para health check.

## Entregáveis

- Aplicação sobe localmente.
- Endpoint de health disponível.
- Estrutura mínima de projeto pronta para evoluir.

## Critérios de conclusão

- O projeto compila sem erros.
- Existe um ponto de entrada principal da aplicação.
- As dependências escolhidas suportam persistência relacional, validação e testes.

## Decisões adotadas

- O projeto utiliza Spring Boot `4.1.1`. Esta versão foi escolhida conscientemente no lugar do Spring Boot 3 mencionado na premissa original do desafio.
- O código é compilado com compatibilidade para Java 21 por meio da propriedade `java.version` do Maven.
- O Lombok será mantido para reduzir código repetitivo nas classes em que seu uso trouxer benefício, sem tornar implícitas as regras de negócio.
- O pacote-base da aplicação é `br.com.rneto.workhub.reservation`.
- O Maven Wrapper é o meio recomendado para executar o build de forma reproduzível.

## Evidências de conclusão

- `./mvnw test`: build concluído com sucesso e teste de contexto aprovado.
- A aplicação inicia localmente com o servidor HTTP na porta 8080.
- `GET /actuator/health`: retorna status `UP`, com os grupos `liveness` e `readiness` disponíveis.
