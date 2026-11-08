package com.pvlbndrnk.chat.auth.data.remote

import com.pvlbndrnk.chat.auth.data.remote.model.CreateAccessTokenRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.CreateAccessTokenResponseDto
import com.pvlbndrnk.chat.auth.data.remote.model.CreateSessionRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.CreateSessionResponseDto
import com.pvlbndrnk.chat.auth.data.remote.model.CreateUserRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.UserResponseDto

interface AuthApi {
    suspend fun createUser(request: CreateUserRequestDto): UserResponseDto
    suspend fun createSession(request: CreateSessionRequestDto): CreateSessionResponseDto
    suspend fun createAccessToken(request: CreateAccessTokenRequestDto): CreateAccessTokenResponseDto
}