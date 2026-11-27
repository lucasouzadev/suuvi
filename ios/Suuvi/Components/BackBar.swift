import SwiftUI

/// Cabecalho de tela interna: voltar a esquerda, titulo central.
struct BackBar: View {
    let title: LocalizedStringKey
    var onBack: () -> Void = {}

    var body: some View {
        ZStack {
            Text(title)
                .font(.system(size: 17, weight: .semibold))
                .foregroundStyle(SuuviColor.text)
            HStack {
                IconButton(systemName: "chevron.left", label: "a11y.back", action: onBack)
                Spacer()
            }
        }
        .frame(height: 52)
    }
}
