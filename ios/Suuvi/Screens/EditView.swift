import SwiftUI
import SwiftData

/// Edicao de peca: cascata a esquerda, tres campos em pilula a direita, botao que muda de rotulo ao salvar.
struct EditView: View {
    @Environment(\.dismiss) private var dismiss
    @Environment(\.modelContext) private var context
    @Query private var allSaved: [SavedPiece]
    let folderId: String
    private let autoCreate: Bool
    private let image: String?
    @State private var title: String
    @State private var place: String
    @State private var tag: String
    @State private var saved: Bool
    /// Imagem guardada da peca (aparece no topo da cascata); com "@sample" (so CI) so existe depois de salvar.
    @State private var shownImage: String?

    init(folderId: String, preset: Bool = false, autoCreate: Bool = false, image: String? = nil) {
        self.folderId = folderId
        self.image = image
        self.autoCreate = autoCreate
        _shownImage = State(initialValue: image == "@sample" ? nil : image)
        _title = State(initialValue: preset ? "Moeda de Lisboa" : "")
        _place = State(initialValue: preset ? "lisboa" : "")
        _tag = State(initialValue: preset ? "viagem" : "")
        _saved = State(initialValue: preset)
    }

    private var valid: Bool { EditRules.isValid(title: title, place: place) }
    private var detail: CollectionDetail? { SampleData.collectionDetail(id: folderId) }
    private var cascade: [CascadeItem] {
        let base = (detail?.pieces.map { CascadeItem.emoji($0.emoji) } ?? []) + [CascadeItem.emoji(detail?.folder.emoji ?? "🏺")]
        let all = shownImage.map { [CascadeItem.image($0)] + base } ?? base
        return Array(all.prefix(3))
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
                        CascadeStack(items: cascade)
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
                if saved { dismiss() } else { save() }
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
        .onAppear {
            // So para o CI: cria uma peca sozinha, para provar que salvar grava no banco.
            if autoCreate && !saved { title = image == "@sample" ? "Cha" : "Bonde 29"; place = "lisboa"; tag = "viagem"; save() }
        }
    }

    /// Valida, normaliza, emite o #n e grava. Nada acontece se a peca for invalida.
    private func save() {
        let existing = allSaved.filter { $0.folderId == folderId }.map(\.number)
        guard let p = PieceRules.create(title: title, place: place, tag: tag, existing: existing, lastIssued: existing.max() ?? 0)
        else { return }
        let img = image == "@sample" ? ImageStore.saveSample() : image   // "@sample": so o CI
        context.insert(SavedPiece(folderId: folderId, number: p.number, title: p.title, place: p.place, tag: p.tag, imagePath: img))
        shownImage = img
        try? context.save()
        tag = p.tag ?? ""
        saved = true
    }
}
