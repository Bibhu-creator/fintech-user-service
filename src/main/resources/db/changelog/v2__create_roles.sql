-- liquibase formatted sql
-- changeset bibhu:2 labels:v1 comment:create roles and permissions tables

CREATE TABLE roles (
                       id          INT AUTO_INCREMENT PRIMARY KEY,
                       name        VARCHAR(50)  NOT NULL UNIQUE,
                       description VARCHAR(255)
);

CREATE TABLE permissions (
                             id       INT AUTO_INCREMENT PRIMARY KEY,
                             name     VARCHAR(100) NOT NULL UNIQUE,
                             resource VARCHAR(50)  NOT NULL,
                             action   VARCHAR(50)  NOT NULL
);

CREATE TABLE user_roles (
                            user_id     BIGINT      NOT NULL,
                            role_id     INT         NOT NULL,
                            assigned_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(id),
                            FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE role_permissions (
                                  role_id       INT NOT NULL,
                                  permission_id INT NOT NULL,
                                  PRIMARY KEY (role_id, permission_id),
                                  FOREIGN KEY (role_id)       REFERENCES roles(id),
                                  FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

-- Seed default roles
INSERT INTO roles (name, description) VALUES
                                          ('CUSTOMER',             'Standard bank customer'),
                                          ('RELATIONSHIP_MANAGER', 'Handles loan approvals'),
                                          ('ADMIN',                'Full platform access');

-- rollback DROP TABLE role_permissions;
-- rollback DROP TABLE user_roles;
-- rollback DROP TABLE permissions;
-- rollback DROP TABLE roles;