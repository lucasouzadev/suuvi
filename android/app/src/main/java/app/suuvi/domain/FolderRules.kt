package app.suuvi.domain

data class FolderItem(
    val id: String,
    val name: String,
    val emoji: String,
    val place: String,
    val number: Int,
    val lastUsedEpochDay: Long,
)

/** Regras da tela Pastas. Mesmas regras no iOS (FolderRules.swift); fixtures/folders.json valida as duas. */
object FolderRules {
    /** Mais usada recentemente primeiro; empate por nome (minusculo, alfabetico). */
    fun ordered(items: List<FolderItem>): List<FolderItem> =
        items.sortedWith(
            compareByDescending<FolderItem> { it.lastUsedEpochDay }
                .thenBy { it.name.lowercase() },
        )

    /** Dia de ultimo uso efetivo: o maior entre o dia base da pasta e o dia da peca salva mais recente. */
    fun lastUsed(base: Long, pieceDays: List<Long>): Long = maxOf(base, pieceDays.maxOrNull() ?: base)
}

/** Pastas de exemplo com o ultimo uso atualizado pelas pecas salvas no banco. */
fun SampleData.foldersUsedBy(saved: List<app.suuvi.data.SavedPiece>): List<FolderItem> =
    folders.map { f ->
        val days = saved.filter { it.folderId == f.id }.map { ShelfRules.epochDay(it.createdAt) }
        f.copy(lastUsedEpochDay = FolderRules.lastUsed(f.lastUsedEpochDay, days))
    }

/** Id da pasta pelo nome (a Shelf de exemplo resume as mesmas colecoes). */
fun SampleData.folderId(name: String): String? = folders.firstOrNull { it.name == name }?.id

val SampleData.folders: List<FolderItem>
    get() = listOf(
        FolderItem("f1", "Lisboa", "🏺", "lisboa", 12, 20320),
        FolderItem("f2", "Café", "☕️", "cafe", 5, 20315),
        FolderItem("f3", "Feira", "🌿", "feira", 7, 20300),
        FolderItem("f4", "Praia", "🐚", "praia", 3, 20280),
        FolderItem("f5", "Mercado", "🍎", "mercado", 2, 20250),
    )
