package app.suuvi.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.SampleShape
import app.suuvi.domain.ViewerRules
import app.suuvi.services.ShapeLoader
import app.suuvi.ui.components.IconAction
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** Visualizador 3D da peca: a malha e projetada e sombreada em Canvas; arrastar gira, pinca da zoom. */
@Composable
fun ViewerScreen(title: String, image: String?, onBack: () -> Unit) {
    var yaw by remember { mutableDoubleStateOf(-30.0) }
    var pitch by remember { mutableDoubleStateOf(20.0) }
    var scale by remember { mutableDoubleStateOf(1.0) }
    val context = androidx.compose.ui.platform.LocalContext.current
    // Recorte salvo da peca; sem recorte (foto opaca ou so emoji) cai na moeda de reserva.
    val mesh = remember(image) { ShapeLoader.mesh(context, image) ?: SampleShape.coinMesh() }
    val frame = remember(mesh) { ViewerRules.framing(mesh) }
    Column(
        Modifier.fillMaxSize().background(SuuviColor.bg)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = SuuviSpace.s5),
    ) {
        Box(Modifier.fillMaxWidth().height(52.dp), contentAlignment = Alignment.Center) {
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.text)
            Box(Modifier.align(Alignment.CenterStart)) {
                IconAction(R.drawable.ic_chevron_left, stringResource(R.string.a11y_back), onBack)
            }
        }
        Canvas(
            Modifier.weight(1f).fillMaxWidth().padding(top = SuuviSpace.s4)
                .background(SuuviColor.canvas, RoundedCornerShape(SuuviRadius.md))
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val r = ViewerRules.drag(yaw, pitch, pan.x.toDouble(), pan.y.toDouble())
                        yaw = r.first; pitch = r.second
                        scale = ViewerRules.zoom(scale, zoom.toDouble())
                    }
                },
        ) {
            val ry = Math.toRadians(yaw); val rp = Math.toRadians(pitch)
            val cy = cos(ry); val sy = sin(ry); val cp = cos(rp); val sp = sin(rp)
            val pts = mesh.vertices.map { v ->
                val x = v.x - frame[0]; val y = v.y - frame[1]; val z = v.z - frame[2]
                val x1 = x * cy + z * sy; val z1 = -x * sy + z * cy
                doubleArrayOf(x1, y * cp - z1 * sp, y * sp + z1 * cp)
            }
            val s = minOf(size.width, size.height) / (2 * frame[3]) * scale
            data class Face(val tri: List<Int>, val depth: Double, val light: Double)
            val faces = ArrayList<Face>()
            for (t in mesh.triangles) {
                val a = pts[t[0]]; val b = pts[t[1]]; val c = pts[t[2]]
                var nx = (b[1] - a[1]) * (c[2] - a[2]) - (b[2] - a[2]) * (c[1] - a[1])
                var ny = (b[2] - a[2]) * (c[0] - a[0]) - (b[0] - a[0]) * (c[2] - a[2])
                var nz = (b[0] - a[0]) * (c[1] - a[1]) - (b[1] - a[1]) * (c[0] - a[0])
                val len = sqrt(nx * nx + ny * ny + nz * nz)
                if (len == 0.0) continue
                nx /= len; ny /= len; nz /= len
                val gx = (a[0] + b[0] + c[0]) / 3; val gy = (a[1] + b[1] + c[1]) / 3; val gz = (a[2] + b[2] + c[2]) / 3
                if (nx * gx + ny * gy + nz * gz < 0) { nx = -nx; ny = -ny; nz = -nz }   // normal para fora
                if (nz >= 0) continue                                                    // camera em -z
                faces.add(Face(t, (a[2] + b[2] + c[2]) / 3, 0.55 + 0.45 * maxOf(0.0, -nz)))
            }
            val cx = size.width / 2; val cyy = size.height / 2
            for (f in faces.sortedByDescending { it.depth }) {
                val path = Path()
                f.tri.forEachIndexed { i, idx ->
                    val p = Offset((cx + pts[idx][0] * s).toFloat(), (cyy + pts[idx][1] * s).toFloat())
                    if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
                }
                path.close()
                val color = androidx.compose.ui.graphics.lerp(SuuviColor.canvas, SuuviColor.accent, f.light.toFloat())   // opaco: sem fios entre faces
                drawPath(path, color)
                drawPath(path, color, style = Stroke(width = 0.5f))
            }
        }
        Text(
            stringResource(R.string.viewer_hint),
            Modifier.fillMaxWidth().padding(vertical = SuuviSpace.s4),
            fontSize = 15.sp, color = SuuviColor.textMuted, textAlign = TextAlign.Center,
        )
    }
}
