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
import app.suuvi.ui.ViewerScreen
import app.suuvi.ui.ShelfScreen

private enum class Screen { SHELF, FOLDERS, SEARCH, COLLECTION, EDIT, SETTINGS, CAPTURE, VIEWER }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Rota inicial opcional (usada pelo CI para tirar print): --es route folders | collection | edit | editdone | profile | settings | capture | capturelifted | search
        val start = when (intent.getStringExtra("route")) {
            "folders" -> Screen.FOLDERS
            "settings" -> Screen.SETTINGS
            "capture", "capturelifted" -> Screen.CAPTURE
            "search" -> Screen.SEARCH
            "collection", "profile" -> Screen.COLLECTION
            "viewer" -> Screen.VIEWER
            "edit", "editdone" -> Screen.EDIT
            else -> Screen.SHELF
        }
        setContent {
            var screen by rememberSaveable { mutableStateOf(start) }
            val preset = intent.getStringExtra("route") == "editdone"
            var collectionId by rememberSaveable { mutableStateOf("f1") }
            var backTo by rememberSaveable { mutableStateOf(Screen.FOLDERS) }
            var captureFrom by rememberSaveable { mutableStateOf(Screen.SHELF) }
            var pieceTitle by rememberSaveable { mutableStateOf("Moeda de Lisboa") }
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
                )
                Screen.FOLDERS -> FoldersScreen(
                    onBack = { screen = Screen.SHELF },
                    onOpen = { collectionId = it; backTo = Screen.FOLDERS; screen = Screen.COLLECTION },
                )
                Screen.COLLECTION -> CollectionScreen(
                    collectionId,
                    onBack = { screen = backTo },
                    onAdd = { captureFrom = Screen.COLLECTION; screen = Screen.CAPTURE },
                    startProfile = intent.getStringExtra("route") == "profile",
                    onPiece = { pieceTitle = it; screen = Screen.VIEWER },
                )
                Screen.VIEWER -> ViewerScreen(pieceTitle, onBack = { screen = Screen.COLLECTION })
                Screen.EDIT -> EditScreen(collectionId, preset, onBack = { screen = Screen.COLLECTION })
                Screen.CAPTURE -> CaptureScreen(
                    onBack = { screen = captureFrom },
                    onCreate = { screen = Screen.EDIT },
                    autoSample = intent.getStringExtra("route") == "capturelifted",
                )
                Screen.SETTINGS -> SettingsScreen(onBack = { screen = Screen.SHELF })
                Screen.SEARCH -> SearchScreen(onClose = { screen = Screen.SHELF })
            }
        }
    }
}
