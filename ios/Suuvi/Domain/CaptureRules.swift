import Foundation

enum CtaState: String { case disabled, lift, lifting, create }

struct PixelRect: Equatable { let x: Int, y: Int, w: Int, h: Int }

/// Regras da Captura/Recorte. Mesmas regras no Android (CaptureRules.kt); fixtures/capture.json valida as duas.
enum CaptureRules {
    static let maxPhotos = 4
    static let alphaThreshold: UInt8 = 16

    static func canAdd(count: Int) -> Bool { count < maxPhotos }

    /// Estado do botao principal: sem fotos = disabled; recortando = lifting; todas recortadas = create; senao lift.
    static func cta(photos: Int, lifted: Int, lifting: Bool) -> CtaState {
        if photos == 0 { return .disabled }
        if lifting { return .lifting }
        return lifted == photos ? .create : .lift
    }

    /// Caixa dos pixels com alpha >= limiar (alpha em ordem de linhas), ou nil se nao ha nenhum.
    static func alphaBounds(alpha: [UInt8], width: Int, height: Int) -> PixelRect? {
        var minX = width, minY = height, maxX = -1, maxY = -1
        for y in 0..<height {
            for x in 0..<width where alpha[y * width + x] >= alphaThreshold {
                minX = min(minX, x); maxX = max(maxX, x)
                minY = min(minY, y); maxY = max(maxY, y)
            }
        }
        return maxX < 0 ? nil : PixelRect(x: minX, y: minY, w: maxX - minX + 1, h: maxY - minY + 1)
    }

    /// Margem p = ceil(max(w,h) * 6 / 100), em inteiros; retangulo limitado a imagem.
    static func paddedCrop(_ box: PixelRect, imageWidth: Int, imageHeight: Int) -> PixelRect {
        let p = (max(box.w, box.h) * 6 + 99) / 100
        let x0 = max(0, box.x - p), y0 = max(0, box.y - p)
        let x1 = min(imageWidth, box.x + box.w + p), y1 = min(imageHeight, box.y + box.h + p)
        return PixelRect(x: x0, y: y0, w: x1 - x0, h: y1 - y0)
    }
}
