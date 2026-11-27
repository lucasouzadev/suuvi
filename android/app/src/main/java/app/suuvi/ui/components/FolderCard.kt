package app.suuvi.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.FolderItem

/** Card de pasta: emoji no centro, @lugar embaixo a esquerda, #n embaixo a direita. */
@Composable
fun FolderCard(item: FolderItem, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .aspectRatio(1.25f)
            .border(1.dp, SuuviColor.border, RoundedCornerShape(SuuviRadius.md))
            .semantics(mergeDescendants = true) {},
    ) {
        Text(item.emoji, fontSize = 36.sp, modifier = Modifier.align(Alignment.Center))
        Row(
            Modifier.fillMaxSize().padding(SuuviSpace.s3),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text("@${item.place}", fontSize = 13.sp, color = SuuviColor.textMuted)
            Spacer(Modifier.weight(1f))
            Text("#${item.number}", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.textMuted)
        }
    }
}
