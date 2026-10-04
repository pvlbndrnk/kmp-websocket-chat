package com.pvlbndrnk.di

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.pvlbndrnk.data.MessageDataSource
import com.pvlbndrnk.data.MessageDataSourceImpl
import com.pvlbndrnk.room.RoomController
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

val mainModule = module {
    single {
        MongoClient.create().getDatabase("message_db")
    }
    single<MessageDataSource> {
        MessageDataSourceImpl(get())
    }
    single {
        RoomController(get())
    }
}

fun Application.configureDependencyInjection() {
    install(Koin) {
        modules(mainModule)
    }
}