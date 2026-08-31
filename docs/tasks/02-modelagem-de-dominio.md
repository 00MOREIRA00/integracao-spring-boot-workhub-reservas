# Task 02 - Modelagem de Domínio

## Objetivo

Definir os conceitos centrais do problema e as regras que pertencem ao domínio.

## Escopo

- Modelar `Room`.
- Modelar `Reservation`.
- Modelar status de reserva.
- Definir invariantes principais.

## Regras a cobrir

- Sala com nome único.
- Reserva somente para sala ativa.
- Janela de operação entre 08:00 e 20:00.
- Duração mínima de 30 minutos e máxima de 4 horas.
- Incrementos de 30 minutos.
- Proibição de sobreposição entre reservas ativas.

## Critérios de conclusão

- As entidades e enums principais estão definidas.
- As regras de negócio estão representadas em tipos e serviços de domínio claros.
- As decisões de modelagem evitam ambiguidade de data, hora e fuso.
