# Spring Boot (Spring MVC and MyBatis) Sample Application

## Run

Start PostgresSQL db container.

```sh
cd docker
docker comose up -d postgres
```

Build & Run.

```sh
./mvnw clean package
java -jar target/spring-mvc-mybatis-0.0.1-SNAPSHOT.jar
```

or

```sh
./mvnw spring-boot:run   
```

Default swagger url is http://localhost:18888/swagger-ui.html.

## Run as Docker Container

Build docker image.

```sh
./mvnw compile jib:dockerBuild
```

Run PostgresSQL db and todo application container.

```sh
cd docker
docker compose up -d
```

## Run Integration Tests with DB

```sh
 ./mvnw test -Dtest="*IT"
```
