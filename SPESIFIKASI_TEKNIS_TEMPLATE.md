# Spesifikasi Teknis Template Surat - Untuk PDF Generator

## STRUKTUR DATA MASTER

### 1. SURAT KETERANGAN DOMISILI

**ID Template:** `SKD` (Surat Keterangan Domisili)

**Konfigurasi Nomor Surat:**
- Format: `{counter}/{office_code}/Pem/{year}`
- Counter: Auto-increment per tahun
- Office Code: 25 (atau konfigurable per kantor)
- Type: "Pem"

**Required Fields:**
```
- subyek.nik: string (16 digits)
- subyek.nama: string
- subyek.tempat_lahir: string
- subyek.tanggal_lahir: date (format: DD-MM-YYYY)
- subyek.pekerjaan: string
- subyek.alamat_lama: string
- subyek.rt_rw_baru: string (e.g., "RT 001 RW 006")
- subyek.desa_baru: string
- subyek.kecamatan_baru: string
- subyek.kabupaten_baru: string
- tujuan_penggunaan: string (e.g., "Identitas", "Permohonan Kredit", etc.)
- penandatangan.nama: string
- penandatangan.gelar: string (e.g., "S.IP")
- penandatangan.jabatan: string (default: "Sekretaris Desa")
- penandatangan.peran: enum ["atas_nama", "langsung"] (default: "atas_nama")
- tanggal_surat: date (format: DD MMMM YYYY, e.g., "06 Juli 2026")
- lokasi_surat: string (default: "Kasomalang Kulon")
- desa_nama: string (default: "Kasomalang Kulon")
- kecamatan_nama: string (default: "Kasomalang")
- kabupaten_nama: string (default: "Subang")
```

**Optional Fields:**
```
- alamat_desa: string (untuk KOP)
- keterangan_rt_rw_lama: string (e.g., "001/006")
```

**Template Text (Placeholders):**
```
KOP:
---
PEMERINTAH KABUPATEN {kabupaten_nama}
KECAMATAN {kecamatan_nama}
DESA {desa_nama}
Alamat : {alamat_desa}

JUDUL:
SURAT KETERANGAN DOMISILI 
Nomor : {nomor_surat}

PEMBUKA:
Yang bertandatangan dibawah ini Kepala Desa {desa_nama}
Kecamatan {kecamatan_nama}
Kabupaten {kabupaten_nama}, menerangkan dengan sesungguhnya bahwa :

ISI:
Nik: {subyek.nik}
Nama: {subyek.nama}
Tempat tanggal lahir: {subyek.tempat_lahir}, {subyek.tanggal_lahir}
Pekerjaan: {subyek.pekerjaan}
Alamat: {subyek.alamat_lama}

Orang tersebut sudah tidak tinggal di alamat sebagaimana tercantum di atas, dan 
berdasarkan keterangan dari Ketua RT/RW, orang tersebut saat ini benar berdomisili 
di {subyek.rt_rw_baru} Desa {subyek.desa_baru} Kecamatan {subyek.kecamatan_baru} 
Kabupaten {subyek.kabupaten_baru}.

Surat Keterangan ini kami berikan untuk : {tujuan_penggunaan}

PENUTUP:
Demikian surat keterangan ini dibuat dengan sebenarnya agar dapat dipergunakan 
sebagaimana mestinya.

TANDA TANGAN:
{lokasi_surat}, {tanggal_surat}
       A.n Kepala Desa {desa_nama}
{penandatangan.jabatan}

{penandatangan.nama},{penandatangan.gelar}
```

---

### 2. SURAT KETERANGAN MISKIN

**ID Template:** `SKM` (Surat Keterangan Miskin)

**Konfigurasi Nomor Surat:**
- Format: `{counter}//Ks/{year}`
- Counter: Auto-increment per tahun
- Office Code: Kosong
- Type: "Ks"

**Required Fields:**
```
- ketua_rw: string (nama)
- ketua_rw_nomor: string (e.g., "02")
- program_bantuan: string (e.g., "Program Jabar Caang")
- data_keluarga: array of {
    nik: string (16 digits),
    nama: string,
    tempat_lahir: string,
    tanggal_lahir: date (DD-MM-YYYY),
    rt: string (e.g., "006"),
    rw: string (e.g., "002")
  }
- penandatangan.nama: string
- penandatangan.gelar: string
- penandatangan.jabatan: string (default: "Sekretaris Desa")
- tanggal_surat: date (DD-Bulan-YYYY)
- lokasi_surat: string
- desa_nama: string
- kecamatan_nama: string
- kabupaten_nama: string
```

**Optional Fields:**
```
- minimum_baris_tabel: 7 (default)
```

**Template Text:**
```
JUDUL:
SURAT KETERANGAN MISKIN
Nomor : {nomor_surat}

PEMBUKA:
Yang bertandatangan dibawah ini Sekretaris Desa {desa_nama}
Kecamatan {kecamatan_nama}
Kabupaten {kabupaten_nama}, menerangkan dengan sesungguhnya bahwa :

ISI:
Orang-orang yang tercantum dalam daftar terlampir adalah benar-benar warga 
Desa kami dan tergolong dalam kategori Keluarga Tidak Mampu.

Daftar nama terlampir adalah warga RW {ketua_rw_nomor} Desa {desa_nama} :

[TABLE]
| NIK | NAMA | TEMPAT, TGL LAHIR | ALAMAT |
|-----|------|-------------------|--------|
{for each data in data_keluarga}
| {data.nik} | {data.nama} | {data.tempat_lahir}, {data.tanggal_lahir} | RT {data.rt} RW {data.rw} |
{/for}

Surat keterangan ini dibuat sebagai salah satu syarat untuk keperluan pengajuan 
bantuan sosial {program_bantuan}

PENUTUP:
Demikian surat ini dibuat dengan sebenarnya agar dapat digunakan sebagaimana mestinya.

TANDA TANGAN:
{lokasi_surat}, {tanggal_surat}
  a.n Kepala Desa {desa_nama}
  Ketua Rw. {ketua_rw_nomor}
Sekretaris Desa 
{penandatangan.nama},{penandatangan.gelar}
```

---

### 3. SURAT KETERANGAN NIKAH TIDAK TERCATAT

**ID Template:** `SKNT` (Surat Keterangan Nikah Tidak Tercatat)

**Konfigurasi Nomor Surat:**
- Format: `{counter}//Ks/{year}`
- Counter: Auto-increment per tahun
- Type: "Ks"

**Required Fields:**
```
- suami: {
    nama: string,
    nama_ayah: string,
    tempat_lahir: string,
    tanggal_lahir: date (DD-MM-YYYY),
    agama: enum ["Islam", "Kristen Protestan", "Kristen Katolik", "Hindu", "Budha", "Konghucu"],
    pekerjaan: string,
    alamat: string
  }
- istri: {
    nama: string,
    nama_ayah: string,
    tempat_lahir: string,
    tanggal_lahir: date (DD-MM-YYYY),
    agama: enum ["Islam", "Kristen Protestan", "Kristen Katolik", "Hindu", "Budha", "Konghucu"],
    pekerjaan: string,
    alamat: string
  }
- tanggal_perkawinan: date (DD-MM-YYYY)
- nama_wali: string (optional)
- nama_saksi_1: string (optional)
- nama_saksi_2: string (optional)
- mas_kawin: string (e.g., "alat sholat", "emas 5 gram")
- status_perkawinan: enum ["duda dan janda", "duda", "janda", "perjaka dan gadis"]
- penandatangan.nama: string
- penandatangan.gelar: string
- penandatangan.jabatan: string (default: "Sekretaris Desa")
- tanggal_surat: date (DD Bulan YYYY)
- lokasi_surat: string
- desa_nama: string
- kecamatan_nama: string
- kabupaten_nama: string
```

**Template Text:**
```
JUDUL:
SURAT KETERANGAN NIKAH TIDAK TERCATAT
Nomor : {nomor_surat}

PEMBUKA:
Yang bertanda tangan di bawah ini, Kepala Desa {desa_nama} 
Kec. {kecamatan_nama} Kab. {kabupaten_nama} menerangkan prihal perkawinan antara :

ISI - PIHAK PERTAMA (SUAMI):
Nama           : {suami.nama}
Bin            : {suami.nama_ayah}
Tempat Tgl Lahir : {suami.tempat_lahir}, {suami.tanggal_lahir}
Agama          : {suami.agama}
Pekerjaan      : {suami.pekerjaan}
Alamat         : {suami.alamat}

Dengan seorang Wanita :
Nama           : {istri.nama}
Bin            : {istri.nama_ayah}
Tempat Tgl Lahir : {istri.tempat_lahir}, {istri.tanggal_lahir}
Agama          : {istri.agama}
Pekerjaan      : {istri.pekerjaan}
Alamat         : {istri.alamat}

ISI - KETERANGAN PERKAWINAN:
Menurut sepengetahuan kami orang tersebut di atas telah menikah pada tanggal 
{tanggal_perkawinan} yang di laksanakan oleh walinya Bapak {nama_wali}
dengan saksi 2 orang, 1. Bapak {nama_saksi_1} 2. Bapak {nama_saksi_2}
dengan mas kawin {mas_kawin} status perkawinan {status_perkawinan}.

PENUTUP:
Demikian surat keterangan ini di buat dengan sebenarnya dan agar bisa 
di pergunakan sebagaimana mestinya.

TANDA TANGAN:
{lokasi_surat}. {tanggal_surat}
a.n Kepala Desa {desa_nama}
Sekretaris Desa

{penandatangan.nama}, {penandatangan.gelar}
```

---

### 4. SURAT KETERANGAN BEDA LUAS REAL

**ID Template:** `SKBL` (Surat Keterangan Beda Luas)

**Konfigurasi Nomor Surat:**
- Format: `{counter}//PEM/{year}`
- Counter: Auto-increment per tahun
- Type: "PEM"

**Required Fields:**
```
- pemilik: {
    nama: string,
    tempat_lahir: string,
    tanggal_lahir: date (DD-MM-YYYY),
    pekerjaan: string,
    rt: string (e.g., "002"),
    rw: string (e.g., "02")
  }
- sertifikat: {
    nomor: string,
    luas_m2: number,
    nama_pemilik: string
  }
- sppt: {
    nomor: string (e.g., "008-0135.0"),
    luas_m2: number,
    nama_pemilik: string
  }
- penandatangan.nama: string
- penandatangan.gelar: string
- penandatangan.jabatan: string (default: "Sekretaris Desa")
- tanggal_surat: date (DD Bulan YYYY)
- lokasi_surat: string
- desa_nama: string
- kecamatan_nama: string
- kabupaten_nama: string
```

**Template Text:**
```
KOP:
PEMERINTAH DAERAH KABUPATEN {kabupaten_nama}
KECAMATAN {kecamatan_nama}
DESA {desa_nama}
Alamat: Jln. Raya {kecamatan_nama} No.01 {kabupaten_nama}

JUDUL:
SURAT KETERANGAN
No : {nomor_surat}

PEMBUKA:
Yang bertandatangan dibawah ini, Sekretaris Desa {desa_nama}
Kecamatan {kecamatan_nama}
Kabupaten {kabupaten_nama}, dengan ini menerangkan bahwa:

ISI:
Luas yang tertera di sertifikat :
Nomor        : {sertifikat.nomor}
Nama Pemilik : {sertifikat.nama_pemilik}
Luas         : {sertifikat.luas_m2} M2
Tempat, Tanggal lahir: {pemilik.tempat_lahir}, {pemilik.tanggal_lahir}
Pekerjaan: {pemilik.pekerjaan}
Alamat : Rt. {pemilik.rt} Rw. {pemilik.rw} Desa {desa_nama} 
         Kecamatan {kecamatan_nama} Kabupaten {kabupaten_nama}.

Dan Luas yang tertera di SPPT :
Nomor: {sppt.nomor}
Luas: {sppt.luas_m2} M2
Nama Pemilik: {sppt.nama_pemilik}
Alamat: Rt. {pemilik.rt} Rw. {pemilik.rw} Desa {desa_nama} 
        Kecamatan {kecamatan_nama} Kabupaten {kabupaten_nama}.

Orang tersebut adalah warga Desa {desa_nama}, dan ada perbedaan luas antara 
Sertifikat dan SPPT, adapun pemilik tanah tersebut orangnya sama itu-itu juga.

PENUTUP:
Demikian surat keterangan ini kami buat dengan sebenarnya agar yang berkepentingan 
menjadi tahu serta untuk dipergunakan sebagaimana mestinya.

TANDA TANGAN:
            {lokasi_surat}, {tanggal_surat}
{penandatangan.jabatan} {desa_nama}

{penandatangan.nama}, {penandatangan.gelar}
```

---

### 5. SURAT UNDANGAN

**ID Template:** `SU` (Surat Undangan)

**Konfigurasi Nomor Surat:**
- Format: `{nomor}` (manual input, tidak auto-increment)
- Custom fields available

**Required Fields:**
```
- nomor_surat: string (e.g., "UN-2024-001")
- sifat_surat: string (e.g., "Biasa", "Penting", "Rahasia")
- lampiran: string (e.g., "1 (satu) lembar", "2 (dua) berkas")
- hal: string (default: "Undangan")
- penerima_nama: string
- penerima_alamat_1: string
- penerima_alamat_2: string
- penerima_kota: string
- hari_tanggal: date (format: "Hari, DD Bulan YYYY")
- waktu: string (e.g., "13.30 WIB")
- tempat_acara: string
- acara_deskripsi: string (e.g., "Musyawarah pencermatan RKP Desa Tahun 2025")
- penandatangan_nama: string
- penandatangan_jabatan: string (default: "KEPALA DESA")
- penandatangan_gelar: string (e.g., "H. AMIRUDIN, S.Pd.I")
- tanggal_surat: date (DD Bulan YYYY)
- lokasi_surat: string
- desa_nama: string
- tembusan: array of string (e.g., ["Camat Kasomalang", "Ketua BPD Desa", "Arsip"])
```

**Template Text:**
```
HEADER:
{lokasi_surat}, {tanggal_surat}
Nomor: {nomor_surat}
Sifat: {sifat_surat}
Lampiran: {lampiran}
Hal: {hal}

PENERIMA:
Yth. {penerima_nama}
{penerima_alamat_1}
{penerima_alamat_2}
di
{penerima_kota}

ISI:
Hari/tanggal: {hari_tanggal}
Waktu: {waktu}
Tempat: {tempat_acara}
Acara: {acara_deskripsi}

PENUTUP:
Demikian kami sampaikan, atas perkenan dan perhatiannya diucapkan terima kasih.

TEMBUSAN:
Tembusan :
{for each item in tembusan}
- {item}
{/for}

TANDA TANGAN:
{penandatangan_jabatan}
{desa_nama}
{penandatangan_gelar}
```

---

## SISTEM PENOMORAN SURAT

### Auto-Increment Counter Logic
```
Counter per Jenis Surat per Tahun:
- Key: `{TYPE}_{YEAR}` (e.g., "Pem_2026", "Ks_2025")
- Format: `[COUNTER]/[OFFICE_CODE]/[TYPE]/[YEAR]`

Storage mechanism:
- Database table: `letter_counters`
- Columns: id, letter_type, year, last_counter
- On each new letter: increment counter, format nomor_surat
```

### Mapping Type Code
```
PEM  → Surat Pemerintah (KD, SKBL)
Ks   → Keterangan Sosial (SKM, SKNT)
```

---

## SIGNATURE ROLES DAN VARIANTS

### Variant 1: A.n Kepala Desa (Atas Nama)
- Digunakan oleh: Sekretaris Desa
- Struktur:
  ```
  {lokasi}, {tanggal}
         A.n Kepala Desa {desa}
  {jabatan}
  
  {nama}, {gelar}
  ```

### Variant 2: Langsung dari Kepala Desa
- Digunakan oleh: Kepala Desa
- Struktur:
  ```
  {lokasi}, {tanggal}
  {jabatan}
  {desa}
  
  {nama}, {gelar}
  ```

### Variant 3: Multiple Signatories
- Digunakan oleh: Surat Miskin (Ketua RW + Sekretaris Desa)
- Struktur:
  ```
  {lokasi}, {tanggal}
    a.n Kepala Desa {desa}
    Ketua Rw. {no_rw}
  Sekretaris Desa
  
  {nama}, {gelar}
  ```

---

## DATE FORMAT STANDARDIZATION

### Format yang digunakan dalam dokumen:
1. **DD-MM-YYYY**: `12-05-1994` (untuk TTL, tanggal perkawinan)
2. **DD Bulan YYYY**: `06 Juli 2026` (untuk tanggal surat, format formal)
3. **DD-Bulan-YYYY**: `02-Oktober-2025` (variasi dengan strip)

### Bulan (Indonesian):
```
Januari, Februari, Maret, April, Mei, Juni,
Juli, Agustus, September, Oktober, November, Desember
```

### Recommended Implementation:
- Store internally as ISO 8601 (YYYY-MM-DD)
- Format output berdasarkan context yang diperlukan

---

## VALIDASI DATA

### Required Validations Per Template:

**SKD (Domisili):**
- nik: 16 digits
- tanggal_lahir: valid date
- rt_rw_baru: format `RT {XX} RW {XX}`

**SKM (Miskin):**
- Minimum 1 data keluarga
- Semua nik: 16 digits
- rw: match dengan ketua_rw_nomor

**SKNT (Nikah Tidak Tercatat):**
- Tanggal perkawinan: valid date, <= tanggal_surat
- Agama: valid enum

**SKBL (Beda Luas):**
- sertifikat.luas_m2 ≠ sppt.luas_m2 (harus ada perbedaan)
- Nomor sertifikat dan SPPT tidak boleh kosong

**SU (Undangan):**
- hari_tanggal: valid date
- penerima_nama: not empty
- acara_deskripsi: not empty

---

## FONT DAN STYLING (UNTUK PDF OUTPUT)

### Rekomendasi Styling:
```
Font Family: Arial atau Calibri
Encoding: UTF-8

KOP:
- Font Size: 12pt
- Weight: Bold
- Line Spacing: 1.0

Judul Surat:
- Font Size: 14pt
- Weight: Bold
- Alignment: Center

Nomor Surat:
- Font Size: 12pt
- Weight: Normal

Body:
- Font Size: 12pt
- Line Spacing: 1.5
- Justification: Justified untuk paragraph panjang

Tabel (untuk SKM):
- Font Size: 10pt
- Border: 1px solid black
- Alignment: Center untuk header, Left untuk data

Tanda Tangan:
- Font Size: 12pt
- Line Height: 2.0 (untuk space tanda tangan)

Margin:
- Top: 1.44cm (1 inchi)
- Bottom: 1.44cm
- Left: 1.44cm
- Right: 1.44cm
```

---

## KESIMPULAN SPESIFIKASI

File ini mencakup:
1. ✅ 5 template surat dengan struktur field lengkap
2. ✅ Format nomor surat yang terstandar
3. ✅ Sistem penomoran auto-increment
4. ✅ Signature variants untuk berbagai jabatan
5. ✅ Format tanggal yang konsisten
6. ✅ Validasi data yang diperlukan
7. ✅ Styling dan formatting recommendation

Siap untuk dikembangkan menjadi:
- Template engine (mustache, handlebars, jinja2)
- PDF generation library (wkhtmltopdf, puppeteer, pdfkit)
- Database schema untuk counter dan template management
