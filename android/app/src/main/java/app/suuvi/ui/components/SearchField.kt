package app.suuvi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviRadius
import app.suuvi.design.SuuviSpace

/** Campo de busca em pilula, com lupa. */
@Composable
fun SearchField(text: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier.fillMaxWidth().heightIn(min = 48.dp),
        shape = RoundedCornerShape(SuuviRadius.pill),
        color = SuuviColor.bg,
        border = BorderStroke(1.dp, SuuviColor.border),
    ) {
        Row(Modifier.padding(horizontal = SuuviSpace.s4), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_search), null, Modifier.size(18.dp), tint = SuuviColor.textMuted)
            Spacer(Modifier.width(SuuviSpace.s2))
            Box(Modifier.weight(1f)) {
                if (text.isEmpty()) {
                    Text(stringResource(R.string.search_field), fontSize = 17.sp, color = SuuviColor.textMuted)
                }
                BasicTextField(
                    value = text, onValueChange = onChange, singleLine = true,
                    textStyle = TextStyle(fontSize = 17.sp, color = SuuviColor.text),
                    cursorBrush = SolidColor(SuuviColor.text),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
