package com.fintech.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KycUploadRequest {

    @NotBlank(message = "Document type is required")
    @Pattern(
            regexp = "^(AADHAAR|PAN|PASSPORT|DRIVING_LICENSE)$",
            message = "Document type must be AADHAAR, PAN, PASSPORT or DRIVING_LICENSE"
    )
    private String docType;

    @NotBlank(message = "Document number is required")
    private String docNumber;
}
