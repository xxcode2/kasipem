# Analisis Format Surat DOCX - Studi Kasus 5 Template

## Ringkasan Eksekutif
Analisis 5 file DOCX dari kantor desa mengungkapkan pola umum dengan variasi spesifik sesuai jenis surat. Setiap surat memiliki struktur yang konsisten dengan KOP, judul, nomor, isi, dan tanda tangan.

---

## 1. SURAT KETERANGAN DOMISILI.docx

### Struktur KOP (Header)
```
PEMERINTAH KABUPATEN SUBANG
KECAMATAN KASOMALANG
DESA KASOMALANG KULON
Alamat : Jl.RayaKasomalang  No.01Kec.Kasomalang – Subang 41281
```

### Judul Surat
**SURAT KETERANGAN DOMISILI**

### Format Nomor Surat
- **Nomor: 140/25/Pem / 2026**
- Format: `[URUT]/[KOM-INTERNAL]/[TIPE] / [TAHUN]`
- Tipe: "Pem" = Pemerintah/Keterangan Pemerintah

### Pembuka Surat
- "Yang bertandatangan dibawah ini Kepala Desa [Nama Desa]..."
- Frasa standar: "menerangkan dengan sesungguhnyabahwa :"

### Data Pribadi Subjek
```
- Nik: [16 DIGIT]
- Nama: [STRING]
- Tempat tanggal lahir: [TEMPAT], [TGL-BULAN-TAHUN]
- Pekerjaan: [STRING]
- Alamat: [ALAMAT LENGKAP]
```

### Isi/Body Surat
- Menjelaskan situasi/alasan surat
- Contoh: Status domisili lama vs baru, alamat RT/RW spesifik
- **Format Data Domisili:**
  - Alamat lama terletak di: [DESA] [KECAMATAN] [KABUPATEN]
  - Alamat baru berdomisili di: RT [XX] RW [XX] [DESA] [KECAMATAN] [KABUPATEN]

### Tujuan Penggunaan
- "Surat Keterangan ini kami berikan untuk : [TUJUAN]"
- Contoh: "Identitas"

### Penutup Surat
- "Demikian surat keterangan ini dibuat dengan sebenarnya agar dapat dipergunakan sebagaimana mestinya."

### Format Tanda Tangan
```
[TEMPAT], [TANGGAL LENGKAP - HARI BULAN TAHUN]
       A.n Kepala Desa [NAMA DESA]
Sekretaris Desa

[NAMA PENANDATANGAN, S.IP]
```

**Signatory:** Sekretaris Desa (A.n = Atas Nama Kepala Desa)

---

## 2. SURAT KETERANGAN MISKIN.docx

### Struktur KOP
- **TIDAK ADA KOP VISUAL** - hanya judul langsung
- Tidak ada header alamat desa

### Judul Surat
**SURAT KETERANGAN MISKIN**

### Format Nomor Surat
- **Nomor: 460/ /Ks/2025**
- Format: `[URUT]/ /[TIPE]/[TAHUN]`
- Tipe: "Ks" = Keterangan Sosial

### Pembuka Surat
- "Yang bertandatangan dibawah ini Sekretaris Desa [Nama Desa]..."
- Frasa: "menerangkan dengan sesungguhnya bahwa :"

### Isi/Body Surat - Format Tabel Data
```
Struktur 1: Pernyataan umum
"Orang-orang yang tercantum dalam daftar terlampir adalah benar-benar 
warga Desa kami dan tergolong dalam kategori Keluarga Tidak Mampu."

Struktur 2: Tabel Data Keluarga
| NIK | NAMA | TEMPAT, TGL LAHIR | ALAMAT |
|-----|------|-------------------|--------|
| [16 DIGIT] | [STRING] | [TEMPAT], [DD-MM-YYYY] | RT [XX] RW [XX] |
```

### Data Khusus
- **Daftar multi-kepala keluarga** (bukan satu orang seperti surat lain)
- Minimal 7 baris data keluarga
- RT/RW spesifik untuk setiap individu
- Catatan: Ada duplikasi/typo "Daftar nama terlampir" vs tabel yang ditampilkan

### Tujuan Penggunaan
- "Surat keterangan ini dibuat sebagai salah satu syarat untuk keperluan pengajuan bantuan sosial [PROGRAM]"
- Contoh: "Program Jabar Caang"

### Penutup Surat
- "Demikian surat ini dibuat dengan sebenarnya agar dapat digunakan sebagaimana mestinya."

### Format Tanda Tangan
```
[TEMPAT], [DD-BULAN-YYYY]
  a.n Kepala Desa [NAMA DESA]
  Ketua Rw. [XX]
Sekretaris Desa 
[NAMA PENANDATANGAN, S.IP]
```

**Signatories:** 
- Ketua RW (tambahan signatory)
- Sekretaris Desa

---

## 3. SURAT KETERANGAN NIKAH TIDAK TERCATAT new.docx

### Struktur KOP
- **TIDAK ADA KOP VISUAL**

### Judul Surat
**SURAT KETERANGAN NIKAH TIDAK TERCATAT**

### Format Nomor Surat
- **Nomor: 474 / / Ks / 2025**
- Format: `[URUT] / / [TIPE] / [TAHUN]`
- Tipe: "Ks" = Keterangan Sosial/Sipil

### Pembuka Surat
- "Yang bertanda tangan di bawah ini, Kepala Desa [Nama Desa]..."
- Frasa: "menerangkan prihal perkawinan antara :"

### Isi/Body Surat - Format Khusus Pihak
Struktur data untuk SUAMI:
```
Nama           : [STRING]
Bin            : [NAMA AYAH]
Tempat Tgl Lahir : [TEMPAT], [DD-MM-YYYY]
Agama          : [PILIHAN]
Pekerjaan      : [STRING]
Alamat         : [ALAMAT LENGKAP]
```

Struktur data untuk ISTRI (sama format):
```
Dengan seorang Wanita:
Nama           : [STRING]
Bin            : [NAMA AYAH]
Tempat Tgl Lahir : [TEMPAT], [DD-MM-YYYY]
Agama          : [PILIHAN]
Pekerjaan      : [STRING]
Alamat         : [ALAMAT LENGKAP]
```

### Data Khusus
- **Tanggal perkawinan:** "Menurut sepengetahuan kami orang tersebut di atas telah menikah pada tanggal [DD-MM-YYYY]"
- **Waliy/Wali:** Nama bapak wali (sering kosong/placeholder)
- **Saksi:** 2 orang, nama sering kosong/placeholder
- **Mas Kawin:** Jenis barang (contoh: "alat sholat")
- **Status Perkawinan:** "duda dan janda" atau status lainnya

### Penutup Surat
- "Demikian surat keterangan ini di buat dengan sebenarnya dan agar bisa di pergunakan sebagaimana mestinya."

### Format Tanda Tangan
```
[TEMPAT]. [DD BULAN YYYY]
a.n Kepala Desa [NAMA DESA]
Sekretaris Desa

[NAMA PENANDATANGAN], S.IP
```

**Signatory:** Sekretaris Desa (A.n)

---

## 4. SURAT KETERANGAN BEDA LUAS REAL.docx

### Struktur KOP (Header)
```
PEMERINTAH DAERAH KABUPATEN SUBANG
KECAMATAN KASOMALANG
DESA KASOMALANG KULON
Alamat: Jln. Raya Kasomalang No.01Subang 41281
```

### Judul Surat
**SURAT KETERANGAN**
(Tidak spesifik, judul singkat)

### Format Nomor Surat
- **No: 141 / / PEM / 2025**
- Format: `[URUT] / / [TIPE] / [TAHUN]`
- Tipe: "PEM" = Pemerintah

### Pembuka Surat
- "Yang bertandatangan dibawah ini, Sekretaris Desa [Nama Desa]..."
- Frasa: "dengan ini menerangkan bahwa:"

### Isi/Body Surat - Format Perbandingan Data
```
STRUKTUR 1: Luas menurut Sertifikat
Nomor         : [NOMOR SERTIFIKAT]
Nama Pemilik  : [NAMA]
Luas          : [NUMBER] M2
Tempat, Tanggal lahir: [TEMPAT], [DD-MM-YYYY]
Pekerjaan     : [STRING]
Alamat        : Rt. [XX] Rw. [XX] [DESA] [KECAMATAN] [KABUPATEN]

STRUKTUR 2: Luas menurut SPPT (Pajak)
Nomor         : [NOMOR SPPT]
Luas          : [NUMBER] M2
Nama Pemilik  : [NAMA]
Alamat        : Rt. [XX] Rw. [XX] [DESA] [KECAMATAN] [KABUPATEN]

PERNYATAAN KETERANGAN:
- Orang tersebut adalah warga Desa [NAMA]
- Ada perbedaan luas antara [DOKUMEN 1] dan [DOKUMEN 2]
- Pemilik tanah sama (identitas sama)
```

### Tujuan Penggunaan
- Tidak eksplisit dicantumkan
- Implisit: Keperluan administratif/perpajakan

### Penutup Surat
- "Demikian surat keterangan ini kami buat dengan sebenarnya agar yang berkepentingan menjadi tahu serta untuk dipergunakan sebagaimana mestinya."

### Format Tanda Tangan
```
            [TEMPAT], [DD BULAN YYYY]
Sekretaris Desa [NAMA DESA]

[NAMA PENANDATANGAN], S.IP
```

**Signatory:** Sekretaris Desa

---

## 5. SURAT UNDANGAN YG DI TTD KEPALA DESA.docx

### Struktur KOP
- **MINIMAL** - hanya tanggal lokasi di header
- Format: "[TEMPAT], [DD BULAN YYYY]"

### Format Header Administratif
```
Nomor: .................
Sifat: .................
Lampiran: ................
Hal: Undangan.
```

### Penerima Surat
```
Yth. ............................
......................................
di
.........................................
```
(Field template kosong yang dapat diisi)

### Isi/Body Surat - Format Tabel
```
Hari/tanggal: [TEMPLATE KOSONG]
Waktu: [TEMPLATE KOSONG]
Tempat: [TEMPLATE KOSONG]
Acara: [JENIS ACARA]
```

### Data Khusus
- **Acara Template:** "Musyawarah pencermatan RKPDesa Tahun [TAHUN]"
- Banyak field yang kosong/template untuk diisi
- Surat ini adalah **template form**, bukan final copy

### Penutup Surat
- "Demikian kami sampaikan, atas perkenan dan perhatiannya diucapkan terimakasih."

### Format Tanda Tangan
```
[SIGNATORY TITLE]
[NAMA PENANDATANGAN, GELAR]
```

**Signatory:** KEPALA DESA KASOMALANG KULON

### Tembusan (Distribusi)
- Camat Kasomalang
- Ketua BPD Desa [NAMA DESA]
- Arsip

---

## ANALISIS KOMPARATIF

### Pola Umum yang Konsisten
| Elemen | Kehadiran | Format |
|--------|-----------|--------|
| KOP (Header) | 3/5 (60%) | Pemerintah → Kecamatan → Desa → Alamat |
| Judul Surat | 5/5 (100%) | CAPS, terpusat |
| Nomor Surat | 5/5 (100%) | `[URUT]/[KOM]/[TIPE]/[TAHUN]` |
| Pembuka | 5/5 (100%) | "Yang bertandatangan dibawah ini..." |
| NIK/Identitas | 2/5 (40%) | 16 digit, hanya surat keterangan personal |
| Data Pribadi | 5/5 (100%) | Nama, TGL Lahir, Pekerjaan, Alamat (variasi) |
| Tujuan Penggunaan | 3/5 (60%) | Explisit dicantumkan |
| Penutup Standar | 5/5 (100%) | "Demikian...agar dapat dipergunakan..." |
| Tanda Tangan | 5/5 (100%) | Sekretaris Desa (sebagian A.n Kepala Desa) |

### Tipe Surat Berdasarkan Format Nomor
| Kode Tipe | Singkatan | Contoh Surat |
|-----------|-----------|--------------|
| PEM | Pemerintah | Domisili, Beda Luas |
| Ks | Keterangan Sosial | Miskin, Nikah Tidak Tercatat |

### Pola Tanda Tangan
1. **A.n Kepala Desa** (Atas Nama): Digunakan oleh Sekretaris Desa
2. **Langsung dari Kepala Desa**: Surat Undangan
3. **Dengan Co-Signatory**: Surat Miskin (ditanda tangani Ketua RW juga)

### Format Nomor Surat Analisis Lebih Dalam
```
[URUT] / [KOMUNITERING/INTERNAL] / [TIPE] / [TAHUN]

Contoh:
- 140/25/Pem/2026   → Nomor urut 140, komunitering 25, tipe Pemerintah, tahun 2026
- 460//Ks/2025      → Nomor urut 460, komunitering kosong, tipe Keterangan Sosial, tahun 2025
- 474//Ks/2025      → Nomor urut 474, komunitering kosong, tipe Keterangan Sosial, tahun 2025
- 141//PEM/2025     → Nomor urut 141, komunitering kosong, tipe Pemerintah, tahun 2025
```

---

## FIELD YANG DAPAT DIKUSTOMISASI PER SURAT

### SURAT KETERANGAN DOMISILI
- [NIK], [NAMA], [TTL], [PEKERJAAN], [ALAMAT LAMA]
- [ALAMAT BARU - RT/RW], [DESA], [KECAMATAN], [KABUPATEN]
- [TUJUAN PENGGUNAAN]
- [TEMPAT, TANGGAL], [NAMA PENANDATANGAN]

### SURAT KETERANGAN MISKIN
- [JUMLAH DATA]: Array of {NIK, NAMA, TTL, ALAMAT}
- [NAMA PROGRAM BANTUAN]
- [KETUA RW], [NAMA PENANDATANGAN]

### SURAT KETERANGAN NIKAH TIDAK TERCATAT
- SUAMI: [NAMA, NAMA_AYAH, TTL, AGAMA, PEKERJAAN, ALAMAT]
- ISTRI: [NAMA, NAMA_AYAH, TTL, AGAMA, PEKERJAAN, ALAMAT]
- [TANGGAL_PERKAWINAN], [NAMA_WALI], [NAMA_SAKSI_1], [NAMA_SAKSI_2]
- [MAS_KAWIN], [STATUS_PERKAWINAN]
- [TEMPAT, TANGGAL], [NAMA PENANDATANGAN]

### SURAT KETERANGAN BEDA LUAS REAL
- SERTIFIKAT: [NOMOR_SERTIFIKAT], [LUAS_SERTIFIKAT], [PEMILIK_DATA]
- SPPT: [NOMOR_SPPT], [LUAS_SPPT]
- [TEMPAT, TANGGAL], [NAMA PENANDATANGAN]

### SURAT UNDANGAN
- [NOMOR], [SIFAT], [LAMPIRAN]
- [PENERIMA_NAMA, PENERIMA_ALAMAT]
- [HARI/TANGGAL], [WAKTU], [TEMPAT], [ACARA]
- [NAMA_KEPALA_DESA]

---

## REKOMENDASI IMPLEMENTASI

### Untuk PDF Generation
1. **Gunakan template DOCX sebagai base** - ekstrak struktur dari file XML
2. **Pola penomoran fleksibel** - support format `N/M/Type/Year`
3. **Field substitution** - mapping data input ke placeholder di template
4. **Dynamic signatures** - dukungan untuk berbagai signatory
5. **Table support** - terutama untuk surat miskin (multiple rows)

### Struktur Data Minimal
```json
{
  "letterType": "keterangan_domisili|keterangan_miskin|nikah_tidak_tercatat|beda_luas|undangan",
  "letterNumber": "140/25/Pem/2026",
  "date": "06 Juli 2026",
  "signatory": {
    "name": "APIPUDIN",
    "title": "S.IP",
    "position": "Sekretaris Desa",
    "role": "atas_nama|langsung"
  },
  "content": { /* variant per tipe surat */ }
}
```

---

## KESIMPULAN

Kelima surat menunjukkan pola administratif yang terstandar dengan:
- **Konsistensi tinggi** dalam struktur dasar (KOP, nomor, pembuka, penutup)
- **Variabilitas sedang** dalam isi (content specific per jenis surat)
- **Fleksibilitas** dalam field pengisian (template-style)
- **Multiple signatories** untuk beberapa jenis (Ketua RW, Sekretaris Desa)

Implementasi PDF generator harus mengakomodasi pola umum sambil memberikan kemampuan customisasi per jenis surat.
