# Delivery API - Coco Bambu

Sistema de gerenciamento de pedidos delivery com API REST e interface grafica.

## Tecnologias

| Camada   | Tecnologia                                    |
|----------|-----------------------------------------------|
| Backend  | Java 17, Spring Boot 4.0.2, Lombok, Jackson, SpringDoc OpenAPI |
| Frontend | Next.js 16, React 19, TypeScript, Tailwind v4 |
| DevOps   | Docker, Docker Compose                        |

## Arquitetura

```
backend/                          frontend/
├── model/       (Entidades)      ├── components/  (React Components)
├── dto/         (Data Transfer)  ├── types/       (TypeScript Interfaces)
├── repository/  (Persistencia)   ├── lib/         (API Client, Formatters)
├── service/     (Regras Negocio) └── app/         (Pages - App Router)
├── controller/  (REST API)
├── exception/   (Error Handling)
└── config/      (CORS)
```

### Decisoes de Design

- **Repository Pattern**: Persistencia em arquivo JSON com escrita atomica e cache em memoria
- **State Machine Pattern**: Validacao de transicoes de status via `EnumMap` com lookup O(1)
- **Service Layer**: Toda logica de negocio encapsulada, controller apenas delega
- **ControllerAdvice**: Tratamento global de erros com respostas HTTP padronizadas
- **Next.js Rewrites**: Proxy de API para resolver networking Docker (browser -> Next.js server -> backend)

### Maquina de Estados

```
RECEIVED --> CONFIRMED --> DISPATCHED --> DELIVERED
    |            |              |
    v            v              v
         CANCELED (estado terminal)
```

- Transicoes sao apenas para frente (nunca retrocedem)
- `CANCELED` pode ser alcancado de qualquer estado nao-terminal
- `DELIVERED` e `CANCELED` sao estados finais

## Arquitetura e Decisões de Design

O projeto segue uma arquitetura em camadas (Layered Architecture) para garantir desacoplamento e testabilidade. Abaixo estão as principais decisões de engenharia adotadas:

### 1. Persistência e Concorrência (Thread-Safety)
Como o desafio exigia persistência em arquivo JSON (que não possui controle transacional nativo), foi implementada uma estratégia robusta para evitar **Race Conditions**:
- **Leitura**: Cache em memória (`ArrayList`) carregado na inicialização, garantindo performance de leitura O(1) ou O(n) sem I/O de disco constante.
- **Escrita**: Utilização de bloqueios (`synchronized`) e Mutex para garantir que apenas uma thread escreva no arquivo por vez.
- **Atomicidade**: A gravação física utiliza a estratégia de *Atomic File Move* (escreve em temp -> move para final), prevenindo corrupção de dados caso o sistema falhe durante a escrita.

### 2. Máquina de Estados (State Machine)
Para gerenciar as transições de status do pedido (`RECEIVED` -> `CONFIRMED` -> ...), evitei o uso de condicionais espalhadas (`if/else`):
- **Implementação**: Classe dedicada `OrderStateMachine` utilizando `EnumMap` e `EnumSet`.
- **Performance**: Validação de transições com complexidade O(1).
- **Manutenibilidade**: As regras de transição estão centralizadas; alterar o fluxo do negócio não exige mudanças nos Services ou Controllers.

### 3. Desacoplamento e Escalabilidade
A camada de Serviço (`OrderService`) desconhece que os dados são salvos em um JSON.
- **Benefício**: Isso permite migrar a persistência para um Banco de Dados Relacional (PostgreSQL, MySQL) ou NoSQL no futuro alterando apenas a implementação do Repositório, sem refatorar uma única linha da regra de negócio.

### 4. Estratégia de Testes
A qualidade foi assegurada através da Pirâmide de Testes:
- **Unitários (Service & StateMachine)**: Validam a lógica de negócio e transições de status isoladamente (com Mockito).
- **Integração (Controller)**: Validam o contrato da API, serialização JSON e Status Codes HTTP (com MockMvc).

### 5. Frontend & Networking
- **Next.js Rewrites**: Configurado como Reverse Proxy para evitar problemas de CORS e simplificar a comunicação entre o browser e o container do backend dentro da rede Docker.

## Como Rodar

### Com Docker (recomendado)

```bash
docker-compose up --build
```

- **Backend**: http://localhost:8080
- **Frontend**: http://localhost:3000

### Sem Docker

#### Backend

```bash
cd backend
mvn spring-boot:run
```

> Requer Java 17+ e Maven 3.9+ instalados.

#### Frontend

```bash
cd frontend
npm install
npm run dev
```

> Requer Node.js 20+ instalado.

## Documentacao da API (Swagger)

Com o backend rodando, acesse a documentacao interativa:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

O Swagger permite testar todos os endpoints diretamente pelo navegador.

## Endpoints da API

| Metodo | Rota                        | Descricao                | Status       |
|--------|-----------------------------|--------------------------|--------------|
| GET    | `/api/orders`               | Listar todos os pedidos  | 200          |
| GET    | `/api/orders/{id}`          | Buscar pedido por ID     | 200 / 404    |
| POST   | `/api/orders`               | Criar novo pedido        | 201          |
| PUT    | `/api/orders/{id}`          | Atualizar pedido         | 200 / 404    |
| DELETE | `/api/orders/{id}`          | Remover pedido           | 204 / 404    |
| PATCH  | `/api/orders/{id}/status`   | Atualizar status         | 200 / 400 / 404 |

### Exemplo - Atualizar Status

```bash
curl -X PATCH http://localhost:8080/api/orders/{id}/status \
  -H "Content-Type: application/json" \
  -d '{"status": "CONFIRMED"}'
```

## Testes

```bash
cd backend
mvn test
```

Os testes cobrem:
- **OrderStateMachineTest**: Todas as transicoes validas e invalidas (parametrizado)
- **OrderServiceTest**: CRUD completo, validacao de transicoes, tratamento de erros
- **OrderControllerTest**: Endpoints REST com MockMvc, status HTTP corretos

## Estrutura do Projeto

```
delivery-challenge/
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/cocobambu/delivery/
│       │   ├── DeliveryApplication.java
│       │   ├── config/CorsConfig.java, OpenApiConfig.java
│       │   ├── controller/OrderController.java
│       │   ├── dto/ErrorResponse.java, StatusUpdateRequest.java
│       │   ├── exception/GlobalExceptionHandler.java, ...
│       │   ├── model/OrderWrapper.java, Order.java, ...
│       │   ├── repository/OrderRepository.java
│       │   └── service/OrderService.java, OrderStateMachine.java
│       └── test/java/com/cocobambu/delivery/
│           ├── controller/OrderControllerTest.java
│           └── service/OrderStateMachineTest.java, OrderServiceTest.java
├── frontend/
│   ├── Dockerfile
│   ├── package.json
│   ├── next.config.ts
│   └── src/
│       ├── app/
│       │   ├── page.tsx (Listagem)
│       │   └── orders/[id]/page.tsx (Detalhes)
│       ├── components/
│       │   ├── OrderList.tsx, OrderDetail.tsx
│       │   ├── StatusBadge.tsx, StatusTimeline.tsx
│       │   └── ActionButtons.tsx
│       ├── lib/api.ts, format.ts
│       └── types/order.ts
├── data/pedidos.json
├── docker-compose.yml
├── BACKLOG.md
└── README.md
```
