import SwiftUI

/// Foto capturada (e, depois do Lift, o recorte) na grade, inclinada como as demais.
struct CapturedPhoto: Identifiable {
    let id = UUID()
    let photoURL: URL
    let photo: UIImage
    var cutout: UIImage?
    var failed = false
}

struct PhotoCard: View {
    let item: CapturedPhoto
    let lifting: Bool
    let tilt: Double
    var onRemove: () -> Void = {}

    var body: some View {
        ZStack(alignment: .topTrailing) {
            Group {
                if let cut = item.cutout {
                    Image(uiImage: cut).resizable().scaledToFit().padding(SuuviSpace.s3)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                        .background(SuuviColor.canvas)
                } else {
                    Color.clear.overlay(Image(uiImage: item.photo).resizable().scaledToFill())
                }
            }
            .aspectRatio(0.9, contentMode: .fit)
            .clipShape(RoundedRectangle(cornerRadius: SuuviRadius.md))
            .overlay(RoundedRectangle(cornerRadius: SuuviRadius.md).stroke(SuuviColor.border, lineWidth: 1))
            .overlay(alignment: .bottomLeading) {
                if lifting && item.cutout == nil { ProgressView().padding(SuuviSpace.s3) }
                else if item.failed { StatusMark(status: .failed).padding(SuuviSpace.s1) }
            }
            CloseButton(action: onRemove).scaleEffect(0.8)
        }
        .rotationEffect(.degrees(tilt))
        .accessibilityElement(children: .contain)
    }
}
