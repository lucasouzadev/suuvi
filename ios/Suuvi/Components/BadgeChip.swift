import SwiftUI

/// Chip letra + emoji das referencias (ex.: "P 📋").
struct BadgeChip: View {
    let letter: String
    let emoji: String

    var body: some View {
        HStack(spacing: SuuviSpace.s1) {
            Text(letter).font(.system(size: 15, weight: .semibold))
            Text(emoji)
        }
        .padding(.horizontal, SuuviSpace.s3)
        .padding(.vertical, SuuviSpace.s2)
        .background(SuuviColor.bg)
        .overlay(
            RoundedRectangle(cornerRadius: SuuviRadius.sm)
                .stroke(SuuviColor.border, lineWidth: 1)
        )
    }
}
