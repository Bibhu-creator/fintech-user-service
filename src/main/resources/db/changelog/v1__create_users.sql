-- liquibase formatted sql
-- changeset bibhu:1 labels:v1 comment:create users table

CREATE TABLE users (
                       id                BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email             VARCHAR(255)  NOT NULL UNIQUE,
                       phone_number      VARCHAR(20)   NOT NULL UNIQUE,
                       password_hash     VARCHAR(255)  NOT NULL,
                       first_name        VARCHAR(100)  NOT NULL,
                       last_name         VARCHAR(100)  NOT NULL,
                       date_of_birth     DATE,
                       status            VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
                       is_mfa_enabled    BOOLEAN       NOT NULL DEFAULT FALSE,
                       created_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
                           ON UPDATE CURRENT_TIMESTAMP,

                       CONSTRAINT chk_user_status
                           CHECK (status IN ('PENDING', 'ACTIVE', 'SUSPENDED', 'CLOSED'))
);

CREATE INDEX idx_users_email  ON users (email);
CREATE INDEX idx_users_phone  ON users (phone_number);
CREATE INDEX idx_users_status ON users (status);

-- rollback DROP TABLE users;