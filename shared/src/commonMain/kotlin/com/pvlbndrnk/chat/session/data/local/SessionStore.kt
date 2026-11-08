package com.pvlbndrnk.chat.session.data.local

interface SessionStore {
    suspend fun read(): String?
    suspend fun write(token: String)
    suspend fun clear()
}