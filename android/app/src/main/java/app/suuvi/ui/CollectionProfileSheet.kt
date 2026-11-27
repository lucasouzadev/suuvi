package app.suuvi.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.CollectionDetail
import app.suuvi.domain.PieceTier
import app.suuvi.domain.SouvenirStatus
import app.suuvi.ui.components.EmojiImage
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Folha de perfil da colecao (nativa Material 3): cabecalho com fechar e compartilhar nas
 * bordas, SingleChoiceSegmentedButtonRow do sistema e compartilhamento via Intent do Android.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionProfileSheet(detail: CollectionDetail, onDismiss: () -> Unit) {
    var segment by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val date = LocalDate.ofEpochDay(detail.folder.lastUsedEpochDay)
        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SuuviColor.bg,
    ) {
        Column(
            Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = SuuviSpace.s4),
            verticalArrangement = Arrangement.spacedBy(SuuviSpace.s3),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(painterResource(R.drawable.ic_x), stringResource(R.string.a11y_close), tint = SuuviColor.text)
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "${detail.folder.name} #${detail.folder.number}")
                    }
                    context.startActivity(Intent.createChooser(send, null))
                }) {
                    Icon(painterResource(R.drawable.ic_share), stringResource(R.string.a11y_share), tint = SuuviColor.text)
                }
            }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(SuuviSpace.s2)) {
                EmojiImage(detail.folder.emoji, 72.dp)
                Text(detail.folder.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = SuuviColor.text)
                Text("#${detail.folder.number}", fontSize = 15.sp, color = SuuviColor.textMuted)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painterResource(R.drawable.ic_map_pin), null, Modifier.size(14.dp), tint = SuuviColor.textMuted)
                    Text(" @${detail.folder.place}", fontSize = 13.sp, color = SuuviColor.textMuted)
                    Spacer(Modifier.width(SuuviSpace.s4))
                    Icon(painterResource(R.drawable.ic_calendar), null, Modifier.size(14.dp), tint = SuuviColor.textMuted)
                    Text(" $date", fontSize = 13.sp, color = SuuviColor.textMuted)
                }
            }
            val labels = listOf(stringResource(R.string.profile_pieces), stringResource(R.string.profile_details))
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                labels.forEachIndexed { i, label ->
                    SegmentedButton(
                        selected = segment == i,
                        onClick = { segment = i },
                        shape = SegmentedButtonDefaults.itemShape(index = i, count = labels.size),
                    ) { Text(label) }
                }
            }
            Column(Modifier.fillMaxWidth().heightIn(min = 220.dp).verticalScroll(rememberScrollState())) {
                if (segment == 0) {
                    if (detail.pieces.isEmpty()) {
                        Text(
                            stringResource(R.string.profile_empty),
                            Modifier.padding(top = SuuviSpace.s6),
                            fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.textMuted,
                        )
                    }
                    detail.pieces.forEach { piece ->
                        ProfileRow {
                            Icon(painterResource(R.drawable.ic_chevron_right), null, Modifier.size(16.dp), tint = SuuviColor.textMuted)
                            EmojiImage(piece.emoji, 24.dp)
                            Text(piece.title, fontSize = 17.sp, color = SuuviColor.text)
                        }
                    }
                } else {
                    val tier = when (PieceTier.Default) {
                        PieceTier.COIN -> R.string.tier_coin
                        PieceTier.PIN -> R.string.tier_pin
                        PieceTier.MAGNET -> R.string.tier_magnet
                    }
                    val status = when (detail.status) {
                        SouvenirStatus.READY -> R.string.a11y_status_ready
                        SouvenirStatus.DRAFT -> R.string.a11y_status_draft
                        SouvenirStatus.FAILED -> R.string.a11y_status_failed
                    }
                    ProfileRow {
                        Text(stringResource(R.string.profile_format), Modifier.weight(1f), fontSize = 17.sp, color = SuuviColor.text)
                        Text(stringResource(tier), fontSize = 17.sp, color = SuuviColor.textMuted)
                    }
                    ProfileRow {
                        Text(stringResource(R.string.profile_status), Modifier.weight(1f), fontSize = 17.sp, color = SuuviColor.text)
                        Text(stringResource(status), fontSize = 17.sp, color = SuuviColor.textMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileRow(content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SuuviSpace.s3),
            content = content,
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(SuuviColor.border))
    }
}
