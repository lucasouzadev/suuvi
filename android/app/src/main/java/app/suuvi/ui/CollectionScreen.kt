package app.suuvi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.CollectionRules
import app.suuvi.domain.PieceItem
import app.suuvi.domain.SampleData
import app.suuvi.domain.ShelfRules
import app.suuvi.domain.collectionDetail
import app.suuvi.ui.components.EmptySlot
import app.suuvi.ui.components.IconAction
import app.suuvi.ui.components.PieceCard
import app.suuvi.ui.components.StatusMark

/** Colecao: voltar + chip, linha "de X" com @lugar/#n/status, grade 2x2 inclinada, legenda e rodape. */
@Composable
fun CollectionScreen(id: String, onBack: () -> Unit, onAdd: () -> Unit = {}, startProfile: Boolean = false, onPiece: (PieceItem) -> Unit = {}) {
    val base = SampleData.collectionDetail(id) ?: return
    val context = androidx.compose.ui.platform.LocalContext.current
    val dao = remember { app.suuvi.data.SuuviDb.get(context).dao() }
    val saved by dao.savedPieces(id).collectAsState(initial = emptyList())
    val d = base.copy(pieces = base.pieces + saved.map { PieceItem(it.id, it.title, it.emoji, it.imagePath) })
    val filled = CollectionRules.slots(d.pieces.size)
    var showProfile by rememberSaveable { mutableStateOf(startProfile) }
    if (showProfile) CollectionProfileSheet(d, onDismiss = { showProfile = false })
    Column(
        Modifier.fillMaxSize().background(SuuviColor.bg)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = SuuviSpace.s5),
        verticalArrangement = Arrangement.spacedBy(SuuviSpace.s4),
    ) {
        Box(Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
            BadgeChip(letter = ShelfRules.chipLetter(d.folder.name), emoji = d.folder.emoji)
            Row(Modifier.fillMaxWidth()) {
                IconAction(R.drawable.ic_chevron_left, stringResource(R.string.a11y_back), onBack)
            }
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                stringResource(R.string.collection_from, d.folder.name),
                Modifier.weight(1f), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.text,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
            Text("@${d.folder.place}", fontSize = 13.sp, color = SuuviColor.textMuted)
            Text(" #${d.folder.number} ", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.textMuted)
            StatusMark(d.status)
        }
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(SuuviSpace.s5),
        ) {
            Column(Modifier.padding(top = SuuviSpace.s2, start = SuuviSpace.s2, end = SuuviSpace.s2), verticalArrangement = Arrangement.spacedBy(SuuviSpace.s4)) {
                filled.chunked(2).forEachIndexed { row, cells ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(SuuviSpace.s4)) {
                        cells.forEachIndexed { col, isPiece ->
                            val i = row * 2 + col
                            Box(Modifier.weight(1f)) {
                                if (isPiece) Box(Modifier.clickable { onPiece(d.pieces[i]) }) { PieceCard(d.pieces[i], CollectionRules.tilt(i)) }
                                else EmptySlot(CollectionRules.tilt(i), onAdd)
                            }
                        }
                    }
                }
            }
            Text(
                stringResource(R.string.collection_caption), Modifier.fillMaxWidth(),
                fontSize = 15.sp, color = SuuviColor.textMuted, textAlign = TextAlign.Center,
            )
        }
        Row(Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_timer), null, Modifier.size(16.dp), tint = SuuviColor.textMuted)
                Text(
                    " " + stringResource(R.string.collection_count, d.pieces.size),
                    fontSize = 15.sp, color = SuuviColor.textMuted, maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
            }
            OutlinedButton(
                onClick = onAdd,
                shape = RoundedCornerShape(SuuviRadius.pill),
                border = BorderStroke(1.dp, SuuviColor.border),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SuuviColor.text),
            ) { Text(stringResource(R.string.collection_add), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, maxLines = 1) }
            Box(Modifier.size(48.dp)) {
                IconAction(R.drawable.ic_info, stringResource(R.string.a11y_info), { showProfile = true })
            }
        }
    }
}
