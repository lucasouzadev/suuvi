import SwiftUI

/// Icone do suuvi (asset gerado de design/icons): traco arredondado, cor pelo foregroundStyle.
struct SuuviIcon: View {
    let name: String
    var size: CGFloat = 24

    var body: some View {
        Image(name)
            .renderingMode(.template)
            .resizable()
            .scaledToFit()
            .frame(width: size, height: size)
    }
}
