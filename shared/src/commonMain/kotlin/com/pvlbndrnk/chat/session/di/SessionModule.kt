package com.pvlbndrnk.chat.session.di

import com.pvlbndrnk.chat.session.SessionManager
import com.pvlbndrnk.chat.session.SessionManagerImpl
import org.koin.dsl.module

val sessionModule = module {
    single<SessionManager> { SessionManagerImpl(get()) }
}