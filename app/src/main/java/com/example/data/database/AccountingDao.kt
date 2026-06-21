package com.example.data.database

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountingDao {
    // Empresa (Companies)
    @Query("SELECT * FROM empresa ORDER BY id DESC")
    fun getAllEmpresas(): Flow<List<Empresa>>

    @Query("SELECT * FROM empresa WHERE id = :id LIMIT 1")
    fun getEmpresaByIdFlow(id: Long): Flow<Empresa?>

    @Query("SELECT * FROM empresa WHERE id = :id LIMIT 1")
    suspend fun getEmpresaById(id: Long): Empresa?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmpresa(empresa: Empresa): Long

    @Update
    suspend fun updateEmpresa(empresa: Empresa)

    @Delete
    suspend fun deleteEmpresa(empresa: Empresa)

    // ContaPGC (Chart of Accounts PGC-NIRF)
    @Query("SELECT * FROM conta_pgc ORDER BY codigo ASC")
    fun getAllContas(): Flow<List<ContaPGC>>

    @Query("SELECT * FROM conta_pgc WHERE codigo = :codigo LIMIT 1")
    suspend fun getContaByCodigo(codigo: String): ContaPGC?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContas(contas: List<ContaPGC>)

    @Query("SELECT COUNT(*) FROM conta_pgc")
    suspend fun getContasCount(): Int

    // ElementoPatrimonial (Asset / Liability Elements)
    @Query("SELECT * FROM elemento_patrimonial")
    fun getAllElementos(): Flow<List<ElementoPatrimonial>>

    @Query("SELECT * FROM elemento_patrimonial WHERE empresaId = :empresaId")
    fun getElementosByEmpresa(empresaId: Long): Flow<List<ElementoPatrimonial>>

    @Query("SELECT * FROM elemento_patrimonial WHERE empresaId = :empresaId")
    suspend fun getElementosByEmpresaSync(empresaId: Long): List<ElementoPatrimonial>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElemento(elemento: ElementoPatrimonial): Long

    @Update
    suspend fun updateElemento(elemento: ElementoPatrimonial)

    @Delete
    suspend fun deleteElemento(elemento: ElementoPatrimonial)

    // Inventarios (Inventories)
    @Query("SELECT * FROM inventario WHERE empresaId = :empresaId ORDER BY criadoEm DESC")
    fun getInventariosByEmpresa(empresaId: Long): Flow<List<Inventario>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventario(inventario: Inventario): Long

    // Balancos (Balance sheets)
    @Query("SELECT * FROM balanco WHERE empresaId = :empresaId ORDER BY criadoEm DESC")
    fun getBalancosByEmpresa(empresaId: Long): Flow<List<Balanco>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalanco(balanco: Balanco): Long
}
