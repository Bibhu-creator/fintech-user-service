package com.fintech.user.exception;


import com.fintech.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class KycException extends BaseException {

    public KycException(String message, String errorCode,
                        HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }

    // Document not found
    public static KycException documentNotFound(Long documentId) {
        return new KycException(
                "KYC document not found: " + documentId,
                "KYC_DOCUMENT_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }

    // Already has pending document
    public static KycException pendingDocumentExists() {
        return new KycException(
                "You already have a KYC document under review. " +
                        "Please wait for the current one to be processed.",
                "KYC_PENDING_EXISTS",
                HttpStatus.CONFLICT
        );
    }

    // Invalid file type
    public static KycException invalidFileType(String fileName) {
        return new KycException(
                "Invalid file type: " + fileName +
                        ". Only PDF, JPG, JPEG, PNG are allowed.",
                "KYC_INVALID_FILE_TYPE",
                HttpStatus.BAD_REQUEST
        );
    }

    // File too large
    public static KycException fileTooLarge(long maxSizeMb) {
        return new KycException(
                "File size exceeds maximum allowed size of " + maxSizeMb + "MB",
                "KYC_FILE_TOO_LARGE",
                HttpStatus.BAD_REQUEST
        );
    }

    // Invalid state transition
    public static KycException invalidStatusTransition(
            String current, String target) {
        return new KycException(
                "Cannot change KYC status from " + current + " to " + target,
                "KYC_INVALID_STATUS_TRANSITION",
                HttpStatus.BAD_REQUEST
        );
    }

    // User not eligible
    public static KycException userNotEligible(String reason) {
        return new KycException(
                "User is not eligible for KYC: " + reason,
                "KYC_USER_NOT_ELIGIBLE",
                HttpStatus.FORBIDDEN
        );
    }

    // Upload failed
    public static KycException uploadFailed() {
        return new KycException(
                "Failed to upload document. Please try again.",
                "KYC_UPLOAD_FAILED",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
