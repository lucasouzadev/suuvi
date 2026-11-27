package app.suuvi.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import app.suuvi.design.SuuviColor

/** Botao de icone (icones gerados de design/icons) com area de toque minima de 48dp. */
@Composable
fun IconAction(@DrawableRes icon: Int, description: String, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(painterResource(icon), contentDescription = description, tint = SuuviColor.text)
    }
}
