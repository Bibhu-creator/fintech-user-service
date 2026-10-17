package com.fintech.user.repository;

import com.fintech.user.domain.User;
import com.fintech.user.domain.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<User> findAllByStatusOrderByCreatedAtDesc(
            UserStatus status, Pageable pageable);

}
