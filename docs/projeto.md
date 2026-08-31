# ROOM-247 — Garantir reservas consistentes de salas compartilhadas

> **Premissas do desafio:** nível Pleno, duração estimada de 4–6 horas, Java 21, Spring Boot 3, implementação prática e PostgreSQL em produção. O banco de desenvolvimento pode ser escolhido pelo desenvolvedor, desde que as diferenças entre ambientes sejam tratadas conscientemente.

## 1. Ticket

`ROOM-247 — Garantir reservas consistentes de salas compartilhadas`

## 2. Contexto da empresa

A WorkHub administra escritórios compartilhados. Seus clientes reservam salas de reunião por períodos de 30 minutos a 4 horas.

Hoje, as reservas são controladas por uma planilha. Com a abertura de uma nova unidade, duas recepcionistas passaram a editar o arquivo simultaneamente, provocando reservas conflitantes.

A empresa decidiu criar um serviço web interno. Nesta primeira fase, ele será usado pela recepção; futuramente, poderá atender um aplicativo de autoatendimento.

## 3. Estado atual

Não existe aplicação anterior nem necessidade de migração de dados.

O sistema corporativo já fornece a identificação do funcionário, mas a autenticação não fará parte desta entrega. Para a aplicação, o solicitante será representado apenas por um identificador textual recebido na requisição.

A infraestrutura de produção executa aplicações como containers e disponibiliza PostgreSQL.

## 4. Problema

Nas últimas quatro semanas ocorreram 11 reservas conflitantes. Em dois casos, reuniões com clientes precisaram ser transferidas após seu início.

A planilha também permite:

- reservar salas inativas;
- criar períodos invertidos ou fora do horário comercial;
- apagar reservas sem registrar o cancelamento;
- cadastrar duas reservas sobrepostas antes que a planilha seja sincronizada.

O volume inicial é pequeno: aproximadamente 300 reservas por dia, com picos de 10 solicitações simultâneas.

## 5. Sua missão

Construir uma aplicação Spring Boot que receba e consulte reservas, aplique as regras do domínio e preserve a consistência quando solicitações concorrentes tentarem ocupar a mesma sala.

A aplicação deverá ser executável localmente e estar preparada para implantação em produção por meio de uma imagem Docker.

## 6. Escopo

### Dentro do escopo

- Cadastro e consulta de salas.
- Criação, consulta e cancelamento de reservas.
- Persistência em banco relacional.
- API HTTP usando Spring MVC.
- Validação e respostas de erro consistentes.
- Testes automatizados.
- Configurações distintas para desenvolvimento e produção.
- Dockerfile para empacotamento da aplicação.
- Documentação de uso e decisões técnicas.

### Fora do escopo

- Interface gráfica.
- Autenticação e autorização.
- Pagamentos.
- Notificações por e-mail.
- Reservas recorrentes.
- Migração da planilha existente.
- Deploy em um provedor de nuvem.

## 7. Requisitos funcionais

- `RF-01` — Cadastrar uma sala com nome único, capacidade e status ativo/inativo.
- `RF-02` — Listar salas, permitindo filtrar por status.
- `RF-03` — Criar uma reserva informando sala, solicitante, início e término.
- `RF-04` — Aceitar reservas somente para salas ativas.
- `RF-05` — Aceitar reservas apenas entre 08:00 e 20:00, no fuso `America/Sao_Paulo`. O horário de início deve ser maior ou igual a 08:00, o horário de término deve ser menor ou igual a 20:00 e `endsAt = 20:00` é permitido.
- `RF-06` — A duração deve ser de no mínimo 30 minutos e no máximo 4 horas.
- `RF-06.1` — `startsAt` e `endsAt` devem respeitar incrementos de 30 minutos. Exemplos válidos: 09:00, 09:30, 10:00. Exemplos inválidos: 09:10, 09:45.
- `RF-07` — Uma sala não pode possuir reservas ativas com períodos sobrepostos.
- `RF-08` — Períodos adjacentes são permitidos. Uma reserva das 10:00 às 11:00 não conflita com outra iniciada às 11:00.
- `RF-09` — Cancelar uma reserva sem removê-la do histórico.
- `RF-10` — Consultar reservas de uma sala dentro de um intervalo.
- `RF-10.1` — A consulta de reservas deve retornar, por padrão, apenas reservas ativas. Reservas canceladas podem ser incluídas por meio de filtro explícito documentado pela implementação.
- `RF-11` — Retornar erros HTTP estruturados para entrada inválida, recurso inexistente e conflito de agenda.
- `RF-12` — Expor um endpoint de verificação de saúde para confirmar que a aplicação está em execução.

## 8. Requisitos não funcionais

- `RNF-01` — O conflito de horário deve continuar sendo impedido quando duas requisições forem processadas simultaneamente.
- `RNF-01.1` — Essa garantia de concorrência deve ser sustentada por mecanismo transacional e/ou restrição no banco de dados compatível com PostgreSQL, não apenas por validação em memória ou por consulta prévia sem proteção contra corrida.
- `RNF-02` — A aplicação não deve depender de estado armazenado somente em memória.
- `RNF-03` — Regras de negócio relevantes devem possuir testes automatizados.
- `RNF-04` — A aplicação deve possuir testes de integração envolvendo a camada HTTP e a persistência.
- `RNF-05` — Logs não devem registrar o corpo completo das requisições.
- `RNF-06` — Configurações sensíveis de produção não podem estar gravadas no repositório ou na imagem.
- `RNF-07` — O container deve executar a aplicação com um usuário não privilegiado.
- `RNF-08` — A documentação deve permitir que outro desenvolvedor execute e teste o projeto sem orientação verbal.

## 9. Restrições

- Use Java 21 e Spring Boot 3.
- A interface HTTP deve usar Spring MVC.
- O banco de produção é PostgreSQL.
- O esquema do banco precisa ser criado de maneira reproduzível e versionada.
- O ambiente `dev` pode oferecer conveniências locais, mas não deve alterar as regras de negócio em relação a `prd`.
- Em `prd`, credenciais e endereço do banco serão fornecidos externamente.
- A imagem de produção não deve conter ferramentas de compilação desnecessárias.
- Não é permitido resolver conflitos apenas consultando o banco e confiando que nenhuma outra requisição gravará simultaneamente.
- Não adicione Redis, mensageria ou outro serviço de infraestrutura nesta entrega.

## 10. Critérios de aceitação

```gherkin
Cenário: criar uma reserva válida
  Dado que a sala "Ipê" está ativa e disponível
  Quando uma reserva é solicitada das 14:00 às 15:00
  Então a reserva é persistida como ativa
  E a resposta informa seu identificador
```

```gherkin
Cenário: aceitar reserva encerrando às 20:00
  Dado que a sala "Ipê" está ativa e disponível
  Quando uma reserva é solicitada das 19:30 às 20:00
  Então a nova reserva é aceita
```

```gherkin
Cenário: rejeitar horário fora do incremento permitido
  Dado que a sala "Ipê" está ativa
  Quando uma reserva é solicitada das 14:10 às 14:40
  Então a solicitação é recusada por entrada inválida
```

```gherkin
Cenário: permitir períodos adjacentes
  Dado que existe uma reserva ativa das 10:00 às 11:00
  Quando outra reserva para a mesma sala é solicitada das 11:00 às 12:00
  Então a nova reserva é aceita
```

```gherkin
Cenário: impedir sobreposição
  Dado que existe uma reserva ativa das 10:00 às 11:30
  Quando outra reserva para a mesma sala é solicitada das 11:00 às 12:00
  Então a nova reserva é recusada como conflito
  E nenhuma reserva adicional é persistida
```

```gherkin
Cenário: solicitações concorrentes
  Dado que uma sala está disponível das 15:00 às 16:00
  Quando duas solicitações sobrepostas são processadas simultaneamente
  Então somente uma delas é confirmada
  E a outra recebe uma resposta de conflito
```

```gherkin
Cenário: reutilizar horário cancelado
  Dado que uma reserva das 09:00 às 10:00 foi cancelada
  Quando outra reserva solicita esse mesmo período
  Então a nova reserva é aceita
  E a reserva anterior permanece disponível no histórico como cancelada
```

```gherkin
Cenário: consulta padrão não retorna canceladas
  Dado que existe uma reserva ativa e uma reserva cancelada no intervalo consultado
  Quando as reservas da sala são consultadas sem filtro adicional
  Então apenas a reserva ativa é retornada
```

```gherkin
Cenário: configuração de produção incompleta
  Dado que a aplicação é iniciada no ambiente prd
  Quando as configurações obrigatórias do banco não estão disponíveis
  Então a inicialização falha explicitamente
  E nenhuma credencial padrão insegura é utilizada
```

```gherkin
Cenário: endpoint de saúde disponível
  Dado que a aplicação foi iniciada com sucesso
  Quando o endpoint de saúde é consultado
  Então a resposta indica que a aplicação está operacional
```

## 11. Dados e contratos disponíveis

Modelo inicial de sala:

```text
Room
- id: UUID
- name: String
- capacity: Integer
- active: Boolean
```

Modelo inicial de reserva:

```text
Reservation
- id: UUID
- roomId: UUID
- requesterId: String
- startsAt: data e hora
- endsAt: data e hora
- status: ACTIVE | CANCELLED
- createdAt: instante
- cancelledAt: instante opcional
```

Exemplo de criação:

```http
POST /api/reservations
Content-Type: application/json
```

```json
{
  "roomId": "27a76785-f021-4327-af82-f054771cd79c",
  "requesterId": "employee-193",
  "startsAt": "2026-08-20T14:00:00-03:00",
  "endsAt": "2026-08-20T15:00:00-03:00"
}
```

Formato mínimo esperado para erros:

```json
{
  "code": "RESERVATION_CONFLICT",
  "message": "A sala não está disponível no período solicitado.",
  "timestamp": "2026-08-17T16:42:10Z"
}
```

Você pode alterar os modelos e contratos se documentar a justificativa e preservar os comportamentos exigidos.

## 12. Definition of Done

- Projeto compilando e executando.
- API implementada com Spring Boot e Spring MVC.
- Persistência relacional com evolução versionada do esquema.
- Separação explícita entre configurações `dev` e `prd`.
- Testes unitários das principais regras.
- Testes de integração da API, persistência e concorrência.
- Dockerfile funcional e adequado para produção.
- Endpoint de verificação de saúde.
- `README.md` contendo:
    - pré-requisitos;
    - execução local;
    - execução dos testes;
    - construção e execução da imagem;
    - variáveis exigidas em produção;
    - exemplos de requisição;
    - decisões técnicas e trade-offs.
- Nenhuma senha real ou credencial padrão de produção versionada.

## 13. Entrega esperada do usuário

Implemente o projeto e depois envie:

- a árvore principal de arquivos;
- os arquivos ou trechos centrais da implementação;
- os testes;
- o Dockerfile;
- as configurações de ambiente;
- o README;
- uma explicação curta de como você impediu reservas concorrentes;
- os comandos e resultados dos testes.

A solução será revisada como por um engenheiro sênior, avaliando correção, modelagem, concorrência, testes, segurança e operabilidade.

## 14. Perguntas que o engenheiro deveria considerar

1. Em qual camada cada validação deve existir e quais garantias também precisam sobreviver fora do fluxo normal da aplicação?
2. Como demonstrar em um teste automatizado que duas requisições simultâneas não confirmam reservas conflitantes?
3. Quais diferenças entre `dev` e `prd` são apenas operacionais, e quais poderiam mascarar problemas?
4. Como representar data, hora e fuso sem tornar a regra de horário comercial ambígua?
5. Quais informações uma resposta de erro precisa expor sem revelar detalhes internos?
