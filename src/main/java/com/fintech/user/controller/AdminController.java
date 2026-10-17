package com.fintech.user.controller;

import com.fintech.common.response.ApiResponse;
import com.fintech.user.domain.UserStatus;
import com.fintech.user.dto.response.UserPageResponse;
import com.fintech.user.dto.response.UserResponse;
import com.fintech.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<UserPageResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) UserStatus status) {

        UserPageResponse response = userService.getAllUsers(
                page, size, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id) {

        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/users/{id}/suspend")
    public ResponseEntity<ApiResponse<UserResponse>> suspendUser(
            @PathVariable Long id,
            @RequestParam Long adminUserId) {

        UserResponse response = userService.suspendUser(id, adminUserId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(
            @PathVariable Long id,
            @RequestParam Long adminUserId) {

        UserResponse response = userService.activateUser(id, adminUserId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/users/{id}/close")
    public ResponseEntity<ApiResponse<UserResponse>> closeUser(
            @PathVariable Long id,
            @RequestParam Long adminUserId) {

        UserResponse response = userService.closeUser(id, adminUserId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
