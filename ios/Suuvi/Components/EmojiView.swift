import SwiftUI
import UIKit

/// Emoji padronizado (Fluent Flat, igual no Android). Sem arte no conjunto curado: cai no emoji do sistema.
struct EmojiView: View {
    let glyph: String
    var size: CGFloat = 20

    private var assetName: String? {
        let name = "emoji_" + glyph.unicodeScalars
            .filter { $0.value != 0xFE0F }
            .map { String($0.value, radix: 16) }
            .joined(separator: "_")
        return UIImage(named: name) != nil ? name : nil
    }

    var body: some View {
        if let name = assetName {
            Image(name)
                .resizable()
                .scaledToFit()
                .frame(width: size, height: size)
                .accessibilityHidden(true)
        } else {
            Text(glyph).font(.system(size: size * 0.85)).accessibilityHidden(true)
        }
    }
}
