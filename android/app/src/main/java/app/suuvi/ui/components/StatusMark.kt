package app.suuvi.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            Icons.Outlined.Check, stringResource(R.string.a11y_status_ready),
            modifier.size(24.dp), tint = SuuviColor.accentInk,
        )
        SouvenirStatus.DRAFT -> Icon(
            Icons.Outlined.Circle, stringResource(R.string.a11y_status_draft),
            modifier.size(24.dp), tint = SuuviColor.textMuted,
        )
        SouvenirStatus.FAILED -> Icon(
            Icons.Outlined.Close, stringResource(R.string.a11y_status_failed),
            modifier.size(24.dp), tint = SuuviColor.textMuted,
        )
    }
}
