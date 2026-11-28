import UIKit
import ImageIO

/// Le a imagem guardada da peca e monta a malha 3D com ShapeRules. nil = sem recorte (foto opaca, arquivo ausente).
enum ShapeLoader {
    static let maxSide = 512

    /// Malha + a propria imagem (textura da face frontal) e o mapeamento imagem -> malha (ShapeRules.Shape).
    struct Textured {
        let mesh: PrismMesh
        let image: CGImage
        let step: Int
        let ox: Int
        let oy: Int
    }

    static func mesh(relative: String?) -> PrismMesh? { solid(relative: relative)?.mesh }

    static func solid(relative: String?) -> Textured? {
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
        guard let s = ShapeRules.shape(alpha: alpha, width: w, height: h),
              let mesh = ShapeRules.solid(s.points, depth: s.depth) else { return nil }
        return Textured(mesh: mesh, image: cg, step: s.step, ox: s.ox, oy: s.oy)
    }
}
