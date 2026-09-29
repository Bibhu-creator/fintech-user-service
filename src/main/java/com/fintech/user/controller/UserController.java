package com.fintech.user.controller;

import com.fintech.common.response.ApiResponse;
import com.fintech.user.dto.request.ChangePasswordRequest;
import com.fintech.user.dto.request.UpdateProfileRequest;
import com.fintech.user.dto.response.UserResponse;
import com.fintech.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(
            @RequestParam Long userId) {

        UserResponse response = userService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestParam Long userId,
            @Valid @RequestBody UpdateProfileRequest request) {

        UserResponse response = userService.updateProfile(
                userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestParam Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(userId, request);
        return ResponseEntity.ok(
                ApiResponse.success("Password changed successfully"));
    }
}
