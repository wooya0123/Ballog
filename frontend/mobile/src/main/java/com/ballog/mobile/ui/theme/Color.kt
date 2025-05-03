package com.ballog.mobile.ui.theme

import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF0066FF)
val OnPrimary = Color.White
val Background = Color(0xFFF2F2F2)
val Surface = Color.White

val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    background = Background,
    surface = Surface
)

val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = Color(0xFF3399FF),
    onPrimary = Color.Black,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E)
)
