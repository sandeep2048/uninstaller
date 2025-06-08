package com.xequal2.uninstaller.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xequal2.uninstaller.data.AppInfo
import com.xequal2.uninstaller.data.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    // Category support
    enum class AppCategory { USER, SYSTEM }
    private val _category = MutableStateFlow(AppCategory.USER)
    val category: StateFlow<AppCategory> = _category.asStateFlow()
    fun selectCategory(category: AppCategory) {
        _category.value = category
    }

    // Main data
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selected = MutableStateFlow<Set<String>>(emptySet())
    val selected: StateFlow<Set<String>> = _selected.asStateFlow()

    // Combined filter: supports category and search query
    val filteredApps: StateFlow<List<AppInfo>> =
        combine(_apps, _query, _category) { apps, q, cat ->
            apps.filter { app ->
                val matchesCategory = when (cat) {
                    AppCategory.USER -> !app.isSystemApp
                    AppCategory.SYSTEM -> app.isSystemApp
                }
                val matchesQuery = q.isBlank() || app.name.contains(q, ignoreCase = true)
                matchesCategory && matchesQuery
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _apps.value = repository.loadInstalledApps()
        }
    }

    fun updateQuery(value: String) {
        _query.value = value
    }

    fun toggleSelection(packageName: String) {
        _selected.value = if (packageName in _selected.value) {
            _selected.value - packageName
        } else {
            _selected.value + packageName
        }
    }

    fun clearSelection() {
        _selected.value = emptySet()
    }

    fun uninstallSelected() {
        val context = getApplication<Application>()
        _selected.value.forEach { pkg ->
            val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:$pkg"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        clearSelection()
    }
}
