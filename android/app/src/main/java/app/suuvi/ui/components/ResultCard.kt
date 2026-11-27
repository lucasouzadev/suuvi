package app.suuvi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.SearchItem

/** Resultado da busca: bloco com emoji (altura varia, para o efeito mosaico) e legenda. */
@Composable
fun ResultCard(item: SearchItem, height: Dp, modifier: Modifier = Modifier) {
    Column(
        modifier.semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(SuuviSpace.s2),
    ) {
        Box(
            Modifier.fillMaxWidth().height(height)
                .background(SuuviColor.canvas, RoundedCornerShape(SuuviRadius.md)),
            contentAlignment = Alignment.Center,
        ) { Text(item.emoji, fontSize = 44.sp) }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                item.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.text,
                maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false),
            )
            Text("@${item.place}", fontSize = 13.sp, color = SuuviColor.textMuted, maxLines = 1)
        }
    }
}
