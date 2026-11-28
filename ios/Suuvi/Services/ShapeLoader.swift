import UIKit
import ImageIO

/// Le a imagem guardada da peca e monta a malha 3D com ShapeRules. nil = sem recorte (foto opaca, arquivo ausente).
enum ShapeLoader {
    static let maxSide = 256

    static func mesh(relative: String?) -> PrismMesh? {
        guard let rel = relative,
              let src = CGImageSourceCreateWithURL(ImageStore.url(relative: rel) as CFURL, nil),
              let cg = CGImageSourceCreateThumbnailAtIndex(src, 0, [
                kCGImageSourceCreateThumbnailFromImageAlways: true,
                kCGImageSourceCreateThumbnailWithTransform: true,
                kCGImageSourceThumbnailMaxPixelSize: maxSide,
              ] as CFDictionary)
        else { return nil }
        let w = cg.width, h = cg.height
        var rgba = [UInt8](repeating: 0, count: w * h * 4)
        let drawn: Bool = rgba.withUnsafeMutableBytes { buf in
            guard let ctx = CGContext(data: buf.baseAddress, width: w, height: h, bitsPerComponent: 8, bytesPerRow: w * 4,
                                      space: CGColorSpaceCreateDeviceRGB(), bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue)
            else { return false }
            ctx.draw(cg, in: CGRect(x: 0, y: 0, width: w, height: h))
            return true
        }
        guard drawn else { return nil }
        let alpha = (0..<(w * h)).map { rgba[$0 * 4 + 3] }
        return ShapeRules.mesh(alpha: alpha, width: w, height: h)
    }
}
