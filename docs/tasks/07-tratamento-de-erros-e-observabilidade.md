# Task 07 - Tratamento de Erros e Observabilidade

## Objetivo

Padronizar respostas de erro e adicionar capacidade mínima de operação.

## Escopo

- Criar modelo de erro HTTP.
- Mapear erros de validação, não encontrado e conflito.
- Configurar logs sem expor corpo completo de requisição.
- Expor endpoint de saúde.

## Entregáveis

- Handler global de exceções.
- Payload consistente para erros.
- Health check operacional.

## Critérios de conclusão

- Erros seguem formato documentado.
- Não há vazamento indevido de detalhes internos.
- Logs permanecem úteis sem expor dados desnecessários.
