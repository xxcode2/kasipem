# FASE 5 — Audit penuh template `file/` + perbaikan + Surat Kematian format template
# 2026-11-15 ~02:02–02:40 WIB — koordinator + 4 auditor + 2 pekerja (web & Android)

## 0. Ringkasan

Perintah user (2026-11-15 ~02:02 WIB): *"cek lagi audit lagi yg jelek perbagus,
yg kurang tambahin, yang ga rapih rapihin, contohnya ngacu pada yang ada di folder
file. jadi sebelum kerjakan lu cek dulu semua yg ada di folder file."*

Dikerjakan: (1) audit langsung SEMUA isi `file/` (34 file: 13 DOCX, 16 PDF unik,
3 spreadsheet) + aplikasi (30 tipe, web vs Android); (2) implementasi Fase 5
Kematian persis brief; (3) perbaikan SEMUA temuan audit yang bisa dikerjakan
dalam satu pass; (4) sisa yang tidak bisa dikerjakan dicatat di §6 dengan alasan.

Hasil: **33 tipe** (30 lama + 3 baru), web dan Android diselaraskan ke satu versi
kanonik per tipe. `npm run build` LOLOS (verifikasi independen koordinator).
Harness render 372/372 LOLOS (dijalankan ulang oleh koordinator). Android TIDAK
bisa di-build di mesin ini (tidak ada JDK/SDK) — verifikasi hanya statis.

Spesifikasi implementasi: `~/workspace/kasipem-study/FASE5_SPEC.md`.
Laporan audit mentah: `/tmp/kasipem-audit/audit-docx.md` (319 baris),
`audit-pdf.md` (270 baris), `audit-xls.md` (234 baris), `audit-app.md` (540 baris).

## 1. TABEL AUDIT PER TIPE (Bagus / Salah / Kurang / Perlu diperbaiki)

Acuan template ditulis per baris. "App lama" = HEAD e1da86c sebelum Fase 5.
Status akhir: ✅ diperbaiki di pass ini, ⚠️ sebagian / butuh keputusan user,
❌ sisa pekerjaan (§6).

| # | Tipe | Temuan audit (acuan template) | Perbaikan di Fase 5 |
|---|---|---|---|
| 1 | domisili | SALAH nomor: app 474/PEM, template DOCX 140/25/Pem/2026 + xlsm 140/…/PEM/2022 sepakat 140. SALAH narasi Android (bertempat tinggal di alamat KTP) vs template+web (sudah TIDAK tinggal di alamat KTP). KURANG: urutan field Android Nama-dulu (template NIK dulu); label Android "Bidang Usaha" vs template "Bergerak di Bidang". KONFLIK signer: DOCX a.n. Sekdes vs xlsm Kades. | ✅ Nomor → 140/PEM. ✅ Android narasi + urutan NIK-dulu + label disamakan ke web/template. ⚠️ Signer TIDAK diubah (tetap Kades) — konflik template, butuh keputusan user (§6.1). |
| 2 | pisah_kk | SALAH narasi web ("mengajukan pemisahan") vs template ("telah Pisah KK dari No. [KK]"). SALAH urutan field (Nama-dulu; template NIK, NAMA per orang). KURANG penutup template (app pakai CLOSING generik). SALAH intro Android (tanpa "dengan sesungguhnya"). | ✅ Urutan NIK(1), Nama(1), NIK(2), Nama(2). ✅ Narasi template di kedua sisi. ✅ Penutup CLOSING_PISAH_KK. ✅ Intro Android disamakan. |
| 3 | skt_dayat | KURANG: template punya HARGA TAKSIRAN TANAH (Bangunan/Tanah/Jumlah) + No. Register + Mengetahui Camat — tidak ada di app. SALAH label batas ("Batas ..." vs template "Sebelah ..."). SALAH penutup (template punya klausul tanggung jawab). | ✅ Tambah field harga taksiran ×3 + noRegister + tanggalRegister. ✅ Label "Sebelah ...". ✅ leftSigner Mengetahui Camat. ✅ Penutup CLOSING_SKT. |
| 4 | beda_luas | SALAH signer: app Kades; kedua template sepakat Sekdes (BEDA LUAS REAL: langsung; Beda Nama 3a: a.n.). KURANG field Nomor NIB (template 3a). SALAH penutup (template spesifik). | ✅ Signer → a.n. Sekdes (kop Subang-left; a.n. ikut aturan Fase 4). ✅ Tambah nomorNIB. ✅ Penutup CLOSING_BEDA_LUAS. |
| 5 | beda_nama | SALAH signer (app Kades; template a.n. Sekdes). SALAH struktur: template 2 seksi dokumen lengkap (NIK/Nama/TTL/Alamat per dokumen); app hanya nama + alasan. | ✅ Signer → a.n. Sekdes. ✅ Restruktur 2 seksi dokumen (dokumenPertama default "IJAZAH", dokumenKedua "BUKU NIKAH & KK"; field lama namaSaatIni/nik/alasan dihapus dari render). ✅ Penutup CLOSING_BEDA_NAMA. ❌ Varian 3c (ibu kandung, 6 orang) → §6.2. |
| 6 | belum_menikah | SALAH signer: app Kades; template xlsm Sekdes. BAGUS: isi + field sudah identik web↔Android (satu-satunya yang hampir selaras). | ✅ Signer → a.n. Sekdes (kop Subang-left). ✅ Penutup CLOSING ditambah di Android. |
| 7 | tidak_mampu | SALAH signer: app Kades; 2 DOCX sepakat a.n. Sekdes (xlsm SKTM bilang Kades — kalah 2 lawan 1; nomor 141 xlsm vs 460 dua DOCX — pertahankan 460). KURANG: narasi Sekolah template (SMPN + keringanan biaya) tidak ada; Kolektif template (RW 02, "Program Jabar Caang") tidak ada; judul Kolektif beda web/Android. | ✅ Signer → a.n. Sekdes semua varian. ✅ leftSigner: Sekolah tidak ada, Kolektif "Ketua RW", Umum "Mengetahui, Ketua RW". ✅ Narasi Sekolah + Kolektif ikut template; keperluan default Kolektif "pengajuan bantuan sosial Program Jabar Caang". ✅ Judul Kolektif "(DAFTAR KOLEKTIF)" di kedua sisi. ✅ Penutup per varian. |
| 8 | ahli_waris | SALAH struktur Android (tabel utama tanpa NIK/TTL; lampiran terpisah; field nikPeninggal dkk. tidak ada di form web; kata "tanggal" hilang di pembuka; paragraf "Tidak ada ahli waris selain..." hanya di web). Web sudah benar (audit Fase 3). | ✅ Android disamakan ke web: tabel utama lengkap, lampiran DIHAPUS, paragraf ditambah, "tanggal" diperbaiki, field jumlahAnak + keterangan ditambah. ⚠️ Nomor 474/PEM tetap (pertanyaan 141 vs 474 belum dijawab user). |
| 9 | nikah_tidak_tercatat | SALAH nomor: app 460.2/Ks; template DOCX 474/Ks. SALAH signer: app Kades; template a.n. Sekdes. KURANG field: Bin, Agama, Wali, 2 Saksi, Mas Kawin (template punya; app tidak). | ✅ Nomor → 474/Ks. ✅ Signer → a.n. Sekdes. ✅ Tambah field bin/agama/wali/saksi/masKawin/status; narasi template (wali + saksi + mas kawin). ✅ Penutup CLOSING_NIKAH. |
| 10 | pemakaman | SALAH signer: app Kades; template a.n. Sekdes. KURANG field: Jam meninggal + blok disemayamkan (Hari/Tanggal/Tempat). SALAH intro (template: blok identitas + "Dengan ini menerangkan bahwa :"). | ✅ Signer → a.n. Sekdes. ✅ Tambah 6 field meninggal/disemayamkan. ✅ Intro template. ✅ Penutup CLOSING_PEMAKAMAN. |
| 11 | tidak_memiliki_rumah | SALAH nomor: app 474/PEM; template 141/Pem (2 surat sepakat). SALAH signer: app Kades; template Sekdes langsung. SALAH sudut pandang Android ("saya") vs template/web ("Orang tersebut"). SALAH intro Android (tanpa Kades). | ✅ Nomor → 141/Pem. ✅ Signer → a.n. Sekdes (ikut aturan Fase 4). ✅ Intro "Yang bertanda tangan di bawah ini:". ✅ Narasi orang ketiga di kedua sisi. ✅ Penutup CLOSING_PERMOHONAN_RUMAH. |
| 12 | undangan | SALAH penutup (template: "Demikian kami sampaikan, atas perkenan dan perhatiannya diucapkan terimakasih."). KURANG: tembusan template (Camat; Ketua BPD; Arsip) tidak jadi default; judul seharusnya TIDAK ADA (template tanpa judul). | ✅ Penutup template. ✅ Tembusan default 3 item. ✅ Judul disembunyikan (hideTitle). |
| 13 | kematian | SALAH total vs template (lihat §2 — ini Fase 5 awal). | ✅ Ditulis ulang penuh per brief (§2). |
| 14 | usaha | BAGUS: nomor 510/PEM ✓, field ✓. KURANG penutup template; kalimat pengantar template ("menurut pengakuannya, saat ini mempunyai :") tidak ada. KONFLIK internal template soal a.n. (SKU auto langsung vs SKU manual a.n.) → ikut aturan Fase 4 (a.n.), sudah begitu. | ✅ Penutup CLOSING_USAHA. ✅ Kalimat pengantar ditambah. ✅ Narasi disamakan kedua sisi. |
| 15 | duda_janda | BAGUS: nomor 474.2/KS ✓, signer a.n. ✓. SALAH judul Android (tetap "DUDA/JANDA"; template+web dinamis DUDA/JANDA). KURANG: baris Status hanya di Android (web tidak ada); narasi template (RT/RW + "masih berstatus") tidak ada. | ✅ Judul dinamis di kedua sisi. ✅ Baris Status dihapus dari Android. ✅ Narasi template + field rt/rw opsional. ✅ Penutup CLOSING di Android. |
| 16 | f101 | SALAH orientasi web (portrait; template PDF landscape A4 + xls landscape Legal; Android sudah landscape). SALAH penutup web (CLOSING_FORM; template punya pernyataan sendiri). KURANG intro di Android. | ✅ Web → landscape (Geom). ✅ Penutup CLOSING_F101. ✅ Intro ditambah di Android. ✅ Judul disamakan (" — "), leftSigner ditulis lengkap. ❌ 41 kolom biodata lengkap → §6.4. |
| 17 | f102 | KURANG opsi Jenis Permohonan (app 10; template ~27 sub-opsi dalam 4 kelompok). SALAH signer web (Pemohon saja; template Petugas + Pemohon). KURANG intro + penutup di Android. | ✅ Opsi diperluas ke 27 opsi template. ✅ leftSigner web "Petugas Pendaftaran". ✅ Heading "PERSYARATAN YANG DILAMPIRKAN". ✅ Intro + penutup di Android. |
| 18 | f103 | SALAH opsi Jenis Permohonan (app SKP/SKPLN/SKTT; template 4 opsi). SALAH signer Android (Petugas Pendaftaran; template Mengetahui Kadis Dukcapil). KURANG intro + penutup di Android. | ✅ Opsi → 4 opsi template. ✅ leftSigner "Mengetahui, Kepala Dinas Kependudukan dan Pencatatan Sipil Kabupaten Subang" (kedua sisi); Petugas Pendaftaran DIHAPUS. ✅ Intro + penutup di Android. ❌ Seksi Sponsor/SKPLN → §6.6. |
| 19 | f104 | BAGUS: field + signer cocok. SALAH judul Android ("FORMULIR F-1.04 PERNYATAAN..."; template "SURAT PERNYATAAN..."). KURANG: paragraf pernyataan template tidak dipakai. | ✅ Judul Android → ikut web. ✅ Paragraf pernyataan template sebagai isi utama. ✅ Penutup CLOSING_SPTJM di Android. |
| 20 | f105 | SALAH urutan blok web (anak dulu; template saksi dulu, anak kemudian). SALAH signer web (Suami+Istri saja; template 4 kolom + NIK tiap penandatangan). SALAH penutup (template spesifik). | ✅ Urutan saksi → anak (kedua sisi). ✅ Grid 4 penandatangan + sub NIK (web; Android sudah benar — disamakan). ✅ Penutup CLOSING_F105. ✅ Intro template. ✅ Judul disamakan. |
| 21 | f106 | KURANG kolom tabel rincian KK (template No/Nama/NIK/SHDK/Keterangan; app hanya Nama+Perubahan). KURANG catatan lampiran. SALAH penutup (template spesifik). | ✅ Kolom rincian KK lengkap. ✅ Paragraf catatan lampiran. ✅ Penutup CLOSING_F106. ✅ Judul + intro disamakan. |
| 22 | f115 | SALAH signer (web: Pemohon + Kades; Android: + Camat; template: Camat + Kades/Lurah kiri, Pemohon kanan, + Paraf Petugas). KURANG blok persyaratan + intro/penutup Android. | ✅ Grid signers [Camat, Pemohon, Kades/Lurah, Petugas]. ✅ Blok statis PERSYARATAN PENERBITAN KK. ✅ Intro + penutup di Android. |
| 23 | f201 | SALAH urutan blok web (kondisional di tengah; template kondisional setelah saksi/orang tua). SALAH isi seksi kondisional (kedua sisi saling melengkapi — tidak ada yang lengkap). SALAH signer Android (Petugas Pencatat; template Mengetahui Kades/Lurah + Pejabat Dukcapil). SALAH default jenis Android ("Kelahiran"; template tanpa default). | ✅ Urutan: kondisional TERAKHIR. ✅ Seksi kondisional = gabungan (union) kedua sisi. ✅ Default jenis kosong. ✅ Signers [Mengetahui Kades/Lurah (+Pejabat Dukcapil), Pelapor]. ✅ Intro + penutup di Android. ❌ Detail penuh tiap seksi (27 field Perkawinan dll.) → §6.5. |
| 24 | f203 | SALAH signer web (Yang Menyatakan saja; template Saksi I/II + Yang Menyatakan, +NIK). SALAH penutup (template spesifik). | ✅ Grid 3 penandatangan + sub NIK. ✅ Penutup CLOSING_F203. ✅ Judul + intro disamakan. |
| 25 | f204 | SALAH signer web (Pasangan ikut ttd; template pasangan TIDAK ttd, yang ttd Saksi I/II + Yang Menyatakan). SALAH penutup (template spesifik). | ✅ Pasangan DIHAPUS dari penandatangan web. ✅ Grid 3 penandatangan + sub NIK. ✅ Penutup CLOSING_F204. ✅ Judul + intro disamakan. ✅ Footnote "*) Coret yang tidak perlu." |
| 26 | sptjm_kematian | TIDAK BISA DIBANDINGKAN: template PDF adalah scan (tidak terbaca). App web vs Android beda (judul, intro, "Mengetahui" teks vs kolom ttd, penutup). | ✅ Judul panjang di kedua sisi. ✅ Intro + penutup disamakan. ✅ Grid signers 2 baris (Saksi/Yang Menyatakan + RT/RW/Kades) di kedua sisi. ⚠️ Struktur template belum terverifikasi (butuh OCR) → §6.8. |
| 27 | sptjm_perkawinan_perceraian | SALAH judul (web pendek, Android panjang-tetap; template panjang + dinamis). SALAH urutan web (anak dulu; template saksi dulu). SALAH signer web (Suami+Istri saja; template 4 kolom + NIK). | ✅ Judul dinamis panjang di kedua sisi. ✅ Urutan saksi → anak. ✅ Grid 4 penandatangan + sub NIK. ✅ Penutup CLOSING_SPTJM_PERKAWINAN. |
| 28 | pbi_asesmen | SALAH penomoran web (nomor bergeser bila jawaban kosong; template nomor asli 1–11). KURANG signer (web Pemohon+Fasilitator saja; template rantai 7 penandatangan). KURANG "TAHUN <tahun>" di judul. | ✅ Nomor asli 1–11 dipertahankan. ✅ Grid 7 penandatangan. ✅ Baris "TAHUN <tahun>". ✅ Intro + penutup di Android. ✅ Spasi judul disamakan. |
| 29 | pbi_pernyataan | KURANG 5 field identitas (template: No KK, JK, WN, Status Kawin, Agama). SALAH signer Android (+Mengetahui Kades; template: Yang Bersangkutan + saksi RW/RT + Camat + a.n. Kades). SALAH isi (app textarea bebas; template 2 butir tetap). | ✅ Tambah 5 field + urutan template. ✅ 2 butir tetap sebagai isi (textarea lama tetap dirender bila terisi). ✅ Penutup CLOSING_PBI_PERNYATAAN. ✅ Grid 5 penandatangan. ✅ Judul "(PBI-JKN)" di kedua sisi. |
| 30 | pbi_tidak_punya_rekening | TIDAK ADA TEMPLATE (rekening BANK) — yang ada di template adalah rekening LISTRIK (tipe baru §4). SALAH signer Android (+Mengetahui Kades). | ✅ Mengetahui Kades DIHAPUS dari Android. ✅ Penutup CLOSING_SPTJM di Android. ✅ Intro disamakan. Tipe dipertahankan apa adanya (dicatat tidak punya padanan template). |
| 31 | sktm_pbi (BARU) | KURANG: 4 varian SKTM PBI (RSUD Subang/IPB/PBI KIS/RSUD Ciereng) tidak ada di app. | ✅ Tipe baru (474.4/KS, tujuan select + blok anak opsional + rantai ttd template). |
| 32 | pbi_rekening_listrik (BARU) | KURANG: Surat Pernyataan Tidak Memiliki Rekening Listrik (xls sheet 3(4)) tidak ada di app. | ✅ Tipe baru (474.4/KS, field nama tetangga + nomor pelanggan). |
| 33 | pernyataan_akta_kelahiran (BARU) | KURANG: surat ke-2 di DOCX Pernyataan (Akta Kelahiran) tidak ada di app. | ✅ Tipe baru (141/Pem, field Nama Ayah/Ibu, signer a.n. Sekdes). |

Hitungan audit: dari 30 tipe lama — 0 yang sepenuhnya benar sebelum perbaikan;
setelah pass ini 30/30 diselaraskan web↔Android ke kanonik template (kecuali
butir-butir bertanda ⚠️/❌ di atas). 4 file PDF scan + kolom 1–23 F.1.01 tidak
bisa diaudit dari teks (butuh OCR/visual).

## 2. SURAT KEMATIAN — FORMAT TEMPLATE (Fase 5 awal)

- Tanpa kop/logo. Tabel header 3 baris di kiri atas (dengan garis vertikal di kiri):
  `Pemerintah/Kota/Kab : Subang / Kecamatan : Kasomalang / Desa/Kelurahan : Kasomalang Kulon`
  (nilai dari data header).
- Kertas landscape A4; isi di kolom kiri ~62% lebar (web: contentW ≈164 mm,
  berakhir di 62% lebar halaman; Android PDF: rightMargin 320f, isi berakhir di
  62%; preview Android fillMaxWidth(0.62f)). Judul/nomor/ttd rata tengah DALAM
  area isi.
- Judul `SURAT KETERANGAN KEMATIAN` bold + underline. Nomor
  `No.: 460.3 / <reg|............> / KS / <tahun>` bold + underline (reg kosong →
  12 titik; tahun dari tanggal surat).
- Intro: `Yang bertanda tangan di bawah ini menerangkan` (tanpa Kepala Desa).
- Field almarhum: Nama, NIK, Jenis Kelamin, Agama, Tempat/Tanggal Lahir,
  Pekerjaan, Alamat. Seksi `Telah meninggal dunia pada :` → Hari, Tanggal,
  Bertempat di, Penyebab Kematian. Seksi `Surat Keterangan ini dibuat
  berdasarkan keterangan Pelapor :` → NIK, Nama, Tempat/Tanggal Lahir,
  Pekerjaan, Alamat. Baris `Hubungan pelapor dengan yang meninggal adalah : <hubungan>`.
- TANPA paragraf penutup. Tanda tangan (tanpa a.n.):
  `Kasomalang Kulon, DD-MM-YYYY / Sekretaris Desa Kasomalang Kulon / APIPUDIN, S.IP`.
- Semua tanggal format DD-MM-YYYY (khusus surat ini).
- KONFLIK (dicatat eksplisit): file `SURAT KEMATIAN.doc` menulis TTD
  `a.n Kepala Desa Kasomalang Kulon / Sekretaris Desa`, SEMENTARA screenshot user
  + sheet `SURAT KEMATIAN` di xlsm menulis `Sekretaris Desa Kasomalang Kulon`
  tanpa a.n. Keputusan: ikut screenshot/xlsm (tanpa a.n.) sesuai brief Fase 5.
  User bisa membalikkan kalau ternyata mau a.n. juga di sini.
- KONFLIK ke-2: `.doc` pelapor tidak punya NIK (hanya Nama, Tanggal Lahir/Umur,
  Pekerjaan, Alamat); xlsm + form aplikasi punya NIK pelapor. Keputusan: NIK
  pelapor tetap ada (ikut xlsm/form).

## 3. PERUBAHAN INFRASTRUKTUR

- WEB `pdf-generator.ts`: tipe `Geom` — portrait = konstanta lama persis;
  landscape 297×210 (contentW ≈164, pageBottom 195). Semua helper memakai geom.
  jsPDF landscape untuk kematian + f101. Kop `tabel`, `hideTitle`, `subtitle`,
  `nomorDisplay` (bold+underline), penutup dilewati bila kosong, grid
  penandatangan. `LetterPreview.tsx`: `@page { size: A4 landscape }` kondisional
  saat preview kematian/f101.
- WEB `letter-content.ts`: 19 konstanta penutup template (§4); `KopVariant
  'tabel'`; signer kind `'sekdes-kematian'`; `LetterContent.signers` /
  `signerColumns` / `landscape` / `hideTitle` / `subtitle` / `nomorDisplay`;
  `SignerBlock.sub` (baris NIK di bawah nama saksi).
- WEB `letter-definitions.ts`: nomor diperbaiki (domisili 140, nikah 474,
  tidak_memiliki_rumah 141); puluhan field baru; f102 27 opsi template; f103
  4 opsi template; 3 tipe baru → total 33 tipe.
- ANDROID `Models.kt`: `KopStyle.TABEL_KEMATIAN`; 33 tipe; konstanta penutup
  pindah ke Models.kt sebagai top-level `val` (satu sumber PDF+preview);
  helper `toDdMmYyyy`, `tahunDariTanggal`, `kalimatTujuanSktmPbi`,
  `F102_JENIS_PERMOHONAN` (27 opsi), `SKTM_PBI_TUJUAN`; `paragrafPembukaAhliWaris`
  diperbaiki; judul F-form diselaraskan; `effectivePdfTitle` dinamis.
- ANDROID `PdfGenerator.kt`: semua 33 generator ditulis ulang per spec
  (penutup, intro "dengan sesungguhnya", narasi/field/label/judul kanonik);
  `Signer.SEKDES_KEMATIAN`; kematian landscape + `addHeaderTabelKematian`;
  3 generator baru; `newDocument(..., rightMargin)`; undangan tanpa judul.
- ANDROID `PreviewScreen.kt` + `Forms.kt` + `HomeScreen.kt`: 33 preview
  diselaraskan ke PDF; cabang kop `TABEL_KEMATIAN`; preview Kematian 62%;
  3 form baru; "30 kartu" → "33 kartu".
- Perbaikan koordinator (di luar laporan pekerja): kalimat tujuan SKTM PBI
  tidak lagi dirender bila tujuan kosong (sebelumnya "untuk keperluan ." di
  web + Android PDF + preview).

## 4. KONSTANTA PENUTUP TEMPLATE (dipakai kedua sisi, string persis)

CLOSING_PISAH_KK, CLOSING_SKT, CLOSING_BEDA_LUAS, CLOSING_BEDA_NAMA,
CLOSING_MISKIN_SEKOLAH, CLOSING_MISKIN_KOLEKTIF, CLOSING_NIKAH,
CLOSING_PEMAKAMAN, CLOSING_PERMOHONAN_RUMAH, CLOSING_USAHA, CLOSING_F101,
CLOSING_F105, CLOSING_F106, CLOSING_F203, CLOSING_F204,
CLOSING_SPTJM_PERKAWINAN, CLOSING_PBI_PERNYATAAN, CLOSING_SKTM_PBI,
CLOSING_UNDANGAN — teks lengkap di `~/workspace/kasipem-study/FASE5_SPEC.md §3`.
Penutup baku lama (CLOSING, CLOSING_FORM, CLOSING_SPTJM, CLOSING_AHLI_WARIS)
tetap dipakai untuk tipe yang template-nya tidak punya penutup khusus.

## 5. VERIFIKASI

- `npm run build` (web) — LOLOS, dijalankan 2× oleh koordinator secara
  independen (terakhir setelah perbaikan kalimat tujuan): "Compiled
  successfully", 4/4 static pages, `/` 153 kB.
- Harness `/tmp/kasipem-fase5-harness/` (tsc proyek + run.js, data fiktif) —
  **372 assert lolos, 0 gagal**, dijalankan ulang oleh koordinator setelah
  perbaikan kalimat tujuan. Mencakup: 33 tipe (kopVariant, signer, closing
  persis, judul, intro, orientasi — hanya kematian+f101 landscape) + varian +
  PDF 6 sampel (MediaBox landscape 841.89×595.28 kematian/f101, portrait
  lainnya; string kunci ada; PDF kematian TIDAK mengandung kalimat penutup
  generik, "a.n. Kepala Desa", atau kop Disdukcapil).
- Android — verifikasi statis oleh pekerja + koordinator: semua `when` atas
  KopStyle/LetterType exhaustive (generatePdf 33/33, nomorOf/tanggalOf 33/33,
  LetterForm 33/33, iconFor 33/33, getEmptyLetterData 33/33, kopFor 29 eksplisit
  + else, addHeaderFor + kop-when Preview semua 5 nilai KopStyle); tidak ada
  lagi string lama ("Petugas Pencatat", "FORMULIR F-1.04", "Lampiran — Data
  Ahli Waris", "Kab. Subang"); konsistensi PDF↔Preview per tipe (pemakaian
  CLOSING_* sama di kedua file); 3 tipe baru terdaftar di UI + punya data class
  + generator + preview + form; keseimbangan kurung 5 file OK.
- `git status`: 12 file diubah (7 web + 5 Android), +3313/−875. Folder `file/`
  TIDAK berubah (0 file). Tidak ada data warga asli di diff (grep bersih —
  satu placeholder lama "Contoh: Kp. Karihkil RT 006/001" di form Nikah sudah
  ada sebelum Fase 5, bukan dari pass ini).
- Geometri Kematian Android: isi berakhir di 62% lebar halaman landscape
  (lebar isi sendiri 55% — sama seperti web: contentW 164 mm = 55% dari 297 mm,
  berakhir di 184 mm = 62%). Konsisten web↔Android.

### BELUM TERVERIFIKASI (jujur)
- Build Android (tidak ada JDK/SDK di mesin ini — wajib `./gradlew
  assembleDebug` di Android Studio; risiko typo nama field hanya tertutup
  di sana).
- Cetak visual di browser + tampilan preview di layar (subagent tanpa browser
  live; @page landscape kondisional + layout 62% hanya terverifikasi lewat
  kode + PDF).
- Vercel redeploy (belum di-push — commit lokal saja, push butuh konfirmasi
  token user).

## 6. SISA PEKERJAAN (tidak dikerjakan pass ini — dengan alasan)

1. **Signer Domisili** — konflik template (DOCX a.n. Sekdes vs xlsm Kades).
   Tetap Kades. Butuh keputusan user.
2. **Beda Nama varian 3c** (beda nama ibu kandung antar-KK, 6 orang, tanpa
   intro) — butuh varian/tabel multi-orang baru; butuh keputusan user.
3. **Dokumen pendamping Ahli Waris** (Surat Kuasa, Ikrar Wakaf, Surat
   Pernyataan Ahli Waris, Keterangan Pernyataan Kematian) — butuh keputusan
   user (ditanya sejak Fase 3, belum dijawab).
4. **F-1.01: 41 kolom biodata lengkap** — butuh redesign form khusus
   (3 tabel berlapis); subset 10 kolom saat ini dipertahankan.
5. **F-2.01: detail penuh tiap seksi** (Perkawinan 27 field, Pengangkatan Anak
   21 field, dst.) — butuh form khusus per jenis pelaporan.
6. **F-1.03: seksi Sponsor (WNA/SKTT) + SKPLN (17–20)** — kasus tepi, butuh
   form kondisional.
7. **Anomali template F-1.15** (satu baris anggota tertukar NIK↔Nama di contoh
   terisi) — kesalahan pengisian contoh, bukan aplikasi; tidak ada yang
   diperbaiki.
8. **Template scan** (F.2.01 KET LSJIR, FORMULIR KEMATIAN BARU, SPTJM KEMATIAN)
   + kolom 1–23 F.1.01 PDF — butuh OCR/baca visual; struktur SPTJM Kematian
   belum terverifikasi dari template (dipakai pola F-2.03/2.04).
9. **Placeholder nomor ahli waris 141 vs 474** (Fase 3) — belum dijawab user.
10. **Varian pembuka ahli waris oleh ahli waris sendiri** (Fase 3) — belum dijawab.
11. **Kop SPTJM/PBI tetap dua logo?** (Fase 3) — belum dijawab.
12. **Nama Kades tanpa underline vs Sekdes dengan underline** (Fase 4) — belum dijawab.
13. **DATA DESA ganda (xlsm + PBI) + macro arsip mati + VLOOKUP rapuh** —
    masalah file Excel lama, BUKAN aplikasi (aplikasi tidak memakai DATA DESA).
14. **Template F-2.03 tercetak dengan NIK terisi** di bawah blok tanda tangan —
    peringatan kebersihan template (harus dikosongkan sebelum dipakai ulang),
    bukan aplikasi.
15. **Kematian tanpa a.n. vs `.doc` yang menulis a.n.** — keputusan ikut
    screenshot (lihat §2); user bisa membalikkan.

## 7. KEPUTUSAN YANG PERLU DIKETAHUI USER

- Aturan a.n. Fase 4 DIPERTAHANKAN untuk semua penandatangan Sekdes, meski
  sebagian template menulis Sekdes langsung (mayoritas DOCX 9/13 memakai a.n.
  + instruksi user eksplisit). Pengecualian hanya Kematian (ikut screenshot).
- Intro "dengan sesungguhnya" DISTANDARDISASI untuk semua surat desa meski
  template bervariasi (hanya Domisili yang memakainya di template).
- Ejaan dibakukan ke ejaan benar (bukan typo template).
- Tipe `pbi_tidak_punya_rekening` (rekening BANK) tidak punya padanan template
  dan dipertahankan apa adanya; padanan template-nya (rekening LISTRIK) dibuat
  sebagai tipe baru terpisah.
- `tempatPernikahan` (nikah) tetap tersimpan di form/data tapi tidak lagi
  dirender (narasi template memakai wali/saksi/mas kawin).
- Nama penandatangan grid (Fasilitator/RW/RT/Camat) memakai field form baru
  bila diisi, bila kosong garis titik-titik.
