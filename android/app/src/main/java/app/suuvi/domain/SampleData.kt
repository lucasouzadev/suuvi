package app.suuvi.domain

/** Linha da lista da Shelf (resumo de uma colecao). */
data class ShelfItem(
    val id: String,
    val name: String,
    val emoji: String,
    val place: String,
    val tier: PieceTier,
    val number: Int,
    val status: SouvenirStatus,
)

data class DailyPick(
    val emoji: String,
    val title: String,
    val note: String,
    val epochDay: Long,
    val place: String,
)

/** Dados de exemplo ate a Shelf ler do banco local. */
object SampleData {
    val collection = listOf(
        ShelfItem("c1", "Lisboa", "🏺", "lisboa", PieceTier.COIN, 12, SouvenirStatus.READY),
        ShelfItem("c2", "Café", "☕️", "cafe", PieceTier.MAGNET, 5, SouvenirStatus.DRAFT),
        ShelfItem("c3", "Feira", "🌿", "feira", PieceTier.PIN, 7, SouvenirStatus.FAILED),
    )
    val received = listOf(
        ShelfItem("r1", "Praia", "🐚", "ana", PieceTier.COIN, 3, SouvenirStatus.READY),
        ShelfItem("r2", "Mercado", "🍎", "bia", PieceTier.PIN, 2, SouvenirStatus.DRAFT),
    )
    val dailyPicks = listOf(
        DailyPick("🏺", "Moeda de Lisboa", "Achada numa feira de rua, num domingo de sol.", 20150, "lisboa"),
        DailyPick("☕️", "Ímã do café", "O primeiro café depois da viagem.", 20210, "cafe"),
        DailyPick("🌿", "Pin da feira", "Uma folha que ficou na bolsa.", 20300, "feira"),
    )
}
