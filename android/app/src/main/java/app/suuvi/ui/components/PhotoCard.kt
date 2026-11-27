package app.suuvi.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.SouvenirStatus
import java.io.File

/** Foto capturada (e, depois do Lift, o recorte) na grade, inclinada como as demais. */
data class CapturedPhoto(val file: File, val photo: ImageBitmap, val cutout: ImageBitmap? = null, val failed: Boolean = false)

@Composable
fun PhotoCard(item: CapturedPhoto, lifting: Boolean, tilt: Float, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(SuuviRadius.md)
    Box(modifier.fillMaxWidth().aspectRatio(0.9f).rotate(tilt)) {
        Box(Modifier.fillMaxSize().clip(shape).border(1.dp, SuuviColor.border, shape)) {
            if (item.cutout != null) {
                Box(Modifier.fillMaxSize().background(SuuviColor.canvas).padding(SuuviSpace.s3), contentAlignment = Alignment.Center) {
                    Image(item.cutout, null, Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                }
            } else {
                Image(item.photo, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
            if (lifting && item.cutout == null) {
                CircularProgressIndicator(
                    Modifier.align(Alignment.BottomStart).padding(SuuviSpace.s3).size(24.dp),
                    color = SuuviColor.textMuted, strokeWidth = 2.dp,
                )
            } else if (item.failed) {
                StatusMark(SouvenirStatus.FAILED, Modifier.align(Alignment.BottomStart).padding(SuuviSpace.s2))
            }
        }
        Box(Modifier.align(Alignment.TopEnd)) { CloseButton(onRemove) }
    }
}
