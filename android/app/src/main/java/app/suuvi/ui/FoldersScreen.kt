package app.suuvi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.FolderRules
import app.suuvi.domain.SampleData
import app.suuvi.domain.folders
import app.suuvi.ui.components.BackBar
import app.suuvi.ui.components.FolderCard
import app.suuvi.ui.components.FooterBar

/** Tela de Pastas: grade de 2 colunas com as colecoes, da mais usada para a menos usada. */
@Composable
fun FoldersScreen(onBack: () -> Unit) {
    val rows = FolderRules.ordered(SampleData.folders)
    Column(
        Modifier
            .fillMaxSize()
            .background(SuuviColor.bg)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = SuuviSpace.s5),
        verticalArrangement = Arrangement.spacedBy(SuuviSpace.s4),
    ) {
        BackBar(title = stringResource(R.string.folders_title), onBack = onBack)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(SuuviSpace.s3),
            verticalArrangement = Arrangement.spacedBy(SuuviSpace.s3),
        ) {
            items(rows, key = { it.id }) { FolderCard(it) }
        }
        FooterBar(version = "0.1.0", showChevron = false)
    }
}
