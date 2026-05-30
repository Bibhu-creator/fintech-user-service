package com.fintech.user.domain;

public enum UserStatus {
    PENDING,
    ACTIVE,
    SUSPENDED,
    CLOSED;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean canTransact() {
        return this == ACTIVE;
    }
}
