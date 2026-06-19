package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AccountingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AccountingRepository(application)

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
