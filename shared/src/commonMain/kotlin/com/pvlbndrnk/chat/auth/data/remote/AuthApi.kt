package com.pvlbndrnk.chat.auth.data.remote

import com.pvlbndrnk.chat.auth.data.remote.model.TokenRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.TokenResponseDto
import com.pvlbndrnk.chat.auth.data.remote.model.LoginRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.LoginResponseDto
import com.pvlbndrnk.chat.auth.data.remote.model.UserRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.UserResponseDto

interface AuthApi {
    suspend fun register(request: UserRequestDto): UserResponseDto
    suspend fun login(request: LoginRequestDto): LoginResponseDto
    suspend fun refreshAccessToken(request: TokenRequestDto): TokenResponseDto
}