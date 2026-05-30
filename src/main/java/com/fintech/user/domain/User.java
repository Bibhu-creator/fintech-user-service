package com.fintech.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "VARCHAR(20)")
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;

    @Column(name = "is_mfa_enabled", nullable = false)
    @Builder.Default
    private boolean mfaEnabled = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Domain methods
    public void activate() {
        if (this.status != UserStatus.PENDING) {
            throw new IllegalStateException(
                    "Cannot activate user with status: " + this.status
            );
        }
        this.status = UserStatus.ACTIVE;
    }

    public void suspend() {
        if (this.status == UserStatus.CLOSED) {
            throw new IllegalStateException(
                    "Cannot suspend a CLOSED account"
            );
        }
        this.status = UserStatus.SUSPENDED;
    }

    public void close() {
        this.status = UserStatus.CLOSED;
    }

    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }
}
