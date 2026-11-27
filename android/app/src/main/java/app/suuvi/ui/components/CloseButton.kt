package app.suuvi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.suuvi.R
import app.suuvi.design.SuuviColor

/** Botao fechar: X em circulo cinza (visual de 32dp, toque de 48dp). */
@Composable
fun CloseButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Box(Modifier.size(32.dp).background(SuuviColor.canvas, CircleShape), contentAlignment = Alignment.Center) {
            Icon(
                painterResource(R.drawable.ic_x), stringResource(R.string.a11y_close),
                Modifier.size(16.dp), tint = SuuviColor.textMuted,
            )
        }
    }
}
