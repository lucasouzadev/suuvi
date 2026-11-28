import Foundation

struct NewPiece: Equatable {
    let number: Int
    let title: String
    let place: String
    let tag: String?
    let lastIssued: Int
}

struct EditedPiece: Equatable {
    let title: String
    let place: String
    let tag: String?
}

/// Criacao de peca (salvar na Edicao). Mesmas regras no Android (PieceRules.kt); fixtures/create.json valida as duas.
enum PieceRules {
    /// Sem espacos das pontas, sem '@' inicial, minusculo.
    static func normalizePlace(_ raw: String) -> String {
        var s = raw.trimmingCharacters(in: .whitespaces)
        if s.hasPrefix("@") { s.removeFirst() }
        return s.trimmingCharacters(in: .whitespaces).lowercased()
    }

    /// Imagem da peca: primeiro recorte, senao a primeira foto (caminhos relativos a pasta do app).
    static func imagePath(cutouts: [String?], photos: [String]) -> String? { cutouts.compactMap { $0 }.first ?? photos.first }

    /// nil se invalido. Caso contrario, a peca normalizada com o proximo #n.
    static func create(title: String, place: String, tag: String, existing: [Int], lastIssued: Int) -> NewPiece? {
        let t = title.trimmingCharacters(in: .whitespaces)
        let p = normalizePlace(place)
        guard !t.isEmpty, !p.isEmpty, t.count <= EditRules.maxTitle else { return nil }
        let g = EditRules.normalizeTag(tag)
        let n = Numbering.next(existing: existing, lastIssued: lastIssued)
        return NewPiece(number: n, title: t, place: p, tag: g.isEmpty ? nil : g, lastIssued: n)
    }

    /// Edicao de peca existente: mesma normalizacao e validacao da criacao; o #n nao muda. nil se invalido.
    static func update(title: String, place: String, tag: String) -> EditedPiece? {
        let t = title.trimmingCharacters(in: .whitespaces)
        let p = normalizePlace(place)
        guard !t.isEmpty, !p.isEmpty, t.count <= EditRules.maxTitle else { return nil }
        let g = EditRules.normalizeTag(tag)
        return EditedPiece(title: t, place: p, tag: g.isEmpty ? nil : g)
    }

    /// Ultimo #n emitido na colecao: o maior entre o contador guardado e os numeros existentes. Apagar nao baixa o contador.
    static func issued(existing: [Int], stored: Int) -> Int { max(stored, existing.max() ?? 0) }
}
