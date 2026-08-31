# Task 08 - Configurações e Ambientes

## Objetivo

Separar com clareza as configurações de desenvolvimento e produção.

## Escopo

- Configurar perfil `dev`.
- Configurar perfil `prd`.
- Externalizar credenciais e endereço do banco em produção.
- Garantir falha explícita quando configuração obrigatória de produção estiver ausente.

## Entregáveis

- Arquivos de configuração por ambiente.
- Conveniências locais sem alterar regra de negócio.
- Documentação das variáveis obrigatórias.

## Critérios de conclusão

- O ambiente `dev` é simples de subir localmente.
- O ambiente `prd` não embute credenciais no repositório.
- A inicialização em produção falha de forma explícita se faltar configuração obrigatória.
