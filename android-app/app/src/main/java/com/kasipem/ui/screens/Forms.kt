package com.kasipem.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
        // ---- Fase 2: 18 tipe baru ----
        LetterType.KEMATIAN -> KematianForm(
            data = letterData as KematianData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.USAHA -> UsahaForm(
            data = letterData as UsahaData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.DUDA_JANDA -> DudaJandaForm(
            data = letterData as DudaJandaData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.F101 -> F101Form(
            data = letterData as F101Data,
            onDataChange = { onDataChange(it) }
        )
        LetterType.F102 -> F102Form(
            data = letterData as F102Data,
            onDataChange = { onDataChange(it) }
        )
        LetterType.F103 -> F103Form(
            data = letterData as F103Data,
            onDataChange = { onDataChange(it) }
        )
        LetterType.F104 -> F104Form(
            data = letterData as F104Data,
            onDataChange = { onDataChange(it) }
        )
        LetterType.F105 -> F105Form(
            data = letterData as F105Data,
            onDataChange = { onDataChange(it) }
        )
        LetterType.F106 -> F106Form(
            data = letterData as F106Data,
            onDataChange = { onDataChange(it) }
        )
        LetterType.F115 -> F115Form(
            data = letterData as F115Data,
            onDataChange = { onDataChange(it) }
        )
        LetterType.F201 -> F201Form(
            data = letterData as F201Data,
            onDataChange = { onDataChange(it) }
        )
        LetterType.F203 -> F203Form(
            data = letterData as F203Data,
            onDataChange = { onDataChange(it) }
        )
        LetterType.F204 -> F204Form(
            data = letterData as F204Data,
            onDataChange = { onDataChange(it) }
        )
        LetterType.SPTJM_KEMATIAN -> SptjmKematianForm(
            data = letterData as SptjmKematianData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.SPTJM_PERKAWINAN_PERCERAIAN -> SptjmPerkawinanPerceraianForm(
            data = letterData as SptjmPerkawinanPerceraianData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.PBI_ASESMEN -> PbiAsesmenForm(
            data = letterData as PbiAsesmenData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.PBI_PERNYATAAN -> PbiPernyataanForm(
            data = letterData as PbiPernyataanData,
            onDataChange = { onDataChange(it) }
        )
        LetterType.PBI_TIDAK_PUNYA_REKENING -> PbiTidakPunyaRekeningForm(
            data = letterData as PbiTidakPunyaRekeningData,
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

/* ==================================================================
   Helper Fase 2 — pintasan field + editor daftar (anggota/anak/dll).
   ================================================================== */

/** Pintasan field teks untuk form Fase 2 (lebih ringkas dari FormTextField langsung). */
@Composable
private fun F(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    placeholder: String = "",
    optional: Boolean = false,
    required: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    error: String? = null
) {
    FormTextField(
        label = label,
        value = value,
        onValueChange = onChange,
        placeholder = placeholder,
        isOptional = optional,
        isRequired = required,
        keyboardType = keyboardType,
        singleLine = singleLine,
        maxLines = maxLines,
        error = error
    )
}

/** Pintasan dropdown untuk form Fase 2. */
@Composable
private fun D(
    label: String,
    value: String,
    options: List<String>,
    onChange: (String) -> Unit,
    optional: Boolean = false
) {
    FormDropdown(label = label, value = value, options = options, onValueChange = onChange, isOptional = optional)
}

/** Pasangan field Tempat Lahir + Tanggal Lahir. */
@Composable
private fun TtlFields(
    tempatLahir: String,
    tanggalLahir: String,
    onTempatLahirChange: (String) -> Unit,
    onTanggalLahirChange: (String) -> Unit
) {
    F("Tempat Lahir", tempatLahir, onTempatLahirChange, placeholder = "Contoh: Subang", optional = true)
    F("Tanggal Lahir", tanggalLahir, onTanggalLahirChange, placeholder = "Contoh: 12 Januari 1990", optional = true)
}

/** Field NIK dengan validasi peringatan (bukan blokir). */
@Composable
private fun NikF(value: String, onChange: (String) -> Unit, label: String = "NIK") {
    F(label, value, onChange, placeholder = "16 digit angka", required = true, keyboardType = KeyboardType.Number, error = nikError(value))
}

/** Baris saksi 1 + 2 (nama + NIK). */
@Composable
private fun SaksiFields(
    namaSaksi1: String, nikSaksi1: String, namaSaksi2: String, nikSaksi2: String,
    onNamaSaksi1Change: (String) -> Unit, onNikSaksi1Change: (String) -> Unit,
    onNamaSaksi2Change: (String) -> Unit, onNikSaksi2Change: (String) -> Unit
) {
    SectionTitle("Saksi-saksi")
    F("Nama Saksi 1", namaSaksi1, onNamaSaksi1Change)
    NikF(value = nikSaksi1, onChange = onNikSaksi1Change, label = "NIK Saksi 1")
    F("Nama Saksi 2", namaSaksi2, onNamaSaksi2Change)
    NikF(value = nikSaksi2, onChange = onNikSaksi2Change, label = "NIK Saksi 2")
}

/** Editor daftar anggota keluarga F-1.01 (maks. 10 anggota). */
@Composable
private fun AnggotaKeluargaEditor(
    list: List<AnggotaKeluarga>,
    onChange: (List<AnggotaKeluarga>) -> Unit
) {
    fun upd(index: Int, f: (AnggotaKeluarga, String) -> AnggotaKeluarga): (String) -> Unit =
        { v -> onChange(list.mapIndexed { i, x -> if (i == index) f(x, v) else x }) }
    list.forEachIndexed { index, a ->
        SectionTitle("Anggota ${index + 1}")
        F("Nama", a.nama, upd(index) { x, v -> x.copy(nama = v) }, required = true)
        NikF(value = a.nik, onChange = upd(index) { x, v -> x.copy(nik = v) })
        D("SHDK", a.shdk, listOf("", "Kepala Keluarga", "Istri", "Suami", "Anak", "Menantu", "Cucu", "Orang Tua", "Mertua", "Famili Lain", "Pembantu", "Lainnya"), upd(index) { x, v -> x.copy(shdk = v) }, optional = true)
        F("Tempat Lahir", a.tempatLahir, upd(index) { x, v -> x.copy(tempatLahir = v) }, optional = true)
        F("Tanggal Lahir", a.tanggalLahir, upd(index) { x, v -> x.copy(tanggalLahir = v) }, optional = true)
        D("Agama", a.agama, listOf("", "Islam", "Kristen", "Katolik", "Hindu", "Buddha", "Konghucu", "Kepercayaan"), upd(index) { x, v -> x.copy(agama = v) }, optional = true)
        D("Pendidikan", a.pendidikan, listOf("", "Tidak/Belum Sekolah", "Belum Tamat SD", "SD", "SLTP", "SLTA", "D1", "D2", "D3", "S1", "S2", "S3"), upd(index) { x, v -> x.copy(pendidikan = v) }, optional = true)
        F("Pekerjaan", a.pekerjaan, upd(index) { x, v -> x.copy(pekerjaan = v) }, optional = true)
        D("Status Kawin", a.statusKawin, listOf("", "Belum Kawin", "Kawin", "Cerai Hidup", "Cerai Mati"), upd(index) { x, v -> x.copy(statusKawin = v) }, optional = true)
        F("Nama Ayah", a.namaAyah, upd(index) { x, v -> x.copy(namaAyah = v) }, optional = true)
        F("Nama Ibu", a.namaIbu, upd(index) { x, v -> x.copy(namaIbu = v) }, optional = true)
        if (list.size > 1) {
            OutlinedButton(onClick = { onChange(list.filterIndexed { i, _ -> i != index }) }, modifier = Modifier.fillMaxWidth()) {
                Text("Hapus Anggota ${index + 1}")
            }
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
    if (list.size < 10) {
        OutlinedButton(onClick = { onChange(list + AnggotaKeluarga()) }, modifier = Modifier.fillMaxWidth()) {
            Text("Tambah Anggota")
        }
    }
}

/** Editor daftar anak (F-1.05 / SPTJM Perkawinan-Perceraian). */
@Composable
private fun AnakEditor(
    list: List<AnakData>,
    onChange: (List<AnakData>) -> Unit
) {
    fun upd(index: Int, f: (AnakData, String) -> AnakData): (String) -> Unit =
        { v -> onChange(list.mapIndexed { i, x -> if (i == index) f(x, v) else x }) }
    list.forEachIndexed { index, a ->
        SectionTitle("Anak ${index + 1}")
        F("Nama Anak", a.nama, upd(index) { x, v -> x.copy(nama = v) })
        F("No. Akta Kelahiran", a.noAktaKelahiran, upd(index) { x, v -> x.copy(noAktaKelahiran = v) }, optional = true)
        F("SHDK", a.shdk, upd(index) { x, v -> x.copy(shdk = v) }, placeholder = "Contoh: Anak kandung", optional = true)
        if (list.size > 1) {
            OutlinedButton(onClick = { onChange(list.filterIndexed { i, _ -> i != index }) }, modifier = Modifier.fillMaxWidth()) {
                Text("Hapus Anak ${index + 1}")
            }
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
    OutlinedButton(onClick = { onChange(list + AnakData()) }, modifier = Modifier.fillMaxWidth()) {
        Text("Tambah Anak")
    }
}

/** Editor daftar anggota yang ikut pindah (F-1.03). */
@Composable
private fun AnggotaPindahEditor(
    list: List<AnggotaPindah>,
    onChange: (List<AnggotaPindah>) -> Unit
) {
    fun upd(index: Int, f: (AnggotaPindah, String) -> AnggotaPindah): (String) -> Unit =
        { v -> onChange(list.mapIndexed { i, x -> if (i == index) f(x, v) else x }) }
    list.forEachIndexed { index, a ->
        SectionTitle("Anggota Pindah ${index + 1}")
        NikF(value = a.nik, onChange = upd(index) { x, v -> x.copy(nik = v) })
        F("Nama", a.nama, upd(index) { x, v -> x.copy(nama = v) }, required = true)
        F("SHDK", a.shdk, upd(index) { x, v -> x.copy(shdk = v) }, placeholder = "Contoh: Anak", optional = true)
        if (list.size > 1) {
            OutlinedButton(onClick = { onChange(list.filterIndexed { i, _ -> i != index }) }, modifier = Modifier.fillMaxWidth()) {
                Text("Hapus Anggota ${index + 1}")
            }
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
    OutlinedButton(onClick = { onChange(list + AnggotaPindah()) }, modifier = Modifier.fillMaxWidth()) {
        Text("Tambah Anggota Pindah")
    }
}

/** Editor tabel anggota keluarga F-1.06 (maks. 7 baris). */
@Composable
private fun AnggotaPerubahanEditor(
    list: List<AnggotaPerubahan>,
    onChange: (List<AnggotaPerubahan>) -> Unit
) {
    fun upd(index: Int, f: (AnggotaPerubahan, String) -> AnggotaPerubahan): (String) -> Unit =
        { v -> onChange(list.mapIndexed { i, x -> if (i == index) f(x, v) else x }) }
    list.forEachIndexed { index, a ->
        SectionTitle("Baris ${index + 1}")
        F("Nama", a.nama, upd(index) { x, v -> x.copy(nama = v) })
        F("Perubahan", a.perubahan, upd(index) { x, v -> x.copy(perubahan = v) }, placeholder = "Contoh: Nama semula → nama menjadi", singleLine = false, maxLines = 2, optional = true)
        if (list.size > 1) {
            OutlinedButton(onClick = { onChange(list.filterIndexed { i, _ -> i != index }) }, modifier = Modifier.fillMaxWidth()) {
                Text("Hapus Baris ${index + 1}")
            }
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
    if (list.size < 7) {
        OutlinedButton(onClick = { onChange(list + AnggotaPerubahan()) }, modifier = Modifier.fillMaxWidth()) {
            Text("Tambah Baris")
        }
    }
}

/** Editor daftar anggota keluarga F-1.15. */
@Composable
private fun AnggotaKkBaruEditor(
    list: List<AnggotaKkBaru>,
    onChange: (List<AnggotaKkBaru>) -> Unit
) {
    fun upd(index: Int, f: (AnggotaKkBaru, String) -> AnggotaKkBaru): (String) -> Unit =
        { v -> onChange(list.mapIndexed { i, x -> if (i == index) f(x, v) else x }) }
    list.forEachIndexed { index, a ->
        SectionTitle("Anggota ${index + 1}")
        NikF(value = a.nik, onChange = upd(index) { x, v -> x.copy(nik = v) })
        F("Nama", a.nama, upd(index) { x, v -> x.copy(nama = v) }, required = true)
        F("SHDK", a.shdk, upd(index) { x, v -> x.copy(shdk = v) }, placeholder = "Contoh: Anak", optional = true)
        if (list.size > 1) {
            OutlinedButton(onClick = { onChange(list.filterIndexed { i, _ -> i != index }) }, modifier = Modifier.fillMaxWidth()) {
                Text("Hapus Anggota ${index + 1}")
            }
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
    OutlinedButton(onClick = { onChange(list + AnggotaKkBaru()) }, modifier = Modifier.fillMaxWidth()) {
        Text("Tambah Anggota")
    }
}

/** Editor daftar warga SKTM Kolektif. */
@Composable
private fun WargaKolektifEditor(
    list: List<WargaKolektif>,
    onChange: (List<WargaKolektif>) -> Unit
) {
    fun upd(index: Int, f: (WargaKolektif, String) -> WargaKolektif): (String) -> Unit =
        { v -> onChange(list.mapIndexed { i, x -> if (i == index) f(x, v) else x }) }
    if (list.isEmpty()) {
        Text("Belum ada warga. Tekan \"Tambah Warga\" di bawah.", style = MaterialTheme.typography.bodyMedium)
    }
    list.forEachIndexed { index, w ->
        SectionTitle("Warga ${index + 1}")
        F("Nama", w.nama, upd(index) { x, v -> x.copy(nama = v) }, required = true)
        NikF(value = w.nik, onChange = upd(index) { x, v -> x.copy(nik = v) })
        F("Alamat", w.alamat, upd(index) { x, v -> x.copy(alamat = v) }, singleLine = false, maxLines = 2, optional = true)
        OutlinedButton(onClick = { onChange(list.filterIndexed { i, _ -> i != index }) }, modifier = Modifier.fillMaxWidth()) {
            Text("Hapus Warga ${index + 1}")
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
    OutlinedButton(onClick = { onChange(list + WargaKolektif()) }, modifier = Modifier.fillMaxWidth()) {
        Text("Tambah Warga")
    }
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
        // Fase 2 — varian domisili: Perorangan / Lembaga / Perusahaan (judul surat berubah).
        D(
            label = "Jenis Domisili",
            value = data.jenisDomisili,
            options = listOf("Perorangan", "Lembaga", "Perusahaan"),
            onChange = { onDataChange(data.copy(jenisDomisili = it)) }
        )
        when (data.jenisDomisili) {
            "Lembaga" -> {
                F("Nama Lembaga", data.namaLembaga, { onDataChange(data.copy(namaLembaga = it)) }, required = true)
                F("Nama Pimpinan", data.namaPimpinan, { onDataChange(data.copy(namaPimpinan = it)) }, required = true)
                F("Alamat", data.alamatOrganisasi, { onDataChange(data.copy(alamatOrganisasi = it)) }, placeholder = "Alamat lembaga", singleLine = false, maxLines = 3)
                F("Keperluan Surat", data.keperluanSurat, { onDataChange(data.copy(keperluanSurat = it)) }, placeholder = "Contoh: Pembuatan rekening bank")
            }
            "Perusahaan" -> {
                F("Nama Perusahaan", data.namaPerusahaan, { onDataChange(data.copy(namaPerusahaan = it)) }, required = true)
                F("Nama Pimpinan", data.namaPimpinan, { onDataChange(data.copy(namaPimpinan = it)) }, required = true)
                F("Nama Sekretaris", data.namaSekretaris, { onDataChange(data.copy(namaSekretaris = it)) }, optional = true)
                F("Bidang Usaha", data.bidangUsaha, { onDataChange(data.copy(bidangUsaha = it)) }, placeholder = "Contoh: Perdagangan")
                F("Alamat", data.alamatOrganisasi, { onDataChange(data.copy(alamatOrganisasi = it)) }, placeholder = "Alamat perusahaan", singleLine = false, maxLines = 3)
                F("Keperluan Surat", data.keperluanSurat, { onDataChange(data.copy(keperluanSurat = it)) }, placeholder = "Contoh: Pembuatan rekening bank")
            }
            else -> {
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
        // Fase 2 — varian SKTM: Umum / Sekolah / Daftar Kolektif (judul surat berubah).
        D(
            label = "Varian SKTM",
            value = data.varianSKTM,
            options = listOf("Umum", "Sekolah", "Daftar Kolektif"),
            onChange = { onDataChange(data.copy(varianSKTM = it)) }
        )
        if (data.varianSKTM == "Daftar Kolektif") {
            Text(
                "Satu surat untuk banyak warga (daftar kolektif).",
                style = MaterialTheme.typography.bodyMedium
            )
            WargaKolektifEditor(data.daftarKolektif) { onDataChange(data.copy(daftarKolektif = it)) }
            FormTextField(
                label = "Keperluan",
                value = data.keperluan,
                onValueChange = { onDataChange(data.copy(keperluan = it)) },
                placeholder = "Contoh: Bansos Program Jabar Caang"
            )
        } else {
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

/* ==================================================================
   Fase 2 — 18 form baru.
   ================================================================== */

@Composable
fun KematianForm(
    data: KematianData,
    onDataChange: (KematianData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.KEMATIAN,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        SectionTitle("Data Almarhum/almarhumah")
        NamaField(label = "Nama Almarhum/almarhumah", value = data.namaAlmarhum, onValueChange = { onDataChange(data.copy(namaAlmarhum = it)) })
        NikF(value = data.nikAlmarhum, onChange = { onDataChange(data.copy(nikAlmarhum = it)) })
        JenisKelaminField(value = data.jenisKelamin, onValueChange = { onDataChange(data.copy(jenisKelamin = it)) })
        F("Agama", data.agama, { onDataChange(data.copy(agama = it)) }, placeholder = "Contoh: Islam", optional = true)
        TtlFields(data.tempatLahir, data.tanggalLahir, { onDataChange(data.copy(tempatLahir = it)) }, { onDataChange(data.copy(tanggalLahir = it)) })
        F("Pekerjaan", data.pekerjaan, { onDataChange(data.copy(pekerjaan = it)) }, optional = true)
        F("Alamat", data.alamat, { onDataChange(data.copy(alamat = it)) }, singleLine = false, maxLines = 3)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Keterangan Meninggal")
        F("Hari Meninggal", data.hariMeninggal, { onDataChange(data.copy(hariMeninggal = it)) }, placeholder = "Contoh: Rabu")
        F("Tanggal Meninggal", data.tanggalMeninggal, { onDataChange(data.copy(tanggalMeninggal = it)) }, placeholder = "Contoh: 11 Agustus 2002")
        F("Tempat Meninggal", data.tempatMeninggal, { onDataChange(data.copy(tempatMeninggal = it)) }, placeholder = "Contoh: Rumah duka")
        F("Penyebab Meninggal", data.penyebabMeninggal, { onDataChange(data.copy(penyebabMeninggal = it)) }, placeholder = "Contoh: Sakit")
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Pelapor")
        NamaField(label = "Nama Pelapor", value = data.namaPelapor, onValueChange = { onDataChange(data.copy(namaPelapor = it)) })
        NikF(value = data.nikPelapor, onChange = { onDataChange(data.copy(nikPelapor = it)) }, label = "NIK Pelapor")
        TtlFields(data.tempatLahirPelapor, data.tanggalLahirPelapor, { onDataChange(data.copy(tempatLahirPelapor = it)) }, { onDataChange(data.copy(tanggalLahirPelapor = it)) })
        F("Pekerjaan Pelapor", data.pekerjaanPelapor, { onDataChange(data.copy(pekerjaanPelapor = it)) }, optional = true)
        F("Alamat Pelapor", data.alamatPelapor, { onDataChange(data.copy(alamatPelapor = it)) }, singleLine = false, maxLines = 3)
        F("Hubungan dengan Almarhum", data.hubunganPelapor, { onDataChange(data.copy(hubunganPelapor = it)) }, placeholder = "Contoh: Istri")
    }
}

@Composable
fun UsahaForm(
    data: UsahaData,
    onDataChange: (UsahaData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.USAHA,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        TtlFields(data.tempatLahir, data.tanggalLahir, { onDataChange(data.copy(tempatLahir = it)) }, { onDataChange(data.copy(tanggalLahir = it)) })
        F("Pekerjaan", data.pekerjaan, { onDataChange(data.copy(pekerjaan = it)) }, optional = true)
        F("Alamat", data.alamat, { onDataChange(data.copy(alamat = it)) }, singleLine = false, maxLines = 3)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Data Usaha")
        F("Nama Usaha", data.namaUsaha, { onDataChange(data.copy(namaUsaha = it)) }, placeholder = "Contoh: Warung Jajanan", required = true)
        F("Alamat Usaha", data.alamatUsaha, { onDataChange(data.copy(alamatUsaha = it)) }, placeholder = "Tempat usaha berada", singleLine = false, maxLines = 3)
    }
}

@Composable
fun DudaJandaForm(
    data: DudaJandaData,
    onDataChange: (DudaJandaData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.DUDA_JANDA,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        D("Status", data.statusDudaJanda, listOf("Duda", "Janda"), { onDataChange(data.copy(statusDudaJanda = it)) })
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        TtlFields(data.tempatLahir, data.tanggalLahir, { onDataChange(data.copy(tempatLahir = it)) }, { onDataChange(data.copy(tanggalLahir = it)) })
        F("Pekerjaan", data.pekerjaan, { onDataChange(data.copy(pekerjaan = it)) }, optional = true)
        F("Alamat", data.alamat, { onDataChange(data.copy(alamat = it)) }, singleLine = false, maxLines = 3)
        F("Keterangan Ketua RT", data.keteranganRT, { onDataChange(data.copy(keteranganRT = it)) }, placeholder = "Keterangan dari Ketua RT setempat", singleLine = false, maxLines = 3, optional = true)
    }
}

@Composable
fun F101Form(
    data: F101Data,
    onDataChange: (F101Data) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.F101,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "Formulir Disdukcapil — kop Dinas Kependudukan dan Pencatatan Sipil Kab. Subang (teks saja, tanpa logo desa).",
            style = MaterialTheme.typography.bodyMedium
        )
        SectionTitle("Data Kepala Keluarga")
        NamaField(label = "Nama Kepala Keluarga", value = data.namaKepalaKeluarga, onValueChange = { onDataChange(data.copy(namaKepalaKeluarga = it)) })
        NikF(value = data.nikKepalaKeluarga, onChange = { onDataChange(data.copy(nikKepalaKeluarga = it)) }, label = "NIK Kepala Keluarga")
        F("No. KK", data.noKK, { onDataChange(data.copy(noKK = it)) }, placeholder = "16 digit nomor KK", keyboardType = KeyboardType.Number)
        F("Alamat", data.alamatKeluarga, { onDataChange(data.copy(alamatKeluarga = it)) }, singleLine = false, maxLines = 3)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Daftar Anggota Keluarga (maks. 10)")
        AnggotaKeluargaEditor(data.daftarAnggota) { onDataChange(data.copy(daftarAnggota = it)) }
    }
}

@Composable
fun F102Form(
    data: F102Data,
    onDataChange: (F102Data) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.F102,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "Formulir Disdukcapil — kop Dinas Kependudukan dan Pencatatan Sipil Kab. Subang (teks saja, tanpa logo desa).",
            style = MaterialTheme.typography.bodyMedium
        )
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        F("No. KK", data.noKK, { onDataChange(data.copy(noKK = it)) }, placeholder = "16 digit nomor KK", keyboardType = KeyboardType.Number)
        D(
            "Jenis Permohonan",
            data.jenisPermohonan,
            listOf("", "KK baru", "KK perubahan", "KK hilang-rusak", "KTP-el baru", "KTP-el pindah", "KTP-el hilang-rusak", "Perpanjangan ITAP", "KIA", "Perubahan data"),
            { onDataChange(data.copy(jenisPermohonan = it)) }
        )
        F("Daftar Lampiran", data.daftarLampiran, { onDataChange(data.copy(daftarLampiran = it)) }, placeholder = "Contoh: Fotokopi KK; Surat keterangan hilang", singleLine = false, maxLines = 3, optional = true)
    }
}

@Composable
fun F103Form(
    data: F103Data,
    onDataChange: (F103Data) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.F103,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "Formulir Disdukcapil — kop Dinas Kependudukan dan Pencatatan Sipil Kab. Subang (teks saja, tanpa logo desa).",
            style = MaterialTheme.typography.bodyMedium
        )
        F("No. KK", data.noKK, { onDataChange(data.copy(noKK = it)) }, placeholder = "16 digit nomor KK", keyboardType = KeyboardType.Number)
        NamaField(label = "Nama Pemohon", value = data.namaPemohon, onValueChange = { onDataChange(data.copy(namaPemohon = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        D("Jenis Permohonan", data.jenisPermohonan, listOf("", "SKP", "SKPLN", "SKTT"), { onDataChange(data.copy(jenisPermohonan = it)) }, optional = true)
        F("Alamat Asal", data.alamatAsal, { onDataChange(data.copy(alamatAsal = it)) }, singleLine = false, maxLines = 3)
        F("RT", data.rt, { onDataChange(data.copy(rt = it)) }, placeholder = "001", keyboardType = KeyboardType.Number)
        F("RW", data.rw, { onDataChange(data.copy(rw = it)) }, placeholder = "001", keyboardType = KeyboardType.Number)
        D("Klasifikasi Kepindahan", data.klasifikasiPindah, listOf("", "Dalam desa", "Antar desa", "Antar kecamatan", "Antar kabupaten", "Antar provinsi"), { onDataChange(data.copy(klasifikasiPindah = it)) }, optional = true)
        F("Alamat Pindah", data.alamatPindah, { onDataChange(data.copy(alamatPindah = it)) }, singleLine = false, maxLines = 3)
        F("Alasan Pindah", data.alasanPindah, { onDataChange(data.copy(alasanPindah = it)) }, placeholder = "Contoh: Pekerjaan", singleLine = false, maxLines = 2, optional = true)
        D("Jenis Kepindahan", data.jenisKepindahan, listOf("", "Kepala Keluarga", "Kepala Keluarga dan seluruh anggota keluarga", "Kepala Keluarga dan sebagian anggota keluarga", "Anggota keluarga"), { onDataChange(data.copy(jenisKepindahan = it)) }, optional = true)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Anggota Keluarga yang Pindah")
        AnggotaPindahEditor(data.daftarAnggotaPindah) { onDataChange(data.copy(daftarAnggotaPindah = it)) }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        F("Anggota Keluarga yang Tidak Pindah", data.anggotaTidakPindah, { onDataChange(data.copy(anggotaTidakPindah = it)) }, placeholder = "Nama / NIK anggota yang tidak ikut pindah", singleLine = false, maxLines = 3, optional = true)
    }
}

@Composable
fun F104Form(
    data: F104Data,
    onDataChange: (F104Data) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.F104,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "Formulir Disdukcapil — surat pernyataan bermaterai cukup.",
            style = MaterialTheme.typography.bodyMedium
        )
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        F("Alamat", data.alamat, { onDataChange(data.copy(alamat = it)) }, singleLine = false, maxLines = 3)
        TtlFields(data.tempatLahir, data.tanggalLahir, { onDataChange(data.copy(tempatLahir = it)) }, { onDataChange(data.copy(tanggalLahir = it)) })
        F("Nama Ibu", data.namaIbu, { onDataChange(data.copy(namaIbu = it)) })
    }
}

@Composable
fun F105Form(
    data: F105Data,
    onDataChange: (F105Data) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.F105,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "Formulir Disdukcapil — SPTJM bermaterai cukup.",
            style = MaterialTheme.typography.bodyMedium
        )
        SectionTitle("Suami")
        NamaField(label = "Nama Suami", value = data.namaSuami, onValueChange = { onDataChange(data.copy(namaSuami = it)) })
        NikF(value = data.nikSuami, onChange = { onDataChange(data.copy(nikSuami = it)) }, label = "NIK Suami")
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Istri")
        NamaField(label = "Nama Istri", value = data.namaIstri, onValueChange = { onDataChange(data.copy(namaIstri = it)) })
        NikF(value = data.nikIstri, onChange = { onDataChange(data.copy(nikIstri = it)) }, label = "NIK Istri")
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Tanggal Perkawinan")
        F("Tanggal", data.tanggalPerkawinan, { onDataChange(data.copy(tanggalPerkawinan = it)) }, placeholder = "Contoh: 5")
        F("Bulan", data.bulanPerkawinan, { onDataChange(data.copy(bulanPerkawinan = it)) }, placeholder = "Contoh: Oktober")
        F("Tahun", data.tahunPerkawinan, { onDataChange(data.copy(tahunPerkawinan = it)) }, placeholder = "Contoh: 1965", keyboardType = KeyboardType.Number)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SaksiFields(
            namaSaksi1 = data.namaSaksi1, nikSaksi1 = data.nikSaksi1,
            namaSaksi2 = data.namaSaksi2, nikSaksi2 = data.nikSaksi2,
            onNamaSaksi1Change = { onDataChange(data.copy(namaSaksi1 = it)) },
            onNikSaksi1Change = { onDataChange(data.copy(nikSaksi1 = it)) },
            onNamaSaksi2Change = { onDataChange(data.copy(namaSaksi2 = it)) },
            onNikSaksi2Change = { onDataChange(data.copy(nikSaksi2 = it)) }
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Daftar Anak")
        AnakEditor(data.daftarAnak) { onDataChange(data.copy(daftarAnak = it)) }
    }
}

@Composable
fun F106Form(
    data: F106Data,
    onDataChange: (F106Data) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.F106,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "Formulir Disdukcapil — surat pernyataan bermaterai cukup.",
            style = MaterialTheme.typography.bodyMedium
        )
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        F("No. KK", data.noKK, { onDataChange(data.copy(noKK = it)) }, placeholder = "16 digit nomor KK", keyboardType = KeyboardType.Number)
        F("Alamat", data.alamat, { onDataChange(data.copy(alamat = it)) }, singleLine = false, maxLines = 3)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Anggota Keluarga yang Mengalami Perubahan (maks. 7)")
        AnggotaPerubahanEditor(data.tabelAnggota) { onDataChange(data.copy(tabelAnggota = it)) }
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Perubahan Pendidikan")
        F("Semula", data.pendidikanSemula, { onDataChange(data.copy(pendidikanSemula = it)) }, optional = true)
        F("Menjadi", data.pendidikanMenjadi, { onDataChange(data.copy(pendidikanMenjadi = it)) }, optional = true)
        F("Dasar Perubahan", data.pendidikanDasar, { onDataChange(data.copy(pendidikanDasar = it)) }, placeholder = "Contoh: Ijazah", optional = true)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Perubahan Pekerjaan")
        F("Semula", data.pekerjaanSemula, { onDataChange(data.copy(pekerjaanSemula = it)) }, optional = true)
        F("Menjadi", data.pekerjaanMenjadi, { onDataChange(data.copy(pekerjaanMenjadi = it)) }, optional = true)
        F("Dasar Perubahan", data.pekerjaanDasar, { onDataChange(data.copy(pekerjaanDasar = it)) }, placeholder = "Contoh: Surat keterangan kerja", optional = true)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        F("Perubahan Agama", data.perubahanAgama, { onDataChange(data.copy(perubahanAgama = it)) }, placeholder = "Contoh: Islam menjadi Kristen", optional = true)
    }
}

@Composable
fun F115Form(
    data: F115Data,
    onDataChange: (F115Data) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.F115,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "Formulir Disdukcapil — kop Dinas Kependudukan dan Pencatatan Sipil Kab. Subang (teks saja, tanpa logo desa).",
            style = MaterialTheme.typography.bodyMedium
        )
        NamaField(label = "Nama Pemohon", value = data.namaPemohon, onValueChange = { onDataChange(data.copy(namaPemohon = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        F("No. KK Lama", data.noKKLama, { onDataChange(data.copy(noKKLama = it)) }, placeholder = "16 digit nomor KK lama", keyboardType = KeyboardType.Number, optional = true)
        F("Alamat", data.alamat, { onDataChange(data.copy(alamat = it)) }, singleLine = false, maxLines = 3)
        F("RT", data.rt, { onDataChange(data.copy(rt = it)) }, placeholder = "001", keyboardType = KeyboardType.Number)
        F("RW", data.rw, { onDataChange(data.copy(rw = it)) }, placeholder = "001", keyboardType = KeyboardType.Number)
        D("Alasan Permohonan", data.alasanPermohonan, listOf("", "Membentuk RT baru", "KK hilang/rusak", "Lainnya"), { onDataChange(data.copy(alasanPermohonan = it)) }, optional = true)
        F("Jumlah Anggota", data.jumlahAnggota, { onDataChange(data.copy(jumlahAnggota = it)) }, placeholder = "Contoh: 4", keyboardType = KeyboardType.Number, optional = true)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Daftar Anggota Keluarga")
        AnggotaKkBaruEditor(data.daftarAnggota) { onDataChange(data.copy(daftarAnggota = it)) }
    }
}

@Composable
fun F201Form(
    data: F201Data,
    onDataChange: (F201Data) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.F201,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "Formulir Disdukcapil — hanya seksi jenis pelaporan yang dipilih yang tampil di bawah ini.",
            style = MaterialTheme.typography.bodyMedium
        )
        D("Jenis Pelaporan", data.jenisPelaporan, F201_JENIS_PELAPORAN, { onDataChange(data.copy(jenisPelaporan = it)) })
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Data Pelapor")
        NamaField(label = "Nama Pelapor", value = data.pelaporNama, onValueChange = { onDataChange(data.copy(pelaporNama = it)) })
        NikF(value = data.pelaporNik, onChange = { onDataChange(data.copy(pelaporNik = it)) }, label = "NIK Pelapor")
        F("Alamat Pelapor", data.pelaporAlamat, { onDataChange(data.copy(pelaporAlamat = it)) }, singleLine = false, maxLines = 3)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Subjek Akta")
        F("Nama Subjek Akta I", data.subjek1Nama, { onDataChange(data.copy(subjek1Nama = it)) })
        NikF(value = data.subjek1Nik, onChange = { onDataChange(data.copy(subjek1Nik = it)) }, label = "NIK Subjek Akta I")
        F("Nama Subjek Akta II", data.subjek2Nama, { onDataChange(data.copy(subjek2Nama = it)) }, optional = true)
        NikF(value = data.subjek2Nik, onChange = { onDataChange(data.copy(subjek2Nik = it)) }, label = "NIK Subjek Akta II")
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SaksiFields(
            namaSaksi1 = data.saksi1Nama, nikSaksi1 = data.saksi1Nik,
            namaSaksi2 = data.saksi2Nama, nikSaksi2 = data.saksi2Nik,
            onNamaSaksi1Change = { onDataChange(data.copy(saksi1Nama = it)) },
            onNikSaksi1Change = { onDataChange(data.copy(saksi1Nik = it)) },
            onNamaSaksi2Change = { onDataChange(data.copy(saksi2Nama = it)) },
            onNikSaksi2Change = { onDataChange(data.copy(saksi2Nik = it)) }
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Orang Tua")
        F("Nama Ayah", data.ayahNama, { onDataChange(data.copy(ayahNama = it)) }, optional = true)
        NikF(value = data.ayahNik, onChange = { onDataChange(data.copy(ayahNik = it)) }, label = "NIK Ayah")
        F("Nama Ibu", data.ibuNama, { onDataChange(data.copy(ibuNama = it)) }, optional = true)
        NikF(value = data.ibuNik, onChange = { onDataChange(data.copy(ibuNik = it)) }, label = "NIK Ibu")
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        // Seksi kondisional — hanya seksi jenis pelaporan yang dipilih yang tampil.
        when (f201Section(data.jenisPelaporan)) {
            "anak" -> {
                SectionTitle("Data Anak (Kelahiran)")
                F("Nama Anak", data.anakNama, { onDataChange(data.copy(anakNama = it)) }, required = true)
                F("Tempat Lahir Anak", data.anakTempatLahir, { onDataChange(data.copy(anakTempatLahir = it)) }, optional = true)
                F("Tanggal Lahir Anak", data.anakTanggalLahir, { onDataChange(data.copy(anakTanggalLahir = it)) }, optional = true)
                D("Jenis Kelamin Anak", data.anakJenisKelamin, listOf("", "Laki-laki", "Perempuan"), { onDataChange(data.copy(anakJenisKelamin = it)) }, optional = true)
                F("Anak Ke", data.anakKe, { onDataChange(data.copy(anakKe = it)) }, placeholder = "Contoh: 1", keyboardType = KeyboardType.Number, optional = true)
            }
            "lahirMati" -> {
                SectionTitle("Data Lahir Mati")
                F("Nama", data.lahirMatiNama, { onDataChange(data.copy(lahirMatiNama = it)) }, required = true)
                F("Tanggal", data.lahirMatiTanggal, { onDataChange(data.copy(lahirMatiTanggal = it)) })
                F("Pukul", data.lahirMatiPukul, { onDataChange(data.copy(lahirMatiPukul = it)) }, placeholder = "Contoh: 08:30", optional = true)
                F("Tempat", data.lahirMatiTempat, { onDataChange(data.copy(lahirMatiTempat = it)) }, optional = true)
                F("Sebab", data.lahirMatiSebab, { onDataChange(data.copy(lahirMatiSebab = it)) }, optional = true)
            }
            "pasangan" -> {
                SectionTitle("Data Perkawinan / Perceraian")
                F("Nama Suami", data.pasanganSuamiNama, { onDataChange(data.copy(pasanganSuamiNama = it)) })
                F("Nama Istri", data.pasanganIstriNama, { onDataChange(data.copy(pasanganIstriNama = it)) })
                F("Tanggal Peristiwa", data.tanggalPeristiwa, { onDataChange(data.copy(tanggalPeristiwa = it)) }, placeholder = "Contoh: 5 Oktober 1965")
                F("Tempat Peristiwa", data.tempatPeristiwa, { onDataChange(data.copy(tempatPeristiwa = it)) }, optional = true)
            }
            "kematian" -> {
                SectionTitle("Data Kematian")
                NikF(value = data.meninggalNik, onChange = { onDataChange(data.copy(meninggalNik = it)) }, label = "NIK yang Meninggal")
                F("Nama yang Meninggal", data.meninggalNama, { onDataChange(data.copy(meninggalNama = it)) }, required = true)
                F("Tanggal Kematian", data.meninggalTanggal, { onDataChange(data.copy(meninggalTanggal = it)) })
                F("Pukul", data.meninggalPukul, { onDataChange(data.copy(meninggalPukul = it)) }, placeholder = "Contoh: 08:30", optional = true)
                F("Sebab", data.meninggalSebab, { onDataChange(data.copy(meninggalSebab = it)) }, optional = true)
                F("Tempat", data.meninggalTempat, { onDataChange(data.copy(meninggalTempat = it)) }, optional = true)
                F("Yang Menerangkan", data.meninggalYangMenerangkan, { onDataChange(data.copy(meninggalYangMenerangkan = it)) }, placeholder = "Contoh: Dokter / Bidan", optional = true)
            }
            else -> {
                SectionTitle("Keterangan Peristiwa")
                F("Keterangan Peristiwa", data.keteranganPeristiwa, { onDataChange(data.copy(keteranganPeristiwa = it)) }, placeholder = "Uraian peristiwa yang dilaporkan", singleLine = false, maxLines = 3, optional = true)
            }
        }
    }
}

@Composable
fun F203Form(
    data: F203Data,
    onDataChange: (F203Data) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.F203,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "Formulir Disdukcapil — SPTJM bermaterai cukup.",
            style = MaterialTheme.typography.bodyMedium
        )
        SectionTitle("Yang Menyatakan")
        NamaField(label = "Nama Yang Menyatakan", value = data.namaYangMenyatakan, onValueChange = { onDataChange(data.copy(namaYangMenyatakan = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        TtlFields(data.tempatLahir, data.tanggalLahir, { onDataChange(data.copy(tempatLahir = it)) }, { onDataChange(data.copy(tanggalLahir = it)) })
        F("Pekerjaan", data.pekerjaan, { onDataChange(data.copy(pekerjaan = it)) }, optional = true)
        F("Alamat", data.alamat, { onDataChange(data.copy(alamat = it)) }, singleLine = false, maxLines = 3)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Data Anak")
        NamaField(label = "Nama Anak", value = data.namaAnak, onValueChange = { onDataChange(data.copy(namaAnak = it)) })
        NikF(value = data.nikAnak, onChange = { onDataChange(data.copy(nikAnak = it)) }, label = "NIK Anak")
        TtlFields(data.tempatLahirAnak, data.tanggalLahirAnak, { onDataChange(data.copy(tempatLahirAnak = it)) }, { onDataChange(data.copy(tanggalLahirAnak = it)) })
        D("Jenis Kelamin Anak", data.jenisKelaminAnak, listOf("", "Laki-laki", "Perempuan"), { onDataChange(data.copy(jenisKelaminAnak = it)) }, optional = true)
        F("Anak Ke", data.anakKe, { onDataChange(data.copy(anakKe = it)) }, placeholder = "Contoh: 1", keyboardType = KeyboardType.Number, optional = true)
        F("Alamat Anak", data.alamatAnak, { onDataChange(data.copy(alamatAnak = it)) }, singleLine = false, maxLines = 3, optional = true)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Data Ibu")
        NamaField(label = "Nama Ibu", value = data.namaIbu, onValueChange = { onDataChange(data.copy(namaIbu = it)) })
        NikF(value = data.nikIbu, onChange = { onDataChange(data.copy(nikIbu = it)) }, label = "NIK Ibu")
        TtlFields(data.tempatLahirIbu, data.tanggalLahirIbu, { onDataChange(data.copy(tempatLahirIbu = it)) }, { onDataChange(data.copy(tanggalLahirIbu = it)) })
        F("Pekerjaan Ibu", data.pekerjaanIbu, { onDataChange(data.copy(pekerjaanIbu = it)) }, optional = true)
        F("Alamat Ibu", data.alamatIbu, { onDataChange(data.copy(alamatIbu = it)) }, singleLine = false, maxLines = 3)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Penolong Kelahiran")
        NamaField(label = "Nama Penolong", value = data.namaPenolong, onValueChange = { onDataChange(data.copy(namaPenolong = it)) })
        NikF(value = data.nikPenolong, onChange = { onDataChange(data.copy(nikPenolong = it)) }, label = "NIK Penolong")
        F("Pekerjaan Penolong", data.pekerjaanPenolong, { onDataChange(data.copy(pekerjaanPenolong = it)) }, optional = true)
        F("Alamat Penolong", data.alamatPenolong, { onDataChange(data.copy(alamatPenolong = it)) }, singleLine = false, maxLines = 3, optional = true)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SaksiFields(
            namaSaksi1 = data.namaSaksi1, nikSaksi1 = data.nikSaksi1,
            namaSaksi2 = data.namaSaksi2, nikSaksi2 = data.nikSaksi2,
            onNamaSaksi1Change = { onDataChange(data.copy(namaSaksi1 = it)) },
            onNikSaksi1Change = { onDataChange(data.copy(nikSaksi1 = it)) },
            onNamaSaksi2Change = { onDataChange(data.copy(namaSaksi2 = it)) },
            onNikSaksi2Change = { onDataChange(data.copy(nikSaksi2 = it)) }
        )
    }
}

@Composable
fun F204Form(
    data: F204Data,
    onDataChange: (F204Data) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.F204,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "Formulir Disdukcapil — SPTJM bermaterai cukup.",
            style = MaterialTheme.typography.bodyMedium
        )
        SectionTitle("Yang Menyatakan")
        NamaField(label = "Nama Yang Menyatakan", value = data.namaYangMenyatakan, onValueChange = { onDataChange(data.copy(namaYangMenyatakan = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        TtlFields(data.tempatLahir, data.tanggalLahir, { onDataChange(data.copy(tempatLahir = it)) }, { onDataChange(data.copy(tanggalLahir = it)) })
        F("Pekerjaan", data.pekerjaan, { onDataChange(data.copy(pekerjaan = it)) }, optional = true)
        F("Alamat", data.alamat, { onDataChange(data.copy(alamat = it)) }, singleLine = false, maxLines = 3)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Pasangan")
        NamaField(label = "Nama Pasangan", value = data.namaPasangan, onValueChange = { onDataChange(data.copy(namaPasangan = it)) })
        NikF(value = data.nikPasangan, onChange = { onDataChange(data.copy(nikPasangan = it)) }, label = "NIK Pasangan")
        TtlFields(data.tempatLahirPasangan, data.tanggalLahirPasangan, { onDataChange(data.copy(tempatLahirPasangan = it)) }, { onDataChange(data.copy(tanggalLahirPasangan = it)) })
        F("Pekerjaan Pasangan", data.pekerjaanPasangan, { onDataChange(data.copy(pekerjaanPasangan = it)) }, optional = true)
        F("Alamat Pasangan", data.alamatPasangan, { onDataChange(data.copy(alamatPasangan = it)) }, singleLine = false, maxLines = 3, optional = true)
        F("No. KK", data.noKK, { onDataChange(data.copy(noKK = it)) }, placeholder = "16 digit nomor KK", keyboardType = KeyboardType.Number, optional = true)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SaksiFields(
            namaSaksi1 = data.namaSaksi1, nikSaksi1 = data.nikSaksi1,
            namaSaksi2 = data.namaSaksi2, nikSaksi2 = data.nikSaksi2,
            onNamaSaksi1Change = { onDataChange(data.copy(namaSaksi1 = it)) },
            onNikSaksi1Change = { onDataChange(data.copy(nikSaksi1 = it)) },
            onNamaSaksi2Change = { onDataChange(data.copy(namaSaksi2 = it)) },
            onNikSaksi2Change = { onDataChange(data.copy(nikSaksi2 = it)) }
        )
    }
}

@Composable
fun SptjmKematianForm(
    data: SptjmKematianData,
    onDataChange: (SptjmKematianData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.SPTJM_KEMATIAN,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "SPTJM bermaterai cukup — ditandatangani Yang Menyatakan, 2 saksi, Ketua RT, Ketua RW, dan Kepala Desa/Lurah.",
            style = MaterialTheme.typography.bodyMedium
        )
        SectionTitle("Yang Menyatakan")
        NamaField(label = "Nama Yang Menyatakan", value = data.namaYangMenyatakan, onValueChange = { onDataChange(data.copy(namaYangMenyatakan = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        TtlFields(data.tempatLahir, data.tanggalLahir, { onDataChange(data.copy(tempatLahir = it)) }, { onDataChange(data.copy(tanggalLahir = it)) })
        F("Pekerjaan", data.pekerjaan, { onDataChange(data.copy(pekerjaan = it)) }, optional = true)
        F("Alamat", data.alamat, { onDataChange(data.copy(alamat = it)) }, singleLine = false, maxLines = 3)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Data Almarhum/almarhumah")
        NamaField(label = "Nama Almarhum/almarhumah", value = data.namaAlmarhum, onValueChange = { onDataChange(data.copy(namaAlmarhum = it)) })
        NikF(value = data.nikAlmarhum, onChange = { onDataChange(data.copy(nikAlmarhum = it)) }, label = "NIK Almarhum/almarhumah")
        TtlFields(data.tempatLahirAlmarhum, data.tanggalLahirAlmarhum, { onDataChange(data.copy(tempatLahirAlmarhum = it)) }, { onDataChange(data.copy(tanggalLahirAlmarhum = it)) })
        F("Alamat Terakhir (sesuai KTP)", data.alamatAlmarhum, { onDataChange(data.copy(alamatAlmarhum = it)) }, singleLine = false, maxLines = 3)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Keterangan Meninggal")
        F("Hari Meninggal", data.hariMeninggal, { onDataChange(data.copy(hariMeninggal = it)) }, placeholder = "Contoh: Rabu")
        F("Tanggal Meninggal", data.tanggalMeninggal, { onDataChange(data.copy(tanggalMeninggal = it)) }, placeholder = "Contoh: 11 Agustus 2002")
        F("Jam Meninggal", data.jamMeninggal, { onDataChange(data.copy(jamMeninggal = it)) }, placeholder = "Contoh: 08:30", optional = true)
        F("Tempat Meninggal", data.tempatMeninggal, { onDataChange(data.copy(tempatMeninggal = it)) }, placeholder = "Contoh: Rumah duka")
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SaksiFields(
            namaSaksi1 = data.namaSaksi1, nikSaksi1 = data.nikSaksi1,
            namaSaksi2 = data.namaSaksi2, nikSaksi2 = data.nikSaksi2,
            onNamaSaksi1Change = { onDataChange(data.copy(namaSaksi1 = it)) },
            onNikSaksi1Change = { onDataChange(data.copy(nikSaksi1 = it)) },
            onNamaSaksi2Change = { onDataChange(data.copy(namaSaksi2 = it)) },
            onNikSaksi2Change = { onDataChange(data.copy(nikSaksi2 = it)) }
        )
    }
}

@Composable
fun SptjmPerkawinanPerceraianForm(
    data: SptjmPerkawinanPerceraianData,
    onDataChange: (SptjmPerkawinanPerceraianData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.SPTJM_PERKAWINAN_PERCERAIAN,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "SPTJM bermaterai cukup — pilih Perkawinan atau Perceraian.",
            style = MaterialTheme.typography.bodyMedium
        )
        D("Jenis SPTJM", data.jenisSPTJM, listOf("Perkawinan", "Perceraian"), { onDataChange(data.copy(jenisSPTJM = it)) })
        SectionTitle("Suami")
        NamaField(label = "Nama Suami", value = data.namaSuami, onValueChange = { onDataChange(data.copy(namaSuami = it)) })
        NikF(value = data.nikSuami, onChange = { onDataChange(data.copy(nikSuami = it)) }, label = "NIK Suami")
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Istri")
        NamaField(label = "Nama Istri", value = data.namaIstri, onValueChange = { onDataChange(data.copy(namaIstri = it)) })
        NikF(value = data.nikIstri, onChange = { onDataChange(data.copy(nikIstri = it)) }, label = "NIK Istri")
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        F("Tanggal ${data.jenisSPTJM}", data.tanggalPeristiwa, { onDataChange(data.copy(tanggalPeristiwa = it)) }, placeholder = "Contoh: 5 Oktober 1965")
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SaksiFields(
            namaSaksi1 = data.namaSaksi1, nikSaksi1 = data.nikSaksi1,
            namaSaksi2 = data.namaSaksi2, nikSaksi2 = data.nikSaksi2,
            onNamaSaksi1Change = { onDataChange(data.copy(namaSaksi1 = it)) },
            onNikSaksi1Change = { onDataChange(data.copy(nikSaksi1 = it)) },
            onNamaSaksi2Change = { onDataChange(data.copy(namaSaksi2 = it)) },
            onNikSaksi2Change = { onDataChange(data.copy(nikSaksi2 = it)) }
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Daftar Anak")
        AnakEditor(data.daftarAnak) { onDataChange(data.copy(daftarAnak = it)) }
    }
}

@Composable
fun PbiAsesmenForm(
    data: PbiAsesmenData,
    onDataChange: (PbiAsesmenData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.PBI_ASESMEN,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "Instrumen asesmen calon penerima Bansos KIS-PBI/SKM — diisi petugas bersama pemohon.",
            style = MaterialTheme.typography.bodyMedium
        )
        SectionTitle("Identitas Calon Penerima")
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        TtlFields(data.tempatLahir, data.tanggalLahir, { onDataChange(data.copy(tempatLahir = it)) }, { onDataChange(data.copy(tanggalLahir = it)) })
        JenisKelaminField(value = data.jenisKelamin, onValueChange = { onDataChange(data.copy(jenisKelamin = it)) })
        F("Status Kawin", data.statusKawin, { onDataChange(data.copy(statusKawin = it)) }, placeholder = "Contoh: Kawin", optional = true)
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        F("No. KK", data.noKK, { onDataChange(data.copy(noKK = it)) }, placeholder = "16 digit nomor KK", keyboardType = KeyboardType.Number)
        F("Nama Ibu Kandung", data.namaIbuKandung, { onDataChange(data.copy(namaIbuKandung = it)) })
        F("Pekerjaan", data.pekerjaan, { onDataChange(data.copy(pekerjaan = it)) }, optional = true)
        F("Alamat", data.alamat, { onDataChange(data.copy(alamat = it)) }, singleLine = false, maxLines = 3)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SectionTitle("Pertanyaan Kondisi (11)")
        F("1. Jaminan kesehatan yang dimiliki", data.jaminanKesehatan, { onDataChange(data.copy(jaminanKesehatan = it)) }, placeholder = "Contoh: BPJS / Tidak ada", optional = true)
        F("2. Alasan jaminan kesehatan non-aktif", data.alasanNonAktif, { onDataChange(data.copy(alasanNonAktif = it)) }, placeholder = "Contoh: Menunggak iuran", optional = true)
        F("3. Status rumah", data.statusRumah, { onDataChange(data.copy(statusRumah = it)) }, placeholder = "Contoh: Milik sendiri / Menumpang", optional = true)
        F("4. Kondisi rumah", data.kondisiRumah, { onDataChange(data.copy(kondisiRumah = it)) }, placeholder = "Contoh: Layak / Tidak layak", optional = true)
        F("5. Ukuran rumah", data.ukuranRumah, { onDataChange(data.copy(ukuranRumah = it)) }, placeholder = "Contoh: 36 m²", optional = true)
        F("6. Dinding rumah", data.dindingRumah, { onDataChange(data.copy(dindingRumah = it)) }, placeholder = "Contoh: Tembok / Bilik", optional = true)
        F("7. Penerangan rumah", data.peneranganRumah, { onDataChange(data.copy(peneranganRumah = it)) }, placeholder = "Contoh: PLN / Lampu minyak", optional = true)
        F("8. Jumlah keluarga dalam satu rumah", data.jumlahKeluarga, { onDataChange(data.copy(jumlahKeluarga = it)) }, keyboardType = KeyboardType.Number, optional = true)
        F("9. Jumlah anggota keluarga", data.jumlahAnggota, { onDataChange(data.copy(jumlahAnggota = it)) }, keyboardType = KeyboardType.Number, optional = true)
        F("10. Bansos yang diterima", data.bansosDiterima, { onDataChange(data.copy(bansosDiterima = it)) }, placeholder = "Contoh: PKH / BPNT / Tidak ada", optional = true)
        F("11. Usaha / penghasilan", data.usahaPenghasilan, { onDataChange(data.copy(usahaPenghasilan = it)) }, placeholder = "Contoh: Buruh harian, Rp 50.000/hari", singleLine = false, maxLines = 2, optional = true)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        F("Catatan Petugas", data.catatanPetugas, { onDataChange(data.copy(catatanPetugas = it)) }, singleLine = false, maxLines = 3, optional = true)
    }
}

@Composable
fun PbiPernyataanForm(
    data: PbiPernyataanData,
    onDataChange: (PbiPernyataanData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.PBI_PERNYATAAN,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        TtlFields(data.tempatLahir, data.tanggalLahir, { onDataChange(data.copy(tempatLahir = it)) }, { onDataChange(data.copy(tanggalLahir = it)) })
        F("Pekerjaan", data.pekerjaan, { onDataChange(data.copy(pekerjaan = it)) }, optional = true)
        F("Alamat", data.alamat, { onDataChange(data.copy(alamat = it)) }, singleLine = false, maxLines = 3)
        F("Isi Pernyataan", data.isiPernyataan, { onDataChange(data.copy(isiPernyataan = it)) }, placeholder = "Tulis isi pernyataan dengan sebenar-benarnya", singleLine = false, maxLines = 5)
    }
}

@Composable
fun PbiTidakPunyaRekeningForm(
    data: PbiTidakPunyaRekeningData,
    onDataChange: (PbiTidakPunyaRekeningData) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        NomorTanggalFields(
            type = LetterType.PBI_TIDAK_PUNYA_REKENING,
            nomorSurat = data.nomorSurat,
            tanggalSurat = data.tanggalSurat,
            onNomorSuratChange = { onDataChange(data.copy(nomorSurat = it)) },
            onTanggalSuratChange = { onDataChange(data.copy(tanggalSurat = it)) }
        )
        Text(
            "Surat pernyataan bermaterai cukup.",
            style = MaterialTheme.typography.bodyMedium
        )
        NamaField(value = data.namaLengkap, onValueChange = { onDataChange(data.copy(namaLengkap = it)) })
        NikField(value = data.nik, onValueChange = { onDataChange(data.copy(nik = it)) })
        TtlFields(data.tempatLahir, data.tanggalLahir, { onDataChange(data.copy(tempatLahir = it)) }, { onDataChange(data.copy(tanggalLahir = it)) })
        F("Pekerjaan", data.pekerjaan, { onDataChange(data.copy(pekerjaan = it)) }, optional = true)
        F("Alamat", data.alamat, { onDataChange(data.copy(alamat = it)) }, singleLine = false, maxLines = 3)
    }
}
