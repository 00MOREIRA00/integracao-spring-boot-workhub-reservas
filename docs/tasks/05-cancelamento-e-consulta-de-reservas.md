# Task 05 - Cancelamento e Consulta de Reservas

## Objetivo

Cobrir operações de leitura e cancelamento sem perda de histórico.

## Escopo

- Criar endpoint de cancelamento.
- Garantir cancelamento lógico.
- Criar consulta por sala e intervalo.
- Definir filtro opcional para incluir canceladas.

## Entregáveis

- Reserva cancelada com `status = CANCELLED`.
- Registro de `cancelledAt`.
- Consulta retornando apenas ativas por padrão.

## Critérios de conclusão

- Cancelar não remove dados do banco.
- Horário cancelado pode ser reutilizado.
- Consulta por intervalo respeita o comportamento documentado.
