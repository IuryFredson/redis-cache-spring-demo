# Uso do Redis como camada de cache

API Spring Boot para demonstrar o Redis como cache em uma consulta de produtos. O projeto usa PostgreSQL como banco principal e Redis como camada intermediaria para reduzir o tempo de resposta em leituras repetidas.

## Stack

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Data Redis
- PostgreSQL
- Redis
- Docker Compose
- Maven

## Arquitetura da demonstracao

```text
Cliente -> API Spring Boot -> Redis
                         -> PostgreSQL
```

Fluxo do endpoint `GET /products/{id}`:

1. A API procura a chave `product:{id}` no Redis.
2. Se encontrar, retorna o produto com `source: "CACHE"`.
3. Se nao encontrar, consulta o PostgreSQL.
4. O resultado e salvo no Redis com TTL de 30 segundos.
5. A resposta informa a origem, o TTL restante e o tempo medido na aplicacao.

## Como rodar

Suba PostgreSQL e Redis:

```bash
docker compose up -d
```

Inicie a API:

```bash
mvn spring-boot:run
```

A aplicacao cria 20 produtos automaticamente se o banco estiver vazio.

## Endpoints

Listar produtos:

```bash
curl http://localhost:8080/products
```

Consultar produto com cache:

```bash
curl http://localhost:8080/products/1
```

Consultar produto sem cache:

```bash
curl http://localhost:8080/products/1/no-cache
```

Invalidar cache manualmente:

```bash
curl -X DELETE http://localhost:8080/cache/products/1
```

Atualizar produto e invalidar o cache automaticamente:

```bash
curl -X PUT http://localhost:8080/products/1 \
  -H 'Content-Type: application/json' \
  -d '{"name":"Notebook Dell Inspiron Atualizado","description":"Notebook com 32GB RAM e SSD 1TB","category":"Informatica","price":4299.90,"stock":5}'
```

## Exemplo de cache miss

Primeira consulta apos limpar o cache:

```json
{
  "source": "DATABASE",
  "durationMs": 130.4,
  "ttlSeconds": 30,
  "data": {
    "id": 1,
    "name": "Notebook Dell Inspiron",
    "description": "Notebook com 16GB RAM e SSD 512GB",
    "category": "Informatica",
    "price": 3599.90,
    "stock": 8,
    "updatedAt": "2026-06-29T10:30:00"
  }
}
```

## Exemplo de cache hit

Segunda consulta para o mesmo produto:

```json
{
  "source": "CACHE",
  "durationMs": 3.2,
  "ttlSeconds": 26,
  "data": {
    "id": 1,
    "name": "Notebook Dell Inspiron",
    "description": "Notebook com 16GB RAM e SSD 512GB",
    "category": "Informatica",
    "price": 3599.90,
    "stock": 8,
    "updatedAt": "2026-06-29T10:30:00"
  }
}
```

## Benchmark

Execute:

```bash
./scripts/benchmark.sh
```

Ou altere parametros:

```bash
BASE_URL=http://localhost:8080 PRODUCT_ID=1 RUNS=20 ./scripts/benchmark.sh
```

Saida em CSV:

```csv
endpoint,run,source,durationMs,curlTotalSeconds
cached,1,DATABASE,126.4,0.139
cached,2,CACHE,2.8,0.011
no-cache,1,DATABASE,122.2,0.132
```

Esses dados podem ser colados em uma planilha para gerar grafico comparando consulta com cache e consulta sem cache.

## Roteiro da demonstracao

1. Mostrar `docker compose up -d` com PostgreSQL e Redis.
2. Iniciar a API com `mvn spring-boot:run`.
3. Listar produtos com `GET /products`.
4. Invalidar o cache do produto 1.
5. Consultar `GET /products/1` e mostrar `source: "DATABASE"`.
6. Repetir `GET /products/1` e mostrar `source: "CACHE"`.
7. Comparar `durationMs` das duas respostas.
8. Esperar 30 segundos e consultar novamente para mostrar expiracao por TTL.
9. Atualizar o produto com `PUT /products/1`.
10. Consultar de novo e mostrar que o cache foi invalidado.
11. Rodar `./scripts/benchmark.sh` e salvar os tempos para graficos.

## Evidencias para o relatorio

- Print dos containers PostgreSQL e Redis em execucao.
- Print da primeira consulta com `source: "DATABASE"`.
- Print da segunda consulta com `source: "CACHE"`.
- Print do TTL diminuindo entre consultas.
- Print da invalidacao manual com `DELETE /cache/products/1`.
- Print do benchmark em CSV.
- Grafico comparando os tempos com cache e sem cache.

## Discussao tecnica

O Redis melhora o tempo de resposta porque evita leituras repetidas no banco principal. Em dados de catalogo, como nome, descricao e categoria de produtos, esse ganho costuma ser seguro e relevante. Em campos sensiveis a atualizacao, como preco e estoque, o cache exige cuidado, pois pode devolver dados desatualizados ate o fim do TTL ou ate uma invalidacao manual. Por isso, a API invalida o cache automaticamente quando um produto e atualizado.
