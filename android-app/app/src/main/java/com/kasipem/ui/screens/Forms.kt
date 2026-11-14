package com.kasipem.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.kasipem.data.*
import com.kasipem.ui.components.FormTextField
import com.kasipem.ui.components.FormDropdown

/**
 * Dispatcher: render form untuk SATU tipe surat saja.
 * Dipakai HomeScreen saat satu kartu dipilih — tidak ada lagi
 * semua form ditampilkan sekaligus.
 */
@Composable
fun LetterForm(
    type: LetterType,
    letterData: Any,
    onDataChange: (Any) -> Unit,
) {
    when (type) {
        LetterType.DOMISILI -> DomisiliForm(
            data = letterData as DomisiliData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.PISAH_KK -> PisahKkForm(
            data = letterData as PisahKkData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.SKT_DAYAT -> SktDayatForm(
            data = letterData as SktDayatData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.BEDA_LUAS -> BedaLuasForm(
            data = letterData as BedaLuasData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.BEDA_NAMA -> BedaNamaForm(
            data = letterData as BedaNamaData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.BELUM_MENIKAH -> BelumMenikahForm(
            data = letterData as BelumMenikahData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.TIDAK_MAMPU -> TidakMampuForm(
            data = letterData as TidakMampuData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.AHLI_WARIS -> AhliWarisForm(
            data = letterData as AhliWarisData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.NIKAH_TIDAK_TERCATAT -> NikahTidakTercatatForm(
            data = letterData as NikahTidakTercatatData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.PEMAKAMAN -> PemakamanForm(
            data = letterData as PemakamanData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.TIDAK_MEMILIKI_RUMAH -> TidakMemilikiRumahForm(
            data = letterData as TidakMemilikiRumahData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.UNDANGAN -> UndanganForm(
            data = letterData as UndanganData,
            onDataChange = { onDataChange(it) }
        )
    }
}

/** Blok nomor + tanggal surat — ada di semua form. Tanggal kosong = hari ini. */
@Composable
private fun NomorTanggalFields(
    type: LetterType,
    nomorSurat: String,
    tanggalSurat: String,
    onNomorSuratChange: (String) -> Unit,
    onTanggalSuratChange: (String) -> Unit,
) {
    FormTextField(
        label = "Nomor Surat",
        value = nomorSurat,
        onValueChange = onNomorSuratChange,
        placeholder = defaultNomorSurat(type).ifEmpty { "Isi nomor surat secara manual" }
    )
    FormTextField(
        label = "Tanggal Surat",
        value = tanggalSurat,
        onValueChange = onTanggalSuratChange,
        placeholder = todayId(),
        isOptional = true
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun NikField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    FormTextField(
        label = "NIK",
        value = value,
        onValueChange = onValueChange,
        placeholder = "16 digit angka",
        isRequired = true,
        error = nikError(value),
        keyboardType = KeyboardType.Number
    )
}

@Composable
private fun NamaField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Nama Lengkap",
) {
    FormTextField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        placeholder = "Nama lengkap sesuai KTP",
        isRequired = true
    )
}

@Composable
private fun JenisKelaminField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    FormDropdown(
        label = "Jenis Kelamin",
        value = value,
        options = listOf("", "Laki-laki", "Perempuan"),
        onValueChange = onValueChange,
        isOptional = true
    )
}

@Composable
fun DomisiliForm(
    data: DomisiliData,
    onDataChange: (DomisiliData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.DOMISILI,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        JenisKelaminField(value = data.jenisKelamin, onValueChange = { onDataChange(data.copy(jenisKelamin = it)) })
        FormTextField(
            label = "Tempat Lahir",
            value = data.tempatLahir,
            onValueChange = { onDataChange(data.copy(tempatLahir = it)) },
            placeholder = "Contoh: Subang",
            isOptional = true
        )
        FormTextField(
            label = "Tanggal Lahir",
            value = data.tanggalLahir,
            onValueChange = { onDataChange(data.copy(tanggalLahir = it)) },
            placeholder = "Contoh: 12 Januari 1990",
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
            placeholder = "001",
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "RW",
            value = data.rw,
            onValueChange = { onDataChange(data.copy(rw = it)) },
            placeholder = "001",
            keyboardType = KeyboardType.Number
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
fun PisahKkForm(
    data: PisahKkData,
    onDataChange: (PisahKkData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.PISAH_KK,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        SectionTitle("Orang Pertama (yang pisah KK)")
        NamaField(label = "Nama 1", value = data.nama1, onValueChange = { onDataChange(data.copy(nama1 = it)) })
        NikField(value = data.nik1, onValueChange = { onDataChange(data.copy(nik1 = it)) })
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Orang Kedua (kepala keluarga / anggota KK lama)")
        NamaField(label = "Nama 2", value = data.nama2, onValueChange = { onDataChange(data.copy(nama2 = it)) })
        NikField(value = data.nik2, onValueChange = { onDataChange(data.copy(nik2 = it)) })
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        FormTextField(
            label = "Nomor KK Lama",
            value = data.nomorKKLama,
            onValueChange = { onDataChange(data.copy(nomorKKLama = it)) },
            placeholder = "16 digit nomor KK",
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "Alamat",
            value = data.alamat,
            onValueChange = { onDataChange(data.copy(alamat = it)) },
            placeholder = "Alamat lengkap",
            singleLine = false,
            maxLines = 3
        )
    }
}

@Composable
fun SktDayatForm(
    data: SktDayatData,
    onDataChange: (SktDayatData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.SKT_DAYAT,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
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
            label = "Alamat",
            value = data.alamat,
            onValueChange = { onDataChange(data.copy(alamat = it)) },
            singleLine = false,
            maxLines = 3
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Data Tanah & Bangunan")
        FormTextField(
            label = "Luas Tanah (m²)",
            value = data.luasTanah,
            onValueChange = { onDataChange(data.copy(luasTanah = it)) },
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "Luas Bangunan (m²)",
            value = data.luasBangunan,
            onValueChange = { onDataChange(data.copy(luasBangunan = it)) },
            keyboardType = KeyboardType.Number,
            isOptional = true
        )
        FormTextField(
            label = "SPPT / NOP",
            value = data.spptNop,
            onValueChange = { onDataChange(data.copy(spptNop = it)) },
            placeholder = "Nomor Objek Pajak SPPT PBB"
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Batas-batas Tanah")
        FormTextField(
            label = "Batas Utara",
            value = data.batasUtara,
            onValueChange = { onDataChange(data.copy(batasUtara = it)) },
            placeholder = "Contoh: Tanah milik ..."
        )
        FormTextField(
            label = "Batas Timur",
            value = data.batasTimur,
            onValueChange = { onDataChange(data.copy(batasTimur = it)) }
        )
        FormTextField(
            label = "Batas Selatan",
            value = data.batasSelatan,
            onValueChange = { onDataChange(data.copy(batasSelatan = it)) }
        )
        FormTextField(
            label = "Batas Barat",
            value = data.batasBarat,
            onValueChange = { onDataChange(data.copy(batasBarat = it)) }
        )
    }
}

@Composable
fun BedaLuasForm(
    data: BedaLuasData,
    onDataChange: (BedaLuasData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.BEDA_LUAS,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        FormTextField(
            label = "Alamat",
            value = data.alamat,
            onValueChange = { onDataChange(data.copy(alamat = it)) },
            singleLine = false,
            maxLines = 3
        )
        FormTextField(
            label = "RT",
            value = data.rt,
            onValueChange = { onDataChange(data.copy(rt = it)) },
            placeholder = "001",
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "RW",
            value = data.rw,
            onValueChange = { onDataChange(data.copy(rw = it)) },
            placeholder = "001",
            keyboardType = KeyboardType.Number
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Data Sertifikat & SPPT")
        FormTextField(
            label = "Nomor Sertifikat",
            value = data.nomorSertifikat,
            onValueChange = { onDataChange(data.copy(nomorSertifikat = it)) }
        )
        FormTextField(
            label = "Luas Sertifikat (m²)",
            value = data.luasSertifikat,
            onValueChange = { onDataChange(data.copy(luasSertifikat = it)) },
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "Nomor SPPT",
            value = data.nomorSPPT,
            onValueChange = { onDataChange(data.copy(nomorSPPT = it)) }
        )
        FormTextField(
            label = "Luas SPPT (m²)",
            value = data.luasSPPT,
            onValueChange = { onDataChange(data.copy(luasSPPT = it)) },
            keyboardType = KeyboardType.Number
        )
    }
}

@Composable
fun BedaNamaForm(
    data: BedaNamaData,
    onDataChange: (BedaNamaData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.BEDA_NAMA,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        FormTextField(
            label = "Nama di Dokumen 1",
            value = data.namaDokumen1,
            onValueChange = { onDataChange(data.copy(namaDokumen1 = it)) },
            placeholder = "Contoh: nama di KTP"
        )
        FormTextField(
            label = "Nama di Dokumen 2",
            value = data.namaDokumen2,
            onValueChange = { onDataChange(data.copy(namaDokumen2 = it)) },
            placeholder = "Contoh: nama di Akta Kelahiran"
        )
        FormTextField(
            label = "Alasan / Keterangan",
            value = data.alasan,
            onValueChange = { onDataChange(data.copy(alasan = it)) },
            placeholder = "Jelaskan perbedaan namanya",
            singleLine = false,
            maxLines = 3
        )
    }
}

@Composable
fun BelumMenikahForm(
    data: BelumMenikahData,
    onDataChange: (BelumMenikahData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.BELUM_MENIKAH,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        JenisKelaminField(value = data.jenisKelamin, onValueChange = { onDataChange(data.copy(jenisKelamin = it)) })
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
            label = "Alamat",
            value = data.alamat,
            onValueChange = { onDataChange(data.copy(alamat = it)) },
            singleLine = false,
            maxLines = 3
        )
        FormTextField(
            label = "RT",
            value = data.rt,
            onValueChange = { onDataChange(data.copy(rt = it)) },
            placeholder = "001",
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "RW",
            value = data.rw,
            onValueChange = { onDataChange(data.copy(rw = it)) },
            placeholder = "001",
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "Keperluan",
            value = data.keperluan,
            onValueChange = { onDataChange(data.copy(keperluan = it)) },
            placeholder = "Contoh: Persyaratan menikah"
        )
    }
}

@Composable
fun TidakMampuForm(
    data: TidakMampuData,
    onDataChange: (TidakMampuData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.TIDAK_MAMPU,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        JenisKelaminField(value = data.jenisKelamin, onValueChange = { onDataChange(data.copy(jenisKelamin = it)) })
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
            label = "Alamat",
            value = data.alamat,
            onValueChange = { onDataChange(data.copy(alamat = it)) },
            singleLine = false,
            maxLines = 3
        )
        FormTextField(
            label = "RT",
            value = data.rt,
            onValueChange = { onDataChange(data.copy(rt = it)) },
            placeholder = "001",
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "RW",
            value = data.rw,
            onValueChange = { onDataChange(data.copy(rw = it)) },
            placeholder = "001",
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "Penghasilan Bulanan (Rp)",
            value = data.penghasilanBulanan,
            onValueChange = { onDataChange(data.copy(penghasilanBulanan = it)) },
            placeholder = "Contoh: 500000",
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "Keperluan",
            value = data.keperluan,
            onValueChange = { onDataChange(data.copy(keperluan = it)) },
            placeholder = "Contoh: Pengajuan bantuan"
        )
        FormTextField(
            label = "Tujuan (Sekolah / Instansi)",
            value = data.tujuan,
            onValueChange = { onDataChange(data.copy(tujuan = it)) },
            placeholder = "Contoh: SMPN Negeri Kasomalang",
            isOptional = true
        )
    }
}

@Composable
fun AhliWarisForm(
    data: AhliWarisData,
    onDataChange: (AhliWarisData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.AHLI_WARIS,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        SectionTitle("Data Almarhum/almarhumah (Peninggal)")
        NamaField(label = "Nama Peninggal", value = data.namaPeninggal, onValueChange = { onDataChange(data.copy(namaPeninggal = it)) })
        FormTextField(
            label = "NIK Peninggal",
            value = data.nikPeninggal,
            onValueChange = { onDataChange(data.copy(nikPeninggal = it)) },
            placeholder = "16 digit angka",
            error = nikError(data.nikPeninggal),
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "Alamat Peninggal",
            value = data.alamatPeninggal,
            onValueChange = { onDataChange(data.copy(alamatPeninggal = it)) },
            singleLine = false,
            maxLines = 3
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Ahli Waris 1")
        NamaField(label = "Nama Ahli Waris 1", value = data.namaAhliWaris1, onValueChange = { onDataChange(data.copy(namaAhliWaris1 = it)) })
        FormTextField(
            label = "NIK Ahli Waris 1",
            value = data.nikAhliWaris1,
            onValueChange = { onDataChange(data.copy(nikAhliWaris1 = it)) },
            placeholder = "16 digit angka",
            error = nikError(data.nikAhliWaris1),
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "Hubungan dengan Peninggal",
            value = data.hubunganAhliWaris1,
            onValueChange = { onDataChange(data.copy(hubunganAhliWaris1 = it)) },
            placeholder = "Contoh: Anak kandung"
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Ahli Waris 2 (opsional)")
        FormTextField(
            label = "Nama Ahli Waris 2",
            value = data.namaAhliWaris2,
            onValueChange = { onDataChange(data.copy(namaAhliWaris2 = it)) },
            isOptional = true
        )
        FormTextField(
            label = "NIK Ahli Waris 2",
            value = data.nikAhliWaris2,
            onValueChange = { onDataChange(data.copy(nikAhliWaris2 = it)) },
            placeholder = "16 digit angka",
            isOptional = true,
            error = nikError(data.nikAhliWaris2),
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "Hubungan dengan Peninggal",
            value = data.hubunganAhliWaris2,
            onValueChange = { onDataChange(data.copy(hubunganAhliWaris2 = it)) },
            placeholder = "Contoh: Istri",
            isOptional = true
        )
        FormTextField(
            label = "Keterangan",
            value = data.keterangan,
            onValueChange = { onDataChange(data.copy(keterangan = it)) },
            placeholder = "Contoh: Untuk pembuatan sertipikat",
            isOptional = true,
            singleLine = false,
            maxLines = 3
        )
    }
}

@Composable
fun NikahTidakTercatatForm(
    data: NikahTidakTercatatData,
    onDataChange: (NikahTidakTercatatData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.NIKAH_TIDAK_TERCATAT,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        SectionTitle("Pihak Pria")
        NamaField(label = "Nama Pria", value = data.namaPria, onValueChange = { onDataChange(data.copy(namaPria = it)) })
        FormTextField(
            label = "NIK Pria",
            value = data.nikPria,
            onValueChange = { onDataChange(data.copy(nikPria = it)) },
            placeholder = "16 digit angka",
            isRequired = true,
            error = nikError(data.nikPria),
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "Tempat Lahir Pria",
            value = data.tempatLahirPria,
            onValueChange = { onDataChange(data.copy(tempatLahirPria = it)) },
            isOptional = true
        )
        FormTextField(
            label = "Tanggal Lahir Pria",
            value = data.tanggalLahirPria,
            onValueChange = { onDataChange(data.copy(tanggalLahirPria = it)) },
            isOptional = true
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Pihak Wanita")
        NamaField(label = "Nama Wanita", value = data.namaWanita, onValueChange = { onDataChange(data.copy(namaWanita = it)) })
        FormTextField(
            label = "NIK Wanita",
            value = data.nikWanita,
            onValueChange = { onDataChange(data.copy(nikWanita = it)) },
            placeholder = "16 digit angka",
            isRequired = true,
            error = nikError(data.nikWanita),
            keyboardType = KeyboardType.Number
        )
        FormTextField(
            label = "Tempat Lahir Wanita",
            value = data.tempatLahirWanita,
            onValueChange = { onDataChange(data.copy(tempatLahirWanita = it)) },
            isOptional = true
        )
        FormTextField(
            label = "Tanggal Lahir Wanita",
            value = data.tanggalLahirWanita,
            onValueChange = { onDataChange(data.copy(tanggalLahirWanita = it)) },
            isOptional = true
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Pernikahan")
        FormTextField(
            label = "Tanggal Pernikahan",
            value = data.tanggalPernikahan,
            onValueChange = { onDataChange(data.copy(tanggalPernikahan = it)) },
            placeholder = "Contoh: 7 November 2025"
        )
        FormTextField(
            label = "Tempat Pernikahan",
            value = data.tempatPernikahan,
            onValueChange = { onDataChange(data.copy(tempatPernikahan = it)) },
            placeholder = "Contoh: Kp. Karihkil RT 006/001",
            singleLine = false,
            maxLines = 3
        )
    }
}

@Composable
fun PemakamanForm(
    data: PemakamanData,
    onDataChange: (PemakamanData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.PEMAKAMAN,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        NamaField(label = "Nama Almarhum/almarhumah", value = data.namaAlmarhum, onValueChange = { onDataChange(data.copy(namaAlmarhum = it)) })
        FormTextField(
            label = "NIK Almarhum/almarhumah",
            value = data.nikAlmarhum,
            onValueChange = { onDataChange(data.copy(nikAlmarhum = it)) },
            placeholder = "16 digit angka",
            error = nikError(data.nikAlmarhum),
            keyboardType = KeyboardType.Number
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
            label = "Alamat",
            value = data.alamat,
            onValueChange = { onDataChange(data.copy(alamat = it)) },
            singleLine = false,
            maxLines = 3
        )
        FormTextField(
            label = "Tanggal Meninggal",
            value = data.tanggalMeninggal,
            onValueChange = { onDataChange(data.copy(tanggalMeninggal = it)) },
            placeholder = "Contoh: 6 Juli 2025"
        )
        FormTextField(
            label = "Lokasi Makam",
            value = data.lokasiMakam,
            onValueChange = { onDataChange(data.copy(lokasiMakam = it)) },
            placeholder = "Contoh: Pemakaman Umum Askid RT 03 RW 06",
            singleLine = false,
            maxLines = 3
        )
        FormTextField(
            label = "Nama Keluarga (penanggung jawab)",
            value = data.namaKeluarga,
            onValueChange = { onDataChange(data.copy(namaKeluarga = it)) },
            isOptional = true
        )
    }
}

@Composable
fun TidakMemilikiRumahForm(
    data: TidakMemilikiRumahData,
    onDataChange: (TidakMemilikiRumahData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.TIDAK_MEMILIKI_RUMAH,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa:",
            style = MaterialTheme.typography.bodyMedium
        )
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
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
            label = "Alamat Tinggal",
            value = data.alamatTinggal,
            onValueChange = { onDataChange(data.copy(alamatTinggal = it)) },
            singleLine = false,
            maxLines = 3
        )
        FormTextField(
            label = "Keterangan",
            value = data.keterangan,
            onValueChange = { onDataChange(data.copy(keterangan = it)) },
            placeholder = "Keterangan tambahan bila ada",
            isOptional = true,
            singleLine = false,
            maxLines = 3
        )
    }
}

@Composable
fun UndanganForm(
    data: UndanganData,
    onDataChange: (UndanganData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.UNDANGAN,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        FormTextField(
            label = "Tujuan Yth. (Nama & Jabatan)",
            value = data.tujuanYth,
            onValueChange = { onDataChange(data.copy(tujuanYth = it)) },
            placeholder = "Contoh: Bapak Camat Kasomalang",
            isRequired = true,
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
            placeholder = "Contoh: 15"
        )
        FormTextField(
            label = "Bulan",
            value = data.bulan,
            onValueChange = { onDataChange(data.copy(bulan = it)) },
            placeholder = "Contoh: November"
        )
        FormTextField(
            label = "Tahun",
            value = data.tahun,
            onValueChange = { onDataChange(data.copy(tahun = it)) },
            placeholder = "Contoh: 2026",
            keyboardType = KeyboardType.Number
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
            placeholder = "Contoh: Camat Kasomalang; Ketua BPD",
            isOptional = true,
            singleLine = false,
            maxLines = 3
        )
    }
}
