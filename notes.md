## Flyway
Spring chama o Flyway automaticamente para atualizar a base de dados com base
nos arquivos .sql contidos na pasta resources/db/migrations. Basta fazer o run do Spring.

## Spring Security
JWT contém o dados em si mesmo. É assinado pelo emissor (jwt.secret) e, portanto, somente lido por ele.
Logout deverá ser trato no lado do cliente. Simplesmente apagar o JWT da aplicação.
É possível criar um blacklist de JWT, mas o ideal mesmo é invalidar a conta do cliente, que aí independentemente
do JWT, o acesso pode ser invalidado.
