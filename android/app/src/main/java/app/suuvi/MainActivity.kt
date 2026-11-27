package app.suuvi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import app.suuvi.ui.CollectionScreen
import app.suuvi.ui.EditScreen
import app.suuvi.ui.FoldersScreen
import app.suuvi.ui.SearchScreen
import app.suuvi.ui.SettingsScreen
import app.suuvi.ui.ShelfScreen

private enum class Screen { SHELF, FOLDERS, SEARCH, COLLECTION, EDIT, SETTINGS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Rota inicial opcional (usada pelo CI para tirar print): --es route folders | collection | edit | editdone | profile | settings | search
        val start = when (intent.getStringExtra("route")) {
            "folders" -> Screen.FOLDERS
            "settings" -> Screen.SETTINGS
            "search" -> Screen.SEARCH
            "collection", "profile" -> Screen.COLLECTION
            "edit", "editdone" -> Screen.EDIT
            else -> Screen.SHELF
        }
        setContent {
            var screen by rememberSaveable { mutableStateOf(start) }
            val preset = intent.getStringExtra("route") == "editdone"
            var collectionId by rememberSaveable { mutableStateOf("f1") }
            var backTo by rememberSaveable { mutableStateOf(Screen.FOLDERS) }
            BackHandler(enabled = screen != Screen.SHELF) {
                screen = when (screen) {
                    Screen.EDIT -> Screen.COLLECTION
                    Screen.COLLECTION -> backTo
                    else -> Screen.SHELF
                }
            }
            when (screen) {
                Screen.SHELF -> ShelfScreen(
                    onFolders = { screen = Screen.FOLDERS },
                    onSearch = { screen = Screen.SEARCH },
                    onSettings = { screen = Screen.SETTINGS },
                    onOpen = { collectionId = it; backTo = Screen.SHELF; screen = Screen.COLLECTION },
                )
                Screen.FOLDERS -> FoldersScreen(
                    onBack = { screen = Screen.SHELF },
                    onOpen = { collectionId = it; backTo = Screen.FOLDERS; screen = Screen.COLLECTION },
                )
                Screen.COLLECTION -> CollectionScreen(
                    collectionId,
                    onBack = { screen = backTo },
                    onAdd = { screen = Screen.EDIT },
                    startProfile = intent.getStringExtra("route") == "profile",
                )
                Screen.EDIT -> EditScreen(collectionId, preset, onBack = { screen = Screen.COLLECTION })
                Screen.SETTINGS -> SettingsScreen(onBack = { screen = Screen.SHELF })
                Screen.SEARCH -> SearchScreen(onClose = { screen = Screen.SHELF })
            }
        }
    }
}
