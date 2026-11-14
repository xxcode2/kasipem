package com.kasipem.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Riwayat Surat lokal (pengganti macro Arsip di APLIKASI SURAT.xlsm).
 * Setiap surat yang dibuat PDF-nya / dicetak tercatat: tanggal, nomor,
 * jenis, nama pemohon. Disimpan di SharedPreferences sebagai JSON —
 * penyimpanan PALING SEDERHANA yang sudah ada di proyek (tidak ada
 * server, tidak ada data warga yang dimuat sebelumnya).
 */
@Serializable
data class HistoryEntry(
    /** Id unik (waktu millis + jenis), dipakai untuk hapus per baris. */
    val id: String,
    val tanggal: String,
    val nomorSurat: String,
    val jenisSurat: String,
    val namaPemohon: String,
    val timestamp: Long
)

class HistoryStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun load(): List<HistoryEntry> {
        val raw = prefs.getString(KEY_ENTRIES, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<HistoryEntry>>(raw)
                .sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun add(entry: HistoryEntry) {
        val updated = (load() + entry).sortedByDescending { it.timestamp }
        prefs.edit().putString(KEY_ENTRIES, json.encodeToString(updated)).apply()
    }

    fun delete(id: String) {
        val updated = load().filter { it.id != id }
        prefs.edit().putString(KEY_ENTRIES, json.encodeToString(updated)).apply()
    }

    fun clearAll() {
        prefs.edit().remove(KEY_ENTRIES).apply()
    }

    companion object {
        private const val PREFS_NAME = "kasipem_history"
        private const val KEY_ENTRIES = "entries"
    }
}
