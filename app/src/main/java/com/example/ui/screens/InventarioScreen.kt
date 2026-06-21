package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.model.Inventario
import com.example.ui.components.ContaMonetariaTextStyle
import com.example.ui.components.ValorMonetarioText
import com.example.ui.pdf.PdfGenerator
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    viewModel: AccountingViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val empresa by viewModel.selectedEmpresa.collectAsState()
    val classes by viewModel.inventarioClasses.collectAsState()
    val resumo by viewModel.resumoPatrimonial.collectAsState()
    val contas by viewModel.contas.collectAsState(initial = emptyList())

    // Config options state
    var dataRef by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
    var tipoDescricao by remember { mutableStateOf("ANALITICO") } // ANALITICO, SINTETICO
    var extensão by remember { mutableStateOf("GERAL") } // GERAL, PARCIAL
    var momento by remember { mutableStateOf("FINAL") } // INICIAL, FINAL, ORDINARIO, EXTRAORDINARIO

    var generatedFile by remember { mutableStateOf<File?>(null) }
    var operationMsg by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventário Classificado", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground) },
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
            // Configuration Parameters Form (Ecrã 5)
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
                            text = "CONFIGURAÇÕES DO RELATÓRIO",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        OutlinedTextField(
                            value = dataRef,
                            onValueChange = { dataRef = it },
                            label = { Text("Data de Referência") },
                            singleLine = true,
                            shape = RoundedCornerShape(AppRadius.sm),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                        // Tipo de Descrição
                        Text("Tipo de Descrição:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Card(
                                onClick = { tipoDescricao = "ANALITICO" },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (tipoDescricao == "ANALITICO") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                border = BorderStroke(1.dp, if (tipoDescricao == "ANALITICO") MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    RadioButton(selected = tipoDescricao == "ANALITICO", onClick = { tipoDescricao = "ANALITICO" })
                                    Text("Analítico", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Card(
                                onClick = { tipoDescricao = "SINTETICO" },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (tipoDescricao == "SINTETICO") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                border = BorderStroke(1.dp, if (tipoDescricao == "SINTETICO") MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    RadioButton(selected = tipoDescricao == "SINTETICO", onClick = { tipoDescricao = "SINTETICO" })
                                    Text("Sintético", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Extensão
                        Text("Extensão / Abrangência:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Card(
                                onClick = { extensão = "GERAL" },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (extensão == "GERAL") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                border = BorderStroke(1.dp, if (extensão == "GERAL") MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    RadioButton(selected = extensão == "GERAL", onClick = { extensão = "GERAL" })
                                    Text("Geral", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Card(
                                onClick = { extensão = "PARCIAL" },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (extensão == "PARCIAL") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                ),
                                border = BorderStroke(1.dp, if (extensão == "PARCIAL") MaterialTheme.colorScheme.primary else Color.Transparent),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    RadioButton(selected = extensão == "PARCIAL", onClick = { extensão = "PARCIAL" })
                                    Text("Parcial", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Momento
                        Text("Momento do Balancete:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("INICIAL" to "Abertura", "FINAL" to "Encerramento", "ORDINARIO" to "Anual").forEach { (key, label) ->
                                Card(
                                    onClick = { momento = key },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (momento == key) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    ),
                                    border = BorderStroke(1.dp, if (momento == key) MaterialTheme.colorScheme.primary else Color.Transparent),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        RadioButton(selected = momento == key, onClick = { momento = key })
                                        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Export Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val emp = empresa
                            val res = resumo
                            if (emp != null && res != null) {
                                val dummyRecord = Inventario(
                                    empresaId = emp.id,
                                    data = dataRef,
                                    tipo = extensão,
                                    descricao = tipoDescricao,
                                    momento = momento
                                )
                                val file = PdfGenerator.exportInventarioToPdf(context, emp, dummyRecord, classes, res, contas)
                                if (file != null) {
                                    generatedFile = file
                                    operationMsg = "PDF oficial do inventário exportado com sucesso!"
                                } else {
                                    operationMsg = "Erro na geração do PDF."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(14.dp),
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

            // Live Document Preview Container (Ecrã 7)
            item {
                Text(
                    text = "PRÉ-VISUALIZAÇÃO DO DOCUMENTO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .softCardShadow(radius = AppRadius.md, elevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(AppRadius.md)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Internal Document layout mimicking the print layout
                        Text(
                            text = "EMPRESA ${empresa?.nome?.uppercase(Locale.getDefault()) ?: ""}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = Color(0xFF1E293B),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "INVENTÁRIO PATRIMONIAL - $tipoDescricao ($momento)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF475569),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Moeda: ${empresa?.moeda} · Referência: $dataRef",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Tree ledger classes
                        for (classe in classes) {
                            val classBgColor = when (classe.classe) {
                                1, 2 -> Color(0xFFECFDF5) // Light emerald
                                3 -> Color(0xFFEEF2FF) // Light indigo
                                4 -> Color(0xFFFFF7ED) // Light orange / warm terracotta
                                5 -> Color(0xFFFFFBEB) // Light warm gold/amber
                                else -> Color(0xFFF8FAFC) // Slate gray
                            }
                            val classBorderColor = when (classe.classe) {
                                1, 2 -> EsmeraldaMetical
                                3 -> IndigoCapulana
                                4 -> TerracotaArquivo
                                5 -> AmbarSelo
                                else -> Slate400
                            }
                            val classTextColor = when (classe.classe) {
                                1, 2 -> Color(0xFF065F46)
                                3 -> Color(0xFF3730A3)
                                4 -> Color(0xFF9A3412)
                                5 -> Color(0xFF92400E)
                                else -> Color(0xFF334155)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(classBgColor)
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(classBorderColor)
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "CLASSE ${classe.classe}",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = classe.tituloClasse.uppercase(Locale.getDefault()),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = classTextColor
                                    )
                                }
                                ValorMonetarioText(valor = classe.subtotal, style = ContaMonetariaTextStyle.Bold, color = classTextColor)
                            }
                            Spacer(modifier = Modifier.height(4.dp))

                            for (grupo in classe.contas) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "  ${grupo.conta.codigo} ${grupo.conta.titulo}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFF334155),
                                        modifier = Modifier.weight(1f)
                                    )
                                    ValorMonetarioText(valor = grupo.subtotal, style = ContaMonetariaTextStyle.Bold, color = Color(0xFF334155))
                                }

                                if (tipoDescricao == "ANALITICO") {
                                    for (item in grupo.itens) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = 12.dp, top = 2.dp, bottom = 2.dp, end = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("    • ${item.descricao}", fontSize = 11.sp, color = Color(0xFF475569))
                                                if (item.quantidade > 1.0 && item.valorUnitario != null) {
                                                    Text(
                                                        text = "      ${String.format("%.1f", item.quantidade)} × ${String.format("%.2f MZN", item.valorUnitario)}",
                                                        fontSize = 9.sp,
                                                        color = Color(0xFF94A3B8)
                                                    )
                                                }
                                            }
                                            ValorMonetarioText(valor = item.valor, style = ContaMonetariaTextStyle.Small, color = Color(0xFF475569))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Summary Footers
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 2.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TOTAL DO ACTIVO (BENS + DIREITOS):", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = Color(0xFF0F172A))
                            ValorMonetarioText(valor = resumo?.totalActivo ?: 0.0, style = ContaMonetariaTextStyle.Bold, color = Color(0xFF0F172A))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TOTAL DO PASSIVO (OBRIGAÇÕES):", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                            ValorMonetarioText(valor = resumo?.totalPassivo ?: 0.0, style = ContaMonetariaTextStyle.Bold, color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("SITUAÇÃO PATRIMONIAL LÍQUIDA:", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = Color(0xFF0D9488))
                            ValorMonetarioText(valor = resumo?.capitalProprio ?: 0.0, style = ContaMonetariaTextStyle.Bold, color = Color(0xFF0D9488))
                        }

                        resumo?.let { r ->
                            Spacer(modifier = Modifier.height(14.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (r.situacaoColor == "VERDE") Color(0xFFF0FDF4) else if (r.situacaoColor == "AMARELO") Color(0xFFFFFBEB) else Color(0xFFFFF1F2)
                                    )
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "SITUAÇÃO PATRIMONIAL: ${r.situacaoPatrimonial.uppercase(Locale.getDefault())}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (r.situacaoColor == "VERDE") Color(0xFF15803D) else if (r.situacaoColor == "AMARELO") Color(0xFFB45309) else Color(0xFFBE123C)
                                )
                            }
                        }
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
        context.startActivity(Intent.createChooser(intent, "Partilhar Inventário PDF"))
    } catch (e: Exception) {
        // Fallback share logic if provider fails
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, android.net.Uri.fromFile(file))
        }
        context.startActivity(Intent.createChooser(intent, "Partilhar Inventário PDF"))
    }
}
