import Foundation

/// Regras das Configuracoes. Mesmas regras no Android (SettingsRules.kt); fixtures/settings.json valida as duas.
enum SettingsRules {
    static let maxNameLength = 24
    /// Formatos aceitos; o primeiro e o padrao.
    static let tiers = ["coin", "pin", "magnet"]

    /// Apara as pontas, colapsa espacos repetidos, corta em 24 caracteres e apara de novo.
    static func normalizeName(_ raw: String) -> String {
        let collapsed = raw.split(whereSeparator: \.isWhitespace).joined(separator: " ")
        return String(collapsed.prefix(maxNameLength)).trimmingCharacters(in: .whitespaces)
    }
}
