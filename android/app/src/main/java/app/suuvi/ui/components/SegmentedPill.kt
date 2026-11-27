package app.suuvi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius

/** Alternador em pilula: trilho cinza, segmento ativo branco com borda fina. */
@Composable
fun SegmentedPill(
    options: List<String>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val track = RoundedCornerShape(SuuviRadius.pill)
    val knob = RoundedCornerShape(SuuviRadius.pill)
    Row(modifier.fillMaxWidth().background(SuuviColor.canvas, track).padding(2.dp)) {
        options.forEachIndexed { i, label ->
            val isSelected = i == selected
            val base = Modifier.weight(1f).height(40.dp)
            val look = if (isSelected) {
                base.background(SuuviColor.bg, knob).border(1.dp, SuuviColor.border, knob)
            } else base
            Box(
                look.clickable(role = Role.Tab) { onSelect(i) },
                contentAlignment = Alignment.Center,
            ) {
                Text(label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.text)
            }
        }
    }
}
