package com.pvlbndrnk.chat

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.pvlbndrnk.chat.di.appModule
import com.pvlbndrnk.chat.presentation.chat.ChatScreen
import com.pvlbndrnk.chat.presentation.username.UsernameScreen
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinConfiguration

@Composable
fun App() {
    KoinApplication(
        configuration = KoinConfiguration(
            config = {
                modules(appModule)
            }
        )
    ) {
        MaterialTheme {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "username_screen"
            ) {
                composable("username_screen") {
                    UsernameScreen(onNavigate = navController::navigate)
                }
                composable(
                    route = "chat_screen/{username}",
                    arguments = listOf(
                        navArgument(name = "username") {
                            type = NavType.StringType
                            nullable = true
                        }
                    )
                ) {
                    val username = it.arguments?.read { getStringOrNull("username") }
                    ChatScreen(username = username)
                }
            }
        }
    }
}
