package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
                Column(modifier = Modifier.fillMaxSize()) {
                    // Resumo Patrimonial Card (Fully Polished Hero Treatment)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .softCardShadow(radius = AppRadius.xl, elevation = 10.dp, color = IndigoCapulana),
                        shape = RoundedCornerShape(AppRadius.xl),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "RESUMO PATRIMONIAL",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(18.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("ACTIVO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ValorMonetarioText(
                                        valor = resumo?.totalActivo ?: 0.0,
                                        style = ContaMonetariaTextStyle.Subheader,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                VerticalDivider(modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Text("PASSIVO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ValorMonetarioText(
                                        valor = resumo?.totalPassivo ?: 0.0,
                                        style = ContaMonetariaTextStyle.Subheader,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                VerticalDivider(modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1.5f)) {
                                    Text("C. PRÓPRIO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    ValorMonetarioText(
                                        valor = resumo?.capitalProprio ?: 0.0,
                                        style = ContaMonetariaTextStyle.Subheader,
                                        color = EsmeraldaMetical
                                    )
                                }
                            }

                            resumo?.let { r ->
                                Spacer(modifier = Modifier.height(18.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Visual Balance Indicator bar - Segmented stacked bar (Ativo / Capital / Passivo)
                                    val totAct = r.totalActivo
                                    val cpValue = if (r.capitalProprio > 0.0) r.capitalProprio else 0.0
                                    val totPas = r.totalPassivo
                                    val sumAll = totAct + cpValue + totPas

                                    val wAct = if (sumAll > 0) (totAct / sumAll).toFloat().coerceAtLeast(0.08f) else 0.34f
                                    val wCp = if (sumAll > 0) (cpValue / sumAll).toFloat().coerceAtLeast(0.08f) else 0.33f
                                    val wPas = if (sumAll > 0) (totPas / sumAll).toFloat().coerceAtLeast(0.12f) else 0.33f

                                    Column(modifier = Modifier.weight(1.5f)) {
                                        Text(
                                            text = "Equilíbrio (Activo / C. Próprio / Passivo)",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(10.dp)
                                                .clip(RoundedCornerShape(5.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
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
                                            Text("Act: ${(wAct * 100).toInt()}%", fontSize = 9.sp, color = EsmeraldaMetical, fontWeight = FontWeight.Bold)
                                            Text("C.P.: ${(wCp * 100).toInt()}%", fontSize = 9.sp, color = AmbarSelo, fontWeight = FontWeight.Bold)
                                            Text("Pas: ${(wPas * 100).toInt()}%", fontSize = 9.sp, color = BrandRose, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
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

                    if (elementos.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
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
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(elementos, key = { it.id }) { item ->
                                val conta = contasMap[item.contaCodigo]
                                val isPassivo = conta?.natureza == "PASSIVO"

                                Card(
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    shape = RoundedCornerShape(AppRadius.md),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .softCardShadow(radius = AppRadius.md, elevation = 2.dp)
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
