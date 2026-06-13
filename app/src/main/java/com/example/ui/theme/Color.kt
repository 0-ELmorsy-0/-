package com.example.ui.theme

import androidx.compose.ui.graphics.Color

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme

val MedicalBlue = Color(0xFF2B8B75)      // Teal green
val MedicalGreen = Color(0xFF0D9488)     // Teal Green clinical accent (very modern and comforting)
val SoftBlueBg = Color(0xFFF2F8F6)       // Very pale mint/teal background
val LightYellow = Color(0xFFFFB020)      // High contrast warning/star yellow

val DarkNavy: Color
  @Composable
  get() = if (MaterialTheme.colorScheme.background == DarkBackground) Color(0xFFF1F5F9) else Color(0xFF0F172A)

val BodyGray: Color
  @Composable
  get() = if (MaterialTheme.colorScheme.background == DarkBackground) Color(0xFF94A3B8) else Color(0xFF475569)

val MedicalBlueDark = Color(0xFF1D4ED8)  // Primary dark theme variant
val MedicalGreenDark = Color(0xFF0F766E) // Secondary dark theme variant
val DarkBackground = Color(0xFF0B132B)   // Luxury navy dark background
val DarkCard = Color(0xFF1C2541)         // Depth surface level for dark mode

