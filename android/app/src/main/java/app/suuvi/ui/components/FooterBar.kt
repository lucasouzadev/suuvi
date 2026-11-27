package app.suuvi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius

/** Rodape: versao, botao Create central e icone de acao. */
@Composable
fun FooterBar(
    version: String,
    showChevron: Boolean = true,
    onCreate: () -> Unit = {},
    onTray: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth().height(64.dp), contentAlignment = Alignment.Center) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (showChevron) Icon(painterResource(R.drawable.ic_chevron_right), null, Modifier.size(16.dp), tint = SuuviColor.textMuted)
            Text(version, fontSize = 15.sp, color = SuuviColor.textMuted)
            Spacer(Modifier.weight(1f))
            IconAction(R.drawable.ic_inbox, stringResource(R.string.a11y_inbox), onTray)
        }
        OutlinedButton(
            onClick = onCreate,
            shape = RoundedCornerShape(SuuviRadius.pill),
            border = BorderStroke(1.dp, SuuviColor.border),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SuuviColor.text),
        ) {
            Text(stringResource(R.string.action_create), fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
