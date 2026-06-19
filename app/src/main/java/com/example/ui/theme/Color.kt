package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ===== NEUTRAL SCALE (Slate) =====
val Slate950 = Color(0xFF0A0F1A)
val Slate900 = Color(0xFF0F172A)
val Slate850 = Color(0xFF151E32)
val Slate800 = Color(0xFF1E293B)
val Slate700 = Color(0xFF334155)
val Slate600 = Color(0xFF475569)
val Slate500 = Color(0xFF64748B)
val Slate400 = Color(0xFF94A3B8)
val Slate300 = Color(0xFFCBD5E1)
val Slate200 = Color(0xFFE2E8F0)
val Slate100 = Color(0xFFF1F5F9)
val Slate50  = Color(0xFFF8FAFC)

// ===== BRAND =====
val BrandPrimary = Color(0xFF0F172A)      // Slate profundo — base de marca
val BrandTeal = Color(0xFF0D9488)         // Teal — activo / positivo
val BrandTealLight = Color(0xFF2DD4BF)    // Teal claro — glow / gradiente
val BrandOcean = Color(0xFF0284C7)        // Azul — acções secundárias
val BrandAmber = Color(0xFFD97706)        // Âmbar — capital próprio
val BrandAmberLight = Color(0xFFFBBF24)
val BrandRose = Color(0xFFE11D48)         // Rosa — passivo / erro
val BrandRoseLight = Color(0xFFFB7185)

// ===== SEMANTIC SURFACES (Light) =====
val SurfaceCanvasLight = Color(0xFFF6F7FB)   // fundo geral (não branco puro)
val SurfaceCardLight = Color(0xFFFFFFFF)
val SurfaceElevatedLight = Color(0xFFFFFFFF)
val SurfaceSunkenLight = Color(0xFFEEF1F6)   // inputs, áreas "encaixadas"

// ===== SEMANTIC SURFACES (Dark) =====
val SurfaceCanvasDark = Slate950
val SurfaceCardDark = Slate850
val SurfaceElevatedDark = Slate800
val SurfaceSunkenDark = Color(0xFF0C1220)

// ===== STATUS =====
val StatusGoodBg = Color(0xFFECFDF5)
val StatusGoodFg = Color(0xFF047857)
val StatusWarnBg = Color(0xFFFFFBEB)
val StatusWarnFg = Color(0xFFB45309)
val StatusBadBg = Color(0xFFFFF1F2)
val StatusBadFg = Color(0xFFBE123C)

// Dark mode equivalents (fundos translúcidos sobre Slate)
val StatusGoodBgDark = Color(0x1A10B981)
val StatusWarnBgDark = Color(0x1AF59E0B)
val StatusBadBgDark = Color(0x1AF43F5E)

// ===== ALIASES (Compatibility) =====
val ModernPrimary = Slate900
val ModernSecondary = BrandTeal
val ModernTertiary = BrandOcean

val AssetColor = BrandTeal
val LiabilityColor = BrandRose
val EquityColor = BrandAmber

val GridBackground = SurfaceCanvasLight
val ErrorColor = BrandRose


