import { LetterCategory, LetterType } from '../types';

/**
 * SATU sumber kebenaran untuk definisi 12 jenis surat:
 * judul, kategori, pola nomor, dan daftar field form.
 * Dipakai oleh store, form, selector, preview, dan PDF generator.
 */

export interface FormField {
  name: string;
  label: string;
  type: 'text' | 'textarea' | 'date' | 'number' | 'select';
  placeholder?: string;
  required?: boolean;
  rows?: number;
  options?: { value: string; label: string }[];
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

export const LETTER_DEFINITIONS: LetterDefinition[] = [
  {
    id: 'domisili',
    title: 'SURAT KETERANGAN DOMISILI',
    shortTitle: 'Keterangan Domisili',
    description: 'Menerangkan tempat tinggal / domisili penduduk desa.',
    category: 'Individu',
    nomorPrefix: '474',
    nomorSuffix: 'PEM',
    fields: [
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka' },
      JENIS_KELAMIN,
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
      { name: 'alamatAsal', label: 'Alamat Asal (sesuai KTP)', type: 'textarea', required: true, rows: 2 },
      { name: 'rt', label: 'RT', type: 'text', required: true, placeholder: 'Contoh: 001' },
      { name: 'rw', label: 'RW', type: 'text', required: true, placeholder: 'Contoh: 006' },
      { name: 'alamatDomisili', label: 'Alamat Domisili Saat Ini', type: 'textarea', required: true, rows: 2 },
      { name: 'keperluanSurat', label: 'Keperluan Surat', type: 'text', required: true },
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
    description: 'Menerangkan bahwa yang bersangkutan tergolong tidak mampu.',
    category: 'Sosial',
    nomorPrefix: '460',
    nomorSuffix: 'Ks',
    fields: [
      { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
      { name: 'nik', label: 'NIK', type: 'text', required: true, placeholder: '16 digit angka' },
      JENIS_KELAMIN,
      { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
      { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
      { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
      { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
      { name: 'rt', label: 'RT', type: 'text', placeholder: 'Contoh: 006' },
      { name: 'rw', label: 'RW', type: 'text', placeholder: 'Contoh: 002' },
      { name: 'penghasilanBulanan', label: 'Penghasilan Bulanan (Rp)', type: 'text' },
      { name: 'keperluan', label: 'Keperluan', type: 'text', required: true },
      { name: 'tujuan', label: 'Tujuan (instansi / program)', type: 'text', required: true },
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
];

/** Data awal (kosong) untuk satu jenis surat, termasuk nomor + tanggal surat. */
export function defaultLetterData(type: LetterType): Record<string, any> {
  const def = LETTER_MAP[type];
  const data: Record<string, any> = {
    nomorSurat: defaultNomorSurat(def),
    tanggalSurat: todayIso(),
  };
  for (const f of def.fields) data[f.name] = '';
  return data;
}

/** NIK valid = tepat 16 digit angka. Bukan blokir keras — hanya peringatan. */
export function isValidNik(value: string): boolean {
  return /^\d{16}$/.test((value || '').trim());
}
