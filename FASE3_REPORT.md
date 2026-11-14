# KASIPEM — FASE 3 REPORT (Kop dinamis, font A4, nomor kosong, audit ahli waris)

**Repo:** https://github.com/xxcode2/kasipem — HEAD sebelum Fase 3: `9a9e87d`
(Fase 2 + perbaikan nomor Pemakaman)
**Tanggal:** 2026-11-15 (01:0x–01:2x WIB)
**Lokasi kerja:** `~/workspace/kasipem-push/` (clone lengkap, branch `main`)
**Spesifikasi:** `~/workspace/kasipem-study/FASE3_BRIEF.md`
**Sumber kebenaran:** `~/workspace/kasipem-study/FILE_INVENTORY.md` +
`~/workspace/kasipem-push/file/SURAT KETERANGAN AHLI WARIS 2021.docx` (read-only)
**Scope:** revisi user 2026-11-15 01:03 WIB (kop dinamis, font A4, nomor kosong,
full audit ahli waris)
**Serahan:** SATU commit LOKAL di clone ini (belum di-push — tidak ada token
GitHub xxcode2 di mesin ini; push dilakukan Kai setelah verifikasi)

## Ringkasan

Semua 4 tugas selesai di web DAN Android, dengan satu arsitektur yang sama:
kop ditentukan dari penandatangan oleh SATU fungsi (`kopFor`), font isi 12pt
untuk A4, nomor surat default kosong (garis titik-titik), dan Surat Keterangan
Ahli Waris ditulis ulang mengikuti contoh DOCX — termasuk daftar ahli waris
dinamis 1–20 baris.

**Web terverifikasi lolos build + harness 176/176 lolos (dijalankan ulang
independen oleh koordinator, bukan hanya klaim pekerja).** Kop web ↔ Android
selaras 30/30 (dicek silang oleh koordinator). Android selesai ditulis lengkap
tetapi **build-nya belum bisa diverifikasi di mesin ini** (tidak ada Java/Android
SDK) — wajib dicek di Android Studio.

Desa: **Desa Kasomalang Kulon, Kecamatan Kasomalang, Kabupaten Subang**. Pejabat:
Kades **H. AMIRUDIN, S.Pd.I**, Sekdes **APIPUDIN, S.IP**. **Tidak ada NIP.**

## 1. Kop dinamis per penandatangan

Satu fungsi `kopFor` dipakai preview + PDF + cetak (web: `letter-content.ts`;
Android: `Models.kt`, dipakai `PdfGenerator.addHeaderFor()` + `PreviewScreen`).
Tidak ada cabang kop terpisah yang bisa drift.

| Penandatangan | Kop | Tipe (jumlah) |
|---|---|---|
| Kepala Desa (ttd langsung) | Logo **Garuda SAJA, di tengah paling atas** (web PDF lebar ~28 mm, proporsional; preview 28 mm), teks kop rata tengah di bawahnya, garis ganda | 11 tipe: domisili, skt_dayat, beda_luas, beda_nama, belum_menikah, tidak_mampu, ahli_waris, nikah_tidak_tercatat, pemakaman, tidak_memiliki_rumah, undangan |
| a.n. Kades oleh Sekdes | Logo **Subang (`kop/sekdes.png`) di kiri**, tanpa Garuda | 2 tipe: pisah_kk, kematian |
| Sekdes langsung | Logo Subang di kiri, tanpa Garuda | 2 tipe: usaha, duda_janda |
| Formulir Dukcapil seri F | Kop Disdukcapil **teks saja** (tetap) | 10 tipe: f101–f204 |
| SPTJM / PBI (penandatangan utama warga; Kades hanya "Mengetahui") | Kop **dua logo** seperti semula (tetap) | 5 tipe: sptjm_kematian, sptjm_perkawinan_perceraian, pbi_asesmen, pbi_pernyataan, pbi_tidak_punya_rekening |

Catatan implementasi:
- Web menghitung `kopVariant` dari **signer akhir** (`resolveSigner`), bukan
  signer dasar — bug sempat membuat SPTJM/PBI salah jadi `garuda-center`,
  tertangkap harness dan sudah diperbaiki.
- Android menghitung dari tipe (`when` 30 cabang, tanpa tumpang tindih) dan
  dicocokkan dengan `Signer` di footer untuk 15 tipe bertanda tangan.
- Logo dimuat via dataURL (web) / assets (Android) dengan fallback kop teks
  bila gagal — tidak crash, tidak PDF kosong.
- Asumsi (sesuai brief): penandatangan Sekdes langsung → kop Sekdes.
- Pertanyaan terbuka: SPTJM/PBI tetap dua logo karena aturan user hanya
  mencakup dua kasus kop (Kades / a.n. Sekdes) — bila user ingin aturan
  berbeda untuk SPTJM/PBI, beri tahu.

## 2. Font A4 (12pt)

| Elemen | Web preview | Web PDF | Android PDF |
|---|---|---|---|
| Isi surat (body) | 12pt (`.letter-sheet`, layar + print) | 12pt | 12pt |
| Judul surat | 15pt bold | 15pt bold | 14pt bold |
| Teks kop (baris atas) | 12pt bold | 12pt bold | 12pt bold |
| Teks kop (baris desa) | 14pt bold | 14pt bold | 14pt bold |
| Baris alamat kop | 12pt | 11pt | 10pt |
| Nomor surat | 11pt | 11pt | 11pt |
| Tabel padat (F-1.01, F-2.01, tabel anggota) | 10pt | 8pt | 8–9pt (F-1.01 dinaikkan 7pt → 8pt) |

Catatan kecil (bukan pemblokir): baris alamat kop sedikit berbeda antar
platform (web PDF 11pt, Android 10pt, web preview 12pt) — baris sekunder,
di bawah rentang 12–14pt di brief. Bila user ingin disamakan persis, beri tahu
angka yang diinginkan.

## 3. Nomor surat default kosong

- `defaultNomorSurat()` kini mengembalikan `''` untuk **semua 30 tipe**
  (web + Android). Pola nomor lama pindah ke `nomorPlaceholder()` /
  `nomorPattern()` yang **hanya** jadi placeholder di form
  (mis. `460.3/.../KS/2026`).
- Preview/PDF: nomor kosong → `Nomor : ` + garis titik-titik (web PDF 48 titik,
  preview 20 titik; Android PDF 48 titik). Tidak ada `undefined`, tidak ada
  nomor default yang muncul sendiri (dicek 30/30).
- Formulir F yang `hideNomor` tetap tanpa baris Nomor; bila nomor F diisi
  manual, baris muncul (Android `hasNomorBaris()`).
- Validasi "nomor wajib diisi" dihapus; label nomor jadi opsional.
- Riwayat Surat mencatat nomor apa adanya (boleh kosong; tampil `-` bila kosong).
- Nomor seri TIDAK berubah sebagai pola (kematian & pemakaman tetap
  `460.3/.../KS/`; ahli waris tetap `474/PEM` sebagai placeholder — lihat
  pertanyaan terbuka §5).

## 4. FULL AUDIT — Surat Keterangan Ahli Waris

### 4a. Sumber contoh

`file/SURAT KETERANGAN AHLI WARIS 2021.docx` (file asli TIDAK diubah;
ekstraksi python-docx ke `/tmp`, bukan deliverable). Hitungan ulang:
**490 paragraf, 69 tabel** (angka "164 paragraf" di FILE_INVENTORY.md §3
sudah kedaluwarsa). Isinya BUKAN satu surat: **10 contoh Surat Keterangan
Ahli Waris** (ditandatangani Kades) + dokumen pendamping (Surat Kuasa Ahli
Waris 4×, Ikrar Wakaf 1×, Surat Kuasa dan Pernyataan Ahli Waris 1×, Surat
Pernyataan Ahli Waris 2×, Keterangan Pernyataan Kematian 1×). Audit memakai
pola **Surat Keterangan Ahli Waris yang ditandatangani Kades** (10 salinan)
sebagai acuan.

Pola contoh (diringkas): judul `SURAT KETERANGAN AHLI WARIS` 14pt bold; kop
teks desa tanpa logo; pembuka narasi (nama almarhum, hari/tanggal/tempat
meninggal, rujukan nomor Surat Kematian, pasangan yang masih hidup, jumlah
anak); "Dengan demikian susunan Ahli Waris adalah sebagai berikut:"; tabel
ahli waris **No | Nama | Hubungan Keluarga | Tanda Tangan** (1–7 baris;
varian belakangan menambah kolom Tempat Tanggal Lahir); kalimat pengesahan;
tabel saksi 2 orang (Ketua RT + Ketua RW); "Untuk Keperluan : …"; penutup
"Demikian surat ini kami buat dengan sebenarnya dan kepada yang bersangkutan
menjadi tahu adanya."; tanda tangan kiri **Mengetahui, Camat Kasomalang**
(No Agenda + tanggal) | kanan **Kepala Desa Kasomalang Kulon** (No Agenda
di sisi Kades).

> ⚠️ Semua nama/NIK di contoh adalah warga asli — HANYA referensi format.
> Tidak ada satu pun yang dipakai sebagai default/placeholder/data uji di
> aplikasi. Data uji Fase 3 memakai nama/NIK fiktif ("Budi Santoso" dkk).

### 4b. Tabel audit (implementasi SEBELUM Fase 3 vs contoh)

| # | Item | Status | Temuan | Perbaikan Fase 3 |
|---|---|---|---|---|
| 1 | Judul | ✅ Bagus | `SURAT KETERANGAN AHLI WARIS` sama persis; contoh 14pt bold, aplikasi kini 15pt (dalam rentang 14–16pt). | Tidak diubah. |
| 2 | Kop | ❌ Salah | Aplikasi lama: dua logo (Garuda kiri + Subang kanan). Contoh: kop TEKS saja, tanpa logo. | Mengikuti **aturan user Fase 3** (mengalahkan contoh): ttd Kades → Garuda tengah saja. Teks kop contoh dipertahankan. |
| 3 | Nomor surat | 🔧 Perlu diperbaiki | Aplikasi lama default `474/001/PEM/2026`. Contoh: sisi Kades `No Agenda: 141/…/PEM/…` (5 salinan), sisi Camat `474.3/…/Pem/…`. `474/PEM` tidak sama persis dengan keduanya. | Default dikosongkan (semua tipe). Placeholder dipertahankan `474/PEM` — **butuh konfirmasi user**: ganti ke `141/…/PEM/…` atau tetap? |
| 4 | Pembuka / intro | ❌ Salah | Aplikasi: intro generik + baris field NIK/alamat peninggal. Contoh: satu paragraf narasi lengkap. | Ditulis ulang jadi paragraf narasi sesuai contoh; segmen kosong dihilangkan otomatis (tanpa pasangan → kalimat pasangan tidak tampil; tanpa nomor surat kematian → rujukan tidak tampil). |
| 5 | Nama peninggal | ✅ Bagus | Ada, wajib, tampil di pembuka. | Tetap (`namaPeninggal`). |
| 6 | Hari / tanggal / tempat meninggal | ❌ Salah | Tidak ada di aplikasi lama. | Ditambah: `hariMeninggal`, `tanggalMeninggal`, `tempatMeninggal` (wajib). |
| 7 | Nomor Surat Kematian | ❌ Salah | Tidak ada di aplikasi lama. Contoh menyebutkannya di ~4 salinan. | Ditambah: `nomorSuratKematian` (opsional). |
| 8 | NIK peninggal | 🔧 Perlu diperbaiki | Ada di aplikasi lama (wajib), **tidak ada** di contoh surat mana pun. | **Dihapus** dari form + konten. |
| 9 | Alamat peninggal | 🔧 Perlu diperbaiki | Ada di aplikasi lama (wajib), **tidak ada** di contoh (alamat hanya di dokumen pendamping, bukan surat ini). | **Dihapus** dari form + konten. |
| 10 | Nama pasangan + status masih hidup | ❌ Salah | Tidak ada di aplikasi lama. Contoh selalu menyebutkannya. | Ditambah: `namaPasangan` + `sebutanPeninggal` (Almarhum/Almarhumah → label Istri/Suami otomatis). |
| 11 | Jumlah anak | ❌ Salah | Tidak ada di aplikasi lama. Contoh selalu menyebutkannya ("telah dilahirkan N orang anak"). | Ditambah: `jumlahAnak` (angka, opsional; Android: dihitung dari hubungan "Anak", fallback daftar-minus-pasangan). |
| 12 | Daftar ahli waris | ❌ Salah | Aplikasi lama: **2 field tetap** (`namaAhliWaris1/2` + NIK + hubungan). Contoh: tabel **1–7 baris**, kolom No / Nama / Hubungan Keluarga / Tanda Tangan (+ varian: Tempat Tanggal Lahir). | **Tabel dinamis** `daftarAhliWaris` (tambah/hapus baris, min 1, maks 20), kolom: Nama, NIK, Tempat/Tgl Lahir, Hubungan Keluarga, Tanda Tangan (kosong — ditandatangani). NIK dimasukkan karena ada di daftar ahli waris dokumen pendamping (Surat Kuasa) untuk orang yang sama, meski tidak ada di tabel utama contoh. **Tidak ada kolom bagian/hak** — contoh tidak memilikinya. |
| 13 | Kalimat pengesahan ahli waris | ❌ Salah | Tidak ada di aplikasi lama. | Ditambah: pengesahan + "Tidak ada ahli waris selain yang tercantum di dalam surat keterangan ini, sesuai pengakuannya." |
| 14 | Saksi (Ketua RT/RW) | ❌ Salah | Tidak ada di aplikasi lama. Contoh: selalu 2 saksi + tabel. | Ditambah: `namaSaksi1/2` + `jabatanSaksi1/2`, dirender sebagai tabel bila terisi. |
| 15 | Untuk Keperluan | ❌ Salah | Tidak ada di aplikasi lama. Contoh selalu ada. | Ditambah: `keperluan` (wajib). |
| 16 | Penutup | ❌ Salah | Aplikasi: penutup generik. Contoh: "Demikian surat ini kami buat dengan sebenarnya dan kepada yang bersangkutan menjadi tahu adanya." | Diganti ke kalimat contoh. |
| 17 | Penandatangan | 🔧 Perlu diperbaiki | Aplikasi: Kades (kanan) saja, tanpa blok kiri. Contoh: kiri Mengetahui, Camat Kasomalang (No Agenda + tanggal) + kanan Kades. | Ditambah penandatangan kiri: Mengetahui, Camat Kasomalang (nama kosong → garis titik-titik). |
| 18 | Dokumen pendamping dalam bundle | 🔧 Perlu diperbaiki (di luar scope) | Bundle berisi jenis terpisah: Surat Kuasa Ahli Waris, Ikrar Wakaf, Surat Kuasa dan Pernyataan Ahli Waris, Surat Pernyataan Ahli Waris, Keterangan Pernyataan Kematian. Bukan bagian dari Surat Keterangan Ahli Waris. | **Tidak dimodelkan** di Fase 3. Bila diinginkan, jadi tipe surat baru terpisah (butuh keputusan user). |
| 19 | Inkonsistensi di contoh (bukan kesalahan aplikasi) | — | Tanggal meninggal berbeda antara surat dan Surat Kuasa; nama istri berbeda antar salinan; ejaan nama berbeda; ada nama terduplikasi dalam satu tabel; TTL orang yang sama berbeda antara Surat Kuasa dan daftar. | Dicatat saja — tidak diikuti, tidak "diperbaiki" (itu isi contoh, bukan aplikasi). |

### 4c. Ringkasan perbaikan ahli waris

- **Dihapus:** `nikPeninggal`, `alamatPeninggal`, `namaAhliWaris1/2`,
  `nikAhliWaris1/2`, `hubunganAhliWaris1/2`, `keterangan` (generik).
- **Ditambah:** `sebutanPeninggal`, `hariMeninggal`, `tanggalMeninggal`,
  `tempatMeninggal`, `nomorSuratKematian`, `namaPasangan`, `jumlahAnak`,
  tabel dinamis `daftarAhliWaris` (1–20 baris), `namaSaksi1/2`,
  `jabatanSaksi1/2`, `keperluan`; Android: seksi **Lampiran — Data Ahli
  Waris** (detail per ahli waris + Data Peninggal, hanya bila ada data —
  cermin lembar detail DOCX).
- **Diubah:** intro → narasi contoh; penutup → kalimat contoh;
  penandatangan → + Mengetahui Camat (kiri); `applicantField` →
  `namaPeninggal`.
- Varian pembuka oleh ahli waris sendiri ("Yang bertanda tangan dibawah ini
  para ahli waris yang sah …", 4 salinan belakangan) TIDAK dimodelkan —
  tipe aplikasi tetap Kades-signed (butuh keputusan user bila diinginkan).

## Perubahan per file

### WEB (`web-app/`, Next.js 14) — 6 file

| File | Perubahan |
|---|---|
| `src/lib/letter-content.ts` | SATU fungsi `kopFor(signer)` → `KopVariant` (`garuda-center` / `subang-left` / `dua-logo` / `disdukcapil`), dihitung dari **signer akhir**; `SignerBlock.kind` (`kades`/`sekdes-an`/`sekdes`/`pemohon`); kasus `ahli_waris` ditulis ulang mengikuti contoh DOCX |
| `src/lib/letter-definitions.ts` | `defaultNomorSurat()` → selalu `''`; helper baru `nomorPlaceholder()`; definisi `ahli_waris` baru (field lama dihapus, tabel dinamis `daftarAhliWaris` min 1 maks 20, `applicantField` → `namaPeninggal`) |
| `src/components/LetterBody.tsx` | 4 cabang kop dari `content.kopVariant` (Garuda tengah 28 mm / Subang kiri 20 mm / dua logo / Disdukcapil teks); komponen `KopText` bersama; font pt: isi 12pt, judul 15pt, kop 12pt (desa 14pt), tabel 10pt; logo gagal → disembunyikan |
| `src/lib/pdf-generator.ts` | `addHeader()` 4 cabang yang sama + helper `addKopText()`; judul 13→15pt, kop 13→12/14pt, alamat kop 11pt; logo hanya dimuat bila varian membutuhkannya; fallback kop teks bila logo gagal |
| `src/app/globals.css` | `.letter-sheet` 12pt (layar + print `@media print`) |
| `src/components/LetterForm.tsx` | Validasi "nomor wajib diisi" dihapus; label nomor jadi opsional + placeholder via `nomorPlaceholder()` |

### ANDROID (`android-app/`, Kotlin + Compose) — 4 file

| File | Perubahan |
|---|---|
| `app/src/main/java/com/kasipem/data/Models.kt` | `KopStyle` + SATU fungsi `kopFor(type)` (30 cabang, tanpa tumpang tindih); `defaultNomorSurat()` → `""`; `nomorPattern()` hanya placeholder; `AhliWarisItem` (nama, NIK, JK, TTL, hubungan, alamat) + `daftarAhliWaris` dinamis; field baru ahli waris (hari/tanggal/tempat meninggal, nomor surat kematian, pasangan, saksi) |
| `app/src/main/java/com/kasipem/data/PdfGenerator.kt` | `addHeaderFor()` (kop dinamis, logo assets + fallback teks); font pt disamakan web (isi 12, judul 14, kop 12/14, alamat 10); ahli waris: narasi pembuka, tabel dinamis, lampiran detail, saksi, Mengetahui Camat |
| `app/src/main/java/com/kasipem/ui/screens/Forms.kt` | `AhliWarisEditor` (tambah/hapus baris); form ahli waris baru; placeholder nomor via `nomorPattern()` |
| `app/src/main/java/com/kasipem/ui/screens/PreviewScreen.kt` | Kop dinamis di preview (72dp Garuda tengah); font preview 12sp; ahli waris dinamis + lampiran + saksi; nomor kosong → garis titik-titik |

## Hasil build WEB — LOLOS (verifikasi koordinator, bukan hanya klaim)

Dijalankan koordinator di `~/workspace/kasipem-push/web-app`:
```
npm run build
✓ Compiled successfully
✓ Generating static pages (4/4)
Route (app): /  147 kB / First Load JS 235 kB (Fase 2: 146 kB / 233 kB)
```
(`npm install` tidak perlu diulang; `node_modules` Fase 2 masih ada.)

## Matriks tes — 176/176 LOLOS (harness dijalankan ulang koordinator)

Metode: harness `/tmp/kasipem-fase3/test-fase3.js` (bukan deliverable) — kompilasi
ulang modul dari **source terbaru** dengan `tsc` proyek, lalu render `LetterBody`
(preview SSR) + `generatePdf` dengan logoLoader dari disk; data uji **fiktif**
("Budi Santoso" dkk, NIK `3213260101900001`). Koordinator menjalankan ulang
harness yang sama (dengan `NODE_PATH` ke `node_modules` web-app):
`RINGKASAN: 176 lolos, 0 gagal.` (exit code 0). Render PDF ke PNG juga dicek
mata: Garuda tengah (domisili), Subang kiri (usaha), dua logo (SPTJM), tabel
ahli waris + saksi — semua benar.

| Uji | Hasil |
|---|---|
| Kop — Kades `domisili` (Garuda tengah 28 mm, 1 gambar), a.n. Kades `pisah_kk` + `kematian`, Sekdes `usaha` + `duda_janda` (Subang kiri, 1 gambar, tanpa Garuda), F `f101` (teks, 0 gambar), SPTJM `sptjm_kematian` + PBI `pbi_pernyataan` (dua logo, 2 gambar) — preview + PDF | ✅ 8/8 kelompok |
| Nomor kosong — 30/30 tipe: `defaultLetterData().nomorSurat === ''`, tidak ada `undefined`, tidak ada pola `001` bocor, baris nomor tampil garis titik-titik; `hideNomor` tetap tanpa baris nomor; placeholder pola tetap ada | ✅ 30/30 (+ 19 cek placeholder) |
| Ahli waris 1 / 2 / 5 baris — preview + PDF: narasi, pasangan + jumlah anak, nomor surat kematian, semua nama tampil, saksi, keperluan, penutup contoh, Mengetahui Camat, tanpa field lama, tanpa `undefined`; varian almarhumah (label "Suami") dan tanpa pasangan (kalimat pasangan hilang) | ✅ semua; PDF 2 halaman untuk surat panjang |
| Font — preview 12/15/14/12/10pt, PDF operator font 12/14/15, tabel PDF 8pt, CSS `.letter-sheet` 12pt layar + print | ✅ |
| Grep akhir — tidak ada `1234567890`, `tujuanSekolah`, `DATA DESA`, field ahli waris lama, `/ 001 /` | ✅ |

### Kop web ↔ Android — selaras 30/30 (cek silang koordinator)

Koordinator menghitung `kopFor` web per tipe (dari `getLetterContent` +
`defaultLetterData`) dan membandingkannya dengan tabel `kopFor` Android:
**30/30 sama, 0 mismatch** (11 garuda-center, 4 subang-left, 10 disdukcapil,
5 dua-logo = 30).

### Matriks Android (verifikasi baca-ulang + spot-check koordinator)

Pekerja Android memeriksa: `kopFor` 30/30 tanpa tumpang tindih, kop konsisten
dengan `Signer` di footer untuk 15 tipe bertanda tangan, 30/30 form + 30/30
generator PDF + 31 fungsi konten preview ada, tidak ada referensi lama ke 2
ahli waris tetap (kecuali komentar dokumentasi). Spot-check koordinator:
kurung/paren/bracket seimbang (selisih 0) di 4 file `.kt` yang diubah;
`addHeaderFor` dipakai di semua generator; `AhliWarisEditor` + tabel dinamis
ada di `Forms.kt`/`PdfGenerator.kt`.

## Hasil build ANDROID — BELUM TERVERIFIKASI (jujur)

- `java`/`gradle`/`kotlinc` **tidak ditemukan** di mesin ini; `ANDROID_HOME`
  kosong; `gradlew` **tidak ada** di tree. Jadi `./gradlew assembleDebug`
  tidak bisa dijalankan di sini. Ini keterbatasan lingkungan, bukan bukti kode
  rusak.
- Yang **masih butuh dicek di Android Studio** (JDK 17 + SDK 34 + `gradlew`
  dari template proyek): `./gradlew assembleDebug`; render preview + PDF per
  kelompok kop (Kades / a.n. Sekdes / Sekdes / F / SPTJM-PBI), ahli waris
  dengan 1, 2, dan 5 ahli waris (tabel + lampiran multi-halaman), nomor kosong
  di semua tipe.

## Grep akhir — BERSIH (dicek ulang koordinator)

`web-app/src` + `android-app/app/src`: tidak ada `1234567890`,
`tujuanSekolah`, `DATA DESA`, `VLOOKUP`; tidak ada nama/NIK warga asli dari
contoh terisi; tidak ada referensi tanda tangan/stempel asli di luar logo kop
desa yang memang dipakai. Field ahli waris lama (`namaAhliWaris1/2` dkk.)
hanya tersisa di 1 komentar dokumentasi di `Models.kt` (bukan kode).
Folder `file/` **tidak berubah** (tidak ada di `git status`).

## 5. Yang masih butuh user

1. **Token GitHub akun xxcode2** untuk push commit Fase 3 — agen tidak push
   apa pun. Cara push dari `~/workspace/kasipem-push/` setelah verifikasi:
   `git log` (pastikan HEAD = commit Fase 3) lalu `git push origin main`.
2. **Redeploy Vercel** setelah push (https://kasipem.vercel.app) — biasanya
   auto-deploy dari `main`; cek hasil deploy-nya.
3. **Seri nomor ahli waris (placeholder):** contoh DOCX memakai `141/…/PEM/…`
   (sisi Kades) dan `474.3/…/Pem/…` (sisi Camat), aplikasi memakai placeholder
   `474/PEM`. Ganti placeholder ke `141` atau tetap? (Default tetap kosong —
   hanya soal teks contoh di form.)
4. **Dokumen pendamping ahli waris** (Surat Kuasa Ahli Waris, Ikrar Wakaf,
   Surat Pernyataan Ahli Waris): dijadikan tipe surat terpisah di aplikasi
   atau tidak?
5. **Varian pembuka oleh ahli waris sendiri** (4 salinan belakangan di bundle):
   dijadikan varian atau tetap Kades-signed saja?
6. **Kop SPTJM/PBI** tetap dua logo (aturan user hanya mencakup dua kasus kop)
   — bila diinginkan aturan lain untuk SPTJM/PBI, beri tahu.
7. **Cek cetak sekali di browser** (pratinjau A4 + modal form ahli waris) —
   baru terverifikasi via SSR + render PDF, belum visual di browser sungguhan.
8. **Cek Android di Android Studio** (poin di atas).
9. **Tanda tangan & stempel asli** tetap TIDAK dipakai otomatis.
10. **Auto-isi dari NIK / DATA DESA** tetap TIDAK ada (privasi).
11. Baris alamat kop: web PDF 11pt vs Android 10pt vs web preview 12pt —
    bila ingin disamakan persis, beri tahu angka yang diinginkan.

## Catatan kebersihan & proses

- Tidak ada data contoh asli di file yang diserahkan; tidak ada `1234567890`
  atau `tujuanSekolah` tersisa.
- `file/` (template DOCX/PDF/XLS/XLSM asli) **tidak diubah** sama sekali.
- `node_modules/` dan `.next/` ter-`.gitignore`, bukan bagian commit.
  `web-app/next-env.d.ts` (auto-generate) tidak ikut commit.
- Dua pekerja bekerja paralel dan tidak saling menyentuh direktori satu sama
  lain (web: `web-app/`; Android: `android-app/`). `README.md` sengaja tidak
  diubah di Fase 3 (hanya laporan ini).
- Dua bug tertangkap harness saat pengembangan dan sudah diperbaiki: (1)
  `kopVariant` sempat dihitung dari signer dasar sehingga SPTJM/PBI salah
  kop — kini dari signer akhir; (2) satu cek harness soal penutup adalah
  artefak teks ter-wrap di stream PDF — teksnya ada, cek diperbaiki.
- Satu `exec` pekerja Android tercatat gagal (exitCode 1 — kemungkinan
  percobaan `gradlew`/toolchain yang memang tidak ada; sudah dijelaskan di
  atas dan tidak berdampak pada hasil).
