# KASIPEM - Quick Start (5 Menit)

Mulai dengan cepat tanpa ribet!

---

## 🌐 Web (Next.js)

### Install & Run (30 detik)

```bash
cd web-app
npm install
npm run dev
```

**Akses:** http://localhost:3000

### Struktur File Penting

```
src/
├── app/page.tsx          ← Halaman utama
├── components/           ← UI components
│   ├── LetterTypeSelector.tsx
│   ├── LetterForm.tsx
│   ├── LetterPreview.tsx
│   └── HeaderForm.tsx
└── lib/
    ├── store.ts          ← State management (Zustand)
    └── pdf-generator.ts  ← PDF export logic
```

### Testing Cepat

1. Buka `http://localhost:3000`
2. Pilih "Surat Keterangan Domisili"
3. Isi form dengan data dummy
4. Klik "Pratinjau & Unduh Surat"
5. Lihat preview, unduh PDF

---

## 📱 Android (Kotlin)

### Install & Run (1-2 menit)

```bash
cd android-app
./gradlew installDebug
```

**Emulator harus running**, atau connect physical device dengan USB Debugging ON.

### Build APK

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### Struktur File Penting

```
src/main/java/com/kasipem/
├── MainActivity.kt       ← Entry point
├── data/
│   ├── Models.kt         ← Data classes
│   ├── LetterStore.kt    ← DataStore persistence
│   └── PdfGenerator.kt   ← PDF export
└── ui/
    ├── screens/
    │   ├── HomeScreen.kt ← Main UI
    │   ├── Forms.kt      ← Form components
    │   ├── SettingsScreen.kt
    │   └── PreviewScreen.kt
    └── theme/
        ├── Theme.kt      ← Color & styling
        └── Typography.kt
```

---

## 🔧 Customization Cepat

### Ubah Warna (Web)

**File:** `web-app/tailwind.config.ts`

```ts
colors: {
  primary: {
    600: '#YOUR_COLOR', // Ganti di sini
  }
}
```

### Ubah Warna (Android)

**File:** `android-app/app/src/main/java/com/kasipem/ui/theme/Theme.kt`

```kotlin
private val BluePrimary = Color(0xFF0284C7) // Ubah hex code
```

### Ubah Nama Aplikasi (Android)

**File:** `android-app/app/src/main/res/values/strings.xml`

```xml
<string name="app_name">KASIPEM Custom Name</string>
```

---

## 📦 Deployment Cepat

### Web (Vercel - Recommended)

```bash
cd web-app
npm install -g vercel
vercel
# Follow prompts, done! 🎉
```

### Web (Docker)

```bash
cd web-app
docker build -t kasipem-web .
docker run -p 3000:3000 kasipem-web
```

### Android (APK Distribution)

```bash
cd android-app
./gradlew assembleDebug
# Share app/build/outputs/apk/debug/app-debug.apk
```

---

## 📋 Form Input Examples

### Domisili
```
Nama: Budi Santoso
NIK: 3201234567890123
Alamat Asal: Jl. Merdeka No. 45, RT 002, RW 003
Alamat Domisili: Jl. Gatot Subroto No. 123
RT: 001
RW: 002
Keperluan: Pembuatan Rekening Bank
```

### Belum Menikah
```
Nama: Siti Nurhaliza
NIK: 3201234567890456
Alamat: Jl. Sudirman No. 78
RT: 005, RW: 001
Tujuan: Universitas Indonesia
Keperluan: Pendaftaran Mahasiswa Baru
```

### Tidak Mampu (SKTM)
```
Nama: Ahmad Wijaya
NIK: 3201234567890789
Alamat: Jl. Merdeka Raya
RT: 003, RW: 004
Sekolah: SMA Negeri 1 Jakarta
Keperluan: Beasiswa Penuh
```

### Beda Luas Tanah
```
Nama: Pak Hendra
NIK: 3201234567890101
Nomor Sertifikat: 001.001.001-2020
Luas Sertifikat: 500
Nomor SPPT: 12.34.567.890.1234
Luas SPPT: 480
```

### Undangan
```
Tujuan: Kepala Dinas Pendidikan, Kecamatan Rancaekek
Hari: Senin
Tanggal: 15 Januari 2024
Pukul: 09:00
Tempat: Aula Kantor Desa Rancamanyar
Agenda: Rapat Koordinasi Pembangunan Desa Tahun 2024
```

---

## 🐛 Troubleshoot Cepat

| Masalah | Solusi |
|---------|--------|
| **Web: Port 3000 unavailable** | `npm run dev -- -p 3001` |
| **Web: CSS not loading** | `npm run build` (production mode) |
| **Android: Gradle fail** | `./gradlew clean build` |
| **Android: APK too large** | Run `./gradlew bundleRelease` instead |
| **PDF blank** | Check browser console for errors |

---

## 🚀 Next Steps

1. **Customize branding** (logo, warna, font)
2. **Test semua jenis surat** dengan data real
3. **Setup hosting** (Vercel/Docker untuk web, Play Store untuk Android)
4. **Share dengan tim** atau publish

---

## 📚 Dokumentasi Lengkap

- **README.md** - Overview & features
- **SETUP_GUIDE.md** - Setup detail & deployment
- **QUICK_START.md** - Ini! 👈

---

**Happy coding! 🎉**

Untuk bantuan: baca SETUP_GUIDE.md atau check troubleshooting section.
