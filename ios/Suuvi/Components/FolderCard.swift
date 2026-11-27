import SwiftUI

/// Card de pasta: emoji no centro, @lugar embaixo a esquerda, #n embaixo a direita.
struct FolderCard: View {
    let item: FolderItem

    var body: some View {
        ZStack {
            EmojiView(glyph: item.emoji, size: 40)
            VStack {
                Spacer()
                HStack(alignment: .firstTextBaseline) {
                    Text("@\(item.place)").font(.system(size: 13))
                    Spacer()
                    Text("#\(item.number)").font(.system(size: 17, weight: .semibold))
                }
                .foregroundStyle(SuuviColor.textMuted)
            }
            .padding(SuuviSpace.s3)
        }
        .frame(maxWidth: .infinity)
        .aspectRatio(1.25, contentMode: .fit)
        .overlay(
            RoundedRectangle(cornerRadius: SuuviRadius.md)
                .stroke(SuuviColor.border, lineWidth: 1)
        )
        .accessibilityElement(children: .combine)
    }
}
