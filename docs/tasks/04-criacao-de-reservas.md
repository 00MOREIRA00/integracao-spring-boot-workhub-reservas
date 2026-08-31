# Task 04 - Criação de Reservas

## Objetivo

Implementar o fluxo principal de criação de reservas com validações funcionais.

## Escopo

- Criar endpoint `POST /api/reservations`.
- Validar existência da sala.
- Validar status ativo da sala.
- Validar horário comercial.
- Validar duração e incremento de 30 minutos.

## Entregáveis

- DTOs de entrada e saída.
- Serviço de aplicação para criação de reserva.
- Persistência da reserva ativa.

## Critérios de conclusão

- Reservas válidas são persistidas.
- Entradas inválidas retornam erro estruturado.
- Períodos adjacentes continuam permitidos.
