package app.suuvi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.ShelfItem
import app.suuvi.domain.ShelfRules
import app.suuvi.ui.BadgeChip

/** Linha da lista: barra, chip letra+emoji, @lugar, letra do formato, #n e status. */
@Composable
fun ShelfRow(item: ShelfItem, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Column(modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            Modifier.fillMaxWidth().heightIn(min = 56.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SuuviSpace.s3),
        ) {
            Box(Modifier.width(2.dp).height(12.dp).background(SuuviColor.border))
            BadgeChip(letter = ShelfRules.chipLetter(item.name), emoji = item.emoji)
            Text("@${item.place}", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.text)
            Spacer(Modifier.weight(1f))
            Text(item.tier.name.take(1), fontSize = 13.sp, color = SuuviColor.textMuted)
            Text("#${item.number}", fontSize = 17.sp, color = SuuviColor.text)
            Box(Modifier.width(1.dp).height(16.dp).background(SuuviColor.border))
            StatusMark(item.status)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(SuuviColor.border))
    }
}
