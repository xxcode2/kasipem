package com.kasipem.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasipem.data.*
import com.kasipem.ui.components.FormTextField
import com.kasipem.ui.components.FormDropdown

@Composable
fun DomisiliForm(
    data: DomisiliData,
    onDataChange: (DomisiliData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(
            label = "Nama Lengkap",
            value = data.namaLengkap,
            onValueChange = { onDataChange(data.copy(namaLengkap = it)) },
            placeholder = "Contoh: Budi Santoso"
        )

        FormTextField(
            label = "NIK",
            value = data.nik,
            onValueChange = { onDataChange(data.copy(nik = it)) },
            placeholder = "16XXXXXXXXXX"
        )

        FormDropdown(
            label = "Jenis Kelamin",
            value = data.jenisKelamin,
            options = listOf("", "Laki-laki", "Perempuan"),
            onValueChange = { onDataChange(data.copy(jenisKelamin = it)) },
            isOptional = true
        )

        FormTextField(
            label = "Tempat Lahir",
            value = data.tempatLahir,
            onValueChange = { onDataChange(data.copy(tempatLahir = it)) },
            placeholder = "Contoh: Bandung",
            isOptional = true
        )

        FormTextField(
            label = "Tanggal Lahir",
            value = data.tanggalLahir,
            onValueChange = { onDataChange(data.copy(tanggalLahir = it)) },
            isOptional = true
        )

        FormTextField(
            label = "Pekerjaan",
            value = data.pekerjaan,
            onValueChange = { onDataChange(data.copy(pekerjaan = it)) },
            placeholder = "Contoh: Wiraswasta",
            isOptional = true
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        FormTextField(
            label = "Alamat Asal (KTP)",
            value = data.alamatAsal,
            onValueChange = { onDataChange(data.copy(alamatAsal = it)) },
            placeholder = "Alamat lengkap di KTP",
            singleLine = false,
            maxLines = 3
        )

        FormTextField(
            label = "RT",
            value = data.rt,
            onValueChange = { onDataChange(data.copy(rt = it)) },
            placeholder = "001"
        )

        FormTextField(
            label = "RW",
            value = data.rw,
            onValueChange = { onDataChange(data.copy(rw = it)) },
            placeholder = "001"
        )

        FormTextField(
            label = "Alamat Domisili Saat Ini",
            value = data.alamatDomisili,
            onValueChange = { onDataChange(data.copy(alamatDomisili = it)) },
            placeholder = "Alamat saat ini",
            singleLine = false,
            maxLines = 3
        )

        FormTextField(
            label = "Keperluan Surat",
            value = data.keperluanSurat,
            onValueChange = { onDataChange(data.copy(keperluanSurat = it)) },
            placeholder = "Contoh: Pembuatan Rekening Bank"
        )
    }
}

@Composable
fun BelumMenikahForm(
    data: BelumMenikahData,
    onDataChange: (BelumMenikahData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(
            label = "Nama Lengkap",
            value = data.namaLengkap,
            onValueChange = { onDataChange(data.copy(namaLengkap = it)) }
        )

        FormTextField(
            label = "NIK",
            value = data.nik,
            onValueChange = { onDataChange(data.copy(nik = it)) }
        )

        FormDropdown(
            label = "Jenis Kelamin",
            value = data.jenisKelamin,
            options = listOf("", "Laki-laki", "Perempuan"),
            onValueChange = { onDataChange(data.copy(jenisKelamin = it)) },
            isOptional = true
        )

        FormTextField(
            label = "Tempat Lahir",
            value = data.tempatLahir,
            onValueChange = { onDataChange(data.copy(tempatLahir = it)) },
            isOptional = true
        )

        FormTextField(
            label = "Tanggal Lahir",
            value = data.tanggalLahir,
            onValueChange = { onDataChange(data.copy(tanggalLahir = it)) },
            isOptional = true
        )

        FormTextField(
            label = "Pekerjaan",
            value = data.pekerjaan,
            onValueChange = { onDataChange(data.copy(pekerjaan = it)) },
            isOptional = true
        )

        FormTextField(
            label = "Alamat Lengkap",
            value = data.alamatLengkap,
            onValueChange = { onDataChange(data.copy(alamatLengkap = it)) },
            singleLine = false,
            maxLines = 3
        )

        FormTextField(
            label = "RT",
            value = data.rt,
            onValueChange = { onDataChange(data.copy(rt = it)) }
        )

        FormTextField(
            label = "RW",
            value = data.rw,
            onValueChange = { onDataChange(data.copy(rw = it)) }
        )

        FormTextField(
            label = "Tujuan/Nama Instansi",
            value = data.tujuanSekolah,
            onValueChange = { onDataChange(data.copy(tujuanSekolah = it)) },
            placeholder = "Contoh: Universitas Indonesia"
        )

        FormTextField(
            label = "Keperluan",
            value = data.keperluan,
            onValueChange = { onDataChange(data.copy(keperluan = it)) },
            placeholder = "Contoh: Pendaftaran Mahasiswa Baru"
        )
    }
}

@Composable
fun TidakMampuForm(
    data: TidakMampuData,
    onDataChange: (TidakMampuData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(
            label = "Nama Lengkap",
            value = data.namaLengkap,
            onValueChange = { onDataChange(data.copy(namaLengkap = it)) }
        )

        FormTextField(
            label = "NIK",
            value = data.nik,
            onValueChange = { onDataChange(data.copy(nik = it)) }
        )

        FormDropdown(
            label = "Jenis Kelamin",
            value = data.jenisKelamin,
            options = listOf("", "Laki-laki", "Perempuan"),
            onValueChange = { onDataChange(data.copy(jenisKelamin = it)) },
            isOptional = true
        )

        FormTextField(
            label = "Tempat Lahir",
            value = data.tempatLahir,
            onValueChange = { onDataChange(data.copy(tempatLahir = it)) },
            isOptional = true
        )

        FormTextField(
            label = "Tanggal Lahir",
            value = data.tanggalLahir,
            onValueChange = { onDataChange(data.copy(tanggalLahir = it)) },
            isOptional = true
        )

        FormTextField(
            label = "Pekerjaan",
            value = data.pekerjaan,
            onValueChange = { onDataChange(data.copy(pekerjaan = it)) },
            isOptional = true
        )

        FormTextField(
            label = "Alamat Lengkap",
            value = data.alamatLengkap,
            onValueChange = { onDataChange(data.copy(alamatLengkap = it)) },
            singleLine = false,
            maxLines = 3
        )

        FormTextField(
            label = "RT",
            value = data.rt,
            onValueChange = { onDataChange(data.copy(rt = it)) }
        )

        FormTextField(
            label = "RW",
            value = data.rw,
            onValueChange = { onDataChange(data.copy(rw = it)) }
        )

        FormTextField(
            label = "Tujuan/Nama Sekolah/Instansi",
            value = data.tujuanSekolah,
            onValueChange = { onDataChange(data.copy(tujuanSekolah = it)) }
        )

        FormTextField(
            label = "Keperluan",
            value = data.keperluan,
            onValueChange = { onDataChange(data.copy(keperluan = it)) }
        )
    }
}

@Composable
fun BedaLuasForm(
    data: BedaLuasData,
    onDataChange: (BedaLuasData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(
            label = "Nama Lengkap",
            value = data.namaLengkap,
            onValueChange = { onDataChange(data.copy(namaLengkap = it)) }
        )

        FormTextField(
            label = "NIK",
            value = data.nik,
            onValueChange = { onDataChange(data.copy(nik = it)) }
        )

        FormTextField(
            label = "Alamat Lengkap",
            value = data.alamatLengkap,
            onValueChange = { onDataChange(data.copy(alamatLengkap = it)) },
            singleLine = false,
            maxLines = 3
        )

        FormTextField(
            label = "RT",
            value = data.rt,
            onValueChange = { onDataChange(data.copy(rt = it)) }
        )

        FormTextField(
            label = "RW",
            value = data.rw,
            onValueChange = { onDataChange(data.copy(rw = it)) }
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            "Data Sertifikat & SPPT",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        FormTextField(
            label = "Nomor Sertifikat",
            value = data.nomorSertifikat,
            onValueChange = { onDataChange(data.copy(nomorSertifikat = it)) }
        )

        FormTextField(
            label = "Luas Sertifikat (m²)",
            value = data.luasSertifikat,
            onValueChange = { onDataChange(data.copy(luasSertifikat = it)) }
        )

        FormTextField(
            label = "Nomor SPPT",
            value = data.nomorSPPT,
            onValueChange = { onDataChange(data.copy(nomorSPPT = it)) }
        )

        FormTextField(
            label = "Luas SPPT (m²)",
            value = data.luasSPPT,
            onValueChange = { onDataChange(data.copy(luasSPPT = it)) }
        )
    }
}

@Composable
fun UndanganForm(
    data: UndanganData,
    onDataChange: (UndanganData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormTextField(
            label = "Tujuan Yth. (Nama & Jabatan)",
            value = data.tujuanYth,
            onValueChange = { onDataChange(data.copy(tujuanYth = it)) },
            singleLine = false,
            maxLines = 3
        )

        FormTextField(
            label = "Hari",
            value = data.hari,
            onValueChange = { onDataChange(data.copy(hari = it)) },
            placeholder = "Contoh: Senin"
        )

        FormTextField(
            label = "Tanggal",
            value = data.tanggal,
            onValueChange = { onDataChange(data.copy(tanggal = it)) },
            placeholder = "Contoh: 15 Januari 2024"
        )

        FormTextField(
            label = "Pukul (Waktu)",
            value = data.pukul,
            onValueChange = { onDataChange(data.copy(pukul = it)) },
            placeholder = "Contoh: 09:00"
        )

        FormTextField(
            label = "Tempat",
            value = data.tempat,
            onValueChange = { onDataChange(data.copy(tempat = it)) },
            placeholder = "Lokasi acara",
            singleLine = false,
            maxLines = 3
        )

        FormTextField(
            label = "Agenda Acara",
            value = data.agendaAcara,
            onValueChange = { onDataChange(data.copy(agendaAcara = it)) },
            placeholder = "Uraian acara",
            singleLine = false,
            maxLines = 3
        )

        FormTextField(
            label = "Tembusan",
            value = data.daftarTembusan,
            onValueChange = { onDataChange(data.copy(daftarTembusan = it)) },
            placeholder = "Nama penerima tembusan",
            isOptional = true,
            singleLine = false,
            maxLines = 3
        )
    }
}
