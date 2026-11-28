import Foundation

struct Vertex3: Equatable { let x: Int; let y: Int; let z: Int }
struct PrismMesh: Equatable { let vertices: [Vertex3]; let triangles: [[Int]] }

/// Malha do suvenir (prisma extrudado do contorno). Mesmas regras no Android (MeshRules.kt); fixtures/mesh.json valida as duas.
enum MeshRules {
    /// Triangulos da face frontal (tampa em z = 0) de um prisma com bevel (4 aneis de n vertices; a tampa so usa o 1o anel).
    /// Vazio se a malha nao tem esse formato (ex.: prisma simples).
    static func frontCap(_ m: PrismMesh) -> [[Int]] {
        guard m.vertices.count >= 12, m.vertices.count % 4 == 0 else { return [] }
        let n = m.vertices.count / 4
        let cap = m.triangles.filter { $0.allSatisfy { $0 < n } }
        return cap.count == n - 2 ? cap : []
    }

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

    // MARK: - Suavizacao: Chaikin, Delaunay restrito ao poligono e bevel

    /// Corte de quinas (Chaikin): cada aresta vira os pontos a 1/4 e 3/4. Divisoes exatas se a entrada for multipla de 4^rounds.
    static func chaikin(_ p: [IPoint], rounds: Int) -> [IPoint] {
        var cur = p
        for _ in 0..<rounds {
            var out: [IPoint] = []
            for i in 0..<cur.count {
                let a = cur[i], b = cur[(i + 1) % cur.count]
                out.append(IPoint(x: (3 * a.x + b.x) / 4, y: (3 * a.y + b.y) / 4))
                out.append(IPoint(x: (a.x + 3 * b.x) / 4, y: (a.y + 3 * b.y) / 4))
            }
            cur = out
        }
        return cur
    }

    private struct EdgeKey: Hashable { let a: Int; let b: Int }

    private static func incircle(_ a: IPoint, _ b: IPoint, _ c: IPoint, _ d: IPoint) -> Int {
        let adx = a.x - d.x, ady = a.y - d.y, bdx = b.x - d.x, bdy = b.y - d.y, cdx = c.x - d.x, cdy = c.y - d.y
        return (adx * adx + ady * ady) * (bdx * cdy - cdx * bdy)
            - (bdx * bdx + bdy * bdy) * (adx * cdy - cdx * ady)
            + (cdx * cdx + cdy * cdy) * (adx * bdy - bdx * ady)
    }

    /// Troca de arestas (Delaunay restrito ao poligono): tira os triangulos finos do ear clipping. Ver fixtures/mesh.json.
    static func delaunay(_ p: [IPoint], _ tris: [[Int]]) -> [[Int]] {
        var t = tris
        var guardN = t.count * t.count * 4 + 16
        while guardN > 0 {
            guardN -= 1
            var owner: [EdgeKey: (Int, Int)] = [:]
            for ti in 0..<t.count { for k in 0..<3 { owner[EdgeKey(a: t[ti][k], b: t[ti][(k + 1) % 3])] = (ti, k) } }
            var flipped = false
            outer: for ti in 0..<t.count {
                for k in 0..<3 {
                    let a = t[ti][k], b = t[ti][(k + 1) % 3], c = t[ti][(k + 2) % 3]
                    guard let o = owner[EdgeKey(a: b, b: a)] else { continue }
                    let d = t[o.0][(o.1 + 2) % 3]
                    if triArea2(p[c], p[a], p[d]) <= 0 || triArea2(p[c], p[d], p[b]) <= 0 { continue }
                    if incircle(p[a], p[b], p[c], p[d]) > 0 {
                        t[ti] = [c, a, d]; t[o.0] = [c, d, b]; flipped = true
                        break outer
                    }
                }
            }
            if !flipped { break }
        }
        return t
    }

    /// Recuo do contorno por miter (so + - * / sqrt e piso, iguais nas duas plataformas).
    static func inset(_ p: [IPoint], by b: Int) -> [IPoint] {
        let n = p.count
        return (0..<n).map { i -> IPoint in
            let pr = p[(i + n - 1) % n], v = p[i], nx = p[(i + 1) % n]
            let e1x = Double(v.x - pr.x), e1y = Double(v.y - pr.y), e2x = Double(nx.x - v.x), e2y = Double(nx.y - v.y)
            let l1 = (e1x * e1x + e1y * e1y).squareRoot(), l2 = (e2x * e2x + e2y * e2y).squareRoot()
            if l1 == 0 || l2 == 0 { return v }
            let n1x = -e1y / l1, n1y = e1x / l1, n2x = -e2y / l2, n2y = e2x / l2
            var d = 1 + n1x * n2x + n1y * n2y
            if d < 0.25 { d = 0.25 }
            let mx = Double(b) * (n1x + n2x) / d, my = Double(b) * (n1y + n2y) / d
            return IPoint(x: Int((Double(v.x) + mx + 0.5).rounded(.down)), y: Int((Double(v.y) + my + 0.5).rounded(.down)))
        }
    }

    /// Prisma com bevel: aneis frente (recuado, z=0), ombro (z=b), ombro (z=depth-b), fundo (recuado, z=depth). Triangulacao Delaunay.
    static func beveled(_ p: [IPoint], depth: Int, bevel: Int) -> PrismMesh? {
        guard let ears = earClip(p) else { return nil }
        let tris = delaunay(p, ears)
        let n = p.count
        var b = min(bevel, (depth - 1) / 2)
        var inner: [IPoint]? = nil
        while b >= 1 {
            let cand = inset(p, by: b)
            if tris.allSatisfy({ triArea2(cand[$0[0]], cand[$0[1]], cand[$0[2]]) > 0 || triArea2(p[$0[0]], p[$0[1]], p[$0[2]]) <= 0 }) { inner = cand; break }
            b /= 2
        }
        guard let q = inner else { return extrude(p, depth: depth) }
        let v = q.map { Vertex3(x: $0.x, y: $0.y, z: 0) } + p.map { Vertex3(x: $0.x, y: $0.y, z: b) }
            + p.map { Vertex3(x: $0.x, y: $0.y, z: depth - b) } + q.map { Vertex3(x: $0.x, y: $0.y, z: depth) }
        var t = tris.map { [$0[0], $0[2], $0[1]] } + tris.map { [$0[0] + 3 * n, $0[1] + 3 * n, $0[2] + 3 * n] }
        for l in 0..<3 {
            for i in 0..<n {
                let a = l * n + i, bb = l * n + (i + 1) % n
                t.append([a, bb, bb + n]); t.append([a, bb + n, a + n])
            }
        }
        return PrismMesh(vertices: v, triangles: t)
    }

    /// Malha fechada: cada aresta dirigida aparece uma vez e a inversa tambem.
    static func isClosed(_ m: PrismMesh) -> Bool {
        var count: [EdgeKey: Int] = [:]
        for t in m.triangles { for k in 0..<3 { count[EdgeKey(a: t[k], b: t[(k + 1) % 3]), default: 0] += 1 } }
        return count.allSatisfy { $0.value == 1 && count[EdgeKey(a: $0.key.b, b: $0.key.a)] == 1 }
    }
}
