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
 * Generator PDF 12 surat (iText 5). Isi SAMA dengan kanon web (FIX_BRIEF.md).
 * Kop: logo Garuda (kiri, assets kop/kades.jpg) + logo Kabupaten Subang
 * (kanan, assets kop/sekdes.png); bila logo tidak ada/rusak, kop tetap
 * tampil sebagai teks — tidak crash.
 * Tanda tangan: Kepala Desa (default); pisah_kk = a.n. Kepala Desa oleh
 * Sekretaris Desa; tidak_mampu = Kepala Desa + "Mengetahui, Ketua RW" di kiri.
 */
class PdfGenerator(private val context: Context) {

    private val baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED)

    private enum class Signer { KADES, SEKDES_AN }

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
        }
    }

    // ------------------------------------------------------------------
    // 1. Domisili
    // ------------------------------------------------------------------
    fun generateDomisiliPdf(headerData: HeaderData, data: DomisiliData, outputFile: File) {
        val document = newDocument(outputFile)
        addHeader(document, headerData)
        addTitle(document, LetterType.DOMISILI, effectiveNomorSurat(data.nomorSurat, LetterType.DOMISILI))
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
        addTitle(document, LetterType.TIDAK_MAMPU, effectiveNomorSurat(data.nomorSurat, LetterType.TIDAK_MAMPU))
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

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------
    private fun newDocument(outputFile: File): Document {
        val document = Document(PageSize.A4)
        // Margin ~20 mm kiri/kanan, ~17 mm atas/bawah.
        document.setMargins(56f, 56f, 48f, 48f)
        PdfWriter.getInstance(document, FileOutputStream(outputFile))
        document.open()
        return document
    }

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
        val titlePara = Paragraph(type.pdfTitle, Font(baseFont, 12f, Font.BOLD))
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
