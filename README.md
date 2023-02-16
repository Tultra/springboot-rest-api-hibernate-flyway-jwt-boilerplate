# Spring Boot Rest Api Boilerplate

Spring Boot boilerplate code para REST API e autenticação com Json Web token (JWT)

## O que este projeto oferece?

- Hibernate/Jpa ORM
- Configurado para mariaDb SQL database
- Controle de migração com flyway
- Modelo de usuário com roles para controle de acesso
- Autenticação com JWT
- Integração com SendGrid (para envio de e-mails de registro/recuperação de senha)
- Testes unitários (coverage de quase 100%)
- Testes de integração com h2 database

## O que falta?
- Serviço de log
- Criação de interface para o serviço de e-mail (para facilitar a transição de provedores)

## Setup

Ao executar este projeto, é necessário que existam as seguintes variáveis de ambiente configuradas:

- DATABASE_NAME: Nome da base de dados mysql
- DATABASE_USER: Nome do usuário da base de dados
- DATABASE_PASS: Senha do usuário da bae de dados
- JWT_SECRET: Segredo para a assinatura das JWT (256bits)
- SENDGRID_API_KEY: Api key do serviço sendgrid
- SENDGRID_VERIFIED_USER: Email do usuário verificado para o envio de e-mails no sendgrid

