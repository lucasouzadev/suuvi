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

    /// Matriz (a, b, c, d, tx, ty) que leva um pixel (u, v) da imagem a tela na face frontal (z = 0 da malha), com a mesma
    /// projecao do desenho: x' = a*u + c*v + tx; y' = b*u + d*v + ty. step/ox/oy: ShapeRules.Shape; unit: ShapeRules.unit(smooth:).
    static func frontAffine(yaw: Double, pitch: Double, scale: Double, width: Double, height: Double,
                            frame f: (cx: Double, cy: Double, cz: Double, r: Double), step: Int, ox: Int, oy: Int,
                            unit: Int = ShapeRules.scale) -> [Double] {
        let ry = yaw * .pi / 180, rp = pitch * .pi / 180
        let cy = cos(ry), sy = sin(ry), cp = cos(rp), sp = sin(rp)
        let s = min(width, height) / (2 * f.r) * scale
        let k = Double(unit) / Double(step)
        let ax = Double(unit * ox), ay = Double(unit * oy)
        return [s * k * cy, s * sp * sy * k, 0, s * k * cp,
                width / 2 + s * ((-ax - f.cx) * cy - f.cz * sy),
                height / 2 + s * ((-ay - f.cy) * cp + sp * sy * (-ax - f.cx) + sp * cy * f.cz)]
    }

    /// Enquadramento: centro (meio da caixa) e raio (meia diagonal) dos vertices; serve para qualquer malha.
    static func framing(_ m: PrismMesh) -> (cx: Double, cy: Double, cz: Double, r: Double) {
        guard let f = m.vertices.first else { return (0, 0, 0, 1) }
        var lo = (x: f.x, y: f.y, z: f.z), hi = lo
        for v in m.vertices {
            lo = (min(lo.x, v.x), min(lo.y, v.y), min(lo.z, v.z))
            hi = (max(hi.x, v.x), max(hi.y, v.y), max(hi.z, v.z))
        }
        let dx = Double(hi.x - lo.x), dy = Double(hi.y - lo.y), dz = Double(hi.z - lo.z)
        return (Double(lo.x + hi.x) / 2, Double(lo.y + hi.y) / 2, Double(lo.z + hi.z) / 2,
                max(1, (dx * dx + dy * dy + dz * dz).squareRoot() / 2))
    }
}

/// Forma de reserva (moeda) para pecas sem recorte: disco 64x64 -> contorno -> prisma.
enum SampleShape {
    static func coinMesh(smooth: Bool = true) -> PrismMesh {
        let n = 64, r = 26.0
        let contour = ContourRules.trace(width: n, height: n) { x, y in
            let dx = Double(x) + 0.5 - 32, dy = Double(y) + 0.5 - 32
            return dx * dx + dy * dy <= r * r
        }
        let pts = ContourRules.simplify(contour, eps: 1).map { IPoint(x: $0.x - 32, y: $0.y - 32) }
        let mesh = smooth ? ShapeRules.solid(pts, depth: 8) : ShapeRules.lowPoly(pts, depth: 8)
        return mesh ?? PrismMesh(vertices: [], triangles: [])
    }
}
