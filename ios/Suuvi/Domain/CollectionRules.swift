import Foundation

struct PieceItem: Identifiable {
    let id: String
    let title: String
    let emoji: String
    var imagePath: String? = nil
    /// true = peca salva pelo usuario (pode editar/apagar); false = exemplo.
    var saved: Bool = false
}

struct CollectionDetail {
    let folder: FolderItem
    let status: SouvenirStatus
    let pieces: [PieceItem]
}

/// Regras da tela Colecao. Mesmas regras no Android (CollectionRules.kt); fixtures/collection.json valida as duas.
enum CollectionRules {
    static let capacity = 4
    static let tilts: [Double] = [-2.5, 2.0, 1.5, -2.0]

    /// Posicoes da grade (2 colunas): no minimo 4; cresce com as pecas e fecha a ultima linha com uma vaga. true = peca, false = vaga.
    static func slots(count: Int) -> [Bool] {
        (0..<max(capacity, count + count % 2)).map { $0 < count }
    }

    static func tilt(_ index: Int) -> Double { tilts[index % tilts.count] }
}

extension SampleData {
    static func collectionDetail(id: String) -> CollectionDetail? {
        guard let folder = folders.first(where: { $0.id == id }) else { return nil }
        switch id {
        case "f1":
            return CollectionDetail(folder: folder, status: .ready, pieces: [
                PieceItem(id: "p1", title: "Moeda de Lisboa", emoji: "🏺"),
                PieceItem(id: "p2", title: "Pastel", emoji: "🍰"),
                PieceItem(id: "p3", title: "Bonde 28", emoji: "🚃"),
            ])
        case "f2":
            return CollectionDetail(folder: folder, status: .draft, pieces: [
                PieceItem(id: "p4", title: "Ímã do café", emoji: "☕️"),
            ])
        default:
            return CollectionDetail(folder: folder, status: .draft, pieces: [])
        }
    }
}
