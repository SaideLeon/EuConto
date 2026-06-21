package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MonthlyFinancialData
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RechartsBarChart(
    data: List<MonthlyFinancialData>,
    currencySymbol: String = "MZN",
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val maxVal = remember(data) {
        val highest = data.maxOfOrNull { maxOf(it.lucro, it.prejuizo) } ?: 100000.0
        // Round to nearest clean ceiling
        val power = Math.pow(10.0, Math.floor(Math.log10(highest)))
        val multiplier = Math.ceil(highest / (power / 2.0))
        (multiplier * (power / 2.0)).coerceAtLeast(10000.0)
    }

    val locale = Locale("pt", "MZ")
    val formatter = remember {
        NumberFormat.getNumberInstance(locale).apply {
            maximumFractionDigits = 0
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .softCardShadow(radius = AppRadius.lg, elevation = 4.dp),
        shape = RoundedCornerShape(AppRadius.lg),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header with title and icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Demonstração de Resultados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = (-0.3).sp
                        )
                        Text(
                            text = "Série mensal histórica de Lucro vs Prejuízo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Interactive Tooltip (just like Recharts floating box)
            AnimatedContent(
                targetState = selectedIndex,
                transitionSpec = {
                    fadeIn() + slideInVertically() with fadeOut() + slideOutVertically()
                },
                label = "tooltip_anim"
            ) { targetIndex ->
                if (targetIndex != null && targetIndex in data.indices) {
                    val item = data[targetIndex]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Mês: ${getDescricaoMesCompleto(item.monthCode)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(EsmeraldaMetical)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Lucro: ${formatter.format(item.lucro)} $currencySymbol",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EsmeraldaMetical
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(BrandRose)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Prejuízo: ${formatter.format(item.prejuizo)} $currencySymbol",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BrandRose
                                    )
                                }
                            }
                            
                            val saldo = item.lucro - item.prejuizo
                            val saldoColor = if (saldo >= 0) EsmeraldaMetical else BrandRose
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "SALDO LÍQUIDO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "${if (saldo >= 0) "+" else ""}${formatter.format(saldo)} $currencySymbol",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = saldoColor
                                )
                            }
                        }
                    }
                } else {
                    // Default instructional tip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Toque em qualquer coluna do gráfico para ver o detalhamento do mês correspondente.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Legend Row (styled premium like Recharts)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(EsmeraldaMetical)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Lucro (Rendimentos)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(BrandRose)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Prejuízo (Gastos / Custos)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Chart area layout (Y-Axis ticks on left, scrollable bars on right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                // Y-Axis Ticks
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(end = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End
                ) {
                    val ticksCount = 5
                    for (i in ticksCount downTo 0) {
                        val tickVal = (maxVal / ticksCount) * i
                        Text(
                            text = formatLargeNumber(tickVal, currencySymbol),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp)) // Offset to align with X-axis
                }

                // Scrollable Grid and Bars Area
                val scrollState = rememberScrollState()
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .horizontalScroll(scrollState)
                ) {
                    // Back Grid Lines
                    Column(
                        modifier = Modifier
                            .width(680.dp)
                            .fillMaxHeight()
                            .padding(bottom = 22.dp), // Align with bar heights
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        val gridLines = 5
                        for (i in 0..gridLines) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(
                                        MaterialTheme.colorScheme.outlineVariant.copy(
                                            alpha = if (i == gridLines) 0.8f else 0.2f
                                        )
                                    )
                            )
                        }
                    }

                    // Columns (Bars) and X-Axis labels
                    Row(
                        modifier = Modifier
                            .width(680.dp)
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        data.forEachIndexed { index, item ->
                            val isSelected = selectedIndex == index
                            
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(48.dp)
                                    .clickable {
                                        selectedIndex = if (isSelected) null else index
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                // Double Bar layout side-by-side
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .padding(bottom = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    // Lucro Bar
                                    val profitHeightRatio = (item.lucro / maxVal).toFloat().coerceIn(0.02f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(profitHeightRatio)
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(
                                                if (isSelected) EsmeraldaGlow else EsmeraldaMetical
                                            )
                                    )

                                    // Prejuizo Bar
                                    val lossHeightRatio = (item.prejuizo / maxVal).toFloat().coerceIn(0.02f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(lossHeightRatio)
                                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                            .background(
                                                if (isSelected) Color(0xFFFDA4AF) else BrandRose
                                            )
                                    )
                                }

                                // X-Axis Label (Month abbreviation)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(18.dp),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    Text(
                                        text = item.monthName,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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

// Format numbers nicely: e.g. 150k MZN or 1.2M MZN
private fun formatLargeNumber(value: Double, currency: String): String {
    return when {
        value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000).replace(",", ".")
        value >= 1_000 -> String.format("%.0fk", value / 1_000).replace(",", ".")
        else -> String.format("%.0f", value)
    }
}

private fun getDescricaoMesCompleto(code: String): String {
    return when(code) {
        "01" -> "Janeiro"
        "02" -> "Fevereiro"
        "03" -> "Março"
        "04" -> "Abril"
        "05" -> "Maio"
        "06" -> "Junho"
        "07" -> "Julho"
        "08" -> "Agosto"
        "09" -> "Setembro"
        "10" -> "Outubro"
        "11" -> "Novembro"
        "12" -> "Dezembro"
        else -> "Mês $code"
    }
}
