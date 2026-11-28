import SwiftUI
import SwiftData

/// Tela de Pastas: grade de 2 colunas com as colecoes, da mais usada para a menos usada.
struct FoldersView: View {
    @Environment(\.dismiss) private var dismiss
    var onOpen: (String) -> Void = { _ in }
    private let columns = [GridItem(.flexible(), spacing: SuuviSpace.s3),
                           GridItem(.flexible(), spacing: SuuviSpace.s3)]
    @Query private var saved: [SavedPiece]
    private var items: [FolderItem] { FolderRules.ordered(SampleData.folders(usedBy: saved)) }

    var body: some View {
        VStack(spacing: SuuviSpace.s4) {
            BackBar(title: "folders.title", onBack: { dismiss() })
            ScrollView {
                LazyVGrid(columns: columns, spacing: SuuviSpace.s3) {
                    ForEach(items) { item in
                        Button { onOpen(item.id) } label: { FolderCard(item: item) }.buttonStyle(.plain)
                    }
                }
            }
            FooterBar(version: "0.1.0", showChevron: false)
        }
        .padding(.horizontal, SuuviSpace.s5)
        .background(SuuviColor.bg)
        .navigationBarBackButtonHidden(true)
    }
}
