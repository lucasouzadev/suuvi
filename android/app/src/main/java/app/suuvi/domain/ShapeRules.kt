package app.suuvi.domain

data class Shape(val points: List<IPoint>, val depth: Int)

/** Forma 3D da peca a partir do alpha do recorte. Mesmas regras no iOS (ShapeRules.swift); fixtures/shape.json valida as duas. */
object ShapeRules {
    const val MAX_GRID = 64

    class Grid(val w: Int, val h: Int, val cells: BooleanArray)

    /** Celulas (alpha do pixel central do bloco >= limiar) numa grade de no maximo 64 de lado. */
    fun grid(alpha: IntArray, width: Int, height: Int): Grid {
        val step = maxOf(1, (maxOf(width, height) + MAX_GRID - 1) / MAX_GRID)
        val gw = (width + step - 1) / step
        val gh = (height + step - 1) / step
        val cells = BooleanArray(gw * gh)
        for (y in 0 until gh) {
            for (x in 0 until gw) {
                val sy = minOf(height - 1, y * step + step / 2)
                val sx = minOf(width - 1, x * step + step / 2)
                cells[y * gw + x] = alpha[sy * width + sx] >= CaptureRules.ALPHA_THRESHOLD
            }
        }
        return Grid(gw, gh, cells)
    }

    /** Mantem so o maior componente 8-conexo (empate: o primeiro na leitura). */
    fun largest(cells: BooleanArray, w: Int, h: Int): BooleanArray {
        val seen = BooleanArray(w * h)
        var best = IntArray(0)
        for (s in 0 until w * h) {
            if (!cells[s] || seen[s]) continue
            val comp = ArrayList<Int>()
            val stack = ArrayDeque<Int>()
            stack.addLast(s); seen[s] = true
            while (stack.isNotEmpty()) {
                val c = stack.removeLast()
                comp.add(c)
                val cx = c % w; val cy = c / w
                for (dy in -1..1) for (dx in -1..1) {
                    if (dx == 0 && dy == 0) continue
                    val nx = cx + dx; val ny = cy + dy
                    if (nx < 0 || ny < 0 || nx >= w || ny >= h) continue
                    val q = ny * w + nx
                    if (cells[q] && !seen[q]) { seen[q] = true; stack.addLast(q) }
                }
            }
            if (comp.size > best.size) best = comp.toIntArray()
        }
        val out = BooleanArray(w * h)
        for (c in best) out[c] = true
        return out
    }

    /** Contorno simplificado (centrado) e profundidade. null se nao ha recorte: vazio, ou imagem toda opaca (foto sem recorte). */
    fun shape(alpha: IntArray, width: Int, height: Int): Shape? {
        if (width <= 0 || height <= 0 || alpha.size < width * height) return null
        val g = grid(alpha, width, height)
        val n = g.cells.count { it }
        if (n == 0 || n == g.w * g.h) return null
        val keep = largest(g.cells, g.w, g.h)
        val pts = ContourRules.simplify(ContourRules.trace(g.w, g.h) { x, y -> keep[y * g.w + x] }, 1)
        if (pts.size < 3) return null
        val minX = pts.minOf { it.x }; val maxX = pts.maxOf { it.x }
        val minY = pts.minOf { it.y }; val maxY = pts.maxOf { it.y }
        val extent = maxOf(maxX - minX, maxY - minY)
        val cx = (minX + maxX) / 2; val cy = (minY + maxY) / 2
        return Shape(pts.map { IPoint(it.x - cx, it.y - cy) }, maxOf(2, (extent + 3) / 6))
    }

    fun mesh(alpha: IntArray, width: Int, height: Int): PrismMesh? {
        val s = shape(alpha, width, height) ?: return null
        return MeshRules.extrude(s.points, s.depth)
    }
}
