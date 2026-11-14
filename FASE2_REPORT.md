# KASIPEM — FASE 2 REPORT (18 formulir baru + varian + Riwayat Surat)

**Repo:** https://github.com/xxcode2/kasipem — HEAD sebelum Fase 2: `209db7c` (Fase 1)
**Tanggal:** 2026-11-15 (00:1x–00:3x WIB)
**Lokasi kerja:** `~/workspace/kasipem-push/` (clone lengkap, branch `main`)
**Spesifikasi:** `~/workspace/kasipem-study/FILE_INTEGRATION_BRIEF.md`
**Sumber kebenaran:** `~/workspace/kasipem-study/FILE_INVENTORY.md` + folder `file/` (read-only)
**Scope:** pilihan user 2026-11-15 00:17 WIB — "Semua jadi form isian (surat + semua F)"
**Serahan:** SATU commit LOKAL di clone ini (belum di-push — tidak ada token GitHub xxcode2 di mesin ini; push dilakukan Kai setelah verifikasi)

## Ringkasan

Aplikasi kini **30 kartu** (12 lama + 18 baru) + 4 varian sebagai opsi di dalam surat
yang sudah ada. Semua 22 desain baru dari folder `file/` menjadi form isian +
preview + PDF + cetak, di web DAN Android. Arsitektur Fase 1 dipertahankan: satu
sumber kebenaran (`getLetterContent`) dipakai preview DAN PDF, satu generator PDF,
nama field kanon sama di form/store/preview/PDF/Android, UI kalem, cetak `@page A4`
+ `window.print()`, NIP tidak dirender.

**Web terverifikasi lolos build + 36/36 kasus uji lolos (uji ulang independen oleh
koordinator, bukan hanya klaim subagent).** Android selesai ditulis lengkap (30/30
di semua layer) tetapi **build-nya belum bisa diverifikasi di mesin ini** (tidak ada
Java/Android SDK) — wajib dicek di Android Studio.

Desa: **Desa Kasomalang Kulon, Kecamatan Kasomalang, Kabupaten Subang**. Pejabat:
Kades **H. AMIRUDIN, S.Pd.I**, Sekdes **APIPUDIN, S.IP**. **Tidak ada NIP.**

## 18 tipe baru + 4 varian

| ID | Nama | Nomor default | Ttd |
|---|---|---|---|
| `kematian` | Surat Keterangan Kematian | 460.3/.../KS/tahun | a.n. Kades — Sekdes |
| `usaha` | Surat Keterangan Usaha (SKU) | 510/.../PEM/tahun | Sekretaris Desa |
| `duda_janda` | Surat Keterangan Duda/Janda | 474.2/.../KS/tahun | Sekretaris Desa (+ ket. Ketua RT) |
| `f101` | F-1.01 Biodata Keluarga | — (tanpa nomor) | Pemohon/Kepala Keluarga |
| `f102` | F-1.02 Pendaftaran Peristiwa Kependudukan | — | Pemohon |
| `f103` | F-1.03 Pendaftaran Perpindahan Penduduk | — | Pemohon |
| `f104` | F-1.04 Pernyataan Tidak Memiliki Dokumen Kependudukan | — | Yang menyatakan (materai) |
| `f105` | F-1.05 SPTJM Perkawinan Belum Tercatat | — | Suami + Istri (materai) |
| `f106` | F-1.06 Pernyataan Perubahan Elemen Data | — | Yang menyatakan |
| `f115` | F-1.15 Permohonan KK Baru WNI | — | Pemohon (Mengetahui Kades/Lurah) |
| `f201` | F-2.01 Pelaporan Pencatatan Sipil | — | Pelapor |
| `f203` | F-2.03 SPTJM Kebenaran Data Kelahiran | — | Yang menyatakan |
| `f204` | F-2.04 SPTJM Kebenaran sebagai Pasangan Suami Istri | — | Yang menyatakan + Pasangan |
| `sptjm_kematian` | SPTJM Kebenaran Data Kematian | — | Yang menyatakan (Mengetahui RT/RW/Kades; materai) |
| `sptjm_perkawinan_perceraian` | SPTJM Perkawinan/Perceraian Belum Tercatat | — | Suami + Istri (materai) |
| `pbi_asesmen` | Asesmen Calon Penerima Bansos KIS-PBI/SKM | — | Pemohon + petugas |
| `pbi_pernyataan` | Surat Pernyataan (PBI-JKN) | 474.4/.../KS/tahun | Yang menyatakan |
| `pbi_tidak_punya_rekening` | Surat Pernyataan Tidak Memiliki Rekening | 474.4/.../KS/tahun | Yang menyatakan |

Varian (opsi di dalam surat lama, bukan kartu baru): `domisili` → jenisDomisili
(Perorangan/Lembaga/Perusahaan, judul berubah); `tidak_mampu` → varianSKTM
(Umum/Sekolah/Daftar Kolektif — kolektif = tabel multi-nama, maks. 20 warga).

Kategori kartu baru: Surat Desa / Formulir Dukcapil / SPTJM / Bansos (PBI-JKN).
Kop: logo desa tetap di surat desa/SPTJM/PBI; formulir Dukcapil seri F pakai kop
Disdukcapil Subang **teks saja** (tanpa logo desa).

## Perubahan per file — WEB (`web-app/`, Next.js 14)

| File | Perubahan |
|---|---|
| `src/types/index.ts` | +18 `LetterType` baru; +4 kategori (Surat Desa, Formulir Dukcapil, SPTJM, Bansos (PBI-JKN)) |
| `src/lib/letter-definitions.ts` | 18 definisi baru (nomor default ikut template); `FormField` baru: `visibleWhen` (field kondisional), `defaultValue`, type `table` (+ `columns`/`minRows`/`maxRows`); `LetterDefinition` baru: `kop` (`desa`/`disdukcapil`), `hideNomor`, `applicantField`; domisili + `jenisDomisili`; SKTM + `varianSKTM` + tabel `daftarKolektif`; helper `isFieldVisible`/`visibleFields`; `defaultLetterData` dukung default select + init tabel |
| `src/lib/letter-content.ts` | 18 kasus baru di `getLetterContent()` (total 30 kasus); judul dinamis (domisili Lembaga/Perusahaan, SKTM Sekolah, Duda/Janda, SPTJM Perkawinan/Perceraian); blok baru `table` + helper `tableFromData`; F-2.01 hanya render seksi jenis pelaporan yang dipilih; kop Disdukcapil (`placeDate` = Subang), `hideNomor`; penandatangan: kematian a.n. Kades–Sekdes; usaha/duda_janda Sekdes; formulir/SPTJM/PBI = pemohon/yang menyatakan |
| `src/components/LetterBody.tsx` | Kop Disdukcapil teks saja; sembunyikan baris Nomor bila `hideNomor`; render blok `table`; `leftSigner.name` bila ada |
| `src/lib/pdf-generator.ts` | Render tabel jsPDF (font 8, grid, header berulang tiap ganti halaman); header kop Disdukcapil teks saja (logo tidak dimuat); `hideNomor`; judul panjang di-wrap; baris tanda tangan panjang di-wrap |
| `src/components/LetterForm.tsx` | Hanya field visible yang tampil/divalidasi; editor tabel (tambah/hapus baris, maks. `maxRows`); nomor disembunyikan bila `hideNomor`; validasi tabel wajib = minimal 1 baris terisi |
| `src/components/LetterTypeSelector.tsx` | Ikon untuk 18 tipe baru |
| `src/components/LetterPreview.tsx` | `recordHistory()` saat Unduh PDF dan Cetak |
| `src/lib/history.ts` **(baru)** | Riwayat Surat localStorage `kasipem_riwayat` (maks. 500 entri): `loadHistory`, `recordHistory`, `deleteHistoryEntry`, `clearHistory`, `getApplicantName` |
| `src/components/RiwayatSurat.tsx` **(baru)** | Modal riwayat (tanggal, nomor, jenis, nama pemohon) + hapus per entri / hapus semua |
| `src/app/page.tsx` | "30 jenis surat & formulir"; kartu sidebar Riwayat Surat + modal |
| `src/lib/store.ts` | **Tidak diubah** — sudah mendelegasikan ke `defaultLetterData(type)` dari `letter-definitions.ts`, jadi 30 tipe langsung jalan |
| `web-app/tsconfig.json` | Hanya efek samping `next build`: `allowJs: true` + format ulang (sama seperti Fase 1); `strict` tetap `true` |
| `README.md` (root) | 12 → 30: fitur, struktur, 18 sub-bagian baru (13–30), seksi Varian, contoh `kasipem_riwayat` |

## Perubahan per file — ANDROID (`android-app/`, Kotlin + Compose)

| File | Perubahan |
|---|---|
| `app/src/main/java/com/kasipem/data/Models.kt` | Enum 12 → **30**; 18 data class kanon baru + data class daftar (`AnggotaKeluarga`, `AnggotaPindah`, `AnakData`, `AnggotaPerubahan`, `AnggotaKkBaru`, `WargaKolektif`); `DomisiliData` + jenisDomisili/field lembaga-perusahaan; `TidakMampuData` + varianSKTM/daftarKolektif; helper `isFormulirDukcapil()`, `F201_JENIS_PELAPORAN` (16), `f201Section()`, `effectivePdfTitle()`, `namaPemohonOf()`; `getEmptyLetterData()` 30 cabang |
| `app/src/main/java/com/kasipem/data/PdfGenerator.kt` | Dispatcher 30 cabang; 18 generator baru; `Signer.SEKDES` baru; `addHeaderDisdukcapil()` (kop teks saja); `addSectionTitle()`, `addSimpleTable()`, `addSignatureColumns()`, `addPlaceDate()`, `ttl()`; `newDocument(landscape)` — F-1.01 landscape; formulir panjang multi-halaman (iText) |
| `app/src/main/java/com/kasipem/data/HistoryStore.kt` **(baru)** | Riwayat Surat lokal: `HistoryEntry` (id, tanggal, nomor, jenis, nama pemohon, timestamp) di SharedPreferences sebagai JSON; `load/add/delete/clearAll` |
| `app/src/main/java/com/kasipem/ui/screens/Forms.kt` | Dispatcher 30 cabang; helper `F`/`D`/`TtlFields`/`NikF`/`SaksiFields` + 6 editor daftar (anggota F-1.01 maks 10, anak, anggota pindah, perubahan F-1.06 maks 7, anggota KK baru, warga kolektif); 18 form baru; `DomisiliForm` & `TidakMampuForm` varian; F-2.01 seksi kondisional |
| `app/src/main/java/com/kasipem/ui/screens/PreviewScreen.kt` | `nomorOf`/`tanggalOf` 30 cabang; kop Disdukcapil teks untuk seri F; judul via `effectivePdfTitle()`; 18 konten preview baru; `PreviewSignature` + varian `SEKDES_ONLY`; setiap PDF/cetak berhasil tercatat ke Riwayat |
| `app/src/main/java/com/kasipem/ui/screens/HistoryScreen.kt` **(baru)** | Layar Riwayat: daftar entri, hapus per baris, hapus semua (dialog konfirmasi), teks kosong bila belum ada |
| `app/src/main/java/com/kasipem/ui/screens/HomeScreen.kt` | `Screen.HISTORY` + tombol ikon riwayat; `iconFor` 18 ikon baru; `accentFor` 4 kategori baru; label "dari 30" |
| Tidak diubah | `MainActivity.kt`, `build.gradle.kts` (tidak perlu — tidak ada dependensi baru; Riwayat pakai SharedPreferences + kotlinx.serialization yang sudah ada), `Typography.kt`, `AndroidManifest.xml`, `strings.xml`, `themes.xml` |

## Hasil build WEB — LOLOS (diverifikasi koordinator, bukan hanya klaim)

Dijalankan koordinator di `~/workspace/kasipem-push/web-app` (setelah klaim subagent web):
```
npm run build
✓ Compiled successfully
✓ Generating static pages (4/4)
Route (app): /  146 kB / First Load JS 233 kB (Fase 1: 134 kB / 222 kB)
```
Catatan lingkungan: `npm install` polos **gagal** di sandbox ini (`EPERM chown`
saat link bin). Workaround yang dipakai subagent web: `npm install
--no-bin-links --ignore-scripts` (153 paket) + symlink manual
`node_modules/.bin/next` + `tsc` agar perintah literal `npm run build` jalan.
Ini quirk sandbox, bukan masalah repo — `node_modules/` ter-`.gitignore`, bukan
bagian commit.

## Matriks tes — 36/36 LOLOS (uji ulang independen oleh koordinator)

Metode: harness `/tmp/kasipem-test/test-30.js` (bukan deliverable) — kompilasi
ulang modul dari **source terbaru** dengan `tsc` proyek, lalu per kasus:
render `LetterBody` (preview) + `generatePdf` dengan logoLoader dari disk; cek
judul, nomor, nama uji, penandatangan, tanpa baris `NIP:`, tanpa `undefined`,
kop benar (logo desa / Disdukcapil teks), ukuran/halaman PDF; cek print CSS di
`globals.css`. Data uji **fiktif** ("Budi Santoso" dkk, NIK `3213260101900001`) —
bukan data contoh asli. Koordinator menjalankan ulang harness yang sama:
`RINGKASAN: 36/36 kasus lolos penuh.` (exit code 0).

| # | Tipe | Preview | PDF | Catatan |
|---|---|---|---|---|
| 1 | domisili (474/PEM) | ✅ | ✅ 75,6 KB, 1 hal, 2 logo | Perorangan, H. AMIRUDIN |
| 2 | pisah_kk | ✅ | ✅ 75,5 KB, 1 hal | a.n. Kades — APIPUDIN |
| 3 | skt_dayat | ✅ | ✅ 76,8 KB, 1 hal | |
| 4 | beda_luas | ✅ | ✅ 76,1 KB, 1 hal | |
| 5 | beda_nama | ✅ | ✅ 74,8 KB, 1 hal | |
| 6 | belum_menikah | ✅ | ✅ 75,7 KB, 1 hal | |
| 7 | tidak_mampu | ✅ | ✅ 76,4 KB, 1 hal | Mengetahui Ketua RW |
| 8 | ahli_waris | ✅ | ✅ 76,4 KB, 1 hal | |
| 9 | nikah_tidak_tercatat | ✅ | ✅ 75,5 KB, 1 hal | |
| 10 | pemakaman | ✅ | ✅ 75,3 KB, 1 hal | Seri 460.3/KS (diperbaiki 2026-11-15 atas konfirmasi user; sebelumnya 474/PEM) |
| 11 | tidak_memiliki_rumah | ✅ | ✅ 75,2 KB, 1 hal | |
| 12 | undangan | ✅ | ✅ 75,9 KB, 1 hal | Nomor manual |
| 13 | kematian (460.3/KS) | ✅ | ✅ 77,6 KB, 1 hal | a.n. Kades — APIPUDIN |
| 14 | usaha (510/PEM) | ✅ | ✅ 75,0 KB, 1 hal | Ttd Sekdes |
| 15 | duda_janda (474.2/KS) | ✅ | ✅ 75,1 KB, 1 hal | Judul dinamis "DUDA" |
| 16 | f101 | ✅ | ✅ 15,3 KB, 1 hal (3 anggota); 31,5 KB, 2 hal (10 anggota) | Kop Disdukcapil teks, 0 logo, tanpa Nomor; header tabel berulang terverifikasi |
| 17 | f102 | ✅ | ✅ 5,8 KB, 1 hal | Kop Disdukcapil, 0 logo |
| 18 | f103 | ✅ | ✅ 10,1 KB, 1 hal | Tabel anggota pindah tampil |
| 19 | f104 | ✅ | ✅ 6,1 KB, 1 hal | |
| 20 | f105 | ✅ | ✅ 9,4 KB, 1 hal | Ttd Suami + Istri, tabel anak |
| 21 | f106 | ✅ | ✅ 9,5 KB, 1 hal | Tabel anggota + perubahan |
| 22 | f115 | ✅ | ✅ 9,3 KB, 1 hal | Mengetahui Kades/Lurah |
| 23 | f201 (Kelahiran) | ✅ | ✅ 11,7 KB, 2 hal | Hanya seksi Kelahiran; seksi lain tersembunyi |
| 24 | f203 | ✅ | ✅ 12,6 KB, 2 hal | |
| 25 | f204 | ✅ | ✅ 9,7 KB, 1 hal | Ttd + Pasangan |
| 26 | sptjm_kematian | ✅ | ✅ 79,4 KB, 2 hal | Mengetahui RT/RW/Kades; materai |
| 27 | sptjm_perkawinan_perceraian | ✅ | ✅ 77,9 KB, 1 hal | Judul "PERKAWINAN BELUM TERCATAT" |
| 28 | pbi_asesmen | ✅ | ✅ 77,9 KB, 2 hal | 11 pertanyaan tampil |
| 29 | pbi_pernyataan (474.4/KS) | ✅ | ✅ 75,0 KB, 1 hal | |
| 30 | pbi_tidak_punya_rekening (474.4/KS) | ✅ | ✅ 74,8 KB, 1 hal | |
| V1 | domisili Lembaga | ✅ | ✅ 74,6 KB, 1 hal | Judul DOMISILI LEMBAGA |
| V2 | domisili Perusahaan | ✅ | ✅ 75,2 KB, 1 hal | Judul DOMISILI PERUSAHAAN |
| V3 | SKTM Sekolah | ✅ | ✅ 76,4 KB, 1 hal | Judul "(SEKOLAH)" |
| V4 | SKTM Kolektif | ✅ | ✅ 76,4 KB, 1 hal | Tabel 2 warga tampil |
| X1 | f201 Kematian | ✅ | ✅ 12,0 KB, 2 hal | Seksi Kematian tampil, Kelahiran tersembunyi |
| X2 | f201 Perkawinan | ✅ | ✅ 10,6 KB, 2 hal | Seksi Perkawinan tampil, lainnya tersembunyi |

Print CSS (verifikasi statis, sama seperti Fase 1): `@page`, `size: A4`,
`margin: 15mm 20mm`, `@media print`, `.print-area` — semua ada di
`globals.css`. **Belum diuji visual di browser sungguhan** — perlu sekali cek
pratinjau cetak A4 di browser + cek `RiwayatSurat` di browser (localStorage).

### Matriks Android (verifikasi baca-ulang + spot-check koordinator)

Subagent Android memeriksa 13 layer per tipe (enum, data class,
getEmptyLetterData, namaPemohonOf, form fn, form dispatch, PDF fn, PDF dispatch,
preview fn, preview dispatch, nomorOf, tanggalOf, iconFor): **30/30 tipe ×
13/13 layer = ALL OK**. Spot-check independen koordinator: enum 30 entri;
PDF dispatch 30 cabang; form dispatch 30 cabang; preview 90 cabang `when`
(30 tipe × 3); kurung/paren/bracket seimbang (selisih 0) di 7 file `.kt` yang
diubah/dibuat; tidak ada referensi ke tipe lama yang hilang.

## Hasil build ANDROID — BELUM TERVERIFIKASI (jujur)

- `java`/`gradle` **tidak ditemukan** di mesin ini; `ANDROID_HOME` kosong;
  `gradlew` **tidak ada** di tree. Jadi `./gradlew assembleDebug` tidak bisa
  dijalankan di sini. Ini keterbatasan lingkungan, bukan bukti kode rusak.
- Yang **masih butuh dicek di Android Studio** (JDK 17 + SDK 34 + `gradlew` dari
  template proyek): `./gradlew assembleDebug`; render 30 preview + generate 30
  PDF di emulator/perangkat (logo kop tampil, teks tidak terpotong, tombol
  PDF/Cetak, layar Riwayat); ikon extended (`Groups`, `Article`,
  `RequestQuote` — dari `material-icons-extended 1.6.0`, sudah di dependensi)
  pastikan resolve saat compile.

## Grep akhir — BERSIH (dicek ulang koordinator)

`web-app/src` + `android-app/app/src`: tidak ada `1234567890`,
`tujuanSekolah`, `DATA DESA`, `VLOOKUP`; tidak ada nama/NIK warga asli dari
contoh terisi (dicek: HERMAN HERMAWAN, SAEPUL HAYAT, LINA HERLINA, APEP RAHAYU,
SAEPUROHMAN, RIO FADILAH, DIDI RIYADI, NENDEN KOMALA + NIK contoh); tidak ada
referensi tanda tangan/stempel asli (`sekdes.png`, `sekdes 2`, `file/sekdes`)
di luar logo kop desa yang memang dipakai (`kop/sekdes.png` = logo Subang).
Literal `NIP:` hanya di 2 tempat kondisional Fase 1 (dirender hanya bila
`signer.nip` terisi; pejabat desa tidak punya NIP — harness menegaskan 0
kemunculan di semua 36 PDF + preview). Folder `file/` **tidak berubah**
(tidak ada di `git status`).

## Penomoran — bentrok & catatan (jangan karang seri baru)

1. **Kematian vs Pemakaman (seri 460.3):** template `SURAT KEMATIAN.doc` dan
   `SURAT PEMAKAMAN.docx` sama-sama memakai seri `460.3/.../KS/`. **TERJAWAB
   2026-11-15 (user): Pemakaman = `460.3/.../KS/`** — nomor pemakaman di aplikasi
   diperbaiki dari `474/PEM` ke `460.3/KS` (web + Android + README) dan ikut
   commit lanjutan setelah commit Fase 2. Kematian tetap `460.3/.../KS/`.
2. **PBI `474.4/.../KS/` dipakai dua tipe** (`pbi_pernyataan` dan
   `pbi_tidak_punya_rekening`) — sesuai bundle PBI-JKN di template (keduanya
   No `474.4/.../KS/2025`), bukan karangan.
3. **F-2.01 "Pelaporan dari Luar Wilayah NKRI"** memakai seksi peristiwa umum
   (tanggal/tempat/keterangan); varian hal. 8 template (form luar negeri) tidak
   dimodelkan terpisah.
4. **F-1.01 padat (web):** tabel 11 kolom di A4 portrait, font 8 — masih
   terbaca, tapi padat. Android memakai landscape untuk F-1.01 (beda orientasi
   dengan web — sengaja, agar generator web tetap satu orientasi).

## Yang masih butuh user

1. **Token GitHub akun xxcode2** untuk push commit Fase 2 — agen tidak push
   apa pun (token lama mungkin sudah di-revoke setelah push Fase 1; bila begitu,
   butuh token baru). Cara push dari `~/workspace/kasipem-push/` setelah
   verifikasi: `git log` (pastikan HEAD = commit Fase 2) lalu `git push origin
   main`.
2. **Redeploy Vercel** setelah push (https://kasipem.vercel.app) — biasanya
   auto-deploy dari `main`; cek hasil deploy-nya.
3. **Konfirmasi penomoran desa:** ✅ SELESAI 2026-11-15 — Pemakaman = `460.3/.../KS/` (user), sudah diperbaiki di web + Android + README.
4. **Cek cetak sekali di browser** (web): buka preview surat → Cetak → lihat
   pratinjau cetak A4; cek juga modal Riwayat Surat (localStorage).
5. **Cek Android di Android Studio** (poin di atas).
6. **Tanda tangan & stempel asli** (`file/sekdes.png`, `file/sekdes 2.png`):
   tetap TIDAK dipakai otomatis. Bila desa menyetujui pemakaian, beri tahu —
   baru dibuatkan opsi tempel tanda tangan/stempel di PDF.
7. **Auto-isi dari NIK / DATA DESA:** tetap TIDAK ada (privasi). Bila di masa
   depan diinginkan, itu keputusan terpisah soal penyimpanan lokal yang aman +
   cara update data warga.
8. **Riwayat Surat** dicatat per aksi klik Unduh/Cetak (web) dan per aksi
   PDF/cetak (Android) — klik PDF lalu Cetak = 2 entri. Bila ingin dedup
   (satu entri per surat), beri tahu.

## Catatan kebersihan & proses

- Tidak ada data contoh asli di file yang diserahkan; tidak ada `1234567890`
  atau `tujuanSekolah` tersisa.
- `file/` (template DOCX/PDF/XLS/XLSM asli) **tidak diubah** sama sekali.
- `node_modules/` dan `.next/` ter-`.gitignore`, bukan bagian commit.
  `web-app/next-env.d.ts` (auto-generate) tidak ikut commit.
- Dua subagent bekerja paralel dan tidak saling menyentuh direktori satu sama
  lain (web: `web-app/` + `README.md`; Android: `android-app/`). Satu-satunya
  tumpang tindih potensial — `README.md` — hanya disentuh pekerja web.
- Satu `exec` di pekerja Android tercatat gagal (exitCode 2, perintah tidak
  teridentifikasi di laporannya); satu `exec` di pekerja web tercatat gagal
  (exitCode 1 = `npm install` polos, `EPERM chown` — sudah dijelaskan di atas
  dan diatasi dengan workaround). Tidak ada dampak yang terdeteksi pada hasil;
  semua verifikasi independen koordinator lolos.
- Tanda tangan & stempel asli tidak pernah di-embed; blok tanda tangan tetap
  nama + jabatan saja.
