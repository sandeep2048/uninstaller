package com.xequal2.uninstaller.data

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val name: String,
    val icon: Drawable,
    val installTime: Long,
    val size: Long,
    val isSystemApp: Boolean
)
