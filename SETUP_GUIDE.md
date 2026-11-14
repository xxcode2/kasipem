# KASIPEM - Setup Guide Lengkap

Panduan lengkap setup dan deployment untuk kedua platform.

---

## 📦 Versi Web (Next.js + React + Tailwind)

### Prasyarat
- **Node.js:** 18.17 atau lebih baru
- **npm:** 9+ atau **yarn:** 4+
- **Git:** untuk version control

### Step 1: Clone & Install

```bash
cd kasipem/web-app

# Install dependencies
npm install
# atau
yarn install
```

### Step 2: Konfigurasi Environment

Buat file `.env.local`:

```env
# .env.local
NEXT_PUBLIC_APP_NAME=KASIPEM
NEXT_PUBLIC_VERSION=1.0.0
```

### Step 3: Development Server

```bash
npm run dev
```

Aplikasi akan berjalan di `http://localhost:3000`

**Fitur dalam development:**
- Hot reload otomatis
- Source maps untuk debugging
- Error boundary yang informatif

### Step 4: Production Build

```bash
# Build aplikasi
npm run build

# Test production build lokal
npm start
```

Output akan ada di folder `.next/`

### Step 5: Deploy

#### Option A: Vercel (Recommended)
```bash
# Install Vercel CLI
npm i -g vercel

# Deploy
vercel
```

#### Option B: Docker
```dockerfile
# Dockerfile
FROM node:18-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

EXPOSE 3000

CMD ["npm", "start"]
```

Deploy:
```bash
docker build -t kasipem-web .
docker run -p 3000:3000 kasipem-web
```

#### Option C: Self-Hosted
```bash
# Di server (Linux/Ubuntu)
git clone <repo>
cd kasipem/web-app
npm install
npm run build

# Gunakan PM2 untuk process management
npm install -g pm2
pm2 start npm --name "kasipem" -- start
pm2 save
pm2 startup
```

### Struktur Web App

```
web-app/
├── src/app/
│   ├── page.tsx          # Halaman utama
│   ├── layout.tsx        # Root layout
│   └── globals.css       # CSS global
├── src/components/       # React components
│   ├── LetterTypeSelector.tsx
│   ├── LetterForm.tsx
│   ├── LetterPreview.tsx
│   └── HeaderForm.tsx
├── src/lib/              # Utilities
│   ├── store.ts          # Zustand store
│   └── pdf-generator.ts  # PDF generation
├── src/types/            # TypeScript types
├── package.json
├── tsconfig.json
├── tailwind.config.ts
└── next.config.js
```

### Customization

**Ubah Warna Primary:**
Edit `tailwind.config.ts`:
```ts
extend: {
  colors: {
    primary: {
      600: '#0284C7', // Ubah di sini
    }
  }
}
```

**Ubah Font:**
Edit `globals.css`:
```css
body {
  font-family: 'Your Font', sans-serif;
}
```

---

## 📱 Versi Android (Kotlin + Jetpack Compose)

### Prasyarat
- **Android Studio:** 2023.1.1 atau lebih baru
- **Android SDK:** 
  - Min SDK: 24 (Android 7.0)
  - Target SDK: 34 (Android 14)
  - Compile SDK: 34
- **Kotlin:** 1.9.10
- **Java:** JDK 17 atau lebih baru
- **Gradle:** 8.1+

### Step 1: Setup Android Studio

1. Download & install [Android Studio](https://developer.android.com/studio)
2. Buka Android Studio
3. Menu: **File > Open > pilih folder `kasipem/android-app`**
4. Tunggu Gradle sync selesai (~5 menit)

### Step 2: Verifikasi Setup

```bash
cd kasipem/android-app

# Check Gradle version
./gradlew --version

# Sync dependencies
./gradlew build
```

### Step 3: Jalankan di Emulator

**Buat Virtual Device:**
1. Android Studio > Device Manager
2. Create device (misal: Pixel 5 - Android 14)
3. Jalankan device

**Deploy ke emulator:**
```bash
./gradlew installDebug

# Atau via Android Studio: Run > Run 'app'
```

### Step 4: Jalankan di Physical Device

**Enable Developer Mode:**
1. Settings > About Phone > Tap "Build Number" 7x
2. Settings > Developer Options > USB Debugging ON
3. Connect via USB

**Deploy:**
```bash
./gradlew installDebug
```

### Step 5: Build Release APK

```bash
# Build APK
./gradlew assembleRelease

# Output: app/build/outputs/apk/release/app-release-unsigned.apk
```

**Untuk production, sign APK:**

1. Create keystore:
```bash
keytool -genkey -v -keystore kasipem.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias kasipem
```

2. Edit `app/build.gradle.kts`:
```gradle
signingConfigs {
    release {
        storeFile file("../kasipem.keystore")
        storePassword "YOUR_PASSWORD"
        keyAlias "kasipem"
        keyPassword "YOUR_PASSWORD"
    }
}

buildTypes {
    release {
        signingConfig signingConfigs.release
    }
}
```

3. Build signed APK:
```bash
./gradlew assembleRelease
```

### Step 6: Generate App Bundle (untuk Play Store)

```bash
./gradlew bundleRelease

# Output: app/build/outputs/bundle/release/app-release.aab
```

### Struktur Android App

```
android-app/
├── app/src/main/
│   ├── java/com/kasipem/
│   │   ├── MainActivity.kt
│   │   ├── data/
│   │   │   ├── Models.kt
│   │   │   ├── LetterStore.kt
│   │   │   └── PdfGenerator.kt
│   │   └── ui/
│   │       ├── theme/
│   │       ├── components/
│   │       └── screens/
│   ├── res/
│   │   ├── values/
│   │   ├── drawable/
│   │   └── layout/
│   └── AndroidManifest.xml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
```

### Customization

**Ubah App Name:**
Edit `android-app/app/src/main/res/values/strings.xml`:
```xml
<resources>
    <string name="app_name">KASIPEM Custom</string>
</resources>
```

**Ubah Theme Color:**
Edit `MainActivity.kt` atau `Theme.kt`:
```kotlin
private val BluePrimary = Color(0xFF0284C7)  // Ubah RGB
```

**Ubah App Icon:**
Android Studio > right-click app > New > Image Asset
- Background layer: app icon
- Foreground: KASIPEM logo

---

## 🧪 Testing

### Web App

```bash
cd web-app

# Unit tests (jika ada)
npm run test

# Build test
npm run build

# Production test
npm start
```

**Test Checklist:**
- [ ] Form submission
- [ ] PDF generation
- [ ] LocalStorage save/load
- [ ] Responsive (mobile, tablet, desktop)
- [ ] Cross-browser (Chrome, Firefox, Safari)

### Android App

**Unit Test:**
```bash
cd android-app
./gradlew test
```

**Instrumented Test (di device/emulator):**
```bash
./gradlew connectedAndroidTest
```

**Manual Testing Checklist:**
- [ ] Form input semua field
- [ ] Validasi form
- [ ] PDF generation & save
- [ ] Print preview
- [ ] Landscape mode
- [ ] Back navigation
- [ ] Settings save/load

---

## 🔍 Debugging

### Web

**Chrome DevTools:**
1. F12 atau Ctrl+Shift+I
2. Elements, Console, Network, Performance tabs
3. Zustand state: `useLetterStore.getState()`

**VS Code Debugging:**
```json
// .vscode/launch.json
{
  "version": "0.2.0",
  "configurations": [
    {
      "name": "Next.js",
      "type": "node",
      "runtimeExecutable": "npm",
      "runtimeArgs": ["run", "dev"],
      "console": "integratedTerminal"
    }
  ]
}
```

### Android

**Android Studio Debugger:**
1. Set breakpoint (click line number)
2. Run in debug mode: Shift+F9
3. Step through code: F10/F11
4. Watch variables di Variables panel

**Logcat:**
```bash
./gradlew -info

# Atau filter:
./gradlew -i | grep "kasipem"
```

---

## 📋 Checklist Deployment

- [ ] **Web:**
  - [ ] `npm run build` success
  - [ ] `.next` folder generated
  - [ ] Env variables set
  - [ ] HTTPS configured
  - [ ] Domain setup
  - [ ] CDN configured (optional)

- [ ] **Android:**
  - [ ] `./gradlew assembleRelease` success
  - [ ] APK signed
  - [ ] Manifest permissions correct
  - [ ] Min SDK 24+
  - [ ] App icon set
  - [ ] Play Store listing prepared

---

## 🚨 Troubleshooting

### Web

| Error | Solusi |
|-------|--------|
| `Module not found` | `npm install` atau `rm -rf node_modules package-lock.json && npm install` |
| `Port 3000 already in use` | `npm run dev -- -p 3001` atau kill process: `lsof -i :3000` |
| `PDF tidak generate` | Check jsPDF import, test di console: `console.log(jsPDF)` |

### Android

| Error | Solusi |
|-------|--------|
| `Gradle sync failed` | File > Sync Now, atau `./gradlew clean build` |
| `Emulator tidak start` | Android Studio > Device Manager > wipe data & restart |
| `APK install failed` | `./gradlew uninstallDebug && ./gradlew installDebug` |
| `Compose error` | Update Kotlin: `kotlinOptions { jvmTarget = "17" }` |

---

## 📊 Performance Optimization

### Web
- Image optimization: Next.js Image component
- Code splitting: Dynamic imports
- CSS purge: Tailwind production
- Caching: Service Worker (PWA)

### Android
- Lazy composition: LazyColumn
- Recomposition: Stable parameters
- Memory: Avoid memory leaks
- Battery: Efficient animations

---

## 📝 Dokumentasi Kode

Jalankan documentation generation:

```bash
# Web (JSDoc)
npm install -D jsdoc
npm run jsdoc

# Android (Dokka)
./gradlew dokkaHtml
# Output: app/build/dokka/html
```

---

**Last Updated:** 2024  
**Status:** Production Ready ✅
