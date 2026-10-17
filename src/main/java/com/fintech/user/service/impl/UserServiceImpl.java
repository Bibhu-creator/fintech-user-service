package com.fintech.user.service.impl;

import com.fintech.user.domain.User;
import com.fintech.user.domain.UserStatus;
import com.fintech.user.dto.request.ChangePasswordRequest;
import com.fintech.user.dto.request.LoginRequest;
import com.fintech.user.dto.request.RegisterRequest;
import com.fintech.user.dto.request.UpdateProfileRequest;
import com.fintech.user.dto.response.UserPageResponse;
import com.fintech.user.dto.response.UserResponse;
import com.fintech.user.exception.InvalidCredentialsException;
import com.fintech.user.exception.UserAlreadyExistsException;
import com.fintech.user.exception.UserException;
import com.fintech.user.repository.UserRepository;
import com.fintech.user.security.AuthResponse;
import com.fintech.user.security.JwtService;
import com.fintech.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("email", request.getEmail());
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new UserAlreadyExistsException("phoneNumber",
                    request.getPhoneNumber());
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(hashedPassword)
                .build();

        User savedUser = userRepository.save(user);

        log.info("User registered successfully: {}", savedUser.getEmail());

        return mapToResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        // Step 1 — find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        // Step 2 — verify password
        if (!passwordEncoder.matches(request.getPassword(),
                user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // Step 3 — generate JWT
        String accessToken = jwtService.generateToken(
                user.getId(), user.getEmail());

        log.info("User logged in successfully: {}", user.getEmail());

        // Step 4 — return token + user info
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(1800L)
                .user(mapToResponse(user))
                .build();
    }

    @Override
    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.notFound(userId));
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId,
                                      UpdateProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.notFound(userId));

        // Validation 1 — user must be active
        if (!user.isActive()) {
            throw UserException.accountNotActive();
        }

        // Validation 2 — phone number uniqueness
        if (!user.getPhoneNumber().equals(request.getPhoneNumber())
                && userRepository.existsByPhoneNumber(
                request.getPhoneNumber())) {
            throw UserException.phoneAlreadyExists(
                    request.getPhoneNumber());
        }

        // Update fields
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());

        User saved = userRepository.save(user);
        log.info("Profile updated for user: {}", userId);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void changePassword(Long userId,
                               ChangePasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.notFound(userId));

        // Validation 1 — current password must be correct
        if (!passwordEncoder.matches(request.getCurrentPassword(),
                user.getPasswordHash())) {
            throw UserException.wrongPassword();
        }

        // Validation 2 — new password cannot be same as current
        if (passwordEncoder.matches(request.getNewPassword(),
                user.getPasswordHash())) {
            throw UserException.samePassword();
        }

        // Validation 3 — confirm password must match new password
        if (!request.getNewPassword().equals(
                request.getConfirmPassword())) {
            throw UserException.passwordMismatch();
        }

        // Update password
        user.setPasswordHash(
                passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed for user: {}", userId);
    }
    @Override
    public UserPageResponse getAllUsers(int page, int size,
                                        UserStatus status) {
        Pageable pageable = PageRequest.of(page, size);

        Page<User> userPage;
        if (status != null) {
            userPage = userRepository
                    .findAllByStatusOrderByCreatedAtDesc(status, pageable);
        } else {
            userPage = userRepository
                    .findAllByOrderByCreatedAtDesc(pageable);
        }

        List<UserResponse> users = userPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return UserPageResponse.builder()
                .users(users)
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .currentPage(userPage.getNumber())
                .pageSize(userPage.getSize())
                .hasNext(userPage.hasNext())
                .hasPrevious(userPage.hasPrevious())
                .build();
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.notFound(userId));
        return mapToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse suspendUser(Long userId, Long adminUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.notFound(userId));

        user.suspend();
        User saved = userRepository.save(user);

        log.info("User {} suspended by admin {}", userId, adminUserId);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse activateUser(Long userId, Long adminUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.notFound(userId));

        // Only SUSPENDED users can be re-activated by admin
        if (user.getStatus() != UserStatus.SUSPENDED) {
            throw UserException.accountNotActive();
        }

        user.setStatus(UserStatus.ACTIVE);
        User saved = userRepository.save(user);

        log.info("User {} activated by admin {}", userId, adminUserId);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public UserResponse closeUser(Long userId, Long adminUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.notFound(userId));

        user.close();
        User saved = userRepository.save(user);

        log.info("User {} closed by admin {}", userId, adminUserId);
        return mapToResponse(saved);
    }
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }


}
