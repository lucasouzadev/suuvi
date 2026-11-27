package app.suuvi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviSpace

/** Tela de catalogo interna: mostra os tokens e os componentes base. */
@Composable
fun CatalogScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SuuviColor.bg)
            .padding(SuuviSpace.s5),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(SuuviSpace.s4),
    ) {
        Text("suuvi", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = SuuviColor.text)
        Text("Catalogo de design", fontSize = 14.sp, color = SuuviColor.textMuted)
        BadgeChip(letter = "C", emoji = "🪴")
        Text("#1", fontSize = 15.sp, color = SuuviColor.textMuted)
    }
}
