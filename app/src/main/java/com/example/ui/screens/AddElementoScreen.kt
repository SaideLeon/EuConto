package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContaPGC
import com.example.ui.viewmodel.AccountingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddElementoScreen(
    viewModel: AccountingViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contas by viewModel.contas.collectAsState(initial = emptyList())

    // Form states
    var descricao by remember { mutableStateOf("") }
    var calculoPorQtd by remember { mutableStateOf(false) }
    var quantidadeStr by remember { mutableStateOf("1") }
    var valorUnitarioStr by remember { mutableStateOf("") }
    var valorTotalStr by remember { mutableStateOf("") }
    var prazoMesesStr by remember { mutableStateOf("") }

    // Account lookup search state
    var selectedConta by remember { mutableStateOf<ContaPGC?>(null) }
    var searchAccountQuery by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    // Filter accounts list matching search query
    val filteredContas = remember(contas, searchAccountQuery) {
        if (searchAccountQuery.isBlank()) {
            contas.take(15) // Keep it fast & lightweight
        } else {
            contas.filter { c ->
                c.codigo.contains(searchAccountQuery, ignoreCase = true) ||
                        c.titulo.contains(searchAccountQuery, ignoreCase = true)
            }
        }
    }

    // Calculated total value derived in real-time
    val calculatedTotal: Double = remember(calculoPorQtd, quantidadeStr, valorUnitarioStr, valorTotalStr) {
        if (calculoPorQtd) {
            val q = quantidadeStr.toDoubleOrNull() ?: 1.0
            val u = valorUnitarioStr.toDoubleOrNull() ?: 0.0
            q * u
        } else {
            valorTotalStr.toDoubleOrNull() ?: 0.0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Elemento Patrimonial", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = MaterialTheme.colorScheme.onBackground)
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
            // Section 1: Descrição
            item {
                Text(
                    text = "DESCRIÇÃO DO BEM OU OBRIGAÇÃO",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    placeholder = { Text("Ex: Viatura Toyota Hilux, Caixa em Dinheiro") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Section 2: Toggle de calculo
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Calcular por Quantidade?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Switch(
                            checked = calculoPorQtd,
                            onCheckedChange = { calculoPorQtd = it }
                        )
                    }
                }
            }

            item {
                AnimatedContent(
                    targetState = calculoPorQtd,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "toggleVal"
                ) { mode ->
                    if (mode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = quantidadeStr,
                                onValueChange = { quantidadeStr = it },
                                label = { Text("Qtd") },
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = valorUnitarioStr,
                                onValueChange = { valorUnitarioStr = it },
                                label = { Text("Valor Unitário") },
                                prefix = { Text("MZN ") },
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(2f)
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = valorTotalStr,
                            onValueChange = { valorTotalStr = it },
                            label = { Text("Valor Total") },
                            prefix = { Text("MZN ") },
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Read-only calculated totals
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Valor Total Calculado:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(
                            text = String.format("%.2f MZN", calculatedTotal),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Section 3: Ledger lookup search & results (Código de contas)
            item {
                Text(
                    text = "CLASSIFICAÇÃO DO PATRIMÓNIO (PGC-NIRF)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
                OutlinedTextField(
                    value = searchAccountQuery,
                    onValueChange = { searchAccountQuery = it },
                    placeholder = { Text("Ex: Viatura, Caixa, Banco, Fornecedores") },
                    trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // List match results inline (Ecrã 4)
            if (selectedConta == null) {
                items(filteredContas) { c ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedConta = c
                                searchAccountQuery = "${c.codigo} - ${c.titulo}"
                            },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(c.codigo, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(c.titulo, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            } else {
                item {
                    val s = selectedConta!!
                    Card(
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0)), // Light Emerald outline
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)), // Slate Light Green
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("CONTA SELECCIONADA", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF047857))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("${s.codigo} · ${s.titulo}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF064E3B))
                                }
                                TextButton(
                                    onClick = {
                                        selectedConta = null
                                        searchAccountQuery = ""
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Limpar", fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFD1FAE5))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Automatic Classification badges readouts derived from metadata PGC properties (R-01)
                            Text("Classificação de Livros:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF064E3B))
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("NATUREZA: ${s.natureza}", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF047857))
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text("SUB-LIVRO: ${s.typeCorrencia ?: "Capital / Gasto"}", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF047857))
                                }
                            }
                        }
                    }
                }

                // If account code starts with 4.3 (Empréstimos), show Timeline Prazo meses field (R-02)
                if (selectedConta!!.codigo.startsWith("4.3")) {
                    item {
                        Text(
                            text = "PRAZO DE REEMBOLSO (MESES)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = prazoMesesStr,
                            onValueChange = { prazoMesesStr = it },
                            placeholder = { Text("Ex: 6 (Corrente) ou 24 (Não Corrente)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Save actions
            item {
                if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = MaterialTheme.colorScheme.error, fontSize = 13.sp, modifier = Modifier.padding(vertical = 4.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (descricao.isBlank()) {
                            errorMsg = "A descrição do elemento é obrigatória."
                        } else if (calculatedTotal <= 0.0) {
                            errorMsg = "O valor total deve ser positivo."
                        } else if (selectedConta == null) {
                            errorMsg = "Deve associar uma conta PGC-NIRF do catálogo."
                        } else {
                            val quant = quantidadeStr.toDoubleOrNull() ?: 1.0
                            val uVal = valorUnitarioStr.toDoubleOrNull()
                            val pMeses = prazoMesesStr.toIntOrNull()

                            viewModel.addElemento(
                                descricao = descricao,
                                valor = calculatedTotal,
                                quantidade = quant,
                                valorUnitario = if (calculoPorQtd) uVal else null,
                                contaCodigo = selectedConta!!.codigo,
                                dataInventario = java.time.LocalDate.now().toString(),
                                prazoMeses = pMeses
                            ) {
                                onNavigateBack()
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Guardar Elemento Patrimonial", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
