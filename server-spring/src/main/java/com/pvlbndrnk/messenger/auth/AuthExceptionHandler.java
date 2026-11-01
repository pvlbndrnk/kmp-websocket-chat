package com.pvlbndrnk.messenger.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ProblemDetail handleUsernameAlreadyExistsException(UsernameAlreadyExistsException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }
}
