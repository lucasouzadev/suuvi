import Foundation

struct FolderItem: Identifiable {
    let id = UUID()
    let name: String
    let emoji: String
    let place: String
    let number: Int
    let lastUsedEpochDay: Int
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
        FolderItem(name: "Lisboa", emoji: "🏺", place: "lisboa", number: 12, lastUsedEpochDay: 20320),
        FolderItem(name: "Café", emoji: "☕️", place: "cafe", number: 5, lastUsedEpochDay: 20315),
        FolderItem(name: "Feira", emoji: "🌿", place: "feira", number: 7, lastUsedEpochDay: 20300),
        FolderItem(name: "Praia", emoji: "🐚", place: "praia", number: 3, lastUsedEpochDay: 20280),
        FolderItem(name: "Mercado", emoji: "🍎", place: "mercado", number: 2, lastUsedEpochDay: 20250),
    ]
}
