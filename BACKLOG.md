# Planejamento e Backlog - Delivery API

Este documento descreve a estratégia de desenvolvimento, critérios de priorização e o backlog detalhado para a construção da solução "Delivery API".

## 1. Estratégia e Critérios de Priorização

Para definir a ordem de execução das tarefas, foram adotados os seguintes critérios arquiteturais e de negócio:

1.  **Mitigação de Riscos (Fail-fast):** As funcionalidades de maior complexidade técnica e risco de falha (Persistência em Arquivo JSON e Máquina de Estados) foram priorizadas para serem validadas logo no início.
2.  **Dependência Técnica:** A estrutura de dados (Model/DTOs) é pré-requisito para qualquer lógica de negócio, portanto, foi a primeira etapa de desenvolvimento.
3.  **Core Business antes de Periféricos:** A API e as regras de negócio (Backend) têm precedência sobre a interface gráfica (Frontend) e configurações finais de deploy, garantindo que o coração da aplicação funcione independentemente da camada de apresentação.

---

## 2. Backlog de Tarefas

Legenda de Status:

- 🟢 `DONE`
- 🟡 `IN PROGRESS`
- 🔴 `TODO`

|   ID   | Fase                   | Tarefa                               | Critérios de Aceitação (Definition of Done)                                                                                                                                                                                                                   | Status |
| :----: | :--------------------- | :----------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | :----: |
| **01** | **Setup**              | Inicialização e Estrutura do Projeto | • Repositório Git iniciado.<br>• Estrutura do projeto Spring Boot (Backend) criada.<br>• Estrutura do projeto Next.js (Frontend) criada.<br>• `.gitignore` configurado adequadamente.<br>• Arquivo base `pedidos.json` importado para o projeto.              |   🟢  |
| **02** | **Core / Model**       | Mapeamento de Entidades e DTOs       | • Classes Java criadas refletindo a estrutura exata do `pedidos.json` (Order, Store, Payment, Items, etc.).<br>• Configuração do Jackson/Gson para serialização/deserialização correta de datas e Enums.                                                      |   🟡  |
| **03** | **Core / Persistence** | Implementação do Repositório JSON    | • Leitura segura do arquivo `pedidos.json`.<br>• Escrita atômica no arquivo (garantir que novos pedidos não sobrescrevam o arquivo inteiro incorretamente).<br>• Tratamento de exceção para falhas de I/O.                                                    |   🔴   |
| **04** | **Business Logic**     | Implementação da Máquina de Estados  | • Enum `OrderStatus` criado (RECEIVED, CONFIRMED, DISPATCHED, DELIVERED, CANCELED).<br>• Lógica de validação de transições implementada (ex: `CONFIRMED` não pode voltar para `RECEIVED`).<br>• Testes unitários cobrindo transições válidas e inválidas.     |   🔴   |
| **05** | **Business Logic**     | Serviço de Pedidos (CRUD)            | • Método para criar novo pedido (Status inicial `RECEIVED`).<br>• Método para buscar pedido por ID.<br>• Método para listar todos os pedidos.<br>• Método para atualizar status do pedido.                                                                    |   🔴   |
| **06** | **API**                | Endpoints REST Controller            | • `GET /api/orders` (Listagem).<br>• `GET /api/orders/{id}` (Detalhes).<br>• `POST /api/orders` (Criação).<br>• `PATCH /api/orders/{id}/status` (Atualização de Estado).<br>• Tratamento de erros (ControllerAdvice) para retornos HTTP adequados (404, 400). |   🔴   |
| **07** | **Frontend**           | Integração e Listagem                | • Configuração do cliente HTTP (Axios/Fetch).<br>• Página principal listando os pedidos retornados pela API.<br>• Formatação visual de valores monetários e datas.                                                                                            |   🔴   |
| **08** | **Frontend**           | Detalhes e Ações                     | • Visualização detalhada dos itens do pedido.<br>• Botões de ação para transição de status.<br>• Bloqueio visual de ações inválidas baseadas no estado atual.                                                                                                 |   🔴   |
| **09** | **Quality**            | Testes e Refatoração                 | • Cobertura de testes unitários para o Service e State Machine.<br>• Revisão de código (Clean Code).                                                                                                                                                          |   🔴   |
| **10** | **DevOps**             | Conteinerização (Docker)             | • `Dockerfile` otimizado para a API Java (Multi-stage build).<br>• `Dockerfile` para o Frontend Next.js.<br>• `docker-compose.yml` orquestrando a subida dos dois serviços simultaneamente.                                                                   |   🔴   |
| **11** | **Docs**               | Documentação Final                   | • `README.md` atualizado com instruções claras de "Como Rodar".<br>• Seção explicando as decisões de arquitetura e design patterns utilizados.<br>• Instruções para execução dos testes.                                                                      |   🔴   |
