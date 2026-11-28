import Foundation

/// Regras da Shelf. Mesmas regras no Android (ShelfRules.kt); fixtures/shelf.json valida as duas.
enum ShelfRules {
    /// Indice do "suvenir do dia"; nil se a lista estiver vazia.
    static func dailyIndex(epochDay: Int, count: Int) -> Int? {
        guard count > 0 else { return nil }
        return ((epochDay % count) + count) % count
    }

    /// Ordem das pecas salvas no sorteio do dia: por colecao (id) e depois por numero. Devolve indices da entrada.
    static func dailyOrder(_ keys: [(folder: String, number: Int)]) -> [Int] {
        keys.indices.sorted { a, b in
            keys[a].folder != keys[b].folder ? keys[a].folder < keys[b].folder : keys[a].number < keys[b].number
        }
    }

    /// Primeira letra ou digito do titulo, em maiusculo; "?" se nao houver.
    static func chipLetter(_ title: String) -> String {
        for ch in title where ch.isLetter || ch.isNumber {
            return String(ch).uppercased()
        }
        return "?"
    }

    /// Dia local desde 1970-01-01.
    static func todayEpochDay(now: Date = Date(), timeZone: TimeZone = .current) -> Int {
        let secs = now.timeIntervalSince1970 + Double(timeZone.secondsFromGMT(for: now))
        return Int(floor(secs / 86_400))
    }
}
