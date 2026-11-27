package app.suuvi.domain

enum class CtaState { DISABLED, LIFT, LIFTING, CREATE }

data class PixelRect(val x: Int, val y: Int, val w: Int, val h: Int)

/** Regras da Captura/Recorte. Mesmas regras no iOS (CaptureRules.swift); fixtures/capture.json valida as duas. */
object CaptureRules {
    const val MAX_PHOTOS = 4
    const val ALPHA_THRESHOLD = 16

    fun canAdd(count: Int) = count < MAX_PHOTOS

    /** Estado do botao principal: sem fotos = DISABLED; recortando = LIFTING; todas recortadas = CREATE; senao LIFT. */
    fun cta(photos: Int, lifted: Int, lifting: Boolean): CtaState = when {
        photos == 0 -> CtaState.DISABLED
        lifting -> CtaState.LIFTING
        lifted == photos -> CtaState.CREATE
        else -> CtaState.LIFT
    }

    /** Caixa dos pixels com alpha >= limiar (alpha em ordem de linhas), ou null se nao ha nenhum. */
    fun alphaBounds(alpha: IntArray, width: Int, height: Int): PixelRect? {
        var minX = width; var minY = height; var maxX = -1; var maxY = -1
        for (y in 0 until height) for (x in 0 until width) {
            if (alpha[y * width + x] >= ALPHA_THRESHOLD) {
                if (x < minX) minX = x; if (x > maxX) maxX = x
                if (y < minY) minY = y; if (y > maxY) maxY = y
            }
        }
        return if (maxX < 0) null else PixelRect(minX, minY, maxX - minX + 1, maxY - minY + 1)
    }

    /** Margem p = ceil(max(w,h) * 6 / 100), em inteiros; retangulo limitado a imagem. */
    fun paddedCrop(box: PixelRect, imageWidth: Int, imageHeight: Int): PixelRect {
        val p = (maxOf(box.w, box.h) * 6 + 99) / 100
        val x0 = maxOf(0, box.x - p); val y0 = maxOf(0, box.y - p)
        val x1 = minOf(imageWidth, box.x + box.w + p); val y1 = minOf(imageHeight, box.y + box.h + p)
        return PixelRect(x0, y0, x1 - x0, y1 - y0)
    }
}
