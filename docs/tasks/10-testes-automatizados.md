# Task 10 - Testes Automatizados

## Objetivo

Cobrir as regras principais do domínio e os fluxos críticos da API.

## Escopo

- Testes unitários das regras de reserva.
- Testes de integração da camada HTTP.
- Testes de persistência.
- Teste de concorrência com requisições simultâneas.

## Casos mínimos

- Criar reserva válida.
- Rejeitar sobreposição.
- Permitir adjacência.
- Reutilizar horário cancelado.
- Rejeitar horário fora do incremento.
- Garantir conflito sob concorrência.

## Critérios de conclusão

- Os testes executam localmente.
- A cobertura das regras centrais é suficiente para revisão técnica.
- Existe evidência automatizada da estratégia de concorrência.
