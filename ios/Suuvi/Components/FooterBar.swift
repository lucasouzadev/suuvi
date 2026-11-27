import SwiftUI

/// Rodape: versao, botao Create central e icone de acao.
struct FooterBar: View {
    let version: String
    var onCreate: () -> Void = {}
    var onTray: () -> Void = {}

    var body: some View {
        ZStack {
            Button(action: onCreate) {
                Text("action.create")
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(SuuviColor.text)
                    .padding(.horizontal, SuuviSpace.s6)
                    .frame(minHeight: 44)
                    .overlay(Capsule().stroke(SuuviColor.border, lineWidth: 1))
            }
            HStack {
                HStack(spacing: SuuviSpace.s1) {
                    Image(systemName: "chevron.right").font(.system(size: 13))
                    Text(version).font(.system(size: 15))
                }
                .foregroundStyle(SuuviColor.textMuted)
                Spacer()
                IconButton(systemName: "tray", label: "a11y.inbox", action: onTray)
            }
        }
        .frame(height: 56)
    }
}
