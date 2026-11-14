# Contoh Data Real dari File DOCX

Dokumen ini berisi ekstraksi data real dari 5 file DOCX yang dianalisis, untuk referensi implementation.

---

## 1. SURAT KETERANGAN DOMISILI - Data Real

### Input Data (JSON)
```json
{
  "letterType": "keterangan_domisili",
  "year": 2026,
  "counterType": "Pem",
  "officeCode": 25,
  "subject": {
    "nik": "3213261205940008",
    "nama": "SAEPUROHMAN",
    "tempat_lahir": "Subang",
    "tanggal_lahir": "1994-05-12",
    "pekerjaan": "Belum/Tidak Bekerja",
    "alamat_lama": "Perum Green Mutiara 2 Rt 005 Rw 017 Desa Dawuan Timur Kecamatan Cikampek"
  },
  "domisili_baru": {
    "rt_rw": "RT 001 RW 006",
    "desa": "Kasomalang Kulon",
    "kecamatan": "Kasomalang",
    "kabupaten": "Subang"
  },
  "tujuan": "Identitas",
  "penandatangan": {
    "nama": "APIPUDIN",
    "gelar": "S.IP",
    "peran": "atas_nama"
  },
  "tanggal_surat": "2026-07-06"
}
```

### Output Text yang Dihasilkan
```
PEMERINTAH KABUPATEN SUBANG
KECAMATAN KASOMALANG
DESA KASOMALANG KULON
Alamat : Jl.RayaKasomalang  No.01Kec.Kasomalang – Subang 41281

SURAT KETERANGAN DOMISILI 
Nomor  : 140/25/Pem / 2026

Yang bertandatangan dibawah ini Kepala Desa Kasomalang Kulon
Kecamatan Kasomalang
Kabupaten Subang, menerangkan dengan sesungguhnya bahwa :

Nik: 3213261205940008
Nama: SAEPUROHMAN
Tempat tanggal lahir: Subang, 12-05-1994
Pekerjaan: Belum/Tidak Bekerja
Alamat: Perum Green Mutiara 2 Rt 005 Rw 017 Desa Dawuan Timur Kecamatan Cikampek

Orang tersebut sudah tidak tinggal di alamat sebagaimana tercantum di atas, 
dan berdasarkan Keterangan dari Ketua RT/RW 001/006 orang tersebut saat ini 
benar berdomisili di RT 001 RW 006 Desa  Kasomalang Kulon Kecamatan Kasomalang  
Kabupaten Subang.

Surat Keterangan ini kami berikan untuk : Identitas

Demikian surat keterangan ini dibuat dengan sebenarnya agar dapat 
dipergunakan sebagaimana mestinya.

Kasomalang Kulon, 06 Juli 2026
       A.n Kepala Desa Kasomalang Kulon
Sekretaris Desa

APIPUDIN, S.IP
```

---

## 2. SURAT KETERANGAN MISKIN - Data Real

### Input Data (JSON)
```json
{
  "letterType": "keterangan_miskin",
  "year": 2025,
  "counterType": "Ks",
  "ketua_rw_nomor": "02",
  "program_bantuan": "Program Jabar Caang",
  "data_keluarga": [
    {
      "nik": "3213025108000001",
      "nama": "DIAN PITRIANI",
      "tempat_lahir": "SUBANG",
      "tanggal_lahir": "2000-08-11",
      "rt": "006",
      "rw": "002"
    },
    {
      "nik": "3213261907870002",
      "nama": "EDIH",
      "tempat_lahir": "SUBANG",
      "tanggal_lahir": "1987-07-19",
      "rt": "006",
      "rw": "002"
    },
    {
      "nik": "3213264102890015",
      "nama": "ANI HERAWATI",
      "tempat_lahir": "SUBANG",
      "tanggal_lahir": "1989-02-01",
      "rt": "006",
      "rw": "002"
    },
    {
      "nik": "3213260702010008",
      "nama": "DEDE PATHURROHMAN",
      "tempat_lahir": "SUBANG",
      "tanggal_lahir": "2001-12-21",
      "rt": "006",
      "rw": "002"
    },
    {
      "nik": "3213264408800006",
      "nama": "HENI ROHAENI",
      "tempat_lahir": "SUBANG",
      "tanggal_lahir": "1980-08-04",
      "rt": "006",
      "rw": "002"
    },
    {
      "nik": "3209271304900005",
      "nama": "ANTONI",
      "tempat_lahir": "SUBANG",
      "tanggal_lahir": "1990-04-13",
      "rt": "003",
      "rw": "002"
    },
    {
      "nik": "3213261402730007",
      "nama": "ENTIS",
      "tempat_lahir": "SUBANG",
      "tanggal_lahir": "1973-02-14",
      "rt": "004",
      "rw": "002"
    }
  ],
  "penandatangan": {
    "nama": "APIPUDIN",
    "gelar": "S.IP"
  },
  "tanggal_surat": "2025-10-02"
}
```

### Output Text yang Dihasilkan
```
SURAT KETERANGAN MISKIN
Nomor   : 460//Ks/2025

Yang bertandatangan dibawah ini Sekretaris Desa Kasomalang Kulon
Kecamatan Kasomalang
Kabupaten Subang, menerangkan dengan sesungguhnya bahwa :

Orang-orang yang tercantum dalam daftar terlampir adalah benar-benar warga  
Desa kami dan tergolong dalam kategori Keluarga Tidak Mampu.

Daftar nama terlampir adalah warga RW 02 Desa Kasomalang Kulon :

┌──────────────────┬──────────────────┬──────────────────┬─────────────────┐
│ NIK              │ NAMA             │ TEMPAT, TGL LAHIR│ ALAMAT          │
├──────────────────┼──────────────────┼──────────────────┼─────────────────┤
│ 3213025108000001 │ DIAN PITRIANI    │ SUBANG, 11-08-00 │ RT 006 RW 002   │
│ 3213261907870002 │ EDIH             │ SUBANG, 19-07-87 │ RT 006 RW 002   │
│ 3213264102890015 │ ANI HERAWATI     │ SUBANG, 01-02-89 │ RT 006 RW 002   │
│ 3213260702010008 │ DEDE PATHURROHMAN│ SUBANG, 21-12-01 │ RT 006 RW 002   │
│ 3213264408800006 │ HENI ROHAENI     │ SUBANG, 04-08-80 │ RT 006 RW 002   │
│ 3209271304900005 │ ANTONI           │ SUBANG, 13-04-90 │ RT 003 RW 002   │
│ 3213261402730007 │ ENTIS            │ SUBANG, 14-02-73 │ RT 004 RW 002   │
└──────────────────┴──────────────────┴──────────────────┴─────────────────┘

Surat keterangan ini dibuat sebagai salah satu syarat untuk keperluan 
pengajuan bantuan sosial Program Jabar Caang

Demikian surat ini dibuat dengan sebenarnya agar dapat digunakan sebagaimana mestinya.

Kasomalang Kulon, 02-Oktober-2025
  a.n Kepala Desa Kasomalang Kulon
  Ketua Rw. 02
Sekretaris Desa 

APIPUDIN, S.IP
```

### Catatan Implementasi
- Memerlukan support untuk array/looping data
- Tabel harus dapat di-render di PDF
- Co-signatory (Ketua RW) harus ditampilkan di signature section
- Tanggal format berbeda: `02-Oktober-2025` (dash dengan bulan penuh)

---

## 3. SURAT KETERANGAN NIKAH TIDAK TERCATAT - Data Real

### Input Data (JSON)
```json
{
  "letterType": "nikah_tidak_tercatat",
  "year": 2025,
  "counterType": "Ks",
  "suami": {
    "nama": "DARTAM",
    "nama_ayah": "SATA",
    "tempat_lahir": "Subang",
    "tanggal_lahir": "1947-05-07",
    "agama": "Islam",
    "pekerjaan": "Petani/kebun",
    "alamat": "Kp.Karihkil RT006/001 Desa Kasomalang Kulon Kecamatan Kasomalang Kabupaten Subang"
  },
  "istri": {
    "nama": "TIRAH",
    "nama_ayah": "ANISEM",
    "tempat_lahir": "Subang",
    "tanggal_lahir": "1952-05-05",
    "agama": "Islam",
    "pekerjaan": "Petani/pekebun",
    "alamat": "Kp.Karihkil RT006/001 Desa Kasomalang Kulon Kecamatan Kasomalang Kabupaten Subang"
  },
  "perkawinan": {
    "tanggal_perkawinan": "1965-10-05",
    "nama_wali": "[PLACEHOLDER - belum diisi]",
    "nama_saksi_1": "[PLACEHOLDER - belum diisi]",
    "nama_saksi_2": "[PLACEHOLDER - belum diisi]",
    "mas_kawin": "alat sholat",
    "status_perkawinan": "duda dan janda"
  },
  "penandatangan": {
    "nama": "APIPUDIN",
    "gelar": "S.IP"
  },
  "tanggal_surat": "2025-11-07"
}
```

### Output Text yang Dihasilkan
```
SURAT KETERANGAN NIKAH TIDAK TERCATAT
Nomor : 474 / / Ks / 2025

Yang bertanda tangan di bawah ini, Kepala Desa Kasomalang Kulon 
Kec. Kasomalang Kab. Subang menerangkan prihal perkawinan antara :

Nama           : DARTAM
Bin            : SATA
Tempat Tgl Lahir : Subang, 07-05-1947
Agama          : Islam
Pekerjaan      : Petani/kebun
Alamat         : Kp.Karihkil RT006/001 Desa Kasomalang Kulon Kecamatan Kasomalang Kabupaten Subang

Dengan seorang Wanita :
Nama           : TIRAH
Bin            : ANISEM
Tempat Tgl Lahir : Subang, 05-05-1952
Agama          : Islam
Pekerjaan      : Petani/pekebun
Alamat         : Kp.Karihkil RT006/001 Desa Kasomalang Kulon Kecamatan Kasomalang Kabupaten Subang

Menurut sepengetahuan kami orang tersebut di atas telah menikah pada tanggal 
05 Oktober 1965 yang di laksanakan oleh walinya Bapak [PLACEHOLDER - belum diisi]
dengan saksi 2 orang, 1. Bapak [PLACEHOLDER - belum diisi] 2. Bapak [PLACEHOLDER - belum diisi]
dengan mas kawin alat sholat status perkawinan duda dan janda.

Demikian surat keterangan ini di buat dengan sebenarnya dan agar bisa 
di pergunakan sebagaimana mestinya.

Kasomalang Kulon. 07 November 2025
a.n Kepala Desa Kasomalang Kulon
Sekretaris Desa

APIPUDIN, S.IP
```

### Catatan Implementasi
- Field wali dan saksi sering kosong/placeholder di dokumen real
- Format tanggal perkawinan bisa written (05 Oktober 1965) atau numeric (05-10-1965)
- "Bin" = Bin (dalam bahasa Arab) = anak/putra dari (ayah)
- Status perkawinan adalah enum: duda, janda, duda dan janda, perjaka, gadis
- Jarak antar tanda tangan besar (untuk tempat TTD tangan)

---

## 4. SURAT KETERANGAN BEDA LUAS REAL - Data Real

### Input Data (JSON)
```json
{
  "letterType": "beda_luas",
  "year": 2025,
  "counterType": "PEM",
  "pemilik": {
    "nama": "IYAH",
    "tempat_lahir": "Subang",
    "tanggal_lahir": "1991-10-18",
    "pekerjaan": "Mengurus Rumah Tangga",
    "rt": "002",
    "rw": "02"
  },
  "sertifikat": {
    "nomor": "995",
    "luas_m2": 173,
    "nama_pemilik": "IYAH"
  },
  "sppt": {
    "nomor": "008-0135.0",
    "luas_m2": 179,
    "nama_pemilik": "IYAH"
  },
  "penandatangan": {
    "nama": "APIPUDIN",
    "gelar": "S.IP"
  },
  "tanggal_surat": "2025-02-21"
}
```

### Output Text yang Dihasilkan
```
PEMERINTAH DAERAH KABUPATEN SUBANG
KECAMATAN KASOMALANG
DESA KASOMALANG KULON
Alamat: Jln. Raya Kasomalang No.01Subang 41281

SURAT KETERANGAN
No : 141 /    / PEM / 2025

Yang bertandatangan dibawah ini, Sekretaris Desa Kasomalang Kulon
Kecamatan Kasomalang
Kabupaten Subang, dengan ini menerangkan bahwa:

Luas yang tertera di sertifikat :
Nomor        : 995
Nama Pemilik : IYAH
Luas         : 173 M2
Tempat, Tanggal lahir: Subang, 18-10-1991
Pekerjaan: Mengurus Rumah Tangga
Alamat : Rt. 002 Rw. 02 Desa Kasomalang Kulon Kecamatan Kasomalang Kabupaten Subang.

Dan Luas yang tertera di SPPT :
Nomor: 008-0135.0
Luas: 179 M2
Nama Pemilik: IYAH
Alamat: Rt. 002 Rw. 02 Desa Kasomalang Kulon Kecamatan Kasomalang Kabupaten Subang.

Orang tersebut adalah warga Desa Kasomalang Kulon, dan ada perbedaan luas antara 
Sertifikat dan SPPT, adapun pemilik tanah tersebut orangnya sama itu-itu juga.

Demikian surat keterangan ini kami buat dengan sebenarnya agar yang berkepentingan 
menjadi tahu serta untuk dipergunakan sebagaimana mestinya.

            Kasomalang Kulon, 21 Februari 2025
Sekretaris Desa Kasomalang Kulon

APIPUDIN, S.IP
```

### Catatan Implementasi
- Luas sertifikat: 173 M2
- Luas SPPT: 179 M2
- Perbedaan: 6 M2 (ini adalah isi/point dari surat)
- SPPT = Surat Pemberitahuan Pajak Tanah (untuk pajak bumi dan bangunan)
- Nomor SPPT format khusus: `008-0135.0` (dengan dot di akhir)
- Indentasi pada "Sekretaris Desa" berbeda (lebih ke kanan)

---

## 5. SURAT UNDANGAN - Data Template

### Input Data (JSON)
```json
{
  "letterType": "undangan",
  "nomor_surat": "UN-2024-001",
  "sifat_surat": "Biasa",
  "lampiran": "1 (satu) lembar",
  "hal": "Undangan",
  "penerima": {
    "nama": "[DIISI: Nama Penerima]",
    "alamat_1": "[DIISI: Alamat 1]",
    "alamat_2": "[DIISI: Alamat 2]",
    "kota": "[DIISI: Kota]"
  },
  "acara": {
    "hari_tanggal": "[DIISI: Hari, DD Bulan YYYY]",
    "waktu": "[DIISI: HH.MM WIB]",
    "tempat": "[DIISI: Tempat Acara]",
    "deskripsi": "Musyawarah pencermatan RKP Desa Tahun 2025"
  },
  "penandatangan": {
    "nama": "H. AMIRUDIN",
    "jabatan": "KEPALA DESA",
    "gelar": "S.Pd.I",
    "desa_nama": "KASOMALANG KULON"
  },
  "tanggal_surat": "2024-08-26",
  "tembusan": [
    "Camat Kasomalang",
    "Ketua BPD Desa Kasomalang Kulon",
    "Arsip"
  ]
}
```

### Output Text yang Dihasilkan
```
Kasomalang Kulon, 26 Agustus 2024
Nomor: UN-2024-001
Sifat: Biasa
Lampiran: 1 (satu) lembar
Hal: Undangan

Yth. [DIISI: Nama Penerima]
[DIISI: Alamat 1]
[DIISI: Alamat 2]
di
[DIISI: Kota]

Hari/tanggal: [DIISI: Hari, DD Bulan YYYY]
Waktu: [DIISI: HH.MM WIB]
Tempat: [DIISI: Tempat Acara]
Acara: Musyawarah pencermatan RKP Desa Tahun 2025

Demikian kami sampaikan, atas perkenan dan perhatiannya diucapkan terima kasih.

Tembusan :
- Camat Kasomalang
- Ketua BPD Desa Kasomalang Kulon
- Arsip

KEPALA DESA KASOMALANG KULON
H. AMIRUDIN, S.Pd.I
```

### Catatan Implementasi
- Ini adalah **template form** - banyak field kosong untuk diisi
- Format: Surat template standart (Nomor, Sifat, Lampiran, Hal)
- Penerima: "Yth." = Yth. (Dengan hormat) - sapaan formal
- Acara deskripsi sudah fixed: "Musyawarah pencermatan RKP Desa Tahun [TAHUN]"
- Tembusan berisi 3 pihak standar: Camat, BPD, Arsip
- Penandatangan: Langsung dari Kepala Desa (bukan A.n)
- Status: Ini adalah template dasar, penerima bisa customize nomor/sifat/lampiran

---

## 📊 Tabel Validasi Data Real

### Validasi NIK (SKD & SKM)
```
SKD:
✓ 3213261205940008 (16 digit)

SKM (7 entries):
✓ 3213025108000001 (16 digit)
✓ 3213261907870002 (16 digit)
✓ 3213264102890015 (16 digit)
✓ 3213260702010008 (16 digit)
✓ 3213264408800006 (16 digit)
✓ 3209271304900005 (16 digit)
✓ 3213261402730007 (16 digit)

Format: Provinsi (4) + Kabupaten (4) + Kode (8)
Semua valid dan sesuai format NIK Indonesia
```

### Validasi Tanggal
```
SKD TTL: 12-05-1994 ✓
SKM TTL: 11-08-2000 ✓
SKNT TTL Suami: 07-05-1947 ✓
SKNT TTL Istri: 05-05-1952 ✓
SKNT Perkawinan: 05-10-1965 ✓ (TTL < Nikah)
SKBL TTL: 18-10-1991 ✓

Format Surat:
SKD: 06 Juli 2026 ✓ (DD MMMM YYYY)
SKM: 02-Oktober-2025 ✓ (DD-Bulan-YYYY)
SKNT: 07 November 2025 ✓ (DD MMMM YYYY)
SKBL: 21 Februari 2025 ✓ (DD MMMM YYYY)
SU: 26 Agustus 2024 ✓ (DD MMMM YYYY)
```

### Validasi Nomor Surat
```
SKD: 140/25/Pem/2026 ✓ (Format: [URUT]/[KODE]/[TIPE]/[TAHUN])
SKM: 460//Ks/2025 ✓ (Format: [URUT]//[TIPE]/[TAHUN], kode kosong)
SKNT: 474//Ks/2025 ✓ (Format: [URUT]//[TIPE]/[TAHUN])
SKBL: 141//PEM/2025 ✓ (Format: [URUT]//[TIPE]/[TAHUN])
SU: UN-2024-001 ✓ (Manual, format custom)
```

---

## 🎯 Key Observations dari Data Real

1. **Konsistensi Tinggi dalam Format**
   - Semua surat mengikuti struktur pemerintah yang sama
   - Field data konsisten dengan spesifikasi

2. **Data Manusia (Real People)**
   - SKD: 1 orang nyata dengan NIK asli
   - SKM: 7 orang nyata dengan NIK asli
   - SKNT: Pasangan nyata (usia lanjut, sudah menikah lama)
   - SKBL: 1 pemilik tanah nyata

3. **Field Optional sering Kosong**
   - Wali di SKNT: sering "Bapak ....................." (placeholder)
   - Saksi di SKNT: sering kosong
   - Hal di SU: template bisa customize

4. **Tipe RT/RW**
   - Format standar: `RT 001 RW 006` (3 digit)
   - Alternatif di sertifikat: `RT006/001` (slash format)

5. **Perbedaan Subtil**
   - Ada typo/spacing issues di dokumen asli
   - Ada variation dalam penulisan (e.g., "Rw. 02" vs "RW 02")
   - Konsisten dalam implementasi, standardisasi output adalah penting

---

## 💾 Rekomendasi Penyimpanan

Untuk implementasi database:

```sql
INSERT INTO surat_submissions (
  template_id, letter_number, submission_data, pdf_path, created_by, created_at
) VALUES (
  'SKD', '140/25/Pem/2026', 
  '{JSON input data di atas}', 
  '/storage/surat/2026/140_SKD_2026.pdf',
  123,
  NOW()
);
```

Simpan:
- Template ID (SKD, SKM, SKNT, SKBL, SU)
- Nomor Surat (auto-generated)
- Data input lengkap (JSON)
- Path ke PDF generated
- User ID yang membuat
- Timestamp

Ini memungkinkan audit trail dan re-generation dokumen jika perlu.
