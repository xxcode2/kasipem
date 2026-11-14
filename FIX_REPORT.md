# KASIPEM — FIX REPORT (Web + Android, 12 surat)

**Repo:** https://github.com/xxcode2/kasipem — commit `63906c2`
**Tanggal perbaikan:** 2026-11-14 (23:2x–23:4x WIB)
**Lokasi hasil:** `~/workspace/kasipem-fix/` (mirror struktur repo; **belum di-push** — tidak ada token GitHub xxcode2 di sini)
**Spesifikasi:** `~/workspace/kasipem-study/FIX_BRIEF.md`

## Ringkasan

Semua bug yang dilaporkan user sudah diperbaiki di kode, dan **web terverifikasi lolos build + 12/12 surat lolos preview/PDF/cetak (uji independen oleh koordinator, bukan hanya klaim subagent)**. Android selesai ditulis lengkap (12/12 di semua layer) tetapi **build-nya belum bisa diverifikasi di mesin ini** (tidak ada Java/Android SDK) — wajib dicek di Android Studio.

Desa asli: **Desa Kasomalang Kulon, Kecamatan Kasomalang, Kabupaten Subang**, alamat kantor **Jln. Raya Kasomalang No.01 Subang 41281**. Pejabat asli (dari user 2026-11-14 ~23:28 WIB): Kades **H. AMIRUDIN, S.Pd.I**, Sekdes **APIPUDIN, S.IP**. **Tidak ada NIP** — baris NIP tidak dirender di preview/PDF/cetak; tidak ada lagi `1234567890` atau placeholder NIP di mana pun (dicek dengan grep di seluruh tree).

## Root cause yang diperbaiki (semua terverifikasi di kode lama)

1. **PDF kosong 7/12 surat** — generator PDF lama hanya handle 5 jenis. Kini satu generator menangani 12/12.
2. **Preview kosong 7/12 surat** — renderer preview lama hanya 4 jenis + undangan. Kini satu renderer (`getLetterContent` / `LetterBody`) dipakai preview DAN PDF, jadi tidak bisa drift.
3. **Field mismatch** — form pakai `alamat`/`alamatAsal`/`alamatDomisili`/`keperluanSurat`, preview/PDF baca `alamatLengkap`/`rt/rw`/`tujuanSekolah` → blank + SKTM "di undefined". Kini satu kanon field untuk form/store/preview/PDF/Android (lihat tabel di bawah).
4. **Cetak beda jauh** — dulu `window.open` + `innerHTML` mentah tanpa CSS. Kini print CSS `@page A4` + `window.print()` dari halaman yang sama.
5. **Kop tidak muncul** — `kop/kades.jpg` (logo Garuda) dan `kop/sekdes.png` (logo Kabupaten Subang) tidak pernah dipakai kode lama. Kini kop dua logo tampil di preview, PDF, dan cetak (dengan fallback kop teks bila logo gagal dimuat).
6. **UI scroll panjang** — dulu 12 surat ditumpuk per kategori + form penuh di bawah. Kini grid 12 kartu (cari + filter kategori) → klik kartu → modal form → modal preview A4.
7. **Android tertinggal** — enum hanya 5 jenis. Kini 12 jenis di semua layer Android.
8. **README bilang 5 surat** — kini 12 (di `README.md` hasil perbaikan ini).

## Kanon field 12 surat (satu sumber kebenaran — web + Android sama)

| Surat | Nomor default | Ttd | Field kanon |
|---|---|---|---|
| domisili | 474/.../PEM/tahun | Kepala Desa | namaLengkap, nik, jenisKelamin, tempatLahir, tanggalLahir, pekerjaan, alamatAsal, rt, rw, alamatDomisili, keperluanSurat |
| pisah_kk | 474/.../PEM/tahun | a.n. Kades — Sekdes | nama1, nik1, nama2, nik2, nomorKKLama, alamat |
| skt_dayat | 593/.../PEM/tahun | Kepala Desa | namaLengkap, nik, tempatLahir, tanggalLahir, alamat, luasTanah, luasBangunan, spptNop, batasUtara, batasTimur, batasSelatan, batasBarat |
| beda_luas | 141/.../PEM/tahun | Kepala Desa | namaLengkap, nik, alamat, rt, rw, nomorSertifikat, luasSertifikat, nomorSPPT, luasSPPT |
| beda_nama | 474/.../PEM/tahun | Kepala Desa | namaLengkap, nik, namaDokumen1, namaDokumen2, alasan |
| belum_menikah | 460.2/.../Ks/tahun | Kepala Desa | namaLengkap, nik, jenisKelamin, tempatLahir, tanggalLahir, pekerjaan, alamat, rt, rw, keperluan |
| tidak_mampu (SKTM) | 460/.../Ks/tahun | Kepala Desa + "Mengetahui, Ketua RW" | namaLengkap, nik, jenisKelamin, tempatLahir, tanggalLahir, pekerjaan, alamat, rt, rw, penghasilanBulanan, keperluan, tujuan |
| ahli_waris | 474/.../PEM/tahun | Kepala Desa | namaPeninggal, nikPeninggal, alamatPeninggal, namaAhliWaris1, nikAhliWaris1, hubunganAhliWaris1, namaAhliWaris2, nikAhliWaris2, hubunganAhliWaris2, keterangan |
| nikah_tidak_tercatat | 460.2/.../Ks/tahun | Kepala Desa | namaPria, nikPria, tempatLahirPria, tanggalLahirPria, namaWanita, nikWanita, tempatLahirWanita, tanggalLahirWanita, tanggalPernikahan, tempatPernikahan |
| pemakaman | 474/.../PEM/tahun | Kepala Desa | namaAlmarhum, nikAlmarhum, tempatLahir, tanggalLahir, alamat, tanggalMeninggal, lokasiMakam, namaKeluarga |
| tidak_memiliki_rumah | 474/.../PEM/tahun | Kepala Desa | namaLengkap, nik, tempatLahir, tanggalLahir, pekerjaan, alamatTinggal, keterangan |
| undangan | manual (editable) | Kepala Desa | tujuanYth, hari, tanggal, bulan, tahun, pukul, tempat, agendaAcara, daftarTembusan |

Semua surat: field `nomorSurat` (editable) + tanggal surat otomatis hari ini (id-ID). Validasi NIK 16 digit angka = peringatan saja, tidak memblokir. Field wajib bertanda `*`; tidak ada lagi `alert()`.

## Perubahan per file — WEB (`web-app/`, Next.js 14)

| File | Perubahan |
|---|---|
| `src/lib/letter-definitions.ts` **(baru)** | Metadata 12 surat (judul, kategori, pola nomor, deskripsi) + konfigurasi field form kanon + `defaultLetterData()` + validasi NIK. Satu sumber kebenaran form/store. |
| `src/lib/letter-content.ts` **(baru)** | `getLetterContent()` — struktur isi surat (judul, nomor, pembuka, blok field/paragraf/daftar, penutup, penandatangan) yang dipakai **bersama** preview dan PDF. |
| `src/components/LetterBody.tsx` **(baru)** | Renderer isi surat murni (kop 2 logo, judul, nomor, isi, tanda tangan) untuk preview; dibungkus `.print-area` saat cetak. |
| `src/lib/pdf-generator.ts` | Ditulis ulang; SATU-SATUNYA generator (async, 12/12 dari `getLetterContent`); kop dua logo + garis ganda tebal+tipis; logo via fetch→dataURL dengan fallback kop teks; downscale canvas maks 400px + re-encode JPEG (PDF awal 18 MB karena PNG 1920×2404 → kini ~75 KB/surat). |
| `src/lib/pdf-generator-v2.ts` | **Dihapus** (dead code; LetterPreview dulu hanya import v1; v2 masih pakai alias `@/types`). |
| `src/lib/store.ts` | Default desa asli + pejabat asli (AMIRUDIN/APIPUDIN, NIP kosong); `setLetterType` reset ke default field tipe itu; header + `namaSekdes`/`nipSekdes`/`logoGaruda`/`logoSubang`/`penandatanganDefault`. |
| `src/types/index.ts` | `HeaderData` + `namaSekdes`, `nipSekdes`, `logoGaruda`, `logoSubang`, `penandatanganDefault`. |
| `src/components/LetterPreview.tsx` | Modal A4; Unduh PDF (async, error inline); Cetak = `window.print()` (tidak ada lagi `window.open`+innerHTML); Buat Surat Baru. |
| `src/components/LetterForm.tsx` | Modal form; field dari kanon; nomor surat editable; tanggal otomatis; validasi inline tanpa `alert()`; NIK bukan 16 digit hanya peringatan kuning. |
| `src/components/LetterTypeSelector.tsx` | Grid 12 kartu + pencarian + filter kategori (Individu/Properti/Administrasi/Sosial/Hukum/Lainnya), klik → modal form. Palet kalem, tanpa animasi berputar. |
| `src/components/HeaderForm.tsx` | Default desa asli; nama + NIP Sekdes (NIP opsional, label jelas); pilihan penandatangan default (Kades / Sekdes a.n.); upload/lihat/reset logo kop (dataURL → localStorage). |
| `src/app/page.tsx` | Header kalem teal-800; alur kartu → modal form → modal preview; tidak ada lagi form panjang di bawah daftar; sidebar ringkas. |
| `src/app/layout.tsx` | Latar `bg-stone-100`, tanpa gradien biru-hijau. |
| `src/app/globals.css` | Tema kalem + print styles: `@page { size: A4; margin: 15mm 20mm; }`, `@media print` sembunyikan semua kecuali `.print-area`, `.letter-sheet` tanpa padding/bayangan saat cetak. |
| `src/app/page.tsx` | (lihat di atas) |
| `tsconfig.json` | Hanya tambahan `"allowJs": true` (ditambahkan Next saat build) + pemformatan; `strict` tetap `true`. |
| `public/kop/sekdes.jpg` **(baru)** | Logo Subang di-resize 400×500 (46 KB, latar putih) dari `kop/sekdes.png` agar PDF tidak membengkak. Aslinya tetap utuh di `kop/sekdes.png`. `public/kop/kades.jpg` tetap. |
| `package.json` | **Tidak diubah.** Catatan: `framer-motion` masih tercantum tapi tidak lagi di-import di mana pun di `src/` — aman dibiarkan, bisa di-prune nanti. |
| `next-env.d.ts` | Dihapus dari tree (file auto-generate; dibuat ulang tiap `next build`/`next dev`). |
| `package-lock.json` | Baru digenerate oleh `npm install` (repo study copy tidak menyertakannya; aslinya ada di repo, 44 KB). |

## Perubahan per file — ANDROID (`android-app/`, Kotlin + Compose)

| File | Perubahan |
|---|---|
| `app/src/main/java/com/kasipem/data/Models.kt` | `LetterType` 12 enum (judul, deskripsi, kategori, pola nomor); 12 data class kanon + `nomorSurat`/`tanggalSurat`; `HeaderData` desa asli + nama/NIP Sekdes + path logo; helper `defaultNomorSurat`/`effectiveNomorSurat`/`effectiveTanggalSurat`/`nikError`/`getEmptyLetterData`. `PersonalData` legacy (sumber mismatch `alamatLengkap`) dihapus. |
| `app/src/main/java/com/kasipem/data/PdfGenerator.kt` | Dispatcher + 12 generator iText; kop 3 kolom (Garuda kiri, teks tengah, Subang kanan, fallback teks); tabel data label:nilai; footer ttd Kades / Sekdes-an / SKTM + "Mengetahui, Ketua RW". |
| `app/src/main/java/com/kasipem/ui/screens/Forms.kt` | Dispatcher `LetterForm()` + 12 form kanon; Nomor + Tanggal Surat di tiap form; error NIK persisten di bawah field; field wajib `*`. |
| `app/src/main/java/com/kasipem/ui/screens/PreviewScreen.kt` | Preview 12/12; tombol PDF (ke `cacheDir`) dan Cetak (PrintManager + `PdfFilePrintAdapter` A4) berfungsi; status tampil sebagai teks persisten. |
| `app/src/main/java/com/kasipem/ui/screens/HomeScreen.kt` | Grid 2 kolom 12 kartu (cari + filter kategori); alur Grid → Form (satu surat) → Preview; banner kalem satu warna. |
| `app/src/main/java/com/kasipem/ui/components/LetterTypeCard.kt` | Kartu putih, ikon satu warna aksen datar (gradien dihapus), tampil kategori + klasifikasi. |
| `app/src/main/java/com/kasipem/ui/components/FormField.kt` | `FormTextField` dukung `error` persisten + `isRequired`; bug `FormDropdown` diperbaiki (`expanded` kini `remember`, sebelumnya reset tiap recomposition). |
| `app/src/main/java/com/kasipem/ui/theme/Theme.kt` | Tema kalem: latar stone `#FAFAF9`, primer teal tua `#0F766E`, sekunder slate. |
| `app/src/main/java/com/kasipem/ui/screens/SettingsScreen.kt` | Seksi Kades + Sekdes (nama asli default, NIP opsional kosong), info logo kop. |
| `app/src/main/assets/kop/kades.jpg` + `sekdes.png` **(baru)** | Copy logo dari `kop/`. |
| Tidak diubah | `MainActivity.kt`, `Typography.kt`, `build.gradle.kts`, `AndroidManifest.xml`, `strings.xml`, `themes.xml`, `gradle-wrapper.properties` (tidak perlu; tidak ada referensi enum lama tersisa). |

## Perubahan lain

| File | Perubahan |
|---|---|
| `README.md` (root, baru di tree ini) | Diselaraskan dari README asli repo: "5 Jenis Surat" → **12 Jenis Surat** (12 sub-bagian lengkap dengan nomor + field), palet tenang `#0F766E`, alur "12 pilihan → modal form → modal preview A4", struktur proyek diperbarui (file `lib/`/`components/` baru), fitur kop logo + pejabat default ditambahkan. |
| `.gitignore` (root, baru) | `node_modules/`, `.next/`, `out/`, `*.tsbuildinfo`, `android-app/app/build/`, `android-app/.kotlin/`, `android-app/.gradle/`, `android-app/local.properties`, `*.apk`, `*.aab`, `.DS_Store`, `.env.local`. Repo asli meng-commit `android-app/app/build/` + `.kotlin/` (~100 MB) dan `tsconfig.tsbuildinfo` — jangan ikutkan lagi saat push. |
| `kop/` | `kades.jpg` + `sekdes.png` (copy logo asli, tidak diubah). |

## Hasil build WEB — LOLOS (diverifikasi koordinator, bukan hanya klaim)

Perintah (dijalankan koordinator di `~/workspace/kasipem-fix/web-app`):
```
npm run build
✓ Compiled successfully
✓ Generating static pages (4/4)
Route (app): /  134 kB / First Load JS 222 kB
```
(`npm install` sudah dijalankan subagent web sebelumnya; `package-lock.json` terbentuk.)

## Matriks tes 12 surat — 12/12 LOLOS (uji ulang independen oleh koordinator)

Metode: harness lokal `/tmp/kasipem-test/test-12.js` (bukan deliverable) — kompilasi ulang modul dari **source terbaru** dengan `tsc` proyek, lalu per surat: render `LetterBody` (preview) + `generatePdf` dengan logoLoader dari disk; cek judul, nomor, nama uji, penandatangan, tanpa baris `NIP:`, 2 logo ter-embed, ukuran/halaman PDF; cek print CSS di `globals.css`. Data uji **fiktif** ("Budi Santoso", NIK `3213260101900001` dkk) — bukan data contoh asli dari docs.

| Surat | Preview | PDF | Cetak* | Catatan |
|---|---|---|---|---|
| domisili (474/PEM) | ✅ | ✅ 75,6 KB, 1 hal, 2 logo | ✅ | Judul, nomor, nama, H. AMIRUDIN, tanpa NIP |
| pisah_kk (474/PEM) | ✅ | ✅ 75,5 KB | ✅ | Ttd a.n. Kades — APIPUDIN, S.IP |
| skt_dayat (593/PEM) | ✅ | ✅ 76,8 KB | ✅ | Batas U/T/S/B tampil |
| beda_luas (141/PEM) | ✅ | ✅ 76,1 KB | ✅ | Sertifikat vs SPPT tampil |
| beda_nama (474/PEM) | ✅ | ✅ 74,8 KB | ✅ | |
| belum_menikah (460.2/Ks) | ✅ | ✅ 75,7 KB | ✅ | |
| tidak_mampu (460/Ks) | ✅ | ✅ 76,4 KB | ✅ | "Mengetahui, Ketua RW" tampil; tujuan tampil, tanpa "undefined" |
| ahli_waris (474/PEM) | ✅ | ✅ 76,4 KB | ✅ | |
| nikah_tidak_tercatat (460.2/Ks) | ✅ | ✅ 75,5 KB | ✅ | |
| pemakaman (474/PEM) | ✅ | ✅ 75,3 KB | ✅ | |
| tidak_memiliki_rumah (474/PEM) | ✅ | ✅ 75,2 KB | ✅ | |
| undangan (manual) | ✅ | ✅ 75,9 KB | ✅ | Nomor manual (garis titik bila kosong); tembusan tampil |

\* Cetak = verifikasi statis: `@page`, `size: A4`, `margin: 15mm 20mm`, `@media print`, `.print-area` ada di `globals.css`, dan tombol Cetak memanggil `window.print()`. **Belum diuji visual di browser sungguhan** — perlu sekali cek pratinjau cetak A4 di browser (lihat "Yang masih butuh user" di bawah).

Hasil harness: `RINGKASAN: 12/12 surat lolos penuh.` (exit code 0). File PDF uji ada di `/tmp/kasipem-test/out-*.pdf` (bukan deliverable; boleh dihapus).

Matriks Android (dari subagent Android, verifikasi baca-ulang — bukan compile): 12/12 ada di Form, Preview, dan PDF-generator untuk semua surat. Kurung/paren/bracket seimbang (selisih 0) di 9 file `.kt` yang diubah (dicek ulang koordinator).

## Hasil build ANDROID — BELUM TERVERIFIKASI (jujur)

- `gradlew` **tidak ada** di tree (hanya `gradle/wrapper/gradle-wrapper.properties`); `java`/`gradle` **tidak ditemukan** di mesin ini; `ANDROID_HOME` kosong. Jadi `./gradlew assembleDebug` tidak bisa dijalankan di sini. Ini keterbatasan lingkungan, bukan bukti kode rusak.
- Yang **masih butuh dicek di Android Studio** (JDK 17 + SDK 34 + `gradlew` dari template proyek): `./gradlew assembleDebug`; render 12 preview + generate 12 PDF di emulator/perangkat (logo kop tampil, teks tidak terpotong, tombol PDF/Cetak); ikon `FamilyRestroom`, `Park`, `House`, `Badge`, `Map` (material-icons-extended 1.6.0, sudah di dependensi) pastikan resolve saat compile.

## Yang masih butuh user

1. **Token GitHub akun xxcode2** untuk push — agen tidak push apa pun. Cara push (dari `~/workspace/kasipem-fix/`, repo git sudah mengarah ke `https://github.com/xxcode2/kasipem.git`):
   ```
   cd ~/workspace/kasipem-fix
   git add -A
   git status        # periksa: jangan sampai node_modules/ .next/ app/build/ .kotlin/ ikut
   git commit -m "KASIPEM: 12 surat lengkap, PDF/preview/cetak diperbaiki, kop logo, UI kalem"
   git push origin main   # butuh token xxcode2 (jangan ditempel di chat; pakai credential helper / gh auth)
   ```
   Perhatian: tree ini **belum berisi** folder `file/` (template DOCX/PDF/XLS asli) dan dokumen analisis root lainnya dari repo asli — push dari tree ini apa adanya akan **menghapus** file-file itu dari repo bila dilakukan sebagai pengganti penuh. Cara aman: copy hasil perbaikan ini ke clone lengkap repo asli (atau `git checkout` file yang diubah saja ke clone tersebut), lalu commit + push dari sana. Jangan `git push --force`.
2. **Redeploy Vercel** setelah push (https://kasipem.vercel.app) — Vercel biasanya auto-deploy dari `main`; cek hasil deploy-nya.
3. **Cek cetak sekali di browser** (web): buka preview surat → Cetak → lihat pratinjau cetak A4 (kop, isi, tanda tangan harus sama dengan preview).
4. **Cek Android di Android Studio** (poin di atas).
5. Bila ada NIP di masa depan, isi lewat HeaderForm/Settings — baris NIP otomatis muncul hanya bila field diisi.
6. Opsional: hapus `framer-motion` dari `web-app/package.json` (tidak terpakai lagi) dan isi nomor surat default per surat bila ada penomoran resmi desa.

## Catatan kebersihan

- Tidak ada data contoh asli (NENDEN KOMALA dkk) di file yang diserahkan; tidak ada `1234567890`, `H. Nama Kepala Desa`, atau `tujuanSekolah` tersisa (dicek grep di `web-app/src` + `android-app/app/src`).
- `file/` (template DOCX/PDF/XLS/XLSM asli) **tidak diubah** sama sekali.
- Ukuran tree hasil (di luar `node_modules/` 385 MB + `.next/` 136 MB hasil build lokal — keduanya di-`.gitignore`, bukan deliverable): source + kop ≈ 1 MB; `.git/objects` 32 MB adalah data fetch bawaan saat setup repo lokal, bukan bagian perbaikan.
- Dua subagent bekerja paralel dan tidak saling menyentuh direktori satu sama lain (diverifikasi via `diff` vs study copy + mtime: tidak ada file Android yang disentuh setelah subagent web mulai, dan sebaliknya).
