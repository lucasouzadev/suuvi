import SwiftUI

/// Edicao de peca: cascata a esquerda, tres campos em pilula a direita, botao que muda de rotulo ao salvar.
struct EditView: View {
    @Environment(\.dismiss) private var dismiss
    let folderId: String
    @State private var title: String
    @State private var place: String
    @State private var tag: String
    @State private var saved: Bool

    init(folderId: String, preset: Bool = false) {
        self.folderId = folderId
        _title = State(initialValue: preset ? "Moeda de Lisboa" : "")
        _place = State(initialValue: preset ? "lisboa" : "")
        _tag = State(initialValue: preset ? "viagem" : "")
        _saved = State(initialValue: preset)
    }

    private var valid: Bool { EditRules.isValid(title: title, place: place) }
    private var detail: CollectionDetail? { SampleData.collectionDetail(id: folderId) }
    private var cascade: [String] {
        let e = (detail?.pieces.map(\.emoji) ?? []) + [detail?.folder.emoji ?? "🏺"]
        return Array(e.prefix(3))
    }

    var body: some View {
        VStack(spacing: SuuviSpace.s4) {
            ZStack {
                Text("edit.title").font(.system(size: 17, weight: .semibold)).foregroundStyle(SuuviColor.text)
                HStack {
                    IconButton(icon: "ic_chevron_left", label: "a11y.back") { dismiss() }
                    Spacer()
                    if saved {
                        SuuviIcon(name: "ic_check").foregroundStyle(SuuviColor.accentInk)
                            .frame(width: 44, height: 44).accessibilityLabel("a11y.saved")
                    }
                }
            }
            .frame(height: 52)
            ScrollView {
                VStack(spacing: SuuviSpace.s7) {
                    HStack(alignment: .top, spacing: SuuviSpace.s4) {
                        CascadeStack(emojis: cascade)
                        VStack(spacing: SuuviSpace.s3) {
                            PillField(number: 1, placeholder: "edit.field.title", text: $title, done: saved)
                            PillField(number: 2, placeholder: "edit.field.place", text: $place, done: saved)
                            PillField(number: 3, placeholder: "edit.field.tag", text: $tag, done: saved)
                        }
                    }
                    if saved {
                        VStack(spacing: SuuviSpace.s2) {
                            Text("edit.saved.title").font(.system(size: 20, weight: .semibold)).foregroundStyle(SuuviColor.text)
                            Text(String(format: NSLocalizedString("edit.saved.body", comment: ""), detail?.folder.name ?? ""))
                                .font(.system(size: 17)).foregroundStyle(SuuviColor.text)
                            Text("edit.saved.hint").font(.system(size: 15)).foregroundStyle(SuuviColor.textMuted)
                        }
                        .multilineTextAlignment(.center)
                    }
                }
                .padding(.top, SuuviSpace.s3)
            }
            Button {
                if saved { dismiss() } else if valid {
                    tag = EditRules.normalizeTag(tag)
                    saved = true
                }
            } label: {
                Text(saved ? "edit.done" : "edit.save")
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(saved || valid ? SuuviColor.text : SuuviColor.textMuted)
                    .padding(.horizontal, SuuviSpace.s6)
                    .frame(minHeight: 48)
                    .background(Capsule().fill(saved || valid ? SuuviColor.bg : SuuviColor.canvas))
                    .overlay(Capsule().stroke(SuuviColor.border, lineWidth: 1))
            }
            .disabled(!saved && !valid)
            .frame(height: 64)
        }
        .padding(.horizontal, SuuviSpace.s5)
        .background(SuuviColor.bg)
        .navigationBarBackButtonHidden(true)
    }
}
