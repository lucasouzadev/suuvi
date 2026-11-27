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

extension SampleData {
    static let folders: [FolderItem] = [
        FolderItem(id: "f1", name: "Lisboa", emoji: "🏺", place: "lisboa", number: 12, lastUsedEpochDay: 20320),
        FolderItem(id: "f2", name: "Café", emoji: "☕️", place: "cafe", number: 5, lastUsedEpochDay: 20315),
        FolderItem(id: "f3", name: "Feira", emoji: "🌿", place: "feira", number: 7, lastUsedEpochDay: 20300),
        FolderItem(id: "f4", name: "Praia", emoji: "🐚", place: "praia", number: 3, lastUsedEpochDay: 20280),
        FolderItem(id: "f5", name: "Mercado", emoji: "🍎", place: "mercado", number: 2, lastUsedEpochDay: 20250),
    ]
}
