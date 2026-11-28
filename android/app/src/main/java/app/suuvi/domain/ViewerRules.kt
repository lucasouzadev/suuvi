package app.suuvi.domain

/** Gestos do visualizador 3D. Mesmas regras no iOS (ViewerRules.swift); fixtures/viewer.json valida as duas. */
object ViewerRules {
    const val MAX_PITCH = 85.0
    const val MIN_SCALE = 0.5
    const val MAX_SCALE = 4.0

    /** Arrastar: dx gira (yaw), dy inclina (pitch, limitado). Retorna (yaw, pitch). */
    fun drag(yaw: Double, pitch: Double, dx: Double, dy: Double): Pair<Double, Double> =
        Pair(yaw + dx * 0.5, minOf(MAX_PITCH, maxOf(-MAX_PITCH, pitch - dy * 0.5)))

    fun zoom(scale: Double, pinch: Double): Double = minOf(MAX_SCALE, maxOf(MIN_SCALE, scale * pinch))

    /** Matriz [a, b, c, d, tx, ty] que leva um pixel (u, v) da imagem a tela na face frontal (z = 0 da malha), com a mesma
     *  projecao do desenho: x' = a*u + c*v + tx; y' = b*u + d*v + ty. step/ox/oy: Shape; unit: ShapeRules.unit(smooth). */
    fun frontAffine(yaw: Double, pitch: Double, scale: Double, width: Double, height: Double, frame: DoubleArray, step: Int, ox: Int, oy: Int, unit: Int = ShapeRules.SCALE): DoubleArray {
        val ry = Math.toRadians(yaw); val rp = Math.toRadians(pitch)
        val cy = Math.cos(ry); val sy = Math.sin(ry); val cp = Math.cos(rp); val sp = Math.sin(rp)
        val s = minOf(width, height) / (2 * frame[3]) * scale
        val k = unit.toDouble() / step
        val ax = (unit * ox).toDouble(); val ay = (unit * oy).toDouble()
        return doubleArrayOf(
            s * k * cy, s * sp * sy * k, 0.0, s * k * cp,
            width / 2 + s * ((-ax - frame[0]) * cy - frame[2] * sy),
            height / 2 + s * ((-ay - frame[1]) * cp + sp * sy * (-ax - frame[0]) + sp * cy * frame[2]),
        )
    }

    /** Enquadramento: centro (meio da caixa) e raio (meia diagonal) dos vertices; serve para qualquer malha. Retorna (cx, cy, cz, r). */
    fun framing(m: PrismMesh): DoubleArray {
        if (m.vertices.isEmpty()) return doubleArrayOf(0.0, 0.0, 0.0, 1.0)
        val loX = m.vertices.minOf { it.x }; val hiX = m.vertices.maxOf { it.x }
        val loY = m.vertices.minOf { it.y }; val hiY = m.vertices.maxOf { it.y }
        val loZ = m.vertices.minOf { it.z }; val hiZ = m.vertices.maxOf { it.z }
        val dx = (hiX - loX).toDouble(); val dy = (hiY - loY).toDouble(); val dz = (hiZ - loZ).toDouble()
        return doubleArrayOf((loX + hiX) / 2.0, (loY + hiY) / 2.0, (loZ + hiZ) / 2.0, maxOf(1.0, Math.sqrt(dx * dx + dy * dy + dz * dz) / 2))
    }
}

/** Forma de reserva (moeda) para pecas sem recorte: disco 64x64 -> contorno -> prisma. */
object SampleShape {
    fun coinMesh(smooth: Boolean = true): PrismMesh {
        val n = 64; val r = 26.0
        val contour = ContourRules.trace(n, n) { x, y ->
            val dx = x + 0.5 - 32; val dy = y + 0.5 - 32
            dx * dx + dy * dy <= r * r
        }
        val pts = ContourRules.simplify(contour, 1).map { IPoint(it.x - 32, it.y - 32) }
        return (if (smooth) ShapeRules.solid(pts, 8) else ShapeRules.lowPoly(pts, 8)) ?: PrismMesh(emptyList(), emptyList())
    }
}
