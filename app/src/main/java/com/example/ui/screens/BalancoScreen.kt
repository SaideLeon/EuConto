package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.model.Balanco
import com.example.ui.components.ContaMonetariaTextStyle
import com.example.ui.components.ValorMonetarioText
import com.example.ui.pdf.PdfGenerator
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalancoScreen(
    viewModel: AccountingViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val empresa by viewModel.selectedEmpresa.collectAsState()
    val balancoResult by viewModel.balancoCalculado.collectAsState()

    // Form inputs
    var dataAtual by remember { mutableStateOf(java.time.LocalDate.now().year.toString()) }
    var dataAnterior by remember { mutableStateOf("") }
    var tipoBalanco by remember { mutableStateOf("INICIAL") } // INICIAL, FINAL, ANALITICO, SINTETICO
    var observacoes by remember { mutableStateOf("") }

    var generatedFile by remember { mutableStateOf<File?>(null) }
    var operationMsg by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Balanço Patrimonial", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Configuration Parameters Form (Ecrã 6)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .softCardShadow(radius = AppRadius.lg, elevation = 4.dp),
                    shape = RoundedCornerShape(AppRadius.lg),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "CONFIGURAÇÕES DO BALANÇO",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        OutlinedTextField(
                            value = dataAtual,
                            onValueChange = { dataAtual = it },
                            label = { Text("Ano / Exercício Actual") },
                            placeholder = { Text("Ex: 2026") },
                            singleLine = true,
                            shape = RoundedCornerShape(AppRadius.sm),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = dataAnterior,
                                onValueChange = { dataAnterior = it },
                                label = { Text("Exercício Anterior (opcional)") },
                                placeholder = { Text("Ex: 2025") },
                                singleLine = true,
                                shape = RoundedCornerShape(AppRadius.sm),
                                modifier = Modifier.weight(1f)
                            )
                            if (dataAnterior.isNotEmpty()) {
                                TextButton(
                                    onClick = { dataAnterior = "" },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Limpar", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                        // Tipo de Balanço
                        Text("Tipo de Balanço:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Card(
                                onClick = { tipoBalanco = "INICIAL" },
                                shape = RoundedCornerShape(AppRadius.xs),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (tipoBalanco == "INICIAL") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                border = BorderStroke(1.dp, if (tipoBalanco == "INICIAL") MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    RadioButton(selected = tipoBalanco == "INICIAL", onClick = { tipoBalanco = "INICIAL" })
                                    Text("Abertura", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Card(
                                onClick = { tipoBalanco = "FINAL" },
                                shape = RoundedCornerShape(AppRadius.xs),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (tipoBalanco == "FINAL") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                border = BorderStroke(1.dp, if (tipoBalanco == "FINAL") MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    RadioButton(selected = tipoBalanco == "FINAL", onClick = { tipoBalanco = "FINAL" })
                                    Text("Encerramento", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Real-time closing confirmation (R-03 / UC-05 - Polished layout)
            balancoResult?.let { b ->
                item {
                    val statusText = if (b.balancoFecha) "Balanço Fechado Correctamente" else "Diferença Pendente no Fecho"
                    val bColor = if (b.balancoFecha) EsmeraldaGlow else BrandRoseLight
                    val bgCol = LatteSlate
                    val indicatorColor = if (b.balancoFecha) EsmeraldaMetical else BrandRose

                    Card(
                        colors = CardDefaults.cardColors(containerColor = bgCol),
                        shape = RoundedCornerShape(AppRadius.md),
                        border = BorderStroke(1.dp, indicatorColor.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .softCardShadow(radius = AppRadius.md, elevation = 2.dp)
                    ) {
                        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                            // Left accent anchor
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .fillMaxHeight()
                                    .background(indicatorColor)
                            )

                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (b.balancoFecha) Icons.Default.Done else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = indicatorColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(statusText, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = bColor)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total Activos:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SoftClay)
                                    ValorMonetarioText(valor = b.totalActivos, style = ContaMonetariaTextStyle.Bold, color = if (b.balancoFecha) EsmeraldaGlow else SoftClay)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total CP + Passivos:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SoftClay)
                                    ValorMonetarioText(valor = b.totalCpEPassivo, style = ContaMonetariaTextStyle.Bold, color = SoftClay)
                                }
                            }
                        }
                    }
                }
            }

            // Actions Export
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val emp = empresa
                            val res = balancoResult
                            if (emp != null && res != null) {
                                val record = Balanco(
                                    empresaId = emp.id,
                                    dataAtual = dataAtual,
                                    dataAnterior = dataAnterior.ifBlank { null },
                                    tipo = tipoBalanco,
                                    observacoes = observacoes
                                )
                                val file = PdfGenerator.exportBalancoToPdf(context, emp, record, res)
                                if (file != null) {
                                    generatedFile = file
                                    operationMsg = "PDF oficial do Balanço exportado com sucesso!"
                                } else {
                                    operationMsg = "Erro na geração do PDF."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(14.dp),
                        enabled = balancoResult != null,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gerar PDF", fontWeight = FontWeight.Bold)
                    }

                    if (generatedFile != null) {
                        Button(
                            onClick = {
                                generatedFile?.let { file ->
                                    sharePdfFile(context, file)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Partilhar PDF", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (operationMsg.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, EsmeraldaGlow.copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(containerColor = LatteSlate)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(EsmeraldaMetical),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Done, contentDescription = null, tint = EspressoBlack, modifier = Modifier.size(14.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(operationMsg, fontSize = 13.sp, color = EsmeraldaGlow, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Document Live Preview Container (Ecrã 7)
            item {
                Text(
                    text = "PRÉ-VISUALIZAÇÃO DO BALANÇO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            balancoResult?.let { b ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .softCardShadow(radius = AppRadius.md, elevation = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(AppRadius.md)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Header
                            Text(
                                "BALANÇO PATRIMONIAL EXERCÍCIO - $tipoBalanco",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = Color(0xFF1E293B),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "EMPRESA: ${empresa?.nome?.uppercase(Locale.getDefault())}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF475569),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Exercício Actual: $dataAtual ${if (dataAnterior.isNotEmpty()) "· Comparativo: $dataAnterior" else ""}",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(12.dp))

                            // 1. ACTIVOS
                            Text("ACTIVOS", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = Color(0xFF0F172A))
                            Spacer(modifier = Modifier.height(6.dp))

                            // Activos Não Correntes
                            Text("  Activos Não Correntes", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF334155))
                            for (gc in b.activosNaoCorrentes) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${gc.conta.codigo} ${gc.conta.titulo}", fontSize = 11.sp, color = Color(0xFF475569), modifier = Modifier.weight(1f))
                                    ValorMonetarioText(valor = gc.subtotal, style = ContaMonetariaTextStyle.Small, color = Color(0xFF475569))
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("  Total Activos Não Correntes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                ValorMonetarioText(valor = b.totalActivosNaoCorrentes, style = ContaMonetariaTextStyle.Bold, color = Color(0xFF1E293B))
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Activos Correntes
                            Text("  Activos Correntes", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF334155))
                            for (gc in b.activosCorrentes) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${gc.conta.codigo} ${gc.conta.titulo}", fontSize = 11.sp, color = Color(0xFF475569), modifier = Modifier.weight(1f))
                                    ValorMonetarioText(valor = gc.subtotal, style = ContaMonetariaTextStyle.Small, color = Color(0xFF475569))
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("  Total Activos Correntes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                ValorMonetarioText(valor = b.totalActivosCorrentes, style = ContaMonetariaTextStyle.Bold, color = Color(0xFF1E293B))
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("TOTAL DOS ACTIVOS (A)", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = Color(0xFF0F172A))
                                ValorMonetarioText(valor = b.totalActivos, style = ContaMonetariaTextStyle.Bold, color = Color(0xFF0F172A))
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 2.dp)
                            Spacer(modifier = Modifier.height(16.dp))

                            // 2. CAPITAL PROPRIO E PASSIVOS
                            Text("CAPITAL PRÓPRIO E PASSIVOS", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = Color(0xFF059669))
                            Spacer(modifier = Modifier.height(6.dp))

                            // Capital Próprio
                            Text("  Capital Próprio", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF065F46))
                            for (gc in b.capitalProprioItens) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${gc.conta.codigo} ${gc.conta.titulo}", fontSize = 11.sp, color = Color(0xFF475569), modifier = Modifier.weight(1f))
                                    ValorMonetarioText(valor = gc.subtotal, style = ContaMonetariaTextStyle.Small, color = Color(0xFF475569))
                                }
                            }
                            // Automated dynamic Net Results insertion matching double-entry bookkeeping (R-03 / UC-05)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("  8.8 Resultado Líquido do Período", fontSize = 11.sp, color = Color(0xFF475569), modifier = Modifier.weight(1f))
                                ValorMonetarioText(valor = b.resultadoLiquidoPeriodo, style = ContaMonetariaTextStyle.Small, color = Color(0xFF475569))
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("  Total de Capital Próprio", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
                                ValorMonetarioText(valor = b.totalCapitalProprio, style = ContaMonetariaTextStyle.Bold, color = Color(0xFF065F46))
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            // Passivos Não Correntes
                            Text("  Passivos Não Correntes", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF334155))
                            for (gc in b.passivosNaoCorrentes) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${gc.conta.codigo} ${gc.conta.titulo}", fontSize = 11.sp, color = Color(0xFF475569), modifier = Modifier.weight(1f))
                                    ValorMonetarioText(valor = gc.subtotal, style = ContaMonetariaTextStyle.Small, color = Color(0xFF475569))
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("  Total Passivos Não Correntes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                ValorMonetarioText(valor = b.totalPassivosNaoCorrentes, style = ContaMonetariaTextStyle.Bold, color = Color(0xFF1E293B))
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // Passivos Correntes
                            Text("  Passivos Correntes", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF334155))
                            for (gc in b.passivosCorrentes) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${gc.conta.codigo} ${gc.conta.titulo}", fontSize = 11.sp, color = Color(0xFF475569), modifier = Modifier.weight(1f))
                                    ValorMonetarioText(valor = gc.subtotal, style = ContaMonetariaTextStyle.Small, color = Color(0xFF475569))
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("  Total Passivos Correntes", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                ValorMonetarioText(valor = b.totalPassivosCorrentes, style = ContaMonetariaTextStyle.Bold, color = Color(0xFF1E293B))
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("TOTAL CP + PASSIVOS (CP+P)", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = Color(0xFF0D9488))
                                ValorMonetarioText(valor = b.totalCpEPassivo, style = ContaMonetariaTextStyle.Bold, color = Color(0xFF0D9488))
                            }
                        }
                    }
                }
            } ?: run {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Sem dados patrimoniais suficientes para pré-visualização.", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

private fun sharePdfFile(context: Context, file: File) {
    try {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Partilhar Balanço PDF"))
    } catch (e: Exception) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, android.net.Uri.fromFile(file))
        }
        context.startActivity(Intent.createChooser(intent, "Partilhar Balanço PDF"))
    }
}
