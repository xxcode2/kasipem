# Panduan Field Mapping - Input ke Template

## QUICK REFERENCE: Setiap Surat Memerlukan Field Apa?

### ✅ SURAT KETERANGAN DOMISILI (SKD)

**Kategori: Data Pribadi**
```
├─ nik ........................... (16 digit, contoh: 3213261205940008)
├─ nama .......................... (contoh: SAEPUROHMAN)
├─ tempat_lahir .................. (contoh: Subang)
├─ tanggal_lahir ................. (format: 12-05-1994)
├─ pekerjaan ..................... (contoh: Belum/Tidak Bekerja)
└─ alamat_lama ................... (contoh: Perum Green Mutiara 2 Rt 005 Rw 017 Desa Dawuan Timur Kecamatan Cikampek)

Kategori: Data Domisili Baru
├─ rt_rw_baru .................... (contoh: RT 001 RW 006)
├─ desa_baru ..................... (contoh: Kasomalang Kulon)
├─ kecamatan_baru ................ (contoh: Kasomalang)
├─ kabupaten_baru ................ (contoh: Subang)
└─ tujuan_penggunaan ............. (contoh: Identitas)

Kategori: Dokumen
├─ nomor_surat ................... (auto-generated: 140/25/Pem/2026)
├─ lokasi_surat .................. (default: Kasomalang Kulon)
├─ tanggal_surat ................. (format: 06 Juli 2026)
└─ desa_nama ..................... (default: Kasomalang Kulon)

Kategori: Tanda Tangan
├─ penandatangan.nama ............ (contoh: APIPUDIN)
├─ penandatangan.gelar ........... (contoh: S.IP)
└─ penandatangan.peran ........... (default: atas_nama)
```

**Contoh Input JSON:**
```json
{
  "letterType": "keterangan_domisili",
  "year": 2026,
  "counterType": "Pem",
  "officeCode": 25,
  "subyek": {
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
  "tujuan_penggunaan": "Identitas",
  "penandatangan": {
    "nama": "APIPUDIN",
    "gelar": "S.IP",
    "peran": "atas_nama"
  },
  "tanggal_surat": "2026-07-06"
}
```

---

### ✅ SURAT KETERANGAN MISKIN (SKM)

**Kategori: Informasi RW**
```
├─ ketua_rw_nomor ................ (contoh: 02)
└─ ketua_rw_nama ................. (tidak ada di contoh, gunakan untuk co-signatory)

Kategori: Program
├─ program_bantuan ............... (contoh: Program Jabar Caang)
└─ tujuan_bantuan ................ (implicit dari surat)

Kategori: Data Keluarga (Repeatable - Array)
├─ [0] ........................... 
│  ├─ nik ........................ (3213025108000001)
│  ├─ nama ....................... (DIAN PITRIANI)
│  ├─ tempat_lahir ............... (SUBANG)
│  ├─ tanggal_lahir .............. (11-08-2000)
│  ├─ rt ......................... (006)
│  └─ rw ......................... (002)
├─ [1]
│  └─ ... (7+ entries total)
└─ [N]

Kategori: Dokumen
├─ nomor_surat ................... (auto-generated: 460//Ks/2025)
├─ lokasi_surat .................. (Kasomalang Kulon)
├─ tanggal_surat ................. (02-Oktober-2025)
└─ desa_nama ..................... (Kasomalang Kulon)

Kategori: Tanda Tangan
├─ ketua_rw.nama ................. (untuk co-signatory)
├─ penandatangan.nama ............ (APIPUDIN)
└─ penandatangan.gelar ........... (S.IP)
```

**Catatan Penting:**
- Minimal 7 data keluarga
- Semua nik harus 16 digit
- RW di daftar harus sama dengan ketua_rw_nomor (atau gabungan RW 2)

**Contoh Input JSON:**
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
    }
    // ... minimal 5 more entries
  ],
  "penandatangan": {
    "nama": "APIPUDIN",
    "gelar": "S.IP"
  },
  "tanggal_surat": "2025-10-02"
}
```

---

### ✅ SURAT KETERANGAN NIKAH TIDAK TERCATAT (SKNT)

**Kategori: Data Suami**
```
├─ nama .......................... (DARTAM)
├─ nama_ayah ..................... (SATA)
├─ tempat_lahir .................. (Subang)
├─ tanggal_lahir ................. (07-05-1947)
├─ agama ......................... (Islam)
├─ pekerjaan ..................... (Petani/kebun)
└─ alamat ........................ (Kp.Karihkil RT006/001 Desa Kasomalang Kulon...)

Kategori: Data Istri
├─ nama .......................... (TIRAH)
├─ nama_ayah ..................... (ANISEM)
├─ tempat_lahir .................. (Subang)
├─ tanggal_lahir ................. (05-05-1952)
├─ agama ......................... (Islam)
├─ pekerjaan ..................... (Petani/pekebun)
└─ alamat ........................ (Kp.Karihkil RT006/001 Desa Kasomalang Kulon...)

Kategori: Detail Perkawinan
├─ tanggal_perkawinan ............ (05 Oktober 1965)
├─ nama_wali ..................... (Bapak .....................)
├─ nama_saksi_1 .................. (Bapak .....................)
├─ nama_saksi_2 .................. (Bapak .....................)
├─ mas_kawin ..................... (alat sholat)
└─ status_perkawinan ............. (duda dan janda)

Kategori: Dokumen
├─ nomor_surat ................... (auto-generated: 474//Ks/2025)
├─ lokasi_surat .................. (Kasomalang Kulon)
├─ tanggal_surat ................. (07 November 2025)
└─ desa_nama ..................... (Kasomalang Kulon)

Kategori: Tanda Tangan
├─ penandatangan.nama ............ (APIPUDIN)
└─ penandatangan.gelar ........... (S.IP)
```

**Contoh Input JSON:**
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
    "nama_wali": "Bapak [Nama]",
    "nama_saksi_1": "Bapak [Nama]",
    "nama_saksi_2": "Bapak [Nama]",
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

---

### ✅ SURAT KETERANGAN BEDA LUAS (SKBL)

**Kategori: Data Pemilik**
```
├─ nama .......................... (IYAH)
├─ tempat_lahir .................. (Subang)
├─ tanggal_lahir ................. (18-10-1991)
├─ pekerjaan ..................... (Mengurus Rumah Tangga)
├─ rt ............................ (002)
└─ rw ............................ (02)

Kategori: Data Sertifikat
├─ nomor ......................... (995)
├─ luas_m2 ....................... (173)
└─ nama_pemilik .................. (IYAH)

Kategori: Data SPPT (Pajak)
├─ nomor ......................... (008-0135.0)
├─ luas_m2 ....................... (179)
└─ nama_pemilik .................. (IYAH)

Kategori: Dokumen
├─ nomor_surat ................... (auto-generated: 141//PEM/2025)
├─ lokasi_surat .................. (Kasomalang Kulon)
├─ tanggal_surat ................. (21 Februari 2025)
└─ desa_nama ..................... (Kasomalang Kulon)

Kategori: Tanda Tangan
├─ penandatangan.nama ............ (APIPUDIN)
└─ penandatangan.gelar ........... (S.IP)
```

**Catatan:**
- Luas Sertifikat ≠ Luas SPPT (harus ada perbedaan, itu poin surat)
- Nama pemilik di sertifikat dan SPPT harus sama

**Contoh Input JSON:**
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

---

### ✅ SURAT UNDANGAN (SU)

**Kategori: Metadata Surat**
```
├─ nomor_surat ................... (manual input, tidak auto-generate)
├─ sifat_surat ................... (Biasa, Penting, Rahasia)
├─ lampiran ...................... (1 (satu) lembar)
└─ hal ........................... (Undangan)

Kategori: Penerima
├─ penerima_nama ................. (nama person/institusi)
├─ penerima_alamat_1 ............. (baris alamat pertama)
├─ penerima_alamat_2 ............. (baris alamat kedua)
└─ penerima_kota ................. (kota/lokasi)

Kategori: Detail Acara
├─ hari_tanggal .................. (Hari, DD Bulan YYYY)
├─ waktu ......................... (HH.MM WIB)
├─ tempat_acara .................. (lokasi acara)
└─ acara_deskripsi ............... (Musyawarah pencermatan RKPDesa Tahun 2025)

Kategori: Dokumen
├─ lokasi_surat .................. (Kasomalang Kulon)
└─ tanggal_surat ................. (26 Agustus 2024)

Kategori: Tanda Tangan
├─ penandatangan_nama ............ (H. AMIRUDIN)
├─ penandatangan_jabatan ......... (KEPALA DESA)
├─ penandatangan_gelar ........... (S.Pd.I)
└─ desa_nama ..................... (KASOMALANG KULON)

Kategori: Distribusi
└─ tembusan ....................... (array: ["Camat Kasomalang", "Ketua BPD Desa", "Arsip"])
```

**Contoh Input JSON:**
```json
{
  "letterType": "undangan",
  "nomor_surat": "UN-2024-001",
  "sifat_surat": "Biasa",
  "lampiran": "1 (satu) lembar",
  "hal": "Undangan",
  "penerima": {
    "nama": "Yth. Kepala Dinas Pendidikan",
    "alamat_1": "Kantor Dinas Pendidikan",
    "alamat_2": "Kabupaten Subang",
    "kota": "Subang"
  },
  "acara": {
    "hari_tanggal": "Senin, 26 Agustus 2024",
    "waktu": "13.30 WIB",
    "tempat": "Aula Kantor Desa",
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

---

## FIELD YANG SAMA UNTUK SEMUA TEMPLATE

```
Desa: Kasomalang Kulon (default, bisa di-override)
Kecamatan: Kasomalang (default)
Kabupaten: Subang (default)
Alamat Desa: Jl. Raya Kasomalang No.01 Kec. Kasomalang – Subang 41281 (untuk KOP)
Lokasi Surat: Kasomalang Kulon (default, lokasi penandatangan)
```

---

## MAPPING FIELD UNTUK DATABASE

### Table: `surat_templates`
```sql
CREATE TABLE surat_templates (
  id INT PRIMARY KEY,
  template_id VARCHAR(10) UNIQUE, -- SKD, SKM, SKNT, SKBL, SU
  template_name VARCHAR(100),
  category VARCHAR(50),
  
  -- Required fields (JSON array)
  required_fields JSON,
  
  -- Template text
  template_content LONGTEXT,
  
  -- Counter configuration
  counter_type VARCHAR(10),     -- PEM, Ks, Manual
  use_office_code BOOLEAN,
  office_code INT DEFAULT 25,
  
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Table: `surat_counters`
```sql
CREATE TABLE surat_counters (
  id INT PRIMARY KEY,
  template_id VARCHAR(10),
  year INT,
  last_counter INT DEFAULT 0,
  
  UNIQUE KEY unique_template_year (template_id, year)
);
```

### Table: `surat_submissions`
```sql
CREATE TABLE surat_submissions (
  id INT PRIMARY KEY AUTO_INCREMENT,
  template_id VARCHAR(10),
  submission_data JSON,        -- Semua field input
  nomor_surat VARCHAR(50),     -- Generated letter number
  pdf_path VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  created_by INT,
  
  INDEX idx_template (template_id),
  INDEX idx_year (YEAR(created_at))
);
```

---

## ALGORITMA: Dari Input ke Output PDF

```
1. VALIDASI INPUT
   ├─ Baca template_id (SKD, SKM, dsb)
   ├─ Load template config dari database
   ├─ Validasi semua required fields ada
   └─ Validasi format data (date, number, enum)

2. GENERATE NOMOR SURAT
   ├─ Query counter berdasarkan template_id + year
   ├─ Increment counter
   ├─ Format: {counter}/{office_code}/{type}/{year}
   └─ Update database counter

3. SUBSTITUTE FIELDS
   ├─ Baca template content
   ├─ Replace semua {field} dengan nilai dari input
   ├─ Handle repeating sections (array dalam SKM)
   └─ Format tanggal ke locale yang tepat

4. GENERATE PDF
   ├─ Render HTML dari template yang sudah di-substitute
   ├─ Apply styling (font, size, margin)
   ├─ Generate PDF file
   └─ Save ke disk

5. STORE METADATA
   ├─ Insert ke surat_submissions
   ├─ Simpan input data (untuk audit)
   ├─ Simpan nomor_surat
   └─ Simpan file path

6. OUTPUT
   └─ Return file PDF path ke user
```

---

## VALIDATION RULES PER FIELD

### Universal Rules
```
- All string fields: max 255 chars (except alamat: 500 chars)
- All dates: ISO 8601 format in storage, convert to locale on output
- NIK: exactly 16 digits
- Nomor sertifikat/SPPT: alphanumeric, no spaces
```

### Specific Rules
```
SKD:
- rt_rw_baru: must match regex `^RT \d{3} RW \d{3}$`

SKM:
- data_keluarga: min 1 item, max 50 items
- rw: must match ketua_rw_nomor

SKNT:
- tanggal_perkawinan <= tanggal_surat
- agama: one of predefined enum
- status_perkawinan: one of predefined enum

SKBL:
- sertifikat.luas_m2 != sppt.luas_m2
- sertifikat.nama_pemilik == sppt.nama_pemilik

SU:
- hari_tanggal: >= today
- waktu: valid HH.MM format
```

---

## TESTING CHECKLIST

- [ ] SKD: 1 orang, alamat lama ke baru
- [ ] SKM: 7 orang, semua di RW 02
- [ ] SKNT: Data lengkap suami-istri, dengan wali dan saksi
- [ ] SKBL: Luas sertifikat ≠ luas SPPT
- [ ] SU: Dengan penerima, waktu, dan tembusan
- [ ] Counter: Increment benar setiap dokumen
- [ ] Tanggal: Format output sesuai ID surat
- [ ] PDF: Readable, tidak ada placeholder tersisa
