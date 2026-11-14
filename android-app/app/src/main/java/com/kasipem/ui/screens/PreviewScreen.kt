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
 * Pratinjau A4 untuk 33/33 jenis surat/formulir. Isi sama dengan PdfGenerator
 * (satu kanon field). Tombol PDF benar-benar membuat file PDF di cache
 * aplikasi; tombol Cetak mengirim PDF itu ke PrintManager Android.
 * Setiap PDF/cetak yang berhasil tercatat ke Riwayat Surat lokal
 * (SharedPreferences, lihat HistoryStore). Status (berhasil/gagal +
 * nama file) tampil sebagai teks persisten di bawah tombol — bukan
 * toast yang menghilang.
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
    val historyStore = remember { HistoryStore(context) }

    fun buildPdfFile(): File {
        val fileName = "kasipem-${letterType.name.lowercase()}-${System.currentTimeMillis()}.pdf"
        val file = File(context.cacheDir, fileName)
        PdfGenerator(context).generatePdf(headerData, letterType, letterData, file)
        // Catat ke Riwayat Surat lokal (tanggal, nomor, jenis, nama pemohon).
        historyStore.add(
            HistoryEntry(
                id = "${letterType.name}-${System.currentTimeMillis()}",
                tanggal = effectiveTanggalSurat(tanggalOf(letterType, letterData)),
                nomorSurat = effectiveNomorSurat(nomorOf(letterType, letterData), letterType),
                jenisSurat = letterType.displayTitle,
                namaPemohon = namaPemohonOf(letterType, letterData),
                timestamp = System.currentTimeMillis()
            )
        )
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
            // FASE 5 — Kematian: landscape; isi menempati kiri ~62% lebar halaman.
            if (letterType == LetterType.KEMATIAN) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.62f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PreviewContent(
                        headerData = headerData,
                        letterData = letterData,
                        letterType = letterType
                    )
                }
            } else {
                PreviewContent(
                    headerData = headerData,
                    letterData = letterData,
                    letterType = letterType
                )
            }
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
    LetterType.KEMATIAN -> (data as KematianData).nomorSurat
    LetterType.USAHA -> (data as UsahaData).nomorSurat
    LetterType.DUDA_JANDA -> (data as DudaJandaData).nomorSurat
    LetterType.F101 -> (data as F101Data).nomorSurat
    LetterType.F102 -> (data as F102Data).nomorSurat
    LetterType.F103 -> (data as F103Data).nomorSurat
    LetterType.F104 -> (data as F104Data).nomorSurat
    LetterType.F105 -> (data as F105Data).nomorSurat
    LetterType.F106 -> (data as F106Data).nomorSurat
    LetterType.F115 -> (data as F115Data).nomorSurat
    LetterType.F201 -> (data as F201Data).nomorSurat
    LetterType.F203 -> (data as F203Data).nomorSurat
    LetterType.F204 -> (data as F204Data).nomorSurat
    LetterType.SPTJM_KEMATIAN -> (data as SptjmKematianData).nomorSurat
    LetterType.SPTJM_PERKAWINAN_PERCERAIAN -> (data as SptjmPerkawinanPerceraianData).nomorSurat
    LetterType.PBI_ASESMEN -> (data as PbiAsesmenData).nomorSurat
    LetterType.PBI_PERNYATAAN -> (data as PbiPernyataanData).nomorSurat
    LetterType.PBI_TIDAK_PUNYA_REKENING -> (data as PbiTidakPunyaRekeningData).nomorSurat
    LetterType.SKTM_PBI -> (data as SktmPbiData).nomorSurat
    LetterType.PBI_REKENING_LISTRIK -> (data as PbiRekeningListrikData).nomorSurat
    LetterType.PERNYATAAN_AKTA_KELAHIRAN -> (data as PernyataanAktaKelahiranData).nomorSurat
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
    LetterType.KEMATIAN -> (data as KematianData).tanggalSurat
    LetterType.USAHA -> (data as UsahaData).tanggalSurat
    LetterType.DUDA_JANDA -> (data as DudaJandaData).tanggalSurat
    LetterType.F101 -> (data as F101Data).tanggalSurat
    LetterType.F102 -> (data as F102Data).tanggalSurat
    LetterType.F103 -> (data as F103Data).tanggalSurat
    LetterType.F104 -> (data as F104Data).tanggalSurat
    LetterType.F105 -> (data as F105Data).tanggalSurat
    LetterType.F106 -> (data as F106Data).tanggalSurat
    LetterType.F115 -> (data as F115Data).tanggalSurat
    LetterType.F201 -> (data as F201Data).tanggalSurat
    LetterType.F203 -> (data as F203Data).tanggalSurat
    LetterType.F204 -> (data as F204Data).tanggalSurat
    LetterType.SPTJM_KEMATIAN -> (data as SptjmKematianData).tanggalSurat
    LetterType.SPTJM_PERKAWINAN_PERCERAIAN -> (data as SptjmPerkawinanPerceraianData).tanggalSurat
    LetterType.PBI_ASESMEN -> (data as PbiAsesmenData).tanggalSurat
    LetterType.PBI_PERNYATAAN -> (data as PbiPernyataanData).tanggalSurat
    LetterType.PBI_TIDAK_PUNYA_REKENING -> (data as PbiTidakPunyaRekeningData).tanggalSurat
    LetterType.SKTM_PBI -> (data as SktmPbiData).tanggalSurat
    LetterType.PBI_REKENING_LISTRIK -> (data as PbiRekeningListrikData).tanggalSurat
    LetterType.PERNYATAAN_AKTA_KELAHIRAN -> (data as PernyataanAktaKelahiranData).tanggalSurat
}

@Composable
private fun PreviewContent(
    headerData: HeaderData,
    letterData: Any,
    letterType: LetterType
) {
    val date = effectiveTanggalSurat(tanggalOf(letterType, letterData))
    val nomor = effectiveNomorSurat(nomorOf(letterType, letterData), letterType)

    // Kop FASE 3 — SATU penentu kopFor() (Models.kt), sama dengan
    // PdfGenerator: Garuda tengah (ttd Kades), Subang kiri (ttd a.n.
    // Kades oleh Sekdes / ttd Sekdes), dua logo (SPTJM/PBI),
    // Disdukcapil teks (seri F). Logo dimuat di sini (di luar cabang
    // when) agar urutan remember stabil; logo gagal dimuat → kop teks
    // saja, tidak crash.
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
    when (kopFor(letterType)) {
        KopStyle.DISDUKCAPIL -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("PEMERINTAH KABUPATEN SUBANG", fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("DINAS KEPENDUDUKAN DAN PENCATATAN SIPIL", fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("Jl. Mayjen Sutoyo S. No. 50 Subang, Telp. (0260) 411424", fontSize = 10.sp, textAlign = TextAlign.Center)
            }
        }
        KopStyle.GARUDA_CENTER -> {
            // Ttd Kepala Desa: logo Garuda SAJA di tengah paling atas, tanpa logo Subang.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(4.dp))
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                garudaBitmap?.let {
                    Image(bitmap = it, contentDescription = "Logo Garuda Pancasila", modifier = Modifier.size(72.dp))
                }
                Text("PEMERINTAH DAERAH ${headerData.namaKabupaten.uppercase()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text(headerData.namaKecamatan.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("DESA ${headerData.namaDesa.uppercase()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("Alamat: ${headerData.alamatKantor}", fontSize = 10.sp, textAlign = TextAlign.Center)
            }
        }
        KopStyle.SUBANG_LEFT -> {
            // Ttd a.n. Kades oleh Sekdes / ttd Sekdes langsung: logo Subang di kiri, tanpa Garuda.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(4.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                subangBitmap?.let {
                    Image(bitmap = it, contentDescription = "Logo Kabupaten Subang", modifier = Modifier.size(44.dp))
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("PEMERINTAH DAERAH ${headerData.namaKabupaten.uppercase()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text(headerData.namaKecamatan.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("DESA ${headerData.namaDesa.uppercase()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Alamat: ${headerData.alamatKantor}", fontSize = 10.sp, textAlign = TextAlign.Center)
                }
                if (subangBitmap != null) Spacer(modifier = Modifier.size(44.dp))
            }
        }
        KopStyle.TABEL_KEMATIAN -> {
            // FASE 5 — Kematian: TANPA kop/logo; tabel 3 baris dengan garis vertikal di kiri.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(72.dp)
                        .background(Color.Black)
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    val kab = headerData.namaKabupaten.removePrefix("KABUPATEN ").trim().split("\\s+".toRegex()).joinToString(" ") {
                        it.lowercase().replaceFirstChar { c -> c.uppercase() }
                    }
                    val kec = headerData.namaKecamatan.removePrefix("KECAMATAN ").trim().split("\\s+".toRegex()).joinToString(" ") {
                        it.lowercase().replaceFirstChar { c -> c.uppercase() }
                    }
                    val desa = headerData.namaDesa.trim().split("\\s+".toRegex()).joinToString(" ") {
                        it.lowercase().replaceFirstChar { c -> c.uppercase() }
                    }
                    Text("Pemerintah/Kota/Kab : $kab", fontSize = 12.sp)
                    Text("Kecamatan : $kec", fontSize = 12.sp)
                    Text("Desa/Kelurahan : $desa", fontSize = 12.sp)
                }
            }
        }
        KopStyle.DUA_LOGO -> {
            // SPTJM / PBI: dua logo seperti semula.
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
                    Text("PEMERINTAH DAERAH ${headerData.namaKabupaten.uppercase()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text(headerData.namaKecamatan.uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("DESA ${headerData.namaDesa.uppercase()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("Alamat: ${headerData.alamatKantor}", fontSize = 10.sp, textAlign = TextAlign.Center)
                }
                subangBitmap?.let {
                    Image(bitmap = it, contentDescription = "Logo Kabupaten Subang", modifier = Modifier.size(44.dp))
                }
            }
        }
    }
    Divider(thickness = 2.dp, color = Color.Black)
    Divider(modifier = Modifier.padding(top = 1.dp), color = Color.Black)

    // Judul + nomor.
    // FASE 5 — Undangan: judul DISEMBUNYIKAN (template tidak punya judul);
    // Kematian: format template (judul + "No.: 460.3 / ... / KS / tahun").
    if (letterType == LetterType.UNDANGAN) {
        // Undangan: nomor saja (sama seperti addNomorLine di PDF).
        Text(
            "Nomor : ${nomor.ifBlank { "...................." }}",
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
    } else if (letterType == LetterType.KEMATIAN) {
        val d = letterData as KematianData
        val reg = d.nomorSurat.ifBlank { "............" }
        val tahun = tahunDariTanggal(effectiveTanggalSurat(d.tanggalSurat))
        Text(
            text = "SURAT KETERANGAN KEMATIAN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Text(
            text = "No.: 460.3 / $reg / KS / $tahun",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Divider(modifier = Modifier.padding(vertical = 4.dp))
    } else {
        Text(
            text = effectivePdfTitle(letterType, letterData),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Divider(modifier = Modifier.padding(vertical = 4.dp))
        // FASE 3: nomor kosong → garis titik-titik; formulir F tanpa nomor
        // tetap tanpa baris Nomor (sama seperti PDF).
        val showNomor = hasNomorBaris(letterType) || nomor.isNotBlank()
        if (showNomor) {
            Text(
                "Nomor : ${nomor.ifBlank { "...................." }}",
                fontSize = 11.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

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
        // ---- Fase 2: 18 tipe baru ----
        LetterType.KEMATIAN -> PreviewKematianContent(headerData, letterData as KematianData, date)
        LetterType.USAHA -> PreviewUsahaContent(headerData, letterData as UsahaData, date)
        LetterType.DUDA_JANDA -> PreviewDudaJandaContent(headerData, letterData as DudaJandaData, date)
        LetterType.F101 -> PreviewF101Content(headerData, letterData as F101Data, date)
        LetterType.F102 -> PreviewF102Content(headerData, letterData as F102Data, date)
        LetterType.F103 -> PreviewF103Content(headerData, letterData as F103Data, date)
        LetterType.F104 -> PreviewF104Content(headerData, letterData as F104Data, date)
        LetterType.F105 -> PreviewF105Content(headerData, letterData as F105Data, date)
        LetterType.F106 -> PreviewF106Content(headerData, letterData as F106Data, date)
        LetterType.F115 -> PreviewF115Content(headerData, letterData as F115Data, date)
        LetterType.F201 -> PreviewF201Content(headerData, letterData as F201Data, date)
        LetterType.F203 -> PreviewF203Content(headerData, letterData as F203Data, date)
        LetterType.F204 -> PreviewF204Content(headerData, letterData as F204Data, date)
        LetterType.SPTJM_KEMATIAN -> PreviewSptjmKematianContent(headerData, letterData as SptjmKematianData, date)
        LetterType.SPTJM_PERKAWINAN_PERCERAIAN -> PreviewSptjmPerkawinanPerceraianContent(headerData, letterData as SptjmPerkawinanPerceraianData, date)
        LetterType.PBI_ASESMEN -> PreviewPbiAsesmenContent(headerData, letterData as PbiAsesmenData, date)
        LetterType.PBI_PERNYATAAN -> PreviewPbiPernyataanContent(headerData, letterData as PbiPernyataanData, date)
        LetterType.PBI_TIDAK_PUNYA_REKENING -> PreviewPbiTidakPunyaRekeningContent(headerData, letterData as PbiTidakPunyaRekeningData, date)
        // ---- Fase 5: 3 tipe baru ----
        LetterType.SKTM_PBI -> PreviewSktmPbiContent(headerData, letterData as SktmPbiData, date)
        LetterType.PBI_REKENING_LISTRIK -> PreviewPbiRekeningListrikContent(headerData, letterData as PbiRekeningListrikData, date)
        LetterType.PERNYATAAN_AKTA_KELAHIRAN -> PreviewPernyataanAktaKelahiranContent(headerData, letterData as PernyataanAktaKelahiranData, date)
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
                // a.n. Kepala Desa oleh Sekdes — dipakai pisah KK,
                // USAHA & Duda/Janda juga (user 2026-11-15: blok Sekdes selalu a.n.).
                TextPreview("a.n. Kepala Desa $desaTitleCase")
                TextPreview("Sekretaris Desa")
                Spacer(modifier = Modifier.height(24.dp))
                TextPreview(header.namaSekdes)
                // Baris NIP hanya bila diisi; default kosong (tidak ada NIP).
                if (header.nipSekdes.isNotBlank()) TextPreview("NIP: ${header.nipSekdes}")
            } else if (signer == "SEKDES_KEMATIAN") {
                // FASE 5 — Kematian SAJA: TANPA a.n. (ikut template/screenshot/xlsm).
                TextPreview("Sekretaris Desa $desaTitleCase")
                Spacer(modifier = Modifier.height(24.dp))
                TextPreview(header.namaSekdes)
                if (header.nipSekdes.isNotBlank()) TextPreview("NIP: ${header.nipSekdes}")
            } else {
                // Kepala Desa saja (tanpa nama desa — sudah ada di baris tempat/tanggal).
                TextPreview("Kepala Desa")
                Spacer(modifier = Modifier.height(24.dp))
                TextPreview(header.namaPejabat)
                if (header.nipPejabat.isNotBlank()) TextPreview("NIP: ${header.nipPejabat}")
            }
        }
    }
}

@Composable
private fun PreviewDomisiliContent(header: HeaderData, data: DomisiliData, date: String) {
    // FASE 5 — narasi = versi WEB (template DOCX); urutan field NIK dulu.
    // Signer TETAP Kades (konflik template — butuh keputusan user).
    val intro = "Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(header.namaDesa)}, Kecamatan ${titleCase(header.namaKecamatan)}, Kabupaten ${titleCase(header.namaKabupaten.removePrefix("KABUPATEN "))}, menerangkan dengan sesungguhnya bahwa:"
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        when (data.jenisDomisili) {
            "Lembaga" -> {
                TextPreview(intro)
                TextPreview("Nama Lembaga      : ${data.namaLembaga}")
                TextPreview("Nama Pimpinan      : ${data.namaPimpinan}")
                TextPreview("Alamat                  : ${data.alamatOrganisasi}")
                Spacer(modifier = Modifier.height(8.dp))
                TextPreview("Lembaga tersebut di atas benar berdomisili di ${data.alamatOrganisasi} dan dipimpin oleh ${data.namaPimpinan}.")
                TextPreview("Surat keterangan ini kami berikan untuk keperluan ${data.keperluanSurat}.")
            }
            "Perusahaan" -> {
                TextPreview(intro)
                TextPreview("Nama Perusahaan : ${data.namaPerusahaan}")
                TextPreview("Nama Pimpinan      : ${data.namaPimpinan}")
                if (data.namaSekretaris.isNotEmpty()) TextPreview("Nama Sekretaris     : ${data.namaSekretaris}")
                if (data.bidangUsaha.isNotEmpty()) TextPreview("Bergerak di Bidang : ${data.bidangUsaha}")
                TextPreview("Alamat                  : ${data.alamatOrganisasi}")
                Spacer(modifier = Modifier.height(8.dp))
                TextPreview("Perusahaan tersebut di atas benar berdomisili di ${data.alamatOrganisasi} dan bergerak di bidang ${data.bidangUsaha}.")
                TextPreview("Surat keterangan ini kami berikan untuk keperluan ${data.keperluanSurat}.")
            }
            else -> {
                TextPreview(intro)
                TextPreview("NIK                          : ${data.nik}")
                TextPreview("Nama                       : ${data.namaLengkap}")
                if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
                if (data.jenisKelamin.isNotEmpty()) TextPreview("Jenis Kelamin         : ${data.jenisKelamin}")
                if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
                TextPreview("Alamat (KTP)            : ${data.alamatAsal}")
                TextPreview("RT/RW                    : ${rtRw(data.rt, data.rw)}")
                TextPreview("Alamat Domisili Saat Ini : ${data.alamatDomisili}")
                Spacer(modifier = Modifier.height(8.dp))
                val rtRwText = if (data.rt.isNotBlank() || data.rw.isNotBlank()) "RT ${data.rt} RW ${data.rw}, " else ""
                TextPreview("Orang tersebut sudah tidak tinggal di alamat sebagaimana tercantum di atas, dan berdasarkan keterangan dari Ketua RT/RW, orang tersebut saat ini benar berdomisili di $rtRwText${data.alamatDomisili}.")
                TextPreview("Surat keterangan ini kami berikan untuk keperluan ${data.keperluanSurat}.")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING)
        PreviewSignature(header, date, "KADES")
    }
}

@Composable
private fun PreviewPisahKkContent(header: HeaderData, data: PisahKkData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(header.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        TextPreview("NIK (1)              : ${data.nik1}")
        TextPreview("Nama (1)            : ${data.nama1}")
        TextPreview("NIK (2)              : ${data.nik2}")
        TextPreview("Nama (2)            : ${data.nama2}")
        TextPreview("Nomor KK Lama  : ${data.nomorKKLama}")
        TextPreview("Alamat               : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Nama-nama tersebut di atas adalah benar warga Desa kami dan telah Pisah KK dari KK Nomor ${data.nomorKKLama} untuk membentuk Kartu Keluarga baru dengan alamat ${data.alamat}.")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_PISAH_KK)
        PreviewSignature(header, date, "SEKDES")
    }
}

@Composable
private fun PreviewSktDayatContent(header: HeaderData, data: SktDayatData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(header.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        TextPreview("Alamat                    : ${data.alamat}")
        if (data.luasTanah.isNotEmpty()) TextPreview("Luas Tanah             : ${data.luasTanah} m²")
        if (data.luasBangunan.isNotEmpty()) TextPreview("Luas Bangunan       : ${data.luasBangunan} m²")
        TextPreview("SPPT / NOP           : ${data.spptNop}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Tanah tersebut di atas adalah benar milik / dikuasai oleh yang bersangkutan dengan batas-batas sebagaimana tersebut di atas, dan sampai saat ini tidak dalam sengketa dengan pihak mana pun.")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Batas-batas tanah tersebut adalah sebagai berikut:")
        TextPreview("Sebelah Utara   : ${data.batasUtara}")
        TextPreview("Sebelah Timur   : ${data.batasTimur}")
        TextPreview("Sebelah Selatan : ${data.batasSelatan}")
        TextPreview("Sebelah Barat   : ${data.batasBarat}")
        if (data.hargaTaksiranBangunan.isNotBlank() || data.hargaTaksiranTanah.isNotBlank() || data.hargaTaksiranJumlah.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview("HARGA TAKSIRAN TANAH :")
            if (data.hargaTaksiranBangunan.isNotBlank()) TextPreview("Bangunan : Rp ${data.hargaTaksiranBangunan}")
            if (data.hargaTaksiranTanah.isNotBlank()) TextPreview("Tanah : Rp ${data.hargaTaksiranTanah}")
            if (data.hargaTaksiranJumlah.isNotBlank()) TextPreview("Jumlah : Rp ${data.hargaTaksiranJumlah}")
        }
        if (data.noRegister.isNotBlank() || data.tanggalRegister.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            if (data.noRegister.isNotBlank()) TextPreview("No. Register : ${data.noRegister}")
            if (data.tanggalRegister.isNotBlank()) TextPreview("Tanggal : ${data.tanggalRegister}")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_SKT)
        PreviewSignature(header, date, "KADES", leftNote = "Mengetahui,\nCamat Kasomalang")
    }
}

@Composable
private fun PreviewBedaLuasContent(header: HeaderData, data: BedaLuasData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(header.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        TextPreview("Alamat                    : ${data.alamat}")
        TextPreview("RT/RW                    : ${rtRw(data.rt, data.rw)}")
        TextPreview("Nomor Sertifikat          : ${data.nomorSertifikat}")
        if (data.nomorNIB.isNotEmpty()) TextPreview("Nomor NIB                  : ${data.nomorNIB}")
        TextPreview("Luas Sertifikat           : ${data.luasSertifikat} m²")
        TextPreview("Nomor SPPT                : ${data.nomorSPPT}")
        TextPreview("Luas SPPT                 : ${data.luasSPPT} m²")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Adalah benar bahwa terdapat perbedaan luas tanah antara Sertifikat dan SPPT yang dimiliki oleh yang bersangkutan, sedangkan pemilik tanah tersebut adalah orang yang sama. Surat keterangan ini diberikan untuk keperluan administrasi pertanahan.")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_BEDA_LUAS)
        PreviewSignature(header, date, "SEKDES")
    }
}

@Composable
private fun PreviewBedaNamaContent(header: HeaderData, data: BedaNamaData, date: String) {
    // FASE 5 — 2 seksi "Keterangan data dari [dokumen]" (template 3b).
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(header.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Keterangan data dari ${data.dokumenPertama.ifBlank { "IJAZAH" }} :")
        TextPreview("NIK                       : ${data.nikDokumenPertama}")
        TextPreview("Nama                     : ${data.namaDokumenPertama}")
        TextPreview("Tempat/Tgl Lahir   : ${data.ttlDokumenPertama}")
        TextPreview("Alamat                  : ${data.alamatDokumenPertama}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Keterangan data dari ${data.dokumenKedua.ifBlank { "BUKU NIKAH & KK" }} :")
        TextPreview("NIK                       : ${data.nikDokumenKedua}")
        TextPreview("Nama                     : ${data.namaDokumenKedua}")
        TextPreview("Tempat/Tgl Lahir   : ${data.ttlDokumenKedua}")
        TextPreview("Alamat                  : ${data.alamatDokumenKedua}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Terdapat perbedaan Nama/Tanggal/Tahun kelahiran yang tertulis di ${data.dokumenPertama.ifBlank { "IJAZAH" }} adalah ${data.namaDokumenPertama} dengan yang tertulis di ${data.dokumenKedua.ifBlank { "BUKU NIKAH & KK" }} adalah ${data.namaDokumenKedua} dan orangnya sama itu-itu juga.")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_BEDA_NAMA)
        PreviewSignature(header, date, "SEKDES")
    }
}

@Composable
private fun PreviewBelumMenikahContent(header: HeaderData, data: BelumMenikahData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(header.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.jenisKelamin.isNotEmpty()) TextPreview("Jenis Kelamin         : ${data.jenisKelamin}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        TextPreview("RT/RW                    : ${rtRw(data.rt, data.rw)}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Adalah benar bahwa yang bersangkutan sampai saat ini belum pernah melakukan perkawinan yang tercatat pada kantor pencatat perkawinan yang berwenang. Surat keterangan ini diberikan untuk keperluan ${data.keperluan}.")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING)
        PreviewSignature(header, date, "SEKDES")
    }
}

@Composable
private fun PreviewTidakMampuContent(header: HeaderData, data: TidakMampuData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        if (data.varianSKTM == "Daftar Kolektif") {
            TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(header.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview("Orang-orang yang tercantum dalam daftar terlampir adalah benar-benar warga Desa kami dan tergolong dalam kategori Keluarga Tidak Mampu. Daftar nama terlampir adalah warga RW 02 Desa ${titleCase(header.namaDesa)}:")
            val warga = data.daftarKolektif.filter { it.nama.isNotBlank() || it.nik.isNotBlank() }
            warga.forEachIndexed { i, w ->
                TextPreview("${i + 1}. ${w.nama} — NIK: ${w.nik} — ${w.alamat}")
            }
            Spacer(modifier = Modifier.height(8.dp))
            val keperluanKolektif = data.keperluan.ifBlank { "pengajuan bantuan sosial Program Jabar Caang" }
            TextPreview("Surat keterangan ini dibuat sebagai salah satu syarat untuk keperluan $keperluanKolektif.")
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview(CLOSING_MISKIN_KOLEKTIF)
            PreviewSignature(header, date, "SEKDES", leftNote = "Ketua RW")
            return@Column
        }
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(header.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.jenisKelamin.isNotEmpty()) TextPreview("Jenis Kelamin         : ${data.jenisKelamin}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        TextPreview("RT/RW                    : ${rtRw(data.rt, data.rw)}")
        if (data.penghasilanBulanan.isNotEmpty()) TextPreview("Penghasilan Bulanan : Rp ${data.penghasilanBulanan}")
        Spacer(modifier = Modifier.height(8.dp))
        if (data.varianSKTM == "Sekolah") {
            val tujuan = data.tujuan.ifBlank { "SMPN Negeri Kasomalang" }
            TextPreview("Orang tersebut adalah warga Desa ${titleCase(header.namaDesa)} dan saat ini akan mendaftar ke $tujuan, namun menurut keterangan Ketua RT/RW setempat dan sepengetahuan kami keadaan ekonominya saat ini termasuk kategori keluarga kurang mampu (Miskin). Untuk itu kami mohon kepada pihak $tujuan agar dapat memberikan keringanan/kebebasan biaya pendidikannya.")
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview(CLOSING_MISKIN_SEKOLAH)
            PreviewSignature(header, date, "SEKDES")
        } else {
            val tujuanText = if (data.tujuan.isNotBlank()) " di ${data.tujuan}" else ""
            TextPreview("Adalah benar bahwa yang bersangkutan termasuk dalam kategori tidak mampu menurut kriteria yang berlaku di desa kami. Surat keterangan ini dibuat sebagai salah satu syarat untuk keperluan ${data.keperluan}$tujuanText.")
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview(CLOSING)
            PreviewSignature(header, date, "SEKDES", leftNote = "Mengetahui,\nKetua RW")
        }
    }
}

@Composable
private fun PreviewAhliWarisContent(header: HeaderData, data: AhliWarisData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        val desaTitleCase = header.namaDesa.trim().split("\\s+".toRegex()).joinToString(" ") {
            it.lowercase().replaceFirstChar { c -> c.uppercase() }
        }
        TextPreview(paragrafPembukaAhliWaris(data, desaTitleCase))
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Dengan demikian susunan Ahli Waris adalah sebagai berikut:")
        val waris = data.daftarAhliWaris.filter { it.nama.isNotBlank() || it.nik.isNotBlank() }
        waris.forEachIndexed { i, w ->
            TextPreview("${i + 1}. ${w.nama} — NIK: ${w.nik} — Tempat/Tgl Lahir: ${ttl(w.tempatLahir, w.tanggalLahir)} — Hubungan: ${w.hubungan}")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Orang-orang tersebut di atas merupakan ahli waris yang sah dari ${sebutanOf(data)} ${data.namaPeninggal}.")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Tidak ada ahli waris selain yang tercantum di dalam surat keterangan ini, sesuai pengakuannya.")
        val saksi = listOf(
            data.namaSaksi1 to data.jabatanSaksi1,
            data.namaSaksi2 to data.jabatanSaksi2
        ).filter { it.first.isNotBlank() || it.second.isNotBlank() }
        if (saksi.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview("Saksi-saksi yang mengetahui keterangan tersebut adalah")
            saksi.forEachIndexed { i, (nama, jabatan) ->
                TextPreview("${i + 1}. $nama — $jabatan")
            }
        }
        if (data.keperluan.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview("Untuk Keperluan : ${data.keperluan}")
        }
        if (data.keterangan.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview("Keterangan: ${data.keterangan}")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_AHLI_WARIS)
        PreviewSignature(header, date, "KADES", leftNote = "Mengetahui,\nCamat Kasomalang")
        // FASE 5 — Lampiran "Data Ahli Waris" DIHAPUS (tidak ada di kanonik web Fase 3).
    }
}

@Composable
private fun PreviewNikahTidakTercatatContent(header: HeaderData, data: NikahTidakTercatatData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(header.namaDesa)}, Kecamatan ${titleCase(header.namaKecamatan)}, Kabupaten ${titleCase(header.namaKabupaten.removePrefix("KABUPATEN "))}, menerangkan perihal perkawinan antara:")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Pihak Pertama (Suami):")
        TextPreview("Nama                          : ${data.namaPria}")
        if (data.binSuami.isNotEmpty()) TextPreview("Bin                             : ${data.binSuami}")
        TextPreview("NIK                             : ${data.nikPria}")
        if (data.tempatLahirPria.isNotEmpty() || data.tanggalLahirPria.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahirPria, data.tanggalLahirPria)}")
        if (data.agamaSuami.isNotEmpty()) TextPreview("Agama                         : ${data.agamaSuami}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Dengan seorang Wanita (Istri):")
        TextPreview("Nama                          : ${data.namaWanita}")
        if (data.binIstri.isNotEmpty()) TextPreview("Bin                             : ${data.binIstri}")
        TextPreview("NIK                             : ${data.nikWanita}")
        if (data.tempatLahirWanita.isNotEmpty() || data.tanggalLahirWanita.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahirWanita, data.tanggalLahirWanita)}")
        if (data.agamaIstri.isNotEmpty()) TextPreview("Agama                         : ${data.agamaIstri}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(narasiNikahTidakTercatat(data))
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_NIKAH)
        PreviewSignature(header, date, "SEKDES")
    }
}

@Composable
private fun PreviewPemakamanContent(header: HeaderData, data: PemakamanData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini,")
        TextPreview("Kepala Desa : ${titleCase(header.namaDesa)}")
        TextPreview("Kecamatan   : ${titleCase(header.namaKecamatan.removePrefix("KECAMATAN "))}")
        TextPreview("Kabupaten   : ${titleCase(header.namaKabupaten.removePrefix("KABUPATEN "))}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Dengan ini menerangkan bahwa :")
        TextPreview("Nama Almarhum/almarhumah : ${data.namaAlmarhum}")
        TextPreview("NIK                          : ${data.nikAlmarhum}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        TextPreview("Alamat                    : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Telah meninggal dunia pada :")
        TextPreview("Hari        : ${data.hariMeninggal}")
        TextPreview("Tanggal  : ${data.tanggalMeninggal}")
        TextPreview("Jam        : ${data.jamMeninggal}")
        TextPreview("Tempat   : ${data.tempatMeninggal}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Dan telah disemayamkan pada :")
        TextPreview("Hari        : ${data.hariDisemayamkan}")
        TextPreview("Tanggal  : ${data.tanggalDisemayamkan}")
        TextPreview("Tempat   : ${data.tempatDisemayamkan}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Almarhum/almarhumah tersebut di atas telah meninggal dunia dan dimakamkan di ${data.lokasiMakam} atas permohonan keluarga, yaitu ${data.namaKeluarga}.")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_PEMAKAMAN)
        PreviewSignature(header, date, "SEKDES")
    }
}

@Composable
private fun PreviewTidakMemilikiRumahContent(header: HeaderData, data: TidakMemilikiRumahData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat Tinggal          : ${data.alamatTinggal}")
        Spacer(modifier = Modifier.height(8.dp))
        // FASE 5 — narasi orang ketiga (template); Android lama orang pertama ("saya") — SALAH.
        TextPreview("Menyatakan dengan sebenarnya bahwa orang tersebut sampai saat ini benar-benar belum memiliki rumah tinggal sendiri dan masih menumpang / tinggal bersama keluarga di alamat tersebut di atas.")
        if (data.keterangan.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview("Keterangan: ${data.keterangan}")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_PERMOHONAN_RUMAH)
        PreviewSignature(header, date, "SEKDES")
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
        TextPreview("Pukul           : ${if (data.pukul.isEmpty()) "" else "${data.pukul} WIB"}")
        TextPreview("Tempat          : ${data.tempat}")
        TextPreview("Agenda          : ${data.agendaAcara}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_UNDANGAN)
        if (data.daftarTembusan.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview("Tembusan:")
            tembusanItems(data.daftarTembusan).forEach { item ->
                TextPreview(item)
            }
        }
        PreviewSignature(header, date, "KADES")
    }
}

@Composable
private fun TextPreview(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        modifier = Modifier.fillMaxWidth()
    )
}

/* ==================================================================
   Fase 2 — 18 konten preview baru.
   ================================================================== */

/** Blok tanda tangan multi-kolom untuk SPTJM/formulir (Yang Menyatakan, saksi, RT/RW, dll). */
@Composable
private fun PreviewSignatureColumns(
    header: HeaderData,
    date: String,
    place: String,
    columns: List<Pair<String, String>>,
    showDate: Boolean = true
) {
    Spacer(modifier = Modifier.height(12.dp))
    if (showDate) {
        TextPreview("$place, $date")
        Spacer(modifier = Modifier.height(8.dp))
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        columns.forEach { (label, name) ->
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                TextPreview(label)
                Spacer(modifier = Modifier.height(24.dp))
                TextPreview(if (name.isBlank()) "(.......................................)" else name)
            }
        }
    }
}

/** FASE 5 — varian PreviewSignatureColumns dengan baris `sub` di bawah nama
 * (mis. "NIK. ............", "Materai Rp10.000", "Pejabat Dukcapil yang
 * Membidangi", "Tanggal Pemasukan Data / Paraf Petugas"). */
@Composable
private fun PreviewSignatureColumnsSub(
    header: HeaderData,
    date: String,
    place: String,
    columns: List<Triple<String, String, String?>>,
    showDate: Boolean = true
) {
    Spacer(modifier = Modifier.height(12.dp))
    if (showDate) {
        TextPreview("$place, $date")
        Spacer(modifier = Modifier.height(8.dp))
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        columns.forEach { (label, name, sub) ->
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                TextPreview(label)
                Spacer(modifier = Modifier.height(24.dp))
                TextPreview(if (name.isBlank()) "(.......................................)" else name)
                if (!sub.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextPreview(sub)
                }
            }
        }
    }
}

@Composable
private fun PreviewKematianContent(header: HeaderData, data: KematianData, date: String) {
    // FASE 5 — ditulis ulang PERSIS FASE5_BRIEF.md: intro tanpa jabatan,
    // seksi meninggal/pelapor sebagai field, TANPA penutup, ttd Sekdes
    // TANPA a.n. Tanggal format DD-MM-YYYY khusus form ini.
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini menerangkan")
        TextPreview("Nama                       : ${data.namaAlmarhum}")
        TextPreview("NIK                          : ${data.nikAlmarhum}")
        if (data.jenisKelamin.isNotEmpty()) TextPreview("Jenis Kelamin         : ${data.jenisKelamin}")
        if (data.agama.isNotEmpty()) TextPreview("Agama                    : ${data.agama}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${data.tempatLahir}, ${toDdMmYyyy(data.tanggalLahir)}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Telah meninggal dunia pada :")
        TextPreview("Hari                    : ${data.hariMeninggal}")
        TextPreview("Tanggal              : ${toDdMmYyyy(data.tanggalMeninggal)}")
        TextPreview("Bertempat di       : ${data.tempatMeninggal}")
        TextPreview("Penyebab Kematian : ${data.penyebabMeninggal}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Surat Keterangan ini dibuat berdasarkan keterangan Pelapor :")
        TextPreview("NIK                          : ${data.nikPelapor}")
        TextPreview("Nama                       : ${data.namaPelapor}")
        if (data.tempatLahirPelapor.isNotEmpty() || data.tanggalLahirPelapor.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${data.tempatLahirPelapor}, ${toDdMmYyyy(data.tanggalLahirPelapor)}")
        if (data.pekerjaanPelapor.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaanPelapor}")
        TextPreview("Alamat                    : ${data.alamatPelapor}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Hubungan pelapor dengan yang meninggal adalah : ${data.hubunganPelapor}")
        // TANPA paragraf penutup (template tidak punya penutup).
        PreviewSignature(header, toDdMmYyyy(date), "SEKDES_KEMATIAN")
    }
}

@Composable
private fun PreviewUsahaContent(header: HeaderData, data: UsahaData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(header.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Orang tersebut adalah warga Desa ${titleCase(header.namaDesa)}, dan menurut pengakuannya, saat ini mempunyai usaha sebagai berikut:")
        TextPreview("Usaha                    : ${data.namaUsaha}")
        TextPreview("Alamat Usaha         : ${data.alamatUsaha}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Adalah benar bahwa yang bersangkutan memiliki usaha ${data.namaUsaha} yang bertempat di ${data.alamatUsaha}. Surat keterangan ini diberikan untuk keperluan administrasi usaha.")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_USAHA)
        PreviewSignature(header, date, "SEKDES")
    }
}

@Composable
private fun PreviewDudaJandaContent(header: HeaderData, data: DudaJandaData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, Kepala Desa ${titleCase(header.namaDesa)}, menerangkan dengan sesungguhnya bahwa:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        // FASE 5 — baris Status DIHAPUS dari tabel (status hanya di judul + narasi).
        val status = data.statusDudaJanda.lowercase()
        if (data.rt.isNotBlank() || data.rw.isNotBlank()) {
            TextPreview("Berdasarkan keterangan dari Ketua RT ${data.rt} RW ${data.rw}, orang tersebut saat ini benar warga Desa ${titleCase(header.namaDesa)} dan sampai saat ini masih berstatus $status dan belum menikah lagi.")
        } else {
            TextPreview("Adalah benar bahwa yang bersangkutan berstatus $status dan sampai saat ini belum menikah lagi.")
        }
        if (data.keteranganRT.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview("Keterangan Ketua RT: ${data.keteranganRT}.")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING)
        PreviewSignature(header, date, "SEKDES")
    }
}

@Composable
private fun PreviewF101Content(header: HeaderData, data: F101Data, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Dengan ini kami sampaikan biodata keluarga sebagai berikut:")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Data Kepala Keluarga")
        TextPreview("Nama Kepala Keluarga : ${data.namaKepalaKeluarga}")
        TextPreview("NIK Kepala Keluarga   : ${data.nikKepalaKeluarga}")
        TextPreview("No. KK                      : ${data.noKK}")
        TextPreview("Alamat                      : ${data.alamatKeluarga}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Daftar Anggota Keluarga")
        data.daftarAnggota.forEachIndexed { i, a ->
            if (a.nama.isNotBlank() || a.nik.isNotBlank()) {
                TextPreview("${i + 1}. ${a.nama} — NIK: ${a.nik} — SHDK: ${a.shdk} — TTL: ${ttl(a.tempatLahir, a.tanggalLahir)} — Agama: ${a.agama} — Pendidikan: ${a.pendidikan} — Pekerjaan: ${a.pekerjaan} — Status Kawin: ${a.statusKawin} — Ayah: ${a.namaAyah} — Ibu: ${a.namaIbu}")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_F101)
        PreviewSignatureColumns(header, date, "Subang", listOf("Kepala Dinas Kependudukan dan Pencatatan Sipil Kabupaten Subang" to "", "Kepala Keluarga" to data.namaKepalaKeluarga))
    }
}

@Composable
private fun PreviewF102Content(header: HeaderData, data: F102Data, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Dengan ini mengajukan permohonan sebagai berikut:")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        TextPreview("No. KK                      : ${data.noKK}")
        TextPreview("Jenis Permohonan    : ${data.jenisPermohonan}")
        if (data.daftarLampiran.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview("PERSYARATAN YANG DILAMPIRKAN")
            TextPreview(data.daftarLampiran)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_FORM)
        PreviewSignatureColumns(header, date, "Subang", listOf("Petugas Pendaftaran" to "", "Pemohon" to data.namaLengkap))
    }
}

@Composable
private fun PreviewF103Content(header: HeaderData, data: F103Data, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Dengan ini mengajukan permohonan pindah penduduk sebagai berikut:")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("No. KK                              : ${data.noKK}")
        TextPreview("Nama Pemohon                   : ${data.namaPemohon}")
        TextPreview("NIK                                   : ${data.nik}")
        TextPreview("Jenis Permohonan            : ${data.jenisPermohonan}")
        TextPreview("Alamat Asal                      : ${data.alamatAsal}")
        TextPreview("RT/RW                             : ${rtRw(data.rt, data.rw)}")
        TextPreview("Klasifikasi Kepindahan   : ${data.klasifikasiPindah}")
        TextPreview("Alamat Pindah                  : ${data.alamatPindah}")
        TextPreview("Alasan Pindah                  : ${data.alasanPindah}")
        TextPreview("Jenis Kepindahan             : ${data.jenisKepindahan}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Anggota Keluarga yang Pindah")
        data.daftarAnggotaPindah.forEachIndexed { i, a ->
            if (a.nama.isNotBlank() || a.nik.isNotBlank()) {
                TextPreview("${i + 1}. ${a.nama} — NIK: ${a.nik} — SHDK: ${a.shdk}")
            }
        }
        if (data.anggotaTidakPindah.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview("Anggota keluarga yang tidak pindah: ${data.anggotaTidakPindah}.")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_FORM)
        PreviewSignatureColumns(header, date, "Subang", listOf("Mengetahui, Kepala Dinas Kependudukan dan Pencatatan Sipil Kabupaten Subang" to "", "Pemohon" to data.namaPemohon))
    }
}

@Composable
private fun PreviewF104Content(header: HeaderData, data: F104Data, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini, dengan sesungguhnya menyatakan bahwa saya tidak memiliki dokumen kependudukan.")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("Alamat                    : ${data.alamat}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        TextPreview("Nama Ibu                 : ${data.namaIbu}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Dengan ini menyatakan bahwa saya tidak memiliki Dokumen Kependudukan dan apabila di kemudian hari ternyata pernyataan saya ini tidak benar, maka saya bersedia diproses secara hukum sesuai dengan peraturan perundang-undangan serta dokumen yang diterbitkan dari permohonan ini menjadi tidak sah.")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_SPTJM)
        PreviewSignatureColumns(header, date, "Subang", listOf("Yang Menyatakan" to data.namaLengkap))
    }
}

@Composable
private fun PreviewF105Content(header: HeaderData, data: F105Data, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Kami yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa perkawinan kami belum tercatat.")
        TextPreview("Nama Suami            : ${data.namaSuami}")
        TextPreview("NIK Suami              : ${data.nikSuami}")
        TextPreview("Nama Istri               : ${data.namaIstri}")
        TextPreview("NIK Istri                 : ${data.nikIstri}")
        val tanggalKawin = listOf(data.tanggalPerkawinan, data.bulanPerkawinan, data.tahunPerkawinan).filter { it.isNotEmpty() }.joinToString(" ")
        TextPreview("Tanggal Perkawinan : $tanggalKawin")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Saksi-saksi")
        TextPreview("Nama Saksi 1 : ${data.namaSaksi1}")
        TextPreview("NIK Saksi 1   : ${data.nikSaksi1}")
        TextPreview("Nama Saksi 2 : ${data.namaSaksi2}")
        TextPreview("NIK Saksi 2   : ${data.nikSaksi2}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Daftar Anak")
        data.daftarAnak.forEachIndexed { i, a ->
            if (a.nama.isNotBlank()) {
                TextPreview("${i + 1}. ${a.nama} — No. Akta Kelahiran: ${a.noAktaKelahiran} — SHDK: ${a.shdk}")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_F105)
        PreviewSignatureColumnsSub(
            header, date, "Subang",
            listOf(
                Triple("PIHAK KEDUA (Istri)", data.namaIstri, "NIK. ${data.nikIstri}"),
                Triple("PIHAK PERTAMA (Suami)", data.namaSuami, "NIK. ${data.nikSuami}"),
                Triple("SAKSI II", data.namaSaksi2, "NIK. ${data.nikSaksi2}"),
                Triple("SAKSI I", data.namaSaksi1, "NIK. ${data.nikSaksi1}")
            )
        )
    }
}

@Composable
private fun PreviewF106Content(header: HeaderData, data: F106Data, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa telah terjadi perubahan elemen data kependudukan sebagai berikut:")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        TextPreview("No. KK                      : ${data.noKK}")
        TextPreview("Alamat                    : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Anggota Keluarga yang Mengalami Perubahan")
        data.tabelAnggota.forEachIndexed { i, a ->
            if (a.nama.isNotBlank() || a.perubahan.isNotBlank() || a.keterangan.isNotBlank()) {
                TextPreview("${i + 1}. ${a.nama} — NIK: ${a.nik} — SHDK: ${a.shdk} — Keterangan: ${a.keterangan.ifBlank { a.perubahan }}")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Perubahan Pendidikan")
        if (data.pendidikanSemula.isNotEmpty()) TextPreview("Semula : ${data.pendidikanSemula}")
        if (data.pendidikanMenjadi.isNotEmpty()) TextPreview("Menjadi : ${data.pendidikanMenjadi}")
        if (data.pendidikanDasar.isNotEmpty()) TextPreview("Dasar Perubahan : ${data.pendidikanDasar}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Perubahan Pekerjaan")
        if (data.pekerjaanSemula.isNotEmpty()) TextPreview("Semula : ${data.pekerjaanSemula}")
        if (data.pekerjaanMenjadi.isNotEmpty()) TextPreview("Menjadi : ${data.pekerjaanMenjadi}")
        if (data.pekerjaanDasar.isNotEmpty()) TextPreview("Dasar Perubahan : ${data.pekerjaanDasar}")
        if (data.perubahanAgama.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview("Perubahan Agama: ${data.perubahanAgama}.")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Terlampir disampaikan fotocopi berkas-berkas yang terkait dengan perubahan elemen data tersebut.")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_F106)
        PreviewSignatureColumns(header, date, "Subang", listOf("Yang Menyatakan" to data.namaLengkap))
    }
}

@Composable
private fun PreviewF115Content(header: HeaderData, data: F115Data, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Dengan ini mengajukan permohonan Kartu Keluarga baru sebagai berikut:")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Nama Pemohon            : ${data.namaPemohon}")
        TextPreview("NIK                          : ${data.nik}")
        TextPreview("No. KK Lama             : ${data.noKKLama}")
        TextPreview("Alamat                    : ${data.alamat}")
        TextPreview("RT/RW                    : ${rtRw(data.rt, data.rw)}")
        TextPreview("Alasan Permohonan   : ${data.alasanPermohonan}")
        TextPreview("Jumlah Anggota       : ${data.jumlahAnggota}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Daftar Anggota Keluarga")
        data.daftarAnggota.forEachIndexed { i, a ->
            if (a.nama.isNotBlank() || a.nik.isNotBlank()) {
                TextPreview("${i + 1}. ${a.nama} — NIK: ${a.nik} — SHDK: ${a.shdk}")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("PERSYARATAN PENERBITAN KK")
        TextPreview("A. KK BARU: 1. Fotokopi kutipan akta kawin/nikah; 2. Fotokopi kutipan akta perceraian; 3. Fotokopi kutipan akta kelahiran kepala keluarga & seluruh anggota; 4. Surat keterangan pindah/datang; 5. KK yang rusak (apabila KK rusak).")
        TextPreview("B. PERUBAHAN KK: 1. Penambahan anggota karena kelahiran (KK lama, kutipan akta kelahiran); 2. Pengurangan anggota (KK lama, surat keterangan kematian, surat keterangan pindah); 3. Penambahan anggota karena menumpang dalam KK (KK yang akan ditumpangi, surat keterangan pindah datang).")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_FORM)
        PreviewSignatureColumns(header, date, "Subang", listOf("Mengetahui, Camat Kasomalang" to "", "Pemohon" to data.namaPemohon))
        PreviewSignatureColumnsSub(
            header, date, "Subang",
            listOf(
                Triple("Kepala Desa / Lurah", header.namaPejabat, null),
                Triple("Petugas", "", "Tanggal Pemasukan Data\nParaf Petugas")
            ),
            showDate = false
        )
    }
}

@Composable
private fun PreviewF201Content(header: HeaderData, data: F201Data, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Dengan ini melaporkan peristiwa pencatatan sipil sebagai berikut:")
        TextPreview("Jenis Pelaporan: ${data.jenisPelaporan}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Data Pelapor")
        TextPreview("Nama Pelapor   : ${data.pelaporNama}")
        TextPreview("NIK Pelapor     : ${data.pelaporNik}")
        TextPreview("Alamat Pelapor : ${data.pelaporAlamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Subjek Akta")
        TextPreview("Nama Subjek Akta I : ${data.subjek1Nama}")
        TextPreview("NIK Subjek Akta I   : ${data.subjek1Nik}")
        if (data.subjek2Nama.isNotEmpty() || data.subjek2Nik.isNotEmpty()) {
            TextPreview("Nama Subjek Akta II : ${data.subjek2Nama}")
            TextPreview("NIK Subjek Akta II   : ${data.subjek2Nik}")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Saksi-saksi")
        TextPreview("Nama Saksi 1 : ${data.saksi1Nama}")
        TextPreview("NIK Saksi 1   : ${data.saksi1Nik}")
        TextPreview("Nama Saksi 2 : ${data.saksi2Nama}")
        TextPreview("NIK Saksi 2   : ${data.saksi2Nik}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Orang Tua")
        TextPreview("Nama Ayah : ${data.ayahNama}")
        TextPreview("NIK Ayah   : ${data.ayahNik}")
        TextPreview("Nama Ibu   : ${data.ibuNama}")
        TextPreview("NIK Ibu     : ${data.ibuNik}")
        Spacer(modifier = Modifier.height(8.dp))
        // Seksi kondisional — hanya seksi jenis pelaporan yang dipilih yang tampil.
        when (f201Section(data.jenisPelaporan)) {
            "anak" -> {
                TextPreview("Data Anak")
                TextPreview("Nama Anak                      : ${data.anakNama}")
                if (data.anakTempatLahir.isNotEmpty() || data.anakTanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.anakTempatLahir, data.anakTanggalLahir)}")
                if (data.anakPukulLahir.isNotEmpty()) TextPreview("Pukul Lahir               : ${data.anakPukulLahir}")
                if (data.anakJenisKelamin.isNotEmpty()) TextPreview("Jenis Kelamin         : ${data.anakJenisKelamin}")
                if (data.anakKe.isNotEmpty()) TextPreview("Anak Ke                : ${data.anakKe}")
            }
            "lahirMati" -> {
                TextPreview("Data Lahir Mati")
                TextPreview("Nama        : ${data.lahirMatiNama}")
                TextPreview("Tanggal  : ${data.lahirMatiTanggal}")
                TextPreview("Pukul      : ${data.lahirMatiPukul}")
                TextPreview("Tempat   : ${data.lahirMatiTempat}")
                if (data.lahirMatiJenisKelamin.isNotEmpty()) TextPreview("Jenis Kelamin : ${data.lahirMatiJenisKelamin}")
                TextPreview("Sebab     : ${data.lahirMatiSebab}")
                if (data.lahirMatiKeterangan.isNotEmpty()) TextPreview("Keterangan : ${data.lahirMatiKeterangan}")
            }
            "pasangan" -> {
                TextPreview("Data Perkawinan / Perceraian")
                TextPreview("Nama Suami            : ${data.pasanganSuamiNama}")
                TextPreview("Nama Istri               : ${data.pasanganIstriNama}")
                TextPreview("Tanggal Peristiwa   : ${data.tanggalPeristiwa}")
                TextPreview("Tempat Peristiwa    : ${data.tempatPeristiwa}")
                if (data.pasanganKeterangan.isNotEmpty()) TextPreview("Keterangan            : ${data.pasanganKeterangan}")
            }
            "kematian" -> {
                TextPreview("Data Kematian")
                TextPreview("NIK                          : ${data.meninggalNik}")
                TextPreview("Nama                       : ${data.meninggalNama}")
                TextPreview("Tanggal Kematian    : ${data.meninggalTanggal}")
                TextPreview("Pukul                      : ${data.meninggalPukul}")
                TextPreview("Sebab                     : ${data.meninggalSebab}")
                TextPreview("Tempat                   : ${data.meninggalTempat}")
                TextPreview("Yang Menerangkan  : ${data.meninggalYangMenerangkan}")
            }
            else -> {
                if (data.tanggalPeristiwa.isNotBlank() || data.tempatPeristiwa.isNotBlank() || data.keteranganPeristiwa.isNotBlank()) {
                    TextPreview("Keterangan Peristiwa")
                    if (data.tanggalPeristiwa.isNotBlank()) TextPreview("Tanggal Peristiwa : ${data.tanggalPeristiwa}")
                    if (data.tempatPeristiwa.isNotBlank()) TextPreview("Tempat Peristiwa : ${data.tempatPeristiwa}")
                    if (data.keteranganPeristiwa.isNotBlank()) TextPreview("Keterangan : ${data.keteranganPeristiwa}")
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_FORM)
        PreviewSignatureColumnsSub(
            header, date, "Subang",
            listOf(
                Triple("Mengetahui, Kepala Desa / Lurah", "", "Pejabat Dukcapil yang Membidangi"),
                Triple("Pelapor", data.pelaporNama, null)
            )
        )
    }
}

@Composable
private fun PreviewF203Content(header: HeaderData, data: F203Data, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya kebenaran data kelahiran sebagai berikut:")
        TextPreview("Nama                       : ${data.namaYangMenyatakan}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Dengan ini menyatakan dengan sesungguhnya bahwa data kelahiran anak berikut ini adalah benar:")
        TextPreview("Nama Anak                      : ${data.namaAnak}")
        TextPreview("NIK Anak                        : ${data.nikAnak}")
        if (data.tempatLahirAnak.isNotEmpty() || data.tanggalLahirAnak.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir Anak : ${ttl(data.tempatLahirAnak, data.tanggalLahirAnak)}")
        if (data.jenisKelaminAnak.isNotEmpty()) TextPreview("Jenis Kelamin Anak         : ${data.jenisKelaminAnak}")
        if (data.anakKe.isNotEmpty()) TextPreview("Anak Ke                       : ${data.anakKe}")
        if (data.alamatAnak.isNotEmpty()) TextPreview("Alamat Anak                  : ${data.alamatAnak}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Data Ibu")
        TextPreview("Nama Ibu                 : ${data.namaIbu}")
        TextPreview("NIK Ibu                   : ${data.nikIbu}")
        if (data.tempatLahirIbu.isNotEmpty() || data.tanggalLahirIbu.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir Ibu : ${ttl(data.tempatLahirIbu, data.tanggalLahirIbu)}")
        if (data.pekerjaanIbu.isNotEmpty()) TextPreview("Pekerjaan Ibu         : ${data.pekerjaanIbu}")
        TextPreview("Alamat Ibu              : ${data.alamatIbu}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Penolong Kelahiran")
        TextPreview("Nama Penolong         : ${data.namaPenolong}")
        TextPreview("NIK Penolong           : ${data.nikPenolong}")
        if (data.pekerjaanPenolong.isNotEmpty()) TextPreview("Pekerjaan Penolong : ${data.pekerjaanPenolong}")
        if (data.alamatPenolong.isNotEmpty()) TextPreview("Alamat Penolong     : ${data.alamatPenolong}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Saksi-saksi")
        TextPreview("Nama Saksi 1 : ${data.namaSaksi1}")
        TextPreview("NIK Saksi 1   : ${data.nikSaksi1}")
        TextPreview("Nama Saksi 2 : ${data.namaSaksi2}")
        TextPreview("NIK Saksi 2   : ${data.nikSaksi2}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_F203)
        PreviewSignatureColumnsSub(
            header, date, "Subang",
            listOf(
                Triple("Saksi I", data.namaSaksi1, "NIK. ${data.nikSaksi1}"),
                Triple("Saya yang Menyatakan", data.namaYangMenyatakan, "NIK. ${data.nik}"),
                Triple("Saksi II", data.namaSaksi2, "NIK. ${data.nikSaksi2}")
            )
        )
    }
}

@Composable
private fun PreviewF204Content(header: HeaderData, data: F204Data, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa kami adalah pasangan suami istri yang sah.")
        TextPreview("Nama                       : ${data.namaYangMenyatakan}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Dengan ini menyatakan dengan sesungguhnya bahwa saya adalah pasangan suami/istri yang sah dari:")
        TextPreview("Nama Pasangan                      : ${data.namaPasangan}")
        TextPreview("NIK Pasangan                        : ${data.nikPasangan}")
        if (data.tempatLahirPasangan.isNotEmpty() || data.tanggalLahirPasangan.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir Pasangan : ${ttl(data.tempatLahirPasangan, data.tanggalLahirPasangan)}")
        if (data.pekerjaanPasangan.isNotEmpty()) TextPreview("Pekerjaan Pasangan             : ${data.pekerjaanPasangan}")
        if (data.alamatPasangan.isNotEmpty()) TextPreview("Alamat Pasangan                 : ${data.alamatPasangan}")
        if (data.noKK.isNotEmpty()) TextPreview("No. KK                                  : ${data.noKK}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Saksi-saksi")
        TextPreview("Nama Saksi 1 : ${data.namaSaksi1}")
        TextPreview("NIK Saksi 1   : ${data.nikSaksi1}")
        TextPreview("Nama Saksi 2 : ${data.namaSaksi2}")
        TextPreview("NIK Saksi 2   : ${data.nikSaksi2}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_F204)
        TextPreview("*) Coret yang tidak perlu.")
        PreviewSignatureColumnsSub(
            header, date, "Subang",
            listOf(
                Triple("Saksi I", data.namaSaksi1, "NIK. ${data.nikSaksi1}"),
                Triple("Saya yang Menyatakan", data.namaYangMenyatakan, "NIK. ${data.nik}"),
                Triple("Saksi II", data.namaSaksi2, "NIK. ${data.nikSaksi2}")
            )
        )
    }
}

@Composable
private fun PreviewSptjmKematianContent(header: HeaderData, data: SptjmKematianData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya kebenaran data kematian sebagai berikut:")
        TextPreview("Nama                       : ${data.namaYangMenyatakan}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Dengan ini menyatakan dengan sesungguhnya bahwa data kematian berikut ini adalah benar:")
        TextPreview("Nama Almarhum/almarhumah : ${data.namaAlmarhum}")
        TextPreview("NIK Almarhum/almarhumah   : ${data.nikAlmarhum}")
        if (data.tempatLahirAlmarhum.isNotEmpty() || data.tanggalLahirAlmarhum.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahirAlmarhum, data.tanggalLahirAlmarhum)}")
        TextPreview("Alamat Terakhir             : ${data.alamatAlmarhum}")
        TextPreview("Hari Meninggal             : ${data.hariMeninggal}")
        TextPreview("Tanggal Meninggal        : ${data.tanggalMeninggal}")
        if (data.jamMeninggal.isNotEmpty()) TextPreview("Jam Meninggal             : ${data.jamMeninggal}")
        TextPreview("Tempat Meninggal         : ${data.tempatMeninggal}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Saksi-saksi")
        TextPreview("Nama Saksi 1 : ${data.namaSaksi1}")
        TextPreview("NIK Saksi 1   : ${data.nikSaksi1}")
        TextPreview("Nama Saksi 2 : ${data.namaSaksi2}")
        TextPreview("NIK Saksi 2   : ${data.nikSaksi2}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_SPTJM)
        val desaTitleCase = titleCase(header.namaDesa)
        PreviewSignatureColumns(header, date, desaTitleCase, listOf("Saksi 1" to data.namaSaksi1, "Yang Menyatakan" to data.namaYangMenyatakan, "Saksi 2" to data.namaSaksi2))
        PreviewSignatureColumns(header, date, desaTitleCase, listOf("Ketua RT" to "", "Ketua RW" to "", "Kepala Desa/Lurah" to ""), showDate = false)
    }
}

@Composable
private fun PreviewSptjmPerkawinanPerceraianContent(header: HeaderData, data: SptjmPerkawinanPerceraianData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        val peristiwa = data.jenisSPTJM.lowercase()
        TextPreview("Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa $peristiwa kami belum tercatat.")
        TextPreview("Nama Suami            : ${data.namaSuami}")
        TextPreview("NIK Suami              : ${data.nikSuami}")
        TextPreview("Nama Istri               : ${data.namaIstri}")
        TextPreview("NIK Istri                 : ${data.nikIstri}")
        TextPreview("Tanggal $peristiwa : ${data.tanggalPeristiwa}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Saksi-saksi")
        TextPreview("Nama Saksi 1 : ${data.namaSaksi1}")
        TextPreview("NIK Saksi 1   : ${data.nikSaksi1}")
        TextPreview("Nama Saksi 2 : ${data.namaSaksi2}")
        TextPreview("NIK Saksi 2   : ${data.nikSaksi2}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Daftar Anak")
        data.daftarAnak.forEachIndexed { i, a ->
            if (a.nama.isNotBlank()) {
                TextPreview("${i + 1}. ${a.nama} — No. Akta Kelahiran: ${a.noAktaKelahiran} — SHDK: ${a.shdk}")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_SPTJM_PERKAWINAN)
        val desaTitleCase = titleCase(header.namaDesa)
        PreviewSignatureColumnsSub(
            header, date, desaTitleCase,
            listOf(
                Triple("PIHAK KEDUA (Istri)", data.namaIstri, "NIK. ${data.nikIstri}"),
                Triple("PIHAK PERTAMA (Suami)", data.namaSuami, "NIK. ${data.nikSuami}"),
                Triple("SAKSI II", data.namaSaksi2, "NIK. ${data.nikSaksi2}"),
                Triple("SAKSI I", data.namaSaksi1, "NIK. ${data.nikSaksi1}")
            )
        )
    }
}

@Composable
private fun PreviewPbiAsesmenContent(header: HeaderData, data: PbiAsesmenData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // FASE 5 — baris TAHUN di bawah judul + intro (web) + penutup CLOSING_FORM + a.n. Sekdes (ke-7).
        val tahun = tahunDariTanggal(effectiveTanggalSurat(data.tanggalSurat))
        Text(
            text = "TAHUN $tahun",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Instrumen asesmen calon penerima bansos KIS-PBI / SKM. Identitas dan kondisi umum calon penerima sebagai berikut:")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Identitas Calon Penerima")
        TextPreview("Nama                       : ${data.namaLengkap}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        if (data.jenisKelamin.isNotEmpty()) TextPreview("Jenis Kelamin         : ${data.jenisKelamin}")
        if (data.statusKawin.isNotEmpty()) TextPreview("Status Kawin          : ${data.statusKawin}")
        TextPreview("NIK                          : ${data.nik}")
        TextPreview("No. KK                      : ${data.noKK}")
        TextPreview("Nama Ibu Kandung    : ${data.namaIbuKandung}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Pertanyaan Kondisi")
        TextPreview("1. Jaminan kesehatan yang dimiliki : ${data.jaminanKesehatan}")
        TextPreview("2. Alasan jaminan kesehatan non-aktif : ${data.alasanNonAktif}")
        TextPreview("3. Status rumah : ${data.statusRumah}")
        TextPreview("4. Kondisi rumah : ${data.kondisiRumah}")
        TextPreview("5. Ukuran rumah : ${data.ukuranRumah}")
        TextPreview("6. Dinding rumah : ${data.dindingRumah}")
        TextPreview("7. Penerangan rumah : ${data.peneranganRumah}")
        TextPreview("8. Jumlah keluarga dalam satu rumah : ${data.jumlahKeluarga}")
        TextPreview("9. Jumlah anggota keluarga : ${data.jumlahAnggota}")
        TextPreview("10. Bansos yang diterima : ${data.bansosDiterima}")
        TextPreview("11. Usaha / penghasilan : ${data.usahaPenghasilan}")
        if (data.catatanPetugas.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview("Catatan petugas: ${data.catatanPetugas}")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_FORM)
        val desaTitleCase = titleCase(header.namaDesa)
        PreviewSignatureColumns(header, date, desaTitleCase, listOf("Pemohon" to data.namaLengkap, "Fasilitator" to "", "Sekretaris Desa" to header.namaSekdes))
        PreviewSignatureColumns(header, date, desaTitleCase, listOf("Ketua RW" to "", "Ketua RT" to "", "Camat Kasomalang" to ""), showDate = false)
        PreviewSignatureColumns(header, date, desaTitleCase, listOf("a.n. Kepala Desa $desaTitleCase\nSekretaris Desa" to header.namaSekdes), showDate = false)
    }
}

@Composable
private fun PreviewPbiPernyataanContent(header: HeaderData, data: PbiPernyataanData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini:")
        TextPreview("No. KK                      : ${data.noKK}")
        TextPreview("NIK                          : ${data.nik}")
        TextPreview("Nama                       : ${data.namaLengkap}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        if (data.jenisKelamin.isNotEmpty()) TextPreview("Jenis Kelamin         : ${data.jenisKelamin}")
        if (data.wargaNegara.isNotEmpty()) TextPreview("Warga Negara         : ${data.wargaNegara}")
        if (data.statusPerkawinan.isNotEmpty()) TextPreview("Status Kawin          : ${data.statusPerkawinan}")
        if (data.agama.isNotEmpty()) TextPreview("Agama                    : ${data.agama}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("1. Saya benar-benar termasuk Keluarga Miskin;")
        TextPreview("2. Apabila pernyataan saya ini tidak sesuai dengan kenyataan yang sebenarnya, saya siap mempertanggung jawabkan dihadapan hukum sesuai dengan ketentuan yang berlaku.")
        if (data.isiPernyataan.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview(data.isiPernyataan)
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_PBI_PERNYATAAN)
        val desaTitleCase = titleCase(header.namaDesa)
        PreviewSignatureColumnsSub(
            header, date, desaTitleCase,
            listOf(
                Triple("Yang Bersangkutan", data.namaLengkap, "Materai Rp10.000"),
                Triple("Saksi Ketua RW", "", null)
            )
        )
        PreviewSignatureColumns(header, date, desaTitleCase, listOf("Saksi Ketua RT" to "", "Mengetahui, Camat Kasomalang" to ""), showDate = false)
        PreviewSignatureColumns(header, date, desaTitleCase, listOf("a.n. Kepala Desa $desaTitleCase\nSekretaris Desa" to header.namaSekdes), showDate = false)
    }
}

@Composable
private fun PreviewPbiTidakPunyaRekeningContent(header: HeaderData, data: PbiTidakPunyaRekeningData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa saya tidak memiliki rekening bank.")
        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_SPTJM)
        val desaTitleCase = titleCase(header.namaDesa)
        PreviewSignatureColumns(header, date, desaTitleCase, listOf("Yang Menyatakan" to data.namaLengkap))
    }
}

/* ==================================================================
   FASE 5 — 3 konten preview baru (§2.5).
   ================================================================== */

@Composable
private fun PreviewSktmPbiContent(header: HeaderData, data: SktmPbiData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini Kepala Desa ${titleCase(header.namaDesa)} Kecamatan ${titleCase(header.namaKecamatan.removePrefix("KECAMATAN "))} Kabupaten ${titleCase(header.namaKabupaten.removePrefix("KABUPATEN "))}, menerangkan dengan sesungguhnya bahwa :")
        TextPreview("No. KK                      : ${data.noKK}")
        TextPreview("NIK                          : ${data.nik}")
        TextPreview("Nama                       : ${data.namaLengkap}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        if (data.jenisKelamin.isNotEmpty()) TextPreview("Jenis Kelamin         : ${data.jenisKelamin}")
        if (data.wargaNegara.isNotEmpty()) TextPreview("Warga Negara         : ${data.wargaNegara}")
        if (data.statusPerkawinan.isNotEmpty()) TextPreview("Status Kawin          : ${data.statusPerkawinan}")
        if (data.agama.isNotEmpty()) TextPreview("Agama                    : ${data.agama}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        val anak = data.daftarAnak.filter { it.nama.isNotBlank() || it.nik.isNotBlank() }
        if (anak.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextPreview("Daftar Anak")
            anak.forEachIndexed { i, a ->
                TextPreview("${i + 1}. ${a.nama} — No. KK: ${a.noKK} — NIK: ${a.nik} — TTL: ${a.ttl} — JK: ${a.jenisKelamin} — WN: ${a.wargaNegara} — Status: ${a.statusPerkawinan} — Agama: ${a.agama} — Pekerjaan: ${a.pekerjaan} — Alamat: ${a.alamat}")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (data.tujuan.isNotBlank()) TextPreview("Surat keterangan ini dibuat untuk keperluan ${kalimatTujuanSktmPbi(data.tujuan)}.")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_SKTM_PBI)
        val desaTitleCase = titleCase(header.namaDesa)
        PreviewSignatureColumns(header, date, desaTitleCase, listOf("Ketua RW" to "", "Ketua RT" to ""))
        PreviewSignatureColumnsSub(
            header, date, desaTitleCase,
            listOf(
                Triple("Mengetahui, Camat Kasomalang", "", null),
                Triple("a.n. Kepala Desa $desaTitleCase\nSekretaris Desa", header.namaSekdes, null)
            ),
            showDate = false
        )
    }
}

@Composable
private fun PreviewPbiRekeningListrikContent(header: HeaderData, data: PbiRekeningListrikData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini Kepala Desa ${titleCase(header.namaDesa)} Kecamatan ${titleCase(header.namaKecamatan.removePrefix("KECAMATAN "))} Kabupaten ${titleCase(header.namaKabupaten.removePrefix("KABUPATEN "))}, menerangkan dengan sesungguhnya bahwa :")
        TextPreview("No. KK                      : ${data.noKK}")
        TextPreview("NIK                          : ${data.nik}")
        TextPreview("Nama                       : ${data.namaLengkap}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        if (data.jenisKelamin.isNotEmpty()) TextPreview("Jenis Kelamin         : ${data.jenisKelamin}")
        if (data.wargaNegara.isNotEmpty()) TextPreview("Warga Negara         : ${data.wargaNegara}")
        if (data.statusPerkawinan.isNotEmpty()) TextPreview("Status Kawin          : ${data.statusPerkawinan}")
        if (data.agama.isNotEmpty()) TextPreview("Agama                    : ${data.agama}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat                    : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Menurut pengakuannya belum mempunyai rekening listrik dan menumpang aliran listrik ke ${data.namaTetangga} dengan nomor pelanggan ${data.nomorPelanggan}.")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_SKTM_PBI)
        val desaTitleCase = titleCase(header.namaDesa)
        PreviewSignatureColumns(header, date, desaTitleCase, listOf("Ketua RW" to "", "Ketua RT" to ""))
        PreviewSignatureColumnsSub(
            header, date, desaTitleCase,
            listOf(
                Triple("Mengetahui, Camat Kasomalang", "", null),
                Triple("a.n. Kepala Desa $desaTitleCase\nSekretaris Desa", header.namaSekdes, null)
            ),
            showDate = false
        )
    }
}

@Composable
private fun PreviewPernyataanAktaKelahiranContent(header: HeaderData, data: PernyataanAktaKelahiranData, date: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini :")
        TextPreview("NIK                          : ${data.nik}")
        TextPreview("Nama                       : ${data.namaLengkap}")
        if (data.tempatLahir.isNotEmpty() || data.tanggalLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${ttl(data.tempatLahir, data.tanggalLahir)}")
        TextPreview("Nama Ayah               : ${data.namaAyah}")
        TextPreview("Nama Ibu                 : ${data.namaIbu}")
        TextPreview("Alamat                    : ${data.alamat}")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview("Menyatakan dengan sebenarnya bahwa orang tersebut sedang membuat AKTA KELAHIRAN dan sampai saat ini masih menunggu selesai dibuat.")
        Spacer(modifier = Modifier.height(8.dp))
        TextPreview(CLOSING_PERMOHONAN_RUMAH)
        PreviewSignature(header, date, "SEKDES")
    }
}
