package app.suuvi.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache

/** Miniaturas das pecas: decodifica ja reduzido (lado maior ~480 px) e guarda em cache de memoria limitado. */
object Thumbs {
    private const val MAX_SIDE = 480
    private val cache = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 16).toInt().coerceAtLeast(1 shl 20)) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }

    fun get(ctx: Context, relative: String): Bitmap? {
        cache.get(relative)?.let { return it }
        val file = ImageStore.file(ctx, relative)
        if (!file.exists()) return null
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.path, bounds)
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX_SIDE) sample *= 2
        val bmp = BitmapFactory.decodeFile(file.path, BitmapFactory.Options().apply { inSampleSize = sample }) ?: return null
        cache.put(relative, bmp)
        return bmp
    }
}
