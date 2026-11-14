# KASIPEM - Pembuat Surat Administrasi Desa/Kelurahan Stateless

KASIPEM adalah aplikasi modern untuk membuat berbagai jenis surat administrasi desa/kelurahan secara cepat, mudah, dan aman. Tersedia dalam dua versi: **Web (React/Next.js)** dan **Mobile (Android Native)**.

## 🎯 Fitur Utama

✅ **Stateless & Sekali Pakai** - Tidak menggunakan database, tanpa login/register, semua diproses di sisi klien  
✅ **12 Jenis Surat** - Domisili, Pisah KK, Keterangan Tanah (SKT Dayat), Beda Luas Tanah, Beda Nama, Belum Menikah, Tidak Mampu (SKTM), Ahli Waris, Nikah Tidak Tercatat, Pemakaman, Tidak Memiliki Rumah, Undangan  
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
│   │   │   ├── LetterTypeSelector.tsx  # Grid 12 kartu + cari/filter
│   │   │   ├── LetterForm.tsx        # Modal form per surat
│   │   │   ├── LetterBody.tsx        # Renderer isi surat (preview)
│   │   │   ├── LetterPreview.tsx     # Modal preview A4 + Unduh PDF / Cetak
│   │   │   └── HeaderForm.tsx        # Kop + pejabat (Kades & Sekdes)
│   │   ├── lib/
│   │   │   ├── store.ts             # Zustand state management
│   │   │   ├── letter-definitions.ts # Metadata + field kanon 12 surat
│   │   │   ├── letter-content.ts     # Isi surat (dipakai preview DAN PDF)
│   │   │   └── pdf-generator.ts      # Satu-satunya generator PDF (jsPDF, 12 surat)
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

Ada **12 jenis surat**. Nomor surat bisa diedit per surat (default per pola di bawah, tahun otomatis). Penandatangan: Kepala Desa, kecuali Pisah KK (a.n. Kepala Desa oleh Sekretaris Desa) dan SKTM (ditambah baris "Mengetahui, Ketua RW").

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

### 10. **Surat Keterangan Pemakaman** (Nomor: 474/.../PEM/tahun)
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
[Pilih Jenis Surat] ← Dari 12 pilihan (grid kartu + cari/filter kategori) → modal form → modal preview A4
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
