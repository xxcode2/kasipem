# KASIPEM - Pembuat Surat Administrasi Desa/Kelurahan Stateless

KASIPEM adalah aplikasi modern untuk membuat berbagai jenis surat administrasi desa/kelurahan secara cepat, mudah, dan aman. Tersedia dalam dua versi: **Web (React/Next.js)** dan **Mobile (Android Native)**.

## 🎯 Fitur Utama

✅ **Stateless & Sekali Pakai** - Tidak menggunakan database, tanpa login/register, semua diproses di sisi klien  
✅ **30 Jenis Surat & Formulir** - 12 surat desa (Domisili, Pisah KK, Keterangan Tanah (SKT Dayat), Beda Luas Tanah, Beda Nama, Belum Menikah, Tidak Mampu (SKTM), Ahli Waris, Nikah Tidak Tercatat, Pemakaman, Tidak Memiliki Rumah, Undangan) + 3 surat desa baru (Kematian, Usaha, Duda/Janda) + 10 formulir Dukcapil seri F (F-1.01, F-1.02, F-1.03, F-1.04, F-1.05, F-1.06, F-1.15, F-2.01, F-2.03, F-2.04) + 2 SPTJM (Kematian, Perkawinan/Perceraian) + 3 dokumen PBI-JKN (Asesmen, Surat Pernyataan, Tidak Memiliki Rekening). Varian: Domisili Lembaga/Perusahaan dan SKTM Sekolah/Daftar Kolektif sebagai opsi di dalam surat yang sama. F-2.01 hanya menampilkan seksi jenis pelaporan yang dipilih.  
✅ **Riwayat Surat (lokal)** - Setiap surat yang diunduh/dicetak tercatat di perangkat (tanggal, nomor, jenis, nama pemohon); bisa dilihat & dihapus. Pengganti macro Arsip di aplikasi Excel lama. Tanpa server, tanpa data warga yang dimuat sebelumnya.
✅ **Form Dinamis** - Kolom input berubah sesuai jenis surat yang dipilih  
✅ **Export & Cetak** - Unduh PDF, DOCX, atau cetak langsung  
✅ **Data Lokal** - Simpan kop surat dan pejabat di device (tidak ke server)  
✅ **Kop Surat Asli** - Logo Garuda + logo Kabupaten Subang dari folder `kop/` tampil di preview, PDF, dan cetak  
✅ **Pejabat Default** - Kepala Desa H. AMIRUDIN, S.Pd.I; Sekretaris Desa APIPUDIN, S.IP (Desa Kasomalang Kulon, Kec. Kasomalang, Kab. Subang). Tidak ada NIP — baris NIP tidak ditampilkan.  
✅ **UI/UX Modern** - Desain tenang (latar stone/slate, aksen teal tua), grid kartu surat + modal form dan preview A4, responsif  
✅ **No Data Leak** - Semua data hanya ada di perangkat Anda  

---

## 📁 Struktur Proyek

```
kasipem/
├── web-app/                    # Aplikasi Web (Next.js + React)
│   ├── package.json
│   ├── tsconfig.json
│   ├── next.config.js
│   ├── tailwind.config.ts
│   ├── postcss.config.js
│   ├── src/
│   │   ├── app/
│   │   │   ├── page.tsx        # Halaman utama
│   │   │   ├── layout.tsx      # Layout root
│   │   │   └── globals.css     # CSS global
│   │   ├── components/
│   │   │   ├── LetterTypeSelector.tsx  # Grid 30 kartu + cari/filter
│   │   │   ├── LetterForm.tsx        # Modal form per surat (field kondisional + tabel)
│   │   │   ├── LetterBody.tsx        # Renderer isi surat (preview; kop desa/Disdukcapil + tabel)
│   │   │   ├── LetterPreview.tsx     # Modal preview A4 + Unduh PDF / Cetak (+ catat riwayat)
│   │   │   ├── RiwayatSurat.tsx      # Modal riwayat surat lokal (localStorage)
│   │   │   └── HeaderForm.tsx        # Kop + pejabat (Kades & Sekdes)
│   │   ├── lib/
│   │   │   ├── store.ts             # Zustand state management
│   │   │   ├── letter-definitions.ts # Metadata + field kanon 30 surat/formulir
│   │   │   ├── letter-content.ts     # Isi surat (dipakai preview DAN PDF)
│   │   │   ├── history.ts            # Riwayat Surat lokal (localStorage)
│   │   │   └── pdf-generator.ts      # Satu-satunya generator PDF (jsPDF, 30 surat/formulir)
│   │   ├── types/
│   │   │   └── index.ts        # TypeScript types
│   │   └── public/
│   └── .gitignore
│
├── android-app/                # Aplikasi Android (Kotlin + Jetpack Compose)
│   ├── build.gradle.kts        # Root build
│   ├── settings.gradle.kts
│   ├── app/
│   │   ├── build.gradle.kts
│   │   ├── AndroidManifest.xml
│   │   ├── src/main/
│   │   │   ├── java/com/kasipem/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── Models.kt        # Data classes
│   │   │   │   │   ├── LetterStore.kt  # DataStore persistence
│   │   │   │   │   └── PdfGenerator.kt # iText PDF generation
│   │   │   │   └── ui/
│   │   │   │       ├── theme/
│   │   │   │       │   ├── Theme.kt
│   │   │   │       │   └── Typography.kt
│   │   │   │       ├── components/
│   │   │   │       │   ├── LetterTypeCard.kt
│   │   │   │       │   └── FormField.kt
│   │   │   │       └── screens/
│   │   │   │           ├── HomeScreen.kt
│   │   │   │           ├── Forms.kt
│   │   │   │           ├── SettingsScreen.kt
│   │   │   │           └── PreviewScreen.kt
│   │   │   └── res/
│   │   └── build/
│   └── gradle/
│
└── README.md
```

---

## 🚀 Quick Start

### **Versi Web (Next.js)**

#### Prasyarat
- Node.js 18+ dan npm/yarn
- Git

#### Setup & Jalankan

```bash
cd web-app

# Install dependencies
npm install

# Development server
npm run dev
# Akses: http://localhost:3000

# Production build
npm run build
npm start
```

**Dependencies Utama:**
- `next@14` - React framework
- `react@18` - UI library
- `framer-motion@10` - Animasi
- `tailwindcss@3` - Styling
- `jspdf@2.5` - PDF generation
- `zustand@4` - State management
- `lucide-react@0.292` - Icons
- `date-fns@2.30` - Date utilities

---

### **Versi Android (Kotlin)**

#### Prasyarat
- Android Studio 2023.1+
- Android SDK 34 (minimum API 24)
- Kotlin 1.9.10+

#### Setup & Jalankan

```bash
cd android-app

# Buka di Android Studio
# File > Open > pilih folder `android-app`

# Atau via command line
./gradlew build

# Deploy ke emulator/device
./gradlew installDebug
```

**Dependencies Utama:**
- `androidx.compose` - UI Framework (Material 3)
- `androidx.lifecycle` - State management
- `androidx.datastore` - Local persistence
- `androidx.navigation` - Navigation
- `com.itextpdf:itextg@5.5` - PDF generation
- `com.kizitonwose:calendar-compose` - Calendar

---

## 📋 Jenis Surat & Form Dinamis

Ada **30 jenis surat/formulir**. Nomor surat bisa diedit per surat (default per pola di bawah, tahun otomatis). Penandatangan: Kepala Desa, kecuali Pisah KK dan Kematian (a.n. Kepala Desa oleh Sekretaris Desa), Usaha dan Duda/Janda (Sekretaris Desa), SKTM (ditambah baris "Mengetahui, Ketua RW"), dan formulir/SPTJM (pemohon / yang menyatakan, sesuai template). Formulir Dukcapil seri F memakai kop teks Dinas Kependudukan dan Pencatatan Sipil Kabupaten Subang (tanpa logo desa) dan tidak bernomor surat.

### 1. **Surat Keterangan Domisili** (Nomor: 474/.../PEM/tahun)
**Tujuan:** Verifikasi tempat tinggal untuk keperluan administrasi

**Field:** Nama Lengkap*, NIK*, Jenis Kelamin, Tempat/Tanggal Lahir, Pekerjaan, Alamat Asal (KTP)*, RT*, RW*, Alamat Domisili Saat Ini*, Keperluan Surat*

---

### 2. **Surat Keterangan Pisah KK** (Nomor: 474/.../PEM/tahun)
**Tujuan:** Pemisahan Kartu Keluarga. **Ttd: a.n. Kepala Desa — Sekretaris Desa.**

**Field:** Nama Pemisah Pertama*, NIK*, Nama Pemisah Kedua*, NIK*, Nomor KK Lama*, Alamat*

---

### 3. **Surat Keterangan Tanah / SKT Dayat** (Nomor: 593/.../PEM/tahun)
**Tujuan:** Sertifikat tanah dan properti, termasuk batas-batas tanah

**Field:** Nama Pemilik Tanah*, NIK*, Tempat/Tanggal Lahir, Alamat*, Luas Tanah (m²)*, Luas Bangunan (m²), SPPT/NOP, Batas Utara, Batas Timur, Batas Selatan, Batas Barat

---

### 4. **Surat Keterangan Beda Luas Tanah** (Nomor: 141/.../PEM/tahun)
**Tujuan:** Administrasi pertanahan, perbedaan sertifikat & SPPT

**Field:** Nama Lengkap*, NIK*, Alamat*, RT, RW, Nomor Sertifikat*, Luas Sertifikat (m²)*, Nomor SPPT*, Luas SPPT (m²)*

---

### 5. **Surat Keterangan Beda Nama** (Nomor: 474/.../PEM/tahun)
**Tujuan:** Perbedaan nama di dokumen

**Field:** Nama Saat Ini*, NIK*, Nama di Dokumen Pertama*, Nama di Dokumen Kedua*, Alasan Perbedaan Nama*

---

### 6. **Surat Keterangan Belum Menikah** (Nomor: 460.2/.../Ks/tahun)
**Tujuan:** Administrasi perkawinan dan pendaftaran

**Field:** Nama Lengkap*, NIK*, Jenis Kelamin, Tempat/Tanggal Lahir, Pekerjaan, Alamat*, RT, RW, Keperluan*

---

### 7. **Surat Keterangan Tidak Mampu (SKTM)** (Nomor: 460/.../Ks/tahun)
**Tujuan:** Bantuan sosial, beasiswa, program kesehatan

**Field:** Nama Lengkap*, NIK*, Jenis Kelamin, Tempat/Tanggal Lahir, Pekerjaan, Alamat*, RT, RW, Penghasilan Bulanan, Keperluan*, Tujuan*

---

### 8. **Surat Keterangan Ahli Waris** (Nomor: 474/.../PEM/tahun)
**Tujuan:** Proses kewarisan dan pengurusan harta

**Field:** Nama Peninggal*, NIK Peninggal*, Alamat Peninggal*, Nama Ahli Waris 1*, NIK, Hubungan, Nama Ahli Waris 2, NIK, Hubungan, Keterangan Tambahan

---

### 9. **Surat Nikah Tidak Tercatat** (Nomor: 460.2/.../Ks/tahun)
**Tujuan:** Pengakuan perkawinan tidak tercatat

**Field:** Nama Pria*, NIK Pria*, Tempat/Tanggal Lahir Pria, Nama Wanita*, NIK Wanita*, Tempat/Tanggal Lahir Wanita, Tanggal Pernikahan*, Tempat Pernikahan*

---

### 10. **Surat Keterangan Pemakaman** (Nomor: 460.3/.../KS/tahun)
**Tujuan:** Izin dan proses pemakaman

**Field:** Nama Almarhum/Almarhumah*, NIK Almarhum, Tempat/Tanggal Lahir, Alamat*, Tanggal Meninggal*, Lokasi Makam*, Nama Keluarga yang Memohon*

---

### 11. **Surat Pernyataan Tidak Memiliki Rumah** (Nomor: 474/.../PEM/tahun)
**Tujuan:** Keterangan tidak memiliki properti

**Field:** Nama Lengkap*, NIK*, Tempat/Tanggal Lahir, Pekerjaan, Alamat Tempat Tinggal Saat Ini*, Keterangan Tambahan

---

### 12. **Surat Undangan Resmi** (Nomor: manual/editable)
**Tujuan:** Acara resmi desa/kelurahan. **Ttd: Kepala Desa.**

**Field:** Tujuan Yth.*, Hari*, Tanggal*, Bulan*, Tahun*, Pukul*, Tempat*, Agenda Acara*, Tembusan (opsional)

---

### 13. **Surat Keterangan Kematian** (Nomor: 460.3/.../KS/tahun)
**Tujuan:** Keterangan resmi meninggal dunia, dasar pengurusan akta kematian. **Ttd: a.n. Kepala Desa — Sekretaris Desa.** Catatan: seri 460.3/.../KS/ juga dipakai Pemakaman di template desa — dipertahankan apa adanya, perlu konfirmasi penomoran resmi desa.

**Field:** Nama Almarhum/Almarhumah*, NIK*, Jenis Kelamin, Agama, Tempat/Tanggal Lahir, Pekerjaan, Alamat*, Hari Meninggal*, Tanggal Meninggal*, Tempat Meninggal*, Penyebab Meninggal*, Nama Pelapor*, NIK Pelapor*, Tempat/Tanggal Lahir Pelapor, Pekerjaan Pelapor, Alamat Pelapor*, Hubungan Pelapor*

---

### 14. **Surat Keterangan Usaha (SKU)** (Nomor: 510/.../PEM/tahun)
**Tujuan:** Menerangkan usaha yang dijalankan warga desa. **Ttd: Sekretaris Desa.**

**Field:** Nama Lengkap*, NIK*, Tempat/Tanggal Lahir, Pekerjaan, Alamat*, Nama/Jenis Usaha*, Alamat Tempat Usaha*

---

### 15. **Surat Keterangan Duda/Janda** (Nomor: 474.2/.../KS/tahun)
**Tujuan:** Menerangkan status duda atau janda. **Ttd: Sekretaris Desa.** Judul berubah mengikuti pilihan Duda/Janda.

**Field:** Status (Duda/Janda)*, Nama Lengkap*, NIK*, Tempat/Tanggal Lahir, Pekerjaan, Alamat*, Keterangan Ketua RT

---

### 16. **Formulir F-1.01 — Biodata Keluarga** (Kop Disdukcapil, tanpa nomor)
**Tujuan:** Biodata keluarga: kepala keluarga + daftar anggota keluarga (maks. 10 anggota). **Ttd: Kepala Keluarga + Kepala Dinas Dukcapil Subang.** PDF multi-halaman bila daftar anggota panjang.

**Field:** Nama Kepala Keluarga*, NIK*, Nomor KK*, Alamat Keluarga*, Daftar Anggota Keluarga (tabel: Nama, NIK, SHDK, Tempat/Tgl Lahir, Agama, Pendidikan, Pekerjaan, Status Kawin, Nama Ayah, Nama Ibu)

---

### 17. **Formulir F-1.02 — Pendaftaran Peristiwa Kependudukan** (Kop Disdukcapil, tanpa nomor)
**Tujuan:** Pendaftaran peristiwa kependudukan (KK, KTP-el, KIA, perubahan data).

**Field:** Nama Lengkap*, NIK*, Nomor KK*, Jenis Permohonan* (KK baru / KK perubahan / KK hilang-rusak / KTP-el baru / KTP-el pindah / KTP-el hilang-rusak / Perpanjangan ITAP / KIA / Perubahan data), Daftar Lampiran

---

### 18. **Formulir F-1.03 — Pendaftaran Perpindahan Penduduk** (Kop Disdukcapil, tanpa nomor)
**Tujuan:** Pendaftaran perpindahan penduduk (SKP / SKPLN / SKTT). PDF multi-halaman bila daftar anggota panjang.

**Field:** Nomor KK*, Nama Pemohon*, NIK*, Jenis Permohonan* (SKP/SKPLN/SKTT), Alamat Asal*, RT, RW, Klasifikasi Kepindahan* (dalam desa / antar desa / antar kecamatan / antar kabupaten / antar provinsi), Alamat Pindah*, Alasan Pindah*, Jenis Kepindahan, Daftar Anggota Keluarga yang Pindah (tabel NIK/Nama/SHDK), Anggota Keluarga yang Tidak Pindah

---

### 19. **F-1.04 — Surat Pernyataan Tidak Memiliki Dokumen Kependudukan** (Kop Disdukcapil, tanpa nomor)
**Tujuan:** Pernyataan tidak memiliki dokumen kependudukan (bermaterai).

**Field:** Nama Lengkap*, Alamat*, Tempat/Tanggal Lahir, Nama Ibu*

---

### 20. **F-1.05 — SPTJM Perkawinan Belum Tercatat** (Kop Disdukcapil, tanpa nomor)
**Tujuan:** SPTJM perkawinan belum tercatat. **Ttd: Suami + Istri.**

**Field:** Nama Suami*, NIK Suami*, Nama Istri*, NIK Istri*, Tanggal Perkawinan*, Bulan/Tahun Perkawinan, Nama Saksi 1*, NIK, Nama Saksi 2*, NIK, Daftar Anak (tabel Nama/No. Akta Kelahiran/SHDK)

---

### 21. **F-1.06 — Surat Pernyataan Perubahan Elemen Data Kependudukan** (Kop Disdukcapil, tanpa nomor)
**Tujuan:** Pernyataan perubahan elemen data kependudukan.

**Field:** Nama Lengkap*, NIK*, Nomor KK*, Alamat*, Tabel Anggota Keluarga (maks. 7 baris: Nama, Perubahan Elemen Data), Perubahan Pendidikan (Semula/Menjadi/Dasar Perubahan), Perubahan Pekerjaan (Semula/Menjadi/Dasar Perubahan), Perubahan Agama

---

### 22. **Formulir F-1.15 — Permohonan KK Baru WNI** (Kop Disdukcapil, tanpa nomor)
**Tujuan:** Permohonan Kartu Keluarga baru bagi WNI.

**Field:** Nama Pemohon*, NIK*, Nomor KK Semula, Alamat*, RT, RW, Alasan Permohonan* (Membentuk RT baru / KK hilang-rusak / Lainnya), Jumlah Anggota Keluarga, Daftar Anggota Keluarga (tabel NIK/Nama/SHDK)

---

### 23. **Formulir F-2.01 — Pelaporan Pencatatan Sipil** (Kop Disdukcapil, tanpa nomor)
**Tujuan:** Pelaporan pencatatan sipil di dalam wilayah NKRI. **Hanya seksi jenis pelaporan yang dipilih yang tampil** di form, preview, dan PDF (16 jenis: Kelahiran, Lahir Mati, Perkawinan, Pembatalan Perkawinan, Perceraian, Pembatalan Perceraian, Kematian, Pengangkatan Anak, Pengakuan Anak, Pengesahan Anak, Perubahan Nama, Perubahan Status Kewarganegaraan, Peristiwa Penting Lainnya, Pembetulan Akta, Pembatalan Akta, Pelaporan dari Luar Wilayah NKRI). PDF multi-halaman.

**Field:** Jenis Pelaporan*, Nama Pelapor*, NIK Pelapor*, Alamat Pelapor*, Subjek Akta I/II, Saksi I/II, Orang Tua (Ayah/Ibu) + seksi kondisional (Data Anak / Lahir Mati / Kematian / Data Peristiwa)

---

### 24. **F-2.03 — SPTJM Kebenaran Data Kelahiran** (Kop Disdukcapil, tanpa nomor)
**Tujuan:** SPTJM kebenaran data kelahiran.

**Field:** Nama Yang Menyatakan*, NIK*, Tempat/Tanggal Lahir, Pekerjaan, Alamat*, Anak (Nama*, NIK, Tempat/Tanggal Lahir*, Jenis Kelamin, Anak Ke-, Alamat), Ibu (Nama*, NIK, Tempat/Tanggal Lahir, Pekerjaan, Alamat), Penolong Kelahiran (Nama, NIK, Pekerjaan, Alamat), Saksi 1*, Saksi 2*

---

### 25. **F-2.04 — SPTJM Kebenaran sebagai Pasangan Suami Istri** (Kop Disdukcapil, tanpa nomor)
**Tujuan:** SPTJM kebenaran sebagai pasangan suami istri.

**Field:** Nama Yang Menyatakan*, NIK*, Tempat/Tanggal Lahir, Pekerjaan, Alamat*, Nama Pasangan*, NIK Pasangan*, Tempat/Tanggal Lahir Pasangan, Pekerjaan Pasangan, Alamat Pasangan, Nomor KK, Saksi 1*, Saksi 2*

---

### 26. **SPTJM Kebenaran Data Kematian** (tanpa nomor)
**Tujuan:** SPTJM kebenaran data kematian untuk pengurusan akta kematian (bermaterai). Mengetahui: Ketua RT, Ketua RW, Kepala Desa/Lurah.

**Field:** Nama Yang Menyatakan*, NIK*, Tempat/Tanggal Lahir, Pekerjaan, Alamat*, Almarhum/Almarhumah (Nama*, NIK, Tempat/Tanggal Lahir, Alamat Terakhir), Hari Meninggal*, Tanggal Meninggal*, Jam Meninggal, Tempat Meninggal*, Saksi 1*, Saksi 2*

---

### 27. **SPTJM Perkawinan/Perceraian Belum Tercatat** (tanpa nomor)
**Tujuan:** SPTJM perkawinan atau perceraian belum tercatat (bermaterai). Judul berubah mengikuti pilihan Perkawinan/Perceraian. **Ttd: Suami + Istri.**

**Field:** Jenis SPTJM* (Perkawinan/Perceraian), Nama Suami*, NIK Suami*, Nama Istri*, NIK Istri*, Tanggal Perkawinan/Perceraian*, Saksi 1*, Saksi 2*, Daftar Anak (tabel Nama/No. Akta Kelahiran/SHDK)

---

### 28. **Instrumen Asesmen Calon Penerima Bansos KIS-PBI / SKM** (tanpa nomor)
**Tujuan:** Instrumen asesmen (wawancara petugas) calon penerima bansos KIS-PBI/SKM. PDF multi-halaman.

**Field:** Nama Lengkap*, Tempat/Tanggal Lahir, Jenis Kelamin, Status Kawin, NIK*, Nomor KK, Nama Ibu Kandung, Pekerjaan, Alamat*, 11 pertanyaan kondisi (jaminan kesehatan, alasan non-aktif, status/kondisi/ukuran/dinding/penerangan rumah, jumlah keluarga, jumlah anggota, bansos diterima, usaha/penghasilan), Catatan Petugas

---

### 29. **Surat Pernyataan (PBI-JKN)** (Nomor: 474.4/.../KS/tahun)
**Tujuan:** Surat pernyataan calon penerima bansos PBI-JKN.

**Field:** Nama Lengkap*, NIK*, Tempat/Tanggal Lahir, Pekerjaan, Alamat*, Isi Pernyataan*

---

### 30. **Surat Pernyataan Tidak Memiliki Rekening** (Nomor: 474.4/.../KS/tahun)
**Tujuan:** Surat pernyataan tidak memiliki rekening bank (PBI-JKN).

**Field:** Nama Lengkap*, NIK*, Tempat/Tanggal Lahir, Pekerjaan, Alamat*

---

## 🔁 Varian (opsi di dalam surat yang sudah ada, bukan kartu baru)

- **Domisili** — pilihan Jenis Domisili: Perorangan (form lama), Lembaga (+ nama lembaga, nama pimpinan, alamat, keperluan; judul "SURAT KETERANGAN DOMISILI LEMBAGA"), Perusahaan (+ nama perusahaan, nama pimpinan, nama sekretaris, bidang usaha, alamat, keperluan; judul "SURAT KETERANGAN DOMISILI PERUSAHAAN").
- **Tidak Mampu (SKTM)** — pilihan Varian SKTM: Umum (form lama), Sekolah (judul "SURAT KETERANGAN TIDAK MAMPU (SEKOLAH)"), Daftar Kolektif (satu surat untuk banyak warga — tabel nama/NIK/alamat, maks. 20 baris).

---

## 🎨 Desain & Tema

### Palet Warna
- **Primary:** `#0F766E` (Teal tua)
- **Secondary:** `#475569` (Slate)
- **Background:** `#FAFAF9` (Stone muda) — kartu putih, bayangan lembut

### Animasi
- **Gradient Background:** Animasi gradien bergerak 8s
- **Card Hover:** Scale + elevation
- **Transisi Form:** Slide & fade saat ganti jenis surat
- **Button Press:** Bounce subtle

### Responsivitas
- **Web:** Mobile-first, breakpoint: sm/md/lg/xl
- **Android:** Material 3, adaptive layout, landscape support

---

## 💾 Data Persistence

### Web (LocalStorage)
```javascript
// Data kop surat & pejabat disimpan di localStorage
localStorage.setItem('kasipem_header', JSON.stringify(headerData))

// Auto-load saat app dimulai
const stored = localStorage.getItem('kasipem_header')

// Riwayat Surat (localStorage 'kasipem_riwayat') — tercatat saat unduh PDF / cetak
localStorage.setItem('kasipem_riwayat', JSON.stringify([{ tanggal, nomor, jenis, namaPemohon }]))
```

### Android (DataStore)
```kotlin
// Preferences encrypted di device
val headerKey = stringPreferencesKey("header_data")
context.dataStore.edit { preferences ->
    preferences[headerKey] = Json.encodeToString(headerData)
}
```

**Catatan:** Form data (data pemohon) **TIDAK** disimpan, hanya untuk session saja.

---

## 📄 Export & Cetak

### Format Output

#### PDF (jsPDF / iText)
- Format: A4 (210x297mm)
- Font: Arial/Times New Roman 11pt
- Spasi: 1.15 - 1.5
- Header: Kop surat dengan logo area, garis pemisah
- Footer: Tanda tangan + NIP, tempat & tanggal otomatis

#### DOCX (docx library)
- Format OOXML (.docx)
- Compatible dengan MS Word, LibreOffice, Google Docs
- Teks dapat diedit lebih lanjut

#### Print
- Native print dialog browser/Android
- Preview sebelum cetak
- Otomatis format A4/F4

---

## 🔒 Keamanan & Privacy

✅ **No Server Upload** - Semua data tetap di device  
✅ **No Database** - Tidak ada penyimpanan di backend  
✅ **No Analytics** - Tidak tracking user activity  
✅ **LocalStorage Only** - Hanya untuk kop surat & pejabat  
✅ **HTTPS Ready** - Bisa di-host di HTTPS  
✅ **No Cookies** - Tidak ada tracking cookies  

---

## 📱 User Flow

```
START
  ↓
[Pilih Jenis Surat] ← Dari 30 pilihan (grid kartu + cari/filter kategori) → modal form → modal preview A4
  ↓
[Isi Form] ← Form fields dinamis sesuai jenis surat
  ↓
[Review Data] ← Pratinjau surat sebelum cetak
  ↓
[Export/Cetak] ← Pilih: Unduh PDF / DOCX / Cetak langsung
  ↓
[Buat Surat Baru] ← Reset form, mulai dari awal
  ↓
END
```

---

## 🛠️ Development

### Tech Stack Perbandingan

| Aspek | Web | Android |
|-------|-----|---------|
| Framework | Next.js 14 | Jetpack Compose |
| Language | TypeScript | Kotlin |
| UI Library | Tailwind CSS | Material 3 |
| Animation | Framer Motion | Compose Animation |
| State | Zustand | MutableState + Recomposition |
| PDF | jsPDF | iText |
| Storage | LocalStorage | DataStore Preferences |
| Build | Webpack (next) | Gradle |

### Build Production

**Web:**
```bash
cd web-app
npm run build
npm start
# Output: .next/ folder siap deploy
```

**Android:**
```bash
cd android-app
./gradlew bundleRelease
# Output: app/build/outputs/bundle/release/
```

---

## 📝 Lisensi & Credits

**Dibuat dengan ❤️ untuk administrasi desa/kelurahan Indonesia**

- **Framework:** Next.js, React, Jetpack Compose
- **Styling:** Tailwind CSS, Material Design 3
- **Icons:** Lucide Icons, Material Icons
- **PDF:** jsPDF, iText
- **State:** Zustand, DataStore

---

## 🆘 Troubleshooting

### Web

**Port 3000 sudah dipakai?**
```bash
npm run dev -- -p 3001
```

**CSS tidak loading?**
```bash
rm -rf .next node_modules
npm install
npm run build
```

---

### Android

**Gradle sync error?**
- Buka Android Studio > File > Sync Now
- Atau: `./gradlew clean build`

**Emulator tidak bisa akses camera?**
- Buka Android Studio > Device Manager > Settings > Camera > Enable

**APK tidak terinstall?**
```bash
./gradlew uninstallDebug
./gradlew installDebug
```

---

## 📊 Statistik Proyek

- **File Count:** ~40 (Web) + ~30 (Android)
- **Lines of Code:** ~3000+ (Web) + ~2500+ (Android)
- **Build Time:** ~10s (Web dev) + ~30s (Android)
- **Bundle Size:** ~200KB (Web) + ~15MB (Android APK)

---

## 🎯 Roadmap Fitur Masa Depan

- [ ] Support multiple bahasa (EN, JP)
- [ ] QR Code di surat
- [ ] Digital signature
- [ ] Cloud sync (optional)
- [ ] Laporan statistik
- [ ] Template custom
- [ ] Web version PWA
- [ ] iOS version

---

## 📞 Support & Feedback

Untuk bug report atau feature request:
- Buat issue di repository
- Email: support@kasipem.dev
- Telegram: @kasipem_support

---

**Version:** 1.0.0  
**Last Updated:** 2024  
**Status:** Production Ready ✅
