import SwiftUI

/// Folha de perfil da colecao (nativa): NavigationStack com fechar/compartilhar na toolbar,
/// Picker segmentado do sistema e ShareLink para compartilhar.
struct CollectionProfileView: View {
    @Environment(\.dismiss) private var dismiss
    let detail: CollectionDetail
    @State private var segment = 0

    private var dateText: String {
        Date(timeIntervalSince1970: Double(detail.folder.lastUsedEpochDay) * 86_400)
            .formatted(.dateTime.day().month(.abbreviated).year().locale(.current))
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: SuuviSpace.s4) {
                    header
                    Picker("", selection: $segment) {
                        Text("profile.pieces").tag(0)
                        Text("profile.details").tag(1)
                    }
                    .pickerStyle(.segmented)
                    content
                }
                .padding(.horizontal, SuuviSpace.s5)
                .padding(.top, SuuviSpace.s2)
            }
            .background(SuuviColor.bg)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button { dismiss() } label: { SuuviIcon(name: "ic_x", size: 20) }
                        .accessibilityLabel("a11y.close")
                }
                ToolbarItem(placement: .topBarTrailing) {
                    ShareLink(item: "\(detail.folder.name) #\(detail.folder.number)") { SuuviIcon(name: "ic_share", size: 20) }
                        .accessibilityLabel("a11y.share")
                }
            }
        }
        .presentationDetents([.medium, .large])
    }

    private var header: some View {
        VStack(spacing: SuuviSpace.s2) {
            EmojiView(glyph: detail.folder.emoji, size: 72)
            Text(detail.folder.name)
                .font(.title2.bold())
                .foregroundStyle(SuuviColor.text)
            Text("#\(detail.folder.number)")
                .font(.subheadline)
                .foregroundStyle(SuuviColor.textMuted)
            HStack(spacing: SuuviSpace.s4) {
                HStack(spacing: SuuviSpace.s1) { SuuviIcon(name: "ic_map_pin", size: 14); Text("@\(detail.folder.place)") }
                HStack(spacing: SuuviSpace.s1) { SuuviIcon(name: "ic_calendar", size: 14); Text(dateText) }
            }
            .font(.footnote)
            .foregroundStyle(SuuviColor.textMuted)
        }
        .frame(maxWidth: .infinity)
    }

    @ViewBuilder
    private var content: some View {
        if segment == 0 {
            if detail.pieces.isEmpty {
                Text("profile.empty")
                    .font(.headline)
                    .foregroundStyle(SuuviColor.textMuted)
                    .padding(.top, SuuviSpace.s6)
            }
            ForEach(detail.pieces) { piece in
                row {
                    EmojiView(glyph: piece.emoji, size: 24)
                    Text(piece.title).font(.body).foregroundStyle(SuuviColor.text)
                    Spacer()
                    SuuviIcon(name: "ic_chevron_right", size: 16)
                        .foregroundStyle(SuuviColor.textMuted)
                }
            }
        } else {
            row {
                Text("profile.format").font(.body).foregroundStyle(SuuviColor.text)
                Spacer()
                Text(LocalizedStringKey("tier.\(PieceTier.default.rawValue)"))
                    .font(.body).foregroundStyle(SuuviColor.textMuted)
            }
            row {
                Text("profile.status").font(.body).foregroundStyle(SuuviColor.text)
                Spacer()
                Text(LocalizedStringKey("a11y.status.\(detail.status.rawValue)"))
                    .font(.body).foregroundStyle(SuuviColor.textMuted)
            }
        }
    }

    private func row<Content: View>(@ViewBuilder _ content: () -> Content) -> some View {
        HStack(spacing: SuuviSpace.s3) { content() }
            .frame(minHeight: 56)
            .overlay(alignment: .bottom) { Rectangle().fill(SuuviColor.border).frame(height: 1) }
    }
}
