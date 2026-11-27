import SwiftUI

/// Chip de filtro: contorno; selecionado = fundo ambar.
struct TagChip: View {
    let title: String
    let selected: Bool
    var action: () -> Void = {}

    var body: some View {
        Button(action: action) {
            Text(title)
                .font(.system(size: 15, weight: .semibold))
                .lineLimit(1)
                .minimumScaleFactor(0.8)
                .foregroundStyle(selected ? SuuviColor.onAccent : SuuviColor.text)
                .frame(maxWidth: .infinity, minHeight: 40)
                .background(Capsule().fill(selected ? SuuviColor.accent : SuuviColor.bg))
                .overlay(Capsule().stroke(selected ? SuuviColor.accent : SuuviColor.border, lineWidth: 1))
        }
        .accessibilityAddTraits(selected ? .isSelected : [])
    }
}
