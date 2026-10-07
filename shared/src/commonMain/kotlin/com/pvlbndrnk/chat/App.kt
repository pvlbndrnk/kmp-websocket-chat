package com.pvlbndrnk.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun App() {
    val darkTheme = isSystemInDarkTheme()
    val background = if (darkTheme) Color.Black else Color.White
    val foreground = if (darkTheme) Color.White else Color.Black

    Box(Modifier.fillMaxSize().background(background), contentAlignment = Alignment.Center) {
        BasicText("Hello World", style = TextStyle(color = foreground))
    }
}
