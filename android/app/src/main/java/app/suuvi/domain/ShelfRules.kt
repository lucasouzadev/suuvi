package app.suuvi.domain

import java.time.LocalDate

/** Regras da Shelf. Mesmas regras no iOS (ShelfRules.swift); fixtures/shelf.json valida as duas. */
object ShelfRules {
    /** Indice do "suvenir do dia"; null se a lista estiver vazia. */
    fun dailyIndex(epochDay: Long, count: Int): Int? {
        if (count <= 0) return null
        return (((epochDay % count) + count) % count).toInt()
    }

    /** Primeira letra ou digito do titulo, em maiusculo; "?" se nao houver. */
    fun chipLetter(title: String): String {
        val ch = title.firstOrNull { it.isLetterOrDigit() } ?: return "?"
        return ch.toString().uppercase()
    }

    fun todayEpochDay(): Long = LocalDate.now().toEpochDay()
}
