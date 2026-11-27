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
}

/** Forma de exemplo (moeda) para o visualizador enquanto nao ha recorte persistido: disco 32x32 -> contorno -> prisma. */
object SampleShape {
    fun coinMesh(): PrismMesh {
        val n = 32; val r = 13.0
        val contour = ContourRules.trace(n, n) { x, y ->
            val dx = x + 0.5 - 16; val dy = y + 0.5 - 16
            dx * dx + dy * dy <= r * r
        }
        val pts = ContourRules.simplify(contour, 1).map { IPoint(it.x - 16, it.y - 16) }
        return MeshRules.extrude(pts, 4) ?: PrismMesh(emptyList(), emptyList())
    }
}
