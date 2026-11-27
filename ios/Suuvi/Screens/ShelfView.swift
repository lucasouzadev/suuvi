import SwiftUI

/// Tela inicial: cabecalho, card do dia, alternador Colecao/Recebidas, lista e rodape.
struct ShelfView: View {
    @State private var segment = 0
    var onFolders: () -> Void = {}
    var onSearch: () -> Void = {}

    private var items: [ShelfItem] {
        segment == 0 ? SampleData.collection : SampleData.received
    }
    private var daily: DailyPick? {
        guard let i = ShelfRules.dailyIndex(epochDay: ShelfRules.todayEpochDay(),
                                            count: SampleData.dailyPicks.count) else { return nil }
        return SampleData.dailyPicks[i]
    }

    var body: some View {
        VStack(spacing: SuuviSpace.s4) {
            HeaderBar(title: "shelf.title", onFolders: onFolders, onSearch: onSearch)
            if let daily { DailyCard(pick: daily) }
            SegmentedPill(options: ["shelf.segment.collection", "shelf.segment.received"],
                          selection: $segment)
            ScrollView {
                LazyVStack(spacing: 0) {
                    ForEach(items) { ShelfRow(item: $0) }
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
            FooterBar(version: "0.1.0")
        }
        .padding(.horizontal, SuuviSpace.s5)
        .background(SuuviColor.bg)
    }
}
