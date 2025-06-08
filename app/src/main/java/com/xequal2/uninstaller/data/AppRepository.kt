package com.xequal2.uninstaller.data

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

class AppRepository(private val application: Application) {
    private val pm: PackageManager = application.packageManager

    fun loadInstalledApps(): List<AppInfo> {
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        return packages.map { info ->
            AppInfo(
                packageName = info.packageName,
                name = pm.getApplicationLabel(info).toString(),
                icon = pm.getApplicationIcon(info),
                installTime = try {
                    pm.getPackageInfo(info.packageName, 0).firstInstallTime
                } catch (e: Exception) {
                    0L
                },
                size = try {
                    java.io.File(info.sourceDir).length()
                } catch (e: Exception) {
                    0L
                },
                isSystemApp = info.flags and ApplicationInfo.FLAG_SYSTEM != 0
            )
        }.sortedBy { it.name.lowercase() }
    }
}
