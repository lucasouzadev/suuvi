import SwiftUI

/// Botao de icone com area de toque de 44pt.
struct IconButton: View {
    let systemName: String
    let label: LocalizedStringKey
    var action: () -> Void = {}

    var body: some View {
        Button(action: action) {
            Image(systemName: systemName)
                .font(.system(size: 18, weight: .regular))
                .foregroundStyle(SuuviColor.text)
                .frame(width: 44, height: 44)
                .contentShape(Rectangle())
        }
        .accessibilityLabel(label)
    }
}
