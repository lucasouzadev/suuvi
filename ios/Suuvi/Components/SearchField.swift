import SwiftUI

/// Campo de busca em pilula, com lupa.
struct SearchField: View {
    @Binding var text: String

    var body: some View {
        HStack(spacing: SuuviSpace.s2) {
            SuuviIcon(name: "ic_search", size: 18).foregroundStyle(SuuviColor.textMuted)
            TextField("search.field", text: $text)
                .font(.system(size: 17))
                .foregroundStyle(SuuviColor.text)
                .submitLabel(.search)
                .autocorrectionDisabled()
        }
        .padding(.horizontal, SuuviSpace.s4)
        .frame(minHeight: 44)
        .overlay(Capsule().stroke(SuuviColor.border, lineWidth: 1))
    }
}
