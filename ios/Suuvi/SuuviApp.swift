import SwiftUI
import SwiftData

enum Route: Hashable {
    case folders
    case settings
    case capture(String, sample: Bool)
    case collection(String)
    case edit(String, preset: Bool, auto: Bool = false, image: String? = nil)
    case viewer(String, image: String? = nil)
    case editPiece(String, pieceId: String)
}

@main
struct SuuviApp: App {
    /// Rota inicial opcional (usada pelo CI para tirar print): -startRoute folders | collection | edit | editdone | created | createdphoto | collection2 | viewerphoto | collection5 | editpiece | deleteask | deleted | shelfnote | viewercutout | profile | settings | capture | capturelifted | search | viewer
    private static let start = UserDefaults.standard.string(forKey: "startRoute")
    @State private var path: [Route] = {
        switch SuuviApp.start {
        case "folders": return [.folders]
        case "settings": return [.settings]
        case "capture": return [.capture("f1", sample: false)]
        case "capturelifted": return [.capture("f1", sample: true)]
        case "collection", "profile": return [.folders, .collection("f1")]
        case "viewer": return [.folders, .collection("f1"), .viewer("Moeda de Lisboa")]
        case "viewerphoto": return [.folders, .collection("f1"), .viewer("Xícara", image: ImageStore.saveSampleCutout("sample-cutout-cup"))]
        case "viewercutout": return [.folders, .collection("f1"), .viewer("Coração", image: ImageStore.saveSampleCutout())]
        case "createdphoto": return [.folders, .collection("f2"), .edit("f2", preset: false, auto: true, image: "@sample")]
        case "collection2": return [.folders, .collection("f2")]
        case "collection5", "deleteask", "deleted": return [.folders, .collection("f1")]
        case "editpiece": return [.folders, .collection("f1"), .editPiece("f1", pieceId: "first")]
        case "created": return [.folders, .collection("f1"), .edit("f1", preset: false, auto: true)]
        case "edit": return [.folders, .collection("f1"), .edit("f1", preset: false)]
        case "editdone": return [.folders, .collection("f1"), .edit("f1", preset: true)]
        default: return []
        }
    }()
    @State private var showSearch = SuuviApp.start == "search"

    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $path) {
                ShelfView(
                    onFolders: { path.append(.folders) },
                    onSettings: { path.append(.settings) },
                    onCreate: { path.append(.capture("f1", sample: false)) },
                    onSearch: { showSearch = true },
                    onOpen: { name in if let id = SampleData.folderId(forName: name) { path.append(.collection(id)) } }
                )
                    .toolbar(.hidden, for: .navigationBar)
                    .navigationDestination(for: Route.self) { route in
                        switch route {
                        case .capture(let id, let sample):
                            CaptureView(autoSample: sample, onCreate: { path.append(.edit(id, preset: false, image: $0)) })
                                .toolbar(.hidden, for: .navigationBar)
                        case .settings:
                            SettingsView().toolbar(.hidden, for: .navigationBar)
                        case .folders:
                            FoldersView(onOpen: { path.append(.collection($0)) }).toolbar(.hidden, for: .navigationBar)
                        case .collection(let id):
                            CollectionView(id: id, startProfile: SuuviApp.start == "profile" && path.count == 2, onAdd: { path.append(.capture(id, sample: false)) }, onPiece: { path.append(.viewer($0.title, image: $0.imagePath)) }, onEdit: { path.append(.editPiece(id, pieceId: $0.id)) })
                                .toolbar(.hidden, for: .navigationBar)
                        case .viewer(let title, let image):
                            ViewerView(title: title, image: image).toolbar(.hidden, for: .navigationBar)
                        case .editPiece(let id, let pieceId):
                            EditView(folderId: id, pieceId: pieceId).toolbar(.hidden, for: .navigationBar)
                        case .edit(let id, let preset, let auto, let image):
                            EditView(folderId: id, preset: preset, autoCreate: auto, image: image).toolbar(.hidden, for: .navigationBar)
                        }
                    }
            }
            .sheet(isPresented: $showSearch) { SearchView() }
            .modifier(DebugSeed())
        }
        .modelContainer(for: [SouvenirCollection.self, Souvenir.self, Asset.self, SavedPiece.self])
    }
}
