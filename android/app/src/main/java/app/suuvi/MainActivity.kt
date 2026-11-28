package app.suuvi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import app.suuvi.ui.CaptureScreen
import app.suuvi.ui.CollectionScreen
import app.suuvi.ui.EditScreen
import app.suuvi.ui.FoldersScreen
import app.suuvi.ui.SearchScreen
import app.suuvi.ui.SettingsScreen
import app.suuvi.services.ImageStore
import app.suuvi.ui.ViewerScreen
import app.suuvi.ui.ShelfScreen
import kotlinx.coroutines.flow.first

private enum class Screen { SHELF, FOLDERS, SEARCH, COLLECTION, EDIT, SETTINGS, CAPTURE, VIEWER }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Rota inicial opcional (usada pelo CI para tirar print): --es route folders | collection | viewerphoto | viewerlow | collection5 | editpiece | deleteask | deleted | shelfnote | viewercutout | edit | editdone | profile | settings | capture | capturelifted | search
        val start = when (intent.getStringExtra("route")) {
            "folders" -> Screen.FOLDERS
            "settings" -> Screen.SETTINGS
            "capture", "capturelifted" -> Screen.CAPTURE
            "search" -> Screen.SEARCH
            "collection", "profile" -> Screen.COLLECTION
            "viewer", "viewercutout", "viewerphoto", "viewerlow" -> Screen.VIEWER
            "collection2", "collection5", "deleteask", "deleted" -> Screen.COLLECTION
            "edit", "editdone", "created", "createdphoto", "editpiece" -> Screen.EDIT
            else -> Screen.SHELF
        }
        val route = intent.getStringExtra("route")
        val cutout = when (route) {
            "viewercutout" -> ImageStore.saveSampleCutout(this)
            "viewerphoto", "viewerlow" -> ImageStore.saveSampleCutout(this, "sample-cutout-cup")   // so CI: recorte com foto, para provar a textura
            else -> null
        }
        if (route in listOf("collection5", "editpiece", "deleteask", "deleted", "shelfnote")) seedForCi(route == "deleted", route == "shelfnote")   // so para o CI: 2 pecas salvas na f1
        setContent {
            var screen by rememberSaveable { mutableStateOf(start) }
            val preset = intent.getStringExtra("route") == "editdone"
            var collectionId by rememberSaveable { mutableStateOf(if (intent.getStringExtra("route") in listOf("createdphoto", "collection2")) "f2" else "f1") }
            var backTo by rememberSaveable { mutableStateOf(Screen.FOLDERS) }
            var captureFrom by rememberSaveable { mutableStateOf(Screen.SHELF) }
            var pieceTitle by rememberSaveable { mutableStateOf(if (route == "viewercutout") "Coração" else if (route == "viewerphoto" || route == "viewerlow") "Xícara" else "Moeda de Lisboa") }
            var viewerImage by rememberSaveable { mutableStateOf(cutout) }
            var pieceImage by rememberSaveable { mutableStateOf<String?>(null) }
            var editingId by rememberSaveable { mutableStateOf<String?>(if (route == "editpiece") "first" else null) }
            BackHandler(enabled = screen != Screen.SHELF) {
                screen = when (screen) {
                    Screen.EDIT -> Screen.COLLECTION
                    Screen.COLLECTION -> backTo
                    Screen.CAPTURE -> captureFrom
                    Screen.VIEWER -> Screen.COLLECTION
                    else -> Screen.SHELF
                }
            }
            when (screen) {
                Screen.SHELF -> ShelfScreen(
                    onFolders = { screen = Screen.FOLDERS },
                    onSearch = { screen = Screen.SEARCH },
                    onSettings = { screen = Screen.SETTINGS },
                    onCreate = { captureFrom = Screen.SHELF; screen = Screen.CAPTURE },
                    onOpen = { collectionId = it; backTo = Screen.SHELF; screen = Screen.COLLECTION },
                    forceNoted = route == "shelfnote",
                )
                Screen.FOLDERS -> FoldersScreen(
                    onBack = { screen = Screen.SHELF },
                    onOpen = { collectionId = it; backTo = Screen.FOLDERS; screen = Screen.COLLECTION },
                )
                Screen.COLLECTION -> CollectionScreen(
                    collectionId,
                    onBack = { screen = backTo },
                    onAdd = { captureFrom = Screen.COLLECTION; screen = Screen.CAPTURE },
                    onEdit = { editingId = it.id; screen = Screen.EDIT },
                    askDeleteFirst = route == "deleteask",
                    startProfile = intent.getStringExtra("route") == "profile",
                    onPiece = { pieceTitle = it.title; viewerImage = it.imagePath; screen = Screen.VIEWER },
                )
                Screen.VIEWER -> ViewerScreen(pieceTitle, viewerImage, onBack = { screen = Screen.COLLECTION }, startLow = route == "viewerlow")
                Screen.EDIT -> EditScreen(collectionId, preset, autoCreate = intent.getStringExtra("route") in listOf("created", "createdphoto"), image = if (intent.getStringExtra("route") == "createdphoto") "@sample" else pieceImage, pieceId = editingId, onBack = { screen = Screen.COLLECTION })
                Screen.CAPTURE -> CaptureScreen(
                    onBack = { screen = captureFrom },
                    onCreate = { pieceImage = it; editingId = null; screen = Screen.EDIT },
                    autoSample = intent.getStringExtra("route") == "capturelifted",
                )
                Screen.SETTINGS -> SettingsScreen(onBack = { screen = Screen.SHELF })
                Screen.SEARCH -> SearchScreen(onClose = { screen = Screen.SHELF })
            }
        }
    }

    /** So para o CI: garante 2 pecas salvas na colecao f1; com [deleteFirst] apaga a de menor numero (prova que apagar remove do banco e o numero nao volta). */
    private fun seedForCi(deleteFirst: Boolean, withNote: Boolean) {
        val dao = app.suuvi.data.SuuviDb.get(this).dao()
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
            val numbers = dao.savedNumbers("f1")
            var next = maxOf(3, app.suuvi.domain.PieceRules.issued(numbers, app.suuvi.services.PieceStore.stored(this@MainActivity, "f1")))
            for (title in listOf("Bonde 30", "Fado").take(maxOf(0, 2 - numbers.size))) {
                next++
                dao.insertSavedPiece(app.suuvi.data.SavedPiece(java.util.UUID.randomUUID().toString(), "f1", next, title, "lisboa", "viagem", "🪙", null, System.currentTimeMillis()))
                app.suuvi.services.PieceStore.setIssued(this@MainActivity, "f1", next)
            }
            if (withNote && dao.savedPieces("f1").first().none { it.note != null }) {
                next++
                dao.insertSavedPiece(app.suuvi.data.SavedPiece(java.util.UUID.randomUUID().toString(), "f1", next, "Pastel de nata", "lisboa", "doce", "🪙", null, System.currentTimeMillis(), "Provado numa manhã de sol."))
                app.suuvi.services.PieceStore.setIssued(this@MainActivity, "f1", next)
            }
            if (deleteFirst) {
                dao.savedPieces("f1").first().firstOrNull()?.let { app.suuvi.services.PieceStore.delete(this@MainActivity, dao, it) }
            }
        }
    }
}
