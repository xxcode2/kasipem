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
            // Header
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
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section: Lokasi
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
                    placeholder = "Contoh: KABUPATEN BANDUNG"
                )

                FormTextField(
                    label = "Nama Kecamatan",
                    value = headerData.namaKecamatan,
                    onValueChange = { onHeaderChange(headerData.copy(namaKecamatan = it)) },
                    placeholder = "Contoh: KECAMATAN RANCAEKEK"
                )

                FormTextField(
                    label = "Nama Desa/Kelurahan",
                    value = headerData.namaDesa,
                    onValueChange = { onHeaderChange(headerData.copy(namaDesa = it)) },
                    placeholder = "Contoh: DESA RANCAMANYAR"
                )

                FormTextField(
                    label = "Alamat Kantor",
                    value = headerData.alamatKantor,
                    onValueChange = { onHeaderChange(headerData.copy(alamatKantor = it)) },
                    placeholder = "Contoh: Jl. Raya Desa No. 123",
                    singleLine = false,
                    maxLines = 3
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Section: Pejabat Penandatangan
                Text(
                    "Penandatangan Surat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                FormTextField(
                    label = "Nama Pejabat",
                    value = headerData.namaPejabat,
                    onValueChange = { onHeaderChange(headerData.copy(namaPejabat = it)) },
                    placeholder = "Contoh: H. Nama Kepala Desa"
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
                    label = "NIP",
                    value = headerData.nipPejabat,
                    onValueChange = { onHeaderChange(headerData.copy(nipPejabat = it)) },
                    placeholder = "Contoh: 1234567890"
                )

                // A.n. Checkbox
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

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Action Button
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
