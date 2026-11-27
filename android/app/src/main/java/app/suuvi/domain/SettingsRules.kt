package app.suuvi.domain

/** Regras das Configuracoes. Mesmas regras no iOS (SettingsRules.swift); fixtures/settings.json valida as duas. */
object SettingsRules {
    const val MAX_NAME_LENGTH = 24

    /** Formatos aceitos; o primeiro e o padrao. */
    val tiers = listOf("coin", "pin", "magnet")

    /** Apara as pontas, colapsa espacos repetidos, corta em 24 caracteres e apara de novo. */
    fun normalizeName(raw: String): String =
        raw.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.joinToString(" ")
            .take(MAX_NAME_LENGTH).trim()
}
