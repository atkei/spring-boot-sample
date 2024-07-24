# Spring WebFlux Reactive Services

Simple example of a reactive services using Spring WebFlux and R2DBC.

## Services

- [user-service](user-service)
  - Java, Spring WebFlux (Reactor), R2DBC, reactor-tools
- [order-service](order-service)
  - Kotlin, Spring WebFlux (Coroutines), R2DBC

## How to Run

Start databases.

```sh
dodker compose up -d
```

Start [user-service](user-service) application.

```sh
cd user-service
./gradlew bootRun
```

Start [order-service](order-service) application.

```sh
cd order-service
./gradlew bootRun
```

## Testing

Create a user.

```sh
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Mike"}' | jq .

{
  "id": 1,
  "name": "Mike"
}
```

Create orders for the user.

```sh
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Order1",
    "userId": 1
  }' | jq .

{
  "id": 1,
  "name": "Order1",
  "userId": 1
}

curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Order2",
    "userId": 1
  }' | jq .

{
  "id": 2,
  "name": "Order2",
  "userId": 1
}
```

Get the user with orders.  
User service calls order service to get corresponding orders.

```sh
curl http://localhost:8080/users/1/orders | jq .

[
  {
    "id": 1,
    "name": "Mike",
    "orders": [
      {
        "id": 1,
        "name": "Order1"
      },
      {
        "id": 2,
        "name": "Order2"
      }
    ]
  }
]
```
