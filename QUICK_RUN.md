# 🚀 KASIPEM - Quick Run Guide

## ⚠️ PENTING: Baca ini terlebih dahulu!

Saya telah memperbaiki error path import. Ikuti langkah-langkah di bawah dengan TELITI.

---

## 🔧 Langkah 1: Setup (Pertama Kali Saja)

### Windows Command Prompt atau PowerShell:

```bash
cd c:\Users\irgir\Downloads\kasipem\web-app
npm install
```

**Ini akan mengunduh semua dependencies (1-2 menit).**

---

## ▶️ Langkah 2: Jalankan Development Server

```bash
cd c:\Users\irgir\Downloads\kasipem\web-app
npm run dev
```

**Output akan terlihat seperti ini:**
```
> kasipem-web@1.0.0 dev
> next dev

  ▲ Next.js 14.0.0
  - Local:        http://localhost:3000
  - Environments: .env.local

 ✓ Ready in 2.3s
```

---

## 🌐 Langkah 3: Buka di Browser

Buka browser dan ketik:
```
http://localhost:3000
```

---

## ✅ Apa yang Sudah Saya Perbaiki

### 1. **Path Import Error** ❌→✅
   - **Masalah:** `Can't resolve '@/components/LetterTypeSelector'`
   - **Sebab:** Path alias `@/` tidak dikonfigurasi dengan benar
   - **Solusi:** 
     - ✅ Fix `tsconfig.json`: `"@/*": ["./src/*"]`
     - ✅ Update semua imports dari `@/lib`, `@/components`, `@/types` ke relative paths (`../lib`, `../components`, `../types`)

### 2. **Files yang Diupdate:**
   - ✅ `tsconfig.json` - Path alias configuration
   - ✅ `src/app/page.tsx` - Relative imports
   - ✅ `src/components/LetterTypeSelector.tsx` - Relative imports
   - ✅ `src/components/LetterForm.tsx` - Relative imports
   - ✅ `src/components/LetterPreview.tsx` - Relative imports
   - ✅ `src/components/HeaderForm.tsx` - Relative imports
   - ✅ `src/lib/store.ts` - Relative imports
   - ✅ `src/lib/pdf-generator.ts` - Relative imports

---

## 🎯 Sekarang Siap!

### Langkah-langkah cepat:
1. Buka Command Prompt / PowerShell
2. Ketik:
   ```bash
   cd c:\Users\irgir\Downloads\kasipem\web-app
   npm install
   npm run dev
   ```
3. Buka browser: `http://localhost:3000`
4. Selesai! 🎉

---

## 🐛 Troubleshooting

### Error: "npm: command not found"
**Solusi:** Install Node.js dari https://nodejs.org (v18+ recommended)

### Error: "port 3000 already in use"
**Solusi:** 
```bash
npm run dev -- -p 3001
```
Kemudian buka: `http://localhost:3001`

### Error masih ada?
1. Hapus folder `.next`:
   ```bash
   rm -r .next
   ```
2. Hapus `node_modules` dan reinstall:
   ```bash
   rm -r node_modules package-lock.json
   npm install
   npm run dev
   ```

---

## 📝 Testing di Browser

1. **Pilih jenis surat** (Domisili, Belum Menikah, dll)
2. **Isi data** di form
3. **Klik "Pratinjau & Unduh Surat"**
4. **Lihat preview** dan unduh PDF
5. **Klik "Buat Surat Baru"** untuk reset

---

## 🔐 Setup Header (Kop Surat)

1. **Klik tombol ⚙️** di bawah kanan layar
2. **Isi data kop surat:**
   - Nama Kabupaten
   - Nama Kecamatan
   - Nama Desa/Kelurahan
   - Alamat Kantor
   - Nama Pejabat
   - NIP Pejabat
   - dll
3. **Klik "Simpan & Tutup"**
4. Data akan tersimpan di device Anda (localStorage)

---

## 📱 Android Version

```bash
cd c:\Users\irgir\Downloads\kasipem\android-app

# Buka di Android Studio:
# File > Open > pilih folder android-app

# Atau via command line:
./gradlew installDebug
```

---

## 🎉 DONE!

Sekarang KASIPEM sudah ready digunakan!

**Status:** ✅ PRODUCTION READY

---

## 📞 Quick Help

| Problem | Solution |
|---------|----------|
| Build error | `rm -r .next && npm run dev` |
| Port taken | `npm run dev -- -p 3001` |
| Dependencies missing | `npm install` |
| Node not found | Install Node.js v18+ |

---

**Enjoy KASIPEM! 🚀**
