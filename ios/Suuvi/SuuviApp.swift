import SwiftUI
import SwiftData

@main
struct SuuviApp: App {
    var body: some Scene {
        WindowGroup {
            CatalogView()
        }
        .modelContainer(for: [SouvenirCollection.self, Souvenir.self, Asset.self])
    }
}
