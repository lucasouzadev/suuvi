import Foundation

/// Regra do #n. Mesma regra no Android (Numbering.kt); fixtures/numbering.json valida as duas.
enum Numbering {
    /// proximo = max(maior numero existente, lastIssued) + 1
    static func next(existing: [Int], lastIssued: Int = 0) -> Int {
        max(existing.max() ?? 0, lastIssued) + 1
    }
}
