package com.miahshihab.mymoney.drive

import android.content.Context
import android.util.Log
import com.miahshihab.mymoney.data.AppDatabaseHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Manages Cloud & Google Drive AppData synchronization.
 * Supports exporting database snapshots, staging backup payloads,
 * and preparing Google Drive REST API uploads under user's Google account.
 */
class GoogleDriveBackupManager(private val context: Context) {

    companion object {
        const val TAG = "GoogleDriveBackup"
        const val BACKUP_MIME_TYPE = "application/json"
        const val BACKUP_PREFIX = "mymoney_backup_"
    }

    private val dbHelper = AppDatabaseHelper(context)

    /**
     * Prepares a timestamped JSON backup snapshot.
     */
    fun createLocalBackupSnapshot(): File {
        val jsonPayload = dbHelper.exportToJsonString()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "$BACKUP_PREFIX$timestamp.json"
        
        val backupDir = File(context.filesDir, "backups")
        if (!backupDir.exists()) {
            backupDir.mkdirs()
        }
        
        val backupFile = File(backupDir, fileName)
        backupFile.writeText(jsonPayload)
        Log.i(TAG, "Backup created successfully at: ${backupFile.absolutePath}")
        return backupFile
    }

    /**
     * List all locally cached or prepared backup snapshots.
     */
    fun getLocalBackups(): List<File> {
        val backupDir = File(context.filesDir, "backups")
        return backupDir.listFiles { file -> file.name.startsWith(BACKUP_PREFIX) && file.name.endsWith(".json") }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }
}
