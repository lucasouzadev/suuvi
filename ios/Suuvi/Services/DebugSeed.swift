import SwiftUI
import SwiftData

/// So para o CI (-startRoute collection5): garante 2 pecas salvas na colecao f1, para provar a grade com mais de 4 pecas.
struct DebugSeed: ViewModifier {
    @Environment(\.modelContext) private var context
    @Query private var saved: [SavedPiece]

    func body(content: Content) -> some View {
        content.onAppear {
            guard UserDefaults.standard.string(forKey: "startRoute") == "collection5" else { return }
            let mine = saved.filter { $0.folderId == "f1" }
            var next = (mine.map(\.number).max() ?? 3) + 1
            for title in ["Bonde 30", "Fado"].prefix(max(0, 2 - mine.count)) {
                context.insert(SavedPiece(folderId: "f1", number: next, title: title, place: "lisboa", tag: nil))
                next += 1
            }
            try? context.save()
        }
    }
}
