# Migrations 

Em desenvolvimento de software, migrations são arquivos/scripts usados para alterar a estrutura de um banco de dados de forma controlada e versionada.

Por exemplo, imagine que sua aplicação começou com esta tabela:
````commandline
usuarios
    - id
    - nome 
    - email
````

Depois disso decidimos acicionar a coluna `data_nascimento`. Em vez de alterar o banco de dados manualmente, cria uma migration como:
````sql
ALTER TABLE usuarios
ADD COLUMN data_nascimento DATE;
````

A migration fica armazenada no projeto. Assim, quando outro desenvolvedor baixar o código ou quando a aplicação for publicada em produção, basta executar as migrations pendentres para deixar o banco de dados atualizado.

## Flayway
O Flyway é uma ferramenta de migrations para bancos de dados relacionais. Ele permite versionar e aplicar alterações no esquema do banco de dados de forma controlada, garantindo que todos os ambientes (desenvolvimento, teste, produção) estejam sincronizados.

Imagine um projeto Spring Boot com Flyway. As migrations são armazenadas em arquivos SQL dentro do diretório `src/main/resources/db/migration`. Cada arquivo de migration segue uma convenção de nomenclatura que inclui um prefixo de versão, como `V1__create_users_table.sql`, `V2__add_birthdate_column.sql`, etc.
````commandline
src/
└── main/
    └── resources/
        └── db/
            └── migration/
                ├── V1__create_users_table.sql
                ├── V2__add_phone_to_users.sql
                └── V3__create_orders_table.sql
````