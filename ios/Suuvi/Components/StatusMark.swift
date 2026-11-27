import SwiftUI

/// Marca de status: check ambar = pronto, circulo = rascunho, X = falhou.
struct StatusMark: View {
    let status: SouvenirStatus

    var body: some View {
        Group {
            switch status {
            case .ready:
                Image(systemName: "checkmark").foregroundStyle(SuuviColor.accentInk)
                    .accessibilityLabel("a11y.status.ready")
            case .draft:
                Image(systemName: "circle").foregroundStyle(SuuviColor.textMuted)
                    .accessibilityLabel("a11y.status.draft")
            case .failed:
                Image(systemName: "xmark").foregroundStyle(SuuviColor.textMuted)
                    .accessibilityLabel("a11y.status.failed")
            }
        }
        .font(.system(size: 18, weight: .regular))
        .frame(width: 44, height: 44)
    }
}
