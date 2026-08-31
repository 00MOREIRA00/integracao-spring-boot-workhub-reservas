# Task 06 - Concorrência e Consistência

## Objetivo

Garantir que reservas conflitantes não sejam confirmadas sob concorrência real.

## Escopo

- Escolher estratégia transacional compatível com PostgreSQL.
- Implementar proteção contra corrida na criação de reservas.
- Tratar o erro de conflito de forma consistente.

## Decisões esperadas

- Definir se a proteção ficará apoiada em lock, restrição de banco ou combinação dos dois.
- Garantir que a solução não dependa apenas de consulta prévia sem proteção.

## Critérios de conclusão

- Em requisições simultâneas sobrepostas, apenas uma é confirmada.
- A outra falha com resposta de conflito.
- A estratégia é explicada de forma curta na documentação.
