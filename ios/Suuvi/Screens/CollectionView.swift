import SwiftUI
import SwiftData

/// Colecao: voltar + chip, linha "de X" com @lugar/#n/status, grade 2x2 inclinada, legenda e rodape.
struct CollectionView: View {
    @Environment(\.dismiss) private var dismiss
    let id: String

    init(id: String, startProfile: Bool = false, onAdd: @escaping () -> Void = {}, onPiece: @escaping (String) -> Void = { _ in }) {
        self.id = id
        self.onAdd = onAdd
        self.onPiece = onPiece
        _showProfile = State(initialValue: startProfile)
    }
    var onAdd: () -> Void = {}
    var onPiece: (String) -> Void = { _ in }
    @State private var showProfile: Bool
    @Query(sort: \SavedPiece.number) private var saved: [SavedPiece]
    private let columns = [GridItem(.flexible(), spacing: SuuviSpace.s4),
                           GridItem(.flexible(), spacing: SuuviSpace.s4)]

    var body: some View {
        if let base = SampleData.collectionDetail(id: id) {
            let extra = saved.filter { $0.folderId == id }.map { PieceItem(id: $0.id.uuidString, title: $0.title, emoji: $0.emoji, imagePath: $0.imagePath) }
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
                        ForEach(0..<CollectionRules.capacity, id: \.self) { i in
                            if filled[i] {
                                PieceCard(piece: d.pieces[i], tilt: CollectionRules.tilt(i))
                                    .onTapGesture { onPiece(d.pieces[i].title) }
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
    }
}
