package com.fintech.user.repository;


import com.fintech.user.domain.KycDocument;
import com.fintech.user.domain.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KycRepository extends JpaRepository<KycDocument, Long> {

    List<KycDocument> findByUserId(Long userId);

    Optional<KycDocument> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndStatus(Long userId, KycStatus status);
}
