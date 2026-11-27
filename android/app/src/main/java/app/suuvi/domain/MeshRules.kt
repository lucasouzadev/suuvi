package app.suuvi.domain

data class Vertex3(val x: Int, val y: Int, val z: Int)
data class PrismMesh(val vertices: List<Vertex3>, val triangles: List<List<Int>>)

/** Malha do suvenir (prisma extrudado do contorno). Mesmas regras no iOS (MeshRules.swift); fixtures/mesh.json valida as duas. */
object MeshRules {
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
}
