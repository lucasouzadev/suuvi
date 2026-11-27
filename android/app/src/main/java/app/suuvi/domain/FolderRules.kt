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
