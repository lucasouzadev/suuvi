import SwiftUI

/// Cascata de cards sobrepostos e inclinados (lado esquerdo da Edicao).
struct CascadeStack: View {
    let emojis: [String]
    private let step: CGFloat = 52

    var body: some View {
        ZStack(alignment: .top) {
            ForEach(Array(emojis.enumerated()), id: \.offset) { i, glyph in
                EmojiView(glyph: glyph, size: 36)
                    .padding(.top, SuuviSpace.s2)
                    .frame(width: 96, height: 108, alignment: .top)
                    .background(RoundedRectangle(cornerRadius: SuuviRadius.md).fill(SuuviColor.canvas))
                    .overlay(RoundedRectangle(cornerRadius: SuuviRadius.md).stroke(SuuviColor.border, lineWidth: 1))
                    .rotationEffect(.degrees(CollectionRules.tilt(i)))
                    .offset(y: CGFloat(i) * step)
            }
        }
        .frame(width: 104, height: 108 + step * CGFloat(max(emojis.count - 1, 0)), alignment: .top)
        .accessibilityHidden(true)
    }
}

/// Campo em pilula com circulo numerado; preenchido (salvo) vira check.
struct PillField: View {
    let number: Int
    let placeholder: LocalizedStringKey
    @Binding var text: String
    let done: Bool

    var body: some View {
        HStack(spacing: SuuviSpace.s2) {
            ZStack {
                Circle().stroke(done ? SuuviColor.accentInk : SuuviColor.border, lineWidth: 1.5)
                if done {
                    SuuviIcon(name: "ic_check", size: 14).foregroundStyle(SuuviColor.accentInk)
                } else {
                    Text("\(number)").font(.system(size: 13, weight: .semibold)).foregroundStyle(SuuviColor.textMuted)
                }
            }
            .frame(width: 26, height: 26)
            TextField(placeholder, text: $text)
                .font(.system(size: 17))
                .foregroundStyle(SuuviColor.text)
                .disabled(done)
                .autocorrectionDisabled()
        }
        .padding(.horizontal, SuuviSpace.s3)
        .frame(minHeight: 48)
        .background(Capsule().fill(done ? SuuviColor.canvas : SuuviColor.bg))
        .overlay(Capsule().stroke(done ? Color.clear : SuuviColor.border, lineWidth: 1))
    }
}
