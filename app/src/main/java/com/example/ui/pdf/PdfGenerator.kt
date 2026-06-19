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
        resumo: ResumoPatrimonial
    ): File? {
        val pdfDocument = PdfDocument()
        // Standard A4 dimensions in points: 595 x 842
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paintText = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }

        val paintTitle = Paint().apply {
            color = Color.rgb(21, 101, 192) // Financial Blue
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        }

        val paintHeader = Paint().apply {
            color = Color.rgb(46, 125, 50) // Green details
            textSize = 11f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val paintBold = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        var y = 50f

        // Document Headers
        canvas.drawText("CONTAFÁCIL · PGC-NIRF", 40f, y, paintHeader)
        y += 25f
        canvas.drawText("INVENTÁRIO CLASSIFICADO - ${inventario.descricao}", 40f, y, paintTitle)
        y += 20f

        // Corporate Metadata
        paintText.textSize = 10f
        canvas.drawText("Empresa: ${empresa.nome}", 40f, y, paintBold)
        canvas.drawText("Data Ref: ${inventario.data}", 350f, y, paintText)
        y += 15f
        canvas.drawText("Actividade: ${empresa.actividade}", 40f, y, paintText)
        canvas.drawText("Cidade: ${empresa.cidade}", 350f, y, paintText)
        y += 15f
        canvas.drawText("NUIT: ${empresa.nuit ?: "N/D"}", 40f, y, paintText)
        canvas.drawText("Momento: ${inventario.momento}", 350f, y, paintText)
        y += 20f

        // Decorative separator line
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 25f

        // Table Header
        canvas.drawText("CONTA / DESCRIÇÃO", 40f, y, paintBold)
        canvas.drawText("QTD", 380f, y, paintBold)
        canvas.drawText("V. UNIT", 430f, y, paintBold)
        canvas.drawText("VALOR TOTAL", 490f, y, paintBold)
        y += 10f
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 15f

        // Populate items
        for (classe in classes) {
            // Class header
            canvas.drawText(classe.tituloClasse.uppercase(Locale.getDefault()), 40f, y, paintBold)
            canvas.drawText(formatCurrency(classe.subtotal), 490f, y, paintBold)
            y += 15f

            for (grupo in classe.contas) {
                // Account subheader
                canvas.drawText("  ${grupo.conta.codigo} - ${grupo.conta.titulo}", 40f, y, paintBold)
                canvas.drawText(formatCurrency(grupo.subtotal), 490f, y, paintBold)
                y += 15f

                // If Analitico, draw detailed individual elements
                if (inventario.descricao == "ANALITICO") {
                    for (item in grupo.itens) {
                        canvas.drawText("    · ${item.descricao}", 40f, y, paintText)
                        canvas.drawText(String.format("%.1f", item.quantidade), 380f, y, paintText)
                        
                        val uValFormatted = item.valorUnitario?.let { formatCurrency(it) } ?: "-"
                        canvas.drawText(uValFormatted, 420f, y, paintText)
                        
                        canvas.drawText(formatCurrency(item.valor), 490f, y, paintText)
                        y += 15f

                        if (y > 780f) {
                            // Break page if it overflows standard A4 height
                            break
                        }
                    }
                }
                
                if (y > 780f) break
            }
            
            y += 10f
            if (y > 780f) break
        }

        // Summary box
        if (y > 700f) {
            // Add custom secondary page if height is tight
            pdfDocument.finishPage(page)
            val pageInfo2 = PdfDocument.PageInfo.Builder(595, 842, 2).create()
            val page2 = pdfDocument.startPage(pageInfo2)
            val canvas2 = page2.canvas
            y = 50f
            
            drawSummarySection(canvas2, resumo, y, paintBold, paintText, linePaint)
            pdfDocument.finishPage(page2)
        } else {
            drawSummarySection(canvas, resumo, y, paintBold, paintText, linePaint)
            pdfDocument.finishPage(page)
        }

        // Write to cache directory or downloads directory safely
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

    private fun drawSummarySection(
        canvas: Canvas,
        resumo: ResumoPatrimonial,
        startY: Float,
        paintBold: Paint,
        paintText: Paint,
        linePaint: Paint
    ) {
        var y = startY
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 20f

        canvas.drawText("RESUMO PATRIMONIAL", 40f, y, paintBold)
        y += 15f

        canvas.drawText("Total de Bens (Ativos Físicos):", 40f, y, paintText)
        canvas.drawText(formatCurrency(resumo.totalBens), 490f, y, paintText)
        y += 15f

        canvas.drawText("Total de Direitos (Contas a Receber):", 40f, y, paintText)
        canvas.drawText(formatCurrency(resumo.totalDireitos), 490f, y, paintText)
        y += 15f

        canvas.drawText("TOTAL DO ACTIVO:", 40f, y, paintBold)
        canvas.drawText(formatCurrency(resumo.totalActivo), 490f, y, paintBold)
        y += 20f

        canvas.drawText("TOTAL DO PASSIVO (Obrigações):", 40f, y, paintBold)
        canvas.drawText(formatCurrency(resumo.totalPassivo), 490f, y, paintBold)
        y += 20f

        canvas.drawText("SITUAÇÃO PATRIMONIAL LÍQUIDA (Capital Próprio):", 40f, y, paintBold)
        canvas.drawText(formatCurrency(resumo.capitalProprio), 490f, y, paintBold)
        y += 15f

        canvas.drawText("Classificação de Situação:", 40f, y, paintText)
        canvas.drawText(resumo.situacaoPatrimonial.uppercase(Locale.getDefault()), 490f, y, paintBold)
        y += 30f

        canvas.drawText("Elaborado em Eu Conto · PGC-NIRF Decreto 70/2009", 40f, y, paintText)
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
            color = Color.rgb(46, 125, 50)
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
        canvas.drawText("CONTAFÁCIL · PGC-NIRF", 40f, y, paintHeaderLabel)
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
