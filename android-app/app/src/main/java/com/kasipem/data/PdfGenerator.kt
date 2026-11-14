package com.kasipem.data

import android.content.Context
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PdfGenerator(private val context: Context) {

    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    private val baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED)

    fun generateDomisiliPdf(
        headerData: HeaderData,
        data: DomisiliData,
        outputFile: File
    ) {
        val document = Document(PageSize.A4)
        document.setMargins(20f, 20f, 20f, 20f)

        PdfWriter.getInstance(document, FileOutputStream(outputFile))
        document.open()

        addHeader(document, headerData)
        addTitle(document, "SURAT KETERANGAN DOMISILI")
        addClassification(document, "140/.../${Calendar.getInstance().get(Calendar.YEAR)}")
        addContent(document, "Yang bertanda tangan di bawah ini adalah Kepala Desa/Lurah ${headerData.namaDesa}, menerangkan bahwa:")

        val dataText = buildString {
            appendLine("Nama                       : ${data.namaLengkap}")
            appendLine("NIK                          : ${data.nik}")
            if (data.jenisKelamin.isNotEmpty()) appendLine("Jenis Kelamin         : ${data.jenisKelamin}")
            if (data.tempatLahir.isNotEmpty()) appendLine("Tempat/Tanggal Lahir : ${data.tempatLahir}, ${data.tanggalLahir}")
            if (data.pekerjaan.isNotEmpty()) appendLine("Pekerjaan                : ${data.pekerjaan}")
            appendLine("Alamat (KTP)            : ${data.alamatAsal}")
            appendLine("RT/RW                    : ${data.rt}/${data.rw}")
            appendLine("Alamat Domisili Saat Ini : ${data.alamatDomisili}")
        }

        addContent(document, dataText)

        val statementText = "Adalah benar bahwa orang tersebut di atas adalah penduduk sah Desa/Kelurahan ${headerData.namaDesa} dan bertempat tinggal di alamat tersebut di atas. Surat keterangan ini diberikan untuk keperluan ${data.keperluanSurat}."
        addContent(document, statementText)

        addFooter(document, headerData)
        document.close()
    }

    fun generateBelumMenikahPdf(
        headerData: HeaderData,
        data: BelumMenikahData,
        outputFile: File
    ) {
        val document = Document(PageSize.A4)
        document.setMargins(20f, 20f, 20f, 20f)

        PdfWriter.getInstance(document, FileOutputStream(outputFile))
        document.open()

        addHeader(document, headerData)
        addTitle(document, "SURAT KETERANGAN BELUM MENIKAH")
        addClassification(document, "460.2/.../${Calendar.getInstance().get(Calendar.YEAR)}")
        addContent(document, "Yang bertanda tangan di bawah ini adalah Kepala Desa/Lurah ${headerData.namaDesa}, menerangkan bahwa:")

        val dataText = buildString {
            appendLine("Nama                       : ${data.namaLengkap}")
            appendLine("NIK                          : ${data.nik}")
            if (data.jenisKelamin.isNotEmpty()) appendLine("Jenis Kelamin         : ${data.jenisKelamin}")
            if (data.tempatLahir.isNotEmpty()) appendLine("Tempat/Tanggal Lahir : ${data.tempatLahir}, ${data.tanggalLahir}")
            if (data.pekerjaan.isNotEmpty()) appendLine("Pekerjaan                : ${data.pekerjaan}")
            appendLine("Alamat                    : ${data.alamatLengkap}")
            appendLine("RT/RW                    : ${data.rt}/${data.rw}")
        }

        addContent(document, dataText)

        val statementText = "Adalah benar bahwa yang bersangkutan sampai saat ini belum pernah melakukan perkawinan yang tercatat pada kantor pencatat perkawinan yang berwenang. Surat keterangan ini diberikan untuk keperluan ${data.keperluan}."
        addContent(document, statementText)

        addFooter(document, headerData)
        document.close()
    }

    fun generateTidakMampuPdf(
        headerData: HeaderData,
        data: TidakMampuData,
        outputFile: File
    ) {
        val document = Document(PageSize.A4)
        document.setMargins(20f, 20f, 20f, 20f)

        PdfWriter.getInstance(document, FileOutputStream(outputFile))
        document.open()

        addHeader(document, headerData)
        addTitle(document, "SURAT KETERANGAN TIDAK MAMPU")
        addClassification(document, "460/.../${Calendar.getInstance().get(Calendar.YEAR)}")
        addContent(document, "Yang bertanda tangan di bawah ini adalah Kepala Desa/Lurah ${headerData.namaDesa}, menerangkan bahwa:")

        val dataText = buildString {
            appendLine("Nama                       : ${data.namaLengkap}")
            appendLine("NIK                          : ${data.nik}")
            if (data.jenisKelamin.isNotEmpty()) appendLine("Jenis Kelamin         : ${data.jenisKelamin}")
            if (data.tempatLahir.isNotEmpty()) appendLine("Tempat/Tanggal Lahir : ${data.tempatLahir}, ${data.tanggalLahir}")
            if (data.pekerjaan.isNotEmpty()) appendLine("Pekerjaan                : ${data.pekerjaan}")
            appendLine("Alamat                    : ${data.alamatLengkap}")
            appendLine("RT/RW                    : ${data.rt}/${data.rw}")
        }

        addContent(document, dataText)

        val statementText = "Adalah benar bahwa yang bersangkutan termasuk dalam kategori tidak mampu menurut kriteria yang berlaku di desa kami. Surat keterangan ini diberikan untuk keperluan ${data.keperluan} di ${data.tujuanSekolah}."
        addContent(document, statementText)

        addFooter(document, headerData)
        document.close()
    }

    fun generateBedaLuasPdf(
        headerData: HeaderData,
        data: BedaLuasData,
        outputFile: File
    ) {
        val document = Document(PageSize.A4)
        document.setMargins(20f, 20f, 20f, 20f)

        PdfWriter.getInstance(document, FileOutputStream(outputFile))
        document.open()

        addHeader(document, headerData)
        addTitle(document, "SURAT KETERANGAN BEDA LUAS TANAH")
        addClassification(document, "141/.../${Calendar.getInstance().get(Calendar.YEAR)}")
        addContent(document, "Yang bertanda tangan di bawah ini adalah Kepala Desa/Lurah ${headerData.namaDesa}, menerangkan bahwa:")

        val dataText = buildString {
            appendLine("Nama                       : ${data.namaLengkap}")
            appendLine("NIK                          : ${data.nik}")
            appendLine("Alamat                    : ${data.alamatLengkap}")
            appendLine("RT/RW                    : ${data.rt}/${data.rw}")
            appendLine()
            appendLine("Nomor Sertifikat          : ${data.nomorSertifikat}")
            appendLine("Luas Sertifikat           : ${data.luasSertifikat} m²")
            appendLine("Nomor SPPT                : ${data.nomorSPPT}")
            appendLine("Luas SPPT                 : ${data.luasSPPT} m²")
        }

        addContent(document, dataText)

        val statementText = "Adalah benar bahwa terdapat perbedaan luas tanah antara Sertifikat dan SPPT yang dimiliki oleh yang bersangkutan. Surat keterangan ini diberikan untuk keperluan administrasi pertanahan."
        addContent(document, statementText)

        addFooter(document, headerData)
        document.close()
    }

    fun generateUndanganPdf(
        headerData: HeaderData,
        data: UndanganData,
        outputFile: File
    ) {
        val document = Document(PageSize.A4)
        document.setMargins(20f, 20f, 20f, 20f)

        PdfWriter.getInstance(document, FileOutputStream(outputFile))
        document.open()

        addHeader(document, headerData)
        addTitle(document, "SURAT UNDANGAN RESMI")

        val metaText = buildString {
            appendLine("Sifat          : Penting")
            appendLine("Lampiran       : -")
            appendLine("Perihal        : Undangan")
        }
        addContent(document, metaText)

        val addressText = "Kepada Yth.\n${data.tujuanYth}"
        addContent(document, addressText)

        addContent(document, "Dengan hormat, kami mengundang Bapak/Ibu untuk hadir dalam acara berikut:")

        val eventText = buildString {
            appendLine("Hari/Tanggal   : ${data.hari}, ${data.tanggal}")
            appendLine("Pukul           : ${data.pukul} WIB")
            appendLine("Tempat          : ${data.tempat}")
            appendLine("Agenda          : ${data.agendaAcara}")
        }
        addContent(document, eventText)

        addContent(document, "Atas perhatian dan kehadiran Bapak/Ibu diucapkan terima kasih.")

        addFooter(document, headerData)
        document.close()
    }

    private fun addHeader(document: Document, header: HeaderData) {
        val headerTable = PdfPTable(1)
        headerTable.widthPercentage = 100f

        val cell = PdfPCell()
        cell.borderColor = BaseColor.BLACK
        cell.paddingBottom = 5f
        cell.paddingTop = 5f

        val titlePhrase = Phrase()
        titlePhrase.add(Chunk("PEMERINTAH ${header.namaKabupaten.uppercase()}\n", Font(baseFont, 11f, Font.BOLD)))
        titlePhrase.add(Chunk("${header.namaKecamatan.uppercase()}\n", Font(baseFont, 11f, Font.BOLD)))
        titlePhrase.add(Chunk("DESA/KELURAHAN ${header.namaDesa.uppercase()}\n", Font(baseFont, 11f, Font.BOLD)))
        titlePhrase.add(Chunk(header.alamatKantor, Font(baseFont, 9f)))

        cell.phrase = titlePhrase
        cell.horizontalAlignment = Element.ALIGN_CENTER

        headerTable.addCell(cell)
        document.add(headerTable)

        val lineSpacer = Paragraph(" ")
        lineSpacer.spacingAfter = 2f
        document.add(lineSpacer)
    }

    private fun addTitle(document: Document, title: String) {
        val titlePara = Paragraph(title, Font(baseFont, 12f, Font.BOLD))
        titlePara.alignment = Element.ALIGN_CENTER
        titlePara.spacingAfter = 5f
        document.add(titlePara)

        val line = Paragraph("_".repeat(50))
        line.alignment = Element.ALIGN_CENTER
        line.spacingAfter = 8f
        document.add(line)
    }

    private fun addClassification(document: Document, classification: String) {
        val classPara = Paragraph("Nomor : $classification", Font(baseFont, 9f))
        classPara.spacingAfter = 10f
        document.add(classPara)
    }

    private fun addContent(document: Document, content: String) {
        val para = Paragraph(content, Font(baseFont, 11f))
        para.alignment = Element.ALIGN_JUSTIFIED
        para.spacingAfter = 8f
        document.add(para)
    }

    private fun addFooter(document: Document, header: HeaderData) {
        val footerSpacer = Paragraph(" ")
        footerSpacer.spacingAfter = 15f
        document.add(footerSpacer)

        val footerTable = PdfPTable(2)
        footerTable.widthPercentage = 100f

        // Left column (issued date)
        val leftCell = PdfPCell()
        leftCell.border = Rectangle.NO_BORDER
        leftCell.phrase = Phrase(
            "Diterbitkan di : ${header.namaDesa}\nTanggal           : ${dateFormat.format(Date())}",
            Font(baseFont, 9f)
        )
        leftCell.horizontalAlignment = Element.ALIGN_LEFT
        footerTable.addCell(leftCell)

        // Right column (signature)
        val rightCell = PdfPCell()
        rightCell.border = Rectangle.NO_BORDER
        val signatureText = buildString {
            appendLine("${if (header.atnPejabat) "A.n. " else ""}${header.jabatanPejabat}")
            appendLine()
            appendLine()
            appendLine(header.namaPejabat)
            appendLine("NIP: ${header.nipPejabat}")
        }
        rightCell.phrase = Phrase(signatureText, Font(baseFont, 9f))
        rightCell.horizontalAlignment = Element.ALIGN_CENTER
        footerTable.addCell(rightCell)

        document.add(footerTable)
    }
}
