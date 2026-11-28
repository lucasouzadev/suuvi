import Foundation
import SwiftData

/// Contador do ultimo #n emitido por colecao (nunca baixa ao apagar) e remocao de pecas com os arquivos delas.
enum PieceStore {
    private static func key(_ folderId: String) -> String { "issued.\(folderId)" }
    static func stored(_ folderId: String) -> Int { UserDefaults.standard.integer(forKey: key(folderId)) }
    static func setIssued(_ n: Int, folder: String) { UserDefaults.standard.set(n, forKey: key(folder)) }

    /// Apaga a peca e a foto/recorte dela. O numero fica gasto: o contador guarda o maior ja emitido.
    @MainActor
    static func delete(_ piece: SavedPiece, among all: [SavedPiece], context: ModelContext) {
        let existing = all.filter { $0.folderId == piece.folderId }.map(\.number)
        setIssued(PieceRules.issued(existing: existing, stored: stored(piece.folderId)), folder: piece.folderId)
        ImageStore.deleteFiles(relative: piece.imagePath)
        context.delete(piece)
        try? context.save()
    }
}
