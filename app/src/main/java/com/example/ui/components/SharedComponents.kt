package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContaPGC
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ValorMonetarioText(
    valor: Double,
    modifier: Modifier = Modifier,
    style: ContaMonetariaTextStyle = ContaMonetariaTextStyle.Body,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    val locale = Locale("pt", "MZ")
    val formatter = NumberFormat.getCurrencyInstance(locale).apply {
        currency = java.util.Currency.getInstance("MZN")
    }
    val formatted = try {
        formatter.format(valor)
    } catch (e: Exception) {
        String.format("%.2f MZN", valor)
    }

    Text(
        text = formatted,
        modifier = modifier,
        color = color,
        fontWeight = when (style) {
            ContaMonetariaTextStyle.Header -> FontWeight.Black
            ContaMonetariaTextStyle.Subheader, ContaMonetariaTextStyle.Bold -> FontWeight.Bold
            else -> FontWeight.Medium
        },
        fontSize = when (style) {
            ContaMonetariaTextStyle.Header -> 24.sp
            ContaMonetariaTextStyle.Subheader -> 18.sp
            ContaMonetariaTextStyle.Bold -> 14.sp
            ContaMonetariaTextStyle.Body -> 13.sp
            ContaMonetariaTextStyle.Small -> 11.sp
        },
        style = TextStyle(
            fontFeatureSettings = "tnum",
            fontFamily = FontFamily.Monospace,
            letterSpacing = if (style == ContaMonetariaTextStyle.Header) (-0.8).sp else (-0.1).sp
        )
    )
}

enum class ContaMonetariaTextStyle {
    Header, Subheader, Body, Bold, Small
}

@Composable
fun ContaPGCChip(
    codigo: String,
    titulo: String,
    modifier: Modifier = Modifier
) {
    val classChar = codigo.firstOrNull() ?: '9'
    val (backgroundColor, textColor) = when (classChar) {
        '1' -> Pair(Color(0xFFEFF6FF), Color(0xFF1D4ED8)) // Blue
        '2' -> Pair(Color(0xFFECFDF5), Color(0xFF047857)) // Emerald
        '3' -> Pair(Color(0xFFFFF7ED), Color(0xFFC2410C)) // Orange / Amber
        '4' -> Pair(Color(0xFFFAF5FF), Color(0xFF7E22CE)) // Purple
        '5' -> Pair(Color(0xFFECFEFF), Color(0xFF0E7490)) // Cyan
        '6' -> Pair(Color(0xFFFFF1F2), Color(0xFFBE123C)) // Rose (Passivo/Despesa)
        '7' -> Pair(Color(0xFFEEF2FF), Color(0xFF4338CA)) // Indigo
        else -> Pair(Slate100, Slate600) // Slate Gray
    }
    
    val stampShape = RoundedCornerShape(4.dp)

    Box(
        modifier = modifier
            .clip(stampShape)
            .border(1.dp, textColor.copy(alpha = 0.25f), stampShape)
            .background(backgroundColor.copy(alpha = 0.95f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$codigo · $titulo",
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.1.sp
        )
    }
}

@Composable
fun SituacaoPatrimonialBadge(
    situacao: String,
    colorName: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (colorName) {
        "VERDE" -> Triple(StatusGoodBg, StatusGoodFg, Icons.Default.Check) // Boa
        "AMARELO" -> Triple(StatusWarnBg, StatusWarnFg, Icons.Default.Warning) // Menos Boa
        "VERMELHO" -> Triple(StatusBadBg, StatusBadFg, Icons.Default.Close) // Pessima
        else -> Triple(Color(0xFFF5F5F5), Color(0xFF616161), Icons.Default.Warning)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(AppRadius.sm))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "SITUAÇÃO: ${situacao.uppercase()}",
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )
    }
}

