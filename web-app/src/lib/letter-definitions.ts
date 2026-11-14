import { LetterCategory, LetterType } from '../types';

/**
 * SATU sumber kebenaran untuk definisi 30 jenis surat/formulir:
 * judul, kategori, pola nomor, dan daftar field form.
 * Dipakai oleh store, form, selector, preview, dan PDF generator.
 *
 * Fase 2: 18 tipe baru (surat desa + formulir Dukcapil F + SPTJM + PBI-JKN)
 * + varian domisili (Perorangan/Lembaga/Perusahaan) dan SKTM
 * (Umum/Sekolah/Daftar Kolektif) sebagai opsi di dalam surat yang sudah ada.
 */

export interface FormField {
  name: string;
  label: string;
  type: 'text' | 'textarea' | 'date' | 'number' | 'select' | 'table';
  placeholder?: string;
  required?: boolean;
  rows?: number;
  options?: { value: string; label: string }[];
  /** Nilai awal untuk field select (mis. varian default). */
  defaultValue?: string;
  /** Field hanya tampil bila field lain bernilai salah satu dari values. */
  visibleWhen?: { field: string; values: string[] };
  /** Untuk type 'table': kolom-kolom tabel. */
  columns?: { name: string; label: string }[];
  /** Untuk type 'table': jumlah baris awal / maksimal. */
  minRows?: number;
  maxRows?: number;
}

export interface LetterDefinition {
  id: LetterType;
  title: string;
  shortTitle: string;
  description: string;
  category: LetterCategory;
  /** Awalan nomor surat, mis. "474" (bagian tengah/akhir mengikuti pola di bawah). */
  nomorPrefix: string;
  /** Sufiks klasifikasi, mis. "PEM" atau "Ks". Kosong = nomor manual. */
  nomorSuffix: string;
  /** Kop yang dipakai: 'desa' (logo Garuda + Subang) atau 'disdukcapil'
   *  (kop teks Dinas Dukcapil Subang, tanpa logo desa) — formulir seri F. */
  kop?: 'desa' | 'disdukcapil';
  /** Sembunyikan baris "Nomor :" (formulir yang memang tidak bernomor). */
  hideNomor?: boolean;
  /** Field yang dipakai sebagai "nama pemohon" di Riwayat Surat. */
  applicantField?: string;
  fields: FormField[];
}

function todayIso(): string {
  return new Date().toISOString().slice(0, 10);
}

export function defaultNomorSurat(def: LetterDefinition): string {
  if (!def.nomorPrefix) return '';
  const year = new Date().getFullYear();
  return `${def.nomorPrefix} / 001 / ${def.nomorSuffix} / ${year}`;
}

const JENIS_KELAMIN: FormField = {
  name: 'jenisKelamin',
  label: 'Jenis Kelamin',
  type: 'select',
  options: [
    { value: '', label: 'Pilih...' },
    { value: 'Laki-laki', label: 'Laki-laki' },
    { value: 'Perempuan', label: 'Perempuan' },
  ],
};

/** Apakah field ini tampil untuk data saat ini (aturan visibleWhen). */
export function isFieldVisible(field: FormField, data: Record<string, any>): boolean {
  if (!field.visibleWhen) return true;
  const v = (data[field.visibleWhen.field] || '').toString();
  return field.visibleWhen.values.includes(v);
}

export function visibleFields(def: LetterDefinition, data: Record<string, any>): FormField[] {
  return def.fields.filter((f) => isFieldVisible(f, data));
}

const PERORANGAN = { field: 'jenisDomisili', values: ['Perorangan'] };
const SKTM_PRIBADI = { field: 'varianSKTM', values: ['Umum', 'Sekolah'] };

export const LETTER_DEFINITIONS: LetterDefinition[] = [
  {
    id: 'domisili',
    title: 'SURAT KETERANGAN DOMISILI',
    shortTitle: 'Keterangan Domisili',
    description: 'Menerangkan tempat tinggal / domisili penduduk desa, lembaga, atau perusahaan.',
    category: 'Individu',
    nomorPrefix: '474',
    nomorSuffix: 'PEM',
    applicantField: 'namaLengkap',
    fields: [
      {
        name: 'jenisDomisili',
        label: 'Jenis Domisili',
        type: 'select',
        defaultValue: 'Perorangan',
        options: [
          { value: 'Perorangan', label: 'Perorangan' },
          { value: 'Lembaga', label: 'Lembaga' },
          { value: 'Perusahaan', label: 'Perusahaan' },
        ],
      },
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true, visibleWhen: PERORANGAN },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka', visibleWhen: PERORANGAN },
      { ...JENIS_KELAMIN, visibleWhen: PERORANGAN },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text', visibleWhen: PERORANGAN },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date', visibleWhen: PERORANGAN },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text', visibleWhen: PERORANGAN },
      { name: 'alamatAsal', label: 'Alamat Asal (sesuai KTP)', type: 'textarea', required: true, rows: 2, visibleWhen: PERORANGAN },
      { name: 'rt', label: 'RT', type: 'text', required: true, placeholder: 'Contoh: 001', visibleWhen: PERORANGAN },
      { name: 'rw', label: 'RW', type: 'text', required: true, placeholder: 'Contoh: 006', visibleWhen: PERORANGAN },
      { name: 'alamatDomisili', label: 'Alamat Domisili Saat Ini', type: 'textarea', required: true, rows: 2, visibleWhen: PERORANGAN },
      { name: 'keperluanSurat', label: 'Keperluan Surat', type: 'text', required: true, visibleWhen: PERORANGAN },
      // --- Varian Lembaga ---
      { name: 'namaLembaga', label: 'Nama Lembaga', type: 'text', required: true, visibleWhen: { field: 'jenisDomisili', values: ['Lembaga'] } },
      { name: 'namaPimpinan', label: 'Nama Pimpinan', type: 'text', required: true, visibleWhen: { field: 'jenisDomisili', values: ['Lembaga', 'Perusahaan'] } },
      { name: 'alamatLembaga', label: 'Alamat Lembaga', type: 'textarea', required: true, rows: 2, visibleWhen: { field: 'jenisDomisili', values: ['Lembaga'] } },
      // --- Varian Perusahaan ---
      { name: 'namaPerusahaan', label: 'Nama Perusahaan', type: 'text', required: true, visibleWhen: { field: 'jenisDomisili', values: ['Perusahaan'] } },
      { name: 'namaSekretaris', label: 'Nama Sekretaris', type: 'text', visibleWhen: { field: 'jenisDomisili', values: ['Perusahaan'] } },
      { name: 'bidangUsaha', label: 'Bergerak di Bidang', type: 'text', required: true, placeholder: 'Contoh: Perdagangan', visibleWhen: { field: 'jenisDomisili', values: ['Perusahaan'] } },
      { name: 'alamatPerusahaan', label: 'Alamat Perusahaan', type: 'textarea', required: true, rows: 2, visibleWhen: { field: 'jenisDomisili', values: ['Perusahaan'] } },
      { name: 'keperluan', label: 'Keperluan Surat', type: 'text', required: true, visibleWhen: { field: 'jenisDomisili', values: ['Lembaga', 'Perusahaan'] } },
    ],
  },
  {
    id: 'pisah_kk',
    title: 'SURAT KETERANGAN PISAH KK',
    shortTitle: 'Pisah KK',
    description: 'Keterangan pemisahan Kartu Keluarga.',
    category: 'Individu',
    nomorPrefix: '474',
    nomorSuffix: 'PEM',
    applicantField: 'nama1',
    fields: [
      { name: 'nama1', label: 'Nama Pemisah Pertama', type: 'text', required: true },
      { name: 'nik1', label: 'NIK Pemisah Pertama', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'nama2', label: 'Nama Pemisah Kedua', type: 'text', required: true },
      { name: 'nik2', label: 'NIK Pemisah Kedua', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'nomorKKLama', label: 'Nomor KK Lama', type: 'text', required: true },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
    ],
  },
  {
    id: 'skt_dayat',
    title: 'SURAT KETERANGAN TANAH',
    shortTitle: 'Keterangan Tanah (DAYAT)',
    description: 'Keterangan kepemilikan / penguasaan tanah (SKT Dayat).',
    category: 'Properti',
    nomorPrefix: '593',
    nomorSuffix: 'PEM',
    applicantField: 'namaLengkap',
    fields: [
      { name: 'namaLengkap', label: 'Nama Pemilik Tanah', type: 'text', required: true },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      { name: 'luasTanah', label: 'Luas Tanah (m²)', type: 'text', required: true },
      { name: 'luasBangunan', label: 'Luas Bangunan (m²)', type: 'text' },
      { name: 'spptNop', label: 'SPPT / NOP', type: 'text' },
      { name: 'batasUtara', label: 'Batas Utara', type: 'text' },
      { name: 'batasTimur', label: 'Batas Timur', type: 'text' },
      { name: 'batasSelatan', label: 'Batas Selatan', type: 'text' },
      { name: 'batasBarat', label: 'Batas Barat', type: 'text' },
    ],
  },
  {
    id: 'beda_luas',
    title: 'SURAT KETERANGAN BEDA LUAS TANAH',
    shortTitle: 'Beda Luas Tanah',
    description: 'Menerangkan perbedaan luas antara Sertifikat dan SPPT.',
    category: 'Properti',
    nomorPrefix: '141',
    nomorSuffix: 'PEM',
    applicantField: 'namaLengkap',
    fields: [
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      { name: 'rt', label: 'RT', type: 'text', placeholder: 'Contoh: 002' },
      { name: 'rw', label: 'RW', type: 'text', placeholder: 'Contoh: 02' },
      { name: 'nomorSertifikat', label: 'Nomor Sertifikat', type: 'text', required: true },
      { name: 'luasSertifikat', label: 'Luas Sertifikat (m²)', type: 'text', required: true },
      { name: 'nomorSPPT', label: 'Nomor SPPT', type: 'text', required: true },
      { name: 'luasSPPT', label: 'Luas SPPT (m²)', type: 'text', required: true },
    ],
  },
  {
    id: 'beda_nama',
    title: 'SURAT KETERANGAN BEDA NAMA',
    shortTitle: 'Beda Nama',
    description: 'Menerangkan perbedaan nama pada dua dokumen.',
    category: 'Administrasi',
    nomorPrefix: '474',
    nomorSuffix: 'PEM',
    applicantField: 'namaLengkap',
    fields: [
      { name: 'namaLengkap', label: 'Nama Saat Ini', type: 'text', required: true },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'namaDokumen1', label: 'Nama di Dokumen Pertama', type: 'text', required: true },
      { name: 'namaDokumen2', label: 'Nama di Dokumen Kedua', type: 'text', required: true },
      { name: 'alasan', label: 'Alasan Perbedaan Nama', type: 'textarea', required: true, rows: 2 },
    ],
  },
  {
    id: 'belum_menikah',
    title: 'SURAT KETERANGAN BELUM MENIKAH',
    shortTitle: 'Belum Menikah',
    description: 'Menerangkan bahwa yang bersangkutan belum pernah menikah.',
    category: 'Administrasi',
    nomorPrefix: '460.2',
    nomorSuffix: 'Ks',
    applicantField: 'namaLengkap',
    fields: [
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka' },
      JENIS_KELAMIN,
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      { name: 'rt', label: 'RT', type: 'text', placeholder: 'Contoh: 001' },
      { name: 'rw', label: 'RW', type: 'text', placeholder: 'Contoh: 006' },
      { name: 'keperluan', label: 'Keperluan', type: 'text', required: true },
    ],
  },
  {
    id: 'tidak_mampu',
    title: 'SURAT KETERANGAN TIDAK MAMPU',
    shortTitle: 'Tidak Mampu (SKTM)',
    description: 'Menerangkan bahwa yang bersangkutan tergolong tidak mampu. Ada varian Sekolah dan Daftar Kolektif.',
    category: 'Sosial',
    nomorPrefix: '460',
    nomorSuffix: 'Ks',
    applicantField: 'namaLengkap',
    fields: [
      {
        name: 'varianSKTM',
        label: 'Varian SKTM',
        type: 'select',
        defaultValue: 'Umum',
        options: [
          { value: 'Umum', label: 'Umum (perorangan)' },
          { value: 'Sekolah', label: 'Sekolah' },
          { value: 'Kolektif', label: 'Daftar Kolektif (banyak warga)' },
        ],
      },
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true, visibleWhen: SKTM_PRIBADI },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka', visibleWhen: SKTM_PRIBADI },
      { ...JENIS_KELAMIN, visibleWhen: SKTM_PRIBADI },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text', visibleWhen: SKTM_PRIBADI },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date', visibleWhen: SKTM_PRIBADI },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text', visibleWhen: SKTM_PRIBADI },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2, visibleWhen: SKTM_PRIBADI },
      { name: 'rt', label: 'RT', type: 'text', placeholder: 'Contoh: 006', visibleWhen: SKTM_PRIBADI },
      { name: 'rw', label: 'RW', type: 'text', placeholder: 'Contoh: 002', visibleWhen: SKTM_PRIBADI },
      { name: 'penghasilanBulanan', label: 'Penghasilan Bulanan (Rp)', type: 'text', visibleWhen: SKTM_PRIBADI },
      { name: 'keperluan', label: 'Keperluan', type: 'text', required: true, visibleWhen: SKTM_PRIBADI },
      { name: 'tujuan', label: 'Tujuan (instansi / program)', type: 'text', required: true, visibleWhen: SKTM_PRIBADI },
      // --- Varian Daftar Kolektif: satu surat untuk banyak warga ---
      {
        name: 'daftarKolektif',
        label: 'Daftar Warga (nama, NIK, alamat)',
        type: 'table',
        required: true,
        visibleWhen: { field: 'varianSKTM', values: ['Kolektif'] },
        minRows: 1,
        maxRows: 20,
        columns: [
          { name: 'nama', label: 'Nama' },
          { name: 'nik', label: 'NIK' },
          { name: 'alamat', label: 'Alamat' },
        ],
      },
    ],
  },
  {
    id: 'ahli_waris',
    title: 'SURAT KETERANGAN AHLI WARIS',
    shortTitle: 'Ahli Waris',
    description: 'Menerangkan ahli waris dari almarhum/almarhumah.',
    category: 'Hukum',
    nomorPrefix: '474',
    nomorSuffix: 'PEM',
    applicantField: 'namaAhliWaris1',
    fields: [
      { name: 'namaPeninggal', label: 'Nama Peninggal (Almarhum/Almarhumah)', type: 'text', required: true },
      { name: 'nikPeninggal', label: 'NIK Peninggal', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'alamatPeninggal', label: 'Alamat Peninggal', type: 'textarea', required: true, rows: 2 },
      { name: 'namaAhliWaris1', label: 'Nama Ahli Waris 1', type: 'text', required: true },
      { name: 'nikAhliWaris1', label: 'NIK Ahli Waris 1', type: 'text', placeholder: '16 digit angka' },
      { name: 'hubunganAhliWaris1', label: 'Hubungan Ahli Waris 1 dengan Peninggal', type: 'text' },
      { name: 'namaAhliWaris2', label: 'Nama Ahli Waris 2', type: 'text' },
      { name: 'nikAhliWaris2', label: 'NIK Ahli Waris 2', type: 'text', placeholder: '16 digit angka' },
      { name: 'hubunganAhliWaris2', label: 'Hubungan Ahli Waris 2 dengan Peninggal', type: 'text' },
      { name: 'keterangan', label: 'Keterangan Tambahan', type: 'textarea', rows: 2 },
    ],
  },
  {
    id: 'nikah_tidak_tercatat',
    title: 'SURAT KETERANGAN NIKAH TIDAK TERCATAT',
    shortTitle: 'Nikah Tidak Tercatat',
    description: 'Menerangkan perkawinan yang belum tercatat resmi.',
    category: 'Administrasi',
    nomorPrefix: '460.2',
    nomorSuffix: 'Ks',
    applicantField: 'namaPria',
    fields: [
      { name: 'namaPria', label: 'Nama Pria', type: 'text', required: true },
      { name: 'nikPria', label: 'NIK Pria', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tempatLahirPria', label: 'Tempat Lahir Pria', type: 'text' },
      { name: 'tanggalLahirPria', label: 'Tanggal Lahir Pria', type: 'date' },
      { name: 'namaWanita', label: 'Nama Wanita', type: 'text', required: true },
      { name: 'nikWanita', label: 'NIK Wanita', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tempatLahirWanita', label: 'Tempat Lahir Wanita', type: 'text' },
      { name: 'tanggalLahirWanita', label: 'Tanggal Lahir Wanita', type: 'date' },
      { name: 'tanggalPernikahan', label: 'Tanggal Pernikahan', type: 'date', required: true },
      { name: 'tempatPernikahan', label: 'Tempat Pernikahan', type: 'text', required: true },
    ],
  },
  {
    id: 'pemakaman',
    title: 'SURAT KETERANGAN PEMAKAMAN',
    shortTitle: 'Pemakaman',
    description: 'Keterangan pemakaman almarhum/almarhumah.',
    category: 'Lainnya',
    nomorPrefix: '474',
    nomorSuffix: 'PEM',
    applicantField: 'namaKeluarga',
    fields: [
      { name: 'namaAlmarhum', label: 'Nama Almarhum/Almarhumah', type: 'text', required: true },
      { name: 'nikAlmarhum', label: 'NIK Almarhum/Almarhumah', type: 'text', placeholder: '16 digit angka' },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      { name: 'tanggalMeninggal', label: 'Tanggal Meninggal', type: 'date', required: true },
      { name: 'lokasiMakam', label: 'Lokasi Makam', type: 'text', required: true },
      { name: 'namaKeluarga', label: 'Nama Keluarga yang Memohon', type: 'text', required: true },
    ],
  },
  {
    id: 'tidak_memiliki_rumah',
    title: 'SURAT PERNYATAAN TIDAK MEMILIKI RUMAH',
    shortTitle: 'Tidak Memiliki Rumah',
    description: 'Pernyataan bahwa yang bersangkutan tidak memiliki rumah.',
    category: 'Sosial',
    nomorPrefix: '474',
    nomorSuffix: 'PEM',
    applicantField: 'namaLengkap',
    fields: [
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
      { name: 'alamatTinggal', label: 'Alamat Tempat Tinggal Saat Ini', type: 'textarea', required: true, rows: 2 },
      { name: 'keterangan', label: 'Keterangan Tambahan', type: 'textarea', rows: 2 },
    ],
  },
  {
    id: 'undangan',
    title: 'SURAT UNDANGAN',
    shortTitle: 'Undangan Resmi',
    description: 'Undangan resmi acara desa/kelurahan.',
    category: 'Administrasi',
    nomorPrefix: '',
    nomorSuffix: '',
    applicantField: 'tujuanYth',
    fields: [
      { name: 'tujuanYth', label: 'Tujuan Yth. (Nama & Jabatan)', type: 'textarea', required: true, rows: 2 },
      { name: 'hari', label: 'Hari', type: 'text', required: true, placeholder: 'Contoh: Senin' },
      { name: 'tanggal', label: 'Tanggal', type: 'text', required: true, placeholder: 'Contoh: 16' },
      { name: 'bulan', label: 'Bulan', type: 'text', required: true, placeholder: 'Contoh: November' },
      { name: 'tahun', label: 'Tahun', type: 'text', required: true, placeholder: 'Contoh: 2026' },
      { name: 'pukul', label: 'Pukul', type: 'text', required: true, placeholder: 'Contoh: 09.00' },
      { name: 'tempat', label: 'Tempat', type: 'textarea', required: true, rows: 2 },
      { name: 'agendaAcara', label: 'Agenda Acara', type: 'textarea', required: true, rows: 3 },
      { name: 'daftarTembusan', label: 'Tembusan (pisahkan dengan koma, opsional)', type: 'textarea', rows: 2 },
    ],
  },
  // ================= FASE 2 — SURAT DESA BARU =================
  {
    id: 'kematian',
    title: 'SURAT KETERANGAN KEMATIAN',
    shortTitle: 'Keterangan Kematian',
    description: 'Keterangan resmi bahwa seseorang telah meninggal dunia (dasar pengurusan akta kematian).',
    category: 'Surat Desa',
    // CATATAN: seri 460.3/.../KS/ juga dipakai pemakaman di template desa — dipertahankan apa adanya.
    nomorPrefix: '460.3',
    nomorSuffix: 'KS',
    applicantField: 'namaPelapor',
    fields: [
      { name: 'namaAlmarhum', label: 'Nama Almarhum/Almarhumah', type: 'text', required: true },
      { name: 'nikAlmarhum', label: 'NIK Almarhum/Almarhumah', type: 'text', required: true, placeholder: '16 digit angka' },
      JENIS_KELAMIN,
      { name: 'agama', label: 'Agama', type: 'text' },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      { name: 'hariMeninggal', label: 'Hari Meninggal', type: 'text', required: true, placeholder: 'Contoh: Rabu' },
      { name: 'tanggalMeninggal', label: 'Tanggal Meninggal', type: 'date', required: true },
      { name: 'tempatMeninggal', label: 'Tempat Meninggal', type: 'text', required: true, placeholder: 'Contoh: Rumah duka' },
      { name: 'penyebabMeninggal', label: 'Penyebab Meninggal', type: 'text', required: true, placeholder: 'Contoh: Sakit' },
      { name: 'namaPelapor', label: 'Nama Pelapor', type: 'text', required: true },
      { name: 'nikPelapor', label: 'NIK Pelapor', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tempatLahirPelapor', label: 'Tempat Lahir Pelapor', type: 'text' },
      { name: 'tanggalLahirPelapor', label: 'Tanggal Lahir Pelapor', type: 'date' },
      { name: 'pekerjaanPelapor', label: 'Pekerjaan Pelapor', type: 'text' },
      { name: 'alamatPelapor', label: 'Alamat Pelapor', type: 'textarea', required: true, rows: 2 },
      { name: 'hubunganPelapor', label: 'Hubungan Pelapor dengan Almarhum/Almarhumah', type: 'text', required: true, placeholder: 'Contoh: Istri' },
    ],
  },
  {
    id: 'usaha',
    title: 'SURAT KETERANGAN USAHA',
    shortTitle: 'Keterangan Usaha (SKU)',
    description: 'Menerangkan usaha yang dijalankan warga desa.',
    category: 'Surat Desa',
    nomorPrefix: '510',
    nomorSuffix: 'PEM',
    applicantField: 'namaLengkap',
    fields: [
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      { name: 'namaUsaha', label: 'Nama / Jenis Usaha', type: 'text', required: true, placeholder: 'Contoh: Warung Jajanan' },
      { name: 'alamatUsaha', label: 'Alamat Tempat Usaha', type: 'textarea', required: true, rows: 2 },
    ],
  },
  {
    id: 'duda_janda',
    title: 'SURAT KETERANGAN DUDA/JANDA',
    shortTitle: 'Keterangan Duda/Janda',
    description: 'Menerangkan status duda atau janda warga desa.',
    category: 'Surat Desa',
    nomorPrefix: '474.2',
    nomorSuffix: 'KS',
    applicantField: 'namaLengkap',
    fields: [
      {
        name: 'statusDudaJanda',
        label: 'Status',
        type: 'select',
        defaultValue: 'Duda',
        options: [
          { value: 'Duda', label: 'Duda' },
          { value: 'Janda', label: 'Janda' },
        ],
      },
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      { name: 'keteranganRT', label: 'Keterangan Ketua RT', type: 'textarea', rows: 2, placeholder: 'Keterangan dari Ketua RT setempat' },
    ],
  },

  // ================= FASE 2 — FORMULIR DUKCAPIL SERI F =================
  {
    id: 'f101',
    title: 'FORMULIR F-1.01 — BIODATA KELUARGA',
    shortTitle: 'F-1.01 Biodata Keluarga',
    description: 'Formulir biodata keluarga: kepala keluarga + daftar anggota keluarga (maks. 10 anggota).',
    category: 'Formulir Dukcapil',
    nomorPrefix: '',
    nomorSuffix: '',
    kop: 'disdukcapil',
    hideNomor: true,
    applicantField: 'namaKepalaKeluarga',
    fields: [
      { name: 'namaKepalaKeluarga', label: 'Nama Kepala Keluarga', type: 'text', required: true },
      { name: 'nikKepalaKeluarga', label: 'NIK Kepala Keluarga', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'noKK', label: 'Nomor Kartu Keluarga', type: 'text', required: true },
      { name: 'alamatKepalaKeluarga', label: 'Alamat Keluarga', type: 'textarea', required: true, rows: 2 },
      {
        name: 'daftarAnggota',
        label: 'Daftar Anggota Keluarga (maks. 10)',
        type: 'table',
        minRows: 1,
        maxRows: 10,
        columns: [
          { name: 'nama', label: 'Nama' },
          { name: 'nik', label: 'NIK' },
          { name: 'shdk', label: 'SHDK' },
          { name: 'tempatLahir', label: 'Tempat Lahir' },
          { name: 'tanggalLahir', label: 'Tgl Lahir' },
          { name: 'agama', label: 'Agama' },
          { name: 'pendidikan', label: 'Pendidikan' },
          { name: 'pekerjaan', label: 'Pekerjaan' },
          { name: 'statusKawin', label: 'Status Kawin' },
          { name: 'namaAyah', label: 'Nama Ayah' },
          { name: 'namaIbu', label: 'Nama Ibu' },
        ],
      },
    ],
  },
  {
    id: 'f102',
    title: 'FORMULIR F-1.02 — PENDAFTARAN PERISTIWA KEPENDUDUKAN',
    shortTitle: 'F-1.02 Peristiwa Kependudukan',
    description: 'Pendaftaran peristiwa kependudukan: KK, KTP-el, KIA, dan perubahan data.',
    category: 'Formulir Dukcapil',
    nomorPrefix: '',
    nomorSuffix: '',
    kop: 'disdukcapil',
    hideNomor: true,
    applicantField: 'namaLengkap',
    fields: [
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'noKK', label: 'Nomor Kartu Keluarga', type: 'text', required: true },
      {
        name: 'jenisPermohonan',
        label: 'Jenis Permohonan',
        type: 'select',
        required: true,
        options: [
          { value: '', label: 'Pilih...' },
          { value: 'KK baru', label: 'KK baru' },
          { value: 'KK perubahan', label: 'KK perubahan' },
          { value: 'KK hilang / rusak', label: 'KK hilang / rusak' },
          { value: 'KTP-el baru', label: 'KTP-el baru' },
          { value: 'KTP-el pindah', label: 'KTP-el pindah' },
          { value: 'KTP-el hilang / rusak', label: 'KTP-el hilang / rusak' },
          { value: 'Perpanjangan ITAP', label: 'Perpanjangan ITAP' },
          { value: 'KIA', label: 'KIA (Kartu Identitas Anak)' },
          { value: 'Perubahan data', label: 'Perubahan data' },
        ],
      },
      { name: 'daftarLampiran', label: 'Daftar Persyaratan / Lampiran', type: 'textarea', rows: 3, placeholder: 'Tuliskan dokumen lampiran yang disertakan' },
    ],
  },
  {
    id: 'f103',
    title: 'FORMULIR F-1.03 — PENDAFTARAN PERPINDAHAN PENDUDUK',
    shortTitle: 'F-1.03 Perpindahan Penduduk',
    description: 'Pendaftaran perpindahan penduduk (SKP / SKPLN / SKTT).',
    category: 'Formulir Dukcapil',
    nomorPrefix: '',
    nomorSuffix: '',
    kop: 'disdukcapil',
    hideNomor: true,
    applicantField: 'namaPemohon',
    fields: [
      { name: 'noKK', label: 'Nomor Kartu Keluarga', type: 'text', required: true },
      { name: 'namaPemohon', label: 'Nama Pemohon', type: 'text', required: true },
      { name: 'nik', label: 'NIK Pemohon', type: 'text', required: true, placeholder: '16 digit angka' },
      {
        name: 'jenisPermohonan',
        label: 'Jenis Permohonan',
        type: 'select',
        required: true,
        options: [
          { value: '', label: 'Pilih...' },
          { value: 'SKP', label: 'SKP (Surat Keterangan Pindah)' },
          { value: 'SKPLN', label: 'SKPLN (Surat Keterangan Pindah Luar Negeri)' },
          { value: 'SKTT', label: 'SKTT (Surat Keterangan Tempat Tinggal)' },
        ],
      },
      { name: 'alamatAsal', label: 'Alamat Asal', type: 'textarea', required: true, rows: 2 },
      { name: 'rt', label: 'RT (alamat asal)', type: 'text', placeholder: 'Contoh: 001' },
      { name: 'rw', label: 'RW (alamat asal)', type: 'text', placeholder: 'Contoh: 006' },
      {
        name: 'klasifikasiPindah',
        label: 'Klasifikasi Kepindahan',
        type: 'select',
        required: true,
        options: [
          { value: '', label: 'Pilih...' },
          { value: 'Dalam desa', label: 'Dalam desa' },
          { value: 'Antar desa', label: 'Antar desa' },
          { value: 'Antar kecamatan', label: 'Antar kecamatan' },
          { value: 'Antar kabupaten', label: 'Antar kabupaten' },
          { value: 'Antar provinsi', label: 'Antar provinsi' },
        ],
      },
      { name: 'alamatPindah', label: 'Alamat Pindah (tujuan)', type: 'textarea', required: true, rows: 2 },
      { name: 'alasanPindah', label: 'Alasan Pindah', type: 'text', required: true },
      {
        name: 'jenisKepindahan',
        label: 'Jenis Kepindahan',
        type: 'select',
        options: [
          { value: '', label: 'Pilih...' },
          { value: 'Kepala keluarga', label: 'Kepala keluarga' },
          { value: 'Kepala keluarga dan seluruh anggota keluarga', label: 'Kepala keluarga dan seluruh anggota keluarga' },
          { value: 'Kepala keluarga dan sebagian anggota keluarga', label: 'Kepala keluarga dan sebagian anggota keluarga' },
          { value: 'Anggota keluarga', label: 'Anggota keluarga' },
        ],
      },
      {
        name: 'daftarAnggotaPindah',
        label: 'Daftar Anggota Keluarga yang Pindah',
        type: 'table',
        minRows: 1,
        maxRows: 10,
        columns: [
          { name: 'nik', label: 'NIK' },
          { name: 'nama', label: 'Nama' },
          { name: 'shdk', label: 'SHDK' },
        ],
      },
      { name: 'anggotaTidakPindah', label: 'Anggota Keluarga yang Tidak Pindah (nama, pisahkan dengan koma)', type: 'textarea', rows: 2 },
    ],
  },
  {
    id: 'f104',
    title: 'F-1.04 — SURAT PERNYATAAN TIDAK MEMILIKI DOKUMEN KEPENDUDUKAN',
    shortTitle: 'F-1.04 Tanpa Dokumen Kependudukan',
    description: 'Pernyataan tidak memiliki dokumen kependudukan (bermaterai).',
    category: 'Formulir Dukcapil',
    nomorPrefix: '',
    nomorSuffix: '',
    kop: 'disdukcapil',
    hideNomor: true,
    applicantField: 'namaLengkap',
    fields: [
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'namaIbu', label: 'Nama Ibu', type: 'text', required: true },
    ],
  },
  {
    id: 'f105',
    title: 'F-1.05 — SPTJM PERKAWINAN BELUM TERCATAT',
    shortTitle: 'F-1.05 SPTJM Perkawinan',
    description: 'SPTJM perkawinan belum tercatat (suami, istri, saksi, daftar anak).',
    category: 'Formulir Dukcapil',
    nomorPrefix: '',
    nomorSuffix: '',
    kop: 'disdukcapil',
    hideNomor: true,
    applicantField: 'namaSuami',
    fields: [
      { name: 'namaSuami', label: 'Nama Suami', type: 'text', required: true },
      { name: 'nikSuami', label: 'NIK Suami', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'namaIstri', label: 'Nama Istri', type: 'text', required: true },
      { name: 'nikIstri', label: 'NIK Istri', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tanggalPerkawinan', label: 'Tanggal Perkawinan', type: 'date', required: true },
      { name: 'bulanPerkawinan', label: 'Bulan Perkawinan', type: 'text', placeholder: 'Contoh: Mei' },
      { name: 'tahunPerkawinan', label: 'Tahun Perkawinan', type: 'text', placeholder: 'Contoh: 2015' },
      { name: 'namaSaksi1', label: 'Nama Saksi 1', type: 'text', required: true },
      { name: 'nikSaksi1', label: 'NIK Saksi 1', type: 'text', placeholder: '16 digit angka' },
      { name: 'namaSaksi2', label: 'Nama Saksi 2', type: 'text', required: true },
      { name: 'nikSaksi2', label: 'NIK Saksi 2', type: 'text', placeholder: '16 digit angka' },
      {
        name: 'daftarAnak',
        label: 'Daftar Anak',
        type: 'table',
        minRows: 1,
        maxRows: 10,
        columns: [
          { name: 'nama', label: 'Nama Anak' },
          { name: 'noAktaKelahiran', label: 'No. Akta Kelahiran' },
          { name: 'shdk', label: 'SHDK' },
        ],
      },
    ],
  },
  {
    id: 'f106',
    title: 'F-1.06 — SURAT PERNYATAAN PERUBAHAN ELEMEN DATA KEPENDUDUKAN',
    shortTitle: 'F-1.06 Perubahan Elemen Data',
    description: 'Pernyataan perubahan elemen data kependudukan (anggota keluarga, pendidikan, pekerjaan, agama).',
    category: 'Formulir Dukcapil',
    nomorPrefix: '',
    nomorSuffix: '',
    kop: 'disdukcapil',
    hideNomor: true,
    applicantField: 'namaLengkap',
    fields: [
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'noKK', label: 'Nomor Kartu Keluarga', type: 'text', required: true },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      {
        name: 'tabelAnggota',
        label: 'Tabel Anggota Keluarga (maks. 7 baris)',
        type: 'table',
        minRows: 1,
        maxRows: 7,
        columns: [
          { name: 'nama', label: 'Nama Anggota Keluarga' },
          { name: 'perubahan', label: 'Perubahan Elemen Data' },
        ],
      },
      { name: 'perubahanPendidikanSemula', label: 'Pendidikan — Semula', type: 'text' },
      { name: 'perubahanPendidikanMenjadi', label: 'Pendidikan — Menjadi', type: 'text' },
      { name: 'dasarPerubahanPendidikan', label: 'Pendidikan — Dasar Perubahan', type: 'text' },
      { name: 'perubahanPekerjaanSemula', label: 'Pekerjaan — Semula', type: 'text' },
      { name: 'perubahanPekerjaanMenjadi', label: 'Pekerjaan — Menjadi', type: 'text' },
      { name: 'dasarPerubahanPekerjaan', label: 'Pekerjaan — Dasar Perubahan', type: 'text' },
      { name: 'perubahanAgama', label: 'Perubahan Agama (semula → menjadi)', type: 'text' },
    ],
  },
  {
    id: 'f115',
    title: 'FORMULIR F-1.15 — PERMOHONAN KK BARU WNI',
    shortTitle: 'F-1.15 Permohonan KK Baru',
    description: 'Permohonan Kartu Keluarga baru bagi WNI.',
    category: 'Formulir Dukcapil',
    nomorPrefix: '',
    nomorSuffix: '',
    kop: 'disdukcapil',
    hideNomor: true,
    applicantField: 'namaPemohon',
    fields: [
      { name: 'namaPemohon', label: 'Nama Pemohon', type: 'text', required: true },
      { name: 'nik', label: 'NIK Pemohon', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'noKKLama', label: 'Nomor KK Semula', type: 'text' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      { name: 'rt', label: 'RT', type: 'text', placeholder: 'Contoh: 001' },
      { name: 'rw', label: 'RW', type: 'text', placeholder: 'Contoh: 006' },
      {
        name: 'alasanPermohonan',
        label: 'Alasan Permohonan',
        type: 'select',
        required: true,
        options: [
          { value: '', label: 'Pilih...' },
          { value: 'Membentuk RT baru', label: 'Membentuk RT baru' },
          { value: 'KK hilang / rusak', label: 'KK hilang / rusak' },
          { value: 'Lainnya', label: 'Lainnya' },
        ],
      },
      { name: 'jumlahAnggota', label: 'Jumlah Anggota Keluarga', type: 'number' },
      {
        name: 'daftarAnggota',
        label: 'Daftar Anggota Keluarga',
        type: 'table',
        minRows: 1,
        maxRows: 15,
        columns: [
          { name: 'nik', label: 'NIK' },
          { name: 'nama', label: 'Nama' },
          { name: 'shdk', label: 'SHDK' },
        ],
      },
    ],
  },
  {
    id: 'f201',
    title: 'FORMULIR F-2.01 — PELAPORAN PENCATATAN SIPIL',
    shortTitle: 'F-2.01 Pelaporan Pencatatan Sipil',
    description: 'Pelaporan pencatatan sipil di dalam wilayah NKRI (16 jenis pelaporan; hanya seksi yang dipilih yang tampil).',
    category: 'Formulir Dukcapil',
    nomorPrefix: '',
    nomorSuffix: '',
    kop: 'disdukcapil',
    hideNomor: true,
    applicantField: 'namaPelapor',
    fields: [
      {
        name: 'jenisPelaporan',
        label: 'Jenis Pelaporan',
        type: 'select',
        required: true,
        options: [
          { value: '', label: 'Pilih jenis pelaporan...' },
          { value: 'Kelahiran', label: 'Kelahiran' },
          { value: 'Lahir Mati', label: 'Lahir Mati' },
          { value: 'Perkawinan', label: 'Perkawinan' },
          { value: 'Pembatalan Perkawinan', label: 'Pembatalan Perkawinan' },
          { value: 'Perceraian', label: 'Perceraian' },
          { value: 'Pembatalan Perceraian', label: 'Pembatalan Perceraian' },
          { value: 'Kematian', label: 'Kematian' },
          { value: 'Pengangkatan Anak', label: 'Pengangkatan Anak' },
          { value: 'Pengakuan Anak', label: 'Pengakuan Anak' },
          { value: 'Pengesahan Anak', label: 'Pengesahan Anak' },
          { value: 'Perubahan Nama', label: 'Perubahan Nama' },
          { value: 'Perubahan Status Kewarganegaraan', label: 'Perubahan Status Kewarganegaraan' },
          { value: 'Peristiwa Penting Lainnya', label: 'Peristiwa Penting Lainnya' },
          { value: 'Pembetulan Akta', label: 'Pembetulan Akta' },
          { value: 'Pembatalan Akta', label: 'Pembatalan Akta' },
          { value: 'Pelaporan dari Luar Wilayah NKRI', label: 'Pelaporan dari Luar Wilayah NKRI' },
        ],
      },
      // --- Data pelapor ---
      { name: 'namaPelapor', label: 'Nama Pelapor', type: 'text', required: true },
      { name: 'nikPelapor', label: 'NIK Pelapor', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'alamatPelapor', label: 'Alamat Pelapor', type: 'textarea', required: true, rows: 2 },
      // --- Subjek akta ---
      { name: 'namaSubjek1', label: 'Subjek Akta I — Nama', type: 'text', required: true },
      { name: 'nikSubjek1', label: 'Subjek Akta I — NIK', type: 'text', placeholder: '16 digit angka' },
      { name: 'namaSubjek2', label: 'Subjek Akta II — Nama', type: 'text' },
      { name: 'nikSubjek2', label: 'Subjek Akta II — NIK', type: 'text', placeholder: '16 digit angka' },
      // --- Saksi ---
      { name: 'namaSaksi1', label: 'Saksi I — Nama', type: 'text', required: true },
      { name: 'nikSaksi1', label: 'Saksi I — NIK', type: 'text', placeholder: '16 digit angka' },
      { name: 'namaSaksi2', label: 'Saksi II — Nama', type: 'text', required: true },
      { name: 'nikSaksi2', label: 'Saksi II — NIK', type: 'text', placeholder: '16 digit angka' },
      // --- Orang tua ---
      { name: 'namaAyah', label: 'Orang Tua — Nama Ayah', type: 'text' },
      { name: 'nikAyah', label: 'Orang Tua — NIK Ayah', type: 'text', placeholder: '16 digit angka' },
      { name: 'namaIbu', label: 'Orang Tua — Nama Ibu', type: 'text' },
      { name: 'nikIbu', label: 'Orang Tua — NIK Ibu', type: 'text', placeholder: '16 digit angka' },
      // --- Seksi kondisional: Kelahiran ---
      { name: 'namaAnak', label: 'Data Anak — Nama', type: 'text', required: true, visibleWhen: { field: 'jenisPelaporan', values: ['Kelahiran'] } },
      { name: 'tempatLahirAnak', label: 'Data Anak — Tempat Lahir', type: 'text', visibleWhen: { field: 'jenisPelaporan', values: ['Kelahiran'] } },
      { name: 'tanggalLahirAnak', label: 'Data Anak — Tanggal Lahir', type: 'date', required: true, visibleWhen: { field: 'jenisPelaporan', values: ['Kelahiran'] } },
      { name: 'pukulLahirAnak', label: 'Data Anak — Pukul Lahir', type: 'text', placeholder: 'Contoh: 08.30', visibleWhen: { field: 'jenisPelaporan', values: ['Kelahiran'] } },
      { name: 'jenisKelaminAnak', label: 'Data Anak — Jenis Kelamin', type: 'select', options: [{ value: '', label: 'Pilih...' }, { value: 'Laki-laki', label: 'Laki-laki' }, { value: 'Perempuan', label: 'Perempuan' }], visibleWhen: { field: 'jenisPelaporan', values: ['Kelahiran'] } },
      { name: 'anakKe', label: 'Data Anak — Anak Ke-', type: 'number', visibleWhen: { field: 'jenisPelaporan', values: ['Kelahiran'] } },
      // --- Seksi kondisional: Lahir Mati ---
      { name: 'tanggalLahirMati', label: 'Lahir Mati — Tanggal', type: 'date', required: true, visibleWhen: { field: 'jenisPelaporan', values: ['Lahir Mati'] } },
      { name: 'pukulLahirMati', label: 'Lahir Mati — Pukul', type: 'text', placeholder: 'Contoh: 08.30', visibleWhen: { field: 'jenisPelaporan', values: ['Lahir Mati'] } },
      { name: 'tempatLahirMati', label: 'Lahir Mati — Tempat', type: 'text', visibleWhen: { field: 'jenisPelaporan', values: ['Lahir Mati'] } },
      { name: 'jenisKelaminLahirMati', label: 'Lahir Mati — Jenis Kelamin', type: 'select', options: [{ value: '', label: 'Pilih...' }, { value: 'Laki-laki', label: 'Laki-laki' }, { value: 'Perempuan', label: 'Perempuan' }], visibleWhen: { field: 'jenisPelaporan', values: ['Lahir Mati'] } },
      { name: 'keteranganLahirMati', label: 'Lahir Mati — Keterangan', type: 'textarea', rows: 2, visibleWhen: { field: 'jenisPelaporan', values: ['Lahir Mati'] } },
      // --- Seksi kondisional: Kematian ---
      { name: 'namaMeninggal', label: 'Data Kematian — Nama', type: 'text', required: true, visibleWhen: { field: 'jenisPelaporan', values: ['Kematian'] } },
      { name: 'nikMeninggal', label: 'Data Kematian — NIK', type: 'text', placeholder: '16 digit angka', visibleWhen: { field: 'jenisPelaporan', values: ['Kematian'] } },
      { name: 'tanggalMeninggal', label: 'Data Kematian — Tanggal Meninggal', type: 'date', required: true, visibleWhen: { field: 'jenisPelaporan', values: ['Kematian'] } },
      { name: 'pukulMeninggal', label: 'Data Kematian — Pukul', type: 'text', placeholder: 'Contoh: 21.15', visibleWhen: { field: 'jenisPelaporan', values: ['Kematian'] } },
      { name: 'sebabMeninggal', label: 'Data Kematian — Sebab Kematian', type: 'text', visibleWhen: { field: 'jenisPelaporan', values: ['Kematian'] } },
      { name: 'tempatMeninggal', label: 'Data Kematian — Tempat Meninggal', type: 'text', visibleWhen: { field: 'jenisPelaporan', values: ['Kematian'] } },
      { name: 'yangMenerangkan', label: 'Data Kematian — Yang Menerangkan', type: 'text', visibleWhen: { field: 'jenisPelaporan', values: ['Kematian'] } },
      // --- Seksi kondisional: jenis pelaporan lain (data peristiwa umum) ---
      { name: 'tanggalPeristiwa', label: 'Data Peristiwa — Tanggal', type: 'date', required: true, visibleWhen: { field: 'jenisPelaporan', values: ['Perkawinan', 'Pembatalan Perkawinan', 'Perceraian', 'Pembatalan Perceraian', 'Pengangkatan Anak', 'Pengakuan Anak', 'Pengesahan Anak', 'Perubahan Nama', 'Perubahan Status Kewarganegaraan', 'Peristiwa Penting Lainnya', 'Pembetulan Akta', 'Pembatalan Akta', 'Pelaporan dari Luar Wilayah NKRI'] } },
      { name: 'tempatPeristiwa', label: 'Data Peristiwa — Tempat', type: 'text', visibleWhen: { field: 'jenisPelaporan', values: ['Perkawinan', 'Pembatalan Perkawinan', 'Perceraian', 'Pembatalan Perceraian', 'Pengangkatan Anak', 'Pengakuan Anak', 'Pengesahan Anak', 'Perubahan Nama', 'Perubahan Status Kewarganegaraan', 'Peristiwa Penting Lainnya', 'Pembetulan Akta', 'Pembatalan Akta', 'Pelaporan dari Luar Wilayah NKRI'] } },
      { name: 'keteranganPeristiwa', label: 'Data Peristiwa — Keterangan', type: 'textarea', rows: 2, visibleWhen: { field: 'jenisPelaporan', values: ['Perkawinan', 'Pembatalan Perkawinan', 'Perceraian', 'Pembatalan Perceraian', 'Pengangkatan Anak', 'Pengakuan Anak', 'Pengesahan Anak', 'Perubahan Nama', 'Perubahan Status Kewarganegaraan', 'Peristiwa Penting Lainnya', 'Pembetulan Akta', 'Pembatalan Akta', 'Pelaporan dari Luar Wilayah NKRI'] } },
    ],
  },
  {
    id: 'f203',
    title: 'F-2.03 — SPTJM KEBENARAN DATA KELAHIRAN',
    shortTitle: 'F-2.03 SPTJM Kelahiran',
    description: 'SPTJM kebenaran data kelahiran (yang menyatakan, anak, ibu, penolong, saksi).',
    category: 'Formulir Dukcapil',
    nomorPrefix: '',
    nomorSuffix: '',
    kop: 'disdukcapil',
    hideNomor: true,
    applicantField: 'namaYangMenyatakan',
    fields: [
      { name: 'namaYangMenyatakan', label: 'Nama Yang Menyatakan', type: 'text', required: true },
      { name: 'nik', label: 'NIK Yang Menyatakan', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      { name: 'namaAnak', label: 'Anak — Nama', type: 'text', required: true },
      { name: 'nikAnak', label: 'Anak — NIK', type: 'text', placeholder: '16 digit angka' },
      { name: 'tempatLahirAnak', label: 'Anak — Tempat Lahir', type: 'text' },
      { name: 'tanggalLahirAnak', label: 'Anak — Tanggal Lahir', type: 'date', required: true },
      { name: 'jenisKelaminAnak', label: 'Anak — Jenis Kelamin', type: 'select', options: [{ value: '', label: 'Pilih...' }, { value: 'Laki-laki', label: 'Laki-laki' }, { value: 'Perempuan', label: 'Perempuan' }] },
      { name: 'anakKe', label: 'Anak — Anak Ke-', type: 'number' },
      { name: 'alamatAnak', label: 'Anak — Alamat', type: 'textarea', rows: 2 },
      { name: 'namaIbu', label: 'Ibu — Nama', type: 'text', required: true },
      { name: 'nikIbu', label: 'Ibu — NIK', type: 'text', placeholder: '16 digit angka' },
      { name: 'tempatLahirIbu', label: 'Ibu — Tempat Lahir', type: 'text' },
      { name: 'tanggalLahirIbu', label: 'Ibu — Tanggal Lahir', type: 'date' },
      { name: 'pekerjaanIbu', label: 'Ibu — Pekerjaan', type: 'text' },
      { name: 'alamatIbu', label: 'Ibu — Alamat', type: 'textarea', rows: 2 },
      { name: 'namaPenolong', label: 'Penolong Kelahiran — Nama', type: 'text' },
      { name: 'nikPenolong', label: 'Penolong Kelahiran — NIK', type: 'text', placeholder: '16 digit angka' },
      { name: 'pekerjaanPenolong', label: 'Penolong Kelahiran — Pekerjaan', type: 'text' },
      { name: 'alamatPenolong', label: 'Penolong Kelahiran — Alamat', type: 'textarea', rows: 2 },
      { name: 'namaSaksi1', label: 'Nama Saksi 1', type: 'text', required: true },
      { name: 'nikSaksi1', label: 'NIK Saksi 1', type: 'text', placeholder: '16 digit angka' },
      { name: 'namaSaksi2', label: 'Nama Saksi 2', type: 'text', required: true },
      { name: 'nikSaksi2', label: 'NIK Saksi 2', type: 'text', placeholder: '16 digit angka' },
    ],
  },
  {
    id: 'f204',
    title: 'F-2.04 — SPTJM KEBENARAN SEBAGAI PASANGAN SUAMI ISTRI',
    shortTitle: 'F-2.04 SPTJM Suami Istri',
    description: 'SPTJM kebenaran sebagai pasangan suami istri.',
    category: 'Formulir Dukcapil',
    nomorPrefix: '',
    nomorSuffix: '',
    kop: 'disdukcapil',
    hideNomor: true,
    applicantField: 'namaYangMenyatakan',
    fields: [
      { name: 'namaYangMenyatakan', label: 'Nama Yang Menyatakan', type: 'text', required: true },
      { name: 'nik', label: 'NIK Yang Menyatakan', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      { name: 'namaPasangan', label: 'Nama Pasangan (Suami/Istri)', type: 'text', required: true },
      { name: 'nikPasangan', label: 'NIK Pasangan', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tempatLahirPasangan', label: 'Tempat Lahir Pasangan', type: 'text' },
      { name: 'tanggalLahirPasangan', label: 'Tanggal Lahir Pasangan', type: 'date' },
      { name: 'pekerjaanPasangan', label: 'Pekerjaan Pasangan', type: 'text' },
      { name: 'alamatPasangan', label: 'Alamat Pasangan', type: 'textarea', rows: 2 },
      { name: 'noKK', label: 'Nomor Kartu Keluarga', type: 'text' },
      { name: 'namaSaksi1', label: 'Nama Saksi 1', type: 'text', required: true },
      { name: 'nikSaksi1', label: 'NIK Saksi 1', type: 'text', placeholder: '16 digit angka' },
      { name: 'namaSaksi2', label: 'Nama Saksi 2', type: 'text', required: true },
      { name: 'nikSaksi2', label: 'NIK Saksi 2', type: 'text', placeholder: '16 digit angka' },
    ],
  },

  // ================= FASE 2 — SPTJM BARU =================
  {
    id: 'sptjm_kematian',
    title: 'SPTJM KEBENARAN DATA KEMATIAN',
    shortTitle: 'SPTJM Kematian',
    description: 'SPTJM kebenaran data kematian untuk pengurusan akta kematian (bermaterai).',
    category: 'SPTJM',
    nomorPrefix: '',
    nomorSuffix: '',
    hideNomor: true,
    applicantField: 'namaYangMenyatakan',
    fields: [
      { name: 'namaYangMenyatakan', label: 'Nama Yang Menyatakan', type: 'text', required: true },
      { name: 'nik', label: 'NIK Yang Menyatakan', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      { name: 'namaAlmarhum', label: 'Almarhum/Almarhumah — Nama', type: 'text', required: true },
      { name: 'nikAlmarhum', label: 'Almarhum/Almarhumah — NIK', type: 'text', placeholder: '16 digit angka' },
      { name: 'tempatLahirAlmarhum', label: 'Almarhum/Almarhumah — Tempat Lahir', type: 'text' },
      { name: 'tanggalLahirAlmarhum', label: 'Almarhum/Almarhumah — Tanggal Lahir', type: 'date' },
      { name: 'alamatAlmarhum', label: 'Almarhum/Almarhumah — Alamat Terakhir (sesuai KTP)', type: 'textarea', rows: 2 },
      { name: 'hariMeninggal', label: 'Hari Meninggal', type: 'text', required: true, placeholder: 'Contoh: Rabu' },
      { name: 'tanggalMeninggal', label: 'Tanggal Meninggal', type: 'date', required: true },
      { name: 'jamMeninggal', label: 'Jam Meninggal', type: 'text', placeholder: 'Contoh: 21.15' },
      { name: 'tempatMeninggal', label: 'Tempat Meninggal', type: 'text', required: true },
      { name: 'namaSaksi1', label: 'Nama Saksi 1', type: 'text', required: true },
      { name: 'nikSaksi1', label: 'NIK Saksi 1', type: 'text', placeholder: '16 digit angka' },
      { name: 'namaSaksi2', label: 'Nama Saksi 2', type: 'text', required: true },
      { name: 'nikSaksi2', label: 'NIK Saksi 2', type: 'text', placeholder: '16 digit angka' },
    ],
  },
  {
    id: 'sptjm_perkawinan_perceraian',
    title: 'SPTJM PERKAWINAN/PERCERAIAN BELUM TERCATAT',
    shortTitle: 'SPTJM Perkawinan/Perceraian',
    description: 'SPTJM perkawinan atau perceraian belum tercatat (bermaterai).',
    category: 'SPTJM',
    nomorPrefix: '',
    nomorSuffix: '',
    hideNomor: true,
    applicantField: 'namaSuami',
    fields: [
      {
        name: 'jenisSPTJM',
        label: 'Jenis SPTJM',
        type: 'select',
        defaultValue: 'Perkawinan',
        options: [
          { value: 'Perkawinan', label: 'Perkawinan Belum Tercatat' },
          { value: 'Perceraian', label: 'Perceraian Belum Tercatat' },
        ],
      },
      { name: 'namaSuami', label: 'Nama Suami', type: 'text', required: true },
      { name: 'nikSuami', label: 'NIK Suami', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'namaIstri', label: 'Nama Istri', type: 'text', required: true },
      { name: 'nikIstri', label: 'NIK Istri', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tanggalPeristiwa', label: 'Tanggal Perkawinan / Perceraian', type: 'date', required: true },
      { name: 'namaSaksi1', label: 'Nama Saksi 1', type: 'text', required: true },
      { name: 'nikSaksi1', label: 'NIK Saksi 1', type: 'text', placeholder: '16 digit angka' },
      { name: 'namaSaksi2', label: 'Nama Saksi 2', type: 'text', required: true },
      { name: 'nikSaksi2', label: 'NIK Saksi 2', type: 'text', placeholder: '16 digit angka' },
      {
        name: 'daftarAnak',
        label: 'Daftar Anak',
        type: 'table',
        minRows: 1,
        maxRows: 10,
        columns: [
          { name: 'nama', label: 'Nama Anak' },
          { name: 'noAktaKelahiran', label: 'No. Akta Kelahiran' },
          { name: 'shdk', label: 'SHDK' },
        ],
      },
    ],
  },

  // ================= FASE 2 — BANSOS PBI-JKN =================
  {
    id: 'pbi_asesmen',
    title: 'INSTRUMEN ASESMEN CALON PENERIMA BANSOS KIS-PBI / SKM',
    shortTitle: 'Asesmen PBI-JKN',
    description: 'Instrumen asesmen calon penerima bansos KIS-PBI / SKM (wawancara petugas).',
    category: 'Bansos (PBI-JKN)',
    nomorPrefix: '',
    nomorSuffix: '',
    hideNomor: true,
    applicantField: 'namaLengkap',
    fields: [
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'jenisKelamin', label: 'Jenis Kelamin', type: 'select', options: [{ value: '', label: 'Pilih...' }, { value: 'Laki-laki', label: 'Laki-laki' }, { value: 'Perempuan', label: 'Perempuan' }] },
      {
        name: 'statusKawin',
        label: 'Status Kawin',
        type: 'select',
        options: [
          { value: '', label: 'Pilih...' },
          { value: 'Belum Kawin', label: 'Belum Kawin' },
          { value: 'Kawin', label: 'Kawin' },
          { value: 'Cerai Hidup', label: 'Cerai Hidup' },
          { value: 'Cerai Mati', label: 'Cerai Mati' },
        ],
      },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'noKK', label: 'Nomor Kartu Keluarga', type: 'text' },
      { name: 'namaIbuKandung', label: 'Nama Ibu Kandung', type: 'text' },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      {
        name: 'jaminanKesehatan',
        label: '1. Jaminan kesehatan yang dimiliki',
        type: 'select',
        options: [
          { value: '', label: 'Pilih...' },
          { value: 'BPJS PBI', label: 'BPJS PBI' },
          { value: 'BPJS Mandiri', label: 'BPJS Mandiri' },
          { value: 'Asuransi lain', label: 'Asuransi lain' },
          { value: 'Tidak punya jaminan kesehatan', label: 'Tidak punya jaminan kesehatan' },
        ],
      },
      { name: 'alasanNonAktif', label: '2. Alasan jaminan kesehatan non-aktif (bila ada)', type: 'textarea', rows: 2 },
      {
        name: 'statusRumah',
        label: '3. Status rumah',
        type: 'select',
        options: [
          { value: '', label: 'Pilih...' },
          { value: 'Milik sendiri', label: 'Milik sendiri' },
          { value: 'Sewa', label: 'Sewa' },
          { value: 'Menumpang', label: 'Menumpang' },
          { value: 'Lainnya', label: 'Lainnya' },
        ],
      },
      {
        name: 'kondisiRumah',
        label: '4. Kondisi rumah',
        type: 'select',
        options: [
          { value: '', label: 'Pilih...' },
          { value: 'Baik', label: 'Baik' },
          { value: 'Rusak ringan', label: 'Rusak ringan' },
          { value: 'Rusak berat', label: 'Rusak berat' },
        ],
      },
      { name: 'ukuranRumah', label: '5. Ukuran rumah', type: 'text', placeholder: 'Contoh: 6 x 8 m' },
      {
        name: 'dindingRumah',
        label: '6. Dinding rumah',
        type: 'select',
        options: [
          { value: '', label: 'Pilih...' },
          { value: 'Tembok', label: 'Tembok' },
          { value: 'Kayu', label: 'Kayu' },
          { value: 'Bambu', label: 'Bambu' },
          { value: 'Lainnya', label: 'Lainnya' },
        ],
      },
      {
        name: 'peneranganRumah',
        label: '7. Penerangan rumah',
        type: 'select',
        options: [
          { value: '', label: 'Pilih...' },
          { value: 'Listrik PLN', label: 'Listrik PLN' },
          { value: 'Listrik non-PLN', label: 'Listrik non-PLN' },
          { value: 'Lampu minyak', label: 'Lampu minyak' },
          { value: 'Lainnya', label: 'Lainnya' },
        ],
      },
      { name: 'jumlahKeluarga', label: '8. Jumlah keluarga dalam satu rumah', type: 'number' },
      { name: 'jumlahAnggota', label: '9. Jumlah anggota keluarga', type: 'number' },
      { name: 'bansosDiterima', label: '10. Bansos yang diterima saat ini', type: 'textarea', rows: 2 },
      { name: 'usahaPenghasilan', label: '11. Usaha / penghasilan', type: 'textarea', rows: 2 },
      { name: 'catatanPetugas', label: 'Catatan petugas', type: 'textarea', rows: 2 },
    ],
  },
  {
    id: 'pbi_pernyataan',
    title: 'SURAT PERNYATAAN (PBI-JKN)',
    shortTitle: 'Pernyataan PBI-JKN',
    description: 'Surat pernyataan calon penerima bansos PBI-JKN.',
    category: 'Bansos (PBI-JKN)',
    nomorPrefix: '474.4',
    nomorSuffix: 'KS',
    applicantField: 'namaLengkap',
    fields: [
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      { name: 'isiPernyataan', label: 'Isi Pernyataan', type: 'textarea', required: true, rows: 4, placeholder: 'Tuliskan isi pernyataan dengan sebenarnya...' },
    ],
  },
  {
    id: 'pbi_tidak_punya_rekening',
    title: 'SURAT PERNYATAAN TIDAK MEMILIKI REKENING',
    shortTitle: 'Tidak Memiliki Rekening',
    description: 'Surat pernyataan tidak memiliki rekening bank (PBI-JKN).',
    category: 'Bansos (PBI-JKN)',
    nomorPrefix: '474.4',
    nomorSuffix: 'KS',
    applicantField: 'namaLengkap',
    fields: [
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka' },
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
    ],
  },
];

export const LETTER_MAP: Record<LetterType, LetterDefinition> = Object.fromEntries(
  LETTER_DEFINITIONS.map((d) => [d.id, d]),
) as Record<LetterType, LetterDefinition>;

export const LETTER_CATEGORIES: LetterCategory[] = [
  'Individu',
  'Properti',
  'Administrasi',
  'Sosial',
  'Hukum',
  'Lainnya',
  'Surat Desa',
  'Formulir Dukcapil',
  'SPTJM',
  'Bansos (PBI-JKN)',
];

/** Data awal (kosong) untuk satu jenis surat, termasuk nomor + tanggal surat. */
export function defaultLetterData(type: LetterType): Record<string, any> {
  const def = LETTER_MAP[type];
  const data: Record<string, any> = {
    nomorSurat: defaultNomorSurat(def),
    tanggalSurat: todayIso(),
  };
  for (const f of def.fields) {
    if (f.type === 'table') {
      const n = f.minRows ?? 1;
      data[f.name] = Array.from({ length: n }, () =>
        Object.fromEntries((f.columns || []).map((c) => [c.name, ''])),
      );
    } else {
      data[f.name] = f.defaultValue ?? '';
    }
  }
  return data;
}

/** NIK valid = tepat 16 digit angka. Bukan blokir keras — hanya peringatan. */
export function isValidNik(value: string): boolean {
  return /^\d{16}$/.test((value || '').trim());
}
