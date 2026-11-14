package com.kasipem.data

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * 33 jenis surat/formulir KASIPEM — kanon yang SAMA dengan web (FIX_BRIEF.md + FILE_INTEGRATION_BRIEF.md + FASE5_SPEC.md).
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
        "Individu", "140", "PEM"
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
        "Hukum", "474", "Ks"
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
        "Sosial", "141", "Pem"
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
        "FORMULIR F-1.01 — BIODATA KELUARGA",
        "Biodata keluarga (Disdukcapil)",
        "Formulir Dukcapil", "", ""
    ),
    F102(
        "F-1.02 Peristiwa Kependudukan",
        "FORMULIR F-1.02 — PENDAFTARAN PERISTIWA KEPENDUDUKAN",
        "Pendaftaran peristiwa kependudukan",
        "Formulir Dukcapil", "", ""
    ),
    F103(
        "F-1.03 Perpindahan Penduduk",
        "FORMULIR F-1.03 — PENDAFTARAN PERPINDAHAN PENDUDUK",
        "Pendaftaran perpindahan penduduk",
        "Formulir Dukcapil", "", ""
    ),
    F104(
        "F-1.04 Tidak Punya Dokumen",
        "F-1.04 — SURAT PERNYATAAN TIDAK MEMILIKI DOKUMEN KEPENDUDUKAN",
        "Pernyataan tidak memiliki dokumen",
        "Formulir Dukcapil", "", ""
    ),
    F105(
        "F-1.05 Perkawinan Belum Tercatat",
        "F-1.05 — SPTJM PERKAWINAN BELUM TERCATAT",
        "SPTJM perkawinan belum tercatat",
        "Formulir Dukcapil", "", ""
    ),
    F106(
        "F-1.06 Perubahan Elemen Data",
        "F-1.06 — SURAT PERNYATAAN PERUBAHAN ELEMEN DATA KEPENDUDUKAN",
        "Perubahan elemen data kependudukan",
        "Formulir Dukcapil", "", ""
    ),
    F115(
        "F-1.15 Permohonan KK Baru",
        "FORMULIR F-1.15 — PERMOHONAN KK BARU WNI",
        "Permohonan Kartu Keluarga baru",
        "Formulir Dukcapil", "", ""
    ),
    F201(
        "F-2.01 Pelaporan Pencatatan Sipil",
        "FORMULIR F-2.01 — PELAPORAN PENCATATAN SIPIL",
        "Pelaporan pencatatan sipil (16 jenis)",
        "Formulir Dukcapil", "", ""
    ),
    F203(
        "F-2.03 SPTJM Kelahiran",
        "F-2.03 — SPTJM KEBENARAN DATA KELAHIRAN",
        "SPTJM kebenaran data kelahiran",
        "Formulir Dukcapil", "", ""
    ),
    F204(
        "F-2.04 SPTJM Suami Istri",
        "F-2.04 — SPTJM KEBENARAN SEBAGAI PASANGAN SUAMI ISTRI",
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
        "INSTRUMEN ASESMEN CALON PENERIMA BANSOS KIS-PBI / SKM",
        "Asesmen calon penerima bansos",
        "Bansos (PBI-JKN)", "", ""
    ),
    PBI_PERNYATAAN(
        "Surat Pernyataan (PBI-JKN)",
        "SURAT PERNYATAAN (PBI-JKN)",
        "Surat pernyataan PBI-JKN",
        "Bansos (PBI-JKN)", "474.4", "KS"
    ),
    PBI_TIDAK_PUNYA_REKENING(
        "Pernyataan Tidak Punya Rekening",
        "SURAT PERNYATAAN TIDAK MEMILIKI REKENING",
        "Pernyataan tidak memiliki rekening",
        "Bansos (PBI-JKN)", "474.4", "KS"
    ),
    // ---- Fase 5: 3 tipe baru (FASE5_SPEC.md §2.5) — total 33 tipe ----
    SKTM_PBI(
        "Keterangan Miskin (PBI-JKN)",
        "SURAT KETERANGAN MISKIN (PBI-JKN)",
        "Keterangan miskin untuk PBI-JKN",
        "Bansos (PBI-JKN)", "474.4", "KS"
    ),
    PBI_REKENING_LISTRIK(
        "Pernyataan Tidak Punya Rekening Listrik",
        "SURAT PERNYATAAN TIDAK MEMILIKI REKENING LISTRIK",
        "Pernyataan tidak memiliki rekening listrik",
        "Bansos (PBI-JKN)", "474.4", "KS"
    ),
    PERNYATAAN_AKTA_KELAHIRAN(
        "Pernyataan Akta Kelahiran",
        "SURAT PERNYATAAN",
        "Pernyataan sedang membuat akta kelahiran",
        "Surat Desa", "141", "Pem"
    )
}

/** Tanggal hari ini, format id-ID, mis. "14 November 2026". */
fun todayId(): String =
    SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())

fun currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

/**
 * FASE 3 (2026-11-15): Nomor surat default = KOSONG untuk semua tipe.
 * Pola nomor (mis. "474/.../PEM/2026") HANYA dipakai sebagai placeholder
 * contoh di form (lihat [nomorPattern]) — tidak pernah jadi nilai.
 * Fungsi ini dipertahankan (mengembalikan "") agar pemanggil lama
 * (placeholder form) tetap kompilasi; nilai default tidak lagi diisi.
 */
fun defaultNomorSurat(type: LetterType): String = ""

/**
 * Pola nomor sebagai CONTOH placeholder di form, mis. "474/.../PEM/2026".
 * BUKAN nilai default — form dibuka dengan nomor kosong.
 */
fun nomorPattern(type: LetterType): String =
    if (type.nomorPrefix.isEmpty()) "" else "${type.nomorPrefix}/.../${type.nomorSuffix}/${currentYear()}"

/**
 * Nomor yang dipakai di preview/PDF/Riwayat: isi form apa adanya.
 * FASE 3: tidak ada lagi fallback ke pola default — nomor kosong
 * tetap kosong, dan preview/PDF menampilkannya sebagai garis
 * titik-titik (lihat PdfGenerator.addTitle / PreviewScreen).
 */
fun effectiveNomorSurat(nomorSurat: String, type: LetterType): String = nomorSurat

/**
 * True bila tipe ini memakai baris Nomor: punya pola nomor, atau nomor
 * manual (undangan). Formulir Dukcapil seri F (hideNomor) tidak punya
 * baris Nomor — kecuali pengguna mengisinya manual.
 */
fun hasNomorBaris(type: LetterType): Boolean =
    type.nomorPrefix.isNotEmpty() || type == LetterType.UNDANGAN

/**
 * Gaya kop surat — FASE 3 (user 2026-11-15): kop mengikuti
 * penandatangan, ditentukan SATU fungsi [kopFor] yang dipakai
 * PdfGenerator DAN PreviewScreen (jangan ada cabang kop lain).
 */
enum class KopStyle {
    /** Ttd Kepala Desa: logo Garuda SAJA, di tengah paling atas, tanpa logo Subang. */
    GARUDA_CENTER,
    /** Ttd a.n. Kades oleh Sekdes / ttd Sekdes langsung: logo Subang di kiri, tanpa Garuda. */
    SUBANG_LEFT,
    /** SPTJM / PBI (penandatangan utama warga; Kades hanya "Mengetahui"): dua logo seperti semula. */
    DUA_LOGO,
    /** Formulir Dukcapil seri F: kop Disdukcapil TEKS saja (tanpa logo desa). */
    DISDUKCAPIL,
    /** FASE 5 — Surat Keterangan Kematian: TANPA kop/logo; tabel 3 baris
     *  (Pemerintah/Kota/Kab, Kecamatan, Desa/Kelurahan) di kiri atas. */
    TABEL_KEMATIAN
}

/**
 * SATU fungsi penentu kop — dipakai PdfGenerator.addHeaderFor dan
 * PreviewScreen.PreviewContent. Pemetaan mengikuti penandatangan
 * (Signer di PdfGenerator): KADES → Garuda tengah; SEKDES_AN dan
 * SEKDES → Subang kiri; SPTJM/PBI → dua logo (tetap); seri F →
 * Disdukcapil teks (tetap).
 */
fun kopFor(type: LetterType): KopStyle = when (type) {
    LetterType.F101, LetterType.F102, LetterType.F103, LetterType.F104,
    LetterType.F105, LetterType.F106, LetterType.F115, LetterType.F201,
    LetterType.F203, LetterType.F204 -> KopStyle.DISDUKCAPIL
    LetterType.SPTJM_KEMATIAN, LetterType.SPTJM_PERKAWINAN_PERCERAIAN,
    LetterType.PBI_ASESMEN, LetterType.PBI_PERNYATAAN,
    LetterType.PBI_TIDAK_PUNYA_REKENING,
    LetterType.SKTM_PBI, LetterType.PBI_REKENING_LISTRIK -> KopStyle.DUA_LOGO
    // FASE 5 — Kematian: tanpa kop, header tabel 3 baris (FASE5_BRIEF.md).
    LetterType.KEMATIAN -> KopStyle.TABEL_KEMATIAN
    // a.n. Kades oleh Sekdes (pisah_kk) + Sekdes langsung / a.n. (usaha,
    // duda_janda, beda_luas, beda_nama, belum_menikah, tidak_mampu,
    // nikah_tidak_tercatat, pemakaman, tidak_memiliki_rumah,
    // pernyataan_akta_kelahiran) — FASE 5: signer → a.n. Sekdes.
    LetterType.PISAH_KK,
    LetterType.USAHA, LetterType.DUDA_JANDA,
    LetterType.BEDA_LUAS, LetterType.BEDA_NAMA, LetterType.BELUM_MENIKAH,
    LetterType.TIDAK_MAMPU, LetterType.NIKAH_TIDAK_TERCATAT,
    LetterType.PEMAKAMAN, LetterType.TIDAK_MEMILIKI_RUMAH,
    LetterType.PERNYATAAN_AKTA_KELAHIRAN -> KopStyle.SUBANG_LEFT
    // Sisanya ditandatangani Kepala Desa langsung.
    else -> KopStyle.GARUDA_CENTER
}

/** Tanggal yang dipakai di preview/PDF: isi form, atau hari ini bila kosong. */
fun effectiveTanggalSurat(tanggalSurat: String): String =
    tanggalSurat.ifBlank { todayId() }

/**
 * FASE 5 — Format tanggal khusus surat kematian (template: "Subang,
 * 01-01-1961"; tanggal meninggal "11-08-2022"; tanggal ttd
 * "Kasomalang Kulon, 10-11-2025"): DD-MM-YYYY.
 * Menerima "dd MMMM yyyy" id-ID, ISO yyyy-MM-dd, atau dd-MM-yyyy
 * (passthrough); bila tidak dikenali, kembalikan apa adanya.
 */
fun toDdMmYyyy(value: String): String {
    val v = value.trim()
    if (v.isEmpty()) return v
    if (Regex("^\\d{2}-\\d{2}-\\d{4}$").matches(v)) return v
    val iso = Regex("^(\\d{4})-(\\d{2})-(\\d{2})$").matchEntire(v)
    if (iso != null) return "${iso.groupValues[3]}-${iso.groupValues[2]}-${iso.groupValues[1]}"
    for (pattern in listOf("dd MMMM yyyy", "d MMMM yyyy", "dd MMM yyyy")) {
        try {
            val parsed = SimpleDateFormat(pattern, Locale("id", "ID")).parse(v)
            if (parsed != null) return SimpleDateFormat("dd-MM-yyyy", Locale("id", "ID")).format(parsed)
        } catch (e: Exception) {
            // Coba pola berikutnya.
        }
    }
    return v
}

/**
 * FASE 5 — Tahun dari tanggal surat (untuk nomor kematian
 * "460.3 / <reg> / KS / <tahun>"): cari tahun 4 digit di teks tanggal
 * (format id-ID / ISO / DD-MM-YYYY); fallback tahun berjalan.
 */
fun tahunDariTanggal(tanggalSurat: String): Int {
    val v = tanggalSurat.trim()
    if (v.isNotEmpty()) {
        for (pattern in listOf("dd MMMM yyyy", "d MMMM yyyy", "dd MMM yyyy", "yyyy-MM-dd", "dd-MM-yyyy")) {
            try {
                val parsed = SimpleDateFormat(pattern, Locale("id", "ID")).parse(v)
                if (parsed != null) return Calendar.getInstance().apply { time = parsed }.get(Calendar.YEAR)
            } catch (e: Exception) {
                // Coba pola berikutnya.
            }
        }
        Regex("(19|20)\\d{2}").find(v)?.value?.toIntOrNull()?.let { return it }
    }
    return currentYear()
}

/**
 * FASE 5 — Konstanta penutup template (FASE5_SPEC.md §3).
 * String PERSIS sama dengan web (letter-content.ts). Dipakai bersama oleh
 * PdfGenerator.kt DAN PreviewScreen.kt (satu sumber — jangan duplikasi).
 */
val CLOSING =
    "Demikian surat keterangan ini dibuat dengan sebenarnya agar dapat dipergunakan sebagaimana mestinya."
val CLOSING_FORM =
    "Demikian formulir ini diisi dengan sebenarnya dan dapat dipertanggungjawabkan."
val CLOSING_SPTJM =
    "Demikian surat pernyataan ini dibuat dengan sebenarnya dan apabila di kemudian hari ternyata tidak benar, saya bersedia dituntut sesuai dengan ketentuan peraturan perundang-undangan yang berlaku."
val CLOSING_AHLI_WARIS =
    "Demikian surat ini kami buat dengan sebenarnya dan kepada yang bersangkutan menjadi tahu adanya."
val CLOSING_PISAH_KK =
    "Demikian surat keterangan ini kami buat dengan sebenarnya agar yang berkepentingan menjadi tahu serta untuk dijadikan bahan periksa selanjutnya."
val CLOSING_SKT =
    "Demikian surat keterangan ini dibuat dengan sebenarnya dan apabila di kemudian hari ternyata keterangan ini tidak benar, maka segala akibat yang timbul menjadi tanggungjawab yang bersangkutan."
val CLOSING_BEDA_LUAS =
    "Demikian surat keterangan ini kami buat dengan sebenarnya agar yang berkepentingan menjadi tahu serta untuk dipergunakan sebagaimana mestinya."
val CLOSING_BEDA_NAMA = "$CLOSING_PISAH_KK dan digunakan sebagaimana mestinya."
val CLOSING_MISKIN_SEKOLAH =
    "Demikian surat keterangan ini kami buat, agar yang berkepentingan menjadi tahu serta untuk dipergunakan sebagaimana mestinya."
val CLOSING_MISKIN_KOLEKTIF =
    "Demikian surat ini dibuat dengan sebenarnya agar dapat digunakan sebagaimana mestinya."
val CLOSING_NIKAH =
    "Demikian surat keterangan ini dibuat dengan sebenarnya dan agar bisa dipergunakan sebagaimana mestinya."
val CLOSING_PEMAKAMAN =
    "Demikian surat keterangan ini kami buat dengan sebenarnya, agar yang berkepentingan menjadi tahu serta untuk dipergunakan sebagaimana mestinya."
val CLOSING_PERMOHONAN_RUMAH =
    "Demikian surat keterangan ini kami berikan, agar dipergunakan sebagaimana mestinya."
val CLOSING_USAHA =
    "Demikian surat keterangan ini kami buat, agar yang berkepentingan menjadi tahu serta untuk dipergunakan sebagaimana mestinya."
val CLOSING_F101 =
    "Demikian formulir ini saya/kami isi dengan sesungguhnya, apabila keterangan tersebut tidak sesuai dengan keadaan sebenarnya, saya bersedia dikenakan sanksi sesuai ketentuan peraturan perundang-undangan yang berlaku."
val CLOSING_F105 =
    "Demikian Surat Pernyataan ini kami buat dengan sebenarnya, apabila dalam keterangan yang saya berikan terdapat hal-hal yang tidak berdasarkan keadaan yang sebenarnya, kami bersedia dikenakan sanksi sesuai dengan ketentuan peraturan perundang-undangan yang berlaku."
val CLOSING_F106 =
    "Demikian Surat Pernyataan ini saya buat dengan sebenarnya, apabila dalam keterangan yang saya berikan terdapat hal-hal yang tidak berdasarkan keadaan yang sebenarnya, saya bersedia dikenakan sanksi sesuai ketentuan peraturan perundang-undangan yang berlaku."
val CLOSING_F203 =
    "Demikian Surat Pernyataan ini saya buat dengan sebenar-benarnya dan apabila di kemudian hari pernyataan saya ini tidak benar, maka saya bersedia diproses secara hukum sesuai dengan peraturan perundang-undangan dan dokumen yang diterbitkan akibat pernyataan ini menjadi tidak sah."
val CLOSING_F204 =
    "Demikian surat pernyataan ini saya buat dengan sebenar-benarnya, dan apabila di kemudian hari ternyata pernyataan saya ini tidak benar, maka saya bersedia diproses secara hukum sesuai dengan peraturan perundang-undangan dan dokumen yang diterbitkan akibat dari pernyataan ini menjadi tidak sah."
val CLOSING_SPTJM_PERKAWINAN =
    "Demikian surat pernyataan ini saya buat dengan sebenar-benarnya, apabila dalam keterangan yang saya berikan terdapat hal-hal yang tidak berdasarkan keadaan yang sebenarnya, saya bersedia dikenakan sanksi sesuai dengan ketentuan peraturan perundang-undangan yang berlaku."
val CLOSING_PBI_PERNYATAAN =
    "Demikian Pernyataan ini saya buat tanpa tekanan dari siapapun juga."
val CLOSING_SKTM_PBI =
    "Demikian Surat Keterangan ini kami buat agar yang berkepentingan menjadi tahu serta maklum."
val CLOSING_UNDANGAN =
    "Demikian kami sampaikan, atas perkenan dan perhatiannya diucapkan terimakasih."

/** "Subang", "12 Januari 1990" -> "Subang, 12 Januari 1990" (bagian kosong dilewati). */
fun ttl(tempat: String, tanggal: String): String =
    listOf(tempat, tanggal).filter { it.isNotEmpty() }.joinToString(", ")

fun rtRw(rt: String, rw: String): String {
    val r = rt.ifBlank { "-" }
    val w = rw.ifBlank { "-" }
    return "$r / $w"
}

/** "KASOMALANG KULON" -> "Kasomalang Kulon". */
fun titleCase(value: String): String =
    value.trim().split("\\s+".toRegex()).joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }

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
    /** FASE 5 — Harga taksiran tanah (template SKT Dayat) + No. Register. */
    val hargaTaksiranBangunan: String = "",
    val hargaTaksiranTanah: String = "",
    val hargaTaksiranJumlah: String = "",
    val noRegister: String = "",
    val tanggalRegister: String = "",
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
    /** FASE 5 — Nomor NIB sertifikat (template Beda Luas 3a). */
    val nomorNIB: String = "",
    val luasSertifikat: String = "",
    val nomorSPPT: String = "",
    val luasSPPT: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

/**
 * FASE 5 — ditulis ulang mengikuti template 3b (Ijazah vs Buku Nikah & KK):
 * 2 seksi "Keterangan data dari [dokumen]" (NIK, Nama, Tempat/Tgl Lahir,
 * Alamat tiap seksi). Field lama (namaLengkap/nik/namaDokumen1/
 * namaDokumen2/alasan) dipertahankan di data class demi kompatibilitas
 * riwayat, tapi TIDAK lagi dirender / diisi lewat form.
 */
@Serializable
data class BedaNamaData(
    val namaLengkap: String = "",
    val nik: String = "",
    val namaDokumen1: String = "",
    val namaDokumen2: String = "",
    val alasan: String = "",
    val dokumenPertama: String = "IJAZAH",
    val nikDokumenPertama: String = "",
    val namaDokumenPertama: String = "",
    val ttlDokumenPertama: String = "",
    val alamatDokumenPertama: String = "",
    val dokumenKedua: String = "BUKU NIKAH & KK",
    val nikDokumenKedua: String = "",
    val namaDokumenKedua: String = "",
    val ttlDokumenKedua: String = "",
    val alamatDokumenKedua: String = "",
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

/**
 * Satu ahli waris — FASE 3 (audit contoh "SURAT KETERANGAN AHLI WARIS
 * 2021.docx"): daftar dinamis (min. 1, maks. 20), kolom mengikuti contoh:
 * tabel utama memakai Nama + Hubungan Keluarga (+ Tanda Tangan untuk
 * ditandatangani tangan); lampiran "Data Ahli Waris" memakai
 * NIK / Jenis Kelamin / Tempat-Tanggal Lahir / Alamat.
 * Data contoh di DOCX (nama/NIK warga asli) HANYA referensi format —
 * tidak pernah jadi default/placeholder/data uji.
 */
@Serializable
data class AhliWarisItem(
    val nama: String = "",
    val nik: String = "",
    val jenisKelamin: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    /** "Istri" / "Suami" / "Anak" (sesuai contoh; istri/suami di baris pertama). */
    val hubungan: String = "",
    val alamat: String = ""
)

/**
 * FASE 3 — ditulis ulang dari hasil audit contoh DOCX 2021
 * (lihat FASE3_REPORT.md koordinator, tabel audit ahli waris):
 * - almarhum: + jenis kelamin (almarhum/almarhumah), hari/tanggal/
 *   tempat meninggal, rujukan Nomor Surat Kematian (bila ada);
 * - pasangan yang masih hidup disebut di pembuka (nama + Istri/Suami);
 * - daftar ahli waris DINAMIS [daftarAhliWaris] ganti 2 field tetap;
 * - saksi-saksi (Ketua RT + Ketua RW, sesuai contoh) + keperluan.
 * Field lama yang dihapus (tidak ada di contoh): namaAhliWaris1/2,
 * nikAhliWaris1/2, hubunganAhliWaris1/2, keterangan (diganti keperluan).
 * Data surat TIDAK disimpan persisten (hanya Riwayat yang disimpan),
 * jadi penghapusan field aman.
 */
@Serializable
data class AhliWarisData(
    val namaPeninggal: String = "",
    /** FASE 5 — TIDAK lagi dirender / diisi lewat form (tidak ada di contoh
     *  DOCX); dipertahankan di data class demi kompatibilitas riwayat. */
    val nikPeninggal: String = "",
    val jenisKelaminPeninggal: String = "",
    val alamatPeninggal: String = "",
    /** FASE 5 — sebutan peninggal (seperti web): "Almarhum" / "Almarhumah". */
    val sebutanPeninggal: String = "Almarhum",
    val hariMeninggal: String = "",
    val tanggalMeninggal: String = "",
    val tempatMeninggal: String = "",
    /** Rujukan "sesuai Surat Kematian Nomor: ..." (bila ada di contoh). */
    val nomorSuratKematian: String = "",
    /** Pasangan yang masih hidup (disebut di paragraf pembuka). */
    val namaPasangan: String = "",
    val hubunganPasangan: String = "Istri",
    /** FASE 5 — jumlah anak dari perkawinan (input form); 0 = hitung dari tabel. */
    val jumlahAnak: Int = 0,
    /** Daftar ahli waris dinamis: baris pertama pasangan, berikutnya anak-anak. */
    val daftarAhliWaris: List<AhliWarisItem> = listOf(AhliWarisItem()),
    /** Saksi-saksi (contoh: Ketua RT + Ketua RW). */
    val namaSaksi1: String = "",
    val jabatanSaksi1: String = "",
    val namaSaksi2: String = "",
    val jabatanSaksi2: String = "",
    /** "Untuk Keperluan : ..." (contoh: Kelengkapan Dokumen, Pembuatan sertipikat). */
    val keperluan: String = "",
    /** FASE 5 — Keterangan tambahan (opsional, seperti web). */
    val keterangan: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

/** "almarhum" / "almarhumah" menurut sebutan peninggal (web) atau jenis
 *  kelamin peninggal (data lama); kosong = netral. */
fun sebutanAlmarhum(jenisKelamin: String): String = when (jenisKelamin) {
    "Perempuan", "Almarhumah" -> "almarhumah"
    "Laki-laki", "Almarhum" -> "almarhum"
    else -> "almarhum/almarhumah"
}

/** Sebutan peninggal untuk satu data ahli waris: pakai sebutanPeninggal
 *  (FASE 5) bila terisi, fallback ke jenisKelaminPeninggal (data lama). */
fun sebutanOf(data: AhliWarisData): String =
    sebutanAlmarhum(data.sebutanPeninggal.ifBlank { data.jenisKelaminPeninggal })

/**
 * Jumlah anak almarhum: pakai input jumlahAnak (form) bila > 0;
 * bila 0, hitung dari daftar — baris ber-hubungan "Anak"
 * (contoh: "Anak", "Anak kandung"); bila tak ada yang ditandai,
 * daftar terisi minus pasangan (baris pertama). Dipakai kalimat
 * "telah dilahirkan N orang anak" di pembuka.
 */
fun jumlahAnakAhliWaris(data: AhliWarisData): Int {
    if (data.jumlahAnak > 0) return data.jumlahAnak
    val terisi = data.daftarAhliWaris.filter { it.nama.isNotBlank() || it.nik.isNotBlank() }
    val anak = terisi.count { it.hubungan.contains("anak", ignoreCase = true) }
    if (anak > 0) return anak
    val pasangan = terisi.count {
        it.hubungan.equals("Istri", ignoreCase = true) || it.hubungan.equals("Suami", ignoreCase = true)
    }
    return (terisi.size - pasangan).coerceAtLeast(0)
}

/**
 * Paragraf pembuka SKAW mengikuti contoh DOCX 2021 (SATU sumber untuk
 * PdfGenerator dan PreviewScreen): almarhum + meninggal (hari/tanggal/
 * tempat, + rujukan Surat Kematian bila ada) + pasangan yang masih
 * hidup + jumlah anak. Klausa yang datanya kosong dilewati.
 */
fun paragrafPembukaAhliWaris(data: AhliWarisData, namaDesaTitleCase: String): String {
    val sebutan = sebutanOf(data)
    // FASE 5: "...telah meninggal dunia pada hari [X], tanggal [Y], di [Z]..."
    // (kata "tanggal" sempat hilang di Android).
    val meninggalParts = mutableListOf<String>()
    if (data.hariMeninggal.isNotBlank()) meninggalParts.add("hari ${data.hariMeninggal}")
    if (data.tanggalMeninggal.isNotBlank()) meninggalParts.add("tanggal ${data.tanggalMeninggal}")
    if (data.tempatMeninggal.isNotBlank()) meninggalParts.add("di ${data.tempatMeninggal}")
    val sb = StringBuilder()
    sb.append("Yang bertanda tangan di bawah ini, Kepala Desa $namaDesaTitleCase, dengan ini menerangkan bahwa $sebutan ${data.namaPeninggal}")
    sb.append(" yang telah meninggal dunia")
    if (meninggalParts.isNotEmpty()) sb.append(" pada ${meninggalParts.joinToString(", ")}")
    if (data.nomorSuratKematian.isNotBlank()) sb.append(" sesuai Surat Kematian Nomor: ${data.nomorSuratKematian}")
    if (data.namaPasangan.isNotBlank()) {
        sb.append(", semasa hidupnya $sebutan telah menikah sah dengan Satu (1) orang ${data.hubunganPasangan} bernama ${data.namaPasangan} yang masih hidup sampai sekarang")
        val anak = jumlahAnakAhliWaris(data)
        if (anak > 0) sb.append(". Dari hasil Perkawinan tersebut telah dilahirkan $anak orang anak")
    }
    sb.append(".")
    return sb.toString()
}

@Serializable
data class NikahTidakTercatatData(
    val namaPria: String = "",
    val nikPria: String = "",
    val tempatLahirPria: String = "",
    val tanggalLahirPria: String = "",
    /** FASE 5 — Bin + Agama (template Nikah Tidak Tercatat). */
    val binSuami: String = "",
    val agamaSuami: String = "",
    val namaWanita: String = "",
    val nikWanita: String = "",
    val tempatLahirWanita: String = "",
    val tanggalLahirWanita: String = "",
    val binIstri: String = "",
    val agamaIstri: String = "",
    val tanggalPernikahan: String = "",
    val tempatPernikahan: String = "",
    /** FASE 5 — wali + 2 saksi + mas kawin + status perkawinan (template). */
    val namaWali: String = "",
    val saksi1: String = "",
    val saksi2: String = "",
    val masKawin: String = "Alat sholat",
    val statusPerkawinan: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

/**
 * FASE 5 — Narasi kanonik Nikah Tidak Tercatat (template): wali/saksi
 * hanya bila terisi; status perkawinan hanya bila terisi.
 */
fun narasiNikahTidakTercatat(data: NikahTidakTercatatData): String {
    val sb = StringBuilder()
    sb.append("Menurut sepengetahuan kami, orang tersebut di atas telah menikah pada tanggal ${data.tanggalPernikahan}")
    val wali = data.namaWali.isNotBlank()
    val s1 = data.saksi1.isNotBlank()
    val s2 = data.saksi2.isNotBlank()
    if (wali || s1 || s2) {
        sb.append(" yang dilaksanakan")
        if (wali) sb.append(" oleh walinya Bapak ${data.namaWali}")
        if (s1 || s2) {
            sb.append(" dengan saksi 2 orang,")
            if (s1) sb.append(" 1. Bapak ${data.saksi1}")
            if (s2) sb.append(" 2. Bapak ${data.saksi2}")
        }
    }
    if (data.masKawin.isNotBlank()) sb.append(" dengan mas kawin ${data.masKawin}")
    if (data.statusPerkawinan.isNotBlank()) sb.append(". Status perkawinan ${data.statusPerkawinan}")
    sb.append(".")
    return sb.toString()
}

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
    /** FASE 5 — seksi meninggal + disemayamkan (template Pemakaman). */
    val hariMeninggal: String = "",
    val jamMeninggal: String = "",
    val tempatMeninggal: String = "",
    val hariDisemayamkan: String = "",
    val tanggalDisemayamkan: String = "",
    val tempatDisemayamkan: String = "",
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
    /** FASE 5 — tembusan default template (pengguna bisa edit). */
    val daftarTembusan: String = "Camat Kasomalang; Ketua BPD Desa Kasomalang Kulon; Arsip",
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
    /** FASE 5 — RT/RW untuk narasi "Berdasarkan keterangan dari Ketua RT ... RW ...". */
    val rt: String = "",
    val rw: String = "",
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
    /** FASE 5 — kolom NIK / SHDK / Keterangan (template F-1.06). */
    val nik: String = "",
    val shdk: String = "",
    val keterangan: String = "",
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
    /** FASE 5 — default KOSONG (sebelumnya "Kelahiran"); pengguna memilih jenis. */
    val jenisPelaporan: String = "",
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
    /** FASE 5 — Pukul Lahir (union seksi template F-2.01). */
    val anakPukulLahir: String = "",
    // Seksi Lahir Mati
    val lahirMatiNama: String = "",
    val lahirMatiTanggal: String = "",
    val lahirMatiPukul: String = "",
    val lahirMatiTempat: String = "",
    val lahirMatiSebab: String = "",
    /** FASE 5 — Jenis Kelamin + Keterangan lahir mati (union template). */
    val lahirMatiJenisKelamin: String = "",
    val lahirMatiKeterangan: String = "",
    // Seksi Perkawinan / Perceraian
    val pasanganSuamiNama: String = "",
    val pasanganIstriNama: String = "",
    val tanggalPeristiwa: String = "",
    val tempatPeristiwa: String = "",
    /** FASE 5 — Keterangan seksi perkawinan/perceraian (union template). */
    val pasanganKeterangan: String = "",
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
    /** FASE 5 — field identitas template xls sheet `2` (urutan render:
     *  No KK, NIK, Nama, TTL, JK, WN, Status Kawin, Agama, Pekerjaan, Alamat). */
    val noKK: String = "",
    val jenisKelamin: String = "",
    val wargaNegara: String = "Indonesia",
    val statusPerkawinan: String = "",
    val agama: String = "",
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

/* ==================================================================
   FASE 5 — 3 tipe baru (FASE5_SPEC.md §2.5). Total 33 tipe.
   ================================================================== */

/** Satu baris daftar anak SKTM PBI (tabel opsional, xls sheet `3`). */
@Serializable
data class AnggotaPbi(
    val noKK: String = "",
    val nik: String = "",
    val nama: String = "",
    val ttl: String = "",
    val jenisKelamin: String = "",
    val wargaNegara: String = "Indonesia",
    val statusPerkawinan: String = "",
    val agama: String = "",
    val pekerjaan: String = "",
    val alamat: String = ""
)

@Serializable
data class SktmPbiData(
    val noKK: String = "",
    val nik: String = "",
    val namaLengkap: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val jenisKelamin: String = "",
    val wargaNegara: String = "Indonesia",
    val statusPerkawinan: String = "",
    val agama: String = "Islam",
    val pekerjaan: String = "",
    val alamat: String = "",
    /** Tujuan: RSUD Subang / Institut Pertanian Bogor (IPB) /
     *  Pengajuan PBI KIS / RSUD Ciereng Subang. */
    val tujuan: String = "",
    /** Daftar anak (tabel opsional — dirender bila ada baris terisi). */
    val daftarAnak: List<AnggotaPbi> = emptyList(),
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class PbiRekeningListrikData(
    val noKK: String = "",
    val nik: String = "",
    val namaLengkap: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val jenisKelamin: String = "",
    val wargaNegara: String = "Indonesia",
    val statusPerkawinan: String = "",
    val agama: String = "Islam",
    val pekerjaan: String = "",
    val alamat: String = "",
    /** Tetangga yang aliran listriknya ditumpang + nomor pelanggannya. */
    val namaTetangga: String = "",
    val nomorPelanggan: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

@Serializable
data class PernyataanAktaKelahiranData(
    val nik: String = "",
    val namaLengkap: String = "",
    val tempatLahir: String = "",
    val tanggalLahir: String = "",
    val namaAyah: String = "",
    val namaIbu: String = "",
    val alamat: String = "",
    val nomorSurat: String = "",
    val tanggalSurat: String = ""
)

/** Opsi Tujuan SKTM PBI (FASE5_SPEC.md §2.5). */
val SKTM_PBI_TUJUAN: List<String> = listOf(
    "RSUD Subang",
    "Institut Pertanian Bogor (IPB)",
    "Pengajuan PBI KIS",
    "RSUD Ciereng Subang"
)

/** Kalimat keperluan SKTM PBI per tujuan (template xls sheet `3`). */
fun kalimatTujuanSktmPbi(tujuan: String): String = when (tujuan) {
    "RSUD Subang" -> "pembebasan biaya rawat inap di RSUD SUBANG"
    "Institut Pertanian Bogor (IPB)" -> "Pembebasan/keringanan biaya pendidikan di Institut Pertanian Bogor (IPB)"
    "Pengajuan PBI KIS" -> "Pengajuan PBI KIS"
    "RSUD Ciereng Subang" -> "pembebasan biaya rawat inap di RSUD Ciereng Subang"
    else -> tujuan
}

/** Pecah daftar tembusan ("a; b; c" / koma / baris) menjadi item-item (seperti web). */
fun tembusanItems(daftarTembusan: String): List<String> =
    daftarTembusan.split(';', '\n', ',').map { it.trim() }.filter { it.isNotBlank() }

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
 * FASE 5 — 27 opsi Jenis Permohonan F-1.02 (template PDF F-1.02:
 * 4 kelompok KK / KTP-el / KIA / Perubahan Data).
 */
val F102_JENIS_PERMOHONAN: List<String> = listOf(
    "KK Baru — Membentuk Keluarga Baru",
    "KK Baru — Penggantian Kepala Keluarga",
    "KK Baru — Pisah KK",
    "KK Baru — Pindah Datang",
    "KK Baru — WNI dari LN karena Pindah",
    "KK Baru — Rentan Adminduk",
    "KK Perubahan Data — Menumpang dalam KK",
    "KK Perubahan Data — Peristiwa Penting",
    "KK Perubahan Data — Perubahan elemen Data dalam KK",
    "KK Hilang/Rusak — Hilang",
    "KK Hilang/Rusak — Rusak",
    "KTP-el Baru",
    "KTP-el Pindah Datang",
    "KTP-el Hilang/Rusak — Hilang",
    "KTP-el Hilang/Rusak — Rusak",
    "Perpanjangan ITAP",
    "KTP-el Perubahan Status Kewarganegaraan",
    "KTP-el Luar Domisili",
    "KTP-el Transmigrasi",
    "KIA Baru",
    "KIA Hilang/Rusak — Hilang",
    "KIA Hilang/Rusak — Rusak",
    "KIA Perpanjangan ITAP",
    "KIA Lainnya",
    "Perubahan Data — KK",
    "Perubahan Data — KTP-el",
    "Perubahan Data — KIA"
)

/** FASE 5 — 4 opsi Jenis Permohonan F-1.03 (template PDF F-1.03). */
val F103_JENIS_PERMOHONAN: List<String> = listOf(
    "Surat Keterangan Kependudukan",
    "Surat Keterangan Pindah",
    "SKPLN",
    "SKTT Bagi Orang Asing Tinggal Terbatas"
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
 * domisili Lembaga/Perusahaan, SKTM Sekolah/Kolektif, Duda/Janda,
 * dan SPTJM Perkawinan/Perceraian mengubah judul.
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
    // FASE 5 — judul dinamis Duda / Janda (template xlsm).
    LetterType.DUDA_JANDA -> when ((data as DudaJandaData).statusDudaJanda) {
        "Janda" -> "SURAT KETERANGAN JANDA"
        else -> "SURAT KETERANGAN DUDA"
    }
    // FASE 5 — judul dinamis panjang SPTJM Perkawinan / Perceraian.
    LetterType.SPTJM_PERKAWINAN_PERCERAIAN -> when ((data as SptjmPerkawinanPerceraianData).jenisSPTJM) {
        "Perceraian" -> "SURAT PERNYATAAN TANGGUNG JAWAB MUTLAK PERCERAIAN BELUM TERCATAT"
        else -> "SURAT PERNYATAAN TANGGUNG JAWAB MUTLAK PERKAWINAN BELUM TERCATAT"
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
    LetterType.SKTM_PBI -> (data as SktmPbiData).namaLengkap
    LetterType.PBI_REKENING_LISTRIK -> (data as PbiRekeningListrikData).namaLengkap
    LetterType.PERNYATAAN_AKTA_KELAHIRAN -> (data as PernyataanAktaKelahiranData).namaLengkap
}

/** Data kosong untuk satu tipe. FASE 3: nomor surat default KOSONG (tidak lagi diisi pola). */
fun getEmptyLetterData(type: LetterType): Any = when (type) {
    LetterType.DOMISILI -> DomisiliData()
    LetterType.PISAH_KK -> PisahKkData()
    LetterType.SKT_DAYAT -> SktDayatData()
    LetterType.BEDA_LUAS -> BedaLuasData()
    LetterType.BEDA_NAMA -> BedaNamaData()
    LetterType.BELUM_MENIKAH -> BelumMenikahData()
    LetterType.TIDAK_MAMPU -> TidakMampuData()
    LetterType.AHLI_WARIS -> AhliWarisData()
    LetterType.NIKAH_TIDAK_TERCATAT -> NikahTidakTercatatData()
    LetterType.PEMAKAMAN -> PemakamanData()
    LetterType.TIDAK_MEMILIKI_RUMAH -> TidakMemilikiRumahData()
    LetterType.UNDANGAN -> UndanganData()
    LetterType.KEMATIAN -> KematianData()
    LetterType.USAHA -> UsahaData()
    LetterType.DUDA_JANDA -> DudaJandaData()
    LetterType.F101 -> F101Data()
    LetterType.F102 -> F102Data()
    LetterType.F103 -> F103Data()
    LetterType.F104 -> F104Data()
    LetterType.F105 -> F105Data()
    LetterType.F106 -> F106Data()
    LetterType.F115 -> F115Data()
    LetterType.F201 -> F201Data()
    LetterType.F203 -> F203Data()
    LetterType.F204 -> F204Data()
    LetterType.SPTJM_KEMATIAN -> SptjmKematianData()
    LetterType.SPTJM_PERKAWINAN_PERCERAIAN -> SptjmPerkawinanPerceraianData()
    LetterType.PBI_ASESMEN -> PbiAsesmenData()
    LetterType.PBI_PERNYATAAN -> PbiPernyataanData()
    LetterType.PBI_TIDAK_PUNYA_REKENING -> PbiTidakPunyaRekeningData()
    LetterType.SKTM_PBI -> SktmPbiData()
    LetterType.PBI_REKENING_LISTRIK -> PbiRekeningListrikData()
    LetterType.PERNYATAAN_AKTA_KELAHIRAN -> PernyataanAktaKelahiranData()
}
