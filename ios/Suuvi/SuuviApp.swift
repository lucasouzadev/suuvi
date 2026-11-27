import SwiftUI
import SwiftData

enum Route: Hashable {
    case folders
}

@main
struct SuuviApp: App {
    /// Rota inicial opcional (usada pelo CI para tirar print): -startRoute folders
    @State private var path: [Route] =
        UserDefaults.standard.string(forKey: "startRoute") == "folders" ? [.folders] : []

    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $path) {
                ShelfView(onFolders: { path.append(.folders) })
                    .toolbar(.hidden, for: .navigationBar)
                    .navigationDestination(for: Route.self) { route in
                        switch route {
                        case .folders: FoldersView().toolbar(.hidden, for: .navigationBar)
                        }
                    }
            }
        }
        .modelContainer(for: [SouvenirCollection.self, Souvenir.self, Asset.self])
    }
}
