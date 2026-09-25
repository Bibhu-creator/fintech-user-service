package com.fintech.user.dto.response;

import com.fintech.user.domain.KycStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class KycResponse {

    private Long id;
    private Long userId;
    private String docType;
    private String docNumber;
    private KycStatus status;
    private String rejectionReason;
    private Instant verifiedAt;
    private Instant createdAt;
}
