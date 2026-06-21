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

// === Fundo / Superfícies (Elegant Dark) ===
val EspressoBlack   = Color(0xFF1C1B1F)  // Fundo principal (background)
val CocoaDark       = Color(0xFF2B2930)  // Cards / containers
val LatteSlate      = Color(0xFF353439)  // Superfícies elevadas (stats, filtros, inputs preenchidos)

// === Cores de Destaque (Accent) ===
val AmberBronze     = Color(0xFFD0BCFF)  // Cor primária / accent (roxo claro M3)
val CreamBronze     = Color(0xFFCCC2DC)  // Accent secundário
val TerracottaPastel = Color(0xFFEFB8C8) // Accent terciário

// === Tons Neutros Suaves ===
val SoftClay        = Color(0xFFE6E1E5)  // Texto principal (alto contraste)
val WarmGrey        = Color(0xFF938F99)  // Texto secundário / muted
val ClayDivider      = Color(0xFF49454F)  // Divisores e bordas subtis

// === Cores de Estado (Pills / Badges) ===
val StatPending      = Color(0xFF49454F)  // Não iniciado
val StatProgress     = Color(0xFF381E72)  // Em progresso (roxo profundo)
val StatCompleted    = Color(0xFF8C1D18)  // Concluído mas não enviado (vermelho/terracota — crítico!)
val StatDelivered    = Color(0xFF211F26)  // Entregue (carvão)

// === Cores Financeiras ===
val MoneyGreen       = Color(0xFFD0BCFF)  // Totalmente pago
val MoneyOrange      = Color(0xFFE8DEF8)  // Pagamento parcial
val MoneyRed         = Color(0xFFF2B8B5)  // Por pagar / dívida

// ===== BRAND (Capulana Financeira style) =====
val IndigoCapulana = Color(0xFF1E2768)      // Azul-índigo profundo — base institucional moçambicana
val EsmeraldaMetical = Color(0xFF0F7A5C)    // Verde-esmeralda terroso — activo / positivo
val EsmeraldaGlow = Color(0xFF34D399)       // Verde claro vibrante para glows e gradientes
val AmbarSelo = Color(0xFFC2780C)           // Âmbar oficial — capital próprio / avisos regulatórios
val TerracotaArquivo = Color(0xFF9A4A2B)    // Terracota — selos de cartório/notariado, usado com extrema raridade

val BrandPrimary = IndigoCapulana
val BrandTeal = EsmeraldaMetical
val BrandTealLight = EsmeraldaGlow
val BrandOcean = Color(0xFF0284C7)        // Azul — acções secundárias
val BrandAmber = AmbarSelo
val BrandAmberLight = Color(0xFFFBBF24)
val BrandRose = Color(0xFFBE123C)         // Rosa — passivo / erro
val BrandRoseLight = Color(0xFFFB7185)

// ===== SEMANTIC SURFACES (Light) =====
val SurfaceCanvasLight = Color(0xFFFAF6F4)   // Fundo creme claro da Capulana Light Scheme
val SurfaceCardLight = Color(0xFFF0E6E1)
val SurfaceElevatedLight = Color(0xFFF0E6E1)
val SurfaceSunkenLight = Color(0xFFE8DAD3)   // Inputs, áreas "encaixadas"

// ===== SEMANTIC SURFACES (Dark) =====
val SurfaceCanvasDark = EspressoBlack
val SurfaceCardDark = CocoaDark
val SurfaceElevatedDark = LatteSlate
val SurfaceSunkenDark = LatteSlate

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
val ModernPrimary = EspressoBlack
val ModernSecondary = BrandTeal
val ModernTertiary = BrandOcean

val AssetColor = BrandTeal
val LiabilityColor = BrandRose
val EquityColor = BrandAmber

val GridBackground = SurfaceCanvasLight
val ErrorColor = BrandRose


