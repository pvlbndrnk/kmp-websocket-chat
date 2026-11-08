package com.pvlbndrnk.chat.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pvlbndrnk.chat.App
import com.pvlbndrnk.chat.session.di.androidSessionModule

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serverBaseUrl = getString(R.string.server_base_url)
        setContent {
            App(
                additionalModules = listOf(androidSessionModule(applicationContext)),
                serverBaseUrl = serverBaseUrl,
            )
        }
    }
}
