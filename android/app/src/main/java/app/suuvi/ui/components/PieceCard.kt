package app.suuvi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.PieceItem

/** Peca na grade da colecao, levemente inclinada (angulo fixo por posicao). */
@Composable
fun PieceCard(piece: PieceItem, tilt: Float, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(SuuviRadius.md)
    Column(
        modifier.fillMaxWidth().aspectRatio(0.9f).rotate(tilt)
            .background(SuuviColor.canvas, shape).border(1.dp, SuuviColor.border, shape)
            .padding(SuuviSpace.s3)
            .semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SuuviSpace.s2),
    ) {
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { EmojiImage(piece.emoji, 56.dp) }
        Text(
            piece.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.text,
            maxLines = 1, overflow = TextOverflow.Ellipsis,
        )
    }
}

/** Vaga vazia: contorno tracejado com "+" (toque = adicionar peca). */
@Composable
fun EmptySlot(tilt: Float, onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    val dash = PathEffect.dashPathEffect(floatArrayOf(18f, 15f))
    val line = SuuviColor.border
    Box(
        modifier.fillMaxWidth().aspectRatio(0.9f).rotate(tilt)
            .drawBehind {
                drawRoundRect(
                    line, cornerRadius = CornerRadius(SuuviRadius.md.toPx()),
                    style = Stroke(width = 1.5.dp.toPx(), pathEffect = dash),
                )
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painterResource(R.drawable.ic_plus), stringResource(R.string.collection_add),
            Modifier.size(28.dp), tint = SuuviColor.textMuted,
        )
    }
}
