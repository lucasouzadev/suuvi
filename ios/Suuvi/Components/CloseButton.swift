import SwiftUI

/// Botao fechar: X em circulo cinza (visual de 32pt, toque de 44pt).
struct CloseButton: View {
    var action: () -> Void = {}

    var body: some View {
        Button(action: action) {
            SuuviIcon(name: "ic_x", size: 16)
                .foregroundStyle(SuuviColor.textMuted)
                .frame(width: 32, height: 32)
                .background(Circle().fill(SuuviColor.canvas))
                .frame(width: 44, height: 44)
                .contentShape(Rectangle())
        }
        .accessibilityLabel("a11y.close")
    }
}
