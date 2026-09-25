package com.fintech.user.domain;

public enum KycStatus {
    PENDING,    // document uploaded, waiting for review
    VERIFIED,   // admin approved — user becomes ACTIVE
    REJECTED;   // admin rejected — user must re-upload
}
