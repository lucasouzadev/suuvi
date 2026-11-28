import Foundation

struct FolderItem: Identifiable {
    let id: String
    let name: String
    let emoji: String
    let place: String
    let number: Int
    let lastUsedEpochDay: Int

    init(id: String = UUID().uuidString, name: String, emoji: String, place: String, number: Int, lastUsedEpochDay: Int) {
        self.id = id
        self.name = name
        self.emoji = emoji
        self.place = place
        self.number = number
        self.lastUsedEpochDay = lastUsedEpochDay
    }
}

/// Regras da tela Pastas. Mesmas regras no Android (FolderRules.kt); fixtures/folders.json valida as duas.
enum FolderRules {
    /// Mais usada recentemente primeiro; empate por nome (minusculo, alfabetico).
    static func ordered(_ items: [FolderItem]) -> [FolderItem] {
        items.sorted {
            if $0.lastUsedEpochDay != $1.lastUsedEpochDay { return $0.lastUsedEpochDay > $1.lastUsedEpochDay }
            return $0.name.lowercased() < $1.name.lowercased()
        }
    }
}

extension FolderRules {
    /// Dia de ultimo uso efetivo: o maior entre o dia base da pasta e o dia da peca salva mais recente.
    static func lastUsed(base: Int, pieceDays: [Int]) -> Int { max(base, pieceDays.max() ?? base) }
}

extension SampleData {
    /// Pastas de exemplo com o ultimo uso atualizado pelas pecas salvas no banco.
    static func folders(usedBy saved: [SavedPiece]) -> [FolderItem] {
        folders.map { f in
            let days = saved.filter { $0.folderId == f.id }.map { ShelfRules.todayEpochDay(now: $0.createdAt) }
            return FolderItem(id: f.id, name: f.name, emoji: f.emoji, place: f.place, number: f.number,
                              lastUsedEpochDay: FolderRules.lastUsed(base: f.lastUsedEpochDay, pieceDays: days))
        }
    }

    /// Id da pasta pelo nome (a Shelf de exemplo resume as mesmas colecoes).
    static func folderId(forName name: String) -> String? { folders.first { $0.name == name }?.id }

    static let folders: [FolderItem] = [
        FolderItem(id: "f1", name: "Lisboa", emoji: "🏺", place: "lisboa", number: 12, lastUsedEpochDay: 20320),
        FolderItem(id: "f2", name: "Café", emoji: "☕️", place: "cafe", number: 5, lastUsedEpochDay: 20315),
        FolderItem(id: "f3", name: "Feira", emoji: "🌿", place: "feira", number: 7, lastUsedEpochDay: 20300),
        FolderItem(id: "f4", name: "Praia", emoji: "🐚", place: "praia", number: 3, lastUsedEpochDay: 20280),
        FolderItem(id: "f5", name: "Mercado", emoji: "🍎", place: "mercado", number: 2, lastUsedEpochDay: 20250),
    ]
}
