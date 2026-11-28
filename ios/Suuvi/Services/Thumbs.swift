import UIKit
import ImageIO

/// Miniaturas das pecas: decodifica ja reduzido (lado maior ~480 px) e guarda em NSCache (esvazia sozinho sob pressao de memoria).
enum Thumbs {
    private static let cache = NSCache<NSString, UIImage>()
    static let maxPixel = 480

    static func image(relative: String) -> UIImage? {
        if let hit = cache.object(forKey: relative as NSString) { return hit }
        guard let src = CGImageSourceCreateWithURL(ImageStore.url(relative: relative) as CFURL, nil),
              let cg = CGImageSourceCreateThumbnailAtIndex(src, 0, [
                kCGImageSourceCreateThumbnailFromImageAlways: true,
                kCGImageSourceCreateThumbnailWithTransform: true,
                kCGImageSourceThumbnailMaxPixelSize: maxPixel,
              ] as CFDictionary)
        else { return nil }
        let ui = UIImage(cgImage: cg)
        cache.setObject(ui, forKey: relative as NSString)
        return ui
    }
}
