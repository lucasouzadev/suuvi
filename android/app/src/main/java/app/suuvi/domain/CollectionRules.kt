package app.suuvi.domain

data class PieceItem(val id: String, val title: String, val emoji: String, val imagePath: String? = null)

data class CollectionDetail(val folder: FolderItem, val status: SouvenirStatus, val pieces: List<PieceItem>)

/** Regras da tela Colecao. Mesmas regras no iOS (CollectionRules.swift); fixtures/collection.json valida as duas. */
object CollectionRules {
    const val CAPACITY = 4
    val tilts = listOf(-2.5f, 2.0f, 1.5f, -2.0f)

    /** Quais das 4 posicoes da grade tem peca (true) e quais sao vagas (false). */
    fun slots(count: Int): List<Boolean> = List(CAPACITY) { it < count }

    fun tilt(index: Int): Float = tilts[index % tilts.size]
}

fun SampleData.collectionDetail(id: String): CollectionDetail? {
    val folder = folders.firstOrNull { it.id == id } ?: return null
    return when (id) {
        "f1" -> CollectionDetail(
            folder, SouvenirStatus.READY,
            listOf(
                PieceItem("p1", "Moeda de Lisboa", "🏺"),
                PieceItem("p2", "Pastel", "🍰"),
                PieceItem("p3", "Bonde 28", "🚃"),
            ),
        )
        "f2" -> CollectionDetail(folder, SouvenirStatus.DRAFT, listOf(PieceItem("p4", "Ímã do café", "☕️")))
        else -> CollectionDetail(folder, SouvenirStatus.DRAFT, emptyList())
    }
}
