# FASE 4 — Blok tanda tangan Sekdes selalu "a.n. Kepala Desa" (2026-11-15 ~01:50 WIB)

## Permintaan user (screenshot + pesan)
- Gambar 1 (Kades): `Kasomalang Kulon, 14 November 2026 / Kepala Desa / H. AMIRUDIN, S.Pd.I` — sudah benar.
- Gambar 2 (Sekdes): harus `a.n. Kepala Desa Kasomalang Kulon` lalu di bawahnya `Sekretaris Desa`, baru nama `APIPUDIN, S.IP`.
- Jadi blok tanda tangan Sekdes selalu berformat a.n., bukan "Sekretaris Desa" saja.

## Temuan saat pengecekan kode
- Pisah KK + Kematian SUDAH benar (a.n. sejak Fase 1) — web `resolveSigner` prefix
  `a.n. Kepala Desa Kasomalang Kulon`, Android `Signer.SEKDES_AN`.
- Yang masih salah: **Usaha (SKU) + Duda/Janda** — web `kind: 'sekdes'` tanpa prefix,
  Android `Signer.SEKDES` / `"SEKDES_ONLY"` di PreviewScreen. Inilah yang terlihat
  di screenshot gambar 2.
- Bonus ketidakselarasan: Android menulis jabatan Kades sebagai
  `"Kepala Desa Kasomalang Kulon"` (PDF + preview), padahal web `"Kepala Desa"` saja
  dan screenshot contoh juga `"Kepala Desa"` (nama desa sudah ada di baris tempat/tanggal).
  Diselaraskan ke `"Kepala Desa"`.

## Perubahan
| File | Perubahan |
|---|---|
| `web-app/src/lib/letter-content.ts` | `resolveSigner`: usaha + duda_janda kini prefix `a.n. Kepala Desa ${desa}`, kind `'sekdes-an'` |
| `android-app/.../data/PdfGenerator.kt` | enum `Signer` tinggal `KADES, SEKDES_AN`; usaha + duda_janda pakai `SEKDES_AN`; KADES → `"Kepala Desa"` (tanpa nama desa); cabang `SEKDES` dihapus |
| `android-app/.../ui/screens/PreviewScreen.kt` | usaha + duda_janda pakai `"SEKDES"`; cabang `"SEKDES_ONLY"` dihapus; Kades → `TextPreview("Kepala Desa")` |

Kop TIDAK berubah: usaha/duda_janda tetap Subang-kiri (`kopFor` web: kind
`'sekdes-an'` → `subang-left`; Android `kopFor`: USAHA/DUDA_JANDA → `SUBANG_LEFT`).

## Hasil akhir blok tanda tangan
- Kades: `Kasomalang Kulon, <tanggal> / Kepala Desa / H. AMIRUDIN, S.Pd.I`
- Sekdes (pisah KK, kematian, usaha, duda/janda, atau default penandatangan = Sekdes):
  `Kasomalang Kulon, <tanggal> / a.n. Kepala Desa Kasomalang Kulon / Sekretaris Desa / APIPUDIN, S.IP`

## Verifikasi
- `npm run build` web: **LOLOS** (exit 0, static pages).
- Grep bersih: tidak ada lagi `SEKDES_ONLY`, `Signer.SEKDES`, atau `kind: 'sekdes'`.
- Preview (LetterBody) + PDF (pdf-generator) web membaca `signer.prefix` secara generik,
  jadi prefix baru otomatis tampil di keduanya. Android PDF (`addFooter`) + preview
  (`PreviewSignature`) diubah di kedua jalurnya.
- BELUM terverifikasi: build Android (tidak ada JDK/SDK di mesin ini — cek di Android Studio),
  dan tampilan visual di browser (hanya verifikasi statis + build).

## Catatan / pertanyaan terbuka
- Di screenshot, nama Kades (`H. AMIRUDIN, S.Pd.I`) tampak tebal TANPA garis bawah,
  sedangkan nama Sekdes (`APIPUDIN, S.IP`) tebal DENGAN garis bawah. Aplikasi saat ini
  menggarisbawahi nama penandatangan di preview web (LetterBody) untuk semua jenis.
  Belum diubah karena belum yakin itu aturan sengaja atau artefak screenshot — user
  bisa konfirmasi kalau nama Kades memang harus tanpa underline.
- Pertanyaan Fase 3 yang masih terbuka: placeholder nomor ahli waris 141 vs 474;
  dokumen pendamping ahli waris jadi tipe terpisah?; varian pembuka oleh ahli waris?;
  kop SPTJM/PBI tetap dua logo?
