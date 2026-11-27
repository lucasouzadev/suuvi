import SwiftUI

/// Cabecalho da Shelf: engrenagem, titulo central, pastas | busca.
struct HeaderBar: View {
    let title: LocalizedStringKey
    var onSettings: () -> Void = {}
    var onFolders: () -> Void = {}
    var onSearch: () -> Void = {}

    var body: some View {
        ZStack {
            Text(title)
                .font(.system(size: 17, weight: .semibold))
                .foregroundStyle(SuuviColor.text)
            HStack(spacing: 0) {
                IconButton(systemName: "gearshape", label: "a11y.settings", action: onSettings)
                Spacer()
                IconButton(systemName: "folder", label: "a11y.folders", action: onFolders)
                Rectangle().fill(SuuviColor.border).frame(width: 1, height: 16)
                IconButton(systemName: "magnifyingglass", label: "a11y.search", action: onSearch)
            }
        }
        .frame(height: 52)
    }
}
