package app.suuvi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.R
import app.suuvi.design.SuuviColor

/** Cabecalho da Shelf: engrenagem, titulo central, pastas | busca. */
@Composable
fun HeaderBar(
    title: String,
    onSettings: () -> Unit = {},
    onFolders: () -> Unit = {},
    onSearch: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
        Text(title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.text)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconAction(Icons.Outlined.Settings, stringResource(R.string.a11y_settings), onSettings)
            Spacer(Modifier.weight(1f))
            IconAction(Icons.Outlined.Folder, stringResource(R.string.a11y_folders), onFolders)
            Box(Modifier.width(1.dp).height(16.dp).background(SuuviColor.border))
            IconAction(Icons.Outlined.Search, stringResource(R.string.a11y_search), onSearch)
        }
    }
}
