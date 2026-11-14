package com.kasipem.data

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * 30 jenis surat/formulir KASIPEM — kanon yang SAMA dengan web (FIX_BRIEF.md + FILE_INTEGRATION_BRIEF.md).
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
        "Sosial", "460.3", "KS"
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
    ),
    // ---- Fase 2: 18 tipe baru (FILE_INTEGRATION_BRIEF.md) ----
    KEMATIAN(
        "Keterangan Kematian",
        "SURAT KETERANGAN KEMATIAN",
        "Keterangan resmi meninggal dunia",
        "Surat Desa", "460.3", "KS"
    ),
    USAHA(
        "Keterangan Usaha (SKU)",
        "SURAT KETERANGAN USAHA",
        "Keterangan memiliki usaha",
        "Surat Desa", "510", "PEM"
    ),
    DUDA_JANDA(
        "Keterangan Duda/Janda",
        "SURAT KETERANGAN DUDA/JANDA",
        "Status duda atau janda",
        "Surat Desa", "474.2", "KS"
    ),
    F101(
        "F-1.01 Biodata Keluarga",
        "FORMULIR F-1.01 BIODATA KELUARGA",
        "Biodata keluarga (Disdukcapil)",
        "Formulir Dukcapil", "", ""
    ),
    F102(
        "F-1.02 Peristiwa Kependudukan",
        "FORMULIR F-1.02 PENDAFTARAN PERISTIWA KEPENDUDUKAN",
        "Pendaftaran peristiwa kependudukan",
        "Formulir Dukcapil", "", ""
    ),
    F103(
        "F-1.03 Perpindahan Penduduk",
        "FORMULIR F-1.03 PENDAFTARAN PERPINDAHAN PENDUDUK",
        "Pendaftaran perpindahan penduduk",
        "Formulir Dukcapil", "", ""
    ),
    F104(
        "F-1.04 Tidak Punya Dokumen",
        "FORMULIR F-1.04 PERNYATAAN TIDAK MEMILIKI DOKUMEN KEPENDUDUKAN",
        "Pernyataan tidak memiliki dokumen",
        "Formulir Dukcapil", "", ""
    ),
    F105(
        "F-1.05 Perkawinan Belum Tercatat",
        "FORMULIR F-1.05 SPTJM PERKAWINAN BELUM TERCATAT",
        "SPTJM perkawinan belum tercatat",
        "Formulir Dukcapil", "", ""
    ),
    F106(
        "F-1.06 Perubahan Elemen Data",
        "FORMULIR F-1.06 PERNYATAAN PERUBAHAN ELEMEN DATA KEPENDUDUKAN",
        "Perubahan elemen data kependudukan",
        "Formulir Dukcapil", "", ""
    ),
    F115(
        "F-1.15 Permohonan KK Baru",
        "FORMULIR F-1.15 PERMOHONAN KK BARU WNI",
        "Permohonan Kartu Keluarga baru",
        "Formulir Dukcapil", "", ""
    ),
    F201(
        "F-2.01 Pelaporan Pencatatan Sipil",
        "FORMULIR F-2.01 PELAPORAN PENCATATAN SIPIL",
        "Pelaporan pencatatan sipil (16 jenis)",
        "Formulir Dukcapil", "", ""
    ),
    F203(
        "F-2.03 SPTJM Kelahiran",
        "FORMULIR F-2.03 SPTJM KEBENARAN DATA KELAHIRAN",
        "SPTJM kebenaran data kelahiran",
        "Formulir Dukcapil", "", ""
    ),
    F204(
        "F-2.04 SPTJM Suami Istri",
        "FORMULIR F-2.04 SPTJM KEBENARAN SEBAGAI PASANGAN SUAMI ISTRI",
        "SPTJM kebenaran sebagai suami istri",
        "Formulir Dukcapil", "", ""
    ),
    SPTJM_KEMATIAN(
        "SPTJM Kematian",
        "SURAT PERNYATAAN TANGGUNG JAWAB MUTLAK KEBENARAN DATA KEMATIAN",
        "SPTJM kebenaran data kematian",
        "SPTJM", "", ""
    ),
    SPTJM_PERKAWINAN_PERCERAIAN(
        "SPTJM Perkawinan/Perceraian",
        "SURAT PERNYATAAN TANGGUNG JAWAB MUTLAK PERKAWINAN/PERCERAIAN BELUM TERCATAT",
        "SPTJM perkawinan/perceraian belum tercatat",
        "SPTJM", "", ""
    ),
    PBI_ASESMEN(
        "Asesmen PBI-JKN",
        "INSTRUMEN ASESMEN CALON PENERIMA BANSOS KIS-PBI/SKM",
        "Asesmen calon penerima bansos",
        "Bansos (PBI-JKN)", "", ""
    ),
    PBI_PERNYATAAN(
        "Surat Pernyataan (PBI-JKN)",
        "SURAT PERNYATAAN",
        "Surat pernyataan PBI-JKN",
        "Bansos (PBI-JKN)", "474.4", "KS"
    ),
    PBI_TIDAK_PUNYA_REKENING(
        "Pernyataan Tidak Punya Rekening",
        "SURAT PERNYATAAN TIDAK MEMILIKI REKENING",
        "Pernyataan tidak memiliki rekening",
        "Bansos (PBI-JKN)", "474.4", "KS"
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
    /** Fase 2 — varian domisili: Perorangan / Lembaga / Perusahaan. */
    val jenisDomisili: String = "Perorangan",
    val namaLembaga: String = "",
    val namaPerusahaan: String = "",
    val namaPimpinan: String = "",
    val namaSekretaris: String = "",
    val bidangUsaha: String = "",
    val alamatOrganisasi: String = "",
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
    /** Fase 2 — varian SKTM: Umum / Sekolah / Daftar Kolektif. */
    val varianSKTM: String = "Umum",
    /** Daftar warga untuk varian Daftar Kolektif (satu surat untuk banyak warga). */
    val daftarKolektif: List<WargaKolektif> = emptyList(),
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

/* ==================================================================
   Fase 2 — 18 tipe baru (FILE_INTEGRATION_BRIEF.md §A–D).
   Semua field diisi manual oleh pengguna. TIDAK ada auto-isi dari
   database warga / NIK dalam bentuk apa pun. Data uji HARUS fiktif.
   ================================================================== */

/** Satu baris warga untuk SKTM Daftar Kolektif. */
@Serializable
data class WargaKolektif(
    val nama: String = "",
    val nik: String = "",
    val alamat: String = ""
)

@Serializable
data class KematianData(
    val namaAlmarhum: String = "",
    val nikAlmarhum: String = "",
    val jenisKelamin: String = "",
    val agama: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val alamat: String = "",
    val hariMeninggal: String = "",
    val tanggalMeninggal: String = "",
    val tempatMeninggal: String = "",
    val penyebabMeninggal: String = "",
    val namaPelapor: String = "",
    val nikPelapor: String = "",
    val tempatLahirPelapor: String = "",
    val tanggalLahirPelapor: String = "",
    val pekerjaanPelapor: String = "",
    val alamatPelapor: String = "",
    val hubunganPelapor: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class UsahaData(
    val namaLengkap: String = "",
    val nik: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val alamat: String = "",
    val namaUsaha: String = "",
    val alamatUsaha: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class DudaJandaData(
    val statusDudaJanda: String = "Duda",
    val namaLengkap: String = "",
    val nik: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val alamat: String = "",
    val keteranganRT: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

/** Satu anggota keluarga F-1.01 (maks. 10 anggota di form). */
@Serializable
data class AnggotaKeluarga(
    val nama: String = "",
    val nik: String = "",
    val shdk: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val agama: String = "",
    val pendidikan: String = "",
    val pekerjaan: String = "",
    val statusKawin: String = "",
    val namaAyah: String = "",
    val namaIbu: String = ""
)

@Serializable
data class F101Data(
    val namaKepalaKeluarga: String = "",
    val nikKepalaKeluarga: String = "",
    val noKK: String = "",
    val alamatKeluarga: String = "",
    val daftarAnggota: List<AnggotaKeluarga> = listOf(AnggotaKeluarga()),
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class F102Data(
    val namaLengkap: String = "",
    val nik: String = "",
    val noKK: String = "",
    val jenisPermohonan: String = "",
    val daftarLampiran: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

/** Satu anggota keluarga yang ikut pindah (F-1.03). */
@Serializable
data class AnggotaPindah(
    val nik: String = "",
    val nama: String = "",
    val shdk: String = ""
)

@Serializable
data class F103Data(
    val noKK: String = "",
    val namaPemohon: String = "",
    val nik: String = "",
    val jenisPermohonan: String = "",
    val alamatAsal: String = "",
    val rt: String = "",
    val rw: String = "",
    val klasifikasiPindah: String = "",
    val alamatPindah: String = "",
    val alasanPindah: String = "",
    val jenisKepindahan: String = "",
    val daftarAnggotaPindah: List<AnggotaPindah> = listOf(AnggotaPindah()),
    val anggotaTidakPindah: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class F104Data(
    val namaLengkap: String = "",
    val alamat: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val namaIbu: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

/** Satu anak (F-1.05 / SPTJM Perkawinan-Perceraian): nama, No Akta Kelahiran, SHDK. */
@Serializable
data class AnakData(
    val nama: String = "",
    val noAktaKelahiran: String = "",
    val shdk: String = ""
)

@Serializable
data class F105Data(
    val namaSuami: String = "",
    val nikSuami: String = "",
    val namaIstri: String = "",
    val nikIstri: String = "",
    val tanggalPerkawinan: String = "",
    val bulanPerkawinan: String = "",
    val tahunPerkawinan: String = "",
    val namaSaksi1: String = "",
    val nikSaksi1: String = "",
    val namaSaksi2: String = "",
    val nikSaksi2: String = "",
    val daftarAnak: List<AnakData> = listOf(AnakData()),
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

/** Satu baris tabel anggota keluarga F-1.06 (maks. 7 baris di form). */
@Serializable
data class AnggotaPerubahan(
    val nama: String = "",
    val perubahan: String = ""
)

@Serializable
data class F106Data(
    val namaLengkap: String = "",
    val nik: String = "",
    val noKK: String = "",
    val alamat: String = "",
    val tabelAnggota: List<AnggotaPerubahan> = listOf(AnggotaPerubahan()),
    val pendidikanSemula: String = "",
    val pendidikanMenjadi: String = "",
    val pendidikanDasar: String = "",
    val pekerjaanSemula: String = "",
    val pekerjaanMenjadi: String = "",
    val pekerjaanDasar: String = "",
    val perubahanAgama: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

/** Satu anggota keluarga F-1.15 (NIK, nama, SHDK). */
@Serializable
data class AnggotaKkBaru(
    val nik: String = "",
    val nama: String = "",
    val shdk: String = ""
)

@Serializable
data class F115Data(
    val namaPemohon: String = "",
    val nik: String = "",
    val noKKLama: String = "",
    val alamat: String = "",
    val rt: String = "",
    val rw: String = "",
    val alasanPermohonan: String = "",
    val jumlahAnggota: String = "",
    val daftarAnggota: List<AnggotaKkBaru> = listOf(AnggotaKkBaru()),
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class F201Data(
    val jenisPelaporan: String = "Kelahiran",
    val pelaporNama: String = "",
    val pelaporNik: String = "",
    val pelaporAlamat: String = "",
    val subjek1Nama: String = "",
    val subjek1Nik: String = "",
    val subjek2Nama: String = "",
    val subjek2Nik: String = "",
    val saksi1Nama: String = "",
    val saksi1Nik: String = "",
    val saksi2Nama: String = "",
    val saksi2Nik: String = "",
    val ayahNama: String = "",
    val ayahNik: String = "",
    val ibuNama: String = "",
    val ibuNik: String = "",
    // Seksi Kelahiran
    val anakNama: String = "",
    val anakTempatLahir: String = "",
    val anakTanggalLahir: String = "",
    val anakJenisKelamin: String = "",
    val anakKe: String = "",
    // Seksi Lahir Mati
    val lahirMatiNama: String = "",
    val lahirMatiTanggal: String = "",
    val lahirMatiPukul: String = "",
    val lahirMatiTempat: String = "",
    val lahirMatiSebab: String = "",
    // Seksi Perkawinan / Perceraian
    val pasanganSuamiNama: String = "",
    val pasanganIstriNama: String = "",
    val tanggalPeristiwa: String = "",
    val tempatPeristiwa: String = "",
    // Seksi Kematian
    val meninggalNik: String = "",
    val meninggalNama: String = "",
    val meninggalTanggal: String = "",
    val meninggalPukul: String = "",
    val meninggalSebab: String = "",
    val meninggalTempat: String = "",
    val meninggalYangMenerangkan: String = "",
    // Seksi umum (jenis pelaporan lain)
    val keteranganPeristiwa: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class F203Data(
    val namaYangMenyatakan: String = "",
    val nik: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val alamat: String = "",
    val namaAnak: String = "",
    val nikAnak: String = "",
    val tempatLahirAnak: String = "",
    val tanggalLahirAnak: String = "",
    val jenisKelaminAnak: String = "",
    val anakKe: String = "",
    val alamatAnak: String = "",
    val namaIbu: String = "",
    val nikIbu: String = "",
    val tempatLahirIbu: String = "",
    val tanggalLahirIbu: String = "",
    val pekerjaanIbu: String = "",
    val alamatIbu: String = "",
    val namaPenolong: String = "",
    val nikPenolong: String = "",
    val pekerjaanPenolong: String = "",
    val alamatPenolong: String = "",
    val namaSaksi1: String = "",
    val nikSaksi1: String = "",
    val namaSaksi2: String = "",
    val nikSaksi2: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class F204Data(
    val namaYangMenyatakan: String = "",
    val nik: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val alamat: String = "",
    val namaPasangan: String = "",
    val nikPasangan: String = "",
    val tempatLahirPasangan: String = "",
    val tanggalLahirPasangan: String = "",
    val pekerjaanPasangan: String = "",
    val alamatPasangan: String = "",
    val noKK: String = "",
    val namaSaksi1: String = "",
    val nikSaksi1: String = "",
    val namaSaksi2: String = "",
    val nikSaksi2: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class SptjmKematianData(
    val namaYangMenyatakan: String = "",
    val nik: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val alamat: String = "",
    val namaAlmarhum: String = "",
    val nikAlmarhum: String = "",
    val tempatLahirAlmarhum: String = "",
    val tanggalLahirAlmarhum: String = "",
    val alamatAlmarhum: String = "",
    val hariMeninggal: String = "",
    val tanggalMeninggal: String = "",
    val jamMeninggal: String = "",
    val tempatMeninggal: String = "",
    val namaSaksi1: String = "",
    val nikSaksi1: String = "",
    val namaSaksi2: String = "",
    val nikSaksi2: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class SptjmPerkawinanPerceraianData(
    val jenisSPTJM: String = "Perkawinan",
    val namaSuami: String = "",
    val nikSuami: String = "",
    val namaIstri: String = "",
    val nikIstri: String = "",
    val tanggalPeristiwa: String = "",
    val namaSaksi1: String = "",
    val nikSaksi1: String = "",
    val namaSaksi2: String = "",
    val nikSaksi2: String = "",
    val daftarAnak: List<AnakData> = listOf(AnakData()),
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class PbiAsesmenData(
    val namaLengkap: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val jenisKelamin: String = "",
    val statusKawin: String = "",
    val nik: String = "",
    val noKK: String = "",
    val namaIbuKandung: String = "",
    val pekerjaan: String = "",
    val alamat: String = "",
    // 11 pertanyaan kondisi (Instrumen Asesmen KIS-PBI/SKM)
    val jaminanKesehatan: String = "",
    val alasanNonAktif: String = "",
    val statusRumah: String = "",
    val kondisiRumah: String = "",
    val ukuranRumah: String = "",
    val dindingRumah: String = "",
    val peneranganRumah: String = "",
    val jumlahKeluarga: String = "",
    val jumlahAnggota: String = "",
    val bansosDiterima: String = "",
    val usahaPenghasilan: String = "",
    val catatanPetugas: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class PbiPernyataanData(
    val namaLengkap: String = "",
    val nik: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val alamat: String = "",
    val isiPernyataan: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class PbiTidakPunyaRekeningData(
    val namaLengkap: String = "",
    val nik: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val pekerjaan: String = "",
    val alamat: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

/** True bila tipe ini formulir Dukcapil seri F → kop Disdukcapil TEKS saja (tanpa logo desa). */
fun isFormulirDukcapil(type: LetterType): Boolean = type in listOf(
    LetterType.F101, LetterType.F102, LetterType.F103, LetterType.F104,
    LetterType.F105, LetterType.F106, LetterType.F115, LetterType.F201,
    LetterType.F203, LetterType.F204
)

/** 16 jenis pelaporan F-2.01 (sesuai formulir). */
val F201_JENIS_PELAPORAN: List<String> = listOf(
    "Kelahiran", "Lahir Mati", "Perkawinan", "Pembatalan Perkawinan",
    "Perceraian", "Pembatalan Perceraian", "Kematian", "Pengangkatan Anak",
    "Pengakuan Anak", "Pengesahan Anak", "Perubahan Nama",
    "Perubahan Status Kewarganegaraan", "Peristiwa Penting Lainnya",
    "Pembetulan Akta", "Pembatalan Akta", "Pelaporan dari Luar Wilayah NKRI"
)

/**
 * Seksi kondisional F-2.01: hanya seksi jenis pelaporan yang dipilih yang
 * tampil di form/preview/PDF. "anak" / "lahirMati" / "pasangan" /
 * "kematian" / "umum" (jenis lain → seksi keterangan umum).
 */
fun f201Section(jenisPelaporan: String): String = when (jenisPelaporan) {
    "Kelahiran" -> "anak"
    "Lahir Mati" -> "lahirMati"
    "Perkawinan", "Pembatalan Perkawinan", "Perceraian", "Pembatalan Perceraian" -> "pasangan"
    "Kematian" -> "kematian"
    else -> "umum"
}

/**
 * Judul PDF/preview yang dipakai, mengikuti varian yang dipilih:
 * domisili Lembaga/Perusahaan dan SKTM Sekolah/Kolektif mengubah judul.
 */
fun effectivePdfTitle(type: LetterType, data: Any): String = when (type) {
    LetterType.DOMISILI -> when ((data as DomisiliData).jenisDomisili) {
        "Lembaga" -> "SURAT KETERANGAN DOMISILI LEMBAGA"
        "Perusahaan" -> "SURAT KETERANGAN DOMISILI PERUSAHAAN"
        else -> type.pdfTitle
    }
    LetterType.TIDAK_MAMPU -> when ((data as TidakMampuData).varianSKTM) {
        "Sekolah" -> "SURAT KETERANGAN TIDAK MAMPU (SEKOLAH)"
        "Daftar Kolektif" -> "SURAT KETERANGAN TIDAK MAMPU (DAFTAR KOLEKTIF)"
        else -> type.pdfTitle
    }
    else -> type.pdfTitle
}

/** Nama pemohon utama per tipe — dipakai Riwayat Surat. */
fun namaPemohonOf(type: LetterType, data: Any): String = when (type) {
    LetterType.DOMISILI -> {
        val d = data as DomisiliData
        when (d.jenisDomisili) {
            "Lembaga" -> d.namaLembaga
            "Perusahaan" -> d.namaPerusahaan
            else -> d.namaLengkap
        }
    }
    LetterType.PISAH_KK -> (data as PisahKkData).nama1
    LetterType.SKT_DAYAT -> (data as SktDayatData).namaLengkap
    LetterType.BEDA_LUAS -> (data as BedaLuasData).namaLengkap
    LetterType.BEDA_NAMA -> (data as BedaNamaData).namaLengkap
    LetterType.BELUM_MENIKAH -> (data as BelumMenikahData).namaLengkap
    LetterType.TIDAK_MAMPU -> (data as TidakMampuData).namaLengkap
    LetterType.AHLI_WARIS -> (data as AhliWarisData).namaPeninggal
    LetterType.NIKAH_TIDAK_TERCATAT -> (data as NikahTidakTercatatData).namaPria
    LetterType.PEMAKAMAN -> (data as PemakamanData).namaAlmarhum
    LetterType.TIDAK_MEMILIKI_RUMAH -> (data as TidakMemilikiRumahData).namaLengkap
    LetterType.UNDANGAN -> (data as UndanganData).tujuanYth
    LetterType.KEMATIAN -> (data as KematianData).namaAlmarhum
    LetterType.USAHA -> (data as UsahaData).namaLengkap
    LetterType.DUDA_JANDA -> (data as DudaJandaData).namaLengkap
    LetterType.F101 -> (data as F101Data).namaKepalaKeluarga
    LetterType.F102 -> (data as F102Data).namaLengkap
    LetterType.F103 -> (data as F103Data).namaPemohon
    LetterType.F104 -> (data as F104Data).namaLengkap
    LetterType.F105 -> (data as F105Data).namaSuami
    LetterType.F106 -> (data as F106Data).namaLengkap
    LetterType.F115 -> (data as F115Data).namaPemohon
    LetterType.F201 -> (data as F201Data).pelaporNama
    LetterType.F203 -> (data as F203Data).namaYangMenyatakan
    LetterType.F204 -> (data as F204Data).namaYangMenyatakan
    LetterType.SPTJM_KEMATIAN -> (data as SptjmKematianData).namaYangMenyatakan
    LetterType.SPTJM_PERKAWINAN_PERCERAIAN -> (data as SptjmPerkawinanPerceraianData).namaSuami
    LetterType.PBI_ASESMEN -> (data as PbiAsesmenData).namaLengkap
    LetterType.PBI_PERNYATAAN -> (data as PbiPernyataanData).namaLengkap
    LetterType.PBI_TIDAK_PUNYA_REKENING -> (data as PbiTidakPunyaRekeningData).namaLengkap
}

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
    LetterType.KEMATIAN -> KematianData(nomorSurat = defaultNomorSurat(type))
    LetterType.USAHA -> UsahaData(nomorSurat = defaultNomorSurat(type))
    LetterType.DUDA_JANDA -> DudaJandaData(nomorSurat = defaultNomorSurat(type))
    LetterType.F101 -> F101Data(nomorSurat = defaultNomorSurat(type))
    LetterType.F102 -> F102Data(nomorSurat = defaultNomorSurat(type))
    LetterType.F103 -> F103Data(nomorSurat = defaultNomorSurat(type))
    LetterType.F104 -> F104Data(nomorSurat = defaultNomorSurat(type))
    LetterType.F105 -> F105Data(nomorSurat = defaultNomorSurat(type))
    LetterType.F106 -> F106Data(nomorSurat = defaultNomorSurat(type))
    LetterType.F115 -> F115Data(nomorSurat = defaultNomorSurat(type))
    LetterType.F201 -> F201Data(nomorSurat = defaultNomorSurat(type))
    LetterType.F203 -> F203Data(nomorSurat = defaultNomorSurat(type))
    LetterType.F204 -> F204Data(nomorSurat = defaultNomorSurat(type))
    LetterType.SPTJM_KEMATIAN -> SptjmKematianData(nomorSurat = defaultNomorSurat(type))
    LetterType.SPTJM_PERKAWINAN_PERCERAIAN -> SptjmPerkawinanPerceraianData(nomorSurat = defaultNomorSurat(type))
    LetterType.PBI_ASESMEN -> PbiAsesmenData(nomorSurat = defaultNomorSurat(type))
    LetterType.PBI_PERNYATAAN -> PbiPernyataanData(nomorSurat = defaultNomorSurat(type))
    LetterType.PBI_TIDAK_PUNYA_REKENING -> PbiTidakPunyaRekeningData(nomorSurat = defaultNomorSurat(type))
}
