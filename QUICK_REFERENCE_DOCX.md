# QUICK REFERENCE - 12 DOCX SURAT KETERANGAN DESA

## Tabel Perbandingan Cepat

| Surat | File | Format No. | Signatory | Field Utama | Type |
|-------|------|-----------|-----------|------------|------|
| 1 | PISAH KK | XXX//PEM/TH | Sekretaris | NIK 1-2, Nama, KK Lama | Individu |
| 2 | SKT DAYAT | XXX//PEM/TH | Kepala | Nama, NIK, TTL, Luas, Batas | Individu |
| 3 | BEDA NAMA | XXX//PEM/TH | Sekretaris | Sertifikat vs SPPT (8 field) | Perbandingan |
| 4 | AHLI WARIS | Variabel | Kepala | Nama Almarhum, Ahli Waris (Tabel) | Tabel |
| 5 | BEDA LUAS REAL | No: XXX//PEM/TH | Sekretaris | Sertifikat vs SPPT (8 field) | Perbandingan |
| 6 | DOMISILI | XXX/XX/Pem/TH | Sekretaris | NIK, Nama, TTL, Alamat Lama, Keperluan | Individu |
| 7 | MISKIN SEKOLAH | XXX/XX/Ks/TH | Sekretaris | NIK, Nama, TTL, Alamat, Sekolah | Individu |
| 8 | MISKIN DAFTAR | XXX/XX/Ks/TH | Sekretaris | Tabel: NIK, Nama, TTL, Alamat | Tabel |
| 9 | NIKAH TDK TERCATAT | XXX//Ks/TH | Kepala | Suami: NIK, Nama, TTL, Istri: NIK, Nama, TTL | Pasangan |
| 10 | PEMAKAMAN | 460.3///KS/TH | Sekretaris | Nama Almarhum, Tgl Meninggal, Jam, Tempat | Individu |
| 11 | TDK PUNYA RUMAH | XXX/XX/Pem/TH | Sekretaris | NIK, Nama, TTL, Pekerjaan, Alamat | Individu |
| 12 | UNDANGAN | Standar | Kepala | Nomor, Hal, Tujuan, Tanggal, Waktu, Tempat, Acara | Resmi |

---

## Kategori Pengelompokan

### A. Surat Individu (6 jenis)
Mengisi data satu orang:
1. PISAH KK - untuk 2 orang tapi terpisah
2. SKT DAYAT - Pemilik tanah
3. DOMISILI - Perubahan alamat
4. MISKIN SEKOLAH - Siswa/Calon siswa
5. PEMAKAMAN - Almarhum
6. UNDANGAN - Dari kepala desa ke penerima

### B. Surat Perbandingan (2 jenis)
Membandingkan dokumen:
1. BEDA NAMA - Sertifikat vs SPPT (nama & luas berbeda)
2. BEDA LUAS REAL - Sertifikat vs SPPT (luas berbeda)

### C. Surat Tabel (2 jenis)
Format tabel dengan multiple rows:
1. AHLI WARIS - List ahli waris + saksi
2. MISKIN DAFTAR - List warga miskin per RW

### D. Surat Khusus (2 jenis)
1. NIKAH TDK TERCATAT - Data suami + istri
2. PERNYATAAN TDK PUNYA RUMAH - 2 pernyataan berbeda

---

## Field Umum yang SELALU Ada

```
Header (SAMA untuk semua):
✓ PEMERINTAH DAERAH KABUPATEN SUBANG
✓ KECAMATAN KASOMALANG
✓ DESA KASOMALANG KULON
✓ Alamat: Jln. Raya Kasomalang No.01 Subang 41281

Personal Data (ADA di 8 surat):
✓ NIK (16 digit)
✓ Nama Lengkap
✓ TTL (Tempat, DD-MM-YYYY)
✓ Pekerjaan
✓ Alamat

Signature (SEMUA surat):
✓ Signatory: Kepala Desa / Sekretaris Desa
✓ Tanda Tangan (space)
✓ Nama: APIPUDIN, S.IP atau H. AMIRUDIN, S.Pd.I
✓ Titulus: [Jabatan]

Footer (SEMUA):
✓ Lokasi, [Hari] [Tanggal] [Bulan] [Tahun]
```

---

## Format Nomor Surat - Cheat Sheet

```
TIPE "PEM" (Keterangan Administrasi Umum):
- XXX / / PEM / TAHUN
- Contoh: 474 / / PEM / 2026
- Surat: Pisah KK, SKT Tanah, Beda Nama, Beda Luas Real, Domisili, Pernyataan

TIPE "Ks/KS" (Kemiskinan, Khusus):
- XXX/XX/Ks/TAHUN  (Miskin Sekolah, Daftar Miskin)
- XXX//Ks/TAHUN    (Nikah Tidak Tercatat)
- 460.3///KS/TAHUN (Pemakaman - FIXED format)
- Contoh: 460/25/Ks/2025 atau 474//Ks/2025

TIPE "Pem/PEM" (Alternatif dengan XX di tengah):
- XXX/XX/Pem/TAHUN
- Contoh: 140/25/Pem/2026
- Surat: Domisili, Pernyataan

TIPE "No :" (Variasi penulisan):
- No : 141 / / PEM / 2025 (ada prefix "No :")
- Nomor : 140 / 25 / Pem / 2026 (ada prefix "Nomor :")

TIPE UNDANGAN (Standar Resmi):
- Nomor: [Reference]
- Sifat: [Biasa/Segera/Rahasia]
- Lampiran: [Items]
- Hal: [Subject]
```

---

## Jenis Data Field

### Text Fields
- Nama, Alamat, Pekerjaan, Jabatan, Agama, Bin, dll

### Number Fields
- NIK (16 digit), Luas (m²), Harga (IDR), Nomor Sertipikat

### Date Fields
- TTL (Tempat, DD-MM-YYYY)
- Tanggal Surat (DD Bulan YYYY)

### Time Fields
- Jam Meninggal (HH.MM Wib)

### Selection Fields
- Sifat Surat: [Biasa | Segera | Rahasia]
- Pekerjaan: [Petani | PNS | Wiraswasta | Pelajar | dll]
- Agama: [Islam | Kristen | Katholik | Hindu | Budha]

### Signature Fields
- Tanda Tangan (Space untuk tanda tangan)
- TTD inisial/cap kepala desa

### Table Fields
- Row data (NIK | Nama | TTL | Alamat) untuk AHLI WARIS & MISKIN

---

## Validasi Input

```
NIK:
- Format: 16 digit numeric
- Regex: ^\d{16}$
- Contoh: 3213266804990001

Nama:
- Format: Uppercase, 3-100 chars
- No special characters kecuali space, dash, dot
- Contoh: SUMARNI APRIANTINI

TTL:
- Format: [KOTA], [DD]-[MM]-[YYYY]
- Contoh: Subang, 27-04-1997
- Bulan: 01-12, Hari: 01-31

Alamat:
- Min 10 chars, Max 200 chars
- Format: Kp. [RT/RW] [Desa] [Kecamatan] [Kabupaten]

Nomor Surat:
- XXX (001-999) / XX atau blank / [Category] / TAHUN
- Category: PEM, Ks, KS, Pem
- TAHUN: 2024-2030+

Luas (m²):
- Numeric, 1-99999
- Contoh: 126, 153, 173

Harga (IDR):
- Numeric, format: XXX.XXX.XXX (dengan delimiter)
- Contoh: 100.000.000, 50.000.000
```

---

## Signatory Reference

| Surat | Signatory | Nama | Gelar |
|-------|-----------|------|-------|
| 1-12 (default) | Sekretaris Desa | APIPUDIN | S.IP atau S.IP. |
| 2, 4, 9, 12 | Kepala Desa | H. AMIRUDIN | S.Pd.I atau S.Pd.I. |
| 2 | Camat | [Blank/TBD] | - |

---

## Checklist Implementasi Kode

### Data Model Required
- [ ] LetterType enum (12 tipe)
- [ ] PersonalData struct (NIK, Nama, TTL, Pekerjaan, Alamat)
- [ ] LetterField class (name, type, validation, required)
- [ ] SignatureData struct (signatory, name, title)

### Validation Rules
- [ ] NIK: 16 digits
- [ ] Nama: 3-100 chars, uppercase
- [ ] TTL: Format validation
- [ ] Nomor: Format sesuai kategori
- [ ] Luas: Numeric validation

### Template Sections
- [ ] StandardHeader
- [ ] LetterTitle
- [ ] LetterNumber
- [ ] Body (with field mapping)
- [ ] SignatureBlock
- [ ] Footer (timestamp + lokasi)

### Output Generation
- [ ] DOCX export
- [ ] PDF export (jika diperlukan)
- [ ] Preview mode
- [ ] Print formatting

---

## File References

**Full Documentation:** `SURAT_DOCX_ANALYSIS.json`
- Complete structure for all 12 letters
- Field details, examples, validation rules

**Summary Guide:** `ANALISIS_DOCX_SUMMARY.md`
- Detailed breakdown per surat
- Body structure explanation
- Common patterns

**Quick Reference:** `QUICK_REFERENCE_DOCX.md` (ini)
- Quick lookup tables
- Fast categorization
- Implementation checklist
