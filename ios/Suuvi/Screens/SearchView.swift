import SwiftUI

/// Busca + filtro (apresentada como sheet nativo): campo, chips de tags e resultados em mosaico.
struct SearchView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var query = ""
    @State private var selected: Set<String> = []

    private let items = SampleData.searchItems
    private let heights: [CGFloat] = [160, 128, 144, 120]
    private var tags: [String] { SearchRules.allTags(items) }
    private var results: [SearchItem] { SearchRules.filter(items, query: query, selected: selected) }
    private let tagColumns = Array(repeating: GridItem(.flexible(), spacing: SuuviSpace.s2), count: 3)

    var body: some View {
        VStack(spacing: SuuviSpace.s3) {
            ZStack {
                Text("search.title")
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(SuuviColor.text)
                HStack { Spacer(); CloseButton { dismiss() } }
            }
            .frame(height: 44)
            SearchField(text: $query)
            ScrollView {
                VStack(spacing: SuuviSpace.s4) {
                    LazyVGrid(columns: tagColumns, spacing: SuuviSpace.s2) {
                        ForEach(tags, id: \.self) { tag in
                            TagChip(title: tag, selected: selected.contains(tag)) {
                                if selected.contains(tag) { selected.remove(tag) } else { selected.insert(tag) }
                            }
                        }
                    }
                    if results.isEmpty {
                        Text("search.empty")
                            .font(.system(size: 17, weight: .semibold))
                            .foregroundStyle(SuuviColor.textMuted)
                            .padding(.top, SuuviSpace.s8)
                    } else {
                        HStack(alignment: .top, spacing: SuuviSpace.s3) {
                            column(0)
                            column(1)
                        }
                    }
                }
                .padding(.bottom, SuuviSpace.s5)
            }
        }
        .padding(.horizontal, SuuviSpace.s5)
        .padding(.top, SuuviSpace.s3)
        .background(SuuviColor.bg)
        .presentationDragIndicator(.visible)
        .presentationCornerRadius(32)
    }

    private func column(_ parity: Int) -> some View {
        VStack(spacing: SuuviSpace.s4) {
            ForEach(Array(results.enumerated()).filter { $0.offset % 2 == parity }, id: \.element.id) { pair in
                ResultCard(item: pair.element, height: heights[pair.offset % heights.count])
            }
        }
        .frame(maxWidth: .infinity)
    }
}
