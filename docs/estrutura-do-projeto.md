# Estrutura do Projeto

## Decisão arquitetural

O projeto utiliza uma arquitetura em camadas, com os arquivos agrupados por responsabilidade técnica. Essa organização foi escolhida por ser simples, conhecida no ecossistema Spring e adequada ao tamanho e ao prazo do projeto.

O pacote-base da aplicação é:

```text
br.com.rneto.workhub.reservation
```

## Estrutura planejada

```text
integracao-spring-boot-workhub-reservas/
├── docs/
│   ├── estrutura-do-projeto.md
│   ├── projeto.md
│   ├── setup/
│   └── tasks/
├── src/
│   ├── main/
│   │   ├── java/br/com/rneto/workhub/reservation/
│   │   │   ├── WorkhubReservationApplication.java
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   │   ├── reservation/
│   │   │   │   └── room/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── db/migration/
│   │       ├── application.yaml
│   │       ├── application-dev.yaml
│   │       └── application-prd.yaml
│   └── test/
│       └── java/br/com/rneto/workhub/reservation/
│           ├── controller/
│           ├── entity/
│           ├── repository/
│           └── service/
├── Dockerfile
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

Essa é a estrutura esperada ao final do projeto. Pacotes e arquivos devem ser criados conforme forem necessários; não é preciso criar diretórios vazios antecipadamente.

## Responsabilidades

### Classe principal

`WorkhubReservationApplication.java` é o ponto de entrada da aplicação. Ela inicializa o contexto do Spring Boot e deve permanecer no pacote-base para que o Spring encontre automaticamente os componentes nos subpacotes.

### `entity`

Contém as entidades persistidas e os conceitos centrais do negócio.

Arquivos esperados:

```text
entity/
├── Reservation.java
├── ReservationStatus.java
└── Room.java
```

- `Room` representa uma sala, incluindo seu identificador, nome, capacidade e situação ativa ou inativa.
- `Reservation` representa a reserva de uma sala por um solicitante durante um período.
- `ReservationStatus` limita os estados possíveis de uma reserva, inicialmente `ACTIVE` e `CANCELLED`.

As entidades podem proteger invariantes próprias e oferecer comportamentos de domínio. Por exemplo, uma reserva pode saber como realizar seu cancelamento e como verificar a sobreposição com outro período. Regras que dependem do banco de dados ou de outras entidades persistidas devem ser coordenadas pelo serviço.

### `repository`

Contém as interfaces responsáveis pelo acesso aos dados. Os repositories utilizam Spring Data JPA para consultar e persistir entidades.

Arquivos esperados:

```text
repository/
├── ReservationRepository.java
└── RoomRepository.java
```

Essa camada não deve conter regras de negócio. Sua responsabilidade é expressar operações de persistência e consultas necessárias pelos serviços.

### `service`

Contém os casos de uso e coordena as regras que envolvem diferentes objetos ou acesso a recursos externos.

Arquivos esperados:

```text
service/
├── ReservationService.java
└── RoomService.java
```

Exemplos de responsabilidades:

- Localizar uma sala antes de criar uma reserva.
- Verificar se a sala está ativa.
- Consultar a disponibilidade do período.
- Definir os limites transacionais.
- Persistir alterações por meio dos repositories.

Um service não deve depender de controller ou de detalhes do protocolo HTTP.

### `controller`

Contém os endpoints HTTP da aplicação.

Arquivos esperados:

```text
controller/
├── ReservationController.java
└── RoomController.java
```

O controller deve:

- Receber e validar os dados da requisição.
- Chamar o service correspondente.
- Converter o resultado em uma resposta HTTP.

O controller deve permanecer enxuto. Regras de negócio e acesso direto ao banco não devem ficar nessa camada.

### `dto`

Contém os contratos de entrada e saída da API. Os DTOs evitam expor diretamente as entidades JPA no protocolo HTTP.

Estrutura esperada:

```text
dto/
├── reservation/
│   ├── CreateReservationRequest.java
│   └── ReservationResponse.java
└── room/
    ├── CreateRoomRequest.java
    └── RoomResponse.java
```

- Classes terminadas em `Request` representam dados recebidos pela API.
- Classes terminadas em `Response` representam dados devolvidos pela API.
- Validações de formato e presença, como `@NotBlank` e `@NotNull`, podem ser aplicadas aos DTOs de entrada.

### `exception`

Centraliza as exceções da aplicação e sua conversão para respostas HTTP estruturadas.

Arquivos possíveis:

```text
exception/
├── ApiError.java
├── GlobalExceptionHandler.java
├── ReservationConflictException.java
└── ResourceNotFoundException.java
```

- As exceções representam falhas conhecidas do negócio ou da aplicação.
- `GlobalExceptionHandler` converte essas falhas para códigos e corpos HTTP consistentes.
- Detalhes internos, stack traces e informações sensíveis não devem ser expostos ao cliente.

### `config`

Contém configurações Java específicas do Spring, quando necessárias. Esse pacote não deve ser criado apenas por antecipação.

Exemplos futuros incluem configuração de relógio, serialização de datas ou propriedades tipadas da aplicação.

### `resources/db/migration`

Contém as migrations versionadas do Flyway.

Exemplo:

```text
db/migration/
├── V1__create_rooms_table.sql
└── V2__create_reservations_table.sql
```

As migrations são responsáveis por criar e evoluir o esquema de maneira reproduzível. Alterações já executadas não devem ser editadas; uma nova alteração deve gerar uma nova migration.

### Arquivos de configuração

- `application.yaml` contém configurações compartilhadas.
- `application-dev.yaml` contém conveniências do ambiente de desenvolvimento.
- `application-prd.yaml` contém a configuração de produção e referencia valores sensíveis fornecidos externamente.

As diferenças entre ambientes devem ser operacionais e não podem alterar as regras de negócio.

### Testes

Os testes seguem, quando fizer sentido, a mesma organização do código principal:

```text
src/test/java/br/com/rneto/workhub/reservation/
├── controller/
├── entity/
├── repository/
└── service/
```

- `entity`: testes unitários das invariantes do domínio.
- `service`: testes dos casos de uso e da coordenação entre dependências.
- `repository`: testes de consultas e persistência.
- `controller`: testes de integração dos contratos HTTP.

## Fluxo entre as camadas

O fluxo normal de uma requisição é:

```text
Requisição HTTP
    → Controller
    → Service
    → Repository
    → Banco de dados
```

A resposta percorre o caminho inverso, sendo convertida para um DTO antes de ser devolvida ao cliente.

Dependências no sentido contrário devem ser evitadas. Por exemplo, um repository não deve chamar um service, e uma entidade não deve conhecer um controller.

## Escopo da Task 02

Durante a modelagem inicial do domínio, apenas a seguinte parte precisa ser criada:

```text
src/main/java/br/com/rneto/workhub/reservation/entity/
├── Reservation.java
├── ReservationStatus.java
└── Room.java
```

Os demais pacotes serão adicionados gradualmente nas tasks seguintes.
