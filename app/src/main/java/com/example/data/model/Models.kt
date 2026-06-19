package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "empresa")
data class Empresa(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val actividade: String,
    val cidade: String,
    val nuit: String?,
    val dataRegisto: String, // Stored as ISO string (e.g., "YYYY-MM-DD")
    val moeda: String = "MZN"
)

@Entity(tableName = "conta_pgc")
data class ContaPGC(
    @PrimaryKey val codigo: String, // e.g. "1.1" or "3.2.4"
    val titulo: String,
    val classe: Int, // e.g., 1, 2, 3...
    val natureza: String, // ACTIVO, PASSIVO, CAPITAL_PROPRIO, GASTO, RENDIMENTO, RESULTADO
    val typeCorrencia: String? // CORRENTE, NAO_CORRENTE or null
)

@Entity(
    tableName = "elemento_patrimonial",
    foreignKeys = [
        ForeignKey(
            entity = Empresa::class,
            parentColumns = ["id"],
            childColumns = ["empresaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ContaPGC::class,
            parentColumns = ["codigo"],
            childColumns = ["contaCodigo"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class ElementoPatrimonial(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val empresaId: Long,
    val descricao: String,
    val valor: Double,
    val quantidade: Double = 1.0,
    val valorUnitario: Double? = null,
    val contaCodigo: String, // FK to ContaPGC
    val dataInventario: String,
    val observacoes: String? = null,
    val prazoMeses: Int? = null // For loans (R-02 rules)
)

@Entity(
    tableName = "inventario",
    foreignKeys = [
        ForeignKey(
            entity = Empresa::class,
            parentColumns = ["id"],
            childColumns = ["empresaId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Inventario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val empresaId: Long,
    val data: String,
    val tipo: String, // GERAL, PARCIAL
    val descricao: String, // ANALITICO, SINTETICO
    val momento: String, // INICIAL, FINAL, ORDINARIO, EXTRAORDINARIO
    val criadoEm: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "balanco",
    foreignKeys = [
        ForeignKey(
            entity = Empresa::class,
            parentColumns = ["id"],
            childColumns = ["empresaId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Balanco(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val empresaId: Long,
    val dataAtual: String,
    val dataAnterior: String?, // Null means Initial Balance (Abertura)
    val tipo: String, // INICIAL, FINAL, ANALITICO, SINTETICO
    val observacoes: String? = null,
    val criadoEm: Long = System.currentTimeMillis()
)

enum class NaturezaConta(val visualName: String) {
    ACTIVO("Activo"),
    PASSIVO("Passivo"),
    CAPITAL_PROPRIO("Capital Próprio"),
    GASTO("Gasto"),
    RENDIMENTO("Rendimento"),
    RESULTADO("Resultado")
}

enum class TipoCorrencia(val visualName: String) {
    CORRENTE("Corrente"),
    NAO_CORRENTE("Não Corrente")
}
