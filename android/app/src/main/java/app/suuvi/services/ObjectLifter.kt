package app.suuvi.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import app.suuvi.domain.CaptureRules
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Recorte do objeto (ML Kit Subject Segmentation, beta): foto -> PNG com fundo transparente,
 * cortado na caixa do objeto com margem. Fica atras desta interface porque o ML Kit ainda e beta.
 */
object ObjectLifter {
    suspend fun lift(photo: File, out: File) {
        val input = BitmapFactory.decodeFile(photo.path) ?: error("decode failed")
        val segmenter = SubjectSegmentation.getClient(SubjectSegmenterOptions.Builder().enableForegroundBitmap().build())
        try {
            val result = suspendCancellableCoroutine { c ->
                segmenter.process(InputImage.fromBitmap(input, 0))
                    .addOnSuccessListener { c.resume(it) }
                    .addOnFailureListener { c.resumeWithException(it) }
            }
            val fg = result.foregroundBitmap ?: error("no object")
            val w = fg.width; val h = fg.height
            val px = IntArray(w * h)
            fg.getPixels(px, 0, w, 0, 0, w, h)
            val alpha = IntArray(w * h) { px[it] ushr 24 }
            val box = CaptureRules.alphaBounds(alpha, w, h) ?: error("no object")
            val r = CaptureRules.paddedCrop(box, w, h)
            val cropped = Bitmap.createBitmap(fg, r.x, r.y, r.w, r.h)
            out.outputStream().use { cropped.compress(Bitmap.CompressFormat.PNG, 100, it) }
        } finally {
            segmenter.close()
        }
    }
}
