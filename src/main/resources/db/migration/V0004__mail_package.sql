CREATE TABLE emails_enviados (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NULL,
    email_tipo VARCHAR(50) NOT NULL UNIQUE,
    data_envio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    api_response TEXT,
    api_response_status_code VARCHAR(10),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);