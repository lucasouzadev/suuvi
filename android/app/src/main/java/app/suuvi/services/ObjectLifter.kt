package app.suuvi.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import app.suuvi.domain.CaptureRules
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Recorte do objeto (ML Kit Subject Segmentation, beta): foto -> PNG com fundo transparente,
 * cortado na caixa do objeto com margem. Fica atras desta interface porque o ML Kit ainda e beta.
 */
object ObjectLifter {
    suspend fun lift(ctx: Context, photo: File, out: File) {
        val input = BitmapFactory.decodeFile(photo.path) ?: error("decode failed")
        val segmenter = SubjectSegmentation.getClient(SubjectSegmenterOptions.Builder().enableForegroundBitmap().build())
        try {
            ensureModule(ctx, segmenter)
            val result = processWithRetry { segmenter.process(InputImage.fromBitmap(input, 0)) }
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

    /** O modelo vem do Google Play services: pede a instalacao e espera, em vez de falhar na primeira vez. */
    private suspend fun ensureModule(ctx: Context, api: com.google.android.gms.common.api.OptionalModuleApi) {
        val client = ModuleInstall.getClient(ctx)
        if (client.areModulesAvailable(api).await().areModulesAvailable()) return
        client.installModules(ModuleInstallRequest.newBuilder().addApi(api).build()).await()
    }

    private suspend fun <T> processWithRetry(attempts: Int = 5, block: () -> Task<T>): T {
        var last: Exception? = null
        repeat(attempts) {
            try {
                return block().await()
            } catch (e: Exception) {
                last = e
                if (e.message?.contains("optional module", ignoreCase = true) != true) throw e
                delay(4_000)
            }
        }
        throw last ?: IllegalStateException("lift failed")
    }

    private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { c ->
        addOnSuccessListener { c.resume(it) }
        addOnFailureListener { c.resumeWithException(it) }
    }
}
