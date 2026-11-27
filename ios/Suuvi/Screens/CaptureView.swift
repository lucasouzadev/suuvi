import SwiftUI
import PhotosUI

/// Captura: ate 4 fotos (camera ou galeria) em grade inclinada; Lift recorta o objeto; depois "Criar peca".
struct CaptureView: View {
    @Environment(\.dismiss) private var dismiss
    var autoSample = false
    var onCreate: () -> Void = {}

    @State private var photos: [CapturedPhoto] = []
    @State private var lifting = false
    @State private var showSource = false
    @State private var showLibrary = false
    @State private var showCamera = false
    @State private var pick: PhotosPickerItem?

    private let columns = [GridItem(.flexible(), spacing: SuuviSpace.s4), GridItem(.flexible(), spacing: SuuviSpace.s4)]
    private var cta: CtaState {
        CaptureRules.cta(photos: photos.count, lifted: photos.filter { $0.cutout != nil }.count, lifting: lifting)
    }

    var body: some View {
        VStack(spacing: SuuviSpace.s4) {
            BackBar(title: "capture.title", onBack: { dismiss() })
            ScrollView {
                VStack(spacing: SuuviSpace.s5) {
                    LazyVGrid(columns: columns, spacing: SuuviSpace.s4) {
                        ForEach(0..<CaptureRules.maxPhotos, id: \.self) { i in
                            if i < photos.count {
                                PhotoCard(item: photos[i], lifting: lifting, tilt: CollectionRules.tilt(i)) { remove(i) }
                            } else {
                                EmptySlot(tilt: CollectionRules.tilt(i)) { showSource = true }
                            }
                        }
                    }
                    .padding(.top, SuuviSpace.s2)
                    .padding(.horizontal, SuuviSpace.s2)
                    Text("capture.caption")
                        .font(.system(size: 15)).foregroundStyle(SuuviColor.textMuted).multilineTextAlignment(.center)
                }
            }
            footer
        }
        .padding(.horizontal, SuuviSpace.s5)
        .background(SuuviColor.bg)
        .navigationBarBackButtonHidden(true)
        .confirmationDialog("capture.add", isPresented: $showSource) {
            if CameraPicker.isAvailable { Button("capture.take") { showCamera = true } }
            Button("capture.library") { showLibrary = true }
        }
        .photosPicker(isPresented: $showLibrary, selection: $pick, matching: .images)
        .fullScreenCover(isPresented: $showCamera) { CameraPicker { add($0) }.ignoresSafeArea() }
        .onChange(of: pick) { _, item in
            guard let item else { return }
            pick = nil
            Task { if let d = try? await item.loadTransferable(type: Data.self) { add(d) } }
        }
        .task {
            guard autoSample, photos.isEmpty,
                  let u = Bundle.main.url(forResource: "sample-object", withExtension: "jpg"),
                  let d = try? Data(contentsOf: u) else { return }
            add(d)
            await liftAll()
        }
    }

    private var footer: some View {
        ZStack {
            Button(action: ctaTapped) {
                Text(ctaTitle)
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(SuuviColor.onAccent)
                    .padding(.horizontal, SuuviSpace.s6)
                    .frame(minHeight: 44)
                    .background(Capsule().fill(cta == .create ? SuuviColor.accent : SuuviColor.bg))
                    .overlay(Capsule().stroke(cta == .create ? SuuviColor.accent : SuuviColor.border, lineWidth: 1))
            }
            .disabled(cta == .disabled || cta == .lifting)
            .opacity(cta == .disabled ? 0.4 : 1)
            HStack {
                HStack(spacing: SuuviSpace.s1) {
                    SuuviIcon(name: "ic_timer", size: 16)
                    Text(String(format: NSLocalizedString("capture.count", comment: ""), photos.count, CaptureRules.maxPhotos))
                        .font(.system(size: 15))
                }
                .foregroundStyle(SuuviColor.textMuted)
                Spacer()
            }
        }
        .frame(height: 56)
    }

    private var ctaTitle: LocalizedStringKey {
        switch cta {
        case .disabled, .lift: return "capture.lift"
        case .lifting: return "capture.lifting"
        case .create: return "capture.create"
        }
    }

    private func ctaTapped() {
        if cta == .create { onCreate() } else { Task { await liftAll() } }
    }

    private func add(_ data: Data) {
        guard CaptureRules.canAdd(count: photos.count),
              let url = try? ImageStore.savePhoto(data),
              let img = UIImage(contentsOfFile: url.path) else { return }
        photos.append(CapturedPhoto(photoURL: url, photo: img))
    }

    private func remove(_ i: Int) {
        guard photos.indices.contains(i) else { return }
        photos.remove(at: i)
    }

    private func liftAll() async {
        lifting = true
        for i in photos.indices where photos[i].cutout == nil {
            let src = photos[i].photoURL, out = ImageStore.cutoutURL(for: src)
            let ok = await Task.detached(priority: .userInitiated) { (try? ObjectLifter.lift(photo: src, to: out)) != nil }.value
            guard photos.indices.contains(i), photos[i].photoURL == src else { continue }
            photos[i].failed = !ok
            if ok { photos[i].cutout = UIImage(contentsOfFile: out.path) }
        }
        lifting = false
    }
}
