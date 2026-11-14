package com.kasipem.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasipem.data.HistoryEntry
import com.kasipem.data.HistoryStore

/**
 * Riwayat Surat lokal (pengganti macro Arsip di APLIKASI SURAT.xlsm):
 * daftar surat yang sudah dibuat PDF-nya / dicetak — tanggal, nomor,
 * jenis, nama pemohon. Pengguna bisa menghapus per baris atau semuanya.
 * Data tersimpan di SharedPreferences (HistoryStore); tidak ada server,
 * tidak ada data warga yang dimuat sebelumnya.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val store = remember { HistoryStore(context) }
    var entries by remember { mutableStateOf(store.load()) }
    var confirmClearAll by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Riwayat Surat", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                }
            },
            actions = {
                if (entries.isNotEmpty()) {
                    IconButton(onClick = { confirmClearAll = true }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Hapus semua riwayat")
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                actionIconContentColor = MaterialTheme.colorScheme.onSurface
            )
        )

        if (confirmClearAll) {
            AlertDialog(
                onDismissRequest = { confirmClearAll = false },
                title = { Text("Hapus semua riwayat?") },
                text = { Text("Seluruh catatan riwayat surat akan dihapus dari perangkat ini dan tidak dapat dikembalikan.") },
                confirmButton = {
                    TextButton(onClick = {
                        store.clearAll()
                        entries = emptyList()
                        confirmClearAll = false
                    }) { Text("Hapus Semua") }
                },
                dismissButton = {
                    TextButton(onClick = { confirmClearAll = false }) { Text("Batal") }
                }
            )
        }

        if (entries.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Belum ada surat yang dibuat.\nSurat yang diunduh PDF / dicetak akan tercatat di sini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Text(
                "${entries.size} surat tercatat",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    HistoryEntryCard(
                        entry = entry,
                        onDelete = {
                            store.delete(entry.id)
                            entries = store.load()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryEntryCard(entry: HistoryEntry, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(entry.jenisSurat, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    entry.namaPemohon.ifBlank { "(tanpa nama)" },
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "No. ${entry.nomorSurat.ifBlank { "-" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    entry.tanggal,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Hapus entri ini")
            }
        }
    }
}
