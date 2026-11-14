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
 * Generator PDF 30 surat/formulir (iText 5). Isi SAMA dengan kanon web (FIX_BRIEF.md + FILE_INTEGRATION_BRIEF.md).
 * Kop: logo Garuda (kiri, assets kop/kades.jpg) + logo Kabupaten Subang
 * (kanan, assets kop/sekdes.png); bila logo tidak ada/rusak, kop tetap
 * tampil sebagai teks — tidak crash. Formulir Dukcapil seri F memakai
 * kop Disdukcapil Subang TEKS saja (tanpa logo desa).
 * Tanda tangan: Kepala Desa (default); pisah_kk = a.n. Kepala Desa oleh
 * Sekretaris Desa; tidak_mampu = Kepala Desa + "Mengetahui, Ketua RW" di kiri.
 * Formulir panjang (F-1.01, F-2.01, F-1.03, PBI Asesmen) BOLEH multi-halaman.
 */
class PdfGenerator(private val context: Context) {

    private val baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED)

    private enum class Signer { KADES, SEKDES_AN, SEKDES }

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
        }
    }

    // ------------------------------------------------------------------
    // 1. Domisili
    // ------------------------------------------------------------------
    fun generateDomisiliPdf(headerData: HeaderData, data: DomisiliData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, effectivePdfTitle(LetterType.DOMISILI, data), effectiveNomorSurat(data.nomorSurat, LetterType.DOMISILI))
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
                addContent(document, "Adalah benar bahwa lembaga tersebut di atas berdomisili di alamat tersebut di atas, Desa ${titleCase(headerData.namaDesa)}. Surat keterangan ini diberikan untuk keperluan ${data.keperluanSurat}.")
            }
            "Perusahaan" -> {
                addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, Kecamatan ${titleCase(headerData.namaKecamatan)}, Kabupaten ${titleCase(headerData.namaKabupaten.removePrefix("KABUPATEN "))}, menerangkan dengan sesungguhnya bahwa:")
                addDataTable(
                    document,
                    listOf(
                        "Nama Perusahaan" to data.namaPerusahaan,
                        "Nama Pimpinan" to data.namaPimpinan,
                        "Nama Sekretaris" to data.namaSekretaris,
                        "Bidang Usaha" to data.bidangUsaha,
                        "Alamat" to data.alamatOrganisasi
                    )
                )
                addContent(document, "Adalah benar bahwa perusahaan tersebut di atas berdomisili di alamat tersebut di atas, Desa ${titleCase(headerData.namaDesa)}. Surat keterangan ini diberikan untuk keperluan ${data.keperluanSurat}.")
            }
            else -> {
                addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, Kecamatan ${titleCase(headerData.namaKecamatan)}, Kabupaten ${titleCase(headerData.namaKabupaten.removePrefix("KABUPATEN "))}, menerangkan dengan sesungguhnya bahwa:")
                addDataTable(
                    document,
                    listOf(
                        "Nama" to data.namaLengkap,
                        "NIK" to data.nik,
                        "Jenis Kelamin" to data.jenisKelamin,
                        "Tempat/Tanggal Lahir" to listOf(data.tempatLahir, data.tanggalLahir).filter { it.isNotEmpty() }.joinToString(", "),
                        "Pekerjaan" to data.pekerjaan,
                        "Alamat (KTP)" to data.alamatAsal,
                        "RT/RW" to rtRw(data.rt, data.rw),
                        "Alamat Domisili Saat Ini" to data.alamatDomisili
                    )
                )
                addContent(document, "Adalah benar bahwa orang tersebut di atas adalah penduduk Desa ${titleCase(headerData.namaDesa)} dan bertempat tinggal di alamat tersebut di atas. Surat keterangan ini diberikan untuk keperluan ${data.keperluanSurat}.")
            }
        }
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES)
        document.close()
    }

    // ------------------------------------------------------------------
    // 2. Pisah KK — ttd a.n. Kepala Desa oleh Sekretaris Desa.
    // ------------------------------------------------------------------
    fun generatePisahKkPdf(headerData: HeaderData, data: PisahKkData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, LetterType.PISAH_KK, effectiveNomorSurat(data.nomorSurat, LetterType.PISAH_KK))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan bahwa:")
        addDataTable(
            document,
            listOf(
                "Nama 1" to data.nama1,
                "NIK 1" to data.nik1,
                "Nama 2" to data.nama2,
                "NIK 2" to data.nik2,
                "Nomor KK Lama" to data.nomorKKLama,
                "Alamat" to data.alamat
            )
        )
        addContent(document, "Adalah benar bahwa ${data.nama1} telah pisah Kartu Keluarga dari ${data.nama2} (KK lama nomor ${data.nomorKKLama}) dan beralamat di ${data.alamat}. Surat keterangan ini diberikan untuk keperluan pengurusan Kartu Keluarga baru.")
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN)
        document.close()
    }

    // ------------------------------------------------------------------
    // 3. SKT Tanah (Dayat)
    // ------------------------------------------------------------------
    fun generateSktDayatPdf(headerData: HeaderData, data: SktDayatData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
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
        addContent(document, "Adalah benar bahwa tanah beserta bangunan di atas adalah milik ${data.namaLengkap} dan tidak dalam keadaan sengketa. Surat keterangan ini diberikan untuk keperluan administrasi pertanahan.")
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES)
        document.close()
    }

    // ------------------------------------------------------------------
    // 4. Beda Luas Tanah
    // ------------------------------------------------------------------
    fun generateBedaLuasPdf(headerData: HeaderData, data: BedaLuasData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, LetterType.BEDA_LUAS, effectiveNomorSurat(data.nomorSurat, LetterType.BEDA_LUAS))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan bahwa:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "NIK" to data.nik,
                "Alamat" to data.alamat,
                "RT/RW" to rtRw(data.rt, data.rw),
                "Nomor Sertifikat" to data.nomorSertifikat,
                "Luas Sertifikat" to data.luasSertifikat.ifEmpty { "" }.let { if (it.isEmpty()) "" else "$it m²" },
                "Nomor SPPT" to data.nomorSPPT,
                "Luas SPPT" to data.luasSPPT.ifEmpty { "" }.let { if (it.isEmpty()) "" else "$it m²" }
            )
        )
        addContent(document, "Adalah benar bahwa terdapat perbedaan luas tanah antara Sertifikat dan SPPT yang dimiliki oleh yang bersangkutan, sedangkan pemilik tanah tersebut adalah orang yang sama. Surat keterangan ini diberikan untuk keperluan administrasi pertanahan.")
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES)
        document.close()
    }

    // ------------------------------------------------------------------
    // 5. Beda Nama
    // ------------------------------------------------------------------
    fun generateBedaNamaPdf(headerData: HeaderData, data: BedaNamaData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, LetterType.BEDA_NAMA, effectiveNomorSurat(data.nomorSurat, LetterType.BEDA_NAMA))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan bahwa:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "NIK" to data.nik,
                "Nama di Dokumen 1" to data.namaDokumen1,
                "Nama di Dokumen 2" to data.namaDokumen2,
                "Alasan / Keterangan" to data.alasan
            )
        )
        addContent(document, "Adalah benar bahwa nama \"${data.namaDokumen1}\" dan nama \"${data.namaDokumen2}\" tersebut di atas adalah orang yang sama, yaitu ${data.namaLengkap}. Surat keterangan ini diberikan untuk keperluan administrasi kependudukan.")
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES)
        document.close()
    }

    // ------------------------------------------------------------------
    // 6. Belum Menikah
    // ------------------------------------------------------------------
    fun generateBelumMenikahPdf(headerData: HeaderData, data: BelumMenikahData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, LetterType.BELUM_MENIKAH, effectiveNomorSurat(data.nomorSurat, LetterType.BELUM_MENIKAH))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan bahwa:")
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
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES)
        document.close()
    }

    // ------------------------------------------------------------------
    // 7. Tidak Mampu (SKTM) — ttd Kades + "Mengetahui, Ketua RW" di kiri.
    // ------------------------------------------------------------------
    fun generateTidakMampuPdf(headerData: HeaderData, data: TidakMampuData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, effectivePdfTitle(LetterType.TIDAK_MAMPU, data), effectiveNomorSurat(data.nomorSurat, LetterType.TIDAK_MAMPU))
        if (data.varianSKTM == "Daftar Kolektif") {
            addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan bahwa warga-warga berikut ini termasuk dalam kategori keluarga kurang mampu (tidak mampu) menurut keterangan Ketua RT/RW setempat dan sepengetahuan kami:")
            val warga = data.daftarKolektif.filter { it.nama.isNotBlank() || it.nik.isNotBlank() }
            addSimpleTable(
                document,
                listOf("No", "Nama", "NIK", "Alamat"),
                warga.mapIndexed { i, w -> listOf("${i + 1}", w.nama, w.nik, w.alamat) }
            )
            addContent(document, "Surat keterangan ini diberikan untuk keperluan ${data.keperluan}.")
            addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES, leftNote = "Mengetahui,\nKetua RW")
            document.close()
            return
        }
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan bahwa:")
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
        val tujuanText = if (data.tujuan.isNotBlank()) " di ${data.tujuan}" else ""
        addContent(document, "Adalah benar bahwa yang bersangkutan termasuk dalam kategori keluarga kurang mampu (tidak mampu) menurut keterangan Ketua RT/RW setempat dan sepengetahuan kami. Surat keterangan ini diberikan untuk keperluan ${data.keperluan}$tujuanText.")
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES, leftNote = "Mengetahui,\nKetua RW")
        document.close()
    }

    // ------------------------------------------------------------------
    // 8. Ahli Waris
    // ------------------------------------------------------------------
    fun generateAhliWarisPdf(headerData: HeaderData, data: AhliWarisData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, LetterType.AHLI_WARIS, effectiveNomorSurat(data.nomorSurat, LetterType.AHLI_WARIS))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan bahwa:")
        addDataTable(
            document,
            listOf(
                "Nama Peninggal" to data.namaPeninggal,
                "NIK Peninggal" to data.nikPeninggal,
                "Alamat Peninggal" to data.alamatPeninggal
            )
        )
        addContent(document, "Adapun ahli waris dari almarhum/almarhumah tersebut di atas adalah:")
        val warisRows = mutableListOf<Pair<String, String>>()
        warisRows += "Nama Ahli Waris 1" to data.namaAhliWaris1
        warisRows += "NIK Ahli Waris 1" to data.nikAhliWaris1
        warisRows += "Hubungan Ahli Waris 1" to data.hubunganAhliWaris1
        if (data.namaAhliWaris2.isNotBlank() || data.nikAhliWaris2.isNotBlank()) {
            warisRows += "Nama Ahli Waris 2" to data.namaAhliWaris2
            warisRows += "NIK Ahli Waris 2" to data.nikAhliWaris2
            warisRows += "Hubungan Ahli Waris 2" to data.hubunganAhliWaris2
        }
        addDataTable(document, warisRows)
        if (data.keterangan.isNotBlank()) {
            addContent(document, "Keterangan: ${data.keterangan}.")
        }
        addContent(document, "Adalah benar bahwa orang-orang tersebut di atas adalah ahli waris yang sah dari ${data.namaPeninggal}. Surat keterangan ini diberikan untuk keperluan administrasi ahli waris.")
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES)
        document.close()
    }

    // ------------------------------------------------------------------
    // 9. Nikah Tidak Tercatat
    // ------------------------------------------------------------------
    fun generateNikahTidakTercatatPdf(headerData: HeaderData, data: NikahTidakTercatatData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, LetterType.NIKAH_TIDAK_TERCATAT, effectiveNomorSurat(data.nomorSurat, LetterType.NIKAH_TIDAK_TERCATAT))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan bahwa:")
        addDataTable(
            document,
            listOf(
                "Nama Pria" to data.namaPria,
                "NIK Pria" to data.nikPria,
                "Tempat/Tanggal Lahir Pria" to listOf(data.tempatLahirPria, data.tanggalLahirPria).filter { it.isNotEmpty() }.joinToString(", "),
                "Nama Wanita" to data.namaWanita,
                "NIK Wanita" to data.nikWanita,
                "Tempat/Tanggal Lahir Wanita" to listOf(data.tempatLahirWanita, data.tanggalLahirWanita).filter { it.isNotEmpty() }.joinToString(", "),
                "Tanggal Pernikahan" to data.tanggalPernikahan,
                "Tempat Pernikahan" to data.tempatPernikahan
            )
        )
        addContent(document, "Adalah benar bahwa ${data.namaPria} dan ${data.namaWanita} telah melangsungkan pernikahan pada tanggal ${data.tanggalPernikahan} di ${data.tempatPernikahan}, namun pernikahan tersebut belum tercatat pada kantor pencatat perkawinan yang berwenang.")
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES)
        document.close()
    }

    // ------------------------------------------------------------------
    // 10. Pemakaman
    // ------------------------------------------------------------------
    fun generatePemakamanPdf(headerData: HeaderData, data: PemakamanData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, LetterType.PEMAKAMAN, effectiveNomorSurat(data.nomorSurat, LetterType.PEMAKAMAN))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan bahwa:")
        addDataTable(
            document,
            listOf(
                "Nama Almarhum/almarhumah" to data.namaAlmarhum,
                "NIK" to data.nikAlmarhum,
                "Tempat/Tanggal Lahir" to listOf(data.tempatLahir, data.tanggalLahir).filter { it.isNotEmpty() }.joinToString(", "),
                "Alamat" to data.alamat,
                "Tanggal Meninggal" to data.tanggalMeninggal,
                "Lokasi Makam" to data.lokasiMakam,
                "Nama Keluarga" to data.namaKeluarga
            )
        )
        addContent(document, "Adalah benar bahwa ${data.namaAlmarhum} telah meninggal dunia pada tanggal ${data.tanggalMeninggal} dan dimakamkan di ${data.lokasiMakam}. Surat keterangan ini diberikan atas permohonan keluarga ${data.namaKeluarga} untuk keperluan administrasi pemakaman.")
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES)
        document.close()
    }

    // ------------------------------------------------------------------
    // 11. Tidak Memiliki Rumah (pernyataan)
    // ------------------------------------------------------------------
    fun generateTidakMemilikiRumahPdf(headerData: HeaderData, data: TidakMemilikiRumahData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
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
        addContent(document, "Dengan ini menyatakan dengan sesungguhnya bahwa sampai saat ini saya belum/tidak memiliki rumah tinggal sendiri dan menumpang/tinggal di alamat tersebut di atas.${if (data.keterangan.isNotBlank()) " ${data.keterangan}" else ""}")
        addContent(document, "Demikian surat pernyataan ini dibuat dengan sebenar-benarnya dan dapat dipergunakan sebagaimana mestinya.")
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES)
        document.close()
    }

    // ------------------------------------------------------------------
    // 12. Undangan — nomor manual (editable).
    // ------------------------------------------------------------------
    fun generateUndanganPdf(headerData: HeaderData, data: UndanganData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, LetterType.UNDANGAN, effectiveNomorSurat(data.nomorSurat, LetterType.UNDANGAN))
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
        addContent(document, "Atas perhatian dan kehadiran Bapak/Ibu/Saudara/i, kami ucapkan terima kasih.")
        if (data.daftarTembusan.isNotBlank()) {
            addContent(document, "Tembusan:\n${data.daftarTembusan}")
        }
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.KADES)
        document.close()
    }

    // ==================================================================
    // Fase 2 — 18 tipe baru.
    // ==================================================================

    // ------------------------------------------------------------------
    // 13. Kematian — ttd a.n. Kepala Desa oleh Sekretaris Desa.
    // Nomor seri 460.3/.../KS/ SAMA dengan pemakaman (sesuai template,
    // dipertahankan apa adanya — perlu konfirmasi desa).
    // ------------------------------------------------------------------
    fun generateKematianPdf(headerData: HeaderData, data: KematianData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, LetterType.KEMATIAN, effectiveNomorSurat(data.nomorSurat, LetterType.KEMATIAN))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan bahwa:")
        addDataTable(
            document,
            listOf(
                "Nama Almarhum/almarhumah" to data.namaAlmarhum,
                "NIK" to data.nikAlmarhum,
                "Jenis Kelamin" to data.jenisKelamin,
                "Agama" to data.agama,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat
            )
        )
        addContent(document, "Telah meninggal dunia pada:")
        addDataTable(
            document,
            listOf(
                "Hari" to data.hariMeninggal,
                "Tanggal" to data.tanggalMeninggal,
                "Tempat" to data.tempatMeninggal,
                "Penyebab" to data.penyebabMeninggal
            )
        )
        addContent(document, "Adapun yang melaporkan adalah:")
        addDataTable(
            document,
            listOf(
                "Nama Pelapor" to data.namaPelapor,
                "NIK Pelapor" to data.nikPelapor,
                "Tempat/Tanggal Lahir Pelapor" to ttl(data.tempatLahirPelapor, data.tanggalLahirPelapor),
                "Pekerjaan Pelapor" to data.pekerjaanPelapor,
                "Alamat Pelapor" to data.alamatPelapor,
                "Hubungan dengan Almarhum" to data.hubunganPelapor
            )
        )
        addContent(document, "Adalah benar bahwa ${data.namaAlmarhum} telah meninggal dunia pada ${data.hariMeninggal}, ${data.tanggalMeninggal} di ${data.tempatMeninggal} karena ${data.penyebabMeninggal}. Surat keterangan ini diberikan untuk keperluan pengurusan akta kematian.")
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES_AN)
        document.close()
    }

    // ------------------------------------------------------------------
    // 14. Keterangan Usaha (SKU) — ttd Sekretaris Desa.
    // ------------------------------------------------------------------
    fun generateUsahaPdf(headerData: HeaderData, data: UsahaData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, LetterType.USAHA, effectiveNomorSurat(data.nomorSurat, LetterType.USAHA))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan bahwa:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "NIK" to data.nik,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat,
                "Nama Usaha" to data.namaUsaha,
                "Alamat Usaha" to data.alamatUsaha
            )
        )
        addContent(document, "Adalah benar bahwa yang bersangkutan memiliki usaha ${data.namaUsaha} yang bertempat di ${data.alamatUsaha}. Surat keterangan ini diberikan untuk keperluan administrasi usaha.")
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES)
        document.close()
    }

    // ------------------------------------------------------------------
    // 15. Duda/Janda — ttd Sekretaris Desa (+ keterangan Ketua RT).
    // ------------------------------------------------------------------
    fun generateDudaJandaPdf(headerData: HeaderData, data: DudaJandaData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, LetterType.DUDA_JANDA, effectiveNomorSurat(data.nomorSurat, LetterType.DUDA_JANDA))
        addContent(document, "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(headerData.namaDesa)}, menerangkan bahwa:")
        addDataTable(
            document,
            listOf(
                "Status" to data.statusDudaJanda,
                "Nama" to data.namaLengkap,
                "NIK" to data.nik,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Pekerjaan" to data.pekerjaan,
                "Alamat" to data.alamat
            )
        )
        if (data.keteranganRT.isNotBlank()) {
            addContent(document, "Keterangan Ketua RT: ${data.keteranganRT}.")
        }
        addContent(document, "Adalah benar bahwa yang bersangkutan berstatus ${data.statusDudaJanda.lowercase()} dan belum menikah lagi sampai saat surat keterangan ini dibuat. Surat keterangan ini diberikan untuk keperluan administrasi kependudukan.")
        addFooter(document, headerData, effectiveTanggalSurat(data.tanggalSurat), Signer.SEKDES)
        document.close()
    }

    // ------------------------------------------------------------------
    // 16. F-1.01 Biodata Keluarga — kop Disdukcapil teks, landscape,
    // tabel 10 anggota, BOLEH multi-halaman.
    // ------------------------------------------------------------------
    fun generateF101Pdf(headerData: HeaderData, data: F101Data, outputFile: File) {
        val document = newDocument(outputFile, landscape = true)
        addHeaderDisdukcapil(document)
        addTitle(document, LetterType.F101, effectiveNomorSurat(data.nomorSurat, LetterType.F101))
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
            fontSize = 7f
        )
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Kepala Keluarga" to data.namaKepalaKeluarga,
                "Kepala Dinas Kependudukan dan Pencatatan Sipil Kab. Subang" to ""
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 17. F-1.02 Pendaftaran Peristiwa Kependudukan — kop Disdukcapil teks.
    // ------------------------------------------------------------------
    fun generateF102Pdf(headerData: HeaderData, data: F102Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderDisdukcapil(document)
        addTitle(document, LetterType.F102, effectiveNomorSurat(data.nomorSurat, LetterType.F102))
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
            addContent(document, "Lampiran yang disertakan:\n${data.daftarLampiran}")
        }
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Pemohon" to data.namaLengkap,
                "Petugas Pendaftaran" to ""
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
        addHeaderDisdukcapil(document)
        addTitle(document, LetterType.F103, effectiveNomorSurat(data.nomorSurat, LetterType.F103))
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
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Pemohon" to data.namaPemohon,
                "Petugas Pendaftaran" to ""
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 19. F-1.04 Pernyataan Tidak Memiliki Dokumen Kependudukan.
    // ------------------------------------------------------------------
    fun generateF104Pdf(headerData: HeaderData, data: F104Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderDisdukcapil(document)
        addTitle(document, LetterType.F104, effectiveNomorSurat(data.nomorSurat, LetterType.F104))
        addContent(document, "Yang bertanda tangan di bawah ini:")
        addDataTable(
            document,
            listOf(
                "Nama" to data.namaLengkap,
                "Alamat" to data.alamat,
                "Tempat/Tanggal Lahir" to ttl(data.tempatLahir, data.tanggalLahir),
                "Nama Ibu" to data.namaIbu
            )
        )
        addContent(document, "Dengan ini menyatakan dengan sesungguhnya bahwa sampai saat ini saya tidak memiliki dokumen kependudukan.")
        addContent(document, "Demikian surat pernyataan ini dibuat dengan sebenar-benarnya dan bermaterai cukup, untuk dapat dipergunakan sebagaimana mestinya.")
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(document, listOf("Yang Menyatakan" to data.namaLengkap))
        document.close()
    }

    // ------------------------------------------------------------------
    // 20. F-1.05 SPTJM Perkawinan Belum Tercatat.
    // ------------------------------------------------------------------
    fun generateF105Pdf(headerData: HeaderData, data: F105Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderDisdukcapil(document)
        addTitle(document, LetterType.F105, effectiveNomorSurat(data.nomorSurat, LetterType.F105))
        addContent(document, "Yang bertanda tangan di bawah ini:")
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
        addContent(document, "Dengan ini menyatakan dengan sesungguhnya bahwa kami telah melangsungkan perkawinan dan belum tercatat pada kantor pencatat perkawinan yang berwenang.")
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
        addContent(document, "Demikian surat pernyataan ini dibuat dengan sebenar-benarnya dan bermaterai cukup.")
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Suami" to data.namaSuami,
                "Istri" to data.namaIstri,
                "Saksi 1" to data.namaSaksi1,
                "Saksi 2" to data.namaSaksi2
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 21. F-1.06 Pernyataan Perubahan Elemen Data Kependudukan.
    // ------------------------------------------------------------------
    fun generateF106Pdf(headerData: HeaderData, data: F106Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderDisdukcapil(document)
        addTitle(document, LetterType.F106, effectiveNomorSurat(data.nomorSurat, LetterType.F106))
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
            listOf("No", "Nama", "Perubahan"),
            data.tabelAnggota.mapIndexed { i, a -> listOf("${i + 1}", a.nama, a.perubahan) }
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
        addContent(document, "Demikian surat pernyataan ini dibuat dengan sebenar-benarnya dan bermaterai cukup.")
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(document, listOf("Yang Menyatakan" to data.namaLengkap))
        document.close()
    }

    // ------------------------------------------------------------------
    // 22. F-1.15 Permohonan KK Baru WNI.
    // ------------------------------------------------------------------
    fun generateF115Pdf(headerData: HeaderData, data: F115Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderDisdukcapil(document)
        addTitle(document, LetterType.F115, effectiveNomorSurat(data.nomorSurat, LetterType.F115))
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
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Pemohon" to data.namaPemohon,
                "Kepala Desa/Lurah" to "",
                "Camat" to ""
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
        addHeaderDisdukcapil(document)
        addTitle(document, LetterType.F201, effectiveNomorSurat(data.nomorSurat, LetterType.F201))
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
                        "Sebab" to data.lahirMatiSebab
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
                        "Tempat Peristiwa" to data.tempatPeristiwa
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
                if (data.keteranganPeristiwa.isNotBlank()) {
                    addSectionTitle(document, "Keterangan Peristiwa")
                    addContent(document, data.keteranganPeristiwa)
                }
            }
        }
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Pelapor" to data.pelaporNama,
                "Petugas Pencatat" to ""
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 24. F-2.03 SPTJM Kebenaran Data Kelahiran.
    // ------------------------------------------------------------------
    fun generateF203Pdf(headerData: HeaderData, data: F203Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderDisdukcapil(document)
        addTitle(document, LetterType.F203, effectiveNomorSurat(data.nomorSurat, LetterType.F203))
        addContent(document, "Yang bertanda tangan di bawah ini:")
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
        addContent(document, "Demikian surat pernyataan ini dibuat dengan sebenar-benarnya dan bermaterai cukup.")
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Yang Menyatakan" to data.namaYangMenyatakan,
                "Saksi 1" to data.namaSaksi1,
                "Saksi 2" to data.namaSaksi2
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 25. F-2.04 SPTJM Kebenaran sebagai Pasangan Suami Istri.
    // ------------------------------------------------------------------
    fun generateF204Pdf(headerData: HeaderData, data: F204Data, outputFile: File) {
        val document = newDocument(outputFile)
        addHeaderDisdukcapil(document)
        addTitle(document, LetterType.F204, effectiveNomorSurat(data.nomorSurat, LetterType.F204))
        addContent(document, "Yang bertanda tangan di bawah ini:")
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
        addContent(document, "Demikian surat pernyataan ini dibuat dengan sebenar-benarnya dan bermaterai cukup.")
        addPlaceDate(document, "Subang", effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Yang Menyatakan" to data.namaYangMenyatakan,
                "Saksi 1" to data.namaSaksi1,
                "Saksi 2" to data.namaSaksi2
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
        addHeader(document, headerData)
        addTitle(document, LetterType.SPTJM_KEMATIAN, effectiveNomorSurat(data.nomorSurat, LetterType.SPTJM_KEMATIAN))
        addContent(document, "Yang bertanda tangan di bawah ini:")
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
        addContent(document, "Demikian surat pernyataan ini dibuat dengan sebenar-benarnya dan bermaterai cukup, untuk keperluan pengurusan akta kematian.")
        addPlaceDate(document, titleCase(headerData.namaDesa), effectiveTanggalSurat(data.tanggalSurat))
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
        addHeader(document, headerData)
        addTitle(document, LetterType.SPTJM_PERKAWINAN_PERCERAIAN, effectiveNomorSurat(data.nomorSurat, LetterType.SPTJM_PERKAWINAN_PERCERAIAN))
        val peristiwa = data.jenisSPTJM.lowercase()
        addContent(document, "Yang bertanda tangan di bawah ini:")
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
        addContent(document, "Dengan ini menyatakan dengan sesungguhnya bahwa kami telah melangsungkan $peristiwa dan belum tercatat pada kantor pencatat perkawinan yang berwenang.")
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
        addContent(document, "Demikian surat pernyataan ini dibuat dengan sebenar-benarnya dan bermaterai cukup.")
        addPlaceDate(document, titleCase(headerData.namaDesa), effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Suami" to data.namaSuami,
                "Istri" to data.namaIstri,
                "Saksi 1" to data.namaSaksi1,
                "Saksi 2" to data.namaSaksi2
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
        addHeader(document, headerData)
        addTitle(document, LetterType.PBI_ASESMEN, effectiveNomorSurat(data.nomorSurat, LetterType.PBI_ASESMEN))
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
                "Camat" to ""
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 29. PBI-JKN Surat Pernyataan.
    // ------------------------------------------------------------------
    fun generatePbiPernyataanPdf(headerData: HeaderData, data: PbiPernyataanData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, LetterType.PBI_PERNYATAAN, effectiveNomorSurat(data.nomorSurat, LetterType.PBI_PERNYATAAN))
        addContent(document, "Yang bertanda tangan di bawah ini:")
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
        if (data.isiPernyataan.isNotBlank()) {
            addContent(document, data.isiPernyataan)
        }
        addContent(document, "Demikian surat pernyataan ini dibuat dengan sebenar-benarnya dan bermaterai cukup, untuk dapat dipergunakan sebagaimana mestinya.")
        addPlaceDate(document, titleCase(headerData.namaDesa), effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Yang Menyatakan" to data.namaLengkap,
                "Mengetahui, Kepala Desa ${titleCase(headerData.namaDesa)}" to headerData.namaPejabat
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // 30. PBI-JKN Surat Pernyataan Tidak Memiliki Rekening.
    // ------------------------------------------------------------------
    fun generatePbiTidakPunyaRekeningPdf(headerData: HeaderData, data: PbiTidakPunyaRekeningData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, LetterType.PBI_TIDAK_PUNYA_REKENING, effectiveNomorSurat(data.nomorSurat, LetterType.PBI_TIDAK_PUNYA_REKENING))
        addContent(document, "Yang bertanda tangan di bawah ini:")
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
        addContent(document, "Dengan ini menyatakan dengan sesungguhnya bahwa sampai saat ini saya tidak memiliki rekening bank atas nama saya sendiri.")
        addContent(document, "Demikian surat pernyataan ini dibuat dengan sebenar-benarnya dan bermaterai cukup, untuk dapat dipergunakan sebagaimana mestinya.")
        addPlaceDate(document, titleCase(headerData.namaDesa), effectiveTanggalSurat(data.tanggalSurat))
        addSignatureColumns(
            document,
            listOf(
                "Yang Menyatakan" to data.namaLengkap,
                "Mengetahui, Kepala Desa ${titleCase(headerData.namaDesa)}" to headerData.namaPejabat
            )
        )
        document.close()
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------
    private fun newDocument(outputFile: File, landscape: Boolean = false): Document {
        val document = Document(if (landscape) PageSize.A4.rotate() else PageSize.A4)
        // Margin ~20 mm kiri/kanan, ~17 mm atas/bawah.
        document.setMargins(56f, 56f, 48f, 48f)
        PdfWriter.getInstance(document, FileOutputStream(outputFile))
        document.open()
        return document
    }

    /** "Subang", "12 Januari 1990" -> "Subang, 12 Januari 1990" (bagian kosong dilewati). */
    private fun ttl(tempat: String, tanggal: String): String =
        listOf(tempat, tanggal).filter { it.isNotEmpty() }.joinToString(", ")

    private fun rtRw(rt: String, rw: String): String {
        val r = rt.ifBlank { "-" }
        val w = rw.ifBlank { "-" }
        return "$r / $w"
    }

    /** "KASOMALANG KULON" -> "Kasomalang Kulon". */
    private fun titleCase(value: String): String =
        value.trim().split("\\s+".toRegex()).joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }

    /** Muat logo dari assets; null bila file tidak ada / rusak (kop tetap jalan sebagai teks). */
    private fun loadLogoImage(assetPath: String): Image? {
        return try {
            val bytes = context.assets.open(assetPath).use { it.readBytes() }
            val image = Image.getInstance(bytes)
            image.scaleToFit(48f, 52f)
            image
        } catch (e: Exception) {
            null
        }
    }

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

        val textCell = PdfPCell()
        textCell.border = Rectangle.NO_BORDER
        textCell.horizontalAlignment = Element.ALIGN_CENTER
        val titlePhrase = Phrase()
        titlePhrase.add(Chunk("PEMERINTAH DAERAH ${header.namaKabupaten.uppercase()}\n", Font(baseFont, 11f, Font.BOLD)))
        titlePhrase.add(Chunk("${header.namaKecamatan.uppercase()}\n", Font(baseFont, 11f, Font.BOLD)))
        titlePhrase.add(Chunk("DESA ${header.namaDesa.uppercase()}\n", Font(baseFont, 12f, Font.BOLD)))
        titlePhrase.add(Chunk("Alamat: ${header.alamatKantor}", Font(baseFont, 9f)))
        textCell.phrase = titlePhrase
        table.addCell(textCell)

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
        title.add(Chunk("PEMERINTAH KABUPATEN SUBANG\n", Font(baseFont, 11f, Font.BOLD)))
        title.add(Chunk("DINAS KEPENDUDUKAN DAN PENCATATAN SIPIL\n", Font(baseFont, 12f, Font.BOLD)))
        title.add(Chunk("Jl. Mayjen Sutoyo S. No. 50 Subang, Telp. (0260) 411424", Font(baseFont, 9f)))
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
        addTitle(document, type.pdfTitle, nomorSurat)
    }

    private fun addTitle(document: Document, title: String, nomorSurat: String) {
        val titlePara = Paragraph(title, Font(baseFont, 12f, Font.BOLD))
        titlePara.alignment = Element.ALIGN_CENTER
        titlePara.spacingAfter = 2f
        document.add(titlePara)

        val underline = Paragraph()
        underline.alignment = Element.ALIGN_CENTER
        underline.spacingAfter = 8f
        underline.add(LineSeparator(0.5f, 45f, BaseColor.BLACK, Element.ALIGN_CENTER, 0f))
        document.add(underline)

        if (nomorSurat.isNotBlank()) {
            val nomorPara = Paragraph("Nomor : $nomorSurat", Font(baseFont, 10f))
            nomorPara.spacingAfter = 10f
            document.add(nomorPara)
        }
    }

    /** Judul seksi di dalam formulir (mis. "Data Pelapor"). */
    private fun addSectionTitle(document: Document, text: String) {
        val para = Paragraph(text, Font(baseFont, 11f, Font.BOLD))
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
        val para = Paragraph("$place, $tanggalSurat", Font(baseFont, 10f))
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
            cell.phrase = Phrase(text, Font(baseFont, 10f))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            table.addCell(cell)
        }
        document.add(table)
    }

    private fun addContent(document: Document, content: String) {
        val para = Paragraph(content, Font(baseFont, 11f))
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
            val labelCell = PdfPCell(Phrase(label, Font(baseFont, 11f)))
            labelCell.border = Rectangle.NO_BORDER
            labelCell.paddingBottom = 2f
            table.addCell(labelCell)
            val valueCell = PdfPCell(Phrase(": $value", Font(baseFont, 11f)))
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
        leftCell.phrase = Phrase(leftText, Font(baseFont, 10f))
        leftCell.horizontalAlignment = Element.ALIGN_LEFT
        footerTable.addCell(leftCell)

        // Kolom kanan: penandatangan.
        val rightCell = PdfPCell()
        rightCell.border = Rectangle.NO_BORDER
        val (jabatan, nama, nip) = when (signer) {
            Signer.KADES -> Triple("Kepala Desa ${titleCase(header.namaDesa)}", header.namaPejabat, header.nipPejabat)
            Signer.SEKDES_AN -> Triple(
                "a.n. Kepala Desa ${titleCase(header.namaDesa)}\nSekretaris Desa",
                header.namaSekdes,
                header.nipSekdes
            )
            Signer.SEKDES -> Triple("Sekretaris Desa", header.namaSekdes, header.nipSekdes)
        }
        val signatureText = buildString {
            appendLine(jabatan)
            appendLine()
            appendLine()
            appendLine(nama)
            // Baris NIP hanya dirender bila NIP diisi; default kosong (tidak ada NIP).
            if (nip.isNotBlank()) append("NIP: $nip")
        }
        rightCell.phrase = Phrase(signatureText, Font(baseFont, 10f))
        rightCell.horizontalAlignment = Element.ALIGN_CENTER
        footerTable.addCell(rightCell)

        document.add(footerTable)
    }
}
