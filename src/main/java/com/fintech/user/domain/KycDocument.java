package com.fintech.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "kyc_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "doc_type", nullable = false, length = 50)
    private String docType;

    @Column(name = "doc_number", nullable = false, length = 100)
    private String docNumber;

    @Column(name = "file_key", nullable = false, length = 500)
    private String fileKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    @Builder.Default
    private KycStatus status = KycStatus.PENDING;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    // Domain methods
    public void verify() {
        this.status = KycStatus.VERIFIED;
        this.verifiedAt = Instant.now();
    }

    public void reject(String reason) {
        this.status = KycStatus.REJECTED;
        this.rejectionReason = reason;
    }
}
