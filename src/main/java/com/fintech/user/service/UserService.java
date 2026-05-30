package com.fintech.user.service;

import com.fintech.user.dto.request.RegisterRequest;
import com.fintech.user.dto.response.UserResponse;

public interface UserService {

    UserResponse register(RegisterRequest request);
}
