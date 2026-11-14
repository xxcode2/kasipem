package com.kasipem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasipem.data.HeaderData
import com.kasipem.ui.components.FormTextField
import com.kasipem.ui.components.FormDropdown

/**
 * Pengaturan data kop & pejabat.
 * Desa asli (Kasomalang Kulon) dan nama pejabat asli sebagai default:
 * Kades H. AMIRUDIN, S.Pd.I; Sekdes APIPUDIN, S.IP (user, 2026-11-14).
 * TIDAK ADA NIP: field NIP ada sebagai opsional kosong; bila kosong,
 * baris NIP tidak dirender di preview/PDF sama sekali.
 * Logo kop (Garuda + Subang) sudah disertakan di assets/kop/ dan
 * dipakai otomatis di preview & PDF; bila file logo tidak ada,
 * kop tetap tampil sebagai teks.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    headerData: HeaderData,
    onHeaderChange: (HeaderData) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(modifier = modifier.background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Data Kop & Pejabat",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Lokasi Desa/Kelurahan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FormTextField(
                    label = "Nama Kabupaten/Kota",
                    value = headerData.namaKabupaten,
                    onValueChange = { onHeaderChange(headerData.copy(namaKabupaten = it)) },
                    placeholder = "Contoh: KABUPATEN SUBANG"
                )

                FormTextField(
                    label = "Nama Kecamatan",
                    value = headerData.namaKecamatan,
                    onValueChange = { onHeaderChange(headerData.copy(namaKecamatan = it)) },
                    placeholder = "Contoh: KECAMATAN KASOMALANG"
                )

                FormTextField(
                    label = "Nama Desa/Kelurahan",
                    value = headerData.namaDesa,
                    onValueChange = { onHeaderChange(headerData.copy(namaDesa = it)) },
                    placeholder = "Contoh: KASOMALANG KULON"
                )

                FormTextField(
                    label = "Alamat Kantor",
                    value = headerData.alamatKantor,
                    onValueChange = { onHeaderChange(headerData.copy(alamatKantor = it)) },
                    placeholder = "Contoh: Jln. Raya Kasomalang No.01 Subang 41281",
                    singleLine = false,
                    maxLines = 3
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    "Kepala Desa (Penandatangan Utama)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FormTextField(
                    label = "Nama Kepala Desa",
                    value = headerData.namaPejabat,
                    onValueChange = { onHeaderChange(headerData.copy(namaPejabat = it)) },
                    placeholder = "H. AMIRUDIN, S.Pd.I"
                )

                FormDropdown(
                    label = "Jabatan",
                    value = headerData.jabatanPejabat,
                    options = listOf(
                        "Kepala Desa",
                        "Sekretaris Desa",
                        "Lurah",
                        "Sekretaris Kelurahan"
                    ),
                    onValueChange = { onHeaderChange(headerData.copy(jabatanPejabat = it)) }
                )

                FormTextField(
                    label = "NIP Kepala Desa",
                    value = headerData.nipPejabat,
                    onValueChange = { onHeaderChange(headerData.copy(nipPejabat = it)) },
                    placeholder = "Kosongkan — tidak ada NIP",
                    isOptional = true
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = headerData.atnPejabat,
                        onCheckedChange = { onHeaderChange(headerData.copy(atnPejabat = it)) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Atas Nama (A.n.) Kepala Desa/Lurah",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    "Sekretaris Desa (Penandatangan a.n. Kepala Desa — Surat Pisah KK)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FormTextField(
                    label = "Nama Sekretaris Desa",
                    value = headerData.namaSekdes,
                    onValueChange = { onHeaderChange(headerData.copy(namaSekdes = it)) },
                    placeholder = "APIPUDIN, S.IP"
                )

                FormTextField(
                    label = "NIP Sekretaris Desa",
                    value = headerData.nipSekdes,
                    onValueChange = { onHeaderChange(headerData.copy(nipSekdes = it)) },
                    placeholder = "Kosongkan — tidak ada NIP",
                    isOptional = true
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    "Logo Kop Surat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Logo Garuda Pancasila (kiri) dan logo Kabupaten Subang (kanan) sudah disertakan di dalam aplikasi (assets/kop/kades.jpg dan assets/kop/sekdes.png) dan tampil otomatis di pratinjau dan PDF. Bila file logo tidak ditemukan, kop tetap tampil sebagai teks.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Simpan & Tutup", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
