# ToDoList API

Este projeto é uma API desenvolvida com **Spring Boot 3**, utilizando **Java 21**, com persistência em **MySQL** e autenticação baseada em **Spring Security** e **OAuth2 Authorization Server**.  
O ambiente de banco de dados é executado via **Docker Compose**, com suporte ao **phpMyAdmin** para gerenciamento visual.

---

## 📦 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5.4**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - OAuth2 Authorization Server
  - Spring Validation
- **MySQL 8**
- **phpMyAdmin**
- **Lombok**
- **Springdoc OpenAPI** (Swagger UI)
- **Docker / Docker Compose**

---

## 🐳 Configuração do Banco de Dados com Docker

O projeto já possui um arquivo `docker-compose.yml` configurado para executar o **MySQL** e o **phpMyAdmin**.

## Para iniciar os serviços:
     ㅤ
    docker compose up -d
    ㅤ
## Isso irá criar:

    - MySQL acessível na porta 3306
    - phpMyAdmin acessível em http://localhost:8080

---

## 🚀 Executando a Aplicação

A aplicação não está sendo executada no container, apenas o banco de dados e o phpMyAdmin.

1. Clone este repositório e entre no diretório do projeto:

       git clone https://github.com/andreybrumatti/todolist_petize.git
       cd todolist


2. Instale as dependências e compile:

            ㅤ
       mvn clean install
       ㅤ
2. Execute a aplicação localmente:


            ㅤ
       mvn spring-boot:run
       ㅤ                  
4. A API estará disponível em:

        ㅤ
       http://localhost:8081
       ㅤ
---

## 📄 Documentação da API

A documentação gerada pelo Swagger UI estará disponível em:
http://localhost:8081/swagger-ui.html

---

## 📂 Estrutura do Projeto

    
    com.testpetize.todolist
    │
    ├── config/ # Configurações gerais da aplicação 
    │
    ├── domain/ # Representação das entidades e objetos de domínio
    │ ├── enums/ # Enumerações utilizadas na aplicação
    │ └── model/ # Modelos (Entidades JPA)
    │
    ├── dto/ # Objetos de transferência de dados
    │ ├── task/ # DTOs relacionados a tarefas
    │ ├── user/ # DTOs relacionados a usuários
    │ 
    ├── exception/ # DTOs para tratamento de erros e exceções
    │
    ├── infra/ # Camada de controle (Controllers REST)
    │ ├── AttachmentController.java # Endpoints para gerenciamento de anexos
    │ ├── AuthController.java # Endpoints de autenticação e autorização
    │ └── TaskController.java # Endpoints de gerenciamento de tarefas
    │
    ├── repository/ # Interfaces de acesso ao banco de dados (Spring Data JPA)
    │
    ├── security/ # Configurações e classes de segurança
    │
    ├── service/ # Regras de negócio e lógica da aplicação
    │
    └── TodolistApplication.java # Classe principal para execução da aplicação Spring Boot

---

## 🛠 Comandos Úteis

### Parar os containers:
    ㅤ
    docker-compose down
    ㅤ

### Recriar os containers limpando volumes:
    ㅤ
    docker compose down -v && docker compose up -d
    ㅤ

### Ver logs:
    ㅤ
    docker compose logs -f
    ㅤ
