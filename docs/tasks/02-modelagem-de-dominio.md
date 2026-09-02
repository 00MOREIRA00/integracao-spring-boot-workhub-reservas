# Task 02 - Modelagem de Domínio

## Status

Concluída.

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

## Decisões adotadas

- `Room` protege suas invariantes de nome obrigatório e capacidade positiva e oferece comportamentos explícitos para ativação e desativação.
- `Reservation` recebe `OffsetDateTime`, preservando o instante e o deslocamento informados no contrato. As regras de horário comercial são avaliadas após conversão para `America/Sao_Paulo`.
- Reservas são aceitas entre 08:00 e 20:00, inclusive com término exatamente às 20:00, e devem começar e terminar no mesmo dia comercial.
- Início e término devem estar em marcas exatas de 30 minutos, sem segundos ou nanossegundos, e a duração deve permanecer entre 30 minutos e 4 horas.
- Os períodos são tratados como intervalos semiabertos `[início, fim)`. Assim, períodos que apenas se encontram em uma extremidade são adjacentes e não se sobrepõem.
- Uma reserva cancelada permanece no histórico, mas não ocupa mais o período para a verificação de sobreposição.
- A unicidade do nome da sala está declarada no mapeamento JPA. Sua garantia pelo esquema versionado será implementada junto à persistência.
- A entidade define a semântica de sobreposição. A consulta de conflitos e a garantia contra requisições concorrentes serão implementadas nas tasks de criação de reservas e concorrência.

## Evidências de conclusão

- Testes unitários cobrem as invariantes de `Room`, as regras temporais de `Reservation`, sobreposição, adjacência e cancelamento.
- A suíte completa pode ser executada com `./mvnw test` em Linux/macOS ou `.\\mvnw.cmd test` no Windows.
