package com.fintech.user.service.impl;

import com.fintech.user.domain.KycDocument;
import com.fintech.user.domain.KycStatus;
import com.fintech.user.domain.User;
import com.fintech.user.domain.UserStatus;
import com.fintech.user.dto.request.KycUploadRequest;
import com.fintech.user.dto.response.KycResponse;
import com.fintech.user.exception.KycException;
import com.fintech.user.repository.KycRepository;
import com.fintech.user.repository.UserRepository;
import com.fintech.user.service.KycService;
import com.fintech.user.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KycServiceImpl implements KycService {

    private final KycRepository kycRepository;
    private final UserRepository userRepository;
    private final StorageService storageService;

    private static final long MAX_FILE_SIZE_MB = 5;
    private static final long MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024;
    private static final List<String> ALLOWED_EXTENSIONS =
            Arrays.asList(".pdf", ".jpg", ".jpeg", ".png");

    @Override
    @Transactional
    public KycResponse uploadDocument(Long userId,
                                      KycUploadRequest request,
                                      MultipartFile file) {

        // Validation 1 — find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validation 2 — user must not be SUSPENDED or CLOSED
        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw KycException.userNotEligible(
                    "Account is suspended");
        }
        if (user.getStatus() == UserStatus.CLOSED) {
            throw KycException.userNotEligible(
                    "Account is closed");
        }

        // Validation 3 — user must not already be ACTIVE
        if (user.getStatus() == UserStatus.ACTIVE) {
            throw KycException.userNotEligible(
                    "Account is already verified and active");
        }

        // Validation 4 — no pending document already exists
        if (kycRepository.existsByUserIdAndStatus(
                userId, KycStatus.PENDING)) {
            throw KycException.pendingDocumentExists();
        }

        // Validation 5 — file must not be empty
        if (file.isEmpty()) {
            throw new RuntimeException("File cannot be empty");
        }

        // Validation 6 — file size check
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw KycException.fileTooLarge(MAX_FILE_SIZE_MB);
        }

        // Validation 7 — file type check
        String extension = getExtension(file);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw KycException.invalidFileType(
                    file.getOriginalFilename());
        }

        // Upload file to storage
        String fileName = userId + "-"
                + request.getDocType().toLowerCase() + "-"
                + UUID.randomUUID() + extension;

        String fileKey;
        try {
            fileKey = storageService.upload(
                    "kyc",
                    fileName,
                    file.getInputStream(),
                    file.getSize()
            );
        } catch (IOException e) {
            throw KycException.uploadFailed();
        }

        // Save KYC document record
        KycDocument document = KycDocument.builder()
                .user(user)
                .docType(request.getDocType())
                .docNumber(request.getDocNumber())
                .fileKey(fileKey)
                .build();

        KycDocument saved = kycRepository.save(document);
        log.info("KYC document uploaded for user: {}", userId);

        return mapToResponse(saved);
    }

    @Override
    public List<KycResponse> getKycStatus(Long userId) {
        return kycRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public KycResponse verifyDocument(Long documentId, Long adminUserId) {

        KycDocument document = kycRepository.findById(documentId)
                .orElseThrow(() ->
                        KycException.documentNotFound(documentId));

        // Validation — only PENDING documents can be verified
        if (document.getStatus() != KycStatus.PENDING) {
            throw KycException.invalidStatusTransition(
                    document.getStatus().name(), "VERIFIED");
        }

        // Verify document
        document.verify();
        KycDocument saved = kycRepository.save(document);

        // Activate user
        User user = document.getUser();
        user.activate();
        userRepository.save(user);

        log.info("KYC verified for user: {} by admin: {}",
                user.getId(), adminUserId);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public KycResponse rejectDocument(Long documentId,
                                      String reason,
                                      Long adminUserId) {

        KycDocument document = kycRepository.findById(documentId)
                .orElseThrow(() ->
                        KycException.documentNotFound(documentId));

        // Validation — only PENDING documents can be rejected
        if (document.getStatus() != KycStatus.PENDING) {
            throw KycException.invalidStatusTransition(
                    document.getStatus().name(), "REJECTED");
        }

        // Validate rejection reason
        if (reason == null || reason.trim().isEmpty()) {
            throw new RuntimeException(
                    "Rejection reason is required");
        }

        document.reject(reason);
        KycDocument saved = kycRepository.save(document);

        log.info("KYC rejected for document: {} by admin: {}",
                documentId, adminUserId);

        return mapToResponse(saved);
    }

    private String getExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null
                && originalFilename.contains(".")) {
            return originalFilename.substring(
                    originalFilename.lastIndexOf("."));
        }
        return ".pdf";
    }

    private KycResponse mapToResponse(KycDocument document) {
        return KycResponse.builder()
                .id(document.getId())
                .userId(document.getUser().getId())
                .docType(document.getDocType())
                .docNumber(document.getDocNumber())
                .status(document.getStatus())
                .rejectionReason(document.getRejectionReason())
                .verifiedAt(document.getVerifiedAt())
                .createdAt(document.getCreatedAt())
                .build();
    }
}