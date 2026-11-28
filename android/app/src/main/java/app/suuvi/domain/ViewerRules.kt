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
    fun coinMesh(): PrismMesh {
        val n = 64; val r = 26.0
        val contour = ContourRules.trace(n, n) { x, y ->
            val dx = x + 0.5 - 32; val dy = y + 0.5 - 32
            dx * dx + dy * dy <= r * r
        }
        val pts = ContourRules.simplify(contour, 1).map { IPoint(it.x - 32, it.y - 32) }
        return ShapeRules.solid(pts, 8) ?: PrismMesh(emptyList(), emptyList())
    }
}
