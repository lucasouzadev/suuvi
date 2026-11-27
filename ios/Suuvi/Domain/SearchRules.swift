import Foundation

struct SearchItem: Identifiable {
    let id: String
    let name: String
    let emoji: String
    let place: String
    let number: Int
    let tags: [String]
}

/// Regras da Busca. Mesmas regras no Android (SearchRules.kt); fixtures/search.json valida as duas.
enum SearchRules {
    /// Consulta (nome, lugar ou tag) E todas as tags selecionadas. Ordem original preservada.
    static func filter(_ items: [SearchItem], query: String, selected: Set<String>) -> [SearchItem] {
        let q = query.trimmingCharacters(in: .whitespaces).lowercased()
        return items.filter { item in
            let matchesQuery = q.isEmpty
                || item.name.lowercased().contains(q)
                || item.place.lowercased().contains(q)
                || item.tags.contains { $0.lowercased().contains(q) }
            return matchesQuery && selected.allSatisfy { item.tags.contains($0) }
        }
    }

    /// Tags unicas em ordem alfabetica (minusculo).
    static func allTags(_ items: [SearchItem]) -> [String] {
        Array(Set(items.flatMap(\.tags))).sorted { $0.lowercased() < $1.lowercased() }
    }
}

extension SampleData {
    static let searchItems: [SearchItem] = [
        SearchItem(id: "s1", name: "Lisboa", emoji: "🏺", place: "lisboa", number: 12, tags: ["viagem", "arte"]),
        SearchItem(id: "s2", name: "Café", emoji: "☕️", place: "cafe", number: 5, tags: ["comida"]),
        SearchItem(id: "s3", name: "Feira", emoji: "🌿", place: "feira", number: 7, tags: ["natureza", "comida"]),
        SearchItem(id: "s4", name: "Praia", emoji: "🐚", place: "praia", number: 3, tags: ["viagem", "natureza"]),
        SearchItem(id: "s5", name: "Mercado", emoji: "🍎", place: "mercado", number: 2, tags: ["comida", "viagem"]),
        SearchItem(id: "s6", name: "Ateliê", emoji: "🎨", place: "atelie", number: 9, tags: ["arte", "casa"]),
    ]
}
