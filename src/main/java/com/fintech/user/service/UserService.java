package com.fintech.user.service;

import com.fintech.user.dto.request.ChangePasswordRequest;
import com.fintech.user.dto.request.LoginRequest;
import com.fintech.user.dto.request.RegisterRequest;
import com.fintech.user.dto.request.UpdateProfileRequest;
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

}
