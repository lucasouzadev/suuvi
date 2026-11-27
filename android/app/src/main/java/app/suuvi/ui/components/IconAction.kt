package app.suuvi.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import app.suuvi.design.SuuviColor

/** Botao de icone com area de toque minima de 48dp (IconButton do Material). */
@Composable
fun IconAction(icon: ImageVector, description: String, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(icon, contentDescription = description, tint = SuuviColor.text)
    }
}
