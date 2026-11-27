package app.suuvi.domain

data class IPoint(val x: Int, val y: Int)

/** Contorno do recorte para o 3D. Mesmas regras no iOS (ContourRules.swift); fixtures/contour.json valida as duas. */
object ContourRules {
    private val DX = intArrayOf(1, 0, -1, 0)   // 0 direita, 1 baixo, 2 esquerda, 3 cima
    private val DY = intArrayOf(0, 1, 0, -1)

    /** Contorno externo (interior a direita) do componente 8-conexo do primeiro pixel (linha, depois coluna). So vertices de curva. */
    fun trace(width: Int, height: Int, filled: (Int, Int) -> Boolean): List<IPoint> {
        fun px(x: Int, y: Int) = x >= 0 && y >= 0 && x < width && y < height && filled(x, y)
        var sx = -1; var sy = -1
        search@ for (y in 0 until height) for (x in 0 until width) if (px(x, y)) { sx = x; sy = y; break@search }
        if (sx < 0) return emptyList()
        var x = sx; var y = sy; var d = 0
        val turns = ArrayList<IPoint>()
        var guardN = 4 * (width + 1) * (height + 1) + 8
        do {
            val left: Boolean; val right: Boolean
            when (d) {
                0 -> { left = px(x, y - 1); right = px(x, y) }
                1 -> { left = px(x, y); right = px(x - 1, y) }
                2 -> { left = px(x - 1, y); right = px(x - 1, y - 1) }
                else -> { left = px(x - 1, y - 1); right = px(x, y - 1) }
            }
            val nd = if (left) (d + 3) % 4 else if (right) d else (d + 1) % 4
            if (nd != d) turns.add(IPoint(x, y))
            d = nd
            x += DX[d]; y += DY[d]
            guardN--
        } while (!(x == sx && y == sy) && guardN > 0)
        // o ponto inicial (canto do pixel mais alto/esquerdo) so e visitado uma vez: chegar nele fecha o contorno
        return listOf(IPoint(sx, sy)) + turns
    }

    /** Dobro da area com sinal (shoelace). */
    fun area2(p: List<IPoint>): Int {
        var s = 0
        for (i in p.indices) {
            val a = p[i]; val b = p[(i + 1) % p.size]
            s += a.x * b.y - b.x * a.y
        }
        return s
    }

    /** Douglas-Peucker em poligono fechado; so inteiros, para dar o mesmo resultado nas duas plataformas. */
    fun simplify(pts: List<IPoint>, eps: Int): List<IPoint> {
        val n = pts.size
        if (n <= 3) return pts
        var k = 0; var best = -1
        for (i in 1 until n) {
            val d = (pts[i].x - pts[0].x) * (pts[i].x - pts[0].x) + (pts[i].y - pts[0].y) * (pts[i].y - pts[0].y)
            if (d > best) { best = d; k = i }
        }
        val keep = BooleanArray(n)
        keep[0] = true; keep[k] = true
        val e2 = eps * eps
        fun dp(idx: List<Int>) {
            if (idx.size <= 2) return
            val a = pts[idx.first()]; val b = pts[idx.last()]
            val ex = b.x - a.x; val ey = b.y - a.y
            val len2 = ex * ex + ey * ey
            var m = -1; var mi = 0
            for (j in 1 until idx.size - 1) {
                val p = pts[idx[j]]
                val v = if (len2 == 0) (p.x - a.x) * (p.x - a.x) + (p.y - a.y) * (p.y - a.y)
                else { val c = (p.x - a.x) * ey - (p.y - a.y) * ex; c * c }
                if (v > m) { m = v; mi = j }
            }
            val limit = if (len2 == 0) e2 else e2 * len2
            if (m > limit) {
                keep[idx[mi]] = true
                dp(idx.subList(0, mi + 1)); dp(idx.subList(mi, idx.size))
            }
        }
        dp((0..k).toList())
        dp((k until n).toList() + 0)
        return (0 until n).filter { keep[it] }.map { pts[it] }
    }
}
