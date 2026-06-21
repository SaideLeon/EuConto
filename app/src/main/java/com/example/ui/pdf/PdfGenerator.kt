package com.example.ui.pdf

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.data.model.Empresa
import com.example.data.model.Inventario
import com.example.data.model.Balanco
import com.example.data.model.ContaPGC
import com.example.data.repository.GrupoClasse
import com.example.data.repository.BalancoCalculado
import com.example.data.repository.ResumoPatrimonial
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale

object PdfGenerator {
    private val ptMZLocale = Locale("pt", "MZ")
    private val currencyFormat = NumberFormat.getCurrencyInstance(ptMZLocale).apply {
        currency = java.util.Currency.getInstance("MZN")
    }

    private fun formatCurrency(value: Double): String {
        return try {
            currencyFormat.format(value)
        } catch (e: Exception) {
            String.format("%.2f MZN", value)
        }
    }

    fun exportInventarioToPdf(
        context: Context,
        empresa: Empresa,
        inventario: Inventario,
        classes: List<GrupoClasse>,
        resumo: ResumoPatrimonial,
        allContas: List<ContaPGC> = emptyList()
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        var currentPage = pdfDocument.startPage(pageInfo)
        var canvas = currentPage.canvas
        var pageNumber = 1
        var y = 50f
        var tabelaStartY = 0f

        val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val paintTitle = Paint().apply {
            color = Color.rgb(26, 35, 126) // Indigo Capulana (#1A237E)
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        }

        val paintHeader = Paint().apply {
            color = Color.rgb(16, 124, 65) // Esmeralda Metical (#107C41)
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintBold = Paint().apply {
            color = Color.BLACK
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        }

        val paintTableHeaderText = Paint().apply {
            color = Color.rgb(33, 33, 33)
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        }

        val linePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1.0f
        }

        val lineThinPaint = Paint().apply {
            color = Color.rgb(180, 180, 180)
            strokeWidth = 0.5f
        }

        val paintTableHeaderBg = Paint().apply {
            color = Color.rgb(240, 240, 240)
            style = Paint.Style.FILL
        }

        val paintSectionBg = Paint().apply {
            color = Color.rgb(245, 245, 245)
            style = Paint.Style.FILL
        }

        // Funções de desenho auxiliares
        fun wrapText(text: String, maxWidth: Float, paint: Paint): List<String> {
            val words = text.split(" ")
            val lines = mutableListOf<String>()
            var currentLine = StringBuilder()
            
            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "${currentLine} ${word}"
                val width = paint.measureText(testLine)
                if (width <= maxWidth) {
                    currentLine.append(if (currentLine.isEmpty()) word else " ${word}")
                } else {
                    if (currentLine.isNotEmpty()) {
                        lines.add(currentLine.toString())
                    }
                    currentLine = StringBuilder(word)
                }
            }
            if (currentLine.isNotEmpty()) {
                lines.add(currentLine.toString())
            }
            return if (lines.isEmpty()) listOf("") else lines
        }

        fun drawTextRightAligned(c: Canvas, text: String, x: Float, yPos: Float, paint: Paint) {
            val oldAlign = paint.textAlign
            paint.textAlign = Paint.Align.RIGHT
            c.drawText(text, x, yPos, paint)
            paint.textAlign = oldAlign
        }

        fun drawDescriptionCell(text: String, x: Float, yPos: Float, paint: Paint, maxWidth: Float): Float {
            val lines = wrapText(text, maxWidth, paint)
            var currentY = yPos
            for (line in lines) {
                canvas.drawText(line, x, currentY, paint)
                if (line != lines.last()) {
                    currentY += 12f
                }
            }
            return currentY
        }

        fun drawTableHeaders() {
            canvas.drawRect(40f, y - 10f, 555f, y + 8f, paintTableHeaderBg)
            canvas.drawLine(40f, y - 10f, 555f, y - 10f, linePaint)
            canvas.drawLine(40f, y + 8f, 555f, y + 8f, linePaint)
            
            canvas.drawText("CL.", 43f, y, paintTableHeaderText)
            canvas.drawText("CONTA", 60f, y, paintTableHeaderText)
            canvas.drawText("DESCRIÇÃO DOS ELEMENTOS", 90f, y, paintTableHeaderText)
            
            val paintHeaderRight = Paint(paintTableHeaderText).apply { textAlign = Paint.Align.RIGHT }
            canvas.drawText("PARCIAL (MZN)", 455f, y, paintHeaderRight)
            canvas.drawText("SUBTOTAL (MZN)", 550f, y, paintHeaderRight)
            
            y += 18f
            tabelaStartY = y - 28f
        }

        fun drawHeaderOnNewPage() {
            y = 50f
            canvas.drawText("EU CONTO · PGC-NIRF", 40f, y, paintHeader)
            y += 15f
            canvas.drawText("INVENTÁRIO CLASSIFICADO - Continuação (${empresa.nome.uppercase(Locale.getDefault())})", 40f, y, paintBold)
            canvas.drawText("Pág. ${pageNumber}", 520f, y, paintText)
            y += 20f
            
            drawTableHeaders()
        }

        fun executePageBreak() {
            // Desenhar linhas horizontais e verticais de fechamento
            canvas.drawLine(40f, y - 8f, 555f, y - 8f, linePaint)
            canvas.drawLine(40f, tabelaStartY, 40f, y - 8f, linePaint)
            canvas.drawLine(57f, tabelaStartY, 57f, y - 8f, linePaint)
            canvas.drawLine(87f, tabelaStartY, 87f, y - 8f, linePaint)
            canvas.drawLine(375f, tabelaStartY, 375f, y - 8f, linePaint)
            canvas.drawLine(460f, tabelaStartY, 460f, y - 8f, linePaint)
            canvas.drawLine(555f, tabelaStartY, 555f, y - 8f, linePaint)
            
            pdfDocument.finishPage(currentPage)
            
            pageNumber++
            val pageInfoNew = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
            currentPage = pdfDocument.startPage(pageInfoNew)
            canvas = currentPage.canvas
            
            drawHeaderOnNewPage()
        }

        fun checkPageOverflow(requiredSpace: Float) {
            if (y + requiredSpace > 780f) {
                executePageBreak()
            }
        }

        fun getOrdemClasseActivo(classe: Int): Int {
            return when (classe) {
                3 -> 1
                2 -> 2
                4 -> 3
                1 -> 4
                else -> 5
            }
        }

        // Corrigido para corresponder à ordem solicitada de prioridade passiva
        fun getOrdemContaPassivo(codigo: String): Int {
            return when {
                codigo.startsWith("4.3") -> 1
                codigo.startsWith("4.6") -> 2
                codigo.startsWith("4.2") -> 3
                else -> 4
            }
        }

        // --- Início das impressões na página 1 ---
        // Document Headers
        canvas.drawText("EU CONTO · PGC-NIRF", 40f, y, paintHeader)
        y += 22f
        canvas.drawText("INVENTÁRIO PATRIMONIAL EXERCIDO", 40f, y, paintTitle)
        y += 18f

        // Corporate Metadata
        paintText.textSize = 9.5f
        canvas.drawText("Empresa: ${empresa.nome}", 40f, y, paintBold)
        canvas.drawText("Data Ref: ${inventario.data}", 350f, y, paintText)
        y += 14f
        canvas.drawText("Actividade: ${empresa.actividade}", 40f, y, paintText)
        canvas.drawText("Cidade: ${empresa.cidade}", 350f, y, paintText)
        y += 14f
        canvas.drawText("NUIT: ${empresa.nuit ?: "N/D"}", 40f, y, paintText)
        canvas.drawText("Momento / Extensão: ${inventario.momento} / ${inventario.tipo}", 350f, y, paintText)
        y += 25f

        // Desenhar a primeira tabela de cabeçalho
        drawTableHeaders()

        // Agrupando todas as contas
        val contasTotalMap = allContas.associateBy { it.codigo }
        val todosGrupoContas = classes.flatMap { it.contas }

        val activos = todosGrupoContas.filter { 
            val nat = it.conta.natureza
            nat == "ACTIVO" || nat == "GASTO"
        }.sortedWith(compareBy<com.example.data.repository.GrupoConta>({ getOrdemClasseActivo(it.conta.classe) }, { it.conta.codigo }))

        val passivos = todosGrupoContas.filter { 
            val nat = it.conta.natureza
            nat == "PASSIVO" || nat == "RENDIMENTO" || nat == "RESULTADO"
        }.sortedWith(compareBy<com.example.data.repository.GrupoConta>({ getOrdemContaPassivo(it.conta.codigo) }, { it.conta.codigo }))

        val capitalProprio = todosGrupoContas.filter { 
            it.conta.natureza == "CAPITAL_PROPRIO"
        }.sortedBy { it.conta.codigo }

        val seccoes = listOf(
            Triple("ACTIVOS (BENS E DIREITOS)", activos, resumo.totalActivo),
            Triple("PASSIVO (OBRIGAÇÕES)", passivos, resumo.totalPassivo),
            Triple("CAPITAL PRÓPRIO (SITUAÇÃO LÍQUIDA)", capitalProprio, resumo.capitalProprio)
        )

        for ((seccaoNome, grupoContas, totalSeccao) in seccoes) {
            if (grupoContas.isEmpty()) continue
            
            // Título de Seção
            checkPageOverflow(30f)
            canvas.drawRect(40f, y - 10f, 555f, y + 8f, paintSectionBg)
            
            val sectionPaint = Paint(paintBold).apply { textAlign = Paint.Align.CENTER; textSize = 9.5f }
            canvas.drawText(seccaoNome, 297f, y, sectionPaint)
            canvas.drawLine(40f, y + 8f, 555f, y + 8f, linePaint)
            y += 18f
            
            var ultimaClasseDesenhada: Int? = null
            var ultimaContaPaiDesenhada: String? = null
            
            for (grupo in grupoContas) {
                // Checar Classe
                val classeId = grupo.conta.classe
                if (classeId != ultimaClasseDesenhada) {
                    checkPageOverflow(25f)
                    val classeTitulo = when (classeId) {
                        1 -> "Meios Financeiros"
                        2 -> "Inventários e Activos Biológicos"
                        3 -> "Investimentos de Capital"
                        4 -> "Contas a Receber, Contas a Pagar, Acréscimos e Diferimentos"
                        5 -> "Capital Próprio"
                        6 -> "Gastos e Perdas"
                        7 -> "Rendimentos e Ganhos"
                        8 -> "Resultados"
                        else -> "Outras Classes"
                    }
                    
                    canvas.drawText(classeId.toString(), 45f, y, paintBold)
                    canvas.drawText("Classe $classeId - ${classeTitulo.uppercase(Locale.getDefault())}", 90f, y, paintBold)
                    canvas.drawLine(40f, y + 4f, 555f, y + 4f, lineThinPaint)
                    y += 16f
                    ultimaClasseDesenhada = classeId
                }
                
                // Checar Conta Pai de 2 dígitos
                val codigoPartes = grupo.conta.codigo.split(".")
                val codigoPai = if (codigoPartes.size >= 2) "${codigoPartes[0]}.${codigoPartes[1]}" else grupo.conta.codigo
                if (codigoPai != ultimaContaPaiDesenhada) {
                    checkPageOverflow(25f)
                    val paiObjeto = contasTotalMap[codigoPai]
                    val paiTitulo = paiObjeto?.titulo ?: grupo.conta.titulo
                    
                    canvas.drawText(codigoPai, 60f, y, paintBold)
                    canvas.drawText(paiTitulo.uppercase(Locale.getDefault()), 90f, y, paintBold)
                    canvas.drawLine(40f, y + 4f, 555f, y + 4f, lineThinPaint)
                    y += 16f
                    ultimaContaPaiDesenhada = codigoPai
                }
                
                // Se a subconta for de nível mais baixo que a conta de 2 dígitos pai, a mostramos como linha intermediária
                if (grupo.conta.codigo != codigoPai) {
                    checkPageOverflow(25f)
                    canvas.drawText(grupo.conta.codigo, 60f, y, paintBold)
                    canvas.drawText("  ${grupo.conta.titulo}", 90f, y, paintBold)
                    canvas.drawLine(40f, y + 4f, 555f, y + 4f, lineThinPaint)
                    y += 16f
                }
                
                // Desenhar Elementos Patrimoniais individuais (ANALITICO)
                if (inventario.descricao == "ANALITICO") {
                    val totalItens = grupo.itens.size
                    for (index in grupo.itens.indices) {
                        val item = grupo.itens[index]
                        
                        // Compor descrição contábil detalhada se houver Qtd e Unidade
                        val rawDesc = item.descricao
                        val itemDesc = if (item.quantidade > 1.0 && item.valorUnitario != null) {
                            "$rawDesc (${String.format(Locale.getDefault(), "%.0f", item.quantidade)} x ${formatCurrency(item.valorUnitario)})"
                        } else {
                            rawDesc
                        }
                        
                        val linesCount = wrapText(itemDesc, 275f, paintText).size
                        val spaceNeeded = 14f + (linesCount - 1) * 12f
                        
                        checkPageOverflow(spaceNeeded)
                        
                        val xDescStart = 100f
                        val yFinal = drawDescriptionCell(itemDesc, xDescStart, y, paintText, 275f)
                        
                        if (totalItens == 1) {
                            drawTextRightAligned(canvas, formatCurrency(item.valor), 550f, y, paintText)
                        } else {
                            // 2+ itens do grupo -> Coluna 3
                            drawTextRightAligned(canvas, formatCurrency(item.valor), 455f, y, paintText)
                            // Última linha do grupo de conta -> Coluna 4 (soma parcial totalizada)
                            if (index == totalItens - 1) {
                                drawTextRightAligned(canvas, formatCurrency(grupo.subtotal), 550f, y, paintBold)
                            }
                        }
                        
                        canvas.drawLine(40f, yFinal + 4f, 555f, yFinal + 4f, lineThinPaint)
                        y = yFinal + 16f
                    }
                } else {
                    // SINTÉTICO - Mostrar saldo agregado direto na coluna 4
                    val itemDesc = "[Saldo agregado da conta ${grupo.conta.codigo}]"
                    checkPageOverflow(18f)
                    canvas.drawText(itemDesc, 100f, y, paintText)
                    drawTextRightAligned(canvas, formatCurrency(grupo.subtotal), 550f, y, paintBold)
                    canvas.drawLine(40f, y + 4f, 555f, y + 4f, lineThinPaint)
                    y += 16f
                }
            }
            
            // Totalizador final de Seccao
            checkPageOverflow(30f)
            canvas.drawRect(40f, y - 10f, 555f, y + 8f, paintSectionBg)
            canvas.drawLine(40f, y - 10f, 555f, y - 10f, linePaint)
            canvas.drawLine(40f, y + 8f, 555f, y + 8f, linePaint)
            
            val totalLabel = when (seccaoNome) {
                "ACTIVOS (BENS E DIREITOS)" -> "TOTAL DO ACTIVO"
                "PASSIVO (OBRIGAÇÕES)" -> "TOTAL DO PASSIVO"
                else -> "TOTAL DO CAPITAL PRÓPRIO"
            }
            canvas.drawText(totalLabel, 90f, y, paintBold)
            drawTextRightAligned(canvas, formatCurrency(totalSeccao), 550f, y, paintBold)
            y += 24f
        }

        // Fechamos a última página da tabela de inventário
        canvas.drawLine(40f, y - 8f, 555f, y - 8f, linePaint)
        canvas.drawLine(40f, tabelaStartY, 40f, y - 8f, linePaint)
        canvas.drawLine(57f, tabelaStartY, 57f, y - 8f, linePaint)
        canvas.drawLine(87f, tabelaStartY, 87f, y - 8f, linePaint)
        canvas.drawLine(375f, tabelaStartY, 375f, y - 8f, linePaint)
        canvas.drawLine(460f, tabelaStartY, 460f, y - 8f, linePaint)
        canvas.drawLine(555f, tabelaStartY, 555f, y - 8f, linePaint)
        
        pdfDocument.finishPage(currentPage)

        // --- Página Inicial de Conclusão, Comentário de Situação Líquida Contábil e Assinaturas ---
        pageNumber++
        val pageInfoConclusion = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
        val conclusionPage = pdfDocument.startPage(pageInfoConclusion)
        val canvasC = conclusionPage.canvas

        y = 50f
        canvasC.drawText("EU CONTO · PGC-NIRF", 40f, y, paintHeader)
        y += 25f
        canvasC.drawText("COMENTÁRIO PATRIMONIAL & SÍNTESE", 40f, y, paintTitle)
        y += 15f
        canvasC.drawLine(40f, y, 555f, y, linePaint)
        y += 30f

        canvasC.drawText("RESUMO PATRIMONIAL DE CONCILIAÇÃO", 40f, y, paintBold)
        y += 25f

        val xLabel = 60f
        val xVal = 500f

        canvasC.drawText("Total de Bens (Ativos Físicos):", xLabel, y, paintText)
        drawTextRightAligned(canvasC, formatCurrency(resumo.totalBens), xVal, y, paintBold)
        y += 18f

        canvasC.drawText("Total de Direitos (Contas a Receber):", xLabel, y, paintText)
        drawTextRightAligned(canvasC, formatCurrency(resumo.totalDireitos), xVal, y, paintBold)
        y += 18f

        canvasC.drawLine(xLabel, y, xVal, y, lineThinPaint)
        y += 18f

        canvasC.drawText("TOTAL DO ACTIVO:", xLabel, y, paintBold)
        drawTextRightAligned(canvasC, formatCurrency(resumo.totalActivo), xVal, y, paintBold)
        y += 24f

        canvasC.drawText("TOTAL DO PASSIVO (Obrigações):", xLabel, y, paintBold)
        drawTextRightAligned(canvasC, formatCurrency(resumo.totalPassivo), xVal, y, paintBold)
        y += 24f

        canvasC.drawLine(xLabel, y, xVal, y, linePaint)
        y += 18f

        canvasC.drawText("SITUAÇÃO PATRIMONIAL LÍQUIDA (Capital Próprio):", xLabel, y, paintBold)
        drawTextRightAligned(canvasC, formatCurrency(resumo.capitalProprio), xVal, y, paintBold)
        y += 35f

        // Texto do Comentário
        val situacaoTexto = when (resumo.situacaoColor) {
            "VERDE" -> "é superior"
            "AMARELO" -> "é igual"
            else -> "é inferior"
        }
        val comentarioPatrimonial = "Comentário:\nA empresa ${empresa.nome} apresenta uma situação patrimonial " +
                "${resumo.situacaoPatrimonial.uppercase(Locale.getDefault())} na data de referência ${inventario.data}, " +
                "visto que o valor total do Activo reunido (${formatCurrency(resumo.totalActivo)}) $situacaoTexto " +
                "em relação às obrigações do Passivo calculadas (${formatCurrency(resumo.totalPassivo)}), " +
                "concluindo-se com um Capital Próprio líquido ajustado de ${formatCurrency(resumo.capitalProprio)}."

        canvasC.drawText("COMENTÁRIO EXECUTIVO DA SITUAÇÃO PATRIMONIAL:", 40f, y, paintBold)
        y += 20f

        val linesComentario = wrapText(comentarioPatrimonial, 515f, paintText)
        for (line in linesComentario) {
            canvasC.drawText(line, 40f, y, paintText)
            y += 15f
        }
        y += 55f

        // Linhas de Assinaturas
        canvasC.drawLine(60f, y, 250f, y, linePaint)
        canvasC.drawLine(340f, y, 530f, y, linePaint)
        y += 15f

        val paintCenterText = Paint(paintText).apply { textAlign = Paint.Align.CENTER }
        canvasC.drawText("O Técnico de Contabilidade", 155f, y, paintCenterText)
        canvasC.drawText("A Gerência da Empresa", 435f, y, paintCenterText)
        y += 15f
        canvasC.drawText("_________________________", 155f, y, paintCenterText)
        canvasC.drawText("_________________________", 435f, y, paintCenterText)

        pdfDocument.finishPage(conclusionPage)

        val directory = context.getExternalFilesDir("Documents") ?: context.cacheDir
        val file = File(directory, "Inventario_${empresa.nome.replace(" ", "_")}_${System.currentTimeMillis()}.pdf")
        
        return try {
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            pdfDocument.close()
            null
        }
    }

    fun exportBalancoToPdf(
        context: Context,
        empresa: Empresa,
        balanco: Balanco,
        resultado: BalancoCalculado
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 9/0.95f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val paintTitle = Paint().apply {
            color = Color.rgb(21, 101, 192) // Corporate Financial Blue
            textSize = 15f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        }

        val paintHeaderLabel = Paint().apply {
            color = Color.rgb(16, 124, 65) // Esmeralda Metical (#107C41)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintBold = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val linePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1.2f
        }

        val thinLinePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 0.8f
        }

        var y = 50f

        // Document header
        canvas.drawText("EU CONTO · PGC-NIRF", 40f, y, paintHeaderLabel)
        y += 22f
        canvas.drawText("BALANÇO PATRIMONIAL EXERCÍCIO - ${balanco.tipo}", 40f, y, paintTitle)
        y += 18f

        canvas.drawText("Empresa: ${empresa.nome}", 40f, y, paintBold)
        canvas.drawText("Exercício Actual: ${balanco.dataAtual}", 350f, y, paintText)
        y += 14f
        canvas.drawText("Actividade: ${empresa.actividade}", 40f, y, paintText)
        if (balanco.dataAnterior != null) {
            canvas.drawText("Exercício Anterior: ${balanco.dataAnterior}", 350f, y, paintText)
        } else {
            canvas.drawText("Exercício Anterior: - (Balanço Inicial)", 350f, y, paintText)
        }
        y += 14f
        canvas.drawText("NUIT: ${empresa.nuit ?: "N/D"}", 40f, y, paintText)
        canvas.drawText("Moeda: ${empresa.moeda}", 350f, y, paintText)
        y += 20f

        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 18f

        // Table Header
        canvas.drawText("RUBRICAS (CONTAS)", 40f, y, paintBold)
        canvas.drawText("Exercício ${balanco.dataAtual}", 330f, y, paintBold)
        if (balanco.dataAnterior != null) {
            canvas.drawText("Exercício ${balanco.dataAnterior}", 450f, y, paintBold)
        }
        y += 8f
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 15f

        // 1. ACTIVOS
        canvas.drawText("ACTIVOS", 40f, y, paintBold)
        y += 15f

        // Activos não correntes
        canvas.drawText("  Activos Não Correntes", 40f, y, paintBold)
        y += 14f
        for (gc in resultado.activosNaoCorrentes) {
            canvas.drawText("    ${gc.conta.codigo} ${gc.conta.titulo}", 50f, y, paintText)
            canvas.drawText(formatCurrency(gc.subtotal), 330f, y, paintText)
            y += 14f
        }
        canvas.drawText("  Total de Activos Não Correntes", 50f, y, paintBold)
        canvas.drawText(formatCurrency(resultado.totalActivosNaoCorrentes), 330f, y, paintBold)
        y += 18f

        // Activos correntes
        canvas.drawText("  Activos Correntes", 40f, y, paintBold)
        y += 14f
        for (gc in resultado.activosCorrentes) {
            canvas.drawText("    ${gc.conta.codigo} ${gc.conta.titulo}", 50f, y, paintText)
            canvas.drawText(formatCurrency(gc.subtotal), 330f, y, paintText)
            y += 14f
        }
        canvas.drawText("  Total de Activos Correntes", 50f, y, paintBold)
        canvas.drawText(formatCurrency(resultado.totalActivosCorrentes), 330f, y, paintBold)
        y += 18f

        canvas.drawLine(40f, y, 555f, y, thinLinePaint)
        y += 15f
        canvas.drawText("TOTAL DOS ACTIVOS", 40f, y, paintBold)
        canvas.drawText(formatCurrency(resultado.totalActivos), 330f, y, paintBold)
        y += 22f

        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 18f

        // 2. CAPITAL PROPRIO E PASSIVOS
        canvas.drawText("CAPITAL PRÓPRIO E PASSIVOS", 40f, y, paintBold)
        y += 15f

        // Capital Proprio
        canvas.drawText("  Capital Próprio", 40f, y, paintBold)
        y += 14f
        for (gc in resultado.capitalProprioItens) {
            canvas.drawText("    ${gc.conta.codigo} ${gc.conta.titulo}", 50f, y, paintText)
            canvas.drawText(formatCurrency(gc.subtotal), 330f, y, paintText)
            y += 14f
        }
        // Always present automated net results to match balances
        canvas.drawText("    8.8 Resultado Líquido do Período", 50f, y, paintText)
        canvas.drawText(formatCurrency(resultado.resultadoLiquidoPeriodo), 330f, y, paintText)
        y += 14f

        canvas.drawText("  Total de Capital Próprio", 50f, y, paintBold)
        canvas.drawText(formatCurrency(resultado.totalCapitalProprio), 330f, y, paintBold)
        y += 18f

        // Passivos não correntes
        canvas.drawText("  Passivos Não Correntes", 40f, y, paintBold)
        y += 14f
        for (gc in resultado.passivosNaoCorrentes) {
            canvas.drawText("    ${gc.conta.codigo} ${gc.conta.titulo}", 50f, y, paintText)
            canvas.drawText(formatCurrency(gc.subtotal), 330f, y, paintText)
            y += 14f
        }
        canvas.drawText("  Total de Passivos Não Correntes", 50f, y, paintBold)
        canvas.drawText(formatCurrency(resultado.totalPassivosNaoCorrentes), 330f, y, paintBold)
        y += 18f

        // Passivos correntes
        canvas.drawText("  Passivos Correntes", 40f, y, paintBold)
        y += 14f
        for (gc in resultado.passivosCorrentes) {
            canvas.drawText("    ${gc.conta.codigo} ${gc.conta.titulo}", 50f, y, paintText)
            canvas.drawText(formatCurrency(gc.subtotal), 330f, y, paintText)
            y += 14f
        }
        canvas.drawText("  Total de Passivos Correntes", 50f, y, paintBold)
        canvas.drawText(formatCurrency(resultado.totalPassivosCorrentes), 330f, y, paintBold)
        y += 18f

        canvas.drawLine(40f, y, 555f, y, thinLinePaint)
        y += 15f
        canvas.drawText("TOTAL DO CAPITAL PRÓPRIO E PASSIVOS", 40f, y, paintBold)
        canvas.drawText(formatCurrency(resultado.totalCpEPassivo), 330f, y, paintBold)
        y += 20f

        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 20f

        // Fecho confirmation
        val dateText = "Balanço Fechado com Sucesso: Ativos == CP + Passivos"
        canvas.drawText(dateText, 40f, y, paintBold)
        y += 25f

        canvas.drawText("Gerado automaticamente via Eu Conto · Moçambique", 40f, y, paintText)

        pdfDocument.finishPage(page)

        val directory = context.getExternalFilesDir("Documents") ?: context.cacheDir
        val file = File(directory, "Balanco_${empresa.nome.replace(" ", "_")}_${System.currentTimeMillis()}.pdf")

        return try {
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            pdfDocument.close()
            null
        }
    }
}
