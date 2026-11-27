package app.suuvi.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.design.EmojiAssets

/** Emoji padronizado (Fluent Flat, igual no iOS). Sem arte no conjunto curado: cai no emoji do sistema. */
@Composable
fun EmojiImage(glyph: String, size: Dp = 20.dp, modifier: Modifier = Modifier) {
    val res = EmojiAssets.resFor(glyph)
    if (res != null) {
        Image(painterResource(res), contentDescription = null, modifier = modifier.size(size))
    } else {
        Text(glyph, fontSize = (size.value * 0.85f).sp, modifier = modifier)
    }
}
