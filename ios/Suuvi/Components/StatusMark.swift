import SwiftUI

/// Marca de status: check ambar = pronto, circulo = rascunho, X = falhou.
struct StatusMark: View {
    let status: SouvenirStatus

    var body: some View {
        Group {
            switch status {
            case .ready:
                SuuviIcon(name: "ic_check").foregroundStyle(SuuviColor.accentInk)
                    .accessibilityLabel("a11y.status.ready")
            case .draft:
                SuuviIcon(name: "ic_circle").foregroundStyle(SuuviColor.textMuted)
                    .accessibilityLabel("a11y.status.draft")
            case .failed:
                SuuviIcon(name: "ic_x").foregroundStyle(SuuviColor.textMuted)
                    .accessibilityLabel("a11y.status.failed")
            }
        }
        .frame(width: 44, height: 44)
    }
}
