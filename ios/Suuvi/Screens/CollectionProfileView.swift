import SwiftUI

/// Folha de perfil da colecao: fechar, emoji grande, nome, meta, alternador Pecas/Detalhes + compartilhar.
struct CollectionProfileView: View {
    @Environment(\.dismiss) private var dismiss
    let detail: CollectionDetail
    @State private var segment = 0

    private var dateText: String {
        Date(timeIntervalSince1970: Double(detail.folder.lastUsedEpochDay) * 86_400)
            .formatted(.dateTime.day().month(.abbreviated).year().locale(.current))
    }

    var body: some View {
        VStack(spacing: SuuviSpace.s4) {
            HStack { Spacer(); CloseButton { dismiss() } }
            VStack(spacing: SuuviSpace.s2) {
                EmojiView(glyph: detail.folder.emoji, size: 72)
                Text(detail.folder.name)
                    .font(.system(size: 22, weight: .bold))
                    .foregroundStyle(SuuviColor.text)
                Text("@\(detail.folder.place) · #\(detail.folder.number)")
                    .font(.system(size: 15))
                    .foregroundStyle(SuuviColor.textMuted)
                HStack(spacing: SuuviSpace.s4) {
                    HStack(spacing: SuuviSpace.s1) {
                        SuuviIcon(name: "ic_map_pin", size: 14)
                        Text("@\(detail.folder.place)")
                    }
                    HStack(spacing: SuuviSpace.s1) {
                        SuuviIcon(name: "ic_calendar", size: 14)
                        Text(dateText)
                    }
                }
                .font(.system(size: 13))
                .foregroundStyle(SuuviColor.textMuted)
            }
            HStack(spacing: SuuviSpace.s2) {
                SegmentedPill(options: ["profile.pieces", "profile.details"], selection: $segment)
                IconButton(icon: "ic_share", label: "a11y.share")
            }
            ScrollView {
                VStack(spacing: 0) {
                    if segment == 0 {
                        if detail.pieces.isEmpty {
                            Text("profile.empty")
                                .font(.system(size: 17, weight: .semibold))
                                .foregroundStyle(SuuviColor.textMuted)
                                .padding(.top, SuuviSpace.s6)
                        }
                        ForEach(detail.pieces) { piece in
                            row {
                                SuuviIcon(name: "ic_chevron_right", size: 16).foregroundStyle(SuuviColor.textMuted)
                                EmojiView(glyph: piece.emoji, size: 24)
                                Text(piece.title).font(.system(size: 17)).foregroundStyle(SuuviColor.text)
                                Spacer()
                            }
                        }
                    } else {
                        row {
                            Text("profile.format").font(.system(size: 17)).foregroundStyle(SuuviColor.text)
                            Spacer()
                            Text(LocalizedStringKey("tier.\(PieceTier.default.rawValue)")).font(.system(size: 17)).foregroundStyle(SuuviColor.textMuted)
                        }
                        row {
                            Text("profile.status").font(.system(size: 17)).foregroundStyle(SuuviColor.text)
                            Spacer()
                            Text(LocalizedStringKey("a11y.status.\(detail.status.rawValue)")).font(.system(size: 17)).foregroundStyle(SuuviColor.textMuted)
                        }
                    }
                }
            }
        }
        .padding(.horizontal, SuuviSpace.s5)
        .padding(.top, SuuviSpace.s2)
        .background(SuuviColor.bg)
        .presentationDragIndicator(.visible)
    }

    private func row<Content: View>(@ViewBuilder _ content: () -> Content) -> some View {
        HStack(spacing: SuuviSpace.s3) { content() }
            .frame(minHeight: 56)
            .overlay(alignment: .bottom) { Rectangle().fill(SuuviColor.border).frame(height: 1) }
    }
}
