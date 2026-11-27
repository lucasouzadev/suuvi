package app.suuvi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius

/** Chip de filtro: contorno; selecionado = fundo ambar. */
@Composable
fun TagChip(title: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().heightIn(min = 40.dp).semantics {
            this.selected = selected
            role = Role.Button
        },
        shape = RoundedCornerShape(SuuviRadius.pill),
        color = if (selected) SuuviColor.accent else SuuviColor.bg,
        border = BorderStroke(1.dp, if (selected) SuuviColor.accent else SuuviColor.border),
    ) {
        Box(Modifier.heightIn(min = 40.dp), contentAlignment = Alignment.Center) {
            Text(
                title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (selected) SuuviColor.onAccent else SuuviColor.text,
            )
        }
    }
}
