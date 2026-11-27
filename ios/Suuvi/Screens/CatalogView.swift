import SwiftUI

/// Tela de catalogo interna: mostra os tokens e os componentes base.
struct CatalogView: View {
    var body: some View {
        VStack(alignment: .leading, spacing: SuuviSpace.s4) {
            Text("suuvi").font(.system(size: 28, weight: .bold)).foregroundStyle(SuuviColor.text)
            Text("Catalogo de design").font(.system(size: 14)).foregroundStyle(SuuviColor.textMuted)
            BadgeChip(letter: "C", emoji: "🪴")
            Text("#1").font(.system(size: 15)).foregroundStyle(SuuviColor.textMuted)
        }
        .padding(SuuviSpace.s5)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topLeading)
        .background(SuuviColor.bg)
    }
}
