package com.pvlbndrnk.chat.auth.domain.error

sealed class AuthException: RuntimeException() {
    class UserAlreadyExists: AuthException()
    class InvalidCredentials: AuthException()
    class InvalidToken: AuthException()
}