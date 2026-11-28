package app.suuvi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.SampleData
import app.suuvi.domain.SearchItem
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import app.suuvi.domain.SearchRules
import app.suuvi.domain.searchItems
import app.suuvi.ui.components.CloseButton
import app.suuvi.ui.components.ResultCard
import app.suuvi.ui.components.SearchField
import app.suuvi.ui.components.TagChip

private val heights = listOf(160.dp, 128.dp, 144.dp, 120.dp)

/** Busca + filtro: titulo com fechar, campo, chips de tags (3 colunas) e resultados em mosaico (2 colunas). */
@Composable
fun SearchScreen(onClose: () -> Unit = {}) {
    var query by rememberSaveable { mutableStateOf("") }
    var selected by rememberSaveable { mutableStateOf(setOf<String>()) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val saved by remember { app.suuvi.data.SuuviDb.get(context).dao().allSavedPieces() }.collectAsState(initial = emptyList())
    // Pecas salvas (mais novas primeiro) e depois os exemplos.
    val items = saved.map { SearchItem(it.id, it.title, it.emoji, it.place, it.number, listOfNotNull(it.tag)) } + SampleData.searchItems
    val tags = SearchRules.allTags(items)
    val results = SearchRules.filter(items, query, selected)

    Column(
        Modifier.fillMaxSize().background(SuuviColor.bg)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = SuuviSpace.s5),
        verticalArrangement = Arrangement.spacedBy(SuuviSpace.s3),
    ) {
        Box(Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.search_title), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = SuuviColor.text)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                CloseButton(onClose)
            }
        }
        SearchField(query, { query = it })
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(SuuviSpace.s4),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(SuuviSpace.s2)) {
            tags.chunked(3).forEach { row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(SuuviSpace.s2)) {
                    row.forEach { tag ->
                        TagChip(
                            tag, tag in selected,
                            { selected = if (tag in selected) selected - tag else selected + tag },
                            Modifier.weight(1f),
                        )
                    }
                    repeat(3 - row.size) { Box(Modifier.weight(1f)) }
                }
            }
            }
            if (results.isEmpty()) {
                Text(
                    stringResource(R.string.search_empty),
                    Modifier.fillMaxWidth().padding(top = SuuviSpace.s8),
                    color = SuuviColor.textMuted, fontSize = 17.sp, fontWeight = FontWeight.SemiBold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            } else {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(SuuviSpace.s3)) {
                    for (parity in 0..1) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(SuuviSpace.s4)) {
                            results.withIndex().filter { it.index % 2 == parity }.forEach { (i, item) ->
                                ResultCard(item, heights[i % heights.size])
                            }
                        }
                    }
                }
            }
            Box(Modifier.height(SuuviSpace.s5))
        }
    }
}
