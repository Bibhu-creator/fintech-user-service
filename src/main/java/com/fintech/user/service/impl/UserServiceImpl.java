package com.fintech.user.service.impl;

import com.fintech.user.domain.User;
import com.fintech.user.dto.request.LoginRequest;
import com.fintech.user.dto.request.RegisterRequest;
import com.fintech.user.dto.response.UserResponse;
import com.fintech.user.exception.InvalidCredentialsException;
import com.fintech.user.exception.UserAlreadyExistsException;
import com.fintech.user.repository.UserRepository;
import com.fintech.user.security.AuthResponse;
import com.fintech.user.security.JwtService;
import com.fintech.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
