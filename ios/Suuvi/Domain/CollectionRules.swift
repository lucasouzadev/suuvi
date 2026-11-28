import Foundation

struct PieceItem: Identifiable {
    let id: String
    let title: String
    let emoji: String
    var imagePath: String? = nil
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

    /// Quais das 4 posicoes da grade tem peca (true) e quais sao vagas (false).
    static func slots(count: Int) -> [Bool] {
        (0..<capacity).map { $0 < count }
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
