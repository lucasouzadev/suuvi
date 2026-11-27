package app.suuvi.domain

data class NewPiece(val number: Int, val title: String, val place: String, val tag: String?, val lastIssued: Int)

/** Criacao de peca (salvar na Edicao). Mesmas regras no iOS (PieceRules.swift); fixtures/create.json valida as duas. */
object PieceRules {
    /** Sem espacos das pontas, sem '@' inicial, minusculo. */
    fun normalizePlace(raw: String): String = raw.trim().removePrefix("@").trim().lowercase()

    /** null se invalido. Caso contrario, a peca normalizada com o proximo #n. */
    fun create(title: String, place: String, tag: String, existing: List<Int>, lastIssued: Int): NewPiece? {
        val t = title.trim()
        val p = normalizePlace(place)
        if (t.isEmpty() || p.isEmpty() || t.length > EditRules.MAX_TITLE) return null
        val g = EditRules.normalizeTag(tag)
        val n = Numbering.next(existing, lastIssued)
        return NewPiece(n, t, p, g.ifEmpty { null }, n)
    }
}
