package app.suuvi.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.DailyPick
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/** Card "Suvenir do dia": titulo, nota de ate 3 linhas, data e lugar. */
@Composable
fun DailyCard(pick: DailyPick, modifier: Modifier = Modifier) {
    val date = LocalDate.ofEpochDay(pick.epochDay)
        .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
    Column(
        modifier
            .fillMaxWidth()
            .border(1.dp, SuuviColor.border, RoundedCornerShape(SuuviRadius.md))
            .padding(SuuviSpace.s4),
        verticalArrangement = Arrangement.spacedBy(SuuviSpace.s2),
    ) {
        Text(
            "${stringResource(R.string.shelf_daily_heading)} ${pick.emoji} ${pick.title}",
            fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.text,
        )
        Text(pick.note, fontSize = 15.sp, color = SuuviColor.text, maxLines = 3, overflow = TextOverflow.Ellipsis)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_calendar), null, Modifier.size(14.dp), tint = SuuviColor.textMuted)
            Text(" $date", fontSize = 13.sp, color = SuuviColor.textMuted)
            Spacer(Modifier.weight(1f))
            Icon(painterResource(R.drawable.ic_map_pin), null, Modifier.size(14.dp), tint = SuuviColor.textMuted)
            Text(" @${pick.place}", fontSize = 13.sp, color = SuuviColor.textMuted)
        }
    }
}
