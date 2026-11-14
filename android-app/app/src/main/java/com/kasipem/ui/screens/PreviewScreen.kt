package com.kasipem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kasipem.data.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PreviewScreen(
    headerData: HeaderData,
    letterData: Any,
    letterType: LetterType,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        // Top Bar
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
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        )

        // Preview Content
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

        // Action Buttons
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
                onClick = { /* Print action */ },
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
                onClick = { /* Download PDF */ },
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

@Composable
private fun PreviewContent(
    headerData: HeaderData,
    letterData: Any,
    letterType: LetterType
) {
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    val currentDate = dateFormat.format(Date())

    // Header
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(4.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "PEMERINTAH ${headerData.namaKabupaten.uppercase()}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            headerData.namaKecamatan.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            "DESA/KELURAHAN ${headerData.namaDesa.uppercase()}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            headerData.alamatKantor,
            fontSize = 9.sp
        )
    }

    // Title
    Text(
        text = getLetterTitle(letterType),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.align(Alignment.CenterHorizontally)
    )

    Divider(modifier = Modifier.padding(vertical = 4.dp))

    // Classification
    Text(
        "Nomor : ${getClassification(letterType)}",
        fontSize = 9.sp,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Content based on letter type
    when (letterType) {
        LetterType.DOMISILI -> {
            val data = letterData as DomisiliData
            PreviewDomisiliContent(headerData, data, currentDate)
        }

        LetterType.BELUM_MENIKAH -> {
            val data = letterData as BelumMenikahData
            PreviewBelumMenikahContent(headerData, data, currentDate)
        }

        LetterType.TIDAK_MAMPU -> {
            val data = letterData as TidakMampuData
            PreviewTidakMampuContent(headerData, data, currentDate)
        }

        LetterType.BEDA_LUAS -> {
            val data = letterData as BedaLuasData
            PreviewBedaLuasContent(headerData, data, currentDate)
        }

        LetterType.UNDANGAN -> {
            val data = letterData as UndanganData
            PreviewUndanganContent(headerData, data, currentDate)
        }
    }
}

@Composable
private fun PreviewDomisiliContent(
    header: HeaderData,
    data: DomisiliData,
    date: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini adalah Kepala Desa/Lurah ${header.namaDesa}, menerangkan bahwa:")

        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        if (data.jenisKelamin.isNotEmpty()) TextPreview("Jenis Kelamin         : ${data.jenisKelamin}")
        if (data.tempatLahir.isNotEmpty()) TextPreview("Tempat/Tanggal Lahir : ${data.tempatLahir}, ${data.tanggalLahir}")
        if (data.pekerjaan.isNotEmpty()) TextPreview("Pekerjaan                : ${data.pekerjaan}")
        TextPreview("Alamat (KTP)            : ${data.alamatAsal}")
        TextPreview("RT/RW                    : ${data.rt}/${data.rw}")
        TextPreview("Alamat Domisili Saat Ini : ${data.alamatDomisili}")

        Spacer(modifier = Modifier.height(8.dp))

        TextPreview("Adalah benar bahwa orang tersebut di atas adalah penduduk sah Desa/Kelurahan ${header.namaDesa} dan bertempat tinggal di alamat tersebut di atas. Surat keterangan ini diberikan untuk keperluan ${data.keperluanSurat}.")

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                TextPreview("Diterbitkan di : ${header.namaDesa}")
                TextPreview("Tanggal           : $date")
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                TextPreview("${if (header.atnPejabat) "A.n. " else ""}${header.jabatanPejabat}")
                Spacer(modifier = Modifier.height(24.dp))
                TextPreview(header.namaPejabat)
                TextPreview("NIP: ${header.nipPejabat}")
            }
        }
    }
}

@Composable
private fun PreviewBelumMenikahContent(
    header: HeaderData,
    data: BelumMenikahData,
    date: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini adalah Kepala Desa/Lurah ${header.namaDesa}, menerangkan bahwa:")

        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        TextPreview("Alamat                    : ${data.alamatLengkap}")
        TextPreview("RT/RW                    : ${data.rt}/${data.rw}")

        Spacer(modifier = Modifier.height(8.dp))

        TextPreview("Adalah benar bahwa yang bersangkutan sampai saat ini belum pernah melakukan perkawinan yang tercatat. Surat keterangan ini diberikan untuk keperluan ${data.keperluan}.")

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                TextPreview("Diterbitkan di : ${header.namaDesa}")
                TextPreview("Tanggal           : $date")
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                TextPreview("${if (header.atnPejabat) "A.n. " else ""}${header.jabatanPejabat}")
                Spacer(modifier = Modifier.height(24.dp))
                TextPreview(header.namaPejabat)
                TextPreview("NIP: ${header.nipPejabat}")
            }
        }
    }
}

@Composable
private fun PreviewTidakMampuContent(
    header: HeaderData,
    data: TidakMampuData,
    date: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini adalah Kepala Desa/Lurah ${header.namaDesa}, menerangkan bahwa:")

        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        TextPreview("Alamat                    : ${data.alamatLengkap}")
        TextPreview("RT/RW                    : ${data.rt}/${data.rw}")

        Spacer(modifier = Modifier.height(8.dp))

        TextPreview("Adalah benar bahwa yang bersangkutan termasuk dalam kategori tidak mampu. Surat keterangan ini diberikan untuk keperluan ${data.keperluan} di ${data.tujuanSekolah}.")

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                TextPreview("Diterbitkan di : ${header.namaDesa}")
                TextPreview("Tanggal           : $date")
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                TextPreview("${if (header.atnPejabat) "A.n. " else ""}${header.jabatanPejabat}")
                Spacer(modifier = Modifier.height(24.dp))
                TextPreview(header.namaPejabat)
                TextPreview("NIP: ${header.nipPejabat}")
            }
        }
    }
}

@Composable
private fun PreviewBedaLuasContent(
    header: HeaderData,
    data: BedaLuasData,
    date: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Yang bertanda tangan di bawah ini adalah Kepala Desa/Lurah ${header.namaDesa}, menerangkan bahwa:")

        TextPreview("Nama                       : ${data.namaLengkap}")
        TextPreview("NIK                          : ${data.nik}")
        TextPreview("Nomor Sertifikat          : ${data.nomorSertifikat}")
        TextPreview("Luas Sertifikat           : ${data.luasSertifikat} m²")
        TextPreview("Nomor SPPT                : ${data.nomorSPPT}")
        TextPreview("Luas SPPT                 : ${data.luasSPPT} m²")

        Spacer(modifier = Modifier.height(8.dp))

        TextPreview("Adalah benar bahwa terdapat perbedaan luas tanah. Surat keterangan ini diberikan untuk keperluan administrasi pertanahan.")

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                TextPreview("Diterbitkan di : ${header.namaDesa}")
                TextPreview("Tanggal           : $date")
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                TextPreview("${if (header.atnPejabat) "A.n. " else ""}${header.jabatanPejabat}")
                Spacer(modifier = Modifier.height(24.dp))
                TextPreview(header.namaPejabat)
                TextPreview("NIP: ${header.nipPejabat}")
            }
        }
    }
}

@Composable
private fun PreviewUndanganContent(
    header: HeaderData,
    data: UndanganData,
    date: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        TextPreview("Sifat: Penting")
        TextPreview("Lampiran: -")
        TextPreview("Perihal: Undangan")

        Spacer(modifier = Modifier.height(8.dp))

        TextPreview("Kepada Yth.")
        TextPreview(data.tujuanYth)

        Spacer(modifier = Modifier.height(8.dp))

        TextPreview("Dengan hormat, kami mengundang Bapak/Ibu untuk hadir dalam acara berikut:")

        TextPreview("Hari/Tanggal   : ${data.hari}, ${data.tanggal}")
        TextPreview("Pukul           : ${data.pukul} WIB")
        TextPreview("Tempat          : ${data.tempat}")
        TextPreview("Agenda          : ${data.agendaAcara}")

        Spacer(modifier = Modifier.height(8.dp))

        TextPreview("Atas perhatian dan kehadiran Bapak/Ibu diucapkan terima kasih.")

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.weight(1f))
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                TextPreview("Diterbitkan di : ${header.namaDesa}")
                TextPreview("Tanggal           : $date")
                Spacer(modifier = Modifier.height(24.dp))
                TextPreview("${if (header.atnPejabat) "A.n. " else ""}${header.jabatanPejabat}")
                Spacer(modifier = Modifier.height(24.dp))
                TextPreview(header.namaPejabat)
                TextPreview("NIP: ${header.nipPejabat}")
            }
        }
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

private fun getLetterTitle(type: LetterType): String = when (type) {
    LetterType.DOMISILI -> "SURAT KETERANGAN DOMISILI"
    LetterType.BELUM_MENIKAH -> "SURAT KETERANGAN BELUM MENIKAH"
    LetterType.TIDAK_MAMPU -> "SURAT KETERANGAN TIDAK MAMPU"
    LetterType.BEDA_LUAS -> "SURAT KETERANGAN BEDA LUAS TANAH"
    LetterType.UNDANGAN -> "SURAT UNDANGAN RESMI"
}

private fun getClassification(type: LetterType): String = when (type) {
    LetterType.DOMISILI -> "140/.../2024"
    LetterType.BELUM_MENIKAH -> "460.2/.../2024"
    LetterType.TIDAK_MAMPU -> "460/.../2024"
    LetterType.BEDA_LUAS -> "141/.../2024"
    LetterType.UNDANGAN -> ".../.../.../2024"
}
