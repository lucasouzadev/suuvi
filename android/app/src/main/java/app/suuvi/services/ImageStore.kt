package app.suuvi.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayInputStream
import java.io.File
import java.util.UUID

/** Arquivos de foto e recorte na pasta do app (filesDir/suuvi). Nada sai do aparelho. */
object ImageStore {
    const val MAX_PIXELS = 1600

    fun dir(ctx: Context, name: String): File = File(ctx.filesDir, "suuvi/$name").apply { mkdirs() }

    /** Reduz a foto (lado maior <= 1600 px, orientacao EXIF aplicada) e salva como JPEG. Devolve o arquivo. */
    fun savePhoto(ctx: Context, bytes: ByteArray): File {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        var sample = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX_PIXELS) sample *= 2
        val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })
            ?: error("decode failed")
        val degrees = when (ExifInterface(ByteArrayInputStream(bytes)).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        val scale = MAX_PIXELS.toFloat() / maxOf(decoded.width, decoded.height)
        val m = Matrix().apply { if (scale < 1f) postScale(scale, scale); if (degrees != 0f) postRotate(degrees) }
        val out: Bitmap = Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, m, true)
        val file = File(dir(ctx, "photos"), UUID.randomUUID().toString() + ".jpg")
        file.outputStream().use { out.compress(Bitmap.CompressFormat.JPEG, 85, it) }
        return file
    }

    fun cutoutFile(ctx: Context, photo: File) = File(dir(ctx, "cutouts"), photo.nameWithoutExtension + ".png")
}
