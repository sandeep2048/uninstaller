package com.xequal2.uninstaller.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                    darkMode = darkMode,
                    onToggleTheme = { darkMode = !darkMode }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: AppViewModel,
    darkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    val apps by viewModel.filteredApps.collectAsState()
    val selected by viewModel.selected.collectAsState()
    val query by viewModel.query.collectAsState()
    val category by viewModel.category.collectAsState()

    Scaffold(
        floatingActionButton = {
            if (selected.isNotEmpty()) {
                FloatingActionButton(onClick = { viewModel.uninstallSelected() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Uninstall")
                }
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(text = "Uninstaller") },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        if (darkMode) {
                            Icon(
                                Icons.Default.LightMode,
                                contentDescription = "Light theme"
                            )
                        } else {
                            Icon(
                                Icons.Default.DarkMode,
                                contentDescription = "Dark theme"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            // Category Tabs
            TabRow(selectedTabIndex = if (category == AppViewModel.AppCategory.USER) 0 else 1) {
                Tab(
                    selected = category == AppViewModel.AppCategory.USER,
                    onClick = { viewModel.selectCategory(AppViewModel.AppCategory.USER) },
                    text = { Text("User Apps") }
                )
                Tab(
                    selected = category == AppViewModel.AppCategory.SYSTEM,
                    onClick = { viewModel.selectCategory(AppViewModel.AppCategory.SYSTEM) },
                    text = { Text("System Apps") }
                )
            }

            // Search Bar
            SearchBar(query = query, onQueryChange = viewModel::updateQuery)

            // App List
            if (apps.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp)) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                AppList(
                    apps = apps,
                    selected = selected,
                    onAppClick = { viewModel.toggleSelection(it) },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
