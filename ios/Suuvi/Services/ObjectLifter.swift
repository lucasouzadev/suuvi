import UIKit
import Vision
import CoreImage

enum LiftError: Error { case noObject, render }

/// Recorte do objeto (Vision, iOS 17+): foto -> PNG com fundo transparente, cortado na caixa do objeto com margem.
enum ObjectLifter {
    static func lift(photo: URL, to out: URL) throws {
        let data = try Data(contentsOf: photo)
        guard let input = CIImage(data: data, options: [.applyOrientationProperty: true]) else { throw LiftError.render }
        let handler = VNImageRequestHandler(ciImage: input)
        let request = VNGenerateForegroundInstanceMaskRequest()
        try handler.perform([request])
        guard let obs = request.results?.first, !obs.allInstances.isEmpty else { throw LiftError.noObject }
        let buffer = try obs.generateMaskedImage(ofInstances: obs.allInstances, from: handler, croppedToInstancesExtent: false)
        let masked = CIImage(cvPixelBuffer: buffer)
        guard let cg = CIContext().createCGImage(masked, from: masked.extent) else { throw LiftError.render }

        let w = cg.width, h = cg.height
        var rgba = [UInt8](repeating: 0, count: w * h * 4)
        let drawn = rgba.withUnsafeMutableBytes { raw -> Bool in
            guard let ctx = CGContext(data: raw.baseAddress, width: w, height: h, bitsPerComponent: 8, bytesPerRow: w * 4,
                                      space: CGColorSpaceCreateDeviceRGB(),
                                      bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue) else { return false }
            ctx.draw(cg, in: CGRect(x: 0, y: 0, width: w, height: h))
            return true
        }
        guard drawn else { throw LiftError.render }
        let alpha = stride(from: 3, to: rgba.count, by: 4).map { rgba[$0] }
        guard let box = CaptureRules.alphaBounds(alpha: alpha, width: w, height: h) else { throw LiftError.noObject }
        let r = CaptureRules.paddedCrop(box, imageWidth: w, imageHeight: h)
        guard let cropped = cg.cropping(to: CGRect(x: r.x, y: r.y, width: r.w, height: r.h)),
              let png = UIImage(cgImage: cropped).pngData() else { throw LiftError.render }
        try png.write(to: out, options: .atomic)
    }
}
