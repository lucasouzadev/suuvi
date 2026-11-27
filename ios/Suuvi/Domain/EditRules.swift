import Foundation

/// Regras da Edicao de peca. Mesmas regras no Android (EditRules.kt); fixtures/edit.json valida as duas.
enum EditRules {
    static let maxTitle = 40

    /// Titulo e lugar obrigatorios (sem contar espacos das pontas); titulo ate 40 caracteres; tag opcional.
    static func isValid(title: String, place: String) -> Bool {
        let t = title.trimmingCharacters(in: .whitespaces)
        let p = place.trimmingCharacters(in: .whitespaces)
        return !t.isEmpty && !p.isEmpty && t.count <= maxTitle
    }

    /// Tira espacos das pontas e '#' inicial; minusculo.
    static func normalizeTag(_ raw: String) -> String {
        var s = raw.trimmingCharacters(in: .whitespaces)
        if s.hasPrefix("#") { s.removeFirst() }
        return s.lowercased()
    }
}
