package com.kasipem.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kasipem.data.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Pratinjau A4 untuk 12/12 jenis surat. Isi sama dengan PdfGenerator
 * (satu kanon field). Tombol PDF benar-benar membuat file PDF di cache
 * aplikasi; tombol Cetak mengirim PDF itu ke PrintManager Android.
 * Status (berhasil/gagal + nama file) tampil sebagai teks persisten
 * di bawah tombol — bukan toast yang menghilang.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    headerData: HeaderData,
    letterData: Any,
    letterType: LetterType,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var statusMessage by remember { mutableStateOf<String?>(null) }

    fun buildPdfFile(): File {
        val fileName = "kasipem-${letterType.name.lowercase()}-${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, fileName)
        PdfGenerator(context).generatePdf(headerData, letterType, letterData, file)
        return file
    }

    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        TopAppBar(
            title = {
                Text(
                    "Pratinjau Surat",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(16.dp)
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PreviewContent(
                headerData = headerData,
                letterData = letterData,
                letterType = letterType
            )
        }

        statusMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Ubah")
            }

            Button(
                onClick = {
                    try {
                        val file = buildPdfFile()
                        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                        printManager.print(
                            "KASIPEM ${letterType.pdfTitle}",
                            PdfFilePrintAdapter(file),
                            PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build()
                        )
                        statusMessage = "Dikirim ke pencetak (file: ${file.name})."
                    } catch (e: Exception) {
                        statusMessage = "Gagal mencetak: ${e.message}"
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cetak")
            }

            Button(
                onClick = {
                    try {
                        val file = buildPdfFile()
                        statusMessage = "PDF tersimpan di penyimpanan internal aplikasi: ${file.name} (${file.length()} byte). Buka lewat pengelola file / bagikan dari aplikasi."
                    } catch (e: Exception) {
                        statusMessage = "Gagal membuat PDF: ${e.message}"
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("PDF")
            }
        }
    }
}

/** Adapter cetak: menulis file PDF yang sudah dibuat ke printer. */
private class PdfFilePrintAdapter(private val file: File) : PrintDocumentAdapter() {
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: android.os.CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: android.os.Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }
        val info = PrintDocumentInfo.Builder(file.name)
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
            .build()
        callback?.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: android.os.CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        try {
            FileInputStream(file).use { input ->
                FileOutputStream(destination.fileDescriptor).use { output ->
                    input.copyTo(output)
                }
            }
            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: Exception) {
            callback?.onWriteFailed(e.message)
        }
    }
}

private fun nomorOf(type: LetterType, data: Any): String = when (type) {
    LetterType.DOMISILI -> (data as DomisiliData).nomorSurat
    LetterType.PISAH_KK -> (data as PisahKkData).nomorSurat
    LetterType.SKT_DAYAT -> (data as SktDayatData).nomorSurat
    LetterType.BEDA_LUAS -> (data as BedaLuasData).nomorSurat
    LetterType.BEDA_NAMA -> (data as BedaNamaData).nomorSurat
    LetterType.BELUM_MENIKAH -> (data as BelumMenikahData).nomorSurat
    LetterType.TIDAK_MAMPU -> (data as TidakMampuData).nomorSurat
    LetterType.AHLI_WARIS -> (data as AhliWarisData).nomorSurat
    LetterType.NIKAH_TIDAK_TERCATAT -> (data as NikahTidakTercatatData).nomorSurat
    LetterType.PEMAKAMAN -> (data as PemakamanData).nomorSurat
    LetterType.TIDAK_MEMILIKI_RUMAH -> (data as TidakMemilikiRumahData).nomorSurat
    LetterType.UNDANGAN -> (data as UndanganData).nomorSurat
}

private fun tanggalOf(type: LetterType, data: Any): String = when (type) {
    LetterType.DOMISILI -> (data as DomisiliData).tanggalSurat
    LetterType.PISAH_KK -> (data as PisahKkData).tanggalSurat
    LetterType.SKT_DAYAT -> (data as SktDayatData).tanggalSurat
    LetterType.BEDA_LUAS -> (data as BedaLuasData).tanggalSurat
    LetterType.BEDA_NAMA -> (data as BedaNamaData).tanggalSurat
    LetterType.BELUM_MENIKAH -> (data as BelumMenikahData).tanggalSurat
    LetterType.TIDAK_MAMPU -> (data as TidakMampuData).tanggalSurat
    LetterType.AHLI_WARIS -> (data as AhliWarisData).tanggalSurat
    LetterType.NIKAH_TIDAK_TERCATAT -> (data as NikahTidakTercatatData).tanggalSurat
    LetterType.PEMAKAMAN -> (data as PemakamanData).tanggalSurat
    LetterType.TIDAK_MEMILIKI_RUMAH -> (data as TidakMemilikiRumahData).tanggalSurat
    LetterType.UNDANGAN -> (data as UndanganData).tanggalSurat
}

@Composable
private fun PreviewContent(
    headerData: HeaderData,
    letterData: Any,
    letterType: LetterType
) {
    val date = effectiveTanggalSurat(tanggalOf(letterType, letterData))
    val nomor = effectiveNomorSurat(nomorOf(letterType, letterData), letterType)

    // Kop: logo Garuda kiri + teks tengah + logo Subang kanan (fallback teks bila logo tak ada).
    val context = LocalContext.current
    val garudaBitmap = remember(headerData.logoGarudaAsset) {
        try {
            context.assets.open(headerData.logoGarudaAsset).use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
        } catch (e: Exception) {
            null
        }
    }
    val subangBitmap = remember(headerData.logoSubangAsset) {
        try {
            context.assets.open(headerData.logoSubangAsset).use { BitmapFactory.decodeStream(it)?.asImageBitmap() }
        } catch (e: Exception) {
            null
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(4.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        garudaBitmap?.let {
            Image(bitmap = it, contentDescription = "Logo Garuda Pancasila", modifier = Modifier.size(44.dp))
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("PEMERINTAH DAERAH ${headerData.namaKabupaten.uppercase()}", fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(headerData.namaKecamatan.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("DESA ${headerData.namaDesa.uppercase()}", fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text("Alamat: ${headerData.alamatKantor}", fontSize = 9.sp, textAlign = TextAlign.Center)
        }
        subangBitmap?.let {
            Image(bitmap = it, contentDescription = "Logo Kabupaten Subang", modifier = Modifier.size(44.dp))
        }
    }
    Divider(thickness = 2.dp, color = Color.Black)
    Divider(modifier = Modifier.padding(top = 1.dp), color = Color.Black)

    Text(
        text = letterType.pdfTitle,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
    )
    Divider(modifier = Modifier.padding(vertical = 4.dp))
    Text(
        "Nomor : ${nomor.ifBlank { "...................." }}",
        fontSize = 9.sp,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))

    when (letterType) {
        LetterType.DOMISILI -> PreviewDomisiliContent(headerData, letterData as DomisiliData, date)
        LetterType.PISAH_KK -> PreviewPisahKkContent(headerData, letterData as PisahKkData, date)
        LetterType.SKT_DAYAT -> PreviewSktDayatContent(headerData, letterData as SktDayatData, date)
        LetterType.BEDA_LUAS -> PreviewBedaLuasContent(headerData, letterData as BedaLuasData, date)
        LetterType.BEDA_NAMA -> PreviewBedaNamaContent(headerData, letterData as BedaNamaData, date)
        LetterType.BELUM_MENIKAH -> PreviewBelumMenikahContent(headerData, letterData as BelumMenikahData, date)
        LetterType.TIDAK_MAMPU -> PreviewTidakMampuContent(headerData, letterData as TidakMampuData, date)
        LetterType.AHLI_WARIS -> PreviewAhliWarisContent(headerData, letterData as AhliWarisData, date)
        LetterType.NIKAH_TIDAK_TERCATAT -> PreviewNikahTidakTercatatContent(headerData, letterData as NikahTidakTercatatData, date)
        LetterType.PEMAKAMAN -> PreviewPemakamanContent(headerData, letterData as PemakamanData, date)
        LetterType.TIDAK_MEMILIKI_RUMAH -> PreviewTidakMemilikiRumahContent(headerData, letterData as TidakMemilikiRumahData, date)
        LetterType.UNDANGAN -> PreviewUndanganContent(headerData, letterData as UndanganData, date)
    }
}

/** Blok tanda tangan: kiri = tempat/tanggal (+ catatan, mis. Mengetahui Ketua RW), kanan = pejabat. */
@Composable
private fun PreviewSignature(
    header: HeaderData,
    date: String,
    signer: String,
    leftNote: String? = null
) {
    val desaTitleCase = header.namaDesa.trim().split("\\s+".toRegex()).joinToString(" ") {
        it.lowercase().replaceFirstChar { c -> c.uppercase() }
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            TextPreview("$desaTitleCase, $date")
            if (leftNote != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextPreview(leftNote)
                Spacer(modifier = Modifier.height(24.dp))
                TextPreview("(.......................................)")
            }
        }
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            if (signer == "SEKDES") {
                TextPreview("a.n. Kepala Desa $desaTitleCase")
                TextPreview("Sekretaris Desa")
                Spacer(modifier = Modifier.height(24.dp))
                TextPreview(header.namaSekdes)
                // Baris NIP hanya bila diisi; default kosong (tidak ada NIP).
                if (header.nipSekdes.isNotBlank()) TextPreview("NIP: ${header.nipSekdes}")
            } else {
                TextPreview("Kepala Desa $desaTitleCase")
                Spacer(modifier = Modifier.height(24.dp))
                TextPreview(header.namaPejabat)
                if (header.nipPejabat.isNotBlank()) TextPreview("NIP: ${header.nipPejabat}")
            }
        }
    }
}

@Composable
private fun PreviewDomisiliContent(header: HeaderData, data: DomisiliData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${header.namaDesa}, menerangkan dengan sesungguhnya bahwa:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.jenisKelamin.isNotEmpty()) TextPreview("Jenis Kelamin         : ${data.jenisKelamin}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${data.tempatLahir}, ${data.tanggalLahir}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat (KTP)            : ${data.alamatAsal}")
        TextPreview("RT/RW                    : ${data.rt} / ${data.rw}")
        TextPreview("Alamat Domisili Saat Ini : ${data.alamatDomisili}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Adalah benar bahwa orang tersebut di atas adalah penduduk Desa ${header.namaDesa} dan bertempat tinggal di alamat tersebut di atas. Surat keterangan ini diberikan untuk keperluan ${data.keperluanSurat}.")
        PreviewSignature(header, date, "KADES")
    }
}

@Composable
private fun PreviewPisahKkContent(header: HeaderData, data: PisahKkData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${header.namaDesa}, menerangkan bahwa:")
        TextPreview("Nama 1              : ${data.nama1}")
        TextPreview("NIK 1                : ${data.nik1}")
        TextPreview("Nama 2              : ${data.nama2}")
        TextPreview("NIK 2                : ${data.nik2}")
        TextPreview("Nomor KK Lama  : ${data.nomorKKLama}")
        TextPreview("Alamat               : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Adalah benar bahwa ${data.nama1} telah pisah Kartu Keluarga dari ${data.nama2} (KK lama nomor ${data.nomorKKLama}) dan beralamat di ${data.alamat}. Surat keterangan ini diberikan untuk keperluan pengurusan Kartu Keluarga baru.")
        PreviewSignature(header, date, "SEKDES")
    }
}

@Composable
private fun PreviewSktDayatContent(header: HeaderData, data: SktDayatData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${header.namaDesa}, menerangkan dengan sesungguhnya bahwa:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${data.tempatLahir}, ${data.tanggalLahir}")
        TextPreview("Alamat                    : ${data.alamat}")
        if (data.luasTanah.isNotEmpty()) TextPreview("Luas Tanah             : ${data.luasTanah} m²")
        if (data.luasBangunan.isNotEmpty()) TextPreview("Luas Bangunan       : ${data.luasBangunan} m²")
        TextPreview("SPPT / NOP           : ${data.spptNop}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Batas-batas tanah:")
        TextPreview("Sebelah Utara   : ${data.batasUtara}")
        TextPreview("Sebelah Timur   : ${data.batasTimur}")
        TextPreview("Sebelah Selatan : ${data.batasSelatan}")
        TextPreview("Sebelah Barat   : ${data.batasBarat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Adalah benar bahwa tanah beserta bangunan di atas adalah milik ${data.namaLengkap} dan tidak dalam keadaan sengketa. Surat keterangan ini diberikan untuk keperluan administrasi pertanahan.")
        PreviewSignature(header, date, "KADES")
    }
}

@Composable
private fun PreviewBedaLuasContent(header: HeaderData, data: BedaLuasData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${header.namaDesa}, menerangkan bahwa:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        TextPreview("Alamat                    : ${data.alamat}")
        TextPreview("RT/RW                    : ${data.rt} / ${data.rw}")
        TextPreview("Nomor Sertifikat          : ${data.nomorSertifikat}")
        TextPreview("Luas Sertifikat           : ${data.luasSertifikat} m²")
        TextPreview("Nomor SPPT                : ${data.nomorSPPT}")
        TextPreview("Luas SPPT                 : ${data.luasSPPT} m²")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Adalah benar bahwa terdapat perbedaan luas tanah antara Sertifikat dan SPPT yang dimiliki oleh yang bersangkutan, sedangkan pemilik tanah tersebut adalah orang yang sama. Surat keterangan ini diberikan untuk keperluan administrasi pertanahan.")
        PreviewSignature(header, date, "KADES")
    }
}

@Composable
private fun PreviewBedaNamaContent(header: HeaderData, data: BedaNamaData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${header.namaDesa}, menerangkan bahwa:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        TextPreview("Nama di Dokumen 1   : ${data.namaDokumen1}")
        TextPreview("Nama di Dokumen 2   : ${data.namaDokumen2}")
        TextPreview("Alasan / Keterangan : ${data.alasan}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Adalah benar bahwa nama \"${data.namaDokumen1}\" dan nama \"${data.namaDokumen2}\" tersebut di atas adalah orang yang sama, yaitu ${data.namaLengkap}. Surat keterangan ini diberikan untuk keperluan administrasi kependudukan.")
        PreviewSignature(header, date, "KADES")
    }
}

@Composable
private fun PreviewBelumMenikahContent(header: HeaderData, data: BelumMenikahData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${header.namaDesa}, menerangkan bahwa:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.jenisKelamin.isNotEmpty()) TextPreview("Jenis Kelamin         : ${data.jenisKelamin}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${data.tempatLahir}, ${data.tanggalLahir}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        TextPreview("RT/RW                    : ${data.rt} / ${data.rw}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Adalah benar bahwa yang bersangkutan sampai saat ini belum pernah melakukan perkawinan yang tercatat pada kantor pencatat perkawinan yang berwenang. Surat keterangan ini diberikan untuk keperluan ${data.keperluan}.")
        PreviewSignature(header, date, "KADES")
    }
}

@Composable
private fun PreviewTidakMampuContent(header: HeaderData, data: TidakMampuData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${header.namaDesa}, menerangkan bahwa:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.jenisKelamin.isNotEmpty()) TextPreview("Jenis Kelamin         : ${data.jenisKelamin}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${data.tempatLahir}, ${data.tanggalLahir}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        TextPreview("RT/RW                    : ${data.rt} / ${data.rw}")
        if (data.penghasilanBulanan.isNotEmpty()) TextPreview("Penghasilan Bulanan : Rp ${data.penghasilanBulanan}")
        Spacer(modifier = Modifier.height(8.dp))
        val tujuanText = if (data.tujuan.isNotBlank()) " di ${data.tujuan}" else ""
        TextPreview("Adalah benar bahwa yang bersangkutan termasuk dalam kategori keluarga kurang mampu (tidak mampu) menurut keterangan Ketua RT/RW setempat dan sepengetahuan kami. Surat keterangan ini diberikan untuk keperluan ${data.keperluan}$tujuanText.")
        PreviewSignature(header, date, "KADES", leftNote = "Mengetahui,\nKetua RW")
    }
}

@Composable
private fun PreviewAhliWarisContent(header: HeaderData, data: AhliWarisData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${header.namaDesa}, menerangkan bahwa:")
        TextPreview("Nama Peninggal       : ${data.namaPeninggal}")
        TextPreview("NIK Peninggal         : ${data.nikPeninggal}")
        TextPreview("Alamat Peninggal     : ${data.alamatPeninggal}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Adapun ahli waris dari almarhum/almarhumah tersebut di atas adalah:")
        TextPreview("Nama Ahli Waris 1       : ${data.namaAhliWaris1}")
        TextPreview("NIK Ahli Waris 1         : ${data.nikAhliWaris1}")
        TextPreview("Hubungan Ahli Waris 1 : ${data.hubunganAhliWaris1}")
        if (data.namaAhliWaris2.isNotBlank() || data.nikAhliWaris2.isNotBlank()) {
            TextPreview("Nama Ahli Waris 2       : ${data.namaAhliWaris2}")
            TextPreview("NIK Ahli Waris 2         : ${data.nikAhliWaris2}")
            TextPreview("Hubungan Ahli Waris 2 : ${data.hubunganAhliWaris2}")
        }
        if (data.keterangan.isNotBlank()) TextPreview("Keterangan : ${data.keterangan}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Adalah benar bahwa orang-orang tersebut di atas adalah ahli waris yang sah dari ${data.namaPeninggal}. Surat keterangan ini diberikan untuk keperluan administrasi ahli waris.")
        PreviewSignature(header, date, "KADES")
    }
}

@Composable
private fun PreviewNikahTidakTercatatContent(header: HeaderData, data: NikahTidakTercatatData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${header.namaDesa}, menerangkan bahwa:")
        TextPreview("Nama Pria                          : ${data.namaPria}")
        TextPreview("NIK Pria                            : ${data.nikPria}")
        if (data.tempatLahirPria.isNotEmpty() || data.tanggalLahirPria.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir Pria   : ${data.tempatLahirPria}, ${data.tanggalLahirPria}")
        TextPreview("Nama Wanita                      : ${data.namaWanita}")
        TextPreview("NIK Wanita                        : ${data.nikWanita}")
        if (data.tempatLahirWanita.isNotEmpty() || data.tanggalLahirWanita.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir Wanita : ${data.tempatLahirWanita}, ${data.tanggalLahirWanita}")
        TextPreview("Tanggal Pernikahan           : ${data.tanggalPernikahan}")
        TextPreview("Tempat Pernikahan            : ${data.tempatPernikahan}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Adalah benar bahwa ${data.namaPria} dan ${data.namaWanita} telah melangsungkan pernikahan pada tanggal ${data.tanggalPernikahan} di ${data.tempatPernikahan}, namun pernikahan tersebut belum tercatat pada kantor pencatat perkawinan yang berwenang.")
        PreviewSignature(header, date, "KADES")
    }
}

@Composable
private fun PreviewPemakamanContent(header: HeaderData, data: PemakamanData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${header.namaDesa}, menerangkan bahwa:")
        TextPreview("Nama Almarhum/almarhumah : ${data.namaAlmarhum}")
        TextPreview("NIK                          : ${data.nikAlmarhum}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${data.tempatLahir}, ${data.tanggalLahir}")
        TextPreview("Alamat                    : ${data.alamat}")
        TextPreview("Tanggal Meninggal    : ${data.tanggalMeninggal}")
        TextPreview("Lokasi Makam          : ${data.lokasiMakam}")
        TextPreview("Nama Keluarga         : ${data.namaKeluarga}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Adalah benar bahwa ${data.namaAlmarhum} telah meninggal dunia pada tanggal ${data.tanggalMeninggal} dan dimakamkan di ${data.lokasiMakam}. Surat keterangan ini diberikan atas permohonan keluarga ${data.namaKeluarga} untuk keperluan administrasi pemakaman.")
        PreviewSignature(header, date, "KADES")
    }
}

@Composable
private fun PreviewTidakMemilikiRumahContent(header: HeaderData, data: TidakMemilikiRumahData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${data.tempatLahir}, ${data.tanggalLahir}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat Tinggal          : ${data.alamatTinggal}")
        Spacer(modifier = Modifier.height(8.dp))
        val keteranganText = if (data.keterangan.isNotBlank()) " ${data.keterangan}" else ""
        TextPreview("Dengan ini menyatakan dengan sesungguhnya bahwa sampai saat ini saya belum/tidak memiliki rumah tinggal sendiri dan menumpang/tinggal di alamat tersebut di atas.$keteranganText")
        TextPreview("Demikian surat pernyataan ini dibuat dengan sebenar-benarnya dan dapat dipergunakan sebagaimana mestinya.")
        PreviewSignature(header, date, "KADES")
    }
}

@Composable
private fun PreviewUndanganContent(header: HeaderData, data: UndanganData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Sifat: Penting")
        TextPreview("Lampiran: -")
        TextPreview("Perihal: Undangan")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Kepada Yth.")
        TextPreview(data.tujuanYth)
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Dengan hormat, kami mengundang Bapak/Ibu/Saudara/i untuk hadir dalam acara berikut:")
        val tanggalAcara = listOf(data.tanggal, data.bulan, data.tahun).filter { it.isNotEmpty() }.joinToString(" ")
        TextPreview("Hari/Tanggal   : ${data.hari}, $tanggalAcara")
        TextPreview("Pukul           : ${data.pukul} WIB")
        TextPreview("Tempat          : ${data.tempat}")
        TextPreview("Agenda          : ${data.agendaAcara}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Atas perhatian dan kehadiran Bapak/Ibu/Saudara/i, kami ucapkan terima kasih.")
        if (data.daftarTembusan.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview("Tembusan:")
            TextPreview(data.daftarTembusan)
        }
        PreviewSignature(header, date, "KADES")
    }
}

@Composable
private fun TextPreview(text: String) {
    Text(
        text = text,
        fontSize = 9.sp,
        modifier = Modifier.fillMaxWidth()
    )
}
