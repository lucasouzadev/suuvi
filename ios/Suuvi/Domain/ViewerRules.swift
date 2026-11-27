import Foundation

/// Gestos do visualizador 3D. Mesmas regras no Android (ViewerRules.kt); fixtures/viewer.json valida as duas.
enum ViewerRules {
    static let maxPitch = 85.0
    static let minScale = 0.5
    static let maxScale = 4.0

    /// Arrastar: dx gira (yaw), dy inclina (pitch, limitado).
    static func drag(yaw: Double, pitch: Double, dx: Double, dy: Double) -> (yaw: Double, pitch: Double) {
        (yaw + dx * 0.5, min(maxPitch, max(-maxPitch, pitch - dy * 0.5)))
    }

    static func zoom(scale: Double, pinch: Double) -> Double { min(maxScale, max(minScale, scale * pinch)) }
}

/// Forma de exemplo (moeda) para o visualizador enquanto nao ha recorte persistido: disco 32x32 -> contorno -> prisma.
enum SampleShape {
    static func coinMesh() -> PrismMesh {
        let n = 32, r = 13.0
        let contour = ContourRules.trace(width: n, height: n) { x, y in
            let dx = Double(x) + 0.5 - 16, dy = Double(y) + 0.5 - 16
            return dx * dx + dy * dy <= r * r
        }
        let pts = ContourRules.simplify(contour, eps: 1).map { IPoint(x: $0.x - 16, y: $0.y - 16) }
        return MeshRules.extrude(pts, depth: 4) ?? PrismMesh(vertices: [], triangles: [])
    }
}
