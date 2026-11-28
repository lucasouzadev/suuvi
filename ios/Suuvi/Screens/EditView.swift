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
    /// Edicao de peca ja salva (id do SavedPiece; "first" = primeira da colecao, so CI). nil = peca nova.
    private let pieceId: String?
    @State private var loaded = false
    @State private var title: String
    @State private var place: String
    @State private var tag: String
    @State private var note = ""
    @State private var saved: Bool
    /// Imagem guardada da peca (aparece no topo da cascata); com "@sample" (so CI) so existe depois de salvar.
    @State private var shownImage: String?

    init(folderId: String, preset: Bool = false, autoCreate: Bool = false, image: String? = nil, pieceId: String? = nil) {
        self.folderId = folderId
        self.pieceId = pieceId
        self.image = image
        self.autoCreate = autoCreate
        _shownImage = State(initialValue: image == "@sample" ? nil : image)
        _title = State(initialValue: preset ? "Moeda de Lisboa" : "")
        _place = State(initialValue: preset ? "lisboa" : "")
        _tag = State(initialValue: preset ? "viagem" : "")
        _saved = State(initialValue: preset)
    }

    private var editing: SavedPiece? {
        guard let pid = pieceId else { return nil }
        let mine = allSaved.filter { $0.folderId == folderId }
        return pid == "first" ? mine.sorted { $0.number < $1.number }.first : mine.first { $0.id.uuidString == pid }
    }

    /// Edicao: preenche os campos com a peca salva (uma vez; a lista pode chegar depois da tela).
    private func load() {
        guard pieceId != nil, !loaded, let p = editing else { return }
        title = p.title; place = p.place; tag = p.tag ?? ""; note = p.note ?? ""; shownImage = p.imagePath
        loaded = true
    }

    private var valid: Bool { EditRules.isValid(title: title, place: place) }
    private var detail: CollectionDetail? { SampleData.collectionDetail(id: folderId) }
    private var cascade: [String] {
        let e = (detail?.pieces.map(\.emoji) ?? []) + [detail?.folder.emoji ?? "🏺"]
        return Array(e.prefix(3))
    }

    private var fields: some View {
        VStack(spacing: SuuviSpace.s3) {
            PillField(number: 1, placeholder: "edit.field.title", text: $title, done: saved)
            PillField(number: 2, placeholder: "edit.field.place", text: $place, done: saved)
            PillField(number: 3, placeholder: "edit.field.tag", text: $tag, done: saved)
            PillField(number: 4, placeholder: "edit.field.note", text: $note, done: saved)
        }
    }

    var body: some View {
        VStack(spacing: SuuviSpace.s4) {
            ZStack {
                Text(pieceId == nil ? LocalizedStringKey("edit.title") : LocalizedStringKey("edit.title.edit")).font(.system(size: 17, weight: .semibold)).foregroundStyle(SuuviColor.text)
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
                    if let img = shownImage {
                        // Com foto: ela vem inteira na frente, campos embaixo ocupando a tela.
                        VStack(spacing: SuuviSpace.s7) {
                            HeroStack(image: img, behind: Array(cascade.prefix(2)))
                            fields
                        }
                    } else {
                        HStack(alignment: .top, spacing: SuuviSpace.s4) {
                            CascadeStack(emojis: cascade)
                            fields
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
                .padding(.top, shownImage == nil ? SuuviSpace.s3 : SuuviSpace.s7)
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
        .onChange(of: allSaved.count) { load() }
        .onChange(of: note) { if note.count > EditRules.maxNote { note = String(note.prefix(EditRules.maxNote)) } }
        .onAppear {
            load()
            // So para o CI: cria uma peca sozinha, para provar que salvar grava no banco.
            if autoCreate && !saved { title = image == "@sample" ? "Cha" : "Bonde 29"; place = "lisboa"; tag = "viagem"; note = image == "@sample" ? "Chá numa tarde de chuva." : "Bonde amarelo da linha 28."; save() }
        }
    }

    /// Valida, normaliza, emite o #n e grava. Nada acontece se a peca for invalida.
    private func save() {
        if pieceId != nil {
            guard let p = editing, let e = PieceRules.update(title: title, place: place, tag: tag, note: note) else { return }
            p.title = e.title; p.place = e.place; p.tag = e.tag; p.note = e.note
            try? context.save()
            dismiss()
            return
        }
        let existing = allSaved.filter { $0.folderId == folderId }.map(\.number)
        guard let p = PieceRules.create(title: title, place: place, tag: tag, note: note, existing: existing, lastIssued: PieceRules.issued(existing: existing, stored: PieceStore.stored(folderId)))
        else { return }
        let img = image == "@sample" ? ImageStore.saveSample() : image   // "@sample": so o CI
        context.insert(SavedPiece(folderId: folderId, number: p.number, title: p.title, place: p.place, tag: p.tag, note: p.note, imagePath: img))
        shownImage = img
        PieceStore.setIssued(p.number, folder: folderId)
        try? context.save()
        tag = p.tag ?? ""
        note = p.note ?? ""
        saved = true
    }
}
