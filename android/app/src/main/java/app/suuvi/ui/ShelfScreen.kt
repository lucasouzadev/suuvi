package app.suuvi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.SampleData
import app.suuvi.domain.folderId
import app.suuvi.domain.ShelfRules
import app.suuvi.ui.components.DailyCard
import app.suuvi.ui.components.FooterBar
import app.suuvi.ui.components.HeaderBar
import app.suuvi.ui.components.SegmentedPill
import app.suuvi.ui.components.ShelfRow

/** Tela inicial: cabecalho, card do dia, alternador Colecao/Recebidas, lista e rodape. */
@Composable
fun ShelfScreen(onFolders: () -> Unit = {}, onSearch: () -> Unit = {}, onOpen: (String) -> Unit = {}) {
    var segment by remember { mutableIntStateOf(0) }
    val rows = if (segment == 0) SampleData.collection else SampleData.received
    val daily = ShelfRules.dailyIndex(ShelfRules.todayEpochDay(), SampleData.dailyPicks.size)
        ?.let { SampleData.dailyPicks[it] }

    Column(
        Modifier
            .fillMaxSize()
            .background(SuuviColor.bg)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = SuuviSpace.s5),
        verticalArrangement = Arrangement.spacedBy(SuuviSpace.s4),
    ) {
        HeaderBar(title = stringResource(R.string.shelf_title), onFolders = onFolders, onSearch = onSearch)
        if (daily != null) DailyCard(daily)
        SegmentedPill(
            options = listOf(
                stringResource(R.string.shelf_segment_collection),
                stringResource(R.string.shelf_segment_received),
            ),
            selected = segment,
            onSelect = { segment = it },
        )
        LazyColumn(Modifier.weight(1f)) {
            items(rows, key = { it.id }) { row -> ShelfRow(row, onClick = { SampleData.folderId(row.name)?.let(onOpen) }) }
            item {
                Column(
                    Modifier.fillMaxWidth().padding(top = SuuviSpace.s8),
                    verticalArrangement = Arrangement.spacedBy(SuuviSpace.s2),
                ) {
                    Text(
                        stringResource(R.string.shelf_helper_title),
                        Modifier.fillMaxWidth(), color = SuuviColor.text,
                        fontSize = 20.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center,
                    )
                    Text(
                        stringResource(R.string.shelf_helper_subtitle),
                        Modifier.fillMaxWidth(), color = SuuviColor.textMuted,
                        fontSize = 20.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center,
                    )
                }
            }
        }
        FooterBar(version = "0.1.0")
    }
}
