package com.pvlbndrnk.chat

import androidx.compose.ui.window.ComposeUIViewController
import com.pvlbndrnk.chat.session.di.iosSessionModule

fun MainViewController() = ComposeUIViewController {
    App(additionalModules = listOf(iosSessionModule))
}
