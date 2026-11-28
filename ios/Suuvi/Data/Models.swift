import Foundation
import SwiftData

@Model
final class SouvenirCollection {
    @Attribute(.unique) var id: UUID
    var name: String
    /// Ultimo #n emitido. So cresce: apagar peca nao devolve o numero.
    var lastIssued: Int
    var createdAt: Date
    @Relationship(deleteRule: .cascade, inverse: \Souvenir.collection)
    var souvenirs: [Souvenir] = []

    init(name: String) {
        self.id = UUID()
        self.name = name
        self.lastIssued = 0
        self.createdAt = Date()
    }

    /// Reserva o proximo numero e atualiza o contador.
    func issueNumber() -> Int {
        let n = Numbering.next(existing: souvenirs.map(\.number), lastIssued: lastIssued)
        lastIssued = n
        return n
    }
}

@Model
final class Souvenir {
    @Attribute(.unique) var id: UUID
    var number: Int
    var title: String
    var tierRaw: String
    var statusRaw: String
    var createdAt: Date
    var collection: SouvenirCollection?
    @Relationship(deleteRule: .cascade, inverse: \Asset.souvenir)
    var assets: [Asset] = []

    var tier: PieceTier {
        get { PieceTier(rawValue: tierRaw) ?? .default }
        set { tierRaw = newValue.rawValue }
    }
    var status: SouvenirStatus {
        get { SouvenirStatus(rawValue: statusRaw) ?? .draft }
        set { statusRaw = newValue.rawValue }
    }

    init(title: String, number: Int, tier: PieceTier = .default) {
        self.id = UUID()
        self.title = title
        self.number = number
        self.tierRaw = tier.rawValue
        self.statusRaw = SouvenirStatus.draft.rawValue
        self.createdAt = Date()
    }
}

@Model
final class Asset {
    @Attribute(.unique) var id: UUID
    /// "photo", "cutout" ou "model"
    var kind: String
    /// Caminho relativo dentro da pasta do app (nunca absoluto).
    var relativePath: String
    var souvenir: Souvenir?

    init(kind: String, relativePath: String) {
        self.id = UUID()
        self.kind = kind
        self.relativePath = relativePath
    }
}

/// Peca criada pela Edicao, guardada por colecao (folderId). O #n vem de PieceRules/Numbering.
@Model
final class SavedPiece {
    @Attribute(.unique) var id: UUID
    var folderId: String
    var number: Int
    var title: String
    var place: String
    var tag: String?
    /// Nota opcional (ate 140 caracteres); aparece no card "suvenir do dia".
    var note: String?
    var emoji: String
    /// Imagem da peca (recorte ou foto), relativa a pasta do app; nil = so emoji.
    var imagePath: String?
    var createdAt: Date

    init(folderId: String, number: Int, title: String, place: String, tag: String?, note: String? = nil, emoji: String = "🪙", imagePath: String? = nil) {
        self.id = UUID()
        self.folderId = folderId
        self.number = number
        self.title = title
        self.place = place
        self.tag = tag
        self.note = note
        self.emoji = emoji
        self.imagePath = imagePath
        self.createdAt = Date()
    }
}
