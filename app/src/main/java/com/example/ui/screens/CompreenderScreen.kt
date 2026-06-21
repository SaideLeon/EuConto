package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ValorMonetarioText
import com.example.ui.components.SituacaoPatrimonialBadge
import com.example.ui.components.ContaMonetariaTextStyle
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompreenderScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Top-level state: Selected Module ID. Null means showing Module Selector index.
    var selectedModuleId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Compreender & Estudar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (selectedModuleId == 1) "Módulo 1: RA3 · Património e Inventário" else "Manual do PGC-NIRF Moçambicano",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedModuleId != null) {
                                selectedModuleId = null
                            } else {
                                onNavigateBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (selectedModuleId == null) {
                ModuleSelector(
                    onSelectModule = { id ->
                        if (id == 1) selectedModuleId = 1
                    }
                )
            } else {
                ModuleReader(
                    moduleId = selectedModuleId!!,
                    onExitModule = { selectedModuleId = null }
                )
            }
        }
    }
}

@Composable
fun ModuleSelector(
    onSelectModule: (Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Graphic Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .softCardShadow(radius = AppRadius.lg, elevation = 4.dp),
                shape = RoundedCornerShape(AppRadius.lg)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = AppGradients.HeroGradient)
                        .padding(24.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ACADEMIA SAFIN",
                                color = BrandAmberLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Formação Integrada em Contabilidade",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Domine os fundamentos do PGC-NIRF e os Resultados de Aprendizagem (RA) exigidos pelo Sistema Nacional de Qualificações de Moçambique.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.88f)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "MÓDULOS DE APRENDIZAGEM",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Active Module 1: RA3
        item {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(AppRadius.md),
                modifier = Modifier
                    .fillMaxWidth()
                    .softCardShadow(radius = AppRadius.md, elevation = 3.dp)
                    .clickable { onSelectModule(1) }
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(EsmeraldaMetical.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = EsmeraldaMetical,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Módulo 1 · RA3",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "6 Lições + 1 Desafio Prático",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(EsmeraldaMetical.copy(alpha = 0.1f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ATIVO / GRÁTIS",
                                color = EsmeraldaMetical,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Património e Inventário na Óptica Contabilística",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Aprenda a definir Património, classificar elementos em Ativo e Passivo (Corrente vs Não Corrente), calcular a Situação Líquida (A-P) e movimentar Contas no Razão em T.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = BrandAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Formador Original: Dr. Changaue",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Começar Estudo",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Locked Module 2
        item {
            LockedModuleCard(
                moduleLabel = "Módulo 2 · RA4",
                title = "Estudo das Contas & Método Partidas Dobradas",
                description = "Lançamentos de diário, lançamentos de razão sistemáticos, reconciliações de caixa e inventário permanente sob as regras fiscais moçambicanas."
            )
        }

        // Locked Module 3
        item {
            LockedModuleCard(
                moduleLabel = "Módulo 3 · RA5",
                title = "O Balancete de Verificação e Retificações",
                description = "Estrutura do Balancete de 4, 6 e 8 colunas, retificações de fim de exercício, depreciações acumuladas e amortizações de ativos intangíveis."
            )
        }
    }
}

@Composable
fun LockedModuleCard(
    moduleLabel: String,
    title: String,
    description: String
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(AppRadius.md),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(AppRadius.md))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = moduleLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "BREVEMENTE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ModuleReader(
    moduleId: Int,
    onExitModule: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val tabs = listOf(
        "1. Conceito",
        "2. Classes",
        "3. Fórmulas",
        "4. Jargões",
        "5. O Razão",
        "6. Inventário/Balanço",
        "7. Desafio"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        // Lesson Body Card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedTab) {
                0 -> LessonIntro()
                1 -> LessonClasses()
                2 -> LessonFormulas()
                3 -> LessonJargons()
                4 -> LessonLedgerT()
                5 -> LessonBalances()
                6 -> LessonQuiz()
            }
        }
    }
}

// =================== TAB 1: INTRO ===================
@Composable
fun LessonIntro() {
    var checkAsset1 by remember { mutableStateOf<String?>(null) }
    
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "O Conceito de Património",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Text(
                text = "A contabilidade fundamenta-se sobre um pilar essencial: o registo e a monitorização de tudo o que uma entidade (pessoa ou empresa) possui e deve. Chamas-se a isto de Património.",
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(AppRadius.sm)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Definição Técnica (RA3):",
                        style = MaterialTheme.typography.titleMedium,
                        color = EsmeraldaMetical,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "“Património é um conjunto de bens, direitos e obrigações pertencentes a uma determinada entidade num dado momento.”",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            Text(
                text = "Os Três Pilares Constituintes:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PilarCard(
                    title = "Bens (Físicos)",
                    desc = "Tudo o que é material, tangível e que pertence à empresa.",
                    examples = "Ex: Máquinas, viatura de entrega, mercadoria em armazém, dinheiro na caixa da loja."
                )
                PilarCard(
                    title = "Direitos (A Receber)",
                    desc = "Saldos a favor da empresa mas que estão na posse de terceiros.",
                    examples = "Ex: Dívidas dos clientes a receber, capital depositado em contas bancárias no BCI/BIM."
                )
                PilarCard(
                    title = "Obrigações (A Pagar)",
                    desc = "Compromissos e dívidas assumidas para com terceiros domésticos ou externos.",
                    examples = "Ex: Empréstimo contraído no BCI a pagar, dívida ao fornecedor Muanaco Fumo, impostos e contribuições para o INSS."
                )
            }
        }

        item {
            InteractiveClassifierWidget()
        }
    }
}

@Composable
fun PilarCard(title: String, desc: String, examples: String) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f), RoundedCornerShape(AppRadius.sm))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = examples, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
        }
    }
}

@Composable
fun InteractiveClassifierWidget() {
    val itemsToClassify = listOf(
        Pair("Uma viatura Nissan da empresa", "BEM"),
        Pair("Dinheiro depositado no BCI", "DIREITO"),
        Pair("Empréstimo contraído no BIM a pagar", "OBRIGAÇÃO"),
        Pair("Edifício comercial arrendado a terceiros", "BEM"),
        Pair("Dívida do cliente Sr. Sozinho", "DIREITO"),
        Pair("Sacos de cimento para venda", "BEM"),
        Pair("Dívidas a pagar ao fornecedor", "OBRIGAÇÃO"),
        Pair("Contribuição devida ao INSS", "OBRIGAÇÃO")
    )
    
    var currentIndex by remember { mutableStateOf(0) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var points by remember { mutableStateOf(0) }
    var hasAnswered by remember { mutableStateOf(false) }

    val currentPair = itemsToClassify[currentIndex % itemsToClassify.size]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .border(1.dp, BrandAmber.copy(alpha = 0.4f), RoundedCornerShape(AppRadius.md)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(AppRadius.md)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DESAFIO DO PATRIMÓNIO",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandAmber,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Acertos: $points",
                    style = MaterialTheme.typography.labelMedium,
                    color = EsmeraldaMetical,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            
            Text(
                text = "Classifique o seguinte elemento patrimonial:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppRadius.sm))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentPair.first,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(18.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("BEM", "DIREITO", "OBRIGAÇÃO").forEach { category ->
                    val isSelected = selectedCategory == category
                    val isCorrect = currentPair.second == category
                    
                    Button(
                        onClick = {
                            if (!hasAnswered) {
                                selectedCategory = category
                                hasAnswered = true
                                if (isCorrect) points += 1
                            }
                        },
                        modifier = Modifier.weight(1f).height(40.dp),
                        enabled = !hasAnswered || isSelected,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (hasAnswered) {
                                if (isCorrect) StatusGoodFg else if (isSelected) StatusBadFg else MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(text = category, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (hasAnswered) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val correct = selectedCategory == currentPair.second
                    Text(
                        text = if (correct) "✓ Correto!" else "✗ Incorreto! O correto é ${currentPair.second}.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (correct) StatusGoodFg else StatusBadFg
                    )
                    
                    TextButton(
                        onClick = {
                            currentIndex = (currentIndex + 1) % itemsToClassify.size
                            selectedCategory = null
                            hasAnswered = false
                        }
                    ) {
                        Text("Avançar", fontWeight = FontWeight.Black)
                        Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}


// =================== TAB 2: CLASSES ===================
@Composable
fun LessonClasses() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Ativos e Passivos no PGC-NIRF",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Text(
                text = "Nesta lição agruparemos os Bens, Direitos e Obrigações nos dois grandes grupos das contas patrimoniais do PGC de Moçambique: Activo e Passivo.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("O ACTIVO", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Recurso controlado por uma entidade resultado de eventos passados e do qual flui benefício económico.\n\nFórmula: Bens + Direitos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("O PASSIVO", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Obrigação presente originária de eventos passados, cuja liquidação resulta em saída de recursos económicos.\n\nFórmula: Obrigações",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Classificação e Subgrupos (Corrente vs Não Corrente)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ClassGroupCard(
                    title = "1. Activo Não Corrente (Longo Prazo)",
                    desc = "Bens ou direitos de permanência superior a 1 ano. Não destinados a venda imediata.",
                    subtypes = "👉 Activos Tangíveis: Viatura comercial, computadores, edifício, armazém de cimento.\n👉 Activos Intangíveis (sem corpo físico): marcas protegidas, direitos autorais, licença de software, patentes industriais."
                )

                ClassGroupCard(
                    title = "2. Activo Corrente (Curto Prazo)",
                    desc = "Recursos realizáveis ou destinados a venda em menos de um ano.",
                    subtypes = "👉 Disponibilidades: Dinheiro na caixa, contas em bancos (BCI/BIM).\n👉 Inventários: Estoques de produtos para comercialização (ex: vestidos, sacos de cimento, pregos).\n👉 Dívidas de Terceiros: Clientes correntes a receber num curto prazo."
                )

                ClassGroupCard(
                    title = "3. Passivo Não Corrente",
                    desc = "Compromissos contratuais cujo vencimento de pagamento supera o prazo de um ano.",
                    subtypes = "👉 Exemplo: Um empréstimo contraído no BCI para reembolsar no prazo de 5 anos."
                )

                ClassGroupCard(
                    title = "4. Passivo Corrente",
                    desc = "Compromissos imediatos ou dívidas a pagar em menos de 1 ano.",
                    subtypes = "👉 Exemplo: Contribuições a pagar ao INSS no mês seguinte, dívida em conta-corrente de mercadorias junto ao fornecedor Muanaco."
                )
            }
        }
    }
}

@Composable
fun ClassGroupCard(title: String, desc: String, subtypes: String) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(AppRadius.sm))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(10.dp)
            ) {
                Text(
                    text = subtypes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


// =================== TAB 3: FORMULAS & CALCULATOR ===================
@Composable
fun LessonFormulas() {
    var assetStr by remember { mutableStateOf("300000") }
    var liabStr by remember { mutableStateOf("120000") }

    val assetVal = assetStr.toDoubleOrNull() ?: 0.0
    val liabVal = liabStr.toDoubleOrNull() ?: 0.0
    val formulaSL = assetVal - liabVal

    val (sit, sitColor) = when {
        formulaSL > 0 -> Pair("BOA", "VERDE")
        formulaSL < 0 -> Pair("PÉSSIMA", "VERMELHO")
        else -> Pair("MENOS BOA", "AMARELO")
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Equações do Valor do Património",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Text(
                text = "O Valor de Património de uma empresa é também rotulado por diversos nomes equivalentes na literatura contabilística do PGC de Moçambique:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(listOf(BrandPrimary, BrandTeal)),
                        shape = RoundedCornerShape(AppRadius.sm)
                    )
                    .padding(18.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Fórmula Geral (Equação Fundamental):",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "VP = (Bens + Direitos) - Obrigações",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ou seja:  VP = A – P   (Ativo - Passivo)",
                        color = BrandAmberLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "✓ Termos Equivalentes Usados nos Exames:", fontWeight = FontWeight.Bold)
                Text(text = "• CP = Capital Próprio", style = MaterialTheme.typography.bodyMedium)
                Text(text = "• PL = Património Líquido", style = MaterialTheme.typography.bodyMedium)
                Text(text = "• SL = Situação Líquida", style = MaterialTheme.typography.bodyMedium)
            }
        }

        item {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        }

        item {
            Text(
                text = "Simulador Dinâmico de Situação Patrimonial",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Text(
                text = "Insira os valores de Ativo e Passivo abaixo e veja se o Balanço de empresa 'fecha' com Situação Boa, Menos boa ou Péssima.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(AppRadius.md),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(AppRadius.md))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    OutlinedTextField(
                        value = assetStr,
                        onValueChange = { assetStr = it },
                        label = { Text("Valor Total do Ativo (A) em MZN") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )

                    OutlinedTextField(
                        value = liabStr,
                        onValueChange = { liabStr = it },
                        label = { Text("Valor Total do Passivo (P) em MZN") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Situação Líquida (SL):", style = MaterialTheme.typography.labelMedium)
                            ValorMonetarioText(
                                valor = formulaSL,
                                style = ContaMonetariaTextStyle.Subheader,
                                color = if (formulaSL >= 0) EsmeraldaMetical else BrandRose
                            )
                        }

                        SituacaoPatrimonialBadge(situacao = sit, colorName = sitColor)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                            .padding(10.dp)
                    ) {
                        val comment = when {
                            formulaSL > 0 -> "Análise Comercial: A situação patrimonial é BOA. Os bens e os direitos que a empresa possui superam o montante total de obrigações assumidas."
                            formulaSL < 0 -> "Análise Comercial: A situação patrimonial é PÉSSIMA (Passivo a descoberto). A empresa possui dívidas superiores a todos os seus bens e direitos acumulados."
                            else -> "Análise Comercial: A situação patrimonial é MENOS BOA (Equilíbrio Neutro). O ativo de capitais é milimetricamente idêntico ao montante de dívidas."
                        }
                        Text(
                            text = comment,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


// =================== TAB 4: JARGONS ===================
@Composable
fun LessonJargons() {
    val items = listOf(
        JargonItem("Crédito", "Direito", "Activo", "Significa possuir um direito corrente sobre alguém."),
        JargonItem("Crédito sobre", "Direito", "Activo", "Valor que a empresa tem a receber de outra pessoa."),
        JargonItem("Crédito á", "Direito", "Activo", "Cortesias de faturas a receber em curto termo."),
        JargonItem("Crédito de", "Obrigação", "Passivo", "Significa dever dinheiro a um parceiro de crédito."),
        
        JargonItem("Débito", "Obrigação", "Passivo", "Compromisso de débito de faturas."),
        JargonItem("Débito sobre", "Obrigação", "Passivo", "Dívida contraída de serviços em progresso."),
        JargonItem("Débito á", "Obrigação", "Passivo", "Encargo assumido perante outras entidades."),
        JargonItem("Débito de", "Direito", "Activo", "Significa receber fundos originados por faturas de cliente."),
        
        JargonItem("Saque", "Direito", "Activo", "Efetuou uma ordem de saque de crédito contra alguém."),
        JargonItem("Saque sobre", "Direito", "Activo", "Direito resultante de saque de letras de câmbio."),
        JargonItem("Saque á", "Direito", "Activo", "Câmbio a favor de nosso balanço."),
        JargonItem("Saque de", "Obrigação", "Passivo", "Obrigação de saque sofrida decorrente de compras."),
        
        JargonItem("Aceite", "Obrigação", "Passivo", "A empresa aceitou pagar uma letra financeira no prazo."),
        JargonItem("Aceite sobre", "Obrigação", "Passivo", "Aceite legal de obrigação de fornecimento de bens."),
        JargonItem("Aceite á", "Obrigação", "Passivo", "Vencimento legal de títulos a serem liquidados."),
        JargonItem("Aceite de", "Direito", "Activo", "Letra aceitada por um cliente a favor da nossa firma.")
    )

    var searchQuery by remember { mutableStateOf("") }
    val filteredItems = items.filter {
        it.expression.contains(searchQuery, ignoreCase = true) ||
        it.significance.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Expressões e Jargões Contabilísticos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Estes termos são usados frequentemente em Moçambique na elaboração de exames de contabilidade. Saiba distinguir Activo (Direito) de Passivo (Obrigação) pelas preposições associadas.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Pesquisar expressão (ex: Crédito de, Aceite de...)") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredItems) { item ->
                JargonCard(item)
            }
        }
    }
}

data class JargonItem(
    val expression: String,
    val significance: String,
    val element: String,
    val ex: String
)

@Composable
fun JargonCard(item: JargonItem) {
    val isActivo = item.element == "Activo"
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.expression,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.ex,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isActivo) EsmeraldaMetical.copy(alpha = 0.15f) else BrandRose.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = item.element.uppercase(),
                        color = if (isActivo) EsmeraldaMetical else BrandRose,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.significance,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


// =================== TAB 5: LEDGER T ===================
data class TAccountEntry(
    val desc: String,
    val valor: Double,
    val isDeve: Boolean
)

@Composable
fun LessonLedgerT() {
    var accountCode by remember { mutableStateOf("1.1") }
    var accountTitle by remember { mutableStateOf("CAIXA") }
    var entryDesc by remember { mutableStateOf("") }
    var entryValueStr by remember { mutableStateOf("") }
    
    // Ledger Ledger entries list
    var entries by remember { mutableStateOf(listOf(
        TAccountEntry("Saldo Inicial", 20000.0, true),
        TAccountEntry("Venda Dinheiro", 15000.0, true),
        TAccountEntry("Pagamento Fornecedor", 5000.0, false)
    )) }

    val totalDeve = entries.filter { it.isDeve }.sumOf { it.valor }
    val totalHaver = entries.filter { !it.isDeve }.sumOf { it.valor }
    val diff = totalDeve - totalHaver

    val (saldoLabel, saldoType) = when {
        diff > 0 -> Pair("Saldo Devedor (Sd)", "DEVEDOR")
        diff < 0 -> Pair("Saldo Credor (Sc)", "CREDOR")
        else -> Pair("Saldo Nulo (So)", "NULO")
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "O Razão Esquemático (Razão em T)",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Text(
                text = "No aspeto gráfico escolar ou no trabalho, as contas são representadas na forma de uma letra “T”. O lado esquerdo é reservado aos Débitos (Deve) e o lado direito aos Créditos (Haver). Ativos aumentam no lado do Deve, enquanto Passivos / Capital Próprio aumentam no Haver.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            // THE VISUAL ACCOUNT IN T WIDGET
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(AppRadius.md),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header of T-chart
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${accountCode} ${accountTitle.uppercase()}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(thickness = 4.dp, color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sides of DEVE vs HAVER
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 160.dp)
                    ) {
                        // Left (DEVE / DEBITO)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = "D - DEVE (Débito)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandTeal,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            entries.filter { it.isDeve }.forEach { entry ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = entry.desc,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    ValorMonetarioText(valor = entry.valor, style = ContaMonetariaTextStyle.Small)
                                }
                            }
                        }

                        // Vertical Central Line
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(2.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                                .align(Alignment.CenterVertically)
                        )

                        // Right (HAVER / CREDITO)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                text = "C - HAVER (Crédito)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandRose,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            entries.filter { !it.isDeve }.forEach { entry ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = entry.desc,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    ValorMonetarioText(valor = entry.valor, style = ContaMonetariaTextStyle.Small)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Totals
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Soma D:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            ValorMonetarioText(valor = totalDeve, style = ContaMonetariaTextStyle.Bold, color = BrandTeal)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Soma C:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            ValorMonetarioText(valor = totalHaver, style = ContaMonetariaTextStyle.Bold, color = BrandRose)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = saldoLabel, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                val unsignedDiff = if (diff < 0) -diff else diff
                                ValorMonetarioText(valor = unsignedDiff, style = ContaMonetariaTextStyle.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when (saldoType) {
                                            "DEVEDOR" -> BrandTeal.copy(alpha = 0.2f)
                                            "CREDOR" -> BrandAmber.copy(alpha = 0.2f)
                                            else -> Slate500.copy(alpha = 0.2f)
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = saldoType,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when (saldoType) {
                                        "DEVEDOR" -> BrandTeal
                                        "CREDOR" -> BrandAmber
                                        else -> Slate600
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Lançar Movimento no Sandbox:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = accountCode,
                            onValueChange = { accountCode = it },
                            label = { Text("Código") },
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = accountTitle,
                            onValueChange = { accountTitle = it },
                            label = { Text("Designação") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = entryDesc,
                        onValueChange = { entryDesc = it },
                        label = { Text("Descrição do lançamento") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Ex: Aumento, Compra, Dívida") }
                    )

                    OutlinedTextField(
                        value = entryValueStr,
                        onValueChange = { entryValueStr = it },
                        label = { Text("Valor em MZN") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val value = entryValueStr.toDoubleOrNull()
                                if (value != null && entryDesc.isNotBlank()) {
                                    entries = entries + TAccountEntry(entryDesc, value, true)
                                    entryDesc = ""
                                    entryValueStr = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandTeal),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Fazer DEVE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val value = entryValueStr.toDoubleOrNull()
                                if (value != null && entryDesc.isNotBlank()) {
                                    entries = entries + TAccountEntry(entryDesc, value, false)
                                    entryDesc = ""
                                    entryValueStr = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandRose),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Fazer HAVER", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    TextButton(
                        onClick = { entries = emptyList() },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Limpar Sandbox de Razão", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// =================== TAB 6: INVENTARIO & BALANÇO ===================
@Composable
fun LessonBalances() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "O Livro de Inventário e Balanço",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Text(
                text = "De tempos em tempos, a situação do património de uma entidade deve ser compilada em relatórios estruturados oficiais. No PGC português e de Moçambique, faz-se isso por meio de dois relatórios:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DocCard(
                    icon = Icons.Default.FormatListBulleted,
                    title = "O Inventário",
                    desc = "É uma relação minuciosa e detalhada (lista analítica) que discrimina todos os elementos que compõem o Ativo e o Passivo em determinado instante, estimando seus valores numéricos.",
                    phases = "Fases da Elaboração:\n1️⃣ Identificação e Avaliação física ou documental.\n2️⃣ Atribuição do respetivo valor monetário.\n3️⃣ Classificação técnica nas respetivas contas do PGC."
                )

                DocCard(
                    icon = Icons.Default.Assessment,
                    title = "O Balanço",
                    desc = "É a síntese de apresentação das contas matrimoniais. Ele agrupa os saldos do Razão em colunas de Ativo, Passivo e de Capital Próprio de forma que a equação geral se estabeleça.",
                    phases = "A Equação Imutável:\n👉 Totais de Ativos = Totais de Passivos + Capital Próprio\nSe esta relação final não for absolutamente idêntica, diz-se comumente que 'O Balanço não fechou'."
                )
            }
        }

        item {
            Text(
                text = "Exercícios Resolvidos (Do Seu Caderno):",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Text(
                text = "Confira a resolução matemática dos trabalhos práticos indicados no programa de estudos:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Trabalho Prático nº 01: Empresa Viva Bem (04/02/2023)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ativo (Bens/Direitos):\n" +
                                "• Dinheiro em cofre: 15.000,00 MZN (Bem)\n" +
                                "• Dinheiro no BCI: 40.000,00 MZN (Bem)\n" +
                                "• Viatura Nissan: 150.000,00 MZN (Bem)\n" +
                                "• 200 Sacos cimento @ 300: 60.000,00 MZN (Bem)\n" +
                                "• 1000kg pregos @ 45: 45.000,00 MZN (Bem)\n" +
                                "• Dívida de cliente Sozinho: 5.000,00 MZN (Direito)\n" +
                                "• Motorizada: 18.000,00 MZN (Bem)\n" +
                                "Total de Ativo (A) = 333.000,00 MZN\n\n" +
                                "Passivo (Obrigações):\n" +
                                "• Empréstimo BCI: 60.000,00 MZN\n" +
                                "• Dívida ao INSS: 3.500,00 MZN\n" +
                                "• Fornecedor Muanaco: 10.000,00 MZN\n" +
                                "Total de Passivo (P) = 73.500,00 MZN\n\n" +
                                "Capital Próprio (CP/SL) = A - P = 333.000,00 - 73.500,00 = 259.500,00 MZN.\n" +
                                "A Situação Patrimonial é BOA.",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun DocCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String, phases: String) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(AppRadius.sm))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = phases,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


// =================== TAB 7: QUIZ ===================
data class QuizQuestion(
    val q: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

@Composable
fun LessonQuiz() {
    val questions = listOf(
        QuizQuestion(
            q = "Qual das seguintes alternativas descreve corretamente o conceito de Património para a contabilidade?",
            options = listOf(
                "Conjunto de viaturas e escritórios de uma instituição.",
                "Conjunto de bens, direitos e obrigações de uma determinada entidade num dado momento.",
                "Quantidade bruta de dinheiro líquido no cofre de caixa."
            ),
            correctIndex = 1,
            explanation = "Exatamente! Património representa o conjunto integral das posições positivas (bens, direitos) e negativas (obrigações)."
        ),
        QuizQuestion(
            q = "A empresa Viva Bem possui Ativos Totais de 333.000 MZN e Passivos Totais de 73.500 MZN. Qual é a sua Situação Líquida / Capital Próprio?",
            options = listOf(
                "406.500 MZN",
                "259.500 MZN",
                "73.500 MZN"
            ),
            correctIndex = 1,
            explanation = "Correto! Fórmulas: CP = A - P. 333.000 - 73.500 = 259.500 MZN."
        ),
        QuizQuestion(
            q = "Como é classificado um software proprietário, como um app de faturação, segundo o PGC-NIRF?",
            options = listOf(
                "Ativo Tangível Não Corrente",
                "Ativo Intangível Não Corrente",
                "Ativo Corrente de Caixa"
            ),
            correctIndex = 1,
            explanation = "Correto! Softwares, patentes, direitos autorais e marcas são imateriais, ou seja, ativos intangíveis de longo prazo."
        ),
        QuizQuestion(
            q = "O termo técnico 'Crédito de' significa na óptica contabilística:",
            options = listOf(
                "Ter um Direito (Ativo)",
                "Ter uma Obrigação (Passivo)",
                "Um saldo de caixa nulo"
            ),
            correctIndex = 1,
            explanation = "Excelente. Lembre-se: 'Crédito de' representa um passivo/obrigação. Diferente de 'Crédito sobre' que é um direito."
        ),
        QuizQuestion(
            q = "Segundo as regras de regulação e movimentação, as Contas de Ativo:",
            options = listOf(
                "Debitam-se pelos aumentos e creditam-se pelas reduções.",
                "Creditam-se pelos aumentos e debitam-se pelas reduções.",
                "Nunca aceitam transações no Deve."
            ),
            correctIndex = 0,
            explanation = "Perfeito! Contas do Ativo registam seus aumentos no lado do Deve (Débito) e suas reduções no lado do Haver (Crédito)."
        )
    )

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var points by remember { mutableStateOf(0) }
    var hasAnswered by remember { mutableStateOf(false) }
    var quizCompleted by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!quizCompleted) {
            val q = questions[currentQuestionIndex]
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AUTO-AVALIAÇÃO DE RA3",
                        style = MaterialTheme.typography.labelSmall,
                        color = EsmeraldaMetical,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Pergunta ${currentQuestionIndex + 1} de ${questions.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = q.q,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    q.options.forEachIndexed { index, option ->
                        val isSelected = selectedOptionIndex == index
                        val isCorrect = q.correctIndex == index
                        
                        val containerColor = if (hasAnswered) {
                            if (isCorrect) Color(0xFFD1FAE5) // light green
                            else if (isSelected) Color(0xFFFEE2E2) // light red
                            else MaterialTheme.colorScheme.surface
                        } else {
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        }

                        val textColor = if (hasAnswered) {
                            if (isCorrect) Color(0xFF065F46)
                            else if (isSelected) Color(0xFF991B1B)
                            else MaterialTheme.colorScheme.onSurface
                        } else {
                            if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        }

                        val borderWidth = if (isSelected) 2.dp else 1.dp
                        val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant

                        Card(
                            onClick = {
                                if (!hasAnswered) {
                                    selectedOptionIndex = index
                                }
                            },
                            enabled = !hasAnswered,
                            shape = RoundedCornerShape(AppRadius.sm),
                            border = BorderStroke(borderWidth, borderColor),
                            colors = CardDefaults.cardColors(containerColor = containerColor),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ('A'.code + index).toChar().toString(),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = option, fontSize = 13.sp, color = textColor, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            if (selectedOptionIndex != null && !hasAnswered) {
                item {
                    Button(
                        onClick = {
                            hasAnswered = true
                            if (selectedOptionIndex == q.correctIndex) points++
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Submeter Resposta", fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (hasAnswered) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (selectedOptionIndex == q.correctIndex) "✓ Correto!" else "✗ Errado!",
                                fontWeight = FontWeight.ExtraBold,
                                color = if (selectedOptionIndex == q.correctIndex) EsmeraldaMetical else BrandRose
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = q.explanation, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            if (currentQuestionIndex + 1 < questions.size) {
                                currentQuestionIndex++
                                selectedOptionIndex = null
                                hasAnswered = false
                            } else {
                                quizCompleted = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(
                            text = if (currentQuestionIndex + 1 < questions.size) "Próxima Pergunta" else "Ver Resultados",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // Quiz Results State
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                brush = AppGradients.SeloGradient,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Parabéns!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Completou o Desafio do Módulo 1 com sucesso.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    
                    Text(
                        text = "Pontuação: $points de ${questions.size} Acertos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = EsmeraldaMetical
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = {
                            currentQuestionIndex = 0
                            selectedOptionIndex = null
                            hasAnswered = false
                            quizCompleted = false
                            points = 0
                        },
                        shape = RoundedCornerShape(AppRadius.sm)
                    ) {
                        Text("Tentar Novamente", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
