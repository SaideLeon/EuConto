package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MonthlyFinancialData(
    val monthCode: String,
    val monthName: String,
    val lucro: Double,
    val prejuizo: Double
)

class AccountingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AccountingRepository(application)
    private val prefs = application.getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleTheme() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue
        prefs.edit().putBoolean("is_dark_mode", newValue).apply()
    }

    // Current State
    val empresas = repository.empresas
    val contas = repository.contas

    private val _selectedEmpresaId = MutableStateFlow<Long?>(null)
    val selectedEmpresaId: StateFlow<Long?> = _selectedEmpresaId.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedEmpresa: StateFlow<Empresa?> = _selectedEmpresaId
        .flatMapLatest { id ->
            if (id != null) repository.getEmpresaById(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val elementos: StateFlow<List<ElementoPatrimonial>> = _selectedEmpresaId
        .flatMapLatest { id ->
            if (id != null) repository.getElementosFlow(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Convert contas to map for fast O(1) lookups
    val contasMap: StateFlow<Map<String, ContaPGC>> = contas
        .map { list -> list.associateBy { it.codigo } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    // Real-time calculations of totals, status, and colored badges
    val resumoPatrimonial: StateFlow<ResumoPatrimonial?> = combine(elementos, contasMap) { els, maps ->
        if (els.isEmpty()) {
            ResumoPatrimonial(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "Boa", "VERDE")
        } else {
            repository.calculateResumoPatrimonial(els, maps)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Monthly progression of Lucro vs Prejuizo (simulation linked to registered elements to keep active graph)
    val monthlyProgression: StateFlow<List<MonthlyFinancialData>> = combine(elementos, contasMap) { els, maps ->
        val months = listOf(
            "01" to "Jan", "02" to "Fev", "03" to "Mar",
            "04" to "Abr", "05" to "Mai", "06" to "Jun",
            "07" to "Jul", "08" to "Ago", "09" to "Set",
            "10" to "Out", "11" to "Nov", "12" to "Dez"
        )

        val baseLucros = mapOf(
            "01" to 120000.0, "02" to 145000.0, "03" to 110000.0,
            "04" to 175000.0, "05" to 160000.0, "06" to 195000.0,
            "07" to 180000.0, "08" to 210000.0, "09" to 225000.0,
            "10" to 205000.0, "11" to 240000.0, "12" to 285000.0
        )
        val basePrejuizos = mapOf(
            "01" to 85000.0, "02" to 95000.0, "03" to 90000.0,
            "04" to 115000.0, "05" to 105000.0, "06" to 125000.0,
            "07" to 110000.0, "08" to 135000.0, "09" to 140000.0,
            "10" to 130000.0, "11" to 155000.0, "12" to 170000.0
        )

        val elementsByMonth = els.groupBy { el ->
            val parts = el.dataInventario.split("-")
            if (parts.size >= 2) parts[1] else "06"
        }

        months.map { (code, name) ->
            var lucVal = baseLucros[code] ?: 0.0
            var prejVal = basePrejuizos[code] ?: 0.0

            val monthElements = elementsByMonth[code] ?: emptyList()
            for (el in monthElements) {
                val conta = maps[el.contaCodigo]
                if (conta == null) {
                    if (el.contaCodigo.startsWith("4")) {
                        prejVal += el.valor
                    } else {
                        lucVal += el.valor
                    }
                } else {
                    when (conta.natureza) {
                        "ACTIVO" -> lucVal += el.valor * 0.15
                        "PASSIVO" -> prejVal += el.valor * 0.15
                        "RENDIMENTO" -> lucVal += el.valor
                        "GASTO" -> prejVal += el.valor
                        "RESULTADO" -> {
                            if (el.valor >= 0) lucVal += el.valor else prejVal += Math.abs(el.valor)
                        }
                    }
                }
            }

            MonthlyFinancialData(
                monthCode = code,
                monthName = name,
                lucro = lucVal,
                prejuizo = prejVal
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Map of each company ID to its corresponding portfolio health status (ResumoPatrimonial) computed dynamically
    val empresasResumo: StateFlow<Map<Long, ResumoPatrimonial>> = combine(
        empresas,
        repository.allElementos,
        contasMap
    ) { emps, elements, cMap ->
        val map = mutableMapOf<Long, ResumoPatrimonial>()
        val elementsByEmp = elements.groupBy { it.empresaId }
        for (emp in emps) {
            val els = elementsByEmp[emp.id] ?: emptyList()
            map[emp.id] = repository.calculateResumoPatrimonial(els, cMap)
        }
        map
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Structured Class List for reports
    val inventarioClasses: StateFlow<List<GrupoClasse>> = combine(elementos, contasMap) { els, maps ->
        repository.generateInventarioEstruturado(els, maps)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Structured Balance calculations
    val balancoCalculado: StateFlow<BalancoCalculado?> = combine(elementos, contasMap) { els, maps ->
        if (els.isEmpty()) null else repository.calculateBalanco(els, maps)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Accounting Actions
    fun selectEmpresa(id: Long) {
        _selectedEmpresaId.value = id
    }

    fun insertEmpresa(nome: String, actividade: String, cidade: String, nuit: String?, dataRegisto: String, onComplete: (Long) -> Unit) {
        viewModelScope.launch {
            val emp = Empresa(
                nome = nome,
                actividade = actividade,
                cidade = cidade,
                nuit = nuit?.ifBlank { null },
                dataRegisto = dataRegisto
            )
            val insertedId = repository.createEmpresa(emp)
            selectEmpresa(insertedId)
            onComplete(insertedId)
        }
    }

    fun deleteEmpresa(empresa: Empresa, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteEmpresa(empresa)
            _selectedEmpresaId.value = null
            onComplete()
        }
    }

    fun addElemento(
        descricao: String,
        valor: Double,
        quantidade: Double,
        valorUnitario: Double?,
        contaCodigo: String,
        dataInventario: String,
        observacoes: String? = null,
        prazoMeses: Int? = null,
        onComplete: () -> Unit
    ) {
        val empId = _selectedEmpresaId.value ?: return
        viewModelScope.launch {
            val element = ElementoPatrimonial(
                empresaId = empId,
                descricao = descricao,
                valor = valor,
                quantidade = quantidade,
                valorUnitario = valorUnitario,
                contaCodigo = contaCodigo,
                dataInventario = dataInventario,
                observacoes = observacoes?.ifBlank { null },
                prazoMeses = prazoMeses
            )
            repository.insertElemento(element)
            onComplete()
        }
    }

    fun deleteElemento(elemento: ElementoPatrimonial) {
        viewModelScope.launch {
            repository.deleteElemento(elemento)
        }
    }

    fun generateInventarioRecord(
        tipo: String, // GERAL, PARCIAL
        descricao: String, // ANALITICO, SINTETICO
        momento: String, // INICIAL, FINAL, ORDINARIO, EXTRAORDINARIO
        data: String,
        onComplete: (Long) -> Unit
    ) {
        val empId = _selectedEmpresaId.value ?: return
        viewModelScope.launch {
            val record = Inventario(
                empresaId = empId,
                data = data,
                tipo = tipo,
                descricao = descricao,
                momento = momento
            )
            val id = repository.insertInventario(record)
            onComplete(id)
        }
    }

    fun generateBalancoRecord(
        dataAtual: String,
        dataAnterior: String?,
        tipo: String, // INICIAL, FINAL, ANALITICO, SINTETICO
        observacoes: String?,
        onComplete: (Long) -> Unit
    ) {
        val empId = _selectedEmpresaId.value ?: return
        viewModelScope.launch {
            val record = Balanco(
                empresaId = empId,
                dataAtual = dataAtual,
                dataAnterior = dataAnterior,
                tipo = tipo,
                observacoes = observacoes
            )
            val id = repository.insertBalanco(record)
            onComplete(id)
        }
    }
}
