package app.suuvi.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.domain.SouvenirStatus

/** Marca de status: check ambar = pronto, circulo = rascunho, X = falhou. */
@Composable
fun StatusMark(status: SouvenirStatus, modifier: Modifier = Modifier) {
    when (status) {
        SouvenirStatus.READY -> Icon(
            painterResource(R.drawable.ic_check), stringResource(R.string.a11y_status_ready),
            modifier.size(24.dp), tint = SuuviColor.accentInk,
        )
        SouvenirStatus.DRAFT -> Icon(
            painterResource(R.drawable.ic_circle), stringResource(R.string.a11y_status_draft),
            modifier.size(24.dp), tint = SuuviColor.textMuted,
        )
        SouvenirStatus.FAILED -> Icon(
            painterResource(R.drawable.ic_x), stringResource(R.string.a11y_status_failed),
            modifier.size(24.dp), tint = SuuviColor.textMuted,
        )
    }
}
