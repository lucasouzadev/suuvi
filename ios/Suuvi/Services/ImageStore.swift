import UIKit
import ImageIO

/// Arquivos de foto e recorte na pasta do app (Application Support/suuvi). Nada sai do aparelho.
enum ImageStore {
    static let maxPixels = 1600

    static func dir(_ name: String) -> URL {
        let base = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)[0]
            .appendingPathComponent("suuvi", isDirectory: true)
            .appendingPathComponent(name, isDirectory: true)
        try? FileManager.default.createDirectory(at: base, withIntermediateDirectories: true)
        return base
    }

    /// Reduz a foto (lado maior <= 1600 px, orientacao aplicada) e salva como JPEG. Devolve o arquivo.
    static func savePhoto(_ data: Data) throws -> URL {
        guard let src = CGImageSourceCreateWithData(data as CFData, nil),
              let cg = CGImageSourceCreateThumbnailAtIndex(src, 0, [
                kCGImageSourceCreateThumbnailFromImageAlways: true,
                kCGImageSourceCreateThumbnailWithTransform: true,
                kCGImageSourceThumbnailMaxPixelSize: maxPixels,
              ] as CFDictionary),
              let jpg = UIImage(cgImage: cg).jpegData(compressionQuality: 0.85)
        else { throw CocoaError(.fileReadCorruptFile) }
        let url = dir("photos").appendingPathComponent(UUID().uuidString + ".jpg")
        try jpg.write(to: url, options: .atomic)
        return url
    }

    static func cutoutURL(for photo: URL) -> URL {
        dir("cutouts").appendingPathComponent(photo.deletingPathExtension().lastPathComponent + ".png")
    }
}
