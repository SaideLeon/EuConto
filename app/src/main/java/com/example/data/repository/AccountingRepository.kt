package com.example.data.repository

import android.content.Context
import com.example.data.database.AppDatabase
import com.example.data.database.DatabaseSeeder
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

data class ResumoPatrimonial(
    val totalBens: Double,
    val totalDireitos: Double,
    val totalObrigacoes: Double,
    val totalActivo: Double,
    val totalPassivo: Double,
    val capitalProprio: Double,
    val situacaoPatrimonial: String, // Boa, Menos Boa, Péssima
    val situacaoColor: String // VERDE, AMARELO, VERMELHO
)

data class GrupoConta(
    val conta: ContaPGC,
    val subtotal: Double,
    val itens: List<ElementoPatrimonial>
)

data class GrupoClasse(
    val classe: Int,
    val tituloClasse: String,
    val subtotal: Double,
    val contas: List<GrupoConta>
)

data class BalancoCalculado(
    val activosNaoCorrentes: List<GrupoConta>,
    val totalActivosNaoCorrentes: Double,
    val activosCorrentes: List<GrupoConta>,
    val totalActivosCorrentes: Double,
    val totalActivos: Double,
    
    val capitalProprioItens: List<GrupoConta>, // e.g. 5.1, 5.5, 5.9
    val resultadoLiquidoPeriodo: Double, // Calculated automatically: Activo - Passivo - existing CP!
    val totalCapitalProprio: Double,
    
    val passivosNaoCorrentes: List<GrupoConta>,
    val totalPassivosNaoCorrentes: Double,
    val passivosCorrentes: List<GrupoConta>,
    val totalPassivosCorrentes: Double,
    val totalPassivos: Double,
    val totalCpEPassivo: Double,
    val balancoFecha: Boolean
)

class AccountingRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val dao = db.dao()

    val empresas: Flow<List<Empresa>> = dao.getAllEmpresas()
    val allElementos: Flow<List<ElementoPatrimonial>> = dao.getAllElementos()

    fun getEmpresaById(id: Long): Flow<Empresa?> = dao.getEmpresaByIdFlow(id)

    suspend fun createEmpresa(empresa: Empresa): Long {
        return dao.insertEmpresa(empresa)
    }

    suspend fun updateEmpresa(empresa: Empresa) {
        dao.updateEmpresa(empresa)
    }

    suspend fun deleteEmpresa(empresa: Empresa) {
        dao.deleteEmpresa(empresa)
    }

    // Flow that guarantees seeding is completed before emitting accounts
    val contas: Flow<List<ContaPGC>> = flow {
        // Run seed check
        val count = dao.getContasCount()
        if (count < 200) {
            dao.insertContas(DatabaseSeeder.getSeedContas())
        }
        dao.getAllContas().collect { emit(it) }
    }

    fun getElementosFlow(empresaId: Long): Flow<List<ElementoPatrimonial>> {
        return dao.getElementosByEmpresa(empresaId)
    }

    suspend fun getElementosSync(empresaId: Long): List<ElementoPatrimonial> {
        return dao.getElementosByEmpresaSync(empresaId)
    }

    suspend fun insertElemento(elemento: ElementoPatrimonial): Long {
        return dao.insertElemento(elemento)
    }

    suspend fun deleteElemento(elemento: ElementoPatrimonial) {
        dao.deleteElemento(elemento)
    }

    suspend fun updateElemento(elemento: ElementoPatrimonial) {
        dao.updateElemento(elemento)
    }

    // Reports persistence
    fun getInventariosFlow(empresaId: Long): Flow<List<Inventario>> {
        return dao.getInventariosByEmpresa(empresaId)
    }

    suspend fun insertInventario(inventario: Inventario): Long {
        return dao.insertInventario(inventario)
    }

    fun getBalancosFlow(empresaId: Long): Flow<List<Balanco>> {
        return dao.getBalancosByEmpresa(empresaId)
    }

    suspend fun insertBalanco(balanco: Balanco): Long {
        return dao.insertBalanco(balanco)
    }

    // UC-02: CaluclarPatrimónio & Situação Patrimonial based on typical Mozambican teaching rules (RA3 manual)
    fun calculateResumoPatrimonial(
        elementos: List<ElementoPatrimonial>,
        contasMap: Map<String, ContaPGC>
    ): ResumoPatrimonial {
        var totalBens = 0.0
        var totalDireitos = 0.0
        var totalObrigacoes = 0.0

        for (el in elementos) {
            val conta = contasMap[el.contaCodigo]
            if (conta == null) {
                // Fallback to active/passive by prefix code if not matched
                if (el.contaCodigo.startsWith("4.2") || el.contaCodigo.startsWith("4.3") || el.contaCodigo.startsWith("4.4") || el.contaCodigo.startsWith("4.6")) {
                    totalObrigacoes += el.valor
                } else if (el.contaCodigo.startsWith("5")) {
                    // Capital is Capital Proprio, not direct Obligation or Asset
                } else {
                    totalBens += el.valor
                }
                continue
            }

            when (conta.natureza) {
                "ACTIVO" -> {
                    // 1.1 (Caixa), Class 2 (Mercadorias), and 3.2 (Ativos Tangiveis) are physical BENS
                    // 1.2 (Bancos), 4.1 (Clientes), 4.5 (Outros Devedores) are financial DIREITOS
                    if (conta.codigo.startsWith("1.1") ||
                        conta.codigo.startsWith("2") ||
                        conta.codigo.startsWith("3.2") ||
                        conta.codigo.startsWith("3.3")
                    ) {
                        totalBens += el.valor
                    } else {
                        totalDireitos += el.valor
                    }
                }
                "PASSIVO" -> {
                    // All liabilities are OBRIGAÇÕES
                    totalObrigacoes += el.valor
                }
                // Class 5, 6, 7 are handled accordingly
            }
        }

        val totalActivo = totalBens + totalDireitos
        val totalPassivo = totalObrigacoes
        val capitalProprio = totalActivo - totalPassivo

        val (situacao, color) = when {
            totalActivo > totalPassivo -> Pair("Boa", "VERDE")
            totalActivo == totalPassivo -> Pair("Menos Boa", "AMARELO")
            else -> Pair("Péssima", "VERMELHO")
        }

        return ResumoPatrimonial(
            totalBens = totalBens,
            totalDireitos = totalDireitos,
            totalObrigacoes = totalObrigacoes,
            totalActivo = totalActivo,
            totalPassivo = totalPassivo,
            capitalProprio = capitalProprio,
            situacaoPatrimonial = situacao,
            situacaoColor = color
        )
    }

    // UC-03 & UC-04: Grouping elements into structured classes and accounts for Inventories
    fun generateInventarioEstruturado(
        elementos: List<ElementoPatrimonial>,
        contasMap: Map<String, ContaPGC>
    ): List<GrupoClasse> {
        val itensPorClasse = elementos.groupBy { el ->
            val conta = contasMap[el.contaCodigo]
            conta?.classe ?: el.contaCodigo.first().toString().toIntOrNull() ?: 9
        }

        return itensPorClasse.map { (classeId, classItems) ->
            val classTitle = when (classeId) {
                1 -> "Classe 1 - Meios Financeiros"
                2 -> "Classe 2 - Inventários e Activos Biológicos"
                3 -> "Classe 3 - Investimentos de Capital"
                4 -> "Classe 4 - Contas a Receber, Contas a Pagar, Acréscimos e Diferimentos"
                5 -> "Classe 5 - Capital Próprio"
                6 -> "Classe 6 - Gastos e Perdas"
                7 -> "Classe 7 - Rendimentos e Ganhos"
                8 -> "Classe 8 - Resultados"
                else -> "Outras Classes"
            }

            val contasNoGrupo = classItems.groupBy { it.contaCodigo }.map { (contaCod, elList) ->
                val conta = contasMap[contaCod] ?: ContaPGC(contaCod, "Conta sem classificação", classeId, "ACTIVO", null)
                val subtotal = elList.sumOf { it.valor }
                GrupoConta(conta, subtotal, elList)
            }.sortedBy { it.conta.codigo }

            val classTotal = contasNoGrupo.sumOf { it.subtotal }
            GrupoClasse(classeId, classTitle, classTotal, contasNoGrupo)
        }.sortedBy { it.classe }
    }

    // UC-05: Calculations for a Balanced Balance Sheet
    fun calculateBalanco(
        elementos: List<ElementoPatrimonial>,
        contasMap: Map<String, ContaPGC>
    ): BalancoCalculado {
        // Filter entities by Nature
        val activos = mutableListOf<ElementoPatrimonial>()
        val passivos = mutableListOf<ElementoPatrimonial>()
        val capitalProprios = mutableListOf<ElementoPatrimonial>()

        for (el in elementos) {
            val conta = contasMap[el.contaCodigo]
            val nat = conta?.natureza ?: "ACTIVO"
            when (nat) {
                "ACTIVO", "GASTO" -> activos.add(el)
                "PASSIVO", "RENDIMENTO", "RESULTADO" -> passivos.add(el)
                "CAPITAL_PROPRIO" -> capitalProprios.add(el)
            }
        }

        // Subdivide Activo into Current and Non-Current (Corrente / Não Corrente)
        val actNaoCorrentes = mutableListOf<ElementoPatrimonial>()
        val actCorrentes = mutableListOf<ElementoPatrimonial>()

        for (el in activos) {
            val conta = contasMap[el.contaCodigo]
            val isCorrente = conta?.typeCorrencia == "CORRENTE"
            if (isCorrente) actCorrentes.add(el) else actNaoCorrentes.add(el)
        }

        // Subdivide Passivo into Current and Non-Current
        val passNaoCorrentes = mutableListOf<ElementoPatrimonial>()
        val passCorrentes = mutableListOf<ElementoPatrimonial>()

        for (el in passivos) {
            val conta = contasMap[el.contaCodigo]
            // R-02: Empréstimos (4.3) - Curto vs Médio/Longo prazo based on months
            val isLoan = el.contaCodigo.startsWith("4.3")
            val correnciaDetermined = if (isLoan && el.prazoMeses != null) {
                if (el.prazoMeses <= 12) "CORRENTE" else "NAO_CORRENTE"
            } else {
                conta?.typeCorrencia ?: "CORRENTE"
            }

            if (correnciaDetermined == "CORRENTE") {
                passCorrentes.add(el)
            } else {
                passNaoCorrentes.add(el)
            }
        }

        // Group into GrupoConta structure helper
        fun listToGrupoConta(list: List<ElementoPatrimonial>): List<GrupoConta> {
            return list.groupBy { it.contaCodigo }.map { (codigo, items) ->
                val conta = contasMap[codigo] ?: ContaPGC(codigo, "Conta sem classificação", codigo.first().toString().toIntOrNull() ?: 9, "ACTIVO", null)
                GrupoConta(conta, items.sumOf { it.valor }, items)
            }.sortedBy { it.conta.codigo }
        }

        val gActNaoCorrentes = listToGrupoConta(actNaoCorrentes)
        val totalActNaoCorrente = gActNaoCorrentes.sumOf { it.subtotal }

        val gActCorrentes = listToGrupoConta(actCorrentes)
        val totalActCorrente = gActCorrentes.sumOf { it.subtotal }

        val totalActivos = totalActNaoCorrente + totalActCorrente

        val gPassNaoCorrentes = listToGrupoConta(passNaoCorrentes)
        val totalPassNC = gPassNaoCorrentes.sumOf { it.subtotal }

        val gPassCorrentes = listToGrupoConta(passCorrentes)
        val totalPassC = gPassCorrentes.sumOf { it.subtotal }

        val totalPassivos = totalPassNC + totalPassC

        val gCapProprio = listToGrupoConta(capitalProprios)
        val totalCapProprioSalvado = gCapProprio.sumOf { it.subtotal }

        // R-03 & UC-05: Auto calculate Net Income (Resultado Líquido) to balance the equation
        // Activo = CP (Capital Social + Reservas + Resultado Líquido) + Passivo
        // Resultado Liquido = Activo - Passivo - Outros Capitais Próprios
        val resultadoLiquidoPeriodo = totalActivos - (totalCapProprioSalvado + totalPassivos)

        val totalCapitalProprio = totalCapProprioSalvado + resultadoLiquidoPeriodo

        val totalCpEPassivo = totalCapitalProprio + totalPassivos

        // Check if balance matches
        val balancoFecha = Math.abs(totalActivos - totalCpEPassivo) < 0.01

        return BalancoCalculado(
            activosNaoCorrentes = gActNaoCorrentes,
            totalActivosNaoCorrentes = totalActaoCorrente(totalActNaoCorrente),
            activosCorrentes = gActCorrentes,
            totalActivosCorrentes = totalActCorrente,
            totalActivos = totalActivos,
            capitalProprioItens = gCapProprio,
            resultadoLiquidoPeriodo = resultadoLiquidoPeriodo,
            totalCapitalProprio = totalCapitalProprio,
            passivosNaoCorrentes = gPassNaoCorrentes,
            totalPassivosNaoCorrentes = totalPassNC,
            passivosCorrentes = gPassCorrentes,
            totalPassivosCorrentes = totalPassC,
            totalPassivos = totalPassivos,
            totalCpEPassivo = totalCpEPassivo,
            balancoFecha = balancoFecha
        )
    }

    private fun totalActaoCorrente(value: Double): Double = value
}
