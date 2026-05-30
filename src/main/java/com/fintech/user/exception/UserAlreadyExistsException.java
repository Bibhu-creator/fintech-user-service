package com.fintech.user.exception;


import com.fintech.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseException {

    public UserAlreadyExistsException(String field, String value) {
        super(
                field + " already registered: " + value,
                "USER_ALREADY_EXISTS",
                HttpStatus.CONFLICT
        );
    }
}
