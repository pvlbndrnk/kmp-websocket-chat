package com.pvlbndrnk.chat.session

import com.pvlbndrnk.chat.core.user.model.User

sealed interface SessionState {
    data class Authenticated(val user: User) : SessionState

    object Unauthenticated : SessionState
    object Restoring : SessionState
    object RestoreFailed : SessionState
}