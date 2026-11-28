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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import app.suuvi.R
import app.suuvi.design.SuuviColor
import app.suuvi.design.SuuviSpace
import app.suuvi.domain.FolderRules
import app.suuvi.domain.SampleData
import app.suuvi.domain.foldersUsedBy
import androidx.compose.runtime.collectAsState
import app.suuvi.domain.folderId
import app.suuvi.domain.ShelfRules
import app.suuvi.ui.components.DailyCard
import app.suuvi.ui.components.FooterBar
import app.suuvi.ui.components.HeaderBar
import app.suuvi.ui.components.SegmentedPill
import app.suuvi.ui.components.ShelfRow

/** Tela inicial: cabecalho, card do dia, alternador Colecao/Recebidas, lista e rodape. */
@Composable
fun ShelfScreen(onFolders: () -> Unit = {}, onSearch: () -> Unit = {}, onOpen: (String) -> Unit = {}, onSettings: () -> Unit = {}, onCreate: () -> Unit = {}, forceNoted: Boolean = false) {
    var segment by remember { mutableIntStateOf(0) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val saved by remember { app.suuvi.data.SuuviDb.get(context).dao().allSavedPieces() }.collectAsState(initial = emptyList())
    // Colecao: da mais usada para a menos usada (peca nova sobe a pasta). Recebidas: ordem original.
    val rows = if (segment == 0) {
        FolderRules.ordered(SampleData.foldersUsedBy(saved)).mapNotNull { f -> SampleData.collection.firstOrNull { it.name == f.name } }
    } else SampleData.received
    // A lista mantem a posicao da primeira linha pela chave; ao reordenar, volta ao topo para nao esconder a linha que subiu.
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    androidx.compose.runtime.LaunchedEffect(rows.map { it.id }) { listState.scrollToItem(0) }
    // Sorteio do dia entre os exemplos e as pecas salvas (qualquer uma; a nota, se houver, vira o texto do card).
    val mine = ShelfRules.dailyOrder(saved.map { it.folderId to it.number }).map { saved[it] }
    fun pick(p: app.suuvi.data.SavedPiece) = app.suuvi.domain.DailyPick(p.emoji, p.title, p.note ?: "", ShelfRules.epochDay(p.createdAt), p.place)
    val picks = SampleData.dailyPicks + mine.map { pick(it) }
    // So para o CI (--es route shelfnote): mostra uma peca salva com nota.
    val daily = (if (forceNoted) mine.firstOrNull { it.note != null }?.let { pick(it) } else null)
        ?: ShelfRules.dailyIndex(ShelfRules.todayEpochDay(), picks.size)?.let { picks[it] }

    Column(
        Modifier
            .fillMaxSize()
            .background(SuuviColor.bg)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = SuuviSpace.s5),
        verticalArrangement = Arrangement.spacedBy(SuuviSpace.s4),
    ) {
        HeaderBar(title = stringResource(R.string.shelf_title), onSettings = onSettings, onFolders = onFolders, onSearch = onSearch)
        if (daily != null && Prefs.showDaily(LocalContext.current)) DailyCard(daily)
        SegmentedPill(
            options = listOf(
                stringResource(R.string.shelf_segment_collection),
                stringResource(R.string.shelf_segment_received),
            ),
            selected = segment,
            onSelect = { segment = it },
        )
        LazyColumn(Modifier.weight(1f), state = listState) {
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
        FooterBar(version = "0.1.0", onCreate = onCreate)
    }
}
