package com.fintech.user.service;

import com.fintech.user.dto.request.KycUploadRequest;
import com.fintech.user.dto.response.KycResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface KycService {

    KycResponse uploadDocument(Long userId,
                               KycUploadRequest request,
                               MultipartFile file);

    List<KycResponse> getKycStatus(Long userId);

    KycResponse verifyDocument(Long documentId, Long adminUserId);

    KycResponse rejectDocument(Long documentId,
                               String reason,
                               Long adminUserId);
}