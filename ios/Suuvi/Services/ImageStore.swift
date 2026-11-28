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

    /// Caminho absoluto de um arquivo guardado como caminho relativo (ex.: "cutouts/abc.png").
    static func url(relative: String) -> URL {
        let base = FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask)[0]
            .appendingPathComponent("suuvi", isDirectory: true)
        return base.appendingPathComponent(relative)
    }

    /// So para o CI: copia a foto de exemplo para a pasta de fotos e devolve o caminho relativo.
    static func saveSample() -> String? {
        guard let src = Bundle.main.url(forResource: "sample-object", withExtension: "jpg") else { return nil }
        let name = UUID().uuidString + ".jpg"
        guard (try? FileManager.default.copyItem(at: src, to: dir("photos").appendingPathComponent(name))) != nil else { return nil }
        return "photos/" + name
    }

    /// So para o CI: copia o recorte de exemplo (PNG com alfa) para a pasta de recortes e devolve o caminho relativo.
    static func saveSampleCutout() -> String? {
        guard let src = Bundle.main.url(forResource: "sample-cutout", withExtension: "png") else { return nil }
        let name = UUID().uuidString + ".png"
        guard (try? FileManager.default.copyItem(at: src, to: dir("cutouts").appendingPathComponent(name))) != nil else { return nil }
        return "cutouts/" + name
    }

    /// Apaga a foto e o recorte de uma peca (mesmo nome-base em photos/ e cutouts/). Nao falha se ja nao existem.
    static func deleteFiles(relative: String?) {
        guard let rel = relative else { return }
        let base = URL(fileURLWithPath: rel).deletingPathExtension().lastPathComponent
        guard !base.isEmpty else { return }
        for r in ["photos/\(base).jpg", "cutouts/\(base).png"] { try? FileManager.default.removeItem(at: url(relative: r)) }
    }

    static func cutoutURL(for photo: URL) -> URL {
        dir("cutouts").appendingPathComponent(photo.deletingPathExtension().lastPathComponent + ".png")
    }
}
