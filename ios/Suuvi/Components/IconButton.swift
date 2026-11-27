import SwiftUI

/// Botao de icone com area de toque de 44pt.
struct IconButton: View {
    let icon: String
    let label: LocalizedStringKey
    var action: () -> Void = {}

    var body: some View {
        Button(action: action) {
            SuuviIcon(name: icon)
                .foregroundStyle(SuuviColor.text)
                .frame(width: 44, height: 44)
                .contentShape(Rectangle())
        }
        .accessibilityLabel(label)
    }
}
