package app.suuvi.domain

data class NewPiece(val number: Int, val title: String, val place: String, val tag: String?, val note: String?, val lastIssued: Int)

data class EditedPiece(val title: String, val place: String, val tag: String?, val note: String?)

/** Criacao de peca (salvar na Edicao). Mesmas regras no iOS (PieceRules.swift); fixtures/create.json valida as duas. */
object PieceRules {
    /** Sem espacos das pontas, sem '@' inicial, minusculo. */
    fun normalizePlace(raw: String): String = raw.trim().removePrefix("@").trim().lowercase()

    /** Imagem da peca: primeiro recorte, senao a primeira foto (caminhos relativos a pasta do app). */
    fun imagePath(cutouts: List<String?>, photos: List<String>): String? = cutouts.filterNotNull().firstOrNull() ?: photos.firstOrNull()

    /** null se invalido. Caso contrario, a peca normalizada com o proximo #n. */
    fun create(title: String, place: String, tag: String, existing: List<Int>, lastIssued: Int, note: String = ""): NewPiece? {
        val t = title.trim()
        val p = normalizePlace(place)
        val nt = EditRules.normalizeNote(note)
        if (t.isEmpty() || p.isEmpty() || t.length > EditRules.MAX_TITLE || (nt?.length ?: 0) > EditRules.MAX_NOTE) return null
        val g = EditRules.normalizeTag(tag)
        val n = Numbering.next(existing, lastIssued)
        return NewPiece(n, t, p, g.ifEmpty { null }, nt, n)
    }

    /** Edicao de peca existente: mesma normalizacao e validacao da criacao; o #n nao muda. null se invalido. */
    fun update(title: String, place: String, tag: String, note: String = ""): EditedPiece? {
        val t = title.trim()
        val p = normalizePlace(place)
        val nt = EditRules.normalizeNote(note)
        if (t.isEmpty() || p.isEmpty() || t.length > EditRules.MAX_TITLE || (nt?.length ?: 0) > EditRules.MAX_NOTE) return null
        val g = EditRules.normalizeTag(tag)
        return EditedPiece(t, p, g.ifEmpty { null }, nt)
    }

    /** Ultimo #n emitido na colecao: o maior entre o contador guardado e os numeros existentes. Apagar nao baixa o contador. */
    fun issued(existing: List<Int>, stored: Int): Int = maxOf(stored, existing.maxOrNull() ?: 0)
}
