import SwiftUI
import SwiftData

enum Route: Hashable {
    case folders
    case collection(String)
    case edit(String, preset: Bool)
}

@main
struct SuuviApp: App {
    /// Rota inicial opcional (usada pelo CI para tirar print): -startRoute folders | collection | edit | editdone | search
    private static let start = UserDefaults.standard.string(forKey: "startRoute")
    @State private var path: [Route] = {
        switch SuuviApp.start {
        case "folders": return [.folders]
        case "collection": return [.folders, .collection("f1")]
        case "edit": return [.folders, .collection("f1"), .edit("f1", preset: false)]
        case "editdone": return [.folders, .collection("f1"), .edit("f1", preset: true)]
        default: return []
        }
    }()
    @State private var showSearch = SuuviApp.start == "search"

    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $path) {
                ShelfView(onFolders: { path.append(.folders) }, onSearch: { showSearch = true })
                    .toolbar(.hidden, for: .navigationBar)
                    .navigationDestination(for: Route.self) { route in
                        switch route {
                        case .folders:
                            FoldersView(onOpen: { path.append(.collection($0)) }).toolbar(.hidden, for: .navigationBar)
                        case .collection(let id):
                            CollectionView(id: id, onAdd: { path.append(.edit(id, preset: false)) })
                                .toolbar(.hidden, for: .navigationBar)
                        case .edit(let id, let preset):
                            EditView(folderId: id, preset: preset).toolbar(.hidden, for: .navigationBar)
                        }
                    }
            }
            .sheet(isPresented: $showSearch) { SearchView() }
        }
        .modelContainer(for: [SouvenirCollection.self, Souvenir.self, Asset.self])
    }
}
