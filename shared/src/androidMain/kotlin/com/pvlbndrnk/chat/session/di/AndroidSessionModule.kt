package com.pvlbndrnk.chat.session.di

import android.content.Context
import com.pvlbndrnk.chat.session.data.local.AndroidSessionStore
import com.pvlbndrnk.chat.session.data.local.SessionStore
import org.koin.dsl.module

fun androidSessionModule(context: Context) = module {
    val applicationContext = context.applicationContext
    single<SessionStore> { AndroidSessionStore(applicationContext) }
}