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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Empresa
import com.example.ui.theme.*
import com.example.ui.viewmodel.AccountingViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: AccountingViewModel,
    onNavigateToEmpresa: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val empresas by viewModel.empresas.collectAsState(initial = emptyList())
    val empresasResumo by viewModel.empresasResumo.collectAsState(initial = emptyMap())
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Eu Conto PGC-NIRF",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            letterSpacing = (-0.5).sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    var showKeyConfigDialog by remember { mutableStateOf(false) }
                    var apiKeyValue by remember { mutableStateOf("") }

                    IconButton(
                        onClick = {
                            apiKeyValue = com.example.data.gemini.GeminiClassifier.getStoredApiKey(context) ?: ""
                            showKeyConfigDialog = true
                        },
                        modifier = Modifier.testTag("dashboard_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configurações de API",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (showKeyConfigDialog) {
                        AlertDialog(
                            onDismissRequest = { showKeyConfigDialog = false },
                            shape = RoundedCornerShape(16.dp),
                            title = { Text("Configurar Chave API Gemini", fontWeight = FontWeight.Bold, color = IndigoCapulana) },
                            text = {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        text = "Insira sua chave de API pessoal do Gemini para usufruir de classificação automática com inteligência artificial.",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    OutlinedTextField(
                                        value = apiKeyValue,
                                        onValueChange = { apiKeyValue = it },
                                        placeholder = { Text("Cole sua chave AI_KEY aqui...") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(AppRadius.sm),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        modifier = Modifier.fillMaxWidth().testTag("config_api_key_field")
                                    )
                                    Text(
                                        text = "Caso não informe uma chave pessoal, a aplicação tentará utilizar a chave global definida do servidor (.env).",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        if (apiKeyValue.isNotBlank()) {
                                            com.example.data.gemini.GeminiClassifier.saveApiKey(context, apiKeyValue)
                                        } else {
                                            com.example.data.gemini.GeminiClassifier.clearApiKey(context)
                                        }
                                        showKeyConfigDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EsmeraldaMetical),
                                    shape = RoundedCornerShape(AppRadius.sm)
                                ) {
                                    Text("Guardar", fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showKeyConfigDialog = false }) {
                                    Text("Cancelar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(AppRadius.md),
                icon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp)) },
                text = { Text("Nova Empresa", style = MaterialTheme.typography.labelLarge) },
                modifier = Modifier.softCardShadow(radius = AppRadius.md, elevation = 6.dp, color = BrandTeal)
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
            if (empresas.isEmpty()) {
                // Modernized Empty State Placeholder with glowing primary tint container
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(28.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .softCardShadow(radius = 55.dp, elevation = 12.dp, color = TerracotaArquivo)
                            .background(
                                brush = AppGradients.HeroGradient,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            modifier = Modifier.size(52.dp),
                            tint = BrandAmberLight
                        )
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    Text(
                        text = "Nenhuma Empresa Registada",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Comece criando uma empresa para gerir os seus elementos patrimoniais, gerar inventários oficiais e balanços em Moçambique.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.82f)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(AppRadius.sm),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Configurar Nova Empresa", style = MaterialTheme.typography.labelLarge)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        // Fluid, modern gradient hero card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .softCardShadow(radius = AppRadius.lg, elevation = 6.dp),
                            shape = RoundedCornerShape(AppRadius.lg),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(brush = AppGradients.HeroGradient)
                                    .padding(22.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Gestão de Ativos & Balanços",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 19.sp,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        letterSpacing = (-0.5).sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Regule e categorize ativos, passivos e capital próprio em conformidade técnica com o PGC-NIRF de Moçambique.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "PORTFÓLIO DE EMPRESAS",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                        )
                    }

                    items(empresas) { emp ->
                        val resumo = empresasResumo[emp.id]
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(AppRadius.md),
                            modifier = Modifier
                                .fillMaxWidth()
                                .softCardShadow(radius = AppRadius.md, elevation = 4.dp)
                                .clickable {
                                    viewModel.selectEmpresa(emp.id)
                                    onNavigateToEmpresa(emp.id)
                                }
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val dotColor = when (resumo?.situacaoColor) {
                                            "VERDE" -> StatusGoodFg
                                            "AMARELO" -> StatusWarnFg
                                            "VERMELHO" -> StatusBadFg
                                            else -> Slate400
                                        }
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(dotColor)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = emp.nome,
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(AppRadius.xs))
                                            .background(MaterialTheme.colorScheme.primaryContainer)
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = emp.moeda,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Pin,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Actividade: ${emp.actividade}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${emp.cidade} · NUIT: ${emp.nuit ?: "Sem NUIT"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (showCreateDialog) {
                var nome by remember { mutableStateOf("") }
                var actividade by remember { mutableStateOf("") }
                var cidade by remember { mutableStateOf("") }
                var nuit by remember { mutableStateOf("") }
                var errorMessage by remember { mutableStateOf("") }

                AlertDialog(
                    onDismissRequest = { showCreateDialog = false },
                    shape = RoundedCornerShape(16.dp),
                    title = {
                        Text(
                            text = "Registar Nova Empresa",
                            fontWeight = FontWeight.Bold,
                            color = IndigoCapulana
                        )
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = nome,
                                onValueChange = { nome = it },
                                label = { Text("Nome da Empresa") },
                                placeholder = { Text("Ex: Viva Bem") },
                                singleLine = true,
                                shape = RoundedCornerShape(AppRadius.sm),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = actividade,
                                onValueChange = { actividade = it },
                                label = { Text("Actividade Comercial") },
                                placeholder = { Text("Ex: Venda de Materiais de Construção") },
                                singleLine = true,
                                shape = RoundedCornerShape(AppRadius.sm),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = cidade,
                                onValueChange = { cidade = it },
                                label = { Text("Cidade / Província") },
                                placeholder = { Text("Ex: Mocuba ou Maputo") },
                                singleLine = true,
                                shape = RoundedCornerShape(AppRadius.sm),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = nuit,
                                onValueChange = { nuit = it },
                                label = { Text("NUIT (Nº Identificação Tributária)") },
                                placeholder = { Text("Ex: 400123456") },
                                singleLine = true,
                                shape = RoundedCornerShape(AppRadius.sm),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (errorMessage.isNotEmpty()) {
                                Text(
                                    text = errorMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (nome.isBlank() || actividade.isBlank() || cidade.isBlank()) {
                                    errorMessage = "Preencha o nome, actividade e cidade."
                                } else {
                                    val data = LocalDate.now().toString()
                                    viewModel.insertEmpresa(
                                        nome = nome,
                                        actividade = actividade,
                                        cidade = cidade,
                                        nuit = nuit,
                                        dataRegisto = data
                                    ) { id ->
                                        showCreateDialog = false
                                        onNavigateToEmpresa(id)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EsmeraldaMetical),
                            shape = RoundedCornerShape(AppRadius.sm)
                        ) {
                            Text("Criar Empresa", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreateDialog = false }) {
                            Text("Cancelar", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                )
            }
        }
    }
}
