package com.fintech.user.service;

import com.fintech.user.domain.UserStatus;
import com.fintech.user.dto.request.ChangePasswordRequest;
import com.fintech.user.dto.request.LoginRequest;
import com.fintech.user.dto.request.RegisterRequest;
import com.fintech.user.dto.request.UpdateProfileRequest;
import com.fintech.user.dto.response.UserPageResponse;
import com.fintech.user.dto.response.UserResponse;
import com.fintech.user.security.AuthResponse;

public interface UserService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserResponse getProfile(Long userId);

    UserResponse updateProfile(Long userId,
                               UpdateProfileRequest request);

    void changePassword(Long userId,
                        ChangePasswordRequest request);

    // Admin methods
    UserPageResponse getAllUsers(int page, int size,
                                 UserStatus status);

    UserResponse getUserById(Long userId);

    UserResponse suspendUser(Long userId, Long adminUserId);

    UserResponse activateUser(Long userId, Long adminUserId);

    UserResponse closeUser(Long userId, Long adminUserId);


}
