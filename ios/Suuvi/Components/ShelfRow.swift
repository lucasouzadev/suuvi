import SwiftUI

/// Linha da lista: barra, chip letra+emoji, @lugar, letra do formato, #n e status.
struct ShelfRow: View {
    let item: ShelfItem

    var body: some View {
        HStack(spacing: SuuviSpace.s3) {
            Rectangle().fill(SuuviColor.border).frame(width: 2, height: 12)
            BadgeChip(letter: ShelfRules.chipLetter(item.name), emoji: item.emoji)
            Text("@\(item.place)")
                .font(.system(size: 17, weight: .semibold))
                .foregroundStyle(SuuviColor.text)
            Spacer(minLength: SuuviSpace.s2)
            Text(String(item.tier.rawValue.prefix(1)).uppercased())
                .font(.system(size: 13))
                .foregroundStyle(SuuviColor.textMuted)
            Text("#\(item.number)")
                .font(.system(size: 17))
                .foregroundStyle(SuuviColor.text)
            Rectangle().fill(SuuviColor.border).frame(width: 1, height: 16)
            StatusMark(status: item.status)
        }
        .frame(minHeight: 56)
        .overlay(alignment: .bottom) {
            Rectangle().fill(SuuviColor.border).frame(height: 1)
        }
    }
}
