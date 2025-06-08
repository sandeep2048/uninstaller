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
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppRepository(application)

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selected = MutableStateFlow<Set<String>>(emptySet())
    val selected: StateFlow<Set<String>> = _selected.asStateFlow()

    val filteredApps: StateFlow<List<AppInfo>> =
        MutableStateFlow(emptyList()).apply {
            viewModelScope.launch {
                combine(_apps, _query) { list, q ->
                    if (q.isBlank()) list else list.filter { it.name.contains(q, true) }
                }.collect { value = it }
            }
        }.asStateFlow()

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
