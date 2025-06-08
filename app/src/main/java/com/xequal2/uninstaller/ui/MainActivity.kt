package com.xequal2.uninstaller.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import com.xequal2.uninstaller.ui.SearchBar
import com.xequal2.uninstaller.ui.theme.UninstallerTheme
import com.xequal2.uninstaller.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var darkMode by remember { mutableStateOf(false) }
            UninstallerTheme(useDarkTheme = darkMode) {
                MainScreen(
                    viewModel = viewModel,
                    onToggleTheme = { darkMode = !darkMode }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: AppViewModel, onToggleTheme: () -> Unit) {
    val apps by viewModel.filteredApps.collectAsState()
    val selected by viewModel.selected.collectAsState()
    val query by viewModel.query.collectAsState()

    Scaffold(
        floatingActionButton = {
            if (selected.isNotEmpty()) {
                FloatingActionButton(onClick = { viewModel.uninstallSelected() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Uninstall")
                }
            }
        },
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text(text = "Uninstaller") },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(Icons.Default.Refresh, contentDescription = "Toggle theme")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = androidx.compose.ui.Modifier.padding(padding)) {
            SearchBar(query = query, onQueryChange = viewModel::updateQuery)
            AppList(
                apps = apps,
                selected = selected,
                onAppClick = { viewModel.toggleSelection(it) }
            )
        }
    }
}
