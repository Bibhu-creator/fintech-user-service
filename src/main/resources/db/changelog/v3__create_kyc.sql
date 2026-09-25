-- liquibase formatted sql
-- changeset bibhu:3 labels:v1 comment:create kyc_documents table

CREATE TABLE kyc_documents (
                               id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id          BIGINT       NOT NULL,
                               doc_type         VARCHAR(50)  NOT NULL,
                               doc_number       VARCHAR(100) NOT NULL,
                               file_key         VARCHAR(500) NOT NULL,
                               status           VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
                               rejection_reason VARCHAR(500),
                               verified_at      TIMESTAMP,
                               created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               CONSTRAINT chk_kyc_status
                                   CHECK (status IN ('PENDING', 'VERIFIED', 'REJECTED')),

                               CONSTRAINT chk_kyc_doc_type
                                   CHECK (doc_type IN ('AADHAAR', 'PAN', 'PASSPORT', 'DRIVING_LICENSE')),

                               FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_kyc_user_id ON kyc_documents (user_id);
CREATE INDEX idx_kyc_status  ON kyc_documents (status);

-- rollback DROP TABLE kyc_documents;