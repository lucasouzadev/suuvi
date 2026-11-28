package app.suuvi.domain

data class Vertex3(val x: Int, val y: Int, val z: Int)
data class PrismMesh(val vertices: List<Vertex3>, val triangles: List<List<Int>>)

/** Malha do suvenir (prisma extrudado do contorno). Mesmas regras no iOS (MeshRules.swift); fixtures/mesh.json valida as duas. */
object MeshRules {
    /** Triangulos da face frontal (tampa em z = 0) de um prisma com bevel (4 aneis de n vertices; a tampa so usa o 1o anel).
     *  Vazio se a malha nao tem esse formato (ex.: prisma simples). */
    fun frontCap(m: PrismMesh): List<List<Int>> {
        if (m.vertices.size < 12 || m.vertices.size % 4 != 0) return emptyList()
        val n = m.vertices.size / 4
        val cap = m.triangles.filter { t -> t.all { it < n } }
        return if (cap.size == n - 2) cap else emptyList()
    }

    private fun triArea2(a: IPoint, b: IPoint, c: IPoint) = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x)

    private fun inside(a: IPoint, b: IPoint, c: IPoint, p: IPoint): Boolean {
        if (p == a || p == b || p == c) return false
        return triArea2(a, b, p) >= 0 && triArea2(b, c, p) >= 0 && triArea2(c, a, p) >= 0
    }

    /** Ear clipping: primeira orelha na ordem dos indices. null se o poligono nao fecha (degenerado). */
    fun earClip(p: List<IPoint>): List<List<Int>>? {
        if (p.size < 3) return null
        val idx = p.indices.toMutableList()
        val out = ArrayList<List<Int>>()
        while (idx.size > 3) {
            val n = idx.size
            var found = false
            for (i in 0 until n) {
                val ip = idx[(i + n - 1) % n]; val ic = idx[i]; val inx = idx[(i + 1) % n]
                val a = p[ip]; val b = p[ic]; val c = p[inx]
                val cr = (b.x - a.x) * (c.y - b.y) - (b.y - a.y) * (c.x - b.x)
                if (cr <= 0) continue
                if (idx.any { it != ip && it != ic && it != inx && inside(a, b, c, p[it]) }) continue
                out.add(listOf(ip, ic, inx)); idx.removeAt(i); found = true; break
            }
            if (!found) return null
        }
        out.add(listOf(idx[0], idx[1], idx[2]))
        return out
    }

    /** Prisma: frente z=0 (0..n-1), fundo z=depth (n..2n-1). Contorno com area2 > 0. */
    fun extrude(p: List<IPoint>, depth: Int): PrismMesh? {
        val ears = earClip(p) ?: return null
        val n = p.size
        val v = p.map { Vertex3(it.x, it.y, 0) } + p.map { Vertex3(it.x, it.y, depth) }
        val t = ArrayList<List<Int>>()
        ears.forEach { t.add(listOf(it[0], it[2], it[1])) }
        ears.forEach { t.add(listOf(it[0] + n, it[1] + n, it[2] + n)) }
        for (i in 0 until n) {
            val a = i; val b = (i + 1) % n
            t.add(listOf(a, b, b + n)); t.add(listOf(a, b + n, a + n))
        }
        return PrismMesh(v, t)
    }

    /** 6x o volume com sinal; para malha fechada com winding consistente = 3 * area2 * depth. */
    fun volume6(m: PrismMesh): Int = m.triangles.sumOf { t ->
        val a = m.vertices[t[0]]; val b = m.vertices[t[1]]; val c = m.vertices[t[2]]
        val cx = b.y * c.z - b.z * c.y; val cy = b.z * c.x - b.x * c.z; val cz = b.x * c.y - b.y * c.x
        a.x * cx + a.y * cy + a.z * cz
    }

    // ---- Suavizacao: Chaikin, Delaunay restrito ao poligono e bevel ----

    /** Corte de quinas (Chaikin): cada aresta vira os pontos a 1/4 e 3/4. Divisoes exatas se a entrada for multipla de 4^rounds. */
    fun chaikin(p: List<IPoint>, rounds: Int): List<IPoint> {
        var cur = p
        repeat(rounds) {
            val out = ArrayList<IPoint>()
            for (i in cur.indices) {
                val a = cur[i]; val b = cur[(i + 1) % cur.size]
                out.add(IPoint((3 * a.x + b.x) / 4, (3 * a.y + b.y) / 4))
                out.add(IPoint((a.x + 3 * b.x) / 4, (a.y + 3 * b.y) / 4))
            }
            cur = out
        }
        return cur
    }

    private fun incircle(a: IPoint, b: IPoint, c: IPoint, d: IPoint): Long {
        val adx = (a.x - d.x).toLong(); val ady = (a.y - d.y).toLong()
        val bdx = (b.x - d.x).toLong(); val bdy = (b.y - d.y).toLong()
        val cdx = (c.x - d.x).toLong(); val cdy = (c.y - d.y).toLong()
        return (adx * adx + ady * ady) * (bdx * cdy - cdx * bdy) -
            (bdx * bdx + bdy * bdy) * (adx * cdy - cdx * ady) +
            (cdx * cdx + cdy * cdy) * (adx * bdy - bdx * ady)
    }

    private fun edgeKey(a: Int, b: Int): Long = a.toLong() * 100_003L + b

    /** Troca de arestas (Delaunay restrito ao poligono): tira os triangulos finos do ear clipping. Ver fixtures/mesh.json. */
    fun delaunay(p: List<IPoint>, tris: List<List<Int>>): List<List<Int>> {
        val t = tris.map { it.toList() }.toMutableList()
        var guardN = t.size * t.size * 4 + 16
        while (guardN > 0) {
            guardN--
            val owner = HashMap<Long, Int>()
            for (ti in t.indices) for (k in 0 until 3) owner[edgeKey(t[ti][k], t[ti][(k + 1) % 3])] = ti * 3 + k
            var flipped = false
            loop@ for (ti in t.indices) {
                for (k in 0 until 3) {
                    val a = t[ti][k]; val b = t[ti][(k + 1) % 3]; val c = t[ti][(k + 2) % 3]
                    val o = owner[edgeKey(b, a)] ?: continue
                    val t2 = o / 3; val k2 = o % 3
                    val d = t[t2][(k2 + 2) % 3]
                    if (triArea2(p[c], p[a], p[d]) <= 0 || triArea2(p[c], p[d], p[b]) <= 0) continue
                    if (incircle(p[a], p[b], p[c], p[d]) > 0) {
                        t[ti] = listOf(c, a, d); t[t2] = listOf(c, d, b); flipped = true
                        break@loop
                    }
                }
            }
            if (!flipped) break
        }
        return t
    }

    /** Recuo do contorno por miter (so + - * / sqrt e piso, iguais nas duas plataformas). */
    fun inset(p: List<IPoint>, b: Int): List<IPoint> {
        val n = p.size
        return List(n) { i ->
            val pr = p[(i + n - 1) % n]; val v = p[i]; val nx = p[(i + 1) % n]
            val e1x = (v.x - pr.x).toDouble(); val e1y = (v.y - pr.y).toDouble()
            val e2x = (nx.x - v.x).toDouble(); val e2y = (nx.y - v.y).toDouble()
            val l1 = Math.sqrt(e1x * e1x + e1y * e1y); val l2 = Math.sqrt(e2x * e2x + e2y * e2y)
            if (l1 == 0.0 || l2 == 0.0) return@List v
            val n1x = -e1y / l1; val n1y = e1x / l1; val n2x = -e2y / l2; val n2y = e2x / l2
            var d = 1 + n1x * n2x + n1y * n2y
            if (d < 0.25) d = 0.25
            val mx = b.toDouble() * (n1x + n2x) / d; val my = b.toDouble() * (n1y + n2y) / d
            IPoint(Math.floor(v.x.toDouble() + mx + 0.5).toInt(), Math.floor(v.y.toDouble() + my + 0.5).toInt())
        }
    }

    /** Prisma com bevel: aneis frente (recuado, z=0), ombro (z=b), ombro (z=depth-b), fundo (recuado, z=depth). Triangulacao Delaunay. */
    fun beveled(p: List<IPoint>, depth: Int, bevel: Int): PrismMesh? {
        val ears = earClip(p) ?: return null
        val tris = delaunay(p, ears)
        val n = p.size
        var b = minOf(bevel, (depth - 1) / 2)
        var q: List<IPoint>? = null
        while (b >= 1) {
            val cand = inset(p, b)
            if (tris.all { triArea2(cand[it[0]], cand[it[1]], cand[it[2]]) > 0 || triArea2(p[it[0]], p[it[1]], p[it[2]]) <= 0 }) { q = cand; break }
            b /= 2
        }
        if (q == null) return extrude(p, depth)
        val v = q.map { Vertex3(it.x, it.y, 0) } + p.map { Vertex3(it.x, it.y, b) } +
            p.map { Vertex3(it.x, it.y, depth - b) } + q.map { Vertex3(it.x, it.y, depth) }
        val t = ArrayList<List<Int>>()
        tris.forEach { t.add(listOf(it[0], it[2], it[1])) }
        tris.forEach { t.add(listOf(it[0] + 3 * n, it[1] + 3 * n, it[2] + 3 * n)) }
        for (l in 0 until 3) for (i in 0 until n) {
            val a = l * n + i; val bb = l * n + (i + 1) % n
            t.add(listOf(a, bb, bb + n)); t.add(listOf(a, bb + n, a + n))
        }
        return PrismMesh(v, t)
    }

    /** Malha fechada: cada aresta dirigida aparece uma vez e a inversa tambem. */
    fun isClosed(m: PrismMesh): Boolean {
        val count = HashMap<Long, Int>()
        for (t in m.triangles) for (k in 0 until 3) count.merge(edgeKey(t[k], t[(k + 1) % 3]), 1, Int::plus)
        return count.all { (key, v) -> v == 1 && count[(key % 100_003L) * 100_003L + key / 100_003L] == 1 }
    }

    /** 6x o volume com sinal, em Long (malhas em ponto fixo estouram Int). */
    fun volume6Long(m: PrismMesh): Long = m.triangles.sumOf { t ->
        val a = m.vertices[t[0]]; val b = m.vertices[t[1]]; val c = m.vertices[t[2]]
        val cx = b.y.toLong() * c.z - b.z.toLong() * c.y
        val cy = b.z.toLong() * c.x - b.x.toLong() * c.z
        val cz = b.x.toLong() * c.y - b.y.toLong() * c.x
        a.x * cx + a.y * cy + a.z * cz
    }
}
