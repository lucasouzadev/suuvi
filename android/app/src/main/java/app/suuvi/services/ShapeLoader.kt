package app.suuvi.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import app.suuvi.domain.PrismMesh
import app.suuvi.domain.ShapeRules

/** Le a imagem guardada da peca e monta a malha 3D com ShapeRules. null = sem recorte (foto opaca, arquivo ausente). */
object ShapeLoader {
    private const val MAX_SIDE = 512

    /** Malha + a propria imagem (textura da face frontal) e o mapeamento imagem -> malha (ShapeRules.Shape). */
    class Textured(val mesh: PrismMesh, val image: Bitmap, val step: Int, val ox: Int, val oy: Int)

    fun mesh(ctx: Context, relative: String?): PrismMesh? = solid(ctx, relative)?.mesh

    fun solid(ctx: Context, relative: String?): Textured? {
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
        val s = ShapeRules.shape(IntArray(w * h) { px[it] ushr 24 }, w, h) ?: return null
        val mesh = ShapeRules.solid(s.points, s.depth) ?: return null
        return Textured(mesh, bmp, s.step, s.ox, s.oy)
    }
}
