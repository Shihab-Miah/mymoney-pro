package com.miahshihab.mymoney.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miahshihab.mymoney.drive.GoogleDriveBackupManager
import com.miahshihab.mymoney.ui.theme.AppThemeColors
import com.miahshihab.mymoney.ui.theme.ThemePresets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: AppThemeColors,
    onThemeSelected: (AppThemeColors) -> Unit,
    isDarkMode: Boolean,
    onDarkModeToggled: (Boolean) -> Unit,
    isAmoled: Boolean,
    onAmoledToggled: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val driveManager = remember { GoogleDriveBackupManager(context) }
    var backupStatusMessage by remember { mutableStateOf<String?>(null) }
    var userGmailAccount by remember { mutableStateOf("shihabmiah.work@gmail.com") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Themes", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Theme & Color Customizer ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🎨 Theme & Color Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Accent Color Palette", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ThemePresets.AllPresets.forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(preset.primary)
                                    .border(
                                        width = if (currentTheme.name == preset.name) 3.dp else 0.dp,
                                        color = if (currentTheme.name == preset.name) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onThemeSelected(preset) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Dark Mode")
                        Switch(checked = isDarkMode, onCheckedChange = onDarkModeToggled)
                    }

                    if (isDarkMode) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Pure Black AMOLED Mode")
                            Switch(checked = isAmoled, onCheckedChange = onAmoledToggled)
                        }
                    }
                }
            }

            // --- Google Drive & Gmail Sync ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("☁️ Google Drive Cloud Backup", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Sign in with your Gmail to backup app records to private Google Drive AppData.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = userGmailAccount,
                        onValueChange = { userGmailAccount = it },
                        label = { Text("Google Account") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val file = driveManager.createLocalBackupSnapshot()
                                backupStatusMessage = "Snapshot generated: ${file.name}"
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Backup Now")
                        }

                        OutlinedButton(
                            onClick = {
                                val backups = driveManager.getLocalBackups()
                                backupStatusMessage = if (backups.isNotEmpty()) {
                                    "Ready to restore: ${backups.first().name}"
                                } else {
                                    "No backups found in local cache"
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Restore")
                        }
                    }

                    if (backupStatusMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            backupStatusMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // --- About Developer & Branding ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("👨‍💻 Developed & Signed By", style = MaterialTheme.typography.titleSmall, color = Color.Gray)
                    Text("Miah Shihab (miah_shihab)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("shihabmiah.work@gmail.com", style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(12.dp))

                    FilledTonalButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Shihab-Miah"))
                            context.startActivity(intent)
                        }
                    ) {
                        Text("View GitHub Profile")
                    }
                }
            }
        }
    }
}
