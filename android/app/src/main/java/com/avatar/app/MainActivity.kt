package com.avatar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.avatar.app.R
import com.avatar.app.ui.auth.AuthViewModel
import com.avatar.app.ui.auth.LoginScreen
import com.avatar.app.ui.auth.RegisterScreen
import com.avatar.app.ui.components.AuthVideoBackground
import com.avatar.app.ui.components.KasAppVideoBgColor
import com.avatar.app.ui.home.HomeScreen
import com.avatar.app.ui.theme.AvatarTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AvatarTheme {
                val authState by authViewModel.uiState.collectAsState()
                var currentAuthScreen by remember { mutableStateOf("login") }
                var showSplashScreen by remember { mutableStateOf(true) }

                // Mobil Giriş videosu (~3.8 sn) bittiğinde veya en geç 4.2 saniyede uygulamayı aç
                LaunchedEffect(Unit) {
                    delay(4200)
                    showSplashScreen = false
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = if (showSplashScreen) KasAppVideoBgColor else Color.White
                ) {
                    Crossfade(targetState = showSplashScreen, label = "AppSplashFade") { isSplash ->
                        if (isSplash) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(KasAppVideoBgColor)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { showSplashScreen = false }
                            ) {
                                // Kullanıcının gönderdiği Mobil Giriş videosunu tam netlikte,
                                // tek sefer baştan sona (tekrar başa sarmadan) oynat ve bitince uygulamayı aç
                                AuthVideoBackground(
                                    rawResId = R.raw.splash_video,
                                    dimAlpha = 0f,
                                    isLooping = false,
                                    onCompletion = {
                                        showSplashScreen = false
                                    }
                                )
                            }
                        } else {
                            if (authState.isLoggedIn && authState.user != null) {
                                HomeScreen(
                                    user = authState.user!!,
                                    authViewModel = authViewModel
                                )
                            } else {
                                when (currentAuthScreen) {
                                    "register" -> {
                                        RegisterScreen(
                                            viewModel = authViewModel,
                                            onNavigateToLogin = { currentAuthScreen = "login" }
                                        )
                                    }
                                    else -> {
                                        LoginScreen(
                                            viewModel = authViewModel,
                                            onNavigateToRegister = { currentAuthScreen = "register" }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
