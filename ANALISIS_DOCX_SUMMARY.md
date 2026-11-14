# ANALISIS STRUKTUR 12 FILE DOCX - SURAT KETERANGAN DESA

**Lokasi:** Desa Kasomalang Kulon, Kecamatan Kasomalang, Kabupaten Subang  
**Tanggal Analisis:** 2025  
**Tujuan:** Dokumentasi untuk implementasi sistem pembuat surat otomatis

---

## Ringkasan Eksekutif

| No | File | Tipe Surat | Format Nomor | Jumlah Field |
|----|------|-----------|--------------|--------------|
| 1 | PISAH KK.docx | Pemisahan Kartu Keluarga | XXX / / PEM / TAHUN | 5+ |
| 2 | SKT DAYAT.docx | Keterangan Tanah | XXX / / PEM / TAHUN | 13+ |
| 3 | SURAT BEDA NAMA new.docx | Perbedaan Luas Tanah | XXX / / PEM / TAHUN | 10 |
| 4 | SURAT KETERANGAN AHLI WARIS 2021.docx | Ahli Waris | Variabel | Tabel |
| 5 | SURAT KETERANGAN BEDA LUAS REAL.docx | Perbedaan Luas | No : XXX / / PEM / TAHUN | 10 |
| 6 | SURAT KETERANGAN DOMISILI.docx | Domisili | Nomor : XXX / XX / Pem / TAHUN | 6 |
| 7 | SURAT KETERANGAN MISKIN (SEKOLAH).docx | Kemiskinan Sekolah | Nomor : XXX/XX/Ks/TAHUN | 6 |
| 8 | SURAT KETERANGAN MISKIN.docx | Kemiskinan (Daftar) | Nomor : XXX/XX/Ks/TAHUN | Tabel |
| 9 | SURAT KETERANGAN NIKAH TIDAK TERCATAT new.docx | Nikah Tidak Tercatat | Nomor : XXX / / Ks / TAHUN | 12+ |
| 10 | SURAT PEMAKAMAN.docx | Pemakaman | Nomor : 460.3/ / / KS / TAHUN | 8+ |
| 11 | SURAT PERNYATAAN TIDAK MEMILIKI RUMAH.docx | Pernyataan Tanpa Rumah | Nomor : XXX / XX / Pem / TAHUN | 5+ |
| 12 | SURAT UNDANGAN YG DI TTD KEPALA DESA.docx | Undangan Resmi | Biasa (No, Sifat, Hal, dll) | 8+ |

---

## STRUKTUR UMUM SURAT

### Kop Surat (Header)
```
PEMERINTAH DAERAH KABUPATEN SUBANG
KECAMATAN KASOMALANG
DESA KASOMALANG KULON
Alamat: Jln. Raya Kasomalang No.01 Subang 41281
```

### Format Nomor Surat (Variasi)
- **Format 1 (PEM):** `XXX / / PEM / TAHUN` 
  - Contoh: `474 / / PEM / 2026`
  - Digunakan oleh: Pisah KK, SKT Tanah, Beda Luas, Domisili, Pernyataan Tanpa Rumah
  
- **Format 2 (Ks):** `XXX / / Ks / TAHUN` atau `XXX/XX/Ks/TAHUN`
  - Contoh: `460/25/Ks/2025`
  - Digunakan oleh: Surat Miskin, Nikah Tidak Tercatat

- **Format 3 (KS):** `460.3/ / / KS / TAHUN`
  - Digunakan oleh: Pemakaman

- **Format 4 (Standar):** `Nomor, Sifat, Lampiran, Hal`
  - Digunakan oleh: Undangan

### Penandatangan (Signatory)
- **Sekretaris Desa:** APIPUDIN, S.IP
- **Kepala Desa:** H. AMIRUDIN, S.Pd.I
- **Format TTD:** Tanda tangan (kosong) + Nama + Gelar

### Timestamp & Lokasi
```
[Lokasi], [Tanggal Hari/Bulan/Tahun]
Contoh: Kasomalang Kulon, 06 Juli 2026
```

---

## DETAIL SETIAP SURAT

### 1. PISAH KK (Pemisahan Kartu Keluarga)

**Nomor:** 474 / / PEM / 2026

**Field yang Diisi:**
- NIK Person 1 (16 digit): `3213266804990001`
- Nama Person 1: `SUMARNI APRIANTINI`
- NIK Person 2 (16 digit): `3213264803040001`
- Nama Person 2: `SUNITA ANGGRAENI`
- Nomor KK Lama (referensi): `3213261802250002`

**Struktur:**
1. Header (PEMERINTAH... DESA...)
2. Judul: SURAT KETERANGAN PISAH KK
3. Nomor Surat
4. Opening: "Yang bertanda tangan di bawah ini Kepala Desa..."
5. Data pemisah (NIK + Nama) untuk setiap person
6. Pernyataan: "...telah Pisah KK dari No. [KK Lama]"
7. Closing: "Demikian surat keterangan ini kami buat dengan sebenarnya..."
8. Timestamp & Lokasi
9. Signatory: a.n Kepala Desa / Sekretaris Desa + Nama

---

### 2. SKT DAYAT (Surat Keterangan Tanah)

**Nomor:** 593 / / PEM / 2026

**Field yang Diisi:**
- Nama Pemilik: `NENDEN KOMALA`
- NIK: `3213266704970005`
- TTL: `Subang, 27-04-1997`
- Alamat: `Kp Sukajaya Rt 002 Rw 005 Desa Kasomalang Kulon...`
- Luas Tanah (m²): `126`
- Luas Bangunan (m²): `48`
- SPPT/NOP: `32.15.031.003.004-0052.0`
- Batas Utara: [Owner Name/NOP]
- Batas Timur: [Owner Name/NOP]
- Batas Selatan: [Jalan/NOP]
- Batas Barat: [Jalan/NOP]
- Harga Taksiran Bangunan (IDR): `100.000.000`
- Harga Taksiran Tanah (IDR): `50.000.000`

**Struktur:**
1. Opening dengan nama officer dan jabatan
2. Statement: "Menerangkan dengan sebenarnya bahwa"
3. Data pemilik tanah
4. Detail tanah (luas, SPPT, boundaries)
5. Harga taksiran
6. Closing: "Demikian surat keterangan ini dibuat dengan sebenarnya..."
7. Signature: Kepala Desa + Camat

---

### 3. SURAT BEDA LUAS NAMA (Perbedaan Luas Tanah - Sertifikat vs SPPT)

**Nomor:** 474 / / PEM / 2025

**Field yang Diisi:**

**Bagian Sertifikat:**
- No Sertipikat: `0975`
- Nomor NIB: `10.08.17.10.01024`
- Luas (Sertifikat, m²): `153`
- Nama Pemilik (Sertifikat): `UUM`
- Alamat (Sertifikat): `Kp. Karihkil Rt . 003 Rw. 002...`

**Bagian SPPT/PBB:**
- NOP: `32.15.031.003.008-0183.0`
- Nama Pemilik (SPPT): `AHLIN`
- Luas (SPPT, m²): `203`
- Alamat (SPPT): `Kp. Karihkil Rt . 003 Rw. 002...`

**Struktur:**
1. Header
2. Judul: SURAT KETERANGAN BEDA LUAS
3. Nomor Surat
4. Opening: "Yang bertanda tangan di bawah ini Kepala Desa..."
5. Blok: "Nama dan Luas yang tertulis di Sertifikat"
6. Blok: "Nama dan Luas yang tertera di SPPT PBB"
7. Statement perbedaan
8. Closing: "Demikian surat keterangan ini kami buat dengan sebenarnya..."
9. Signatory: Sekretaris Desa (a.n Kepala Desa)

---

### 4. SURAT KETERANGAN AHLI WARIS

**Format:** Tanpa nomor standar

**Field yang Diisi (per Ahli Waris):**
- Nama Almarhum: `DIDI RIYADI`
- No | Nama | Hubungan Keluarga | Tanda Tangan
  - Contoh: 1 | AAT HADIATIN | Istri | [TTD]
  - Contoh: 2 | DEDE RAHMAT | Anak | [TTD]
  - Contoh: 3 | BUDIMAN RIA MIZAR | Anak | [TTD]

**Saksi-saksi:**
- No | Nama | Jabatan | Tanda Tangan
  - Contoh: 1 | LINA HERLINA | Ketua Rt. 03 Rw. 04 | [TTD]
  - Contoh: 2 | DANG NURMAS | Ketua RW. 04 | [TTD]

**Keperluan:** `Kelengkapan Dokumen`

**Struktur:**
1. Judul: SURAT KETERANGAN AHLI WARIS
2. Opening dengan nama almarhum
3. Tabel Ahli Waris: No | Nama | Hubungan | TTD
4. Statement: "...merupakan ahli waris yang sah dari almarhum..."
5. Bagian Saksi-saksi
6. Keperluan Surat
7. Closing

---

### 5. SURAT KETERANGAN BEDA LUAS REAL

**Nomor:** No : 141 / / PEM / 2025  
**Penanda Tangan:** Sekretaris Desa

**Field yang Diisi:**

**Sertifikat:**
- Nomor Sertifikat: `995`
- Nama Pemilik: `IYAH`
- Luas (m²): `173`
- TTL: `Subang, 18-10-1991`
- Pekerjaan: `Mengurus Rumah Tangga`
- Alamat: `Rt. 002 Rw. 02 Desa Kasomalang Kulon...`

**SPPT:**
- Nomor SPPT: `008-0135.0`
- Luas (m²): `179`

**Struktur:**
Similar dengan Surat Beda Nama, tapi diisi oleh Sekretaris Desa, bukan Kepala Desa.

---

### 6. SURAT KETERANGAN DOMISILI

**Nomor:** Nomor : 140 / 25 / Pem / 2026

**Field yang Diisi:**
- NIK: `3213261205940008`
- Nama: `SAEPUROHMAN`
- TTL: `Subang, 12-05-1994`
- Pekerjaan: `Belum/Tidak Bekerja`
- Alamat Lama: `Perum Green Mutiara 2 Rt 005 Rw 017 Desa Dawuan Timur...`
- Keperluan: `Identitas` (atau pilihan lain)

**Struktur:**
1. Header
2. Judul: SURAT KETERANGAN DOMISILI
3. Nomor Surat
4. Opening
5. Data diri (NIK, Nama, TTL, Pekerjaan, Alamat Lama)
6. Statement: "...sudah tidak tinggal dialamat sebagaimana tercantum diatas..."
7. Keperluan Surat
8. Closing
9. Signatory: a.n Kepala Desa / Sekretaris Desa

---

### 7. SURAT KETERANGAN MISKIN (SEKOLAH)

**Nomor:** Nomor : 460/25/Ks/2025

**Field yang Diisi:**
- Nama Lengkap: `RIO FADILAH`
- TTL: `Subang, 23-10-2010`
- Pekerjaan: `Pelajar`
- Alamat: `Rt. 004 Rw. 003 Desa Kasomalang kulon`
- Sekolah Tujuan: `SMPN Negeri Kasomalang`
- Keperluan: `Pendaftaran`

**Struktur:**
1. Header
2. Judul: SURAT KETERANGAN MISKIN
3. Nomor Surat
4. Opening: "Kepala Desa Kasomalang Kulon... menerangkan..."
5. Data diri lengkap
6. Permohonan keringanan biaya ke sekolah
7. Keperluan
8. Closing
9. Signatory: a.n Kepala Desa / Sekretaris Desa

---

### 8. SURAT KETERANGAN MISKIN (DAFTAR)

**Nomor:** Nomor : 460/25/Ks/2025

**Struktur Khusus (Tabel):**

Tabel dengan kolom:
- NIK
- NAMA
- TEMPAT, TGL LAHIR
- ALAMAT

**Contoh Data:**
```
3213025108000001 | DIAN PITRIANI | SUBANG,11-08-2000 | RT 006 RW 002
3213261907870002 | EDIH | SUBANG.19-07-1982 | RT 006 RW 002
3213264102890015 | ANI HERAWATI | SUBANG,01-02-1989 | RT 006 RW 002
3213260702010008 | DEDE PATHURROHMAN | SUBANG,21-12-2001 | RT 006 RW 002
3213264408800006 | HENI ROHAENI | SUBANG,04-08-1980 | RT 006 RW 002
```

**Catatan:** Dokumen ini berisi daftar warga miskin per RW dalam format tabel.

---

### 9. SURAT KETERANGAN NIKAH TIDAK TERCATAT

**Nomor:** Nomor : 474 / / Ks / 2025

**Field yang Diisi:**

**Suami (Laki-laki):**
- Nama: `DARTAM`
- Bin (Ayah): `SATA`
- TTL: `Subang, 07-05-1947`
- Agama: `Islam`
- Pekerjaan: `Petani/kebun`
- Alamat: `Kp.Karihkil RT006/001 Desa Kasomalang Kulon...`

**Istri (Perempuan):**
- Nama: `TIRAH`
- Bin (Ayah): `ANISEM`
- TTL: [to be filled]
- Agama: [to be filled]
- Pekerjaan: [to be filled]
- Alamat: [to be filled]

**Struktur:**
1. Judul: SURAT KETERANGAN NIKAH TIDAK TERCATAT
2. Nomor Surat
3. Opening: Kepala Desa menerangkan
4. Data Suami (Laki-laki) lengkap
5. Transisi: "Dengan seorang Wanita"
6. Data Istri (Perempuan) lengkap
7. Detail pernikahan
8. Saksi-saksi
9. Closing
10. Signatory

---

### 10. SURAT KETERANGAN PEMAKAMAN

**Nomor:** Nomor : 460.3/ / / KS / 2025

**Field yang Diisi:**
- Nama Almarhum: `APEP RAHAYU`
- Alamat: `Karajan Timur Rt. 001 Rw. 005...`

**Hari Meninggal:**
- Hari: `Jum'at`
- Tanggal: `23 Juni 2025`
- Jam: `07.55 Wib`
- Tempat: `Di Rumah Sakit Bayukarta`

**Hari Disemayamkan:**
- Hari: `Jum'at`
- Tanggal: `23 Juni 2023` [NOTE: Mungkin typo di template, harus 2025]
- Tempat: `Pemakaman Umum Askid Rt. 03 Rw.06 Kasomalang Kulon`

**Struktur:**
1. Header
2. Judul: SURAT KETERANGAN PEMAKAMAN
3. Nomor Surat
4. Opening: Kepala Desa menerangkan
5. Data almarhum (Nama, Alamat)
6. Hari Meninggal (Hari, Tanggal, Jam, Tempat)
7. Hari Disemayamkan (Hari, Tanggal, Tempat)
8. Closing
9. Signatory: a.n Kepala Desa / Sekretaris Desa

---

### 11. SURAT PERNYATAAN TIDAK MEMILIKI RUMAH

**Nomor:** Nomor : 141 / / Pem / 2025

**PERNYATAAN 1: Tidak Memiliki Rumah**

**Field:**
- NIK: `3213262304800001`
- Nama: `DENA WAHYUDIN`
- TTL: `Bandung, 23-04-1980`
- Pekerjaan: `Wiraswasta`
- Alamat: `Rt. 02 Rw. 07 Desa Kasomalang Kulon...`

**PERNYATAAN 2: Terkait Akta Kelahiran**

**Field:**
- NIK: `3213260401130013`
- Nama: `M DAPA`
- TTL: `Subang, 04-01-2013`
- Nama Ayah: `CECE KURNIAWAN`
- Nama Ibu: `UPIT FITRIYANI`
- Alamat: `Kp. Sukajaya Rt. 01 Rw. 05...`

**Catatan:** Dokumen berisi 2 pernyataan dengan tujuan berbeda.

**Struktur:**
1. Judul Pernyataan
2. Nomor Surat
3. Opening declaration
4. Data diri lengkap
5. Statement khusus
6. Closing: "Demikian surat keterangan ini kami berikan..."
7. Signatory: Sekretaris Desa

---

### 12. SURAT UNDANGAN (Resmi dari Kepala Desa)

**Format:** Surat resmi standar (bukan khusus nomor)

**Field yang Diisi:**
- Nomor: [Reference number]
- Sifat: `Biasa` (atau Segera, Rahasia)
- Lampiran: [Attached documents]
- Hal (Subject): `Undangan`
- Tujuan (Recipient): `Yth. [Title/Name]`
- Alamat Tujuan: [Address]
- Hari/Tanggal: [Date]
- Waktu: [Time]
- Tempat: [Location]
- Acara: `Musyawarah pencermatan RKPDesa Tahun 2025`

**Struktur:**
1. Tanggal & Lokasi
2. Nomor, Sifat, Lampiran
3. Hal (Subject)
4. Recipient (Yth. ...)
5. Recipient Address
6. Salutation
7. Event Details (Hari/Tanggal, Waktu, Tempat, Acara)
8. Closing: "Demikian kami sampaikan, atas perkenan..."
9. Distribution (Tembusan): Camat, Ketua BPD, Arsip
10. Signatory: KEPALA DESA KASOMALANG KULON / H. AMIRUDIN, S.Pd.I.

---

## POLA UMUM & STANDARDISASI

### Format Nomor Surat (Master List)
```
Kategori PEM (Keterangan Administrasi):
- XXX / / PEM / TAHUN       (Pisah KK, SKT Tanah, Beda Luas, Domisili, Pernyataan)

Kategori Ks/KS (Kemiskinan & Khusus):
- XXX / XX / Ks / TAHUN      (Surat Miskin)
- XXX / / Ks / TAHUN         (Nikah Tidak Tercatat)
- 460.3 / / / KS / TAHUN    (Pemakaman)
```

### Personal Data Fields (Standar)
Hampir semua surat membutuhkan:
1. **NIK** - 16 digit format: `3213XXXXXXXXXXX`
2. **Nama Lengkap** - Uppercase
3. **Tempat, Tanggal Lahir** - Format: `Kota, DD-MM-YYYY`
4. **Pekerjaan** - Text description
5. **Alamat** - Format: `Kp/RT. XX Rw. XX Desa Kasomalang Kulon Kecamatan Kasomalang Kabupaten Subang`

### Signatory (Penandatangan)
- **Default:** Sekretaris Desa (atas nama Kepala Desa)
- **Name:** APIPUDIN, S.IP
- **Alternative:** Kepala Desa H. AMIRUDIN, S.Pd.I (untuk dokumen khusus)

### Timestamp Format
```
[Lokasi], [Hari] [Tanggal] [Bulan] [Tahun]
Contoh: Kasomalang Kulon, 06 Juli 2026
```

### Surat dengan Tabel
1. **SURAT KETERANGAN AHLI WARIS** - Tabel Ahli Waris & Saksi
   - Kolom: No | Nama | Hubungan Keluarga | Tanda Tangan

2. **SURAT KETERANGAN MISKIN (Daftar)** - Tabel Warga Miskin
   - Kolom: NIK | NAMA | TEMPAT,TGL LAHIR | ALAMAT

---

## IMPLEMENTASI UNTUK KODE

### Struktur Data untuk Setiap Surat

```json
{
  "letter_type": "PISAH_KK",
  "title": "SURAT KETERANGAN PISAH KK",
  "number_format": "XXX / / PEM / TAHUN",
  "requires_table": false,
  "fields": [
    {
      "field_name": "nik_1",
      "label": "NIK Pemisah 1",
      "type": "text",
      "validation": "digits:16",
      "required": true
    },
    {
      "field_name": "nama_1",
      "label": "Nama Pemisah 1",
      "type": "text",
      "required": true
    }
    // ... more fields
  ],
  "signature": {
    "signatory": "Sekretaris Desa",
    "name": "APIPUDIN, S.IP",
    "title": "Sekretaris Desa"
  }
}
```

### Validasi Input
- **NIK:** 16 digits, format `XXXXXXXXXXX`
- **Nama:** Uppercase, 3-100 characters
- **TTL:** Format `Kota, DD-MM-YYYY`
- **Nomor Surat:** Format sesuai kategori (PEM, Ks, KS)

### Template Sections
Setiap surat memiliki:
1. **Header** - Sama untuk semua (PEMERINTAH... DESA...)
2. **Title** - Unik per surat
3. **Number** - Format sesuai kategori
4. **Body** - Field-field yang diisi
5. **Signature** - Officer + Name
6. **Footer** - Timestamp + Lokasi

---

## KESIMPULAN

File JSON lengkap tersimpan di: **SURAT_DOCX_ANALYSIS.json**

Semua 12 surat sudah dianalisis dan didokumentasikan dengan:
- ✅ Nama & deskripsi surat
- ✅ Format nomor surat
- ✅ Kop/header surat
- ✅ Semua field yang perlu diisi
- ✅ Contoh data nyata
- ✅ Struktur body surat
- ✅ Bagian penandatangan
- ✅ Format timestamp/lokasi

Data siap untuk digunakan dalam pengembangan sistem pembuat surat otomatis.
