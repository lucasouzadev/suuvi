import Foundation

struct Vertex3: Equatable { let x: Int; let y: Int; let z: Int }
struct PrismMesh: Equatable { let vertices: [Vertex3]; let triangles: [[Int]] }

/// Malha do suvenir (prisma extrudado do contorno). Mesmas regras no Android (MeshRules.kt); fixtures/mesh.json valida as duas.
enum MeshRules {
    private static func triArea2(_ a: IPoint, _ b: IPoint, _ c: IPoint) -> Int {
        (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)
    }

    private static func inside(_ a: IPoint, _ b: IPoint, _ c: IPoint, _ p: IPoint) -> Bool {
        if p == a || p == b || p == c { return false }
        return triArea2(a, b, p) >= 0 && triArea2(b, c, p) >= 0 && triArea2(c, a, p) >= 0
    }

    /// Ear clipping: primeira orelha na ordem dos indices. nil se o poligono nao fecha (degenerado).
    static func earClip(_ p: [IPoint]) -> [[Int]]? {
        guard p.count >= 3 else { return nil }
        var idx = Array(0..<p.count)
        var out: [[Int]] = []
        while idx.count > 3 {
            let n = idx.count
            var found = false
            for i in 0..<n {
                let ip = idx[(i + n - 1) % n], ic = idx[i], inx = idx[(i + 1) % n]
                let a = p[ip], b = p[ic], c = p[inx]
                let cr = (b.x - a.x) * (c.y - b.y) - (b.y - a.y) * (c.x - b.x)
                if cr <= 0 { continue }
                if idx.contains(where: { $0 != ip && $0 != ic && $0 != inx && inside(a, b, c, p[$0]) }) { continue }
                out.append([ip, ic, inx]); idx.remove(at: i); found = true; break
            }
            if !found { return nil }
        }
        out.append([idx[0], idx[1], idx[2]])
        return out
    }

    /// Prisma: frente z=0 (0..n-1), fundo z=depth (n..2n-1). Contorno com area2 > 0.
    static func extrude(_ p: [IPoint], depth: Int) -> PrismMesh? {
        guard let ears = earClip(p) else { return nil }
        let n = p.count
        let v = p.map { Vertex3(x: $0.x, y: $0.y, z: 0) } + p.map { Vertex3(x: $0.x, y: $0.y, z: depth) }
        var t = ears.map { [$0[0], $0[2], $0[1]] } + ears.map { [$0[0] + n, $0[1] + n, $0[2] + n] }
        for i in 0..<n {
            let a = i, b = (i + 1) % n
            t.append([a, b, b + n]); t.append([a, b + n, a + n])
        }
        return PrismMesh(vertices: v, triangles: t)
    }

    /// 6x o volume com sinal; para malha fechada com winding consistente = 3 * area2 * depth.
    static func volume6(_ m: PrismMesh) -> Int {
        m.triangles.reduce(0) { s, t in
            let a = m.vertices[t[0]], b = m.vertices[t[1]], c = m.vertices[t[2]]
            let cx = b.y * c.z - b.z * c.y, cy = b.z * c.x - b.x * c.z, cz = b.x * c.y - b.y * c.x
            return s + a.x * cx + a.y * cy + a.z * cz
        }
    }
}
