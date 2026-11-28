import SwiftUI
import SwiftData

/// So para o CI: garante 2 pecas salvas na colecao f1 (rotas collection5, editpiece, deleteask, deleted).
/// Na rota "deleted" apaga a de menor numero, para provar que apagar remove do banco e que o numero nao volta.
struct DebugSeed: ViewModifier {
    @Environment(\.modelContext) private var context
    @Query private var saved: [SavedPiece]

    func body(content: Content) -> some View {
        content.onAppear {
            let route = UserDefaults.standard.string(forKey: "startRoute")
            guard ["collection5", "editpiece", "deleteask", "deleted", "shelfnote"].contains(route) else { return }
            var mine = saved.filter { $0.folderId == "f1" }
            var next = max(3, PieceRules.issued(existing: mine.map(\.number), stored: PieceStore.stored("f1")))
            for title in ["Bonde 30", "Fado"].prefix(max(0, 2 - mine.count)) {
                next += 1
                let p = SavedPiece(folderId: "f1", number: next, title: title, place: "lisboa", tag: "viagem")
                context.insert(p)
                mine.append(p)
                PieceStore.setIssued(next, folder: "f1")
            }
            if route == "shelfnote", !saved.contains(where: { $0.note != nil }) {
                next += 1
                context.insert(SavedPiece(folderId: "f1", number: next, title: "Pastel de nata", place: "lisboa", tag: "doce", note: "Provado numa manhã de sol."))
                PieceStore.setIssued(next, folder: "f1")
            }
            try? context.save()
            if route == "deleted", let first = mine.sorted(by: { $0.number < $1.number }).first {
                PieceStore.delete(first, among: mine, context: context)
            }
        }
    }
}
