import SwiftUI

/// Alternador em pilula: trilho cinza, segmento ativo branco com borda fina.
struct SegmentedPill: View {
    let options: [LocalizedStringKey]
    @Binding var selection: Int

    var body: some View {
        HStack(spacing: 0) {
            ForEach(options.indices, id: \.self) { i in
                let selected = i == selection
                Button { selection = i } label: {
                    Text(options[i])
                        .font(.system(size: 15, weight: .semibold))
                        .foregroundStyle(SuuviColor.text)
                        .frame(maxWidth: .infinity, minHeight: 40)
                        .background(
                            RoundedRectangle(cornerRadius: SuuviRadius.sm - 2)
                                .fill(selected ? SuuviColor.bg : Color.clear)
                        )
                        .overlay(
                            RoundedRectangle(cornerRadius: SuuviRadius.sm - 2)
                                .stroke(selected ? SuuviColor.border : Color.clear, lineWidth: 1)
                        )
                }
                .accessibilityAddTraits(selected ? .isSelected : [])
            }
        }
        .padding(2)
        .background(RoundedRectangle(cornerRadius: SuuviRadius.sm).fill(SuuviColor.canvas))
    }
}
