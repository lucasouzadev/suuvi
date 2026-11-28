package app.suuvi.ui

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.CaptureRules
import app.suuvi.domain.CollectionRules
import app.suuvi.domain.CtaState
import app.suuvi.services.ImageStore
import app.suuvi.services.ObjectLifter
import app.suuvi.ui.components.BackBar
import app.suuvi.ui.components.CapturedPhoto
import app.suuvi.ui.components.EmptySlot
import app.suuvi.ui.components.PhotoCard
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Captura: ate 4 fotos (camera ou galeria) em grade inclinada; Lift recorta o objeto; depois "Criar peca". */
@Composable
fun CaptureScreen(onBack: () -> Unit, onCreate: (String?) -> Unit, autoSample: Boolean = false) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val photos = remember { mutableStateListOf<CapturedPhoto>() }
    var lifting by remember { mutableStateOf(false) }
    var showSource by remember { mutableStateOf(false) }
    var cameraFile by remember { mutableStateOf<File?>(null) }

    suspend fun add(bytes: ByteArray) {
        if (!CaptureRules.canAdd(photos.size)) return
        val item = withContext(Dispatchers.IO) {
            runCatching {
                val f = ImageStore.savePhoto(ctx, bytes)
                CapturedPhoto(f, android.graphics.BitmapFactory.decodeFile(f.path).asImageBitmap())
            }.getOrNull()
        }
        if (item != null) photos.add(item)
    }

    suspend fun liftAll() {
        lifting = true
        for (i in photos.indices) {
            val p = photos.getOrNull(i) ?: continue
            if (p.cutout != null) continue
            val out = ImageStore.cutoutFile(ctx, p.file)
            val res = runCatching { ObjectLifter.lift(ctx, p.file, out) }
            res.exceptionOrNull()?.let { android.util.Log.w("suuvi", "lift failed: $it", it) }
            val ok = res.isSuccess
            val bmp = if (ok) withContext(Dispatchers.IO) { android.graphics.BitmapFactory.decodeFile(out.path)?.asImageBitmap() } else null
            val idx = photos.indexOfFirst { it.file == p.file }
            if (idx >= 0) photos[idx] = photos[idx].copy(cutout = bmp, failed = bmp == null)
        }
        lifting = false
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) scope.launch {
            val bytes = withContext(Dispatchers.IO) { ctx.contentResolver.openInputStream(uri)?.use { it.readBytes() } }
            if (bytes != null) add(bytes)
        }
    }
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        val f = cameraFile
        if (ok && f != null) scope.launch { add(withContext(Dispatchers.IO) { f.readBytes() }) }
    }
    val hasCamera = ctx.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    fun launchPicker() = picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    fun launchCamera() {
        val f = File(ctx.cacheDir, "camera/${UUID.randomUUID()}.jpg").apply { parentFile?.mkdirs() }
        cameraFile = f
        camera.launch(FileProvider.getUriForFile(ctx, "${ctx.packageName}.files", f))
    }

    LaunchedEffect(autoSample) {
        if (autoSample && photos.isEmpty()) {
            add(withContext(Dispatchers.IO) { ctx.assets.open("sample-object.jpg").use { it.readBytes() } })
            liftAll()
        }
    }

    val cta = CaptureRules.cta(photos.size, photos.count { it.cutout != null }, lifting)

    if (showSource) {
        AlertDialog(
            onDismissRequest = { showSource = false },
            containerColor = SuuviColor.bg,
            title = { Text(stringResource(R.string.capture_add), color = SuuviColor.text) },
            text = {
                Column {
                    if (hasCamera) TextButton({ showSource = false; launchCamera() }) {
                        Text(stringResource(R.string.capture_take), color = SuuviColor.text)
                    }
                    TextButton({ showSource = false; launchPicker() }) {
                        Text(stringResource(R.string.capture_library), color = SuuviColor.text)
                    }
                }
            },
            confirmButton = {},
        )
    }

    Column(
        Modifier.fillMaxSize().background(SuuviColor.bg).windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = SuuviSpace.s5),
        verticalArrangement = Arrangement.spacedBy(SuuviSpace.s4),
    ) {
        BackBar(stringResource(R.string.capture_title), onBack)
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(SuuviSpace.s5),
        ) {
            Column(
                Modifier.padding(top = SuuviSpace.s2, start = SuuviSpace.s2, end = SuuviSpace.s2),
                verticalArrangement = Arrangement.spacedBy(SuuviSpace.s4),
            ) {
                for (row in 0..1) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(SuuviSpace.s4)) {
                        for (col in 0..1) {
                            val i = row * 2 + col
                            Box(Modifier.weight(1f)) {
                                val p = photos.getOrNull(i)
                                if (p != null) PhotoCard(p, lifting, CollectionRules.tilt(i), onRemove = { photos.removeAt(i) })
                                else EmptySlot(CollectionRules.tilt(i), onClick = { if (hasCamera) showSource = true else launchPicker() })
                            }
                        }
                    }
                }
            }
            val anyFailed = photos.any { it.failed }
            Text(
                stringResource(if (anyFailed) R.string.capture_failed else R.string.capture_caption), Modifier.fillMaxWidth(),
                fontSize = 15.sp, color = if (anyFailed) SuuviColor.danger else SuuviColor.textMuted, textAlign = TextAlign.Center,
            )
        }
        Row(Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_timer), null, Modifier.size(16.dp), tint = SuuviColor.textMuted)
                Text(
                    " " + stringResource(R.string.capture_count, photos.size, CaptureRules.MAX_PHOTOS),
                    fontSize = 15.sp, color = SuuviColor.textMuted, maxLines = 1,
                )
            }
            val create = cta == CtaState.CREATE
            OutlinedButton(
                onClick = {
                    if (create) onCreate(
                        app.suuvi.domain.PieceRules.imagePath(
                            photos.map { if (it.cutout == null) null else "cutouts/" + it.file.nameWithoutExtension + ".png" },
                            photos.map { "photos/" + it.file.name },
                        ),
                    ) else scope.launch { liftAll() }
                },
                enabled = cta != CtaState.DISABLED && cta != CtaState.LIFTING,
                modifier = Modifier.alpha(if (cta == CtaState.DISABLED) 0.4f else 1f),
                shape = RoundedCornerShape(SuuviRadius.pill),
                border = BorderStroke(1.dp, if (create) SuuviColor.accent else SuuviColor.border),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (create) SuuviColor.accent else SuuviColor.bg,
                    contentColor = SuuviColor.onAccent,
                    disabledContainerColor = SuuviColor.bg,
                    disabledContentColor = SuuviColor.onAccent,
                ),
            ) {
                Text(
                    stringResource(
                        when (cta) {
                            CtaState.CREATE -> R.string.capture_create
                            CtaState.LIFTING -> R.string.capture_lifting
                            else -> R.string.capture_lift
                        },
                    ),
                    fontSize = 17.sp, fontWeight = FontWeight.SemiBold, maxLines = 1,
                )
            }
            Box(Modifier.weight(1f))
        }
    }
}
