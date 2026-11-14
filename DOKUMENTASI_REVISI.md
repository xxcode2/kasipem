# 📋 KASIPEM - Dokumentasi Revisi Sesuai Format Dokumen Asli

## 🔄 Status: REVISI BERDASARKAN ANALISIS FILE ASLI

Setelah mempelajari file-file contoh surat dari `c:\Users\irgir\Downloads\kasipem\file\`, saya telah melakukan revisi menyeluruh untuk memastikan semua surat sesuai dengan format dokumen asli.

---

## 📂 File-File Contoh yang Dianalisis

✅ **SURAT KETERANGAN DOMISILI.docx**
✅ **SURAT KETERANGAN MISKIN.docx** (SKTM)
✅ **SURAT KETERANGAN NIKAH TIDAK TERCATAT new.docx** (Belum Menikah)
✅ **SURAT KETERANGAN BEDA LUAS REAL.docx** (Beda Luas Tanah)
✅ **SURAT UNDANGAN YG DI TTD KEPALA DESA.docx** (Undangan)

---

## 🔍 TEMUAN UTAMA DARI ANALISIS

### 1. **Struktur KOP SURAT**
```
┌─────────────────────────────────────────────────┐
│  PEMERINTAH [KABUPATEN]                        │
│  [KECAMATAN]                                    │
│  DESA/KELURAHAN [NAMA DESA]                     │
│  [ALAMAT KANTOR]                                │
├═════════════════════════════════════════════════┤  (Garis ganda: tebal + tipis)
```

**Perubahan dari v1:**
- ✅ Margin top dikecilkan dari 20mm ke 15mm
- ✅ Header box dengan border 0.5pt
- ✅ Garis pemisah ganda (0.8pt + 0.3pt)

### 2. **FORMAT NOMOR SURAT**
Sesuai dengan standar pemerintah Indonesia:
```
[URUT]/[KOMUNIKASI]/[TIPE]/[TAHUN]

Contoh:
- Domisili:          1/......../Pem/2026
- Belum Menikah:     1/......../Ks/2026
- Tidak Mampu:       1/......../Ks/2026
- Beda Luas Tanah:   1/......../PEM/2026
- Undangan:          1/......../PEM/2026
```

**Keterangan:**
- **Pem** = Pemerintahan (Surat Domisili, Beda Luas Tanah, Undangan)
- **Ks** = Keterangan Sosial (Belum Menikah, Tidak Mampu)
- Format: `N/......../[TIPE]/TAHUN` (titik untuk komunikasi/internal)

### 3. **FORMAT TANGGAL**
Dua format berbeda digunakan:

**TTL (Tempat Tanggal Lahir):** `DD-MM-YYYY`
- Contoh: 12-05-1994

**Surat (Diterbitkan di):** `DD MMMM YYYY`
- Contoh: 06 Juli 2026

**Perubahan dari v1:**
- ✅ Fungsi terpisah: `formatDateTTL()` dan `formatDateSurat()`
- ✅ Lebih akurat sesuai standar Indonesia

### 4. **STRUKTUR ISI SURAT**
Semua surat mengikuti pola:
1. KOP SURAT
2. JUDUL (bold, underline)
3. NOMOR SURAT
4. PEMBUKA (formal)
5. DATA PEMOHON (tabel-like format dengan spacing konsisten)
6. PERNYATAAN UTAMA (inti surat)
7. PENUTUP (Demikian surat ini dibuat...)
8. TANDA TANGAN (A.n. / Langsung + Ruang TTD + Nama Bold Underline + NIP)

### 5. **TANDA TANGAN**
Format dua kolom:

```
MENGETAHUI,                          [DESA], [TANGGAL]
Ketua RW                             A.n. Kepala Desa
________________                     ________________
(18 pt space)                        (18 pt space)
                                     NAMA PEJABAT
                                     ─────────────────
                                     NIP: XXXXXXX
```

**Perubahan dari v1:**
- ✅ Format dua kolom (Mengetahui + Pejabat Utama)
- ✅ Garis bawah nama (underline konsisten)
- ✅ Ruang tanda tangan lebih jelas (18pt)

---

## 🔄 PERUBAHAN DETAIL PER JENIS SURAT

### 1️⃣ SURAT KETERANGAN DOMISILI

**Data Fields (sesuai DOCX):**
```
Nama                      : [nama lengkap]
NIK                       : [16 digit]
Jenis Kelamin             : [Laki-laki/Perempuan] (OPSIONAL)
Tempat/Tanggal Lahir      : [tempat], [DD-MM-YYYY] (OPSIONAL)
Pekerjaan                 : [pekerjaan] (OPSIONAL)
Alamat di KTP             : [alamat lengkap]
RT/RW                     : [XX]/[XX]
Alamat Domisili Saat Ini  : [alamat baru]
```

**Pernyataan Inti:**
```
"Berdasarkan pengecekan lapangan dan arsip desa bahwa yang tersebut di atas 
benar adalah penduduk sah dan bertempat tinggal di Desa/Kelurahan [NAMA DESA]. 
Surat keterangan ini diberikan untuk keperluan: [KEPERLUAN]"
```

**Pembaruan di v2:**
- ✅ Data fields dengan spacing konsisten
- ✅ Format pernyataan sesuai dokumen asli
- ✅ Nomor surat: `N/......../Pem/TAHUN`

---

### 2️⃣ SURAT KETERANGAN BELUM MENIKAH

**Data Fields:**
```
Nama                      : [nama lengkap]
NIK                       : [16 digit]
Jenis Kelamin             : [Laki-laki/Perempuan] (OPSIONAL)
Tempat/Tanggal Lahir      : [tempat], [DD-MM-YYYY] (OPSIONAL)
Pekerjaan                 : [pekerjaan] (OPSIONAL)
Alamat                    : [alamat lengkap]
RT/RW                     : [XX]/[XX]
```

**Pernyataan Inti:**
```
"Adalah benar bahwa yang bersangkutan sampai dengan saat ini belum pernah 
melakukan perkawinan yang tercatat pada kantor pencatat perkawinan/pelaporan 
sipil yang berwenang. Surat keterangan ini diberikan untuk keperluan: [KEPERLUAN]"
```

**Nomor Surat:** `N/......../Ks/TAHUN`

---

### 3️⃣ SURAT KETERANGAN TIDAK MAMPU (SKTM)

**Data Fields:**
```
Nama                      : [nama lengkap]
NIK                       : [16 digit]
Jenis Kelamin             : [Laki-laki/Perempuan] (OPSIONAL)
Tempat/Tanggal Lahir      : [tempat], [DD-MM-YYYY] (OPSIONAL)
Pekerjaan                 : [pekerjaan] (OPSIONAL)
Alamat                    : [alamat lengkap]
RT/RW                     : [XX]/[XX]
Sekolah/Instansi          : [nama sekolah/instansi]
```

**Pernyataan Inti:**
```
"Adalah benar bahwa yang bersangkutan termasuk dalam kategori tidak mampu 
menurut kriteria yang berlaku di Desa/Kelurahan [NAMA DESA]. Surat keterangan 
ini diberikan untuk keperluan: [KEPERLUAN] di [SEKOLAH/INSTANSI]"
```

**Nomor Surat:** `N/......../Ks/TAHUN`

---

### 4️⃣ SURAT KETERANGAN BEDA LUAS TANAH

**Data Fields:**
```
Nama                      : [nama lengkap]
NIK                       : [16 digit]
Alamat                    : [alamat lengkap]
RT/RW                     : [XX]/[XX]
Nomor Sertifikat          : [nomor sertifikat]
Luas Sertifikat           : [luas] m²
Nomor SPPT                : [nomor SPPT]
Luas SPPT                 : [luas] m²
```

**Pernyataan Inti:**
```
"Berdasarkan pengecekan di lapangan dan data yang ada di kantor Desa/Kelurahan, 
terdapat perbedaan luas tanah antara Sertifikat dan SPPT (Surat Pemberitahuan 
Pajak Tahunan), dengan rincian sebagai berikut: [DATA]"

"Surat keterangan ini diberikan untuk keperluan administrasi pertanahan dan dapat 
digunakan sebagai dasar untuk melakukan perubahan data perpajakan tanah sesuai 
dengan peraturan yang berlaku."
```

**Nomor Surat:** `N/......../PEM/TAHUN`

---

### 5️⃣ SURAT UNDANGAN RESMI

**Data Fields:**
```
Sifat           : Penting
Lampiran        : -
Perihal         : Undangan Acara
Kepada Yth.     : [nama dan jabatan]
Hari/Tanggal    : [hari], [tanggal lengkap]
Pukul           : [pukul] WIB
Tempat          : [lokasi acara]
Agenda          : [rincian acara]
```

**Pernyataan:**
```
"Dengan hormat, kami mengundang Bapak/Ibu untuk hadir dalam acara berikut:"

[Data acara]

"Atas perhatian dan kehadiran Bapak/Ibu diucapkan terima kasih."
```

**Nomor Surat:** `N/......../PEM/TAHUN`

**Catatan:** Hanya ditandatangani oleh Kepala Desa (tidak perlu Mengetahui)

---

## 💾 FILE YANG DIPERBARUI

### ✅ **Web App (Next.js)**

**File Baru:**
- `web-app/src/lib/pdf-generator-v2.ts` - Generator PDF yang sesuai format asli

**Fitur Utama v2:**
```typescript
// Format tanggal berbeda
formatDateTTL(date)    // DD-MM-YYYY untuk TTL
formatDateSurat(date)  // DD MMMM YYYY untuk tanggal surat

// Header KOP yang profesional
addHeaderKopSurat()    // Dengan border dan garis ganda

// Judul dengan garis bawah
addJudulSurat()        // Centered, bold, underlined

// Data fields terformat
addDataField()         // Label : Value (spacing konsisten)

// Blok tanda tangan dua kolom
addSignatureBlock()    // Mengetahui + Pejabat + TTD
```

### 📝 **Android App (Kotlin)**

**Status:** Ready untuk diupdate dengan format yang sama
- File: `android-app/app/src/main/java/com/kasipem/data/PdfGenerator.kt`
- Akan menggunakan iText dengan format yang identik dengan versi Web

---

## 🔄 CARA MENGGUNAKAN FILE BARU

### Di Web App

**Sebelum (v1):**
```typescript
import { generatePdf } from '@/lib/pdf-generator';
```

**Sesudah (v2):**
```typescript
import { generatePdf } from '@/lib/pdf-generator-v2';
```

**Langkah Update:**
1. Copy `pdf-generator-v2.ts` ke `pdf-generator.ts` (atau gunakan import v2)
2. Update import di `LetterPreview.tsx`
3. Test dengan data sample dari docx asli
4. Verifikasi output PDF dengan membandingkan dengan file docx

---

## ✅ VALIDATION CHECKLIST

Sebelum production, verifikasi:

- [ ] KOP SURAT tampil dengan border dan garis ganda
- [ ] JUDUL surat bold dan underline
- [ ] NOMOR SURAT format: `N/......../[TIPE]/TAHUN`
- [ ] DATA FIELDS spacing konsisten (2-3pt antar baris)
- [ ] TTL format DD-MM-YYYY (contoh: 12-05-1994)
- [ ] Tanggal surat format DD MMMM YYYY (contoh: 06 Juli 2026)
- [ ] TANDA TANGAN format dua kolom
- [ ] Nama pejabat bold dan underline
- [ ] NIP tertampil dengan format `NIP: XXXXXXX`
- [ ] Pernyataan inti sesuai teks dari docx asli
- [ ] PDF ukuran A4 portrait
- [ ] Print preview OK tanpa pemotongan

---

## 📊 PERBANDINGAN v1 vs v2

| Aspek | v1 | v2 |
|-------|-----|-----|
| **KOP SURAT** | Sederhana | Border + Garis Ganda |
| **Nomor Surat** | Random/Default | Format Standar Pemerintah |
| **Format Tanggal** | Unified | Terpisah (TTL vs Surat) |
| **Data Fields** | Basic | Terformat konsisten |
| **Tanda Tangan** | Satu kolom | Dua kolom (Mengetahui+Pejabat) |
| **Pernyataan** | Generic | Spesifik per jenis surat |
| **Kualitas** | Draft | Professional/Production |

---

## 🚀 NEXT STEPS

1. **Test PDF Generator v2**
   - Jalankan dengan sample data
   - Bandingkan output dengan file DOCX asli

2. **Update Android App**
   - Implementasi format yang sama di iText
   - Test di emulator dan device

3. **Update Form Inputs**
   - Ensure semua field required/optional sesuai dokumen
   - Add validation rules

4. **Dokumentasi**
   - Update README dengan informasi format baru
   - Tambahkan contoh data real

5. **Testing Menyeluruh**
   - Manual testing semua 5 jenis surat
   - Print testing di berbagai printer
   - Compare dengan dokumen asli

---

## 📞 NOTES

- Semua format disesuaikan dengan standar surat dinas Indonesia yang berlaku
- File DOCX asli tersimpan di `c:\Users\irgir\Downloads\kasipem\file\`
- Dokumentasi lengkap dari sub-agent tersimpan di folder yang sama
- Production deployment siap setelah semua testing selesai

---

**Status:** ✅ READY FOR IMPLEMENTATION & TESTING

**Version:** 2.0.0 (Aligned with Real Document Format)

**Date:** 2024
