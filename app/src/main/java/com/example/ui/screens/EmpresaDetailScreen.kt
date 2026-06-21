package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ElementoPatrimonial
import com.example.ui.components.ContaPGCChip
import com.example.ui.components.ContaMonetariaTextStyle
import com.example.ui.components.SituacaoPatrimonialBadge
import com.example.ui.components.ValorMonetarioText
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpresaDetailScreen(
    viewModel: AccountingViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddElemento: () -> Unit,
    onNavigateToInventario: () -> Unit,
    onNavigateToBalanco: () -> Unit,
    modifier: Modifier = Modifier
) {
    val empresa by viewModel.selectedEmpresa.collectAsState()
    val elementos by viewModel.elementos.collectAsState()
    val resumo by viewModel.resumoPatrimonial.collectAsState()
    val contasMap by viewModel.contasMap.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = empresa?.nome ?: "Detalhes da Empresa",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        empresa?.let {
                            Text(
                                text = "NUIT: ${it.nuit ?: "Sem NUIT"}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { viewModel.toggleTheme() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("detail_theme_toggle_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.NightsStay else Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isDarkMode) "Escuro" else "Claro",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                    }

                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar Empresa",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            val emp = empresa
            if (emp == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    item {
                        // Resumo Patrimonial Card (Fully Polished Hero Treatment conforming to Section 4.2 Stat Cards)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .softCardShadow(radius = AppRadius.lg, elevation = 6.dp),
                            shape = RoundedCornerShape(AppRadius.lg),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, ClayDivider)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "RESUMO PATRIMONIAL",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                val statScrollState = rememberScrollState()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(statScrollState),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Activo Stat Card
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = LatteSlate),
                                        shape = RoundedCornerShape(AppRadius.md),
                                        border = BorderStroke(1.dp, ClayDivider.copy(alpha = 0.5f)),
                                        modifier = Modifier.widthIn(min = 125.dp)
                                    ) {
                                        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                                            Box(
                                                modifier = Modifier
                                                    .width(4.dp)
                                                    .fillMaxHeight()
                                                    .background(EsmeraldaMetical)
                                            )
                                            Column(modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)) {
                                                Text(
                                                    text = "ACTIVO",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = WarmGrey,
                                                    letterSpacing = 1.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                ValorMonetarioText(
                                                    valor = resumo?.totalActivo ?: 0.0,
                                                    style = ContaMonetariaTextStyle.Subheader,
                                                    color = EsmeraldaGlow
                                                )
                                            }
                                        }
                                    }

                                    // Passivo Stat Card
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = LatteSlate),
                                        shape = RoundedCornerShape(AppRadius.md),
                                        border = BorderStroke(1.dp, ClayDivider.copy(alpha = 0.5f)),
                                        modifier = Modifier.widthIn(min = 125.dp)
                                    ) {
                                        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                                            Box(
                                                modifier = Modifier
                                                    .width(4.dp)
                                                    .fillMaxHeight()
                                                    .background(BrandRose)
                                            )
                                            Column(modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)) {
                                                Text(
                                                    text = "PASSIVO",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = WarmGrey,
                                                    letterSpacing = 1.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                ValorMonetarioText(
                                                    valor = resumo?.totalPassivo ?: 0.0,
                                                    style = ContaMonetariaTextStyle.Subheader,
                                                    color = BrandRoseLight
                                                )
                                            }
                                        }
                                    }

                                    // Capital Próprio Stat Card
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = LatteSlate),
                                        shape = RoundedCornerShape(AppRadius.md),
                                        border = BorderStroke(1.dp, ClayDivider.copy(alpha = 0.5f)),
                                        modifier = Modifier.widthIn(min = 125.dp)
                                    ) {
                                        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                                            Box(
                                                modifier = Modifier
                                                    .width(4.dp)
                                                    .fillMaxHeight()
                                                    .background(AmbarSelo)
                                            )
                                            Column(modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)) {
                                                Text(
                                                    text = "C. PRÓPRIO",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = WarmGrey,
                                                    letterSpacing = 1.sp
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                ValorMonetarioText(
                                                    valor = resumo?.capitalProprio ?: 0.0,
                                                    style = ContaMonetariaTextStyle.Subheader,
                                                    color = BrandAmberLight
                                                )
                                            }
                                        }
                                    }
                                }

                                resumo?.let { r ->
                                    Spacer(modifier = Modifier.height(14.dp))
                                    HorizontalDivider(color = ClayDivider.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val totAct = r.totalActivo
                                        val cpValue = if (r.capitalProprio > 0.0) r.capitalProprio else 0.0
                                        val totPas = r.totalPassivo
                                        val sumAll = totAct + cpValue + totPas

                                        val wAct = if (sumAll > 0) (totAct / sumAll).toFloat().coerceAtLeast(0.08f) else 0.34f
                                        val wCp = if (sumAll > 0) (cpValue / sumAll).toFloat().coerceAtLeast(0.08f) else 0.33f
                                        val wPas = if (sumAll > 0) (totPas / sumAll).toFloat().coerceAtLeast(0.12f) else 0.33f

                                        Column(modifier = Modifier.weight(1.5f)) {
                                            Text(
                                                text = "Equilíbrio Patrimonial (Activo / C. Próprio / Passivo)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = WarmGrey
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(8.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(LatteSlate)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .weight(wAct)
                                                        .background(EsmeraldaMetical)
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .weight(wCp)
                                                        .background(AmbarSelo)
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxHeight()
                                                        .weight(wPas)
                                                        .background(BrandRose)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Act: ${(wAct * 100).toInt()}%", fontSize = 9.sp, color = EsmeraldaGlow, fontWeight = FontWeight.Bold)
                                                Text("C.P.: ${(wCp * 100).toInt()}%", fontSize = 9.sp, color = BrandAmberLight, fontWeight = FontWeight.Bold)
                                                Text("Pas: ${(wPas * 100).toInt()}%", fontSize = 9.sp, color = BrandRoseLight, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                                            SituacaoPatrimonialBadge(
                                                situacao = r.situacaoPatrimonial,
                                                colorName = r.situacaoColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // Action buttons segment (Redesigned as cards with nice badges and shadows)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                onClick = onNavigateToAddElemento,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(AppRadius.md),
                                modifier = Modifier
                                    .weight(1f)
                                    .softCardShadow(radius = AppRadius.md, elevation = 4.dp, color = BrandTeal)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Add Elemento", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, textAlign = TextAlign.Center)
                                }
                            }

                            Surface(
                                onClick = onNavigateToInventario,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(AppRadius.md),
                                modifier = Modifier
                                    .weight(1.1f)
                                    .softCardShadow(radius = AppRadius.md, elevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.secondary)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Gerar Inventário", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSecondaryContainer, textAlign = TextAlign.Center)
                                }
                            }

                            Surface(
                                onClick = onNavigateToBalanco,
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(AppRadius.md),
                                modifier = Modifier
                                    .weight(1.1f)
                                    .softCardShadow(radius = AppRadius.md, elevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.PieChart, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.tertiary)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Gerar Balanço", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onTertiaryContainer, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))

                        // Title section
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Elementos Patrimoniais (${elementos.size})",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = (-0.3).sp
                            )
                        }
                    }

                    if (elementos.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.List,
                                            contentDescription = null,
                                            modifier = Modifier.size(28.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Text(
                                        text = "Nenhum Elemento Patrimonial",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Registe todos os bens, caixas, dívidas, empréstimos e capitais da empresa para consolidar o relatório.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(elementos, key = { it.id }) { item ->
                            val conta = contasMap[item.contaCodigo]
                            val isPassivo = conta?.natureza == "PASSIVO"

                            Card(
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, ClayDivider),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 5.dp)
                                    .softCardShadow(radius = 16.dp, elevation = 2.dp)
                            ) {
                                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                                    // Left Indicator Accent Bar - high craftsmanship visual scanability
                                    Box(
                                        modifier = Modifier
                                            .width(6.dp)
                                            .fillMaxHeight()
                                            .background(
                                                if (isPassivo) MaterialTheme.colorScheme.error 
                                                else EsmeraldaMetical
                                            )
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.descricao,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            ContaPGCChip(
                                                codigo = item.contaCodigo,
                                                titulo = conta?.titulo ?: "Classificação PGC"
                                            )
                                            if (item.quantidade > 1.0 && item.valorUnitario != null) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "${String.format("%.1f", item.quantidade)} × ${String.format("%.2f MZN", item.valorUnitario)}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            ValorMonetarioText(
                                                valor = item.valor,
                                                style = ContaMonetariaTextStyle.Bold,
                                                color = if (isPassivo) MaterialTheme.colorScheme.error else EsmeraldaMetical
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            IconButton(
                                                onClick = { viewModel.deleteElemento(item) },
                                                modifier = Modifier.size(48.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteOutline,
                                                    contentDescription = "Remover",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                                    modifier = Modifier.size(20.dp)
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

            if (showDeleteConfirm) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirm = false },
                    title = { Text("Eliminar Empresa?", fontWeight = FontWeight.Bold) },
                    text = { Text("Esta acção eliminará permanente a empresa e todos os seus elementos patrimoniais. Não poderá ser revertida.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                val empToDelete = empresa
                                if (empToDelete != null) {
                                    viewModel.deleteEmpresa(empToDelete) {
                                        showDeleteConfirm = false
                                        onNavigateBack()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Sim, Eliminar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirm = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}
