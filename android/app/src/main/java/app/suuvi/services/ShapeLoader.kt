package app.suuvi.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import app.suuvi.domain.PrismMesh
import app.suuvi.domain.ShapeRules

/** Le a imagem guardada da peca e monta a malha 3D com ShapeRules. null = sem recorte (foto opaca, arquivo ausente). */
object ShapeLoader {
    private const val MAX_SIDE = 256

    fun mesh(ctx: Context, relative: String?): PrismMesh? {
        if (relative == null) return null
        val file = ImageStore.file(ctx, relative)
        if (!file.exists()) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.path, bounds)
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX_SIDE) sample *= 2
        val bmp = BitmapFactory.decodeFile(file.path, BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }) ?: return null
        val w = bmp.width; val h = bmp.height
        val px = IntArray(w * h)
        bmp.getPixels(px, 0, w, 0, 0, w, h)
        return ShapeRules.mesh(IntArray(w * h) { px[it] ushr 24 }, w, h)
    }
}
