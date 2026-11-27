package app.suuvi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.CollectionRules

/** Cascata de cards sobrepostos e inclinados (lado esquerdo da Edicao). */
@Composable
fun CascadeStack(emojis: List<String>, modifier: Modifier = Modifier) {
    val step = 52
    val shape = RoundedCornerShape(SuuviRadius.md)
    Box(modifier.width(104.dp).height((108 + step * maxOf(emojis.size - 1, 0)).dp)) {
        emojis.forEachIndexed { i, glyph ->
            Box(
                Modifier.offset(x = 4.dp, y = (i * step).dp).size(96.dp, 108.dp).rotate(CollectionRules.tilt(i))
                    .background(SuuviColor.canvas, shape).border(1.dp, SuuviColor.border, shape),
                contentAlignment = Alignment.Center,
            ) { EmojiImage(glyph, 40.dp) }
        }
    }
}

/** Campo em pilula com circulo numerado; preenchido (salvo) vira check. */
@Composable
fun PillField(number: Int, hint: String, text: String, onChange: (String) -> Unit, done: Boolean) {
    val pill = RoundedCornerShape(SuuviRadius.pill)
    Row(
        Modifier.fillMaxWidth().heightIn(min = 48.dp)
            .background(if (done) SuuviColor.canvas else SuuviColor.bg, pill)
            .then(if (done) Modifier else Modifier.border(BorderStroke(1.dp, SuuviColor.border), pill))
            .padding(horizontal = SuuviSpace.s3),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(26.dp).border(1.5.dp, if (done) SuuviColor.accentInk else SuuviColor.border, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (done) Icon(painterResource(R.drawable.ic_check), null, Modifier.size(14.dp), tint = SuuviColor.accentInk)
            else Text("$number", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.textMuted)
        }
        Spacer(Modifier.width(SuuviSpace.s2))
        Box(Modifier.weight(1f)) {
            if (text.isEmpty()) Text(hint, fontSize = 17.sp, color = SuuviColor.textMuted, maxLines = 1)
            BasicTextField(
                value = text, onValueChange = onChange, enabled = !done, singleLine = true,
                textStyle = TextStyle(fontSize = 17.sp, color = SuuviColor.text),
                cursorBrush = SolidColor(SuuviColor.text), modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
