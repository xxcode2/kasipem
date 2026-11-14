package com.kasipem.data

import android.content.Context
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import java.io.File
import java.io.FileOutputStream

/**
 * Generator PDF 33 surat/formulir (iText 5). Isi SAMA dengan kanon web (FIX_BRIEF.md + FILE_INTEGRATION_BRIEF.md + FASE5_SPEC.md).
 * Kop FASE 3 — SATU penentu [kopFor] (Models.kt) dipakai di sini dan di
 * PreviewScreen: ttd Kades → logo Garuda SAJA di tengah paling atas;
 * ttd a.n. Kades oleh Sekdes / ttd Sekdes langsung → logo Kabupaten
 * Subang di kiri; SPTJM/PBI → dua logo seperti semula; formulir
 * Dukcapil seri F → kop Disdukcapil Subang TEKS saja (tanpa logo desa);
 * FASE 5: kematian → TANPA kop, header tabel 3 baris, landscape, isi kiri 62%.
 * Bila logo tidak ada/rusak, kop tetap tampil sebagai teks — tidak crash.
 * Font FASE 3 (samakan dengan web): isi 12pt, judul 14pt bold, teks kop
 * 12pt (baris desa 14pt); tabel padat 8–10pt.
 * Tanda tangan: Kepala Desa (default); pisah_kk/usaha/duda_janda/beda_luas/
 * beda_nama/belum_menikah/tidak_mampu/nikah_tidak_tercatat/pemakaman/
 * tidak_memiliki_rumah/pernyataan_akta_kelahiran = a.n. Kepala Desa oleh
 * Sekretaris Desa (Fase 4: blok Sekdes selalu "a.n. Kepala Desa Kasomalang
 * Kulon / Sekretaris Desa") — KECUALI kematian (FASE 5, ikut template:
 * "Sekretaris Desa Kasomalang Kulon" tanpa a.n.).
 * tidak_mampu = a.n. Sekdes + "Mengetahui, Ketua RW" di kiri (Umum) /
 * tanpa leftSigner (Sekolah) / "Ketua RW" (Kolektif);
 * ahli_waris = Kepala Desa + "Mengetahui, Camat Kasomalang" di kiri.
 * Formulir panjang (F-1.01, F-2.01, F-1.03, PBI Asesmen) BOLEH multi-halaman.
 * Penutup FASE 5: setiap generate*Pdf merender kalimat penutup template
 * (§3 FASE5_SPEC.md) tepat sebelum addFooter / addPlaceDate — kecuali
 * kematian (template tidak punya penutup).
 */
class PdfGenerator(private val context: Context) {

    private val baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED)

    // FASE 5 — Konstanta penutup (CLOSING_*) tinggal di Models.kt (top-level,
    // dipakai bersama PdfGenerator + PreviewScreen) — lihat FASE5_SPEC.md §3.

    private enum class Signer {
        KADES,
        SEKDES_AN,
        /** FASE 5 — kematian SAJA: "Sekretaris Desa Kasomalang Kulon" tanpa a.n. */
        SEKDES_KEMATIAN
    }

    // ------------------------------------------------------------------
    // Dispatcher — satu pintu masuk untuk semua tipe.
    // ------------------------------------------------------------------
    fun generatePdf(
        headerData: HeaderData,
        letterType: LetterType,
        letterData: Any,
        outputFile: File
    ) {
        when (letterType) {
            LetterType.DOMISILI -> generateDomisiliPdf(headerData, letterData as DomisiliData, outputFile)
            LetterType.PISAH_KK -> generatePisahKkPdf(headerData, letterData as PisahKkData, outputFile)
            LetterType.SKT_DAYAT -> generateSktDayatPdf(headerData, letterData as SktDayatData, outputFile)
            LetterType.BEDA_LUAS -> generateBedaLuasPdf(headerData, letterData as BedaLuasData, outputFile)
            LetterType.BEDA_NAMA -> generateBedaNamaPdf(headerData, letterData as BedaNamaData, outputFile)
            LetterType.BELUM_MENIKAH -> generateBelumMenikahPdf(headerData, letterData as BelumMenikahData, outputFile)
            LetterType.TIDAK_MAMPU -> generateTidakMampuPdf(headerData, letterData as TidakMampuData, outputFile)
            LetterType.AHLI_WARIS -> generateAhliWarisPdf(headerData, letterData as AhliWarisData, outputFile)
            LetterType.NIKAH_TIDAK_TERCATAT -> generateNikahTidakTercatatPdf(headerData, letterData as NikahTidakTercatatData, outputFile)
            LetterType.PEMAKAMAN -> generatePemakamanPdf(headerData, letterData as PemakamanData, outputFile)
            LetterType.TIDAK_MEMILIKI_RUMAH -> generateTidakMemilikiRumahPdf(headerData, letterData as TidakMemilikiRumahData, outputFile)
            LetterType.UNDANGAN -> generateUndanganPdf(headerData, letterData as UndanganData, outputFile)
            // ---- Fase 2: 18 tipe baru ----
            LetterType.KEMATIAN -> generateKematianPdf(headerData, letterData as KematianData, outputFile)
            LetterType.USAHA -> generateUsahaPdf(headerData, letterData as UsahaData, outputFile)
            LetterType.DUDA_JANDA -> generateDudaJandaPdf(headerData, letterData as DudaJandaData, outputFile)
            LetterType.F101 -> generateF101Pdf(headerData, letterData as F101Data, outputFile)
            LetterType.F102 -> generateF102Pdf(headerData, letterData as F102Data, outputFile)
            LetterType.F103 -> generateF103Pdf(headerData, letterData as F103Data, outputFile)
            LetterType.F104 -> generateF104Pdf(headerData, letterData as F104Data, outputFile)
            LetterType.F105 -> generateF105Pdf(headerData, letterData as F105Data, outputFile)
            LetterType.F106 -> generateF106Pdf(headerData, letterData as F106Data, outputFile)
            LetterType.F115 -> generateF115Pdf(headerData, letterData as F115Data, outputFile)
            LetterType.F201 -> generateF201Pdf(headerData, letterData as F201Data, outputFile)
            LetterType.F203 -> generateF203Pdf(headerData, letterData as F203Data, outputFile)
            LetterType.F204 -> generateF204Pdf(headerData, letterData as F204Data, outputFile)
            LetterType.SPTJM_KEMATIAN -> generateSptjmKematianPdf(headerData, letterData as SptjmKematianData, outputFile)
            LetterType.SPTJM_PERKAWINAN_PERCERAIAN -> generateSptjmPerkawinanPerceraianPdf(headerData, letterData as SptjmPerkawinanPerceraianData, outputFile)
            LetterType.PBI_ASESMEN -> generatePbiAsesmenPdf(headerData, letterData as PbiAsesmenData, outputFile)
            LetterType.PBI_PERNYATAAN -> generatePbiPernyataanPdf(headerData, letterData as PbiPernyataanData, outputFile)
            LetterType.PBI_TIDAK_PUNYA_REKENING -> generatePbiTidakPunyaRekeningPdf(headerData, letterData as PbiTidakPunyaRekeningData, outputFile)
            // ---- Fase 5: 3 tipe baru ----
            LetterType.SKTM_PBI -> generateSktmPbiPdf(headerData, letterData as SktmPbiData, outputFile)
            LetterType.PBI_REKENING_LISTRIK -> generatePbiRekeningListrikPdf(headerData, letterData as PbiRekeningListrikData, outputFile)
            LetterType.PERNYATAAN_AKTA_KELAHIRAN -> generatePernyataanAktaKelahiranPdf(headerData, letterData as PernyataanAktaKelahiranData, outputFile)
        }
    }

    // ------------------------------------------------------------------
    // 1. Domisili
    // ------------------------------------------------------------------
    fun generateDomisiliPdf(headerData: HeaderData, data: DomisiliData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.DOMISILI)
        addTitle(document, effectivePdfTitle(LetterType.DOMISILI, data), effectiveNomorSurat(data.nomorSurat, LetterType.DOMISILI))
        // FASE 5 — narasi = versi WEB (template DOCX); urutan field NIK dulu.
        // Signer TETAP Kades (konflik template DOCX a.n. Sekdes vs xlsm Kades — butuh keputusan user).
        when (data.jenisDomisili) {
            "Lembaga" -> {
                addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, Kecamatan ${titleCase(headerData.namaKecamatan)}, Kabupaten ${titleCase(headerData.namaKabupaten.removePrefix("KABUPATEN "))}, menerangkan dengan sesungguhnya bahwa:")
                addDataTable(
                    document,
                    listOf(
                        "Nama Lembaga" to data.namaLembaga,
                        "Nama Pimpinan" to data.namaPimpinan,
                        "Alamat" to data.alamatOrganisasi
                    )
                )
                addContent(document, "Lembaga tersebut di atas benar berdomisili di ${data.alamatOrganisasi} dan dipimpin oleh ${data.namaPimpinan}.")
                addContent(document, "Surat keterangan ini kami berikan untuk keperluan ${data.keperluanSurat}.")
            }
            "Perusahaan" -> {
                addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, Kecamatan ${titleCase(headerData.namaKecamatan)}, Kabupaten ${titleCase(headerData.namaKabupaten.removePrefix("KABUPATEN "))}, menerangkan dengan sesungguhnya bahwa:")
                addDataTable(
                    document,
                    listOf(
                        "Nama Perusahaan" to data.namaPerusahaan,
                        "Nama Pimpinan" to data.namaPimpinan,
                        "Nama Sekretaris" to data.namaSekretaris,
                        "Bergerak di Bidang" to data.bidangUsaha,
                        "Alamat" to data.alamatOrganisasi
                    )
                )
                addContent(document, "Perusahaan tersebut di atas benar berdomisili di ${data.alamatOrganisasi} dan bergerak di bidang ${data.bidangUsaha}.")
                addContent(document, "Surat keterangan ini kami berikan untuk keperluan ${data.keperluanSurat}.")
            }
            else -> {
                addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, Kecamatan ${titleCase(headerData.namaKecamatan)}, Kabupaten ${titleCase(headerData.namaKabupaten.removePrefix("KABUPATEN "))}, menerangkan dengan sesungguhnya bahwa:")
                addDataTable(
                    document,
                    listOf(
                        "NIK" to data.nik,
                        "Nama" to data.namaLengkap,
                        "Tempat/Tanggal Lahir" to listOf(data.tempatLahir, data.tanggalLahir).filter { it.isNotEmpty() }.joinToString(", "),
                        "Jenis Kelamin" to data.jenisKelamin,
                        "Pekerjaan" to data.pekerjaan,
                        "Alamat (KTP)" to data.alamatAsal,
                        "RT/RW" to rtRw(data.rt, data.rw),
                        "Alamat Domisili Saat Ini" to data.alamatDomisili
                    )
                )
                val rtRwText = if (data.rt.isNotBlank() || data.rw.isNotBlank()) "RT ${data.rt} RW ${data.rw}, " else ""
                addContent(document, "Orang tersebut sudah tidak tinggal di alamat sebagaimana tercantum di atas, dan berdasarkan keterangan dari Ketua RT/RW, orang tersebut saat ini benar berdomisili di $rtRwText${data.alamatDomisili}.")
                addContent(document, "Surat keterangan ini kami berikan untuk keperluan ${data.keperluanSurat}.")
            }
        }
        addContent(document, CLOSING)
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES)
        document.close()
    }

    // ------------------------------------------------------------------
    // 2. Pisah KK — ttd a.n. Kepala Desa oleh Sekretaris Desa.
    // ------------------------------------------------------------------
    fun generatePisahKkPdf(headerData: HeaderData, data: PisahKkData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.PISAH_KK)
        addTitle(document, LetterType.PISAH_KK, effectiveNomorSurat(data.nomorSurat, LetterType.PISAH_KK))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        addDataTable(
            document,
            listOf(
                "NIK (1)" to data.nik1,
                "Nama (1)" to data.nama1,
                "NIK (2)" to data.nik2,
                "Nama (2)" to data.nama2,
                "Nomor KK Lama" to data.nomorKKLama,
                "Alamat" to data.alamat
            )
        )
        addContent(document, "Nama-nama tersebut di atas adalah benar warga Desa kami dan telah Pisah KK dari KK Nomor ${data.nomorKKLama} untuk membentuk Kartu Keluarga baru dengan alamat ${data.alamat}.")
        addContent(document, CLOSING_PISAH_KK)
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN)
        document.close()
    }

    // ------------------------------------------------------------------
    // 3. SKT Tanah (Dayat)
    // ------------------------------------------------------------------
    fun generateSktDayatPdf(headerData: HeaderData, data: SktDayatData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.SKT_DAYAT)
        addTitle(document, LetterType.SKT_DAYAT, effectiveNomorSurat(data.nomorSurat, LetterType.SKT_DAYAT))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "NIK" to data.nik,
                "Tempat/Tanggal Lahir" to listOf(data.tempatLahir, data.tanggalLahir).filter { it.isNotEmpty() }.joinToString(", "),
                "Alamat" to data.alamat,
                "Luas Tanah" to data.luasTanah.ifEmpty { "" }.let { if (it.isEmpty()) "" else "$it m²" },
                "Luas Bangunan" to data.luasBangunan.ifEmpty { "" }.let { if (it.isEmpty()) "" else "$it m²" },
                "SPPT / NOP" to data.spptNop
            )
        )
        addContent(document, "Tanah tersebut di atas adalah benar milik / dikuasai oleh yang bersangkutan dengan batas-batas sebagaimana tersebut di atas, dan sampai saat ini tidak dalam sengketa dengan pihak mana pun.")
        addContent(document, "Batas-batas tanah tersebut adalah sebagai berikut:")
        addDataTable(
            document,
            listOf(
                "Sebelah Utara" to data.batasUtara,
                "Sebelah Timur" to data.batasTimur,
                "Sebelah Selatan" to data.batasSelatan,
                "Sebelah Barat" to data.batasBarat
            )
        )
        // FASE 5 — Harga taksiran tanah + No. Register (template SKT Dayat).
        if (data.hargaTaksiranBangunan.isNotBlank() || data.hargaTaksiranTanah.isNotBlank() || data.hargaTaksiranJumlah.isNotBlank()) {
            addContent(document, "HARGA TAKSIRAN TANAH :")
            addDataTable(
                document,
                listOf(
                    "Bangunan" to data.hargaTaksiranBangunan.ifBlank { "" }.let { if (it.isEmpty()) "" else "Rp $it" },
                    "Tanah" to data.hargaTaksiranTanah.ifBlank { "" }.let { if (it.isEmpty()) "" else "Rp $it" },
                    "Jumlah" to data.hargaTaksiranJumlah.ifBlank { "" }.let { if (it.isEmpty()) "" else "Rp $it" }
                )
            )
        }
        if (data.noRegister.isNotBlank() || data.tanggalRegister.isNotBlank()) {
            addDataTable(
                document,
                listOf(
                    "No. Register" to data.noRegister,
                    "Tanggal" to data.tanggalRegister
                )
            )
        }
        addContent(document, CLOSING_SKT)
        addFooter(
            document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES,
            leftNote = "Mengetahui,\nCamat Kasomalang"
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 4. Beda Luas Tanah
    // ------------------------------------------------------------------
    fun generateBedaLuasPdf(headerData: HeaderData, data: BedaLuasData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.BEDA_LUAS)
        addTitle(document, LetterType.BEDA_LUAS, effectiveNomorSurat(data.nomorSurat, LetterType.BEDA_LUAS))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "NIK" to data.nik,
                "Alamat" to data.alamat,
                "RT/RW" to rtRw(data.rt, data.rw),
                "Nomor Sertifikat" to data.nomorSertifikat,
                "Nomor NIB" to data.nomorNIB,
                "Luas Sertifikat" to data.luasSertifikat.ifEmpty { "" }.let { if (it.isEmpty()) "" else "$it m²" },
                "Nomor SPPT" to data.nomorSPPT,
                "Luas SPPT" to data.luasSPPT.ifEmpty { "" }.let { if (it.isEmpty()) "" else "$it m²" }
            )
        )
        addContent(document, "Adalah benar bahwa terdapat perbedaan luas tanah antara Sertifikat dan SPPT yang dimiliki oleh yang bersangkutan, sedangkan pemilik tanah tersebut adalah orang yang sama. Surat keterangan ini diberikan untuk keperluan administrasi pertanahan.")
        addContent(document, CLOSING_BEDA_LUAS)
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN)
        document.close()
    }

    // ------------------------------------------------------------------
    // 5. Beda Nama
    // ------------------------------------------------------------------
    fun generateBedaNamaPdf(headerData: HeaderData, data: BedaNamaData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.BEDA_NAMA)
        addTitle(document, LetterType.BEDA_NAMA, effectiveNomorSurat(data.nomorSurat, LetterType.BEDA_NAMA))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        // FASE 5 — 2 seksi "Keterangan data dari [dokumen]" (template 3b).
        addSectionTitle(document, "Keterangan data dari ${data.dokumenPertama.ifBlank { "IJAZAH" }} :")
        addDataTable(
            document,
            listOf(
                "NIK" to data.nikDokumenPertama,
                "Nama" to data.namaDokumenPertama,
                "Tempat/Tgl Lahir" to data.ttlDokumenPertama,
                "Alamat" to data.alamatDokumenPertama
            )
        )
        addSectionTitle(document, "Keterangan data dari ${data.dokumenKedua.ifBlank { "BUKU NIKAH & KK" }} :")
        addDataTable(
            document,
            listOf(
                "NIK" to data.nikDokumenKedua,
                "Nama" to data.namaDokumenKedua,
                "Tempat/Tgl Lahir" to data.ttlDokumenKedua,
                "Alamat" to data.alamatDokumenKedua
            )
        )
        addContent(document, "Terdapat perbedaan Nama/Tanggal/Tahun kelahiran yang tertulis di ${data.dokumenPertama.ifBlank { "IJAZAH" }} adalah ${data.namaDokumenPertama} dengan yang tertulis di ${data.dokumenKedua.ifBlank { "BUKU NIKAH & KK" }} adalah ${data.namaDokumenKedua} dan orangnya sama itu-itu juga.")
        addContent(document, CLOSING_BEDA_NAMA)
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN)
        document.close()
    }

    // ------------------------------------------------------------------
    // 6. Belum Menikah
    // ------------------------------------------------------------------
    fun generateBelumMenikahPdf(headerData: HeaderData, data: BelumMenikahData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.BELUM_MENIKAH)
        addTitle(document, LetterType.BELUM_MENIKAH, effectiveNomorSurat(data.nomorSurat, LetterType.BELUM_MENIKAH))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "NIK" to data.nik,
                "Jenis Kelamin" to data.jenisKelamin,
                "Tempat/Tanggal Lahir" to listOf(data.tempatLahir, data.tanggalLahir).filter { it.isNotEmpty() }.joinToString(", "),
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat,
                "RT/RW" to rtRw(data.rt, data.rw)
            )
        )
        addContent(document, "Adalah benar bahwa yang bersangkutan sampai saat ini belum pernah melakukan perkawinan yang tercatat pada kantor pencatat perkawinan yang berwenang. Surat keterangan ini diberikan untuk keperluan ${data.keperluan}.")
        addContent(document, CLOSING)
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN)
        document.close()
    }

    // ------------------------------------------------------------------
    // 7. Tidak Mampu (SKTM) — ttd Kades + "Mengetahui, Ketua RW" di kiri.
    // ------------------------------------------------------------------
    fun generateTidakMampuPdf(headerData: HeaderData, data: TidakMampuData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.TIDAK_MAMPU)
        addTitle(document, effectivePdfTitle(LetterType.TIDAK_MAMPU, data), effectiveNomorSurat(data.nomorSurat, LetterType.TIDAK_MAMPU))
        // FASE 5 — signer a.n. Sekdes untuk SEMUA varian (kop Subang-left).
        if (data.varianSKTM == "Daftar Kolektif") {
            addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
            addContent(document, "Orang-orang yang tercantum dalam daftar terlampir adalah benar-benar warga Desa kami dan tergolong dalam kategori Keluarga Tidak Mampu. Daftar nama terlampir adalah warga RW 02 Desa ${titleCase(headerData.namaDesa)}:")
            val warga = data.daftarKolektif.filter { it.nama.isNotBlank() || it.nik.isNotBlank() }
            addSimpleTable(
                document,
                listOf("No", "Nama", "NIK", "Alamat"),
                warga.mapIndexed { i, w -> listOf("${i + 1}", w.nama, w.nik, w.alamat) }
            )
            val keperluanKolektif = data.keperluan.ifBlank { "pengajuan bantuan sosial Program Jabar Caang" }
            addContent(document, "Surat keterangan ini dibuat sebagai salah satu syarat untuk keperluan $keperluanKolektif.")
            addContent(document, CLOSING_MISKIN_KOLEKTIF)
            addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN, leftNote = "Ketua RW")
            document.close()
            return
        }
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "NIK" to data.nik,
                "Jenis Kelamin" to data.jenisKelamin,
                "Tempat/Tanggal Lahir" to listOf(data.tempatLahir, data.tanggalLahir).filter { it.isNotEmpty() }.joinToString(", "),
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat,
                "RT/RW" to rtRw(data.rt, data.rw),
                "Penghasilan Bulanan" to data.penghasilanBulanan.ifEmpty { "" }.let { if (it.isEmpty()) "" else "Rp $it" }
            )
        )
        if (data.varianSKTM == "Sekolah") {
            // FASE 5 — narasi Sekolah (template Miskin Sekolah); tujuan default SMPN Negeri Kasomalang.
            val tujuan = data.tujuan.ifBlank { "SMPN Negeri Kasomalang" }
            addContent(document, "Orang tersebut adalah warga Desa ${titleCase(headerData.namaDesa)} dan saat ini akan mendaftar ke $tujuan, namun menurut keterangan Ketua RT/RW setempat dan sepengetahuan kami keadaan ekonominya saat ini termasuk kategori keluarga kurang mampu (Miskin). Untuk itu kami mohon kepada pihak $tujuan agar dapat memberikan keringanan/kebebasan biaya pendidikannya.")
            addContent(document, CLOSING_MISKIN_SEKOLAH)
            addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN)
        } else {
            // Umum — narasi samakan ke web.
            val tujuanText = if (data.tujuan.isNotBlank()) " di ${data.tujuan}" else ""
            addContent(document, "Adalah benar bahwa yang bersangkutan termasuk dalam kategori tidak mampu menurut kriteria yang berlaku di desa kami. Surat keterangan ini dibuat sebagai salah satu syarat untuk keperluan ${data.keperluan}$tujuanText.")
            addContent(document, CLOSING)
            addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN, leftNote = "Mengetahui,\nKetua RW")
        }
        document.close()
    }

    // ------------------------------------------------------------------
    // 8. Ahli Waris — FASE 3, ditulis ulang dari audit contoh
    // "SURAT KETERANGAN AHLI WARIS 2021.docx" (salinan yang
    // ditandatangani Kepala Desa): pembuka (almarhum + meninggal +
    // + jumlah anak) → "Dengan demikian susunan Ahli Waris..." →
    // tabel No/Nama/Hubungan/Tanda Tangan → penegas → saksi (Ketua
    // RT/RW) → Untuk Keperluan → penutup → ttd Mengetahui Camat (kiri)
    // + Kepala Desa (kanan) → lampiran "Data Ahli Waris" (detail
    // NIK/TTL/alamat per ahli waris, seperti lembar detail di DOCX).
    // ------------------------------------------------------------------
    fun generateAhliWarisPdf(headerData: HeaderData, data: AhliWarisData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.AHLI_WARIS)
        addTitle(document, LetterType.AHLI_WARIS, effectiveNomorSurat(data.nomorSurat, LetterType.AHLI_WARIS))
        addContent(document, paragrafPembukaAhliWaris(data, titleCase(headerData.namaDesa)))
        addContent(document, "Dengan demikian susunan Ahli Waris adalah sebagai berikut:")
        val waris = data.daftarAhliWaris.filter { it.nama.isNotBlank() || it.nik.isNotBlank() }
        // FASE 5 — kolom tabel utama: No, Nama, NIK, Tempat/Tgl Lahir, Hubungan Keluarga, Tanda Tangan.
        addSimpleTable(
            document,
            listOf("No", "Nama", "NIK", "Tempat/Tgl Lahir", "Hubungan Keluarga", "Tanda Tangan"),
            waris.mapIndexed { i, w ->
                listOf("${i + 1}", w.nama, w.nik, ttl(w.tempatLahir, w.tanggalLahir), w.hubungan, "")
            }
        )
        addContent(
            document,
            "Orang-orang tersebut di atas merupakan ahli waris yang sah dari " +
                "${sebutanOf(data)} ${data.namaPeninggal}."
        )
        // FASE 5 — paragraf penegas (kanonik web Fase 3).
        addContent(document, "Tidak ada ahli waris selain yang tercantum di dalam surat keterangan ini, sesuai pengakuannya.")
        val saksi = listOf(
            data.namaSaksi1 to data.jabatanSaksi1,
            data.namaSaksi2 to data.jabatanSaksi2
        ).filter { it.first.isNotBlank() || it.second.isNotBlank() }
        if (saksi.isNotEmpty()) {
            addContent(document, "Saksi-saksi yang mengetahui keterangan tersebut adalah")
            addSimpleTable(
                document,
                listOf("No", "Nama", "Jabatan", "Tanda Tangan"),
                saksi.mapIndexed { i, (nama, jabatan) -> listOf("${i + 1}", nama, jabatan, "") }
            )
        }
        if (data.keperluan.isNotBlank()) {
            addContent(document, "Untuk Keperluan : ${data.keperluan}")
        }
        if (data.keterangan.isNotBlank()) {
            addContent(document, "Keterangan: ${data.keterangan}")
        }
        addContent(document, CLOSING_AHLI_WARIS)
        addFooter(
            document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES,
            leftNote = "Mengetahui,\nCamat Kasomalang"
        )
        // FASE 5 — Lampiran "Data Ahli Waris" DIHAPUS (tidak ada di kanonik web Fase 3).
        document.close()
    }

    // ------------------------------------------------------------------
    // 9. Nikah Tidak Tercatat
    // ------------------------------------------------------------------
    fun generateNikahTidakTercatatPdf(headerData: HeaderData, data: NikahTidakTercatatData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.NIKAH_TIDAK_TERCATAT)
        addTitle(document, LetterType.NIKAH_TIDAK_TERCATAT, effectiveNomorSurat(data.nomorSurat, LetterType.NIKAH_TIDAK_TERCATAT))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, Kecamatan ${titleCase(headerData.namaKecamatan)}, Kabupaten ${titleCase(headerData.namaKabupaten.removePrefix("KABUPATEN "))}, menerangkan perihal perkawinan antara:")
        addContent(document, "Pihak Pertama (Suami):")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaPria,
                "Bin" to data.binSuami,
                "NIK" to data.nikPria,
                "Tempat/Tanggal Lahir" to listOf(data.tempatLahirPria, data.tanggalLahirPria).filter { it.isNotEmpty() }.joinToString(", "),
                "Agama" to data.agamaSuami
            )
        )
        addContent(document, "Dengan seorang Wanita (Istri):")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaWanita,
                "Bin" to data.binIstri,
                "NIK" to data.nikWanita,
                "Tempat/Tanggal Lahir" to listOf(data.tempatLahirWanita, data.tanggalLahirWanita).filter { it.isNotEmpty() }.joinToString(", "),
                "Agama" to data.agamaIstri
            )
        )
        addContent(document, narasiNikahTidakTercatat(data))
        addContent(document, CLOSING_NIKAH)
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN)
        document.close()
    }

    // ------------------------------------------------------------------
    // 10. Pemakaman
    // ------------------------------------------------------------------
    fun generatePemakamanPdf(headerData: HeaderData, data: PemakamanData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.PEMAKAMAN)
        addTitle(document, LetterType.PEMAKAMAN, effectiveNomorSurat(data.nomorSurat, LetterType.PEMAKAMAN))
        // FASE 5 — intro template: "Yang bertanda tangan di bawah ini," +
        // blok identitas (3 baris field) + "Dengan ini menerangkan bahwa :".
        addContent(document, "Yang bertanda tangan di bawah ini,")
        addDataTable(
            document,
            listOf(
                "Kepala Desa" to titleCase(headerData.namaDesa),
                "Kecamatan" to titleCase(headerData.namaKecamatan.removePrefix("KECAMATAN ")),
                "Kabupaten" to titleCase(headerData.namaKabupaten.removePrefix("KABUPATEN "))
            )
        )
        addContent(document, "Dengan ini menerangkan bahwa :")
        addDataTable(
            document,
            listOf(
                "Nama Almarhum/almarhumah" to data.namaAlmarhum,
                "NIK" to data.nikAlmarhum,
                "Tempat/Tanggal Lahir" to listOf(data.tempatLahir, data.tanggalLahir).filter { it.isNotEmpty() }.joinToString(", "),
                "Alamat" to data.alamat
            )
        )
        addContent(document, "Telah meninggal dunia pada :")
        addDataTable(
            document,
            listOf(
                "Hari" to data.hariMeninggal,
                "Tanggal" to data.tanggalMeninggal,
                "Jam" to data.jamMeninggal,
                "Tempat" to data.tempatMeninggal
            )
        )
        addContent(document, "Dan telah disemayamkan pada :")
        addDataTable(
            document,
            listOf(
                "Hari" to data.hariDisemayamkan,
                "Tanggal" to data.tanggalDisemayamkan,
                "Tempat" to data.tempatDisemayamkan
            )
        )
        addContent(document, "Almarhum/almarhumah tersebut di atas telah meninggal dunia dan dimakamkan di ${data.lokasiMakam} atas permohonan keluarga, yaitu ${data.namaKeluarga}.")
        addContent(document, CLOSING_PEMAKAMAN)
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN)
        document.close()
    }

    // ------------------------------------------------------------------
    // 11. Tidak Memiliki Rumah (pernyataan)
    // ------------------------------------------------------------------
    fun generateTidakMemilikiRumahPdf(headerData: HeaderData, data: TidakMemilikiRumahData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.TIDAK_MEMILIKI_RUMAH)
        addTitle(document, LetterType.TIDAK_MEMILIKI_RUMAH, effectiveNomorSurat(data.nomorSurat, LetterType.TIDAK_MEMILIKI_RUMAH))
        addContent(document, "Yang bertanda tangan di bawah ini:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "NIK" to data.nik,
                "Tempat/Tanggal Lahir" to listOf(data.tempatLahir, data.tanggalLahir).filter { it.isNotEmpty() }.joinToString(", "),
                "Pekerjaan" to data.pekerjaan,
                "Alamat Tinggal" to data.alamatTinggal
            )
        )
        // FASE 5 — narasi orang ketiga (template); Android lama orang pertama ("saya") — SALAH.
        addContent(document, "Menyatakan dengan sebenarnya bahwa orang tersebut sampai saat ini benar-benar belum memiliki rumah tinggal sendiri dan masih menumpang / tinggal bersama keluarga di alamat tersebut di atas.")
        if (data.keterangan.isNotBlank()) {
            addContent(document, "Keterangan: ${data.keterangan}")
        }
        addContent(document, CLOSING_PERMOHONAN_RUMAH)
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN)
        document.close()
    }

    // ------------------------------------------------------------------
    // 12. Undangan — nomor manual (editable).
    // ------------------------------------------------------------------
    fun generateUndanganPdf(headerData: HeaderData, data: UndanganData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.UNDANGAN)
        // FASE 5 — template tidak punya judul: sembunyikan judul, tampilkan hanya baris Nomor.
        addNomorLine(document, effectiveNomorSurat(data.nomorSurat, LetterType.UNDANGAN))
        addDataTable(
            document,
            listOf(
                "Sifat" to "Penting",
                "Lampiran" to "-",
                "Perihal" to "Undangan"
            )
        )
        addContent(document, "Kepada Yth.\n${data.tujuanYth}")
        addContent(document, "Dengan hormat, kami mengundang Bapak/Ibu/Saudara/i untuk hadir dalam acara berikut:")
        val tanggalAcara = listOf(data.tanggal, data.bulan, data.tahun).filter { it.isNotEmpty() }.joinToString(" ")
        addDataTable(
            document,
            listOf(
                "Hari/Tanggal" to listOf(data.hari, tanggalAcara).filter { it.isNotEmpty() }.joinToString(", "),
                "Pukul" to data.pukul.ifEmpty { "" }.let { if (it.isEmpty()) "" else "$it WIB" },
                "Tempat" to data.tempat,
                "Agenda" to data.agendaAcara
            )
        )
        addContent(document, CLOSING_UNDANGAN)
        if (data.daftarTembusan.isNotBlank()) {
            addContent(document, "Tembusan:\n${tembusanItems(data.daftarTembusan).joinToString("\n")}")
        }
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES)
        document.close()
    }

    // ==================================================================
    // Fase 2 — 18 tipe baru.
    // ==================================================================

    // ------------------------------------------------------------------
    // 13. Kematian — FASE 5, ditulis ulang PERSIS FASE5_BRIEF.md:
    // TANPA kop (header tabel 3 baris), landscape A4, isi kiri ~62%,
    // judul+nomor format template, seksi meninggal/pelapor sebagai
    // field, TANPA penutup, ttd "Sekretaris Desa Kasomalang Kulon"
    // TANPA a.n. (konflik: SURAT KEMATIAN.doc justru menulis a.n.;
    // keputusan ikut screenshot/xlsm sesuai brief Fase 5).
    // Tanggal format DD-MM-YYYY khusus form ini.
    // ------------------------------------------------------------------
    fun generateKematianPdf(headerData: HeaderData, data: KematianData, outputFile: File) {
        val document = newDocument(outputFile, landscape = true, rightMargin = 320f)
        addHeaderTabelKematian(document, headerData)
        addKematianTitle(document, data)
        addContent(document, "Yang bertanda tangan di bawah ini menerangkan")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaAlmarhum,
                "NIK" to data.nikAlmarhum,
                "Jenis Kelamin" to data.jenisKelamin,
                "Agama" to data.agama,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, toDdMmYyyy(data.tanggalLahir)),
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat
            )
        )
        addContent(document, "Telah meninggal dunia pada :")
        addDataTable(
            document,
            listOf(
                "Hari" to data.hariMeninggal,
                "Tanggal" to toDdMmYyyy(data.tanggalMeninggal),
                "Bertempat di" to data.tempatMeninggal,
                "Penyebab Kematian" to data.penyebabMeninggal
            )
        )
        addContent(document, "Surat Keterangan ini dibuat berdasarkan keterangan Pelapor :")
        addDataTable(
            document,
            listOf(
                "NIK" to data.nikPelapor,
                "Nama" to data.namaPelapor,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahirPelapor, toDdMmYyyy(data.tanggalLahirPelapor)),
                "Pekerjaan" to data.pekerjaanPelapor,
                "Alamat" to data.alamatPelapor
            )
        )
        addContent(document, "Hubungan pelapor dengan yang meninggal adalah : ${data.hubunganPelapor}")
        // TANPA paragraf penutup (template tidak punya penutup).
        addFooter(
            document, headerData,
            toDdMmYyyy(effectiveTanggalSurat(data.tanggalSurat)),
            Signer.SEKDES_KEMATIAN
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 14. Keterangan Usaha (SKU) — ttd Sekretaris Desa.
    // ------------------------------------------------------------------
    fun generateUsahaPdf(headerData: HeaderData, data: UsahaData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.USAHA)
        addTitle(document, LetterType.USAHA, effectiveNomorSurat(data.nomorSurat, LetterType.USAHA))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "NIK" to data.nik,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat
            )
        )
        // FASE 5 — kalimat pengantar template xlsm sebelum field usaha.
        addContent(document, "Orang tersebut adalah warga Desa ${titleCase(headerData.namaDesa)}, dan menurut pengakuannya, saat ini mempunyai usaha sebagai berikut:")
        addDataTable(
            document,
            listOf(
                "Usaha" to data.namaUsaha,
                "Alamat Usaha" to data.alamatUsaha
            )
        )
        addContent(document, "Adalah benar bahwa yang bersangkutan memiliki usaha ${data.namaUsaha} yang bertempat di ${data.alamatUsaha}. Surat keterangan ini diberikan untuk keperluan administrasi usaha.")
        addContent(document, CLOSING_USAHA)
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN)
        document.close()
    }

    // ------------------------------------------------------------------
    // 15. Duda/Janda — ttd Sekretaris Desa (+ keterangan Ketua RT).
    // ------------------------------------------------------------------
    fun generateDudaJandaPdf(headerData: HeaderData, data: DudaJandaData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.DUDA_JANDA)
        // FASE 5 — judul dinamis DUDA / JANDA.
        addTitle(document, effectivePdfTitle(LetterType.DUDA_JANDA, data), effectiveNomorSurat(data.nomorSurat, LetterType.DUDA_JANDA))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "NIK" to data.nik,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat
            )
        )
        // FASE 5 — baris Status DIHAPUS dari tabel (status hanya di judul + narasi).
        val status = data.statusDudaJanda.lowercase()
        if (data.rt.isNotBlank() || data.rw.isNotBlank()) {
            addContent(document, "Berdasarkan keterangan dari Ketua RT ${data.rt} RW ${data.rw}, orang tersebut saat ini benar warga Desa ${titleCase(headerData.namaDesa)} dan sampai saat ini masih berstatus $status dan belum menikah lagi.")
        } else {
            addContent(document, "Adalah benar bahwa yang bersangkutan berstatus $status dan sampai saat ini belum menikah lagi.")
        }
        if (data.keteranganRT.isNotBlank()) {
            addContent(document, "Keterangan Ketua RT: ${data.keteranganRT}.")
        }
        addContent(document, CLOSING)
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN)
        document.close()
    }

    // ------------------------------------------------------------------
    // 16. F-1.01 Biodata Keluarga — kop Disdukcapil teks, landscape,
    // tabel 10 anggota, BOLEH multi-halaman.
    // ------------------------------------------------------------------
    fun generateF101Pdf(headerData: HeaderData, data: F101Data, outputFile: File) {
        val document = newDocument(outputFile, landscape = true)
        addHeaderFor(document, headerData, LetterType.F101)
        addTitle(document, LetterType.F101, effectiveNomorSurat(data.nomorSurat, LetterType.F101))
        // FASE 5 — intro (web) + penutup CLOSING_F101; judul " — " (enum).
        addContent(document, "Dengan ini kami sampaikan biodata keluarga sebagai berikut:")
        addSectionTitle(document, "Data Kepala Keluarga")
        addDataTable(
            document,
            listOf(
                "Nama Kepala Keluarga" to data.namaKepalaKeluarga,
                "NIK Kepala Keluarga" to data.nikKepalaKeluarga,
                "No. KK" to data.noKK,
                "Alamat" to data.alamatKeluarga
            )
        )
        addSectionTitle(document, "Daftar Anggota Keluarga")
        addSimpleTable(
            document,
            listOf("No", "Nama", "NIK", "SHDK", "Tempat/Tgl Lahir", "Agama", "Pendidikan", "Pekerjaan", "Status Kawin", "Nama Ayah", "Nama Ibu"),
            data.daftarAnggota.mapIndexed { i, a ->
                listOf("${i + 1}", a.nama, a.nik, a.shdk, ttl(a.tempatLahir, a.tanggalLahir), a.agama, a.pendidikan, a.pekerjaan, a.statusKawin, a.namaAyah, a.namaIbu)
            },
            fontSize = 8f
        )
        addContent(document, CLOSING_F101)
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Kepala Dinas Kependudukan dan Pencatatan Sipil Kabupaten Subang" to "",
                "Kepala Keluarga" to data.namaKepalaKeluarga
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 17. F-1.02 Pendaftaran Peristiwa Kependudukan — kop Disdukcapil teks.
    // ------------------------------------------------------------------
    fun generateF102Pdf(headerData: HeaderData, data: F102Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.F102)
        addTitle(document, LetterType.F102, effectiveNomorSurat(data.nomorSurat, LetterType.F102))
        // FASE 5 — intro (web) + penutup CLOSING_FORM.
        addContent(document, "Dengan ini mengajukan permohonan sebagai berikut:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "NIK" to data.nik,
                "No. KK" to data.noKK,
                "Jenis Permohonan" to data.jenisPermohonan
            )
        )
        if (data.daftarLampiran.isNotBlank()) {
            addSectionTitle(document, "PERSYARATAN YANG DILAMPIRKAN")
            addContent(document, data.daftarLampiran)
        }
        addContent(document, CLOSING_FORM)
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        // Template: Petugas (kiri) + Pemohon (kanan).
        addSignatureColumns(
            document,
            listOf(
                "Petugas Pendaftaran" to "",
                "Pemohon" to data.namaLengkap
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 18. F-1.03 Pendaftaran Perpindahan Penduduk — kop Disdukcapil teks,
    // tabel anggota pindah, BOLEH multi-halaman.
    // ------------------------------------------------------------------
    fun generateF103Pdf(headerData: HeaderData, data: F103Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.F103)
        addTitle(document, LetterType.F103, effectiveNomorSurat(data.nomorSurat, LetterType.F103))
        // FASE 5 — intro (web) + penutup CLOSING_FORM; ttd Mengetahui Kadis (kiri) + Pemohon (kanan).
        addContent(document, "Dengan ini mengajukan permohonan pindah penduduk sebagai berikut:")
        addDataTable(
            document,
            listOf(
                "No. KK" to data.noKK,
                "Nama Pemohon" to data.namaPemohon,
                "NIK" to data.nik,
                "Jenis Permohonan" to data.jenisPermohonan,
                "Alamat Asal" to data.alamatAsal,
                "RT/RW" to rtRw(data.rt, data.rw),
                "Klasifikasi Kepindahan" to data.klasifikasiPindah,
                "Alamat Pindah" to data.alamatPindah,
                "Alasan Pindah" to data.alasanPindah,
                "Jenis Kepindahan" to data.jenisKepindahan
            )
        )
        addSectionTitle(document, "Anggota Keluarga yang Pindah")
        addSimpleTable(
            document,
            listOf("No", "NIK", "Nama", "SHDK"),
            data.daftarAnggotaPindah.mapIndexed { i, a -> listOf("${i + 1}", a.nik, a.nama, a.shdk) }
        )
        if (data.anggotaTidakPindah.isNotBlank()) {
            addContent(document, "Anggota keluarga yang tidak pindah: ${data.anggotaTidakPindah}.")
        }
        addContent(document, CLOSING_FORM)
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Mengetahui, Kepala Dinas Kependudukan dan Pencatatan Sipil Kabupaten Subang" to "",
                "Pemohon" to data.namaPemohon
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 19. F-1.04 Pernyataan Tidak Memiliki Dokumen Kependudukan.
    // ------------------------------------------------------------------
    fun generateF104Pdf(headerData: HeaderData, data: F104Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.F104)
        addTitle(document, LetterType.F104, effectiveNomorSurat(data.nomorSurat, LetterType.F104))
        // FASE 5 — intro pola web + paragraf pernyataan template + penutup CLOSING_SPTJM.
        addContent(document, "Yang bertanda tangan di bawah ini, dengan sesungguhnya menyatakan bahwa saya tidak memiliki dokumen kependudukan.")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "Alamat" to data.alamat,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Nama Ibu" to data.namaIbu
            )
        )
        addContent(document, "Dengan ini menyatakan bahwa saya tidak memiliki Dokumen Kependudukan dan apabila di kemudian hari ternyata pernyataan saya ini tidak benar, maka saya bersedia diproses secara hukum sesuai dengan peraturan perundang-undangan serta dokumen yang diterbitkan dari permohonan ini menjadi tidak sah.")
        addContent(document, CLOSING_SPTJM)
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(document, listOf("Yang Menyatakan" to data.namaLengkap))
        document.close()
    }

    // ------------------------------------------------------------------
    // 20. F-1.05 SPTJM Perkawinan Belum Tercatat.
    // ------------------------------------------------------------------
    fun generateF105Pdf(headerData: HeaderData, data: F105Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.F105)
        addTitle(document, LetterType.F105, effectiveNomorSurat(data.nomorSurat, LetterType.F105))
        // FASE 5 — intro template; urutan blok suami/istri → tanggal → SAKSI → tabel anak.
        addContent(document, "Kami yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa perkawinan kami belum tercatat.")
        addDataTable(
            document,
            listOf(
                "Nama Suami" to data.namaSuami,
                "NIK Suami" to data.nikSuami,
                "Nama Istri" to data.namaIstri,
                "NIK Istri" to data.nikIstri,
                "Tanggal Perkawinan" to listOf(data.tanggalPerkawinan, data.bulanPerkawinan, data.tahunPerkawinan).filter { it.isNotEmpty() }.joinToString(" ")
            )
        )
        addSectionTitle(document, "Saksi-saksi")
        addDataTable(
            document,
            listOf(
                "Nama Saksi 1" to data.namaSaksi1,
                "NIK Saksi 1" to data.nikSaksi1,
                "Nama Saksi 2" to data.namaSaksi2,
                "NIK Saksi 2" to data.nikSaksi2
            )
        )
        addSectionTitle(document, "Daftar Anak")
        addSimpleTable(
            document,
            listOf("No", "Nama", "No. Akta Kelahiran", "SHDK"),
            data.daftarAnak.mapIndexed { i, a -> listOf("${i + 1}", a.nama, a.noAktaKelahiran, a.shdk) }
        )
        addContent(document, CLOSING_F105)
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        // Template: PIHAK KEDUA | PIHAK PERTAMA (+materai) | SAKSI II | SAKSI I, tiap-tiap sub NIK.
        addSignatureColumnsSub(
            document,
            listOf(
                Triple("PIHAK KEDUA (Istri)", data.namaIstri, "NIK. ${data.nikIstri}"),
                Triple("PIHAK PERTAMA (Suami)", data.namaSuami, "NIK. ${data.nikSuami}"),
                Triple("SAKSI II", data.namaSaksi2, "NIK. ${data.nikSaksi2}"),
                Triple("SAKSI I", data.namaSaksi1, "NIK. ${data.nikSaksi1}")
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 21. F-1.06 Pernyataan Perubahan Elemen Data Kependudukan.
    // ------------------------------------------------------------------
    fun generateF106Pdf(headerData: HeaderData, data: F106Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.F106)
        addTitle(document, LetterType.F106, effectiveNomorSurat(data.nomorSurat, LetterType.F106))
        // FASE 5 — intro (web) + tabel rincian KK kolom NIK/SHDK/Keterangan + catatan lampiran + CLOSING_F106.
        addContent(document, "Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa telah terjadi perubahan elemen data kependudukan sebagai berikut:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "NIK" to data.nik,
                "No. KK" to data.noKK,
                "Alamat" to data.alamat
            )
        )
        addSectionTitle(document, "Anggota Keluarga yang Mengalami Perubahan")
        addSimpleTable(
            document,
            listOf("No", "Nama", "NIK", "SHDK", "Keterangan"),
            data.tabelAnggota.mapIndexed { i, a ->
                listOf("${i + 1}", a.nama, a.nik, a.shdk, a.keterangan.ifBlank { a.perubahan })
            }
        )
        addSectionTitle(document, "Perubahan Pendidikan")
        addDataTable(
            document,
            listOf(
                "Semula" to data.pendidikanSemula,
                "Menjadi" to data.pendidikanMenjadi,
                "Dasar Perubahan" to data.pendidikanDasar
            )
        )
        addSectionTitle(document, "Perubahan Pekerjaan")
        addDataTable(
            document,
            listOf(
                "Semula" to data.pekerjaanSemula,
                "Menjadi" to data.pekerjaanMenjadi,
                "Dasar Perubahan" to data.pekerjaanDasar
            )
        )
        if (data.perubahanAgama.isNotBlank()) {
            addContent(document, "Perubahan Agama: ${data.perubahanAgama}.")
        }
        addContent(document, "Terlampir disampaikan fotocopi berkas-berkas yang terkait dengan perubahan elemen data tersebut.")
        addContent(document, CLOSING_F106)
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(document, listOf("Yang Menyatakan" to data.namaLengkap))
        document.close()
    }

    // ------------------------------------------------------------------
    // 22. F-1.15 Permohonan KK Baru WNI.
    // ------------------------------------------------------------------
    fun generateF115Pdf(headerData: HeaderData, data: F115Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.F115)
        addTitle(document, LetterType.F115, effectiveNomorSurat(data.nomorSurat, LetterType.F115))
        // FASE 5 — intro (web) + blok PERSYARATAN PENERBITAN KK + penutup CLOSING_FORM.
        addContent(document, "Dengan ini mengajukan permohonan Kartu Keluarga baru sebagai berikut:")
        addDataTable(
            document,
            listOf(
                "Nama Pemohon" to data.namaPemohon,
                "NIK" to data.nik,
                "No. KK Lama" to data.noKKLama,
                "Alamat" to data.alamat,
                "RT/RW" to rtRw(data.rt, data.rw),
                "Alasan Permohonan" to data.alasanPermohonan,
                "Jumlah Anggota" to data.jumlahAnggota
            )
        )
        addSectionTitle(document, "Daftar Anggota Keluarga")
        addSimpleTable(
            document,
            listOf("No", "NIK", "Nama", "SHDK"),
            data.daftarAnggota.mapIndexed { i, a -> listOf("${i + 1}", a.nik, a.nama, a.shdk) }
        )
        addSectionTitle(document, "PERSYARATAN PENERBITAN KK")
        addContent(document, "A. KK BARU: 1. Fotokopi kutipan akta kawin/nikah; 2. Fotokopi kutipan akta perceraian; 3. Fotokopi kutipan akta kelahiran kepala keluarga & seluruh anggota; 4. Surat keterangan pindah/datang; 5. KK yang rusak (apabila KK rusak).")
        addContent(document, "B. PERUBAHAN KK: 1. Penambahan anggota karena kelahiran (KK lama, kutipan akta kelahiran); 2. Pengurangan anggota (KK lama, surat keterangan kematian, surat keterangan pindah); 3. Penambahan anggota karena menumpang dalam KK (KK yang akan ditumpangi, surat keterangan pindah datang).")
        addContent(document, CLOSING_FORM)
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        // Grid 2 kolom (template): baris 1 [Camat, Pemohon], baris 2 [Kades/Lurah, Petugas].
        addSignatureColumns(
            document,
            listOf(
                "Mengetahui, Camat Kasomalang" to "",
                "Pemohon" to data.namaPemohon
            )
        )
        addSignatureColumnsSub(
            document,
            listOf(
                Triple("Kepala Desa / Lurah", headerData.namaPejabat, null),
                Triple("Petugas", "", "Tanggal Pemasukan Data\nParaf Petugas")
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 23. F-2.01 Pelaporan Pencatatan Sipil — seksi kondisional:
    // hanya seksi jenis pelaporan yang dipilih yang dirender.
    // BOLEH multi-halaman.
    // ------------------------------------------------------------------
    fun generateF201Pdf(headerData: HeaderData, data: F201Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.F201)
        addTitle(document, LetterType.F201, effectiveNomorSurat(data.nomorSurat, LetterType.F201))
        // FASE 5 — intro (web); urutan blok: Jenis+Pelapor → Subjek Akta →
        // Saksi-saksi → Orang Tua → seksi kondisional TERAKHIR.
        addContent(document, "Dengan ini melaporkan peristiwa pencatatan sipil sebagai berikut:")
        addContent(document, "Jenis Pelaporan: ${data.jenisPelaporan}")
        addSectionTitle(document, "Data Pelapor")
        addDataTable(
            document,
            listOf(
                "Nama Pelapor" to data.pelaporNama,
                "NIK Pelapor" to data.pelaporNik,
                "Alamat Pelapor" to data.pelaporAlamat
            )
        )
        addSectionTitle(document, "Subjek Akta")
        addDataTable(
            document,
            listOf(
                "Nama Subjek Akta I" to data.subjek1Nama,
                "NIK Subjek Akta I" to data.subjek1Nik,
                "Nama Subjek Akta II" to data.subjek2Nama,
                "NIK Subjek Akta II" to data.subjek2Nik
            )
        )
        addSectionTitle(document, "Saksi-saksi")
        addDataTable(
            document,
            listOf(
                "Nama Saksi 1" to data.saksi1Nama,
                "NIK Saksi 1" to data.saksi1Nik,
                "Nama Saksi 2" to data.saksi2Nama,
                "NIK Saksi 2" to data.saksi2Nik
            )
        )
        addSectionTitle(document, "Orang Tua")
        addDataTable(
            document,
            listOf(
                "Nama Ayah" to data.ayahNama,
                "NIK Ayah" to data.ayahNik,
                "Nama Ibu" to data.ibuNama,
                "NIK Ibu" to data.ibuNik
            )
        )
        when (f201Section(data.jenisPelaporan)) {
            "anak" -> {
                addSectionTitle(document, "Data Anak")
                addDataTable(
                    document,
                    listOf(
                        "Nama Anak" to data.anakNama,
                        "Tempat/Tanggal Lahir" to ttl(data.anakTempatLahir, data.anakTanggalLahir),
                        "Pukul Lahir" to data.anakPukulLahir,
                        "Jenis Kelamin" to data.anakJenisKelamin,
                        "Anak Ke" to data.anakKe
                    )
                )
            }
            "lahirMati" -> {
                addSectionTitle(document, "Data Lahir Mati")
                addDataTable(
                    document,
                    listOf(
                        "Nama" to data.lahirMatiNama,
                        "Tanggal" to data.lahirMatiTanggal,
                        "Pukul" to data.lahirMatiPukul,
                        "Tempat" to data.lahirMatiTempat,
                        "Jenis Kelamin" to data.lahirMatiJenisKelamin,
                        "Sebab" to data.lahirMatiSebab,
                        "Keterangan" to data.lahirMatiKeterangan
                    )
                )
            }
            "pasangan" -> {
                addSectionTitle(document, "Data Perkawinan / Perceraian")
                addDataTable(
                    document,
                    listOf(
                        "Nama Suami" to data.pasanganSuamiNama,
                        "Nama Istri" to data.pasanganIstriNama,
                        "Tanggal Peristiwa" to data.tanggalPeristiwa,
                        "Tempat Peristiwa" to data.tempatPeristiwa,
                        "Keterangan" to data.pasanganKeterangan
                    )
                )
            }
            "kematian" -> {
                addSectionTitle(document, "Data Kematian")
                addDataTable(
                    document,
                    listOf(
                        "NIK" to data.meninggalNik,
                        "Nama" to data.meninggalNama,
                        "Tanggal Kematian" to data.meninggalTanggal,
                        "Pukul" to data.meninggalPukul,
                        "Sebab" to data.meninggalSebab,
                        "Tempat" to data.meninggalTempat,
                        "Yang Menerangkan" to data.meninggalYangMenerangkan
                    )
                )
            }
            else -> {
                if (data.tanggalPeristiwa.isNotBlank() || data.tempatPeristiwa.isNotBlank() || data.keteranganPeristiwa.isNotBlank()) {
                    addSectionTitle(document, "Keterangan Peristiwa")
                    addDataTable(
                        document,
                        listOf(
                            "Tanggal Peristiwa" to data.tanggalPeristiwa,
                            "Tempat Peristiwa" to data.tempatPeristiwa,
                            "Keterangan" to data.keteranganPeristiwa
                        )
                    )
                }
            }
        }
        addContent(document, CLOSING_FORM)
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        // Template: kiri "Mengetahui, Kepala Desa / Lurah" (+ "Pejabat Dukcapil yang Membidangi"), kanan Pelapor.
        addSignatureColumnsSub(
            document,
            listOf(
                Triple("Mengetahui, Kepala Desa / Lurah", "", "Pejabat Dukcapil yang Membidangi"),
                Triple("Pelapor", data.pelaporNama, null)
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 24. F-2.03 SPTJM Kebenaran Data Kelahiran.
    // ------------------------------------------------------------------
    fun generateF203Pdf(headerData: HeaderData, data: F203Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.F203)
        addTitle(document, LetterType.F203, effectiveNomorSurat(data.nomorSurat, LetterType.F203))
        // FASE 5 — intro kanonik; penutup CLOSING_F203; signers [Saksi I, Saya yang Menyatakan, Saksi II] + NIK.
        addContent(document, "Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya kebenaran data kelahiran sebagai berikut:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaYangMenyatakan,
                "NIK" to data.nik,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat
            )
        )
        addContent(document, "Dengan ini menyatakan dengan sesungguhnya bahwa data kelahiran anak berikut ini adalah benar:")
        addSectionTitle(document, "Data Anak")
        addDataTable(
            document,
            listOf(
                "Nama Anak" to data.namaAnak,
                "NIK Anak" to data.nikAnak,
                "Tempat/Tanggal Lahir Anak" to ttl(data.tempatLahirAnak, data.tanggalLahirAnak),
                "Jenis Kelamin Anak" to data.jenisKelaminAnak,
                "Anak Ke" to data.anakKe,
                "Alamat Anak" to data.alamatAnak
            )
        )
        addSectionTitle(document, "Data Ibu")
        addDataTable(
            document,
            listOf(
                "Nama Ibu" to data.namaIbu,
                "NIK Ibu" to data.nikIbu,
                "Tempat/Tanggal Lahir Ibu" to ttl(data.tempatLahirIbu, data.tanggalLahirIbu),
                "Pekerjaan Ibu" to data.pekerjaanIbu,
                "Alamat Ibu" to data.alamatIbu
            )
        )
        addSectionTitle(document, "Penolong Kelahiran")
        addDataTable(
            document,
            listOf(
                "Nama Penolong" to data.namaPenolong,
                "NIK Penolong" to data.nikPenolong,
                "Pekerjaan Penolong" to data.pekerjaanPenolong,
                "Alamat Penolong" to data.alamatPenolong
            )
        )
        addSectionTitle(document, "Saksi-saksi")
        addDataTable(
            document,
            listOf(
                "Nama Saksi 1" to data.namaSaksi1,
                "NIK Saksi 1" to data.nikSaksi1,
                "Nama Saksi 2" to data.namaSaksi2,
                "NIK Saksi 2" to data.nikSaksi2
            )
        )
        addContent(document, CLOSING_F203)
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumnsSub(
            document,
            listOf(
                Triple("Saksi I", data.namaSaksi1, "NIK. ${data.nikSaksi1}"),
                Triple("Saya yang Menyatakan", data.namaYangMenyatakan, "NIK. ${data.nik}"),
                Triple("Saksi II", data.namaSaksi2, "NIK. ${data.nikSaksi2}")
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 25. F-2.04 SPTJM Kebenaran sebagai Pasangan Suami Istri.
    // ------------------------------------------------------------------
    fun generateF204Pdf(headerData: HeaderData, data: F204Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.F204)
        addTitle(document, LetterType.F204, effectiveNomorSurat(data.nomorSurat, LetterType.F204))
        // FASE 5 — intro kanonik; penutup CLOSING_F204; signers [Saksi I, Saya yang Menyatakan, Saksi II] + NIK;
        // Pasangan TIDAK menandatangani; footnote "*) Coret yang tidak perlu."
        addContent(document, "Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa kami adalah pasangan suami istri yang sah.")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaYangMenyatakan,
                "NIK" to data.nik,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat
            )
        )
        addContent(document, "Dengan ini menyatakan dengan sesungguhnya bahwa saya adalah pasangan suami/istri yang sah dari:")
        addDataTable(
            document,
            listOf(
                "Nama Pasangan" to data.namaPasangan,
                "NIK Pasangan" to data.nikPasangan,
                "Tempat/Tanggal Lahir Pasangan" to ttl(data.tempatLahirPasangan, data.tanggalLahirPasangan),
                "Pekerjaan Pasangan" to data.pekerjaanPasangan,
                "Alamat Pasangan" to data.alamatPasangan,
                "No. KK" to data.noKK
            )
        )
        addSectionTitle(document, "Saksi-saksi")
        addDataTable(
            document,
            listOf(
                "Nama Saksi 1" to data.namaSaksi1,
                "NIK Saksi 1" to data.nikSaksi1,
                "Nama Saksi 2" to data.namaSaksi2,
                "NIK Saksi 2" to data.nikSaksi2
            )
        )
        addContent(document, CLOSING_F204)
        addContent(document, "*) Coret yang tidak perlu.")
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumnsSub(
            document,
            listOf(
                Triple("Saksi I", data.namaSaksi1, "NIK. ${data.nikSaksi1}"),
                Triple("Saya yang Menyatakan", data.namaYangMenyatakan, "NIK. ${data.nik}"),
                Triple("Saksi II", data.namaSaksi2, "NIK. ${data.nikSaksi2}")
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 26. SPTJM Kebenaran Data Kematian — ttd Yang Menyatakan + saksi +
    // Ketua RT / Ketua RW / Kepala Desa. Bermaterai.
    // ------------------------------------------------------------------
    fun generateSptjmKematianPdf(headerData: HeaderData, data: SptjmKematianData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.SPTJM_KEMATIAN)
        addTitle(document, LetterType.SPTJM_KEMATIAN, effectiveNomorSurat(data.nomorSurat, LetterType.SPTJM_KEMATIAN))
        // FASE 5 — intro kanonik (web); penutup CLOSING_SPTJM; judul panjang tetap.
        addContent(document, "Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya kebenaran data kematian sebagai berikut:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaYangMenyatakan,
                "NIK" to data.nik,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat
            )
        )
        addContent(document, "Dengan ini menyatakan dengan sesungguhnya bahwa data kematian berikut ini adalah benar:")
        addDataTable(
            document,
            listOf(
                "Nama Almarhum/almarhumah" to data.namaAlmarhum,
                "NIK Almarhum/almarhumah" to data.nikAlmarhum,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahirAlmarhum, data.tanggalLahirAlmarhum),
                "Alamat Terakhir" to data.alamatAlmarhum,
                "Hari Meninggal" to data.hariMeninggal,
                "Tanggal Meninggal" to data.tanggalMeninggal,
                "Jam Meninggal" to data.jamMeninggal,
                "Tempat Meninggal" to data.tempatMeninggal
            )
        )
        addSectionTitle(document, "Saksi-saksi")
        addDataTable(
            document,
            listOf(
                "Nama Saksi 1" to data.namaSaksi1,
                "NIK Saksi 1" to data.nikSaksi1,
                "Nama Saksi 2" to data.namaSaksi2,
                "NIK Saksi 2" to data.nikSaksi2
            )
        )
        addContent(document, CLOSING_SPTJM)
        addPlaceDate(document, titleCase(headerData.namaDesa), effectiveTanggalSurat(data.tanggalSurat))
        // Grid 3 kolom: baris 1 [Saksi 1, Yang Menyatakan, Saksi 2], baris 2 [Ketua RT, Ketua RW, Kepala Desa / Lurah].
        addSignatureColumns(
            document,
            listOf(
                "Saksi 1" to data.namaSaksi1,
                "Yang Menyatakan" to data.namaYangMenyatakan,
                "Saksi 2" to data.namaSaksi2
            )
        )
        addSignatureColumns(
            document,
            listOf(
                "Ketua RT" to "",
                "Ketua RW" to "",
                "Kepala Desa/Lurah" to ""
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 27. SPTJM Perkawinan/Perceraian Belum Tercatat.
    // ------------------------------------------------------------------
    fun generateSptjmPerkawinanPerceraianPdf(headerData: HeaderData, data: SptjmPerkawinanPerceraianData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.SPTJM_PERKAWINAN_PERCERAIAN)
        // FASE 5 — judul dinamis PANJANG (Perkawinan / Perceraian belum tercatat).
        addTitle(document, effectivePdfTitle(LetterType.SPTJM_PERKAWINAN_PERCERAIAN, data), effectiveNomorSurat(data.nomorSurat, LetterType.SPTJM_PERKAWINAN_PERCERAIAN))
        val peristiwa = data.jenisSPTJM.lowercase()
        addContent(document, "Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa $peristiwa kami belum tercatat.")
        addDataTable(
            document,
            listOf(
                "Nama Suami" to data.namaSuami,
                "NIK Suami" to data.nikSuami,
                "Nama Istri" to data.namaIstri,
                "NIK Istri" to data.nikIstri,
                "Tanggal $peristiwa" to data.tanggalPeristiwa
            )
        )
        addSectionTitle(document, "Saksi-saksi")
        addDataTable(
            document,
            listOf(
                "Nama Saksi 1" to data.namaSaksi1,
                "NIK Saksi 1" to data.nikSaksi1,
                "Nama Saksi 2" to data.namaSaksi2,
                "NIK Saksi 2" to data.nikSaksi2
            )
        )
        addSectionTitle(document, "Daftar Anak")
        addSimpleTable(
            document,
            listOf("No", "Nama", "No. Akta Kelahiran", "SHDK"),
            data.daftarAnak.mapIndexed { i, a -> listOf("${i + 1}", a.nama, a.noAktaKelahiran, a.shdk) }
        )
        addContent(document, CLOSING_SPTJM_PERKAWINAN)
        addPlaceDate(document, titleCase(headerData.namaDesa), effectiveTanggalSurat(data.tanggalSurat))
        // Template: PIHAK KEDUA | PIHAK PERTAMA | SAKSI II | SAKSI I, tiap-tiap sub NIK.
        addSignatureColumnsSub(
            document,
            listOf(
                Triple("PIHAK KEDUA (Istri)", data.namaIstri, "NIK. ${data.nikIstri}"),
                Triple("PIHAK PERTAMA (Suami)", data.namaSuami, "NIK. ${data.nikSuami}"),
                Triple("SAKSI II", data.namaSaksi2, "NIK. ${data.nikSaksi2}"),
                Triple("SAKSI I", data.namaSaksi1, "NIK. ${data.nikSaksi1}")
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 28. PBI-JKN Asesmen Calon Penerima Bansos KIS-PBI/SKM —
    // 11 pertanyaan kondisi, BOLEH multi-halaman.
    // ------------------------------------------------------------------
    fun generatePbiAsesmenPdf(headerData: HeaderData, data: PbiAsesmenData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.PBI_ASESMEN)
        addTitle(document, LetterType.PBI_ASESMEN, effectiveNomorSurat(data.nomorSurat, LetterType.PBI_ASESMEN))
        // FASE 5 — baris TAHUN di bawah judul + intro (web) + penutup CLOSING_FORM +
        // signers + a.n. Kades/Sekdes (ke-7).
        val tahun = tahunDariTanggal(effectiveTanggalSurat(data.tanggalSurat))
        val tahunPara = Paragraph("TAHUN $tahun", Font(baseFont, 12f, Font.BOLD))
        tahunPara.alignment = Element.ALIGN_CENTER
        tahunPara.spacingAfter = 8f
        document.add(tahunPara)
        addContent(document, "Instrumen asesmen calon penerima bansos KIS-PBI / SKM. Identitas dan kondisi umum calon penerima sebagai berikut:")
        addSectionTitle(document, "Identitas Calon Penerima")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Jenis Kelamin" to data.jenisKelamin,
                "Status Kawin" to data.statusKawin,
                "NIK" to data.nik,
                "No. KK" to data.noKK,
                "Nama Ibu Kandung" to data.namaIbuKandung,
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat
            )
        )
        addSectionTitle(document, "Pertanyaan Kondisi")
        addDataTable(
            document,
            listOf(
                "1. Jaminan kesehatan yang dimiliki" to data.jaminanKesehatan,
                "2. Alasan jaminan kesehatan non-aktif" to data.alasanNonAktif,
                "3. Status rumah" to data.statusRumah,
                "4. Kondisi rumah" to data.kondisiRumah,
                "5. Ukuran rumah" to data.ukuranRumah,
                "6. Dinding rumah" to data.dindingRumah,
                "7. Penerangan rumah" to data.peneranganRumah,
                "8. Jumlah keluarga dalam satu rumah" to data.jumlahKeluarga,
                "9. Jumlah anggota keluarga" to data.jumlahAnggota,
                "10. Bansos yang diterima" to data.bansosDiterima,
                "11. Usaha / penghasilan" to data.usahaPenghasilan
            )
        )
        if (data.catatanPetugas.isNotBlank()) {
            addContent(document, "Catatan petugas: ${data.catatanPetugas}")
        }
        addContent(document, CLOSING_FORM)
        addPlaceDate(document, titleCase(headerData.namaDesa), effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Pemohon" to data.namaLengkap,
                "Fasilitator" to "",
                "Sekretaris Desa" to headerData.namaSekdes
            )
        )
        addSignatureColumns(
            document,
            listOf(
                "Ketua RW" to "",
                "Ketua RT" to "",
                "Camat Kasomalang" to ""
            )
        )
        addSignatureColumns(
            document,
            listOf(
                "a.n. Kepala Desa ${titleCase(headerData.namaDesa)}\nSekretaris Desa" to headerData.namaSekdes
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 29. PBI-JKN Surat Pernyataan.
    // ------------------------------------------------------------------
    fun generatePbiPernyataanPdf(headerData: HeaderData, data: PbiPernyataanData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.PBI_PERNYATAAN)
        addTitle(document, LetterType.PBI_PERNYATAAN, effectiveNomorSurat(data.nomorSurat, LetterType.PBI_PERNYATAAN))
        addContent(document, "Yang bertanda tangan di bawah ini:")
        // FASE 5 — urutan field template xls sheet `2`.
        addDataTable(
            document,
            listOf(
                "No. KK" to data.noKK,
                "NIK" to data.nik,
                "Nama" to data.namaLengkap,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Jenis Kelamin" to data.jenisKelamin,
                "Warga Negara" to data.wargaNegara,
                "Status Kawin" to data.statusPerkawinan,
                "Agama" to data.agama,
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat
            )
        )
        // 2 butir pernyataan tetap template.
        addContent(document, "1. Saya benar-benar termasuk Keluarga Miskin;")
        addContent(document, "2. Apabila pernyataan saya ini tidak sesuai dengan kenyataan yang sebenarnya, saya siap mempertanggung jawabkan dihadapan hukum sesuai dengan ketentuan yang berlaku.")
        if (data.isiPernyataan.isNotBlank()) {
            addContent(document, data.isiPernyataan)
        }
        addContent(document, CLOSING_PBI_PERNYATAAN)
        addPlaceDate(document, titleCase(headerData.namaDesa), effectiveTanggalSurat(data.tanggalSurat))
        // Grid 2 kolom: [Yang Bersangkutan (+materai), Saksi Ketua RW], [Saksi Ketua RT, Mengetahui Camat], [a.n. Sekdes].
        addSignatureColumnsSub(
            document,
            listOf(
                Triple("Yang Bersangkutan", data.namaLengkap, "Materai Rp10.000"),
                Triple("Saksi Ketua RW", "", null)
            )
        )
        addSignatureColumns(
            document,
            listOf(
                "Saksi Ketua RT" to "",
                "Mengetahui, Camat Kasomalang" to ""
            )
        )
        addSignatureColumns(
            document,
            listOf(
                "a.n. Kepala Desa ${titleCase(headerData.namaDesa)}\nSekretaris Desa" to headerData.namaSekdes
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 30. PBI-JKN Surat Pernyataan Tidak Memiliki Rekening.
    // ------------------------------------------------------------------
    fun generatePbiTidakPunyaRekeningPdf(headerData: HeaderData, data: PbiTidakPunyaRekeningData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.PBI_TIDAK_PUNYA_REKENING)
        addTitle(document, LetterType.PBI_TIDAK_PUNYA_REKENING, effectiveNomorSurat(data.nomorSurat, LetterType.PBI_TIDAK_PUNYA_REKENING))
        // FASE 5 — intro samakan ke web; penutup CLOSING_SPTJM; signer Yang Menyatakan SAJA
        // ("Mengetahui, Kepala Desa" DIHAPUS dari Android — tidak ada di template mana pun).
        addContent(document, "Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa saya tidak memiliki rekening bank.")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "NIK" to data.nik,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat
            )
        )
        addContent(document, CLOSING_SPTJM)
        addPlaceDate(document, titleCase(headerData.namaDesa), effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Yang Menyatakan" to data.namaLengkap
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 31. SKTM PBI-JKN (FASE 5, baru) — kop dua logo, ttd RW/RT/Camat/a.n. Sekdes.
    // ------------------------------------------------------------------
    fun generateSktmPbiPdf(headerData: HeaderData, data: SktmPbiData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.SKTM_PBI)
        addTitle(document, LetterType.SKTM_PBI, effectiveNomorSurat(data.nomorSurat, LetterType.SKTM_PBI))
        addContent(document, "Yang bertanda tangan di bawah ini Kepala Desa ${titleCase(headerData.namaDesa)} Kecamatan ${titleCase(headerData.namaKecamatan.removePrefix("KECAMATAN "))} Kabupaten ${titleCase(headerData.namaKabupaten.removePrefix("KABUPATEN "))}, menerangkan dengan sesungguhnya bahwa :")
        addDataTable(
            document,
            listOf(
                "No. KK" to data.noKK,
                "NIK" to data.nik,
                "Nama" to data.namaLengkap,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Jenis Kelamin" to data.jenisKelamin,
                "Warga Negara" to data.wargaNegara,
                "Status Kawin" to data.statusPerkawinan,
                "Agama" to data.agama,
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat
            )
        )
        val anak = data.daftarAnak.filter { it.nama.isNotBlank() || it.nik.isNotBlank() }
        if (anak.isNotEmpty()) {
            addSectionTitle(document, "Daftar Anak")
            addSimpleTable(
                document,
                listOf("No", "No. KK", "NIK", "Nama", "Tempat/Tgl Lahir", "JK", "WN", "Status", "Agama", "Pekerjaan", "Alamat"),
                anak.mapIndexed { i, a ->
                    listOf("${i + 1}", a.noKK, a.nik, a.nama, a.ttl, a.jenisKelamin, a.wargaNegara, a.statusPerkawinan, a.agama, a.pekerjaan, a.alamat)
                },
                fontSize = 8f
            )
        }
        if (data.tujuan.isNotBlank()) addContent(document, "Surat keterangan ini dibuat untuk keperluan ${kalimatTujuanSktmPbi(data.tujuan)}.")
        addContent(document, CLOSING_SKTM_PBI)
        addPlaceDate(document, titleCase(headerData.namaDesa), effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Ketua RW" to "",
                "Ketua RT" to ""
            )
        )
        addSignatureColumns(
            document,
            listOf(
                "Mengetahui, Camat Kasomalang" to "",
                "a.n. Kepala Desa ${titleCase(headerData.namaDesa)}\nSekretaris Desa" to headerData.namaSekdes
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 32. Pernyataan Tidak Memiliki Rekening Listrik (FASE 5, baru) —
    // kop dua logo, ttd sama seperti SKTM PBI.
    // ------------------------------------------------------------------
    fun generatePbiRekeningListrikPdf(headerData: HeaderData, data: PbiRekeningListrikData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.PBI_REKENING_LISTRIK)
        addTitle(document, LetterType.PBI_REKENING_LISTRIK, effectiveNomorSurat(data.nomorSurat, LetterType.PBI_REKENING_LISTRIK))
        addContent(document, "Yang bertanda tangan di bawah ini Kepala Desa ${titleCase(headerData.namaDesa)} Kecamatan ${titleCase(headerData.namaKecamatan.removePrefix("KECAMATAN "))} Kabupaten ${titleCase(headerData.namaKabupaten.removePrefix("KABUPATEN "))}, menerangkan dengan sesungguhnya bahwa :")
        addDataTable(
            document,
            listOf(
                "No. KK" to data.noKK,
                "NIK" to data.nik,
                "Nama" to data.namaLengkap,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Jenis Kelamin" to data.jenisKelamin,
                "Warga Negara" to data.wargaNegara,
                "Status Kawin" to data.statusPerkawinan,
                "Agama" to data.agama,
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat
            )
        )
        addContent(document, "Menurut pengakuannya belum mempunyai rekening listrik dan menumpang aliran listrik ke ${data.namaTetangga} dengan nomor pelanggan ${data.nomorPelanggan}.")
        addContent(document, CLOSING_SKTM_PBI)
        addPlaceDate(document, titleCase(headerData.namaDesa), effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Ketua RW" to "",
                "Ketua RT" to ""
            )
        )
        addSignatureColumns(
            document,
            listOf(
                "Mengetahui, Camat Kasomalang" to "",
                "a.n. Kepala Desa ${titleCase(headerData.namaDesa)}\nSekretaris Desa" to headerData.namaSekdes
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 33. Pernyataan Akta Kelahiran (FASE 5, baru) — kop Subang-left,
    // ttd a.n. Sekdes (ikut P3).
    // ------------------------------------------------------------------
    fun generatePernyataanAktaKelahiranPdf(headerData: HeaderData, data: PernyataanAktaKelahiranData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderFor(document, headerData, LetterType.PERNYATAAN_AKTA_KELAHIRAN)
        addTitle(document, LetterType.PERNYATAAN_AKTA_KELAHIRAN, effectiveNomorSurat(data.nomorSurat, LetterType.PERNYATAAN_AKTA_KELAHIRAN))
        addContent(document, "Yang bertanda tangan di bawah ini :")
        addDataTable(
            document,
            listOf(
                "NIK" to data.nik,
                "Nama" to data.namaLengkap,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Nama Ayah" to data.namaAyah,
                "Nama Ibu" to data.namaIbu,
                "Alamat" to data.alamat
            )
        )
        addContent(document, "Menyatakan dengan sebenarnya bahwa orang tersebut sedang membuat AKTA KELAHIRAN dan sampai saat ini masih menunggu selesai dibuat.")
        addContent(document, CLOSING_PERMOHONAN_RUMAH)
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN)
        document.close()
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------
    private fun newDocument(outputFile: File, landscape: Boolean = false, rightMargin: Float = 56f): Document {
        val document = Document(if (landscape) PageSize.A4.rotate() else PageSize.A4)
        // Margin ~20 mm kiri, ~17 mm atas/bawah; margin kanan bisa
        // diperlebar (kematian: isi kiri ~62% lebar landscape).
        document.setMargins(56f, rightMargin, 48f, 48f)
        PdfWriter.getInstance(document, FileOutputStream(outputFile))
        document.open()
        return document
    }

    // Helper ttl/rtRw/titleCase tinggal di Models.kt (top-level, dipakai bersama).

    /** Muat logo dari assets; null bila file tidak ada / rusak (kop tetap jalan sebagai teks). */
    private fun loadLogoImage(assetPath: String, maxWidth: Float = 48f, maxHeight: Float = 52f): Image? {
        return try {
            val bytes = context.assets.open(assetPath).use { it.readBytes() }
            val image = Image.getInstance(bytes)
            image.scaleToFit(maxWidth, maxHeight)
            image
        } catch (e: Exception) {
            null
        }
    }

    /**
     * SATU pintu kop untuk semua generator: gaya kop ditentukan [kopFor]
     * (satu fungsi yang sama dipakai PreviewScreen) — jangan panggil
     * addHeader/addHeaderDisdukcapil langsung dari generator.
     */
    private fun addHeaderFor(document: Document, header: HeaderData, type: LetterType) {
        when (kopFor(type)) {
            KopStyle.GARUDA_CENTER -> addHeaderGarudaCenter(document, header)
            KopStyle.SUBANG_LEFT -> addHeaderSubangLeft(document, header)
            KopStyle.DUA_LOGO -> addHeader(document, header)
            KopStyle.DISDUKCAPIL -> addHeaderDisdukcapil(document)
            KopStyle.TABEL_KEMATIAN -> addHeaderTabelKematian(document, header)
        }
    }

    /**
     * FASE 5 — Header surat kematian (template, tanpa kop/logo): tabel
     * 3 baris dengan garis vertikal di kiri.
     */
    private fun addHeaderTabelKematian(document: Document, header: HeaderData) {
        val table = PdfPTable(2)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(2f, 5f))
        val rows = listOf(
            "Pemerintah/Kota/Kab" to titleCase(header.namaKabupaten.removePrefix("KABUPATEN ")),
            "Kecamatan" to titleCase(header.namaKecamatan.removePrefix("KECAMATAN ")),
            "Desa/Kelurahan" to titleCase(header.namaDesa)
        )
        for ((label, value) in rows) {
            val labelCell = PdfPCell(Phrase(label, Font(baseFont, 12f)))
            labelCell.border = Rectangle.LEFT
            labelCell.borderWidthLeft = 1.5f
            labelCell.paddingBottom = 2f
            table.addCell(labelCell)
            val valueCell = PdfPCell(Phrase(": $value", Font(baseFont, 12f)))
            valueCell.border = Rectangle.NO_BORDER
            valueCell.paddingBottom = 2f
            table.addCell(valueCell)
        }
        document.add(table)
        val spacer = Paragraph(" ")
        spacer.spacingAfter = 8f
        document.add(spacer)
    }

    /**
     * FASE 5 — Judul + nomor surat kematian format template:
     * judul bold + underline, nomor "No.: 460.3 / <reg|............> / KS / <tahun>"
     * bold + underline, rata tengah dalam area isi.
     */
    private fun addKematianTitle(document: Document, data: KematianData) {
        val titlePara = Paragraph("SURAT KETERANGAN KEMATIAN", Font(baseFont, 14f, Font.BOLD))
        titlePara.alignment = Element.ALIGN_CENTER
        titlePara.spacingAfter = 2f
        document.add(titlePara)
        val underline = Paragraph()
        underline.alignment = Element.ALIGN_CENTER
        underline.spacingAfter = 4f
        underline.add(LineSeparator(0.5f, 45f, BaseColor.BLACK, Element.ALIGN_CENTER, 0f))
        document.add(underline)
        val reg = data.nomorSurat.ifBlank { "............" }
        val tahun = tahunDariTanggal(effectiveTanggalSurat(data.tanggalSurat))
        val nomorPara = Paragraph("No.: 460.3 / $reg / KS / $tahun", Font(baseFont, 11f, Font.BOLD))
        nomorPara.alignment = Element.ALIGN_CENTER
        nomorPara.spacingAfter = 2f
        document.add(nomorPara)
        val underline2 = Paragraph()
        underline2.alignment = Element.ALIGN_CENTER
        underline2.spacingAfter = 8f
        underline2.add(LineSeparator(0.5f, 45f, BaseColor.BLACK, Element.ALIGN_CENTER, 0f))
        document.add(underline2)
    }

    /** Baris Nomor saja (tanpa judul) — undangan (FASE 5: template tidak punya judul). */
    private fun addNomorLine(document: Document, nomorSurat: String) {
        val nomorText = if (nomorSurat.isBlank()) "Nomor : ${".".repeat(48)}" else "Nomor : $nomorSurat"
        val nomorPara = Paragraph(nomorText, Font(baseFont, 11f))
        nomorPara.spacingAfter = 10f
        document.add(nomorPara)
    }

    /** Sel teks kop desa (dipakai semua varian kop desa): 12pt, baris desa 14pt, alamat 10pt. */
    private fun kopTextCell(header: HeaderData): PdfPCell {
        val textCell = PdfPCell()
        textCell.border = Rectangle.NO_BORDER
        textCell.horizontalAlignment = Element.ALIGN_CENTER
        textCell.verticalAlignment = Element.ALIGN_MIDDLE
        val titlePhrase = Phrase()
        titlePhrase.add(Chunk("PEMERINTAH DAERAH ${header.namaKabupaten.uppercase()}\n", Font(baseFont, 12f, Font.BOLD)))
        titlePhrase.add(Chunk("${header.namaKecamatan.uppercase()}\n", Font(baseFont, 12f, Font.BOLD)))
        titlePhrase.add(Chunk("DESA ${header.namaDesa.uppercase()}\n", Font(baseFont, 14f, Font.BOLD)))
        titlePhrase.add(Chunk("Alamat: ${header.alamatKantor}", Font(baseFont, 10f)))
        textCell.phrase = titlePhrase
        return textCell
    }

    /**
     * Kop ttd Kepala Desa (FASE 3): logo Garuda SAJA, di tengah paling
     * atas di atas teks kop (~28 mm lebar, proporsional), tanpa logo
     * Subang. Bila logo gagal dimuat, hanya teks kop yang tampil.
     */
    private fun addHeaderGarudaCenter(document: Document, header: HeaderData) {
        loadLogoImage(header.logoGarudaAsset, 80f, 90f)?.let { image ->
            image.alignment = Element.ALIGN_CENTER
            image.spacingAfter = 4f
            document.add(image)
        }
        val title = Paragraph()
        title.alignment = Element.ALIGN_CENTER
        title.spacingAfter = 2f
        title.add(Chunk("PEMERINTAH DAERAH ${header.namaKabupaten.uppercase()}\n", Font(baseFont, 12f, Font.BOLD)))
        title.add(Chunk("${header.namaKecamatan.uppercase()}\n", Font(baseFont, 12f, Font.BOLD)))
        title.add(Chunk("DESA ${header.namaDesa.uppercase()}\n", Font(baseFont, 14f, Font.BOLD)))
        title.add(Chunk("Alamat: ${header.alamatKantor}", Font(baseFont, 10f)))
        document.add(title)
        addDoubleLine(document)
    }

    /**
     * Kop ttd a.n. Kades oleh Sekdes / ttd Sekdes langsung (FASE 3):
     * logo Kabupaten Subang di KIRI (ukuran seperti kop lama), teks kop
     * rata tengah, tanpa logo Garuda. Sel kanan dikosongkan agar teks
     * tetap di tengah seperti kop dua logo.
     */
    private fun addHeaderSubangLeft(document: Document, header: HeaderData) {
        val table = PdfPTable(3)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(1f, 5f, 1f))

        val subangCell = PdfPCell()
        subangCell.border = Rectangle.NO_BORDER
        subangCell.horizontalAlignment = Element.ALIGN_CENTER
        subangCell.verticalAlignment = Element.ALIGN_MIDDLE
        loadLogoImage(header.logoSubangAsset)?.let { subangCell.addElement(it) }
        table.addCell(subangCell)

        table.addCell(kopTextCell(header))

        val emptyCell = PdfPCell()
        emptyCell.border = Rectangle.NO_BORDER
        table.addCell(emptyCell)

        document.add(table)
        addDoubleLine(document)
    }

    /** Kop dua logo (Garuda kiri + Subang kanan) — dipakai SPTJM/PBI seperti semula. */
    private fun addHeader(document: Document, header: HeaderData) {
        val table = PdfPTable(3)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(1f, 5f, 1f))

        val garudaCell = PdfPCell()
        garudaCell.border = Rectangle.NO_BORDER
        garudaCell.horizontalAlignment = Element.ALIGN_CENTER
        garudaCell.verticalAlignment = Element.ALIGN_MIDDLE
        loadLogoImage(header.logoGarudaAsset)?.let { garudaCell.addElement(it) }
        table.addCell(garudaCell)

        table.addCell(kopTextCell(header))

        val subangCell = PdfPCell()
        subangCell.border = Rectangle.NO_BORDER
        subangCell.horizontalAlignment = Element.ALIGN_CENTER
        subangCell.verticalAlignment = Element.ALIGN_MIDDLE
        loadLogoImage(header.logoSubangAsset)?.let { subangCell.addElement(it) }
        table.addCell(subangCell)

        document.add(table)

        // Garis ganda tebal + tipis di bawah kop.
        addDoubleLine(document)
    }

    /** Kop Disdukcapil Subang TEKS saja (tanpa logo desa) — untuk formulir seri F. */
    private fun addHeaderDisdukcapil(document: Document) {
        val title = Paragraph()
        title.alignment = Element.ALIGN_CENTER
        title.spacingAfter = 2f
        title.add(Chunk("PEMERINTAH KABUPATEN SUBANG\n", Font(baseFont, 12f, Font.BOLD)))
        title.add(Chunk("DINAS KEPENDUDUKAN DAN PENCATATAN SIPIL\n", Font(baseFont, 14f, Font.BOLD)))
        title.add(Chunk("Jl. Mayjen Sutoyo S. No. 50 Subang, Telp. (0260) 411424", Font(baseFont, 10f)))
        document.add(title)
        addDoubleLine(document)
    }

    /** Garis ganda tebal + tipis di bawah kop. */
    private fun addDoubleLine(document: Document) {
        val thickLine = Paragraph()
        thickLine.spacingBefore = 4f
        thickLine.add(LineSeparator(2f, 100f, BaseColor.BLACK, Element.ALIGN_CENTER, 0f))
        document.add(thickLine)
        val thinLine = Paragraph()
        thinLine.spacingAfter = 10f
        thinLine.add(LineSeparator(0.5f, 100f, BaseColor.BLACK, Element.ALIGN_CENTER, -3f))
        document.add(thinLine)
    }

    private fun addTitle(document: Document, type: LetterType, nomorSurat: String) {
        // FASE 3: baris Nomor tampil untuk tipe bernomor (atau bila nomor
        // diisi manual); formulir F tanpa nomor tetap tanpa baris Nomor.
        addTitle(document, type.pdfTitle, nomorSurat, hasNomorBaris(type) || nomorSurat.isNotBlank())
    }

    private fun addTitle(document: Document, title: String, nomorSurat: String, showNomor: Boolean = true) {
        val titlePara = Paragraph(title, Font(baseFont, 14f, Font.BOLD))
        titlePara.alignment = Element.ALIGN_CENTER
        titlePara.spacingAfter = 2f
        document.add(titlePara)

        val underline = Paragraph()
        underline.alignment = Element.ALIGN_CENTER
        underline.spacingAfter = 8f
        underline.add(LineSeparator(0.5f, 45f, BaseColor.BLACK, Element.ALIGN_CENTER, 0f))
        document.add(underline)

        if (showNomor) {
            // FASE 3: nomor kosong → garis titik-titik (untuk diisi tangan
            // saat registrasi), BUKAN pola default, BUKAN "undefined".
            val nomorText = if (nomorSurat.isBlank()) "Nomor : ${".".repeat(48)}" else "Nomor : $nomorSurat"
            val nomorPara = Paragraph(nomorText, Font(baseFont, 11f))
            nomorPara.spacingAfter = 10f
            document.add(nomorPara)
        }
    }

    /** Judul seksi di dalam formulir (mis. "Data Pelapor"). */
    private fun addSectionTitle(document: Document, text: String) {
        val para = Paragraph(text, Font(baseFont, 12f, Font.BOLD))
        para.spacingBefore = 6f
        para.spacingAfter = 4f
        document.add(para)
    }

    /** Tabel berbingkai (header tebal + latar abu muda) untuk daftar/tabel formulir. */
    private fun addSimpleTable(document: Document, headers: List<String>, rows: List<List<String>>, fontSize: Float = 9f) {
        val visibleRows = rows.filter { row -> row.any { it.isNotBlank() } }
        if (visibleRows.isEmpty()) return
        val table = PdfPTable(headers.size)
        table.widthPercentage = 100f
        table.headerRows = 1
        for (h in headers) {
            val cell = PdfPCell(Phrase(h, Font(baseFont, fontSize, Font.BOLD)))
            cell.background = BaseColor(0xF5, 0xF5, 0xF4)
            cell.horizontalAlignment = Element.ALIGN_CENTER
            table.addCell(cell)
        }
        for (row in visibleRows) {
            for (i in headers.indices) {
                val value = row.getOrElse(i) { "" }
                table.addCell(PdfPCell(Phrase(value, Font(baseFont, fontSize))))
            }
        }
        document.add(table)
        val spacer = Paragraph(" ")
        spacer.spacingAfter = 4f
        document.add(spacer)
    }

    /** Baris tempat + tanggal, mis. "Kasomalang Kulon, 15 November 2026". */
    private fun addPlaceDate(document: Document, place: String, tanggalSurat: String) {
        val para = Paragraph("$place, $tanggalSurat", Font(baseFont, 12f))
        para.spacingBefore = 8f
        para.spacingAfter = 4f
        document.add(para)
    }

    /** Tabel tanda tangan multi-kolom: label di atas, ruang kosong, lalu nama (atau garis bila kosong). */
    private fun addSignatureColumns(document: Document, columns: List<Pair<String, String>>) {
        val spacer = Paragraph(" ")
        spacer.spacingAfter = 8f
        document.add(spacer)
        val table = PdfPTable(columns.size)
        table.widthPercentage = 100f
        for ((label, name) in columns) {
            val cell = PdfPCell()
            cell.border = Rectangle.NO_BORDER
            val text = buildString {
                appendLine(label)
                appendLine()
                appendLine()
                appendLine(if (name.isBlank()) "(.......................................)" else name)
            }
            cell.phrase = Phrase(text, Font(baseFont, 12f))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            table.addCell(cell)
        }
        document.add(table)
    }

    /**
     * FASE 5 — varian addSignatureColumns dengan baris `sub` di bawah nama
     * (mis. "NIK. ............" untuk saksi, "Materai Rp10.000",
     * "Pejabat Dukcapil yang Membidangi", "Tanggal Pemasukan Data /
     * Paraf Petugas"). Sub null/blank = tidak dirender.
     */
    private fun addSignatureColumnsSub(document: Document, columns: List<Triple<String, String, String?>>) {
        val spacer = Paragraph(" ")
        spacer.spacingAfter = 8f
        document.add(spacer)
        val table = PdfPTable(columns.size)
        table.widthPercentage = 100f
        for ((label, name, sub) in columns) {
            val cell = PdfPCell()
            cell.border = Rectangle.NO_BORDER
            val text = buildString {
                appendLine(label)
                appendLine()
                appendLine()
                appendLine(if (name.isBlank()) "(.......................................)" else name)
                if (!sub.isNullOrBlank()) {
                    appendLine()
                    append(sub)
                }
            }
            cell.phrase = Phrase(text, Font(baseFont, 12f))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            table.addCell(cell)
        }
        document.add(table)
    }

    private fun addContent(document: Document, content: String) {
        val para = Paragraph(content, Font(baseFont, 12f))
        para.alignment = Element.ALIGN_JUSTIFIED
        para.spacingAfter = 8f
        document.add(para)
    }

    /** Tabel data label : nilai tanpa garis (baris kosong dilewati). */
    private fun addDataTable(document: Document, rows: List<Pair<String, String>>) {
        val visible = rows.filter { it.second.isNotBlank() }
        if (visible.isEmpty()) return
        val table = PdfPTable(2)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(2f, 5f))
        for ((label, value) in visible) {
            val labelCell = PdfPCell(Phrase(label, Font(baseFont, 12f)))
            labelCell.border = Rectangle.NO_BORDER
            labelCell.paddingBottom = 2f
            table.addCell(labelCell)
            val valueCell = PdfPCell(Phrase(": $value", Font(baseFont, 12f)))
            valueCell.border = Rectangle.NO_BORDER
            valueCell.paddingBottom = 2f
            table.addCell(valueCell)
        }
        val spacer = Paragraph(" ")
        spacer.spacingAfter = 2f
        document.add(table)
        document.add(spacer)
    }

    private fun addFooter(
        document: Document,
        header: HeaderData,
        tanggalSurat: String,
        signer: Signer,
        leftNote: String? = null
    ) {
        val spacer = Paragraph(" ")
        spacer.spacingAfter = 12f
        document.add(spacer)

        val footerTable = PdfPTable(2)
        footerTable.widthPercentage = 100f

        // Kolom kiri: tempat + tanggal surat, lalu catatan (mis. "Mengetahui, Ketua RW").
        val leftCell = PdfPCell()
        leftCell.border = Rectangle.NO_BORDER
        val leftText = buildString {
            append("${titleCase(header.namaDesa)}, $tanggalSurat")
            if (leftNote != null) {
                append("\n\n$leftNote")
                append("\n\n\n")
                append("(.......................................)")
            }
        }
        leftCell.phrase = Phrase(leftText, Font(baseFont, 12f))
        leftCell.horizontalAlignment = Element.ALIGN_LEFT
        footerTable.addCell(leftCell)

        // Kolom kanan: penandatangan.
        val rightCell = PdfPCell()
        rightCell.border = Rectangle.NO_BORDER
        val (jabatan, nama, nip) = when (signer) {
            Signer.KADES -> Triple("Kepala Desa", header.namaPejabat, header.nipPejabat)
            Signer.SEKDES_AN -> Triple(
                "a.n. Kepala Desa ${titleCase(header.namaDesa)}\nSekretaris Desa",
                header.namaSekdes,
                header.nipSekdes
            )
            // FASE 5 — kematian SAJA: tanpa a.n.
            Signer.SEKDES_KEMATIAN -> Triple(
                "Sekretaris Desa ${titleCase(header.namaDesa)}",
                header.namaSekdes,
                header.nipSekdes
            )
        }
        val signatureText = buildString {
            appendLine(jabatan)
            appendLine()
            appendLine()
            appendLine(nama)
            // Baris NIP hanya dirender bila NIP diisi; default kosong (tidak ada NIP).
            if (nip.isNotBlank()) append("NIP: $nip")
        }
        rightCell.phrase = Phrase(signatureText, Font(baseFont, 12f))
        rightCell.horizontalAlignment = Element.ALIGN_CENTER
        footerTable.addCell(rightCell)

        document.add(footerTable)
    }
}
