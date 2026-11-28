import SwiftUI
import SwiftData

/// Tela inicial: cabecalho, card do dia, alternador Colecao/Recebidas, lista e rodape.
struct ShelfView: View {
    @State private var segment = 0
    var onFolders: () -> Void = {}
    var onSettings: () -> Void = {}
    var onCreate: () -> Void = {}
    @AppStorage("showDaily") private var showDaily = true
    var onSearch: () -> Void = {}
    var onOpen: (String) -> Void = { _ in }

    @Query private var saved: [SavedPiece]
    /// Colecao: da mais usada para a menos usada (peca nova sobe a pasta). Recebidas: ordem original.
    private var items: [ShelfItem] {
        if segment != 0 { return SampleData.received }
        return FolderRules.ordered(SampleData.folders(usedBy: saved))
            .compactMap { f in SampleData.collection.first { $0.name == f.name } }
    }
    private var daily: DailyPick? {
        guard let i = ShelfRules.dailyIndex(epochDay: ShelfRules.todayEpochDay(),
                                            count: SampleData.dailyPicks.count) else { return nil }
        return SampleData.dailyPicks[i]
    }

    var body: some View {
        VStack(spacing: SuuviSpace.s4) {
            HeaderBar(title: "shelf.title", onSettings: onSettings, onFolders: onFolders, onSearch: onSearch)
            if showDaily, let daily { DailyCard(pick: daily) }
            SegmentedPill(options: ["shelf.segment.collection", "shelf.segment.received"],
                          selection: $segment)
            ScrollView {
                LazyVStack(spacing: 0) {
                    ForEach(items) { item in
                        Button { onOpen(item.name) } label: { ShelfRow(item: item) }.buttonStyle(.plain)
                    }
                }
                VStack(spacing: SuuviSpace.s2) {
                    Text("shelf.helper.title")
                        .font(.system(size: 20, weight: .semibold))
                        .foregroundStyle(SuuviColor.text)
                    Text("shelf.helper.subtitle")
                        .font(.system(size: 20, weight: .semibold))
                        .foregroundStyle(SuuviColor.textMuted)
                }
                .multilineTextAlignment(.center)
                .padding(.top, SuuviSpace.s8)
            }
            FooterBar(version: "0.1.0", onCreate: onCreate)
        }
        .padding(.horizontal, SuuviSpace.s5)
        .background(SuuviColor.bg)
    }
}
