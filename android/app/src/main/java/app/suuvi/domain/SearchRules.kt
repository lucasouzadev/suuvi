package app.suuvi.domain

data class SearchItem(
    val id: String,
    val name: String,
    val emoji: String,
    val place: String,
    val number: Int,
    val tags: List<String>,
)

/** Regras da Busca. Mesmas regras no iOS (SearchRules.swift); fixtures/search.json valida as duas. */
object SearchRules {
    /** Consulta (nome, lugar ou tag) E todas as tags selecionadas. Ordem original preservada. */
    fun filter(items: List<SearchItem>, query: String, selected: Set<String>): List<SearchItem> {
        val q = query.trim().lowercase()
        return items.filter { item ->
            val matchesQuery = q.isEmpty() ||
                item.name.lowercase().contains(q) ||
                item.place.lowercase().contains(q) ||
                item.tags.any { it.lowercase().contains(q) }
            matchesQuery && selected.all { it in item.tags }
        }
    }

    /** Tags unicas em ordem alfabetica (minusculo). */
    fun allTags(items: List<SearchItem>): List<String> =
        items.flatMap { it.tags }.distinct().sortedBy { it.lowercase() }
}

val SampleData.searchItems: List<SearchItem>
    get() = listOf(
        SearchItem("s1", "Lisboa", "🏺", "lisboa", 12, listOf("viagem", "arte")),
        SearchItem("s2", "Café", "☕️", "cafe", 5, listOf("comida")),
        SearchItem("s3", "Feira", "🌿", "feira", 7, listOf("natureza", "comida")),
        SearchItem("s4", "Praia", "🐚", "praia", 3, listOf("viagem", "natureza")),
        SearchItem("s5", "Mercado", "🍎", "mercado", 2, listOf("comida", "viagem")),
        SearchItem("s6", "Ateliê", "🎨", "atelie", 9, listOf("arte", "casa")),
    )
