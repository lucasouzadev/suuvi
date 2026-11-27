package app.suuvi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import app.suuvi.ui.FoldersScreen
import app.suuvi.ui.SearchScreen
import app.suuvi.ui.ShelfScreen

private enum class Screen { SHELF, FOLDERS, SEARCH }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Rota inicial opcional (usada pelo CI para tirar print): --es route folders | search
        val start = when (intent.getStringExtra("route")) {
            "folders" -> Screen.FOLDERS
            "search" -> Screen.SEARCH
            else -> Screen.SHELF
        }
        setContent {
            var screen by rememberSaveable { mutableStateOf(start) }
            BackHandler(enabled = screen != Screen.SHELF) { screen = Screen.SHELF }
            when (screen) {
                Screen.SHELF -> ShelfScreen(onFolders = { screen = Screen.FOLDERS }, onSearch = { screen = Screen.SEARCH })
                Screen.FOLDERS -> FoldersScreen(onBack = { screen = Screen.SHELF })
                Screen.SEARCH -> SearchScreen(onClose = { screen = Screen.SHELF })
            }
        }
    }
}
