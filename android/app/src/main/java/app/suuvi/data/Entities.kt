package app.suuvi.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class SouvenirCollection(
    @PrimaryKey val id: String,
    val name: String,
    /** Ultimo #n emitido. So cresce: apagar peca nao devolve o numero. */
    val lastIssued: Int = 0,
    val createdAt: Long,
)

@Entity(
    tableName = "souvenirs",
    foreignKeys = [ForeignKey(
        entity = SouvenirCollection::class,
        parentColumns = ["id"], childColumns = ["collectionId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("collectionId")],
)
data class Souvenir(
    @PrimaryKey val id: String,
    val collectionId: String,
    val number: Int,
    val title: String,
    /** PieceTier.name */
    val tier: String,
    /** SouvenirStatus.name */
    val status: String,
    val createdAt: Long,
)

@Entity(
    tableName = "assets",
    foreignKeys = [ForeignKey(
        entity = Souvenir::class,
        parentColumns = ["id"], childColumns = ["souvenirId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("souvenirId")],
)
data class Asset(
    @PrimaryKey val id: String,
    val souvenirId: String,
    /** "photo", "cutout" ou "model" */
    val kind: String,
    /** Caminho relativo dentro da pasta do app (nunca absoluto). */
    val relativePath: String,
)
