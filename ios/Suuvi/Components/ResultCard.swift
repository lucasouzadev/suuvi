import SwiftUI

/// Resultado da busca: bloco com emoji (altura varia, para o efeito mosaico) e legenda.
struct ResultCard: View {
    let item: SearchItem
    let height: CGFloat

    var body: some View {
        VStack(alignment: .leading, spacing: SuuviSpace.s2) {
            Text(item.emoji)
                .font(.system(size: 44))
                .frame(maxWidth: .infinity, minHeight: height, maxHeight: height)
                .background(RoundedRectangle(cornerRadius: SuuviRadius.md).fill(SuuviColor.canvas))
            HStack {
                Text(item.name)
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundStyle(SuuviColor.text)
                Spacer()
                Text("@\(item.place)")
                    .font(.system(size: 13))
                    .foregroundStyle(SuuviColor.textMuted)
            }
            .lineLimit(1)
        }
        .accessibilityElement(children: .combine)
    }
}
