import Foundation

/// Forma 3D da peca a partir do alpha do recorte. Mesmas regras no Android (ShapeRules.kt); fixtures/shape.json valida as duas.
enum ShapeRules {
    static let maxGrid = 64

    /// points/depth: contorno centrado e profundidade. step/ox/oy: mapeamento imagem -> malha da textura
    /// (pixel = (x/16 + ox) * step e (y/16 + oy) * step, com x,y em coordenadas da malha).
    struct Shape: Equatable { let points: [IPoint]; let depth: Int; let step: Int; let ox: Int; let oy: Int }

    /// Passo da grade: lado da imagem dividido para caber em 64 celulas.
    static func step(width: Int, height: Int) -> Int { max(1, (max(width, height) + maxGrid - 1) / maxGrid) }

    /// Celulas (alpha do pixel central do bloco >= limiar) numa grade de no maximo 64 de lado.
    static func grid(alpha: [UInt8], width: Int, height: Int) -> (w: Int, h: Int, cells: [Bool]) {
        let step = step(width: width, height: height)
        let gw = (width + step - 1) / step, gh = (height + step - 1) / step
        var cells = [Bool](repeating: false, count: gw * gh)
        for y in 0..<gh {
            for x in 0..<gw {
                let sy = min(height - 1, y * step + step / 2), sx = min(width - 1, x * step + step / 2)
                cells[y * gw + x] = alpha[sy * width + sx] >= CaptureRules.alphaThreshold
            }
        }
        return (gw, gh, cells)
    }

    /// Mantem so o maior componente 8-conexo (empate: o primeiro na leitura).
    static func largest(_ cells: [Bool], w: Int, h: Int) -> [Bool] {
        var seen = [Bool](repeating: false, count: w * h)
        var best: [Int] = []
        for s in 0..<(w * h) where cells[s] && !seen[s] {
            var comp: [Int] = [], stack = [s]
            seen[s] = true
            while let c = stack.popLast() {
                comp.append(c)
                let cx = c % w, cy = c / w
                for dy in -1...1 {
                    for dx in -1...1 where dx != 0 || dy != 0 {
                        let nx = cx + dx, ny = cy + dy
                        guard nx >= 0, ny >= 0, nx < w, ny < h else { continue }
                        let q = ny * w + nx
                        if cells[q] && !seen[q] { seen[q] = true; stack.append(q) }
                    }
                }
            }
            if comp.count > best.count { best = comp }
        }
        var out = [Bool](repeating: false, count: w * h)
        for c in best { out[c] = true }
        return out
    }

    /// Contorno simplificado (centrado) e profundidade. nil se nao ha recorte: vazio, ou imagem toda opaca (foto sem recorte).
    static func shape(alpha: [UInt8], width: Int, height: Int) -> Shape? {
        guard width > 0, height > 0, alpha.count >= width * height else { return nil }
        let g = grid(alpha: alpha, width: width, height: height)
        let n = g.cells.filter { $0 }.count
        if n == 0 || n == g.w * g.h { return nil }
        let keep = largest(g.cells, w: g.w, h: g.h)
        let pts = ContourRules.simplify(ContourRules.trace(width: g.w, height: g.h) { keep[$1 * g.w + $0] }, eps: 1)
        guard pts.count >= 3 else { return nil }
        let xs = pts.map(\.x), ys = pts.map(\.y)
        let minX = xs.min()!, maxX = xs.max()!, minY = ys.min()!, maxY = ys.max()!
        let extent = max(maxX - minX, maxY - minY)
        let cx = (minX + maxX) / 2, cy = (minY + maxY) / 2
        return Shape(points: pts.map { IPoint(x: $0.x - cx, y: $0.y - cy) }, depth: max(2, (extent + 3) / 6),
                     step: step(width: width, height: height), ox: cx, oy: cy)
    }

    static let scale = 16

    /// Unidades da malha por celula da grade: 16 no solido suave (pontos x16), 1 no low-poly.
    static func unit(smooth: Bool) -> Int { smooth ? scale : 1 }

    /// Low-poly: prisma simples do contorno simplificado (sem suavizar, sem bevel).
    static func lowPoly(_ points: [IPoint], depth: Int) -> PrismMesh? { MeshRules.extrude(points, depth: depth) }

    /// Solido suave: pontos x16 -> Chaikin (2 rodadas) -> prisma com bevel de 1/4 da profundidade.
    static func solid(_ points: [IPoint], depth: Int) -> PrismMesh? {
        let p = MeshRules.chaikin(points.map { IPoint(x: $0.x * scale, y: $0.y * scale) }, rounds: 2)
        let d = depth * scale
        return MeshRules.beveled(p, depth: d, bevel: d / 4)
    }

    static func mesh(alpha: [UInt8], width: Int, height: Int) -> PrismMesh? {
        guard let s = shape(alpha: alpha, width: width, height: height) else { return nil }
        return solid(s.points, depth: s.depth)
    }
}
