package app.suuvi.domain

/** Regras da Edicao de peca. Mesmas regras no iOS (EditRules.swift); fixtures/edit.json valida as duas. */
object EditRules {
    const val MAX_TITLE = 40
    const val MAX_NOTE = 140

    /** Nota opcional: tira espacos das pontas; vazia = null. */
    fun normalizeNote(raw: String): String? = raw.trim().ifEmpty { null }

    /** Titulo e lugar obrigatorios (sem contar espacos das pontas); titulo ate 40 caracteres; tag opcional. */
    fun isValid(title: String, place: String): Boolean {
        val t = title.trim()
        return t.isNotEmpty() && place.trim().isNotEmpty() && t.length <= MAX_TITLE
    }

    /** Tira espacos das pontas e '#' inicial; minusculo. */
    fun normalizeTag(raw: String): String = raw.trim().removePrefix("#").lowercase()
}
