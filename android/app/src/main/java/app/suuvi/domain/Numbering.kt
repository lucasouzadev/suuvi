package app.suuvi.domain

/** Regra do #n. Mesma regra no iOS (Numbering.swift); fixtures/numbering.json valida as duas. */
object Numbering {
    /** proximo = max(maior numero existente, lastIssued) + 1 */
    fun next(existing: List<Int>, lastIssued: Int = 0): Int =
        maxOf(existing.maxOrNull() ?: 0, lastIssued) + 1
}
