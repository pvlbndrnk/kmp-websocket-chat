package com.pvlbndrnk.chat.session.di

import com.pvlbndrnk.chat.session.data.local.IosSessionStore
import com.pvlbndrnk.chat.session.data.local.SessionStore
import org.koin.dsl.module

val iosSessionModule = module {
    single<SessionStore> { IosSessionStore() }
}