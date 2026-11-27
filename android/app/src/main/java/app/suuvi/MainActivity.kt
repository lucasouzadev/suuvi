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
import app.suuvi.ui.FoldersScreen
import app.suuvi.ui.SearchScreen
import app.suuvi.ui.ShelfScreen

private enum class Screen { SHELF, FOLDERS, SEARCH, COLLECTION }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Rota inicial opcional (usada pelo CI para tirar print): --es route folders | collection | search
        val start = when (intent.getStringExtra("route")) {
            "folders" -> Screen.FOLDERS
            "search" -> Screen.SEARCH
            "collection" -> Screen.COLLECTION
            else -> Screen.SHELF
        }
        setContent {
            var screen by rememberSaveable { mutableStateOf(start) }
            var collectionId by rememberSaveable { mutableStateOf("f1") }
            BackHandler(enabled = screen != Screen.SHELF) {
                screen = if (screen == Screen.COLLECTION) Screen.FOLDERS else Screen.SHELF
            }
            when (screen) {
                Screen.SHELF -> ShelfScreen(onFolders = { screen = Screen.FOLDERS }, onSearch = { screen = Screen.SEARCH })
                Screen.FOLDERS -> FoldersScreen(
                    onBack = { screen = Screen.SHELF },
                    onOpen = { collectionId = it; screen = Screen.COLLECTION },
                )
                Screen.COLLECTION -> CollectionScreen(collectionId, onBack = { screen = Screen.FOLDERS })
                Screen.SEARCH -> SearchScreen(onClose = { screen = Screen.SHELF })
            }
        }
    }
}
