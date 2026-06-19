package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContaPGC
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
        fontWeight = if (style == ContaMonetariaTextStyle.Bold) FontWeight.Bold else FontWeight.Normal,
        fontSize = when (style) {
            ContaMonetariaTextStyle.Header -> 22.sp
            ContaMonetariaTextStyle.Subheader -> 16.sp
            ContaMonetariaTextStyle.Bold, ContaMonetariaTextStyle.Body -> 14.sp
            ContaMonetariaTextStyle.Small -> 11.sp
        },
        fontFamily = FontFamily.Monospace // Monospace matches perfectly centered decimals (JetBrains Mono vibe)
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
    val backgroundColor = when (classChar) {
        '1' -> Color(0xFFE3F2FD) // Light blue
        '2' -> Color(0xFFE8F5E9) // Light green
        '3' -> Color(0xFFFFF3E0) // Light orange
        '4' -> Color(0xFFF3E5F5) // Light purple
        '5' -> Color(0xFFE0F7FA) // Light cyan
        '6' -> Color(0xFFFFEBEE) // Light red
        '7' -> Color(0xFFE8EAF6) // Darker blue line
        else -> Color(0xFFECEFF1) // Light gray
    }

    val textColor = when (classChar) {
        '1' -> Color(0xFF1565C0)
        '2' -> Color(0xFF2E7D32)
        '3' -> Color(0xFFEF6C00)
        '4' -> Color(0xFF6A1B9A)
        '5' -> Color(0xFF00838F)
        '6' -> Color(0xFFC62828)
        '7' -> Color(0xFF283593)
        else -> Color(0xFF37474F)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$codigo · $titulo",
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SituacaoPatrimonialBadge(
    situacao: String,
    colorName: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (colorName) {
        "VERDE" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32)) // Boa
        "AMARELO" -> Pair(Color(0xFFFFFDE7), Color(0xFFF57F17)) // Menos Boa
        "VERMELHO" -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828)) // Pessima
        else -> Pair(Color(0xFFF5F5F5), Color(0xFF616161))
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(50))
                .background(textColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "SITUAÇÃO: ${situacao.uppercase()}",
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
