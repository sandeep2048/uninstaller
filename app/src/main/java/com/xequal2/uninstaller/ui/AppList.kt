package com.xequal2.uninstaller.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.xequal2.uninstaller.data.AppInfo

@Composable
fun AppList(
    apps: List<AppInfo>,
    selected: Set<String>,
    onAppClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.padding(8.dp)) {
        items(apps) { app ->
            AppItem(
                app = app,
                selected = app.packageName in selected,
                onClick = { onAppClick(app.packageName) }
            )
        }
    }
}

@Composable
fun AppItem(app: AppInfo, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                bitmap = app.icon.toBitmap().asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = app.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = app.packageName, style = MaterialTheme.typography.bodySmall)
            }
            Checkbox(checked = selected, onCheckedChange = { onClick() })
        }
    }
}
