import SwiftUI

/// Peca na grade da colecao, levemente inclinada (angulo fixo por posicao).
struct PieceCard: View {
    let piece: PieceItem
    let tilt: Double

    var body: some View {
        VStack(spacing: SuuviSpace.s2) {
            EmojiView(glyph: piece.emoji, size: 56)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            Text(piece.title)
                .font(.system(size: 13, weight: .semibold))
                .foregroundStyle(SuuviColor.text)
                .lineLimit(1)
        }
        .padding(SuuviSpace.s3)
        .frame(maxWidth: .infinity)
        .aspectRatio(0.9, contentMode: .fit)
        .background(RoundedRectangle(cornerRadius: SuuviRadius.md).fill(SuuviColor.canvas))
        .overlay(RoundedRectangle(cornerRadius: SuuviRadius.md).stroke(SuuviColor.border, lineWidth: 1))
        .rotationEffect(.degrees(tilt))
        .accessibilityElement(children: .combine)
    }
}

/// Vaga vazia: contorno tracejado com "+" (toque = adicionar peca).
struct EmptySlot: View {
    let tilt: Double
    var action: () -> Void = {}

    var body: some View {
        Button(action: action) {
            SuuviIcon(name: "ic_plus", size: 28)
                .foregroundStyle(SuuviColor.textMuted)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .aspectRatio(0.9, contentMode: .fit)
                .overlay(
                    RoundedRectangle(cornerRadius: SuuviRadius.md)
                        .stroke(SuuviColor.border, style: StrokeStyle(lineWidth: 1.5, dash: [6, 5]))
                )
                .rotationEffect(.degrees(tilt))
        }
        .accessibilityLabel("collection.add")
    }
}
