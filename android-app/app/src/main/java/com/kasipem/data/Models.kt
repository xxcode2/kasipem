package com.kasipem.data

import kotlinx.serialization.Serializable

@Serializable
enum class LetterType {
    DOMISILI,
    BELUM_MENIKAH,
    TIDAK_MAMPU,
    BEDA_LUAS,
    UNDANGAN
}

@Serializable
data class HeaderData(
    val namaKabupaten: String = "KABUPATEN",
    val namaKecamatan: String = "KECAMATAN",
    val namaDesa: String = "DESA/KELURAHAN",
    val alamatKantor: String = "Jl. Raya No. 123",
    val namaPejabat: String = "H. Nama Kepala Desa",
    val atnPejabat: Boolean = false,
    val nipPejabat: String = "1234567890",
    val jabatanPejabat: String = "Kepala Desa"
)

@Serializable
data class PersonalData(
    val namaLengkap: String = "",
    val nik: String = "",
    val alamatLengkap: String = "",
    val rt: String = "",
    val rw: String = "",
    val jenisKelamin: String = "",
    val nomorKK: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = ""
)

@Serializable
data class DomisiliData(
    val namaLengkap: String = "",
    val nik: String = "",
    val alamatLengkap: String = "",
    val rt: String = "",
    val rw: String = "",
    val jenisKelamin: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val alamatAsal: String = "",
    val alamatDomisili: String = "",
    val keperluanSurat: String = ""
)

@Serializable
data class BelumMenikahData(
    val namaLengkap: String = "",
    val nik: String = "",
    val alamatLengkap: String = "",
    val rt: String = "",
    val rw: String = "",
    val jenisKelamin: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val tujuanSekolah: String = "",
    val keperluan: String = ""
)

@Serializable
data class TidakMampuData(
    val namaLengkap: String = "",
    val nik: String = "",
    val alamatLengkap: String = "",
    val rt: String = "",
    val rw: String = "",
    val jenisKelamin: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val tujuanSekolah: String = "",
    val keperluan: String = ""
)

@Serializable
data class BedaLuasData(
    val namaLengkap: String = "",
    val nik: String = "",
    val alamatLengkap: String = "",
    val rt: String = "",
    val rw: String = "",
    val nomorSertifikat: String = "",
    val luasSertifikat: String = "",
    val nomorSPPT: String = "",
    val luasSPPT: String = ""
)

@Serializable
data class UndanganData(
    val tujuanYth: String = "",
    val hari: String = "",
    val tanggal: String = "",
    val pukul: String = "",
    val tempat: String = "",
    val agendaAcara: String = "",
    val daftarTembusan: String = ""
)
