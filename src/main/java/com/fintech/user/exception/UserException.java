package com.fintech.user.exception;

import com.fintech.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserException extends BaseException {

    public UserException(String message, String errorCode,
                         HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }

    public static UserException notFound(Long userId) {
        return new UserException(
                "User not found: " + userId,
                "USER_NOT_FOUND",
                HttpStatus.NOT_FOUND
        );
    }

    public static UserException wrongPassword() {
        return new UserException(
                "Current password is incorrect",
                "WRONG_PASSWORD",
                HttpStatus.BAD_REQUEST
        );
    }

    public static UserException samePassword() {
        return new UserException(
                "New password cannot be same as current password",
                "SAME_PASSWORD",
                HttpStatus.BAD_REQUEST
        );
    }

    public static UserException passwordMismatch() {
        return new UserException(
                "New password and confirm password do not match",
                "PASSWORD_MISMATCH",
                HttpStatus.BAD_REQUEST
        );
    }

    public static UserException phoneAlreadyExists(String phone) {
        return new UserException(
                "Phone number already registered: " + phone,
                "DUPLICATE_PHONE",
                HttpStatus.CONFLICT
        );
    }

    public static UserException accountNotActive() {
        return new UserException(
                "Account must be active to update profile",
                "ACCOUNT_NOT_ACTIVE",
                HttpStatus.FORBIDDEN
        );
    }
}
