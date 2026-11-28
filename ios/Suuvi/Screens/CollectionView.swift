import SwiftUI
import SwiftData

/// Colecao: voltar + chip, linha "de X" com @lugar/#n/status, grade 2x2 inclinada, legenda e rodape.
struct CollectionView: View {
    @Environment(\.dismiss) private var dismiss
    let id: String

    init(id: String, startProfile: Bool = false, onAdd: @escaping () -> Void = {}, onPiece: @escaping (PieceItem) -> Void = { _ in }, onEdit: @escaping (PieceItem) -> Void = { _ in }) {
        self.id = id
        self.onAdd = onAdd
        self.onPiece = onPiece
        self.onEdit = onEdit
        _showProfile = State(initialValue: startProfile)
    }
    var onAdd: () -> Void = {}
    var onPiece: (PieceItem) -> Void = { _ in }
    var onEdit: (PieceItem) -> Void = { _ in }
    @Environment(\.modelContext) private var context
    @State private var pendingDelete: PieceItem?
    @State private var asked = false
    @State private var showProfile: Bool
    @Query(sort: \SavedPiece.number) private var saved: [SavedPiece]
    private let columns = [GridItem(.flexible(), spacing: SuuviSpace.s4),
                           GridItem(.flexible(), spacing: SuuviSpace.s4)]

    var body: some View {
        if let base = SampleData.collectionDetail(id: id) {
            let extra = saved.filter { $0.folderId == id }.map { PieceItem(id: $0.id.uuidString, title: $0.title, emoji: $0.emoji, imagePath: $0.imagePath, saved: true) }
            content(CollectionDetail(folder: base.folder, status: base.status, pieces: base.pieces + extra))
        }
    }

    private func content(_ d: CollectionDetail) -> some View {
        let filled = CollectionRules.slots(count: d.pieces.count)
        return VStack(spacing: SuuviSpace.s4) {
            ZStack {
                BadgeChip(letter: ShelfRules.chipLetter(d.folder.name), emoji: d.folder.emoji)
                HStack {
                    IconButton(icon: "ic_chevron_left", label: "a11y.back") { dismiss() }
                    Spacer()
                }
            }
            .frame(height: 52)
            HStack(alignment: .center) {
                Text(String(format: NSLocalizedString("collection.from", comment: ""), d.folder.name))
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(SuuviColor.text)
                    .lineLimit(1)
                Spacer()
                Text("@\(d.folder.place)").font(.system(size: 13)).foregroundStyle(SuuviColor.textMuted)
                Text("#\(d.folder.number)").font(.system(size: 17, weight: .semibold)).foregroundStyle(SuuviColor.textMuted)
                StatusMark(status: d.status)
            }
            ScrollView {
                VStack(spacing: SuuviSpace.s5) {
                    LazyVGrid(columns: columns, spacing: SuuviSpace.s4) {
                        ForEach(0..<filled.count, id: \.self) { i in
                            if filled[i] {
                                PieceCard(piece: d.pieces[i], tilt: CollectionRules.tilt(i))
                                    .onTapGesture { onPiece(d.pieces[i]) }
                                    .contextMenu {
                                        if d.pieces[i].saved {
                                            Button("piece.edit") { onEdit(d.pieces[i]) }
                                            Button("piece.delete", role: .destructive) { pendingDelete = d.pieces[i] }
                                        }
                                    }
                            } else {
                                EmptySlot(tilt: CollectionRules.tilt(i), action: onAdd)
                            }
                        }
                    }
                    .padding(.top, SuuviSpace.s2)
                    .padding(.horizontal, SuuviSpace.s2)
                    Text("collection.caption")
                        .font(.system(size: 15))
                        .foregroundStyle(SuuviColor.textMuted)
                        .multilineTextAlignment(.center)
                }
            }
            // So para o CI (-startRoute collection5): abre rolado ate o fim para mostrar a 3a linha da grade.
            .defaultScrollAnchor(UserDefaults.standard.string(forKey: "startRoute") == "collection5" ? .bottom : .top)
            ZStack {
                Button(action: onAdd) {
                    Text("collection.add")
                        .font(.system(size: 17, weight: .semibold))
                        .foregroundStyle(SuuviColor.text)
                        .padding(.horizontal, SuuviSpace.s6)
                        .frame(minHeight: 44)
                        .overlay(Capsule().stroke(SuuviColor.border, lineWidth: 1))
                }
                HStack {
                    HStack(spacing: SuuviSpace.s1) {
                        SuuviIcon(name: "ic_timer", size: 16)
                        Text(String(format: NSLocalizedString("collection.count", comment: ""), d.pieces.count))
                            .font(.system(size: 15))
                    }
                    .foregroundStyle(SuuviColor.textMuted)
                    Spacer()
                    IconButton(icon: "ic_info", label: "a11y.info") { showProfile = true }
                }
            }
            .frame(height: 56)
        }
        .padding(.horizontal, SuuviSpace.s5)
        .background(SuuviColor.bg)
        .navigationBarBackButtonHidden(true)
        .sheet(isPresented: $showProfile) { CollectionProfileView(detail: d) }
        .confirmationDialog("delete.title", isPresented: Binding(get: { pendingDelete != nil }, set: { if !$0 { pendingDelete = nil } }),
                            titleVisibility: .visible, presenting: pendingDelete) { p in
            Button("delete.confirm", role: .destructive) { delete(p) }
        } message: { _ in Text("delete.message") }
        .onAppear(perform: debugAsk)
        .onChange(of: saved.count) { debugAsk() }
    }

    private func delete(_ p: PieceItem) {
        guard let m = saved.first(where: { $0.id.uuidString == p.id }) else { return }
        PieceStore.delete(m, among: saved, context: context)
    }

    /// So para o CI (-startRoute deleteask): abre o dialogo de apagar na primeira peca salva.
    private func debugAsk() {
        guard !asked, UserDefaults.standard.string(forKey: "startRoute") == "deleteask",
              let m = saved.filter({ $0.folderId == id }).first else { return }
        asked = true
        pendingDelete = PieceItem(id: m.id.uuidString, title: m.title, emoji: m.emoji, imagePath: m.imagePath, saved: true)
    }
}
