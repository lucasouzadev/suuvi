import Foundation

struct IPoint: Equatable { let x: Int; let y: Int }

/// Contorno do recorte para o 3D. Mesmas regras no Android (ContourRules.kt); fixtures/contour.json valida as duas.
enum ContourRules {
    private static let dx = [1, 0, -1, 0]   // 0 direita, 1 baixo, 2 esquerda, 3 cima
    private static let dy = [0, 1, 0, -1]

    /// Contorno externo (interior a direita) do componente 8-conexo do primeiro pixel (linha, depois coluna). So vertices de curva.
    static func trace(width: Int, height: Int, filled: (Int, Int) -> Bool) -> [IPoint] {
        func px(_ x: Int, _ y: Int) -> Bool { x >= 0 && y >= 0 && x < width && y < height && filled(x, y) }
        var sx = -1, sy = -1
        search: for y in 0..<height { for x in 0..<width where px(x, y) { sx = x; sy = y; break search } }
        if sx < 0 { return [] }
        var x = sx, y = sy, d = 0
        var turns: [IPoint] = []
        var guardN = 4 * (width + 1) * (height + 1) + 8
        repeat {
            let left: Bool, right: Bool
            switch d {
            case 0: left = px(x, y - 1); right = px(x, y)
            case 1: left = px(x, y); right = px(x - 1, y)
            case 2: left = px(x - 1, y); right = px(x - 1, y - 1)
            default: left = px(x - 1, y - 1); right = px(x, y - 1)
            }
            let nd = left ? (d + 3) % 4 : (right ? d : (d + 1) % 4)
            if nd != d { turns.append(IPoint(x: x, y: y)) }
            d = nd
            x += dx[d]; y += dy[d]
            guardN -= 1
        } while !(x == sx && y == sy) && guardN > 0
        // o ponto inicial (canto do pixel mais alto/esquerdo) so e visitado uma vez: chegar nele fecha o contorno
        return [IPoint(x: sx, y: sy)] + turns
    }

    /// Dobro da area com sinal (shoelace).
    static func area2(_ p: [IPoint]) -> Int {
        var s = 0
        for i in 0..<p.count {
            let a = p[i], b = p[(i + 1) % p.count]
            s += a.x * b.y - b.x * a.y
        }
        return s
    }

    /// Douglas-Peucker em poligono fechado; so inteiros, para dar o mesmo resultado nas duas plataformas.
    static func simplify(_ pts: [IPoint], eps: Int) -> [IPoint] {
        let n = pts.count
        if n <= 3 { return pts }
        var k = 0, best = -1
        for i in 1..<n {
            let d = (pts[i].x - pts[0].x) * (pts[i].x - pts[0].x) + (pts[i].y - pts[0].y) * (pts[i].y - pts[0].y)
            if d > best { best = d; k = i }
        }
        var keep = [Bool](repeating: false, count: n)
        keep[0] = true; keep[k] = true
        let e2 = eps * eps
        func dp(_ idx: [Int]) {
            guard idx.count > 2 else { return }
            let a = pts[idx[0]], b = pts[idx[idx.count - 1]]
            let ex = b.x - a.x, ey = b.y - a.y
            let len2 = ex * ex + ey * ey
            var m = -1, mi = 0
            for j in 1..<(idx.count - 1) {
                let p = pts[idx[j]]
                let v: Int
                if len2 == 0 { v = (p.x - a.x) * (p.x - a.x) + (p.y - a.y) * (p.y - a.y) }
                else { let c = (p.x - a.x) * ey - (p.y - a.y) * ex; v = c * c }
                if v > m { m = v; mi = j }
            }
            let limit = len2 == 0 ? e2 : e2 * len2
            if m > limit {
                keep[idx[mi]] = true
                dp(Array(idx[0...mi])); dp(Array(idx[mi...]))
            }
        }
        dp(Array(0...k))
        dp(Array(k..<n) + [0])
        return (0..<n).filter { keep[$0] }.map { pts[$0] }
    }
}
