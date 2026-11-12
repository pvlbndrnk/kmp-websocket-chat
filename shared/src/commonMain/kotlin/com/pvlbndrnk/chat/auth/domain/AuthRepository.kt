package com.pvlbndrnk.chat.auth.domain

interface AuthRepository {
    suspend fun register(username: String, password: String)
    suspend fun login(username: String, password: String)
    suspend fun refreshAccessToken()
}