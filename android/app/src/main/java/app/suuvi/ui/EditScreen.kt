package app.suuvi.ui

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.EditRules
import app.suuvi.domain.PieceRules
import kotlinx.coroutines.launch
import app.suuvi.domain.SampleData
import app.suuvi.domain.collectionDetail
import app.suuvi.ui.components.CascadeStack
import app.suuvi.ui.components.IconAction
import app.suuvi.ui.components.PillField

/** Edicao de peca: cascata a esquerda, tres campos em pilula a direita, botao que muda de rotulo ao salvar. */
@Composable
fun EditScreen(folderId: String, preset: Boolean = false, autoCreate: Boolean = false, image: String? = null, onBack: () -> Unit) {
    var title by rememberSaveable { mutableStateOf(if (preset) "Moeda de Lisboa" else "") }
    var place by rememberSaveable { mutableStateOf(if (preset) "lisboa" else "") }
    var tag by rememberSaveable { mutableStateOf(if (preset) "viagem" else "") }
    var saved by rememberSaveable { mutableStateOf(preset) }
    val detail = SampleData.collectionDetail(folderId)
    val cascade = ((detail?.pieces?.map { it.emoji } ?: emptyList()) + (detail?.folder?.emoji ?: "🏺")).take(3)
    val valid = EditRules.isValid(title, place)
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val dao = androidx.compose.runtime.remember { app.suuvi.data.SuuviDb.get(context).dao() }

    /** Valida, normaliza, emite o #n e grava. Nada acontece se a peca for invalida. */
    fun save() {
        scope.launch {
            val existing = dao.savedNumbers(folderId)
            val p = PieceRules.create(title, place, tag, existing, existing.maxOrNull() ?: 0) ?: return@launch
            dao.insertSavedPiece(
                app.suuvi.data.SavedPiece(
                    java.util.UUID.randomUUID().toString(), folderId, p.number, p.title, p.place, p.tag, "🪙",
                    if (image == "@sample") app.suuvi.services.ImageStore.saveSample(context) else image,   // "@sample": so o CI
                    System.currentTimeMillis(),
                ),
            )
            tag = p.tag ?: ""
            saved = true
        }
    }
    // So para o CI: cria uma peca sozinha, para provar que salvar grava no banco.
    androidx.compose.runtime.LaunchedEffect(autoCreate) {
        if (autoCreate && !saved) { title = if (image == "@sample") "Cha" else "Bonde 29"; place = "lisboa"; tag = "viagem"; save() }
    }
    val enabled = saved || valid

    Column(
        Modifier.fillMaxSize().background(SuuviColor.bg)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = SuuviSpace.s5),
        verticalArrangement = Arrangement.spacedBy(SuuviSpace.s4),
    ) {
        Box(Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.edit_title), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.text)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconAction(R.drawable.ic_chevron_left, stringResource(R.string.a11y_back), onBack)
                androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                if (saved) {
                    Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                        Icon(painterResource(R.drawable.ic_check), stringResource(R.string.a11y_saved), tint = SuuviColor.accentInk)
                    }
                }
            }
        }
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(top = SuuviSpace.s3),
            verticalArrangement = Arrangement.spacedBy(SuuviSpace.s7),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(SuuviSpace.s4)) {
                CascadeStack(cascade)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(SuuviSpace.s3)) {
                    PillField(1, stringResource(R.string.edit_field_title), title, { title = it }, saved)
                    PillField(2, stringResource(R.string.edit_field_place), place, { place = it }, saved)
                    PillField(3, stringResource(R.string.edit_field_tag), tag, { tag = it }, saved)
                }
            }
            if (saved) {
                Column(
                    Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(SuuviSpace.s2),
                ) {
                    Text(stringResource(R.string.edit_saved_title), fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.text, textAlign = TextAlign.Center)
                    Text(stringResource(R.string.edit_saved_body, detail?.folder?.name ?: ""), fontSize = 17.sp, color = SuuviColor.text, textAlign = TextAlign.Center)
                    Text(stringResource(R.string.edit_saved_hint), fontSize = 15.sp, color = SuuviColor.textMuted, textAlign = TextAlign.Center)
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(64.dp), contentAlignment = Alignment.Center) {
            OutlinedButton(
                onClick = {
                    if (saved) onBack() else save()
                },
                enabled = enabled,
                shape = RoundedCornerShape(SuuviRadius.pill),
                border = BorderStroke(1.dp, SuuviColor.border),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SuuviColor.text, disabledContentColor = SuuviColor.textMuted,
                    containerColor = SuuviColor.bg, disabledContainerColor = SuuviColor.canvas,
                ),
            ) {
                Text(
                    stringResource(if (saved) R.string.edit_done else R.string.edit_save),
                    fontSize = 17.sp, fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
