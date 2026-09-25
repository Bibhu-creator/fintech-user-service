package com.fintech.user.controller;

import com.fintech.common.response.ApiResponse;
import com.fintech.user.dto.request.KycUploadRequest;
import com.fintech.user.dto.response.KycResponse;
import com.fintech.user.service.KycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    // User uploads KYC document
    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<KycResponse>> uploadDocument(
            @RequestParam("userId") Long userId,
            @RequestParam("docType") String docType,
            @RequestParam("docNumber") String docNumber,
            @RequestPart("file") MultipartFile file) {

        log.info("KYC upload request from user: {}", userId);

        KycUploadRequest request = new KycUploadRequest(docType, docNumber);

        KycResponse response = kycService.uploadDocument(
                userId, request, file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    // User checks their KYC status
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<KycResponse>>> getStatus(
            @RequestParam("userId") Long userId) {

        List<KycResponse> response = kycService.getKycStatus(userId);

        return ResponseEntity
                .ok(ApiResponse.success(response));
    }

    // Admin verifies a KYC document
    @PutMapping("/{documentId}/verify")
    public ResponseEntity<ApiResponse<KycResponse>> verifyDocument(
            @PathVariable Long documentId,
            @RequestParam("adminUserId") Long adminUserId) {

        KycResponse response = kycService.verifyDocument(
                documentId, adminUserId);

        return ResponseEntity
                .ok(ApiResponse.success(response));
    }

    // Admin rejects a KYC document
    @PutMapping("/{documentId}/reject")
    public ResponseEntity<ApiResponse<KycResponse>> rejectDocument(
            @PathVariable Long documentId,
            @RequestParam("adminUserId") Long adminUserId,
            @RequestParam("reason") String reason) {

        KycResponse response = kycService.rejectDocument(
                documentId, reason, adminUserId);

        return ResponseEntity
                .ok(ApiResponse.success(response));
    }
}
