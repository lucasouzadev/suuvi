import Foundation

struct NewPiece: Equatable {
    let number: Int
    let title: String
    let place: String
    let tag: String?
    let lastIssued: Int
}

/// Criacao de peca (salvar na Edicao). Mesmas regras no Android (PieceRules.kt); fixtures/create.json valida as duas.
enum PieceRules {
    /// Sem espacos das pontas, sem '@' inicial, minusculo.
    static func normalizePlace(_ raw: String) -> String {
        var s = raw.trimmingCharacters(in: .whitespaces)
        if s.hasPrefix("@") { s.removeFirst() }
        return s.trimmingCharacters(in: .whitespaces).lowercased()
    }

    /// nil se invalido. Caso contrario, a peca normalizada com o proximo #n.
    static func create(title: String, place: String, tag: String, existing: [Int], lastIssued: Int) -> NewPiece? {
        let t = title.trimmingCharacters(in: .whitespaces)
        let p = normalizePlace(place)
        guard !t.isEmpty, !p.isEmpty, t.count <= EditRules.maxTitle else { return nil }
        let g = EditRules.normalizeTag(tag)
        let n = Numbering.next(existing: existing, lastIssued: lastIssued)
        return NewPiece(number: n, title: t, place: p, tag: g.isEmpty ? nil : g, lastIssued: n)
    }
}
