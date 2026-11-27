package app.suuvi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius
import app.suuvi.design.SuuviSpace
import app.suuvi.ui.components.EmojiImage

/** Chip letra + emoji das referencias (ex.: "P 📋"). */
@Composable
fun BadgeChip(letter: String, emoji: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(SuuviColor.bg, RoundedCornerShape(SuuviRadius.sm))
            .border(1.dp, SuuviColor.border, RoundedCornerShape(SuuviRadius.sm))
            .padding(horizontal = SuuviSpace.s3, vertical = SuuviSpace.s2),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SuuviSpace.s1),
    ) {
        Text(letter, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        EmojiImage(emoji, 20.dp)
    }
}
