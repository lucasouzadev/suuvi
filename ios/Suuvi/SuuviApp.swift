import SwiftUI
import SwiftData

@main
struct SuuviApp: App {
    var body: some Scene {
        WindowGroup {
            ShelfView()
        }
        .modelContainer(for: [SouvenirCollection.self, Souvenir.self, Asset.self])
    }
}
