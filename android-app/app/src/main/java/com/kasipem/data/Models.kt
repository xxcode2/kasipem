package com.kasipem.data

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * 12 jenis surat KASIPEM — kanon yang SAMA dengan web (FIX_BRIEF.md).
 * Desa asli: Desa Kasomalang Kulon, Kecamatan Kasomalang, Kabupaten Subang.
 * Pejabat (diberikan user 2026-11-14 ~23:28 WIB): Kades H. AMIRUDIN, S.Pd.I;
 * Sekdes APIPUDIN, S.IP. TIDAK ADA NIP — default NIP kosong dan baris NIP
 * hanya dirender bila NIP diisi di Settings.
 */
@Serializable
enum class LetterType(
    /** Judul tampil di kartu (Title Case). */
    val displayTitle: String,
    /** Judul di kop surat / PDF (huruf kapital). */
    val pdfTitle: String,
    val description: String,
    /** Kategori untuk filter di grid: Individu / Properti / Administrasi / Sosial / Hukum / Lainnya. */
    val category: String,
    /** Awalan nomor surat, mis. "474". Kosong = nomor manual (undangan). */
    val nomorPrefix: String,
    /** Bagian tengah nomor surat, mis. "PEM" atau "Ks". */
    val nomorSuffix: String
) {
    DOMISILI(
        "Keterangan Domisili",
        "SURAT KETERANGAN DOMISILI",
        "Verifikasi tempat tinggal",
        "Individu", "474", "PEM"
    ),
    PISAH_KK(
        "Pisah Kartu Keluarga",
        "SURAT KETERANGAN PISAH KK",
        "Pisah dari KK lama",
        "Administrasi", "474", "PEM"
    ),
    SKT_DAYAT(
        "Keterangan Tanah (SKT)",
        "SURAT KETERANGAN TANAH",
        "Keterangan kepemilikan tanah",
        "Properti", "593", "PEM"
    ),
    BEDA_LUAS(
        "Beda Luas Tanah",
        "SURAT KETERANGAN BEDA LUAS TANAH",
        "Sertifikat vs SPPT",
        "Properti", "141", "PEM"
    ),
    BEDA_NAMA(
        "Beda Nama",
        "SURAT KETERANGAN BEDA NAMA",
        "Perbedaan nama antar dokumen",
        "Administrasi", "474", "PEM"
    ),
    BELUM_MENIKAH(
        "Belum Menikah",
        "SURAT KETERANGAN BELUM MENIKAH",
        "Administrasi perkawinan",
        "Individu", "460.2", "Ks"
    ),
    TIDAK_MAMPU(
        "Tidak Mampu (SKTM)",
        "SURAT KETERANGAN TIDAK MAMPU",
        "Bantuan sosial",
        "Sosial", "460", "Ks"
    ),
    AHLI_WARIS(
        "Ahli Waris",
        "SURAT KETERANGAN AHLI WARIS",
        "Keterangan ahli waris",
        "Hukum", "474", "PEM"
    ),
    NIKAH_TIDAK_TERCATAT(
        "Nikah Tidak Tercatat",
        "SURAT KETERANGAN NIKAH TIDAK TERCATAT",
        "Pernikahan tidak tercatat",
        "Hukum", "460.2", "Ks"
    ),
    PEMAKAMAN(
        "Pemakaman",
        "SURAT KETERANGAN PEMAKAMAN",
        "Keterangan pemakaman",
        "Sosial", "474", "PEM"
    ),
    TIDAK_MEMILIKI_RUMAH(
        "Tidak Memiliki Rumah",
        "SURAT PERNYATAAN TIDAK MEMILIKI RUMAH",
        "Pernyataan belum punya rumah",
        "Sosial", "474", "PEM"
    ),
    UNDANGAN(
        "Undangan Resmi",
        "SURAT UNDANGAN",
        "Acara resmi desa",
        "Lainnya", "", ""
    )
}

/** Tanggal hari ini, format id-ID, mis. "14 November 2026". */
fun todayId(): String =
    SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())

fun currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

/**
 * Nomor surat default per tipe, mis. "474/.../PEM/2026".
 * Undangan memakai nomor manual → default kosong.
 */
fun defaultNomorSurat(type: LetterType): String =
    if (type.nomorPrefix.isEmpty()) "" else "${type.nomorPrefix}/.../${type.nomorSuffix}/${currentYear()}"

/** Nomor yang dipakai di preview/PDF: isi form, atau default tipe bila kosong. */
fun effectiveNomorSurat(nomorSurat: String, type: LetterType): String =
    nomorSurat.ifBlank { defaultNomorSurat(type) }

/** Tanggal yang dipakai di preview/PDF: isi form, atau hari ini bila kosong. */
fun effectiveTanggalSurat(tanggalSurat: String): String =
    tanggalSurat.ifBlank { todayId() }

/**
 * Validasi NIK: peringatan saja, jangan blokir keras.
 * Mengembalikan pesan error, atau null bila NIK kosong / valid (16 digit angka).
 */
fun nikError(nik: String): String? {
    if (nik.isBlank()) return null
    val digitsOnly = nik.all { it.isDigit() }
    return if (nik.length != 16 || !digitsOnly) "NIK harus 16 digit angka" else null
}

@Serializable
data class HeaderData(
    val namaKabupaten: String = "KABUPATEN SUBANG",
    val namaKecamatan: String = "KECAMATAN KASOMALANG",
    val namaDesa: String = "KASOMALANG KULON",
    val alamatKantor: String = "Jln. Raya Kasomalang No.01 Subang 41281",
    /** Nama Kades asli (user, 2026-11-14); bisa diedit lewat Settings. */
    val namaPejabat: String = "H. AMIRUDIN, S.Pd.I",
    val atnPejabat: Boolean = false,
    /** TIDAK ADA NIP (user, 2026-11-14): default kosong, baris NIP tidak dirender bila kosong. */
    val nipPejabat: String = "",
    val jabatanPejabat: String = "Kepala Desa",
    /** Nama Sekdes asli (user, 2026-11-14); bisa diedit lewat Settings. */
    val namaSekdes: String = "APIPUDIN, S.IP",
    /** TIDAK ADA NIP: default kosong, baris NIP tidak dirender bila kosong. */
    val nipSekdes: String = "",
    /** Path logo di assets: Garuda Pancasila (kiri) dan logo Kabupaten Subang (kanan). */
    val logoGarudaAsset: String = "kop/kades.jpg",
    val logoSubangAsset: String = "kop/sekdes.png"
)

/* PersonalData (legacy, tidak dipakai lagi) dihapus — setiap surat memakai
   data class kanon-nya sendiri di bawah ini. */

@Serializable
data class DomisiliData(
    val namaLengkap: String = "",
    val nik: String = "",
    val jenisKelamin: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val alamatAsal: String = "",
    val rt: String = "",
    val rw: String = "",
    val alamatDomisili: String = "",
    val keperluanSurat: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class PisahKkData(
    val nama1: String = "",
    val nik1: String = "",
    val nama2: String = "",
    val nik2: String = "",
    val nomorKKLama: String = "",
    val alamat: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class SktDayatData(
    val namaLengkap: String = "",
    val nik: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val alamat: String = "",
    val luasTanah: String = "",
    val luasBangunan: String = "",
    val spptNop: String = "",
    val batasUtara: String = "",
    val batasTimur: String = "",
    val batasSelatan: String = "",
    val batasBarat: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class BedaLuasData(
    val namaLengkap: String = "",
    val nik: String = "",
    val alamat: String = "",
    val rt: String = "",
    val rw: String = "",
    val nomorSertifikat: String = "",
    val luasSertifikat: String = "",
    val nomorSPPT: String = "",
    val luasSPPT: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class BedaNamaData(
    val namaLengkap: String = "",
    val nik: String = "",
    val namaDokumen1: String = "",
    val namaDokumen2: String = "",
    val alasan: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class BelumMenikahData(
    val namaLengkap: String = "",
    val nik: String = "",
    val jenisKelamin: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val alamat: String = "",
    val rt: String = "",
    val rw: String = "",
    val keperluan: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class TidakMampuData(
    val namaLengkap: String = "",
    val nik: String = "",
    val jenisKelamin: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val alamat: String = "",
    val rt: String = "",
    val rw: String = "",
    val penghasilanBulanan: String = "",
    val keperluan: String = "",
    val tujuan: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class AhliWarisData(
    val namaPeninggal: String = "",
    val nikPeninggal: String = "",
    val alamatPeninggal: String = "",
    val namaAhliWaris1: String = "",
    val nikAhliWaris1: String = "",
    val hubunganAhliWaris1: String = "",
    val namaAhliWaris2: String = "",
    val nikAhliWaris2: String = "",
    val hubunganAhliWaris2: String = "",
    val keterangan: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class NikahTidakTercatatData(
    val namaPria: String = "",
    val nikPria: String = "",
    val tempatLahirPria: String = "",
    val tanggalLahirPria: String = "",
    val namaWanita: String = "",
    val nikWanita: String = "",
    val tempatLahirWanita: String = "",
    val tanggalLahirWanita: String = "",
    val tanggalPernikahan: String = "",
    val tempatPernikahan: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class PemakamanData(
    val namaAlmarhum: String = "",
    val nikAlmarhum: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val alamat: String = "",
    val tanggalMeninggal: String = "",
    val lokasiMakam: String = "",
    val namaKeluarga: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class TidakMemilikiRumahData(
    val namaLengkap: String = "",
    val nik: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val alamatTinggal: String = "",
    val keterangan: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class UndanganData(
    val tujuanYth: String = "",
    val hari: String = "",
    val tanggal: String = "",
    val bulan: String = "",
    val tahun: String = "",
    val pukul: String = "",
    val tempat: String = "",
    val agendaAcara: String = "",
    val daftarTembusan: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

/** Data kosong untuk satu tipe, dengan nomor surat default tipe itu sudah terisi. */
fun getEmptyLetterData(type: LetterType): Any = when (type) {
    LetterType.DOMISILI -> DomisiliData(nomorSurat = defaultNomorSurat(type))
    LetterType.PISAH_KK -> PisahKkData(nomorSurat = defaultNomorSurat(type))
    LetterType.SKT_DAYAT -> SktDayatData(nomorSurat = defaultNomorSurat(type))
    LetterType.BEDA_LUAS -> BedaLuasData(nomorSurat = defaultNomorSurat(type))
    LetterType.BEDA_NAMA -> BedaNamaData(nomorSurat = defaultNomorSurat(type))
    LetterType.BELUM_MENIKAH -> BelumMenikahData(nomorSurat = defaultNomorSurat(type))
    LetterType.TIDAK_MAMPU -> TidakMampuData(nomorSurat = defaultNomorSurat(type))
    LetterType.AHLI_WARIS -> AhliWarisData(nomorSurat = defaultNomorSurat(type))
    LetterType.NIKAH_TIDAK_TERCATAT -> NikahTidakTercatatData(nomorSurat = defaultNomorSurat(type))
    LetterType.PEMAKAMAN -> PemakamanData(nomorSurat = defaultNomorSurat(type))
    LetterType.TIDAK_MEMILIKI_RUMAH -> TidakMemilikiRumahData(nomorSurat = defaultNomorSurat(type))
    LetterType.UNDANGAN -> UndanganData(nomorSurat = defaultNomorSurat(type))
}
