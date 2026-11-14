import { format, parseISO } from 'date-fns';
import { id as idLocale } from 'date-fns/locale';
import { HeaderData, LetterType } from '../types';
import { LETTER_MAP } from './letter-definitions';

/**
 * SATU sumber kebenaran isi surat.
 * LetterBody (preview HTML) dan pdf-generator (jsPDF) sama-sama merender
 * struktur yang dikembalikan getLetterContent() — tidak ada dua template
 * yang bisa drift.
 *
 * Fase 2: 30 jenis (12 lama + 18 baru), varian domisili & SKTM,
 * kop Disdukcapil (teks saja) untuk formulir seri F, dan blok 'table'
 * untuk daftar anggota / daftar anak / daftar kolektif.
 *
 * Fase 3: kop DINAMIS per penandatangan — satu fungsi kopFor(signer)
 * dipakai preview (LetterBody) DAN PDF (pdf-generator), jadi keduanya
 * tidak bisa drift. Ahli waris ditulis ulang mengikuti contoh DOCX
 * (narasi pembuka, tabel ahli waris dinamis, saksi RT/RW, keperluan,
 * penutup khusus, Mengetahui Camat).
 */

export interface FieldRow {
  label: string;
  value: string;
}

export type ContentBlock =
  | { kind: 'fields'; rows: FieldRow[] }
  | { kind: 'paragraph'; text: string }
  | { kind: 'list'; heading?: string; items: string[] }
  | { kind: 'table'; headers: string[]; rows: string[][]; widths?: number[] };

export interface SignerBlock {
  /** Baris di atas jabatan, mis. "a.n. Kepala Desa Kasomalang Kulon". */
  prefix?: string;
  role: string;
  name: string;
  nip: string;
  /**
   * Asal penandatangan (diisi resolveSigner; signer literal formulir/
   * SPTJM/PBI tidak punya kind — kopFor menebaknya dari role/prefix).
   * 'kades' = ttd langsung Kades; 'sekdes-an' = a.n. Kades oleh Sekdes;
   * 'sekdes' = ttd langsung Sekdes; 'pemohon' = pemohon/yang menyatakan.
   */
  kind?: 'kades' | 'sekdes-an' | 'sekdes' | 'pemohon';
  /** Baris di bawah nama (Fase 5 §1C), mis. "NIK. ............" atau "Materai Rp10.000". */
  sub?: string;
}

/**
 * Varian kop (Fase 3) — ditentukan dari penandatangan, BUKAN dari tipe
 * surat, supaya satu aturan berlaku di preview, PDF, dan cetak:
 * - 'garuda-center': ttd langsung Kepala Desa → logo Garuda SAJA di
 *   tengah paling atas, teks kop rata tengah di bawahnya.
 * - 'subang-left': ttd a.n. Kades oleh Sekdes, atau ttd langsung Sekdes
 *   → logo Subang di kiri, tanpa Garuda.
 * - 'dua-logo': penandatangan utama warga/pemohon (SPTJM/PBI/dll.,
 *   Kades hanya "Mengetahui") → kop dua logo seperti sebelumnya.
 * - 'disdukcapil': formulir Dukcapil seri F → kop Disdukcapil teks saja.
 * - 'tabel': surat kematian (Fase 5) → TANPA kop logo; tabel kecil 3
 *   baris (Pemerintah/Kota/Kab, Kecamatan, Desa/Kelurahan) di kiri atas.
 */
export type KopVariant = 'garuda-center' | 'subang-left' | 'dua-logo' | 'disdukcapil' | 'tabel';

export function kopFor(content: Pick<LetterContent, 'kop' | 'signer'>): KopVariant {
  if (content.kop === 'disdukcapil') return 'disdukcapil';
  const s = content.signer;
  if (s.kind === 'sekdes-an' || s.kind === 'sekdes') return 'subang-left';
  if (s.kind === 'kades') return 'garuda-center';
  if (s.kind === 'pemohon') return 'dua-logo';
  // Fallback untuk signer literal tanpa kind (formulir/SPTJM/PBI).
  if (s.prefix && s.prefix.startsWith('a.n.')) return 'subang-left';
  if (s.role === 'Sekretaris Desa') return 'subang-left';
  if (s.role === 'Kepala Desa') return 'garuda-center';
  return 'dua-logo';
}

export interface LetterContent {
  title: string;
  nomor: string;
  /** Kop yang dipakai: 'desa' (logo Garuda + Subang) atau 'disdukcapil' (teks saja). */
  kop: 'desa' | 'disdukcapil';
  /** Varian kop dinamis per penandatangan (Fase 3) — lihat kopFor(). */
  kopVariant: KopVariant;
  /** Sembunyikan baris "Nomor :" (formulir yang memang tidak bernomor). */
  hideNomor?: boolean;
  /** Sembunyikan baris judul (undangan — template tidak punya judul). */
  hideTitle?: boolean;
  /** Baris di bawah judul, mis. "TAHUN 2025" (pbi_asesmen). */
  subtitle?: string;
  /** Teks nomor lengkap siap tampil (kematian: "No.: 460.3 / ... / KS / 2026"). */
  nomorDisplay?: string;
  /** Halaman landscape A4 (kematian, f101 — Fase 5 §1A). */
  landscape?: boolean;
  intro: string;
  blocks: ContentBlock[];
  closing: string;
  placeDate: string;
  signer: SignerBlock;
  /** Blok tanda tangan kiri (khusus SKTM: Mengetahui, Ketua RW). */
  leftSigner?: { prefix?: string; role: string; name?: string };
  /** Grid penandatangan (Fase 5 §1C) — bila ada, renderer mengabaikan
   *  signer/leftSigner tunggal dan merender grid ini. */
  signers?: SignerBlock[];
  /** Jumlah kolom grid penandatangan (default 2). */
  signerColumns?: number;
}

export function formatDateSurat(date?: string): string {
  const fallback = () => format(new Date(), 'dd MMMM yyyy', { locale: idLocale });
  if (!date) return fallback();
  try {
    return format(parseISO(date), 'dd MMMM yyyy', { locale: idLocale });
  } catch {
    return fallback();
  }
}

export function formatDateTTL(date?: string): string {
  if (!date) return '';
  try {
    return format(parseISO(date), 'dd-MM-yyyy', { locale: idLocale });
  } catch {
    return '';
  }
}

function ttl(tempat?: string, tanggal?: string): string {
  const t = formatDateTTL(tanggal);
  if (tempat && t) return `${tempat}, ${t}`;
  return tempat || t || '';
}

/** Baris field — dilewati bila nilai kosong dan opsional. */
function row(label: string, value: string | undefined, required = false): FieldRow | null {
  const v = (value || '').trim();
  if (!v && !required) return null;
  return { label, value: v };
}

function rows(...rs: (FieldRow | null)[]): FieldRow[] {
  return rs.filter((r): r is FieldRow => r !== null);
}

/** Nama penandatangan dari data isian; garis titik bila kosong. */
function nameOrDots(name?: string): string {
  const v = (name || '').trim();
  return v || '........................';
}

/**
 * Ubah data tabel dari form (array objek) menjadi baris string untuk blok
 * 'table'. Baris yang seluruh kolomnya kosong dilewati. Kolom tanggal
 * diformat dd-MM-yyyy.
 */
function tableFromData(
  data: Record<string, any>,
  fieldName: string,
  columns: { name: string; label: string; date?: boolean }[],
): string[][] {
  const arr = Array.isArray(data[fieldName]) ? data[fieldName] : [];
  const out: string[][] = [];
  for (const item of arr) {
    if (!item || typeof item !== 'object') continue;
    const vals = columns.map((c) => {
      const v = item[c.name];
      if (v === undefined || v === null) return '';
      if (c.date) return formatDateTTL(String(v));
      return String(v).trim();
    });
    if (vals.some((v) => v)) out.push(vals);
  }
  return out;
}

function tableBlock(
  heading: string | undefined,
  headers: string[],
  dataRows: string[][],
  widths?: number[],
): ContentBlock[] {
  const blocks: ContentBlock[] = [];
  if (heading) blocks.push({ kind: 'paragraph', text: heading });
  if (dataRows.length > 0) {
    blocks.push({ kind: 'table', headers, rows: dataRows, widths });
  } else {
    blocks.push({ kind: 'paragraph', text: '(belum ada baris terisi)' });
  }
  return blocks;
}

function resolveSigner(type: LetterType, header: HeaderData): SignerBlock {
  const desa = header.namaDesa || 'Kasomalang Kulon';
  // a.n. Kepala Desa oleh Sekretaris Desa: pisah KK.
  // (kematian TIDAK di sini — Fase 5: ttd "Sekretaris Desa Kasomalang
  // Kulon" TANPA a.n., ikut screenshot/template; lihat case 'kematian'.)
  if (type === 'pisah_kk' || header.penandatanganDefault === 'sekdes') {
    return {
      prefix: `a.n. Kepala Desa ${desa}`,
      role: 'Sekretaris Desa',
      name: header.namaSekdes || 'APIPUDIN, S.IP',
      nip: (header.nipSekdes || '').trim(),
      kind: 'sekdes-an',
    };
  }
  // Sekretaris Desa: usaha + duda/janda — juga a.n. Kepala Desa (user 2026-11-15:
  // blok Sekdes selalu "a.n. Kepala Desa Kasomalang Kulon / Sekretaris Desa").
  if (type === 'usaha' || type === 'duda_janda') {
    return {
      prefix: `a.n. Kepala Desa ${desa}`,
      role: 'Sekretaris Desa',
      name: header.namaSekdes || 'APIPUDIN, S.IP',
      nip: (header.nipSekdes || '').trim(),
      kind: 'sekdes-an',
    };
  }
  return {
    role: header.jabatanPejabat || 'Kepala Desa',
    name: header.namaPejabat || 'H. AMIRUDIN, S.Pd.I',
    nip: (header.nipPejabat || '').trim(),
    kind: 'kades',
  };
}

const CLOSING = 'Demikian surat keterangan ini dibuat dengan sebenarnya agar dapat dipergunakan sebagaimana mestinya.';
const CLOSING_AHLI_WARIS =
  'Demikian surat ini kami buat dengan sebenarnya dan kepada yang bersangkutan menjadi tahu adanya.';
const CLOSING_FORM = 'Demikian formulir ini diisi dengan sebenarnya dan dapat dipertanggungjawabkan.';
const CLOSING_SPTJM =
  'Demikian surat pernyataan ini dibuat dengan sebenarnya dan apabila di kemudian hari ternyata tidak benar, saya bersedia dituntut sesuai dengan ketentuan peraturan perundang-undangan yang berlaku.';

// ---- Konstanta penutup template (Fase 5 §3) ----
const CLOSING_PISAH_KK = 'Demikian surat keterangan ini kami buat dengan sebenarnya agar yang berkepentingan menjadi tahu serta untuk dijadikan bahan periksa selanjutnya.';
const CLOSING_SKT = 'Demikian surat keterangan ini dibuat dengan sebenarnya dan apabila di kemudian hari ternyata keterangan ini tidak benar, maka segala akibat yang timbul menjadi tanggungjawab yang bersangkutan.';
const CLOSING_BEDA_LUAS = 'Demikian surat keterangan ini kami buat dengan sebenarnya agar yang berkepentingan menjadi tahu serta untuk dipergunakan sebagaimana mestinya.';
const CLOSING_BEDA_NAMA = CLOSING_PISAH_KK + ' dan digunakan sebagaimana mestinya.';
const CLOSING_MISKIN_SEKOLAH = 'Demikian surat keterangan ini kami buat, agar yang berkepentingan menjadi tahu serta untuk dipergunakan sebagaimana mestinya.';
const CLOSING_MISKIN_KOLEKTIF = 'Demikian surat ini dibuat dengan sebenarnya agar dapat digunakan sebagaimana mestinya.';
const CLOSING_NIKAH = 'Demikian surat keterangan ini dibuat dengan sebenarnya dan agar bisa dipergunakan sebagaimana mestinya.';
const CLOSING_PEMAKAMAN = 'Demikian surat keterangan ini kami buat dengan sebenarnya, agar yang berkepentingan menjadi tahu serta untuk dipergunakan sebagaimana mestinya.';
const CLOSING_PERMOHONAN_RUMAH = 'Demikian surat keterangan ini kami berikan, agar dipergunakan sebagaimana mestinya.';
const CLOSING_USAHA = 'Demikian surat keterangan ini kami buat, agar yang berkepentingan menjadi tahu serta untuk dipergunakan sebagaimana mestinya.';
const CLOSING_F101 = 'Demikian formulir ini saya/kami isi dengan sesungguhnya, apabila keterangan tersebut tidak sesuai dengan keadaan sebenarnya, saya bersedia dikenakan sanksi sesuai ketentuan peraturan perundang-undangan yang berlaku.';
const CLOSING_F105 = 'Demikian Surat Pernyataan ini kami buat dengan sebenarnya, apabila dalam keterangan yang saya berikan terdapat hal-hal yang tidak berdasarkan keadaan yang sebenarnya, kami bersedia dikenakan sanksi sesuai dengan ketentuan peraturan perundang-undangan yang berlaku.';
const CLOSING_F106 = 'Demikian Surat Pernyataan ini saya buat dengan sebenarnya, apabila dalam keterangan yang saya berikan terdapat hal-hal yang tidak berdasarkan keadaan yang sebenarnya, saya bersedia dikenakan sanksi sesuai ketentuan peraturan perundang-undangan yang berlaku.';
const CLOSING_F203 = 'Demikian Surat Pernyataan ini saya buat dengan sebenar-benarnya dan apabila di kemudian hari pernyataan saya ini tidak benar, maka saya bersedia diproses secara hukum sesuai dengan peraturan perundang-undangan dan dokumen yang diterbitkan akibat pernyataan ini menjadi tidak sah.';
const CLOSING_F204 = 'Demikian surat pernyataan ini saya buat dengan sebenar-benarnya, dan apabila di kemudian hari ternyata pernyataan saya ini tidak benar, maka saya bersedia diproses secara hukum sesuai dengan peraturan perundang-undangan dan dokumen yang diterbitkan akibat dari pernyataan ini menjadi tidak sah.';
const CLOSING_SPTJM_PERKAWINAN = 'Demikian surat pernyataan ini saya buat dengan sebenar-benarnya, apabila dalam keterangan yang saya berikan terdapat hal-hal yang tidak berdasarkan keadaan yang sebenarnya, saya bersedia dikenakan sanksi sesuai dengan ketentuan peraturan perundang-undangan yang berlaku.';
const CLOSING_PBI_PERNYATAAN = 'Demikian Pernyataan ini saya buat tanpa tekanan dari siapapun juga.';
const CLOSING_SKTM_PBI = 'Demikian Surat Keterangan ini kami buat agar yang berkepentingan menjadi tahu serta maklum.';
const CLOSING_UNDANGAN = 'Demikian kami sampaikan, atas perkenan dan perhatiannya diucapkan terimakasih.';

/** Penandatangan a.n. Kepala Desa oleh Sekretaris Desa (aturan Fase 4 / P3). */
function sekdesAnSigner(header: HeaderData, desa: string): SignerBlock {
  return {
    prefix: `a.n. Kepala Desa ${desa}`,
    role: 'Sekretaris Desa',
    name: header.namaSekdes || 'APIPUDIN, S.IP',
    nip: (header.nipSekdes || '').trim(),
    kind: 'sekdes-an',
  };
}

/** Baris sub NIK di bawah nama penandatangan (grid §1C). */
function nikSub(nik?: string): string {
  return `NIK. ${(nik || '').trim() || '........................'}`;
}

/** Tahun dari tanggal surat (ISO) — untuk nomor kematian & subtitle pbi_asesmen. */
function yearOf(dateStr?: string): number {
  if (dateStr) {
    try {
      return parseISO(dateStr).getFullYear();
    } catch {
      // jatuh ke tahun berjalan
    }
  }
  return new Date().getFullYear();
}

export function getLetterContent(
  type: LetterType,
  header: HeaderData,
  data: Record<string, any>,
): LetterContent {
  const def = LETTER_MAP[type];
  const desa = header.namaDesa || 'Kasomalang Kulon';
  const kecamatan = header.namaKecamatan || 'Kasomalang';
  const kabupaten = header.namaKabupaten || 'Subang';
  const nomor = (data.nomorSurat || '').trim();
  const kop = def.kop ?? 'desa';
  const place = kop === 'disdukcapil' ? 'Subang' : desa;
  const placeDate = `${place}, ${formatDateSurat(data.tanggalSurat)}`;
  const signer = resolveSigner(type, header);
  const introKades =
    `Yang bertanda tangan di bawah ini Kepala Desa ${desa}, Kecamatan ${kecamatan}, ` +
    `Kabupaten ${kabupaten}, menerangkan dengan sesungguhnya bahwa:`;

  const base = {
    title: def.title,
    nomor,
    kop,
    hideNomor: def.hideNomor,
    placeDate,
    signer,
  };

  // kopVariant dihitung dari signer AKHIR — kasus di bawah bisa
  // meng-override signer dari base (mis. SPTJM/PBI → Yang Menyatakan),
  // jadi varian kop TIDAK boleh dihitung dari base saja. Kasus boleh
  // juga memaksa kopVariant (kematian → 'tabel'; sktm_pbi /
  // pbi_rekening_listrik → 'dua-logo').
  const out: Omit<LetterContent, 'kopVariant'> & { kopVariant?: KopVariant } = (() => {
    switch (type) {
    case 'domisili': {
      const jenis = data.jenisDomisili || 'Perorangan';
      if (jenis === 'Lembaga') {
        return {
          ...base,
          title: 'SURAT KETERANGAN DOMISILI LEMBAGA',
          intro: introKades,
          blocks: [
            {
              kind: 'fields',
              rows: rows(
                row('Nama Lembaga', data.namaLembaga, true),
                row('Nama Pimpinan', data.namaPimpinan, true),
                row('Alamat Lembaga', data.alamatLembaga, true),
              ),
            },
            {
              kind: 'paragraph',
              text:
                `Lembaga tersebut di atas benar berdomisili di ${data.alamatLembaga || ''} ` +
                `dan dipimpin oleh ${data.namaPimpinan || ''}.`,
            },
            {
              kind: 'paragraph',
              text: `Surat keterangan ini kami berikan untuk keperluan ${data.keperluan || ''}.`,
            },
          ],
          closing: CLOSING,
        };
      }
      if (jenis === 'Perusahaan') {
        return {
          ...base,
          title: 'SURAT KETERANGAN DOMISILI PERUSAHAAN',
          intro: introKades,
          blocks: [
            {
              kind: 'fields',
              rows: rows(
                row('Nama Perusahaan', data.namaPerusahaan, true),
                row('Nama Pimpinan', data.namaPimpinan, true),
                row('Nama Sekretaris', data.namaSekretaris),
                row('Bergerak di Bidang', data.bidangUsaha, true),
                row('Alamat Perusahaan', data.alamatPerusahaan, true),
              ),
            },
            {
              kind: 'paragraph',
              text:
                `Perusahaan tersebut di atas benar berdomisili di ${data.alamatPerusahaan || ''} ` +
                `dan bergerak di bidang ${data.bidangUsaha || ''}.`,
            },
            {
              kind: 'paragraph',
              text: `Surat keterangan ini kami berikan untuk keperluan ${data.keperluan || ''}.`,
            },
          ],
          closing: CLOSING,
        };
      }
      const rtRw = [data.rt, data.rw].filter(Boolean).join('/');
      return {
        ...base,
        intro: introKades,
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('NIK', data.nik, true),
              row('Nama', data.namaLengkap, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Jenis Kelamin', data.jenisKelamin),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat (KTP)', data.alamatAsal, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              'Orang tersebut sudah tidak tinggal di alamat sebagaimana tercantum di atas, dan ' +
              `berdasarkan keterangan dari Ketua RT/RW, orang tersebut saat ini benar berdomisili di ` +
              `${rtRw ? `RT ${data.rt} RW ${data.rw}, ` : ''}${data.alamatDomisili || ''}.`,
          },
          {
            kind: 'paragraph',
            text: `Surat keterangan ini kami berikan untuk keperluan ${data.keperluanSurat || ''}.`,
          },
        ],
        closing: CLOSING,
      };
    }

    case 'pisah_kk': {
      return {
        ...base,
        intro: introKades,
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('NIK (1)', data.nik1, true),
              row('Nama (1)', data.nama1, true),
              row('NIK (2)', data.nik2, true),
              row('Nama (2)', data.nama2, true),
              row('Nomor KK Lama', data.nomorKKLama, true),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              'Nama-nama tersebut di atas adalah benar warga Desa kami dan telah Pisah KK dari KK Nomor ' +
              `${data.nomorKKLama || ''} untuk membentuk Kartu Keluarga baru dengan alamat ${data.alamat || ''}.`,
          },
        ],
        closing: CLOSING_PISAH_KK,
      };
    }

    case 'skt_dayat': {
      return {
        ...base,
        intro: introKades,
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaLengkap, true),
              row('NIK', data.nik, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Alamat', data.alamat, true),
              row('Luas Tanah', data.luasTanah ? `${data.luasTanah} m²` : '', true),
              row('Luas Bangunan', data.luasBangunan ? `${data.luasBangunan} m²` : ''),
              row('SPPT / NOP', data.spptNop),
            ),
          },
          {
            kind: 'fields',
            rows: rows(
              row('Sebelah Utara', data.batasUtara),
              row('Sebelah Timur', data.batasTimur),
              row('Sebelah Selatan', data.batasSelatan),
              row('Sebelah Barat', data.batasBarat),
            ),
          },
          {
            kind: 'fields',
            rows: rows(
              row('Harga Taksiran Bangunan', data.hargaTaksiranBangunan ? `Rp ${data.hargaTaksiranBangunan}` : ''),
              row('Harga Taksiran Tanah', data.hargaTaksiranTanah ? `Rp ${data.hargaTaksiranTanah}` : ''),
              row('Jumlah Harga Taksiran', data.hargaTaksiranJumlah ? `Rp ${data.hargaTaksiranJumlah}` : ''),
              row('No. Register', data.noRegister),
              row('Tanggal Register', formatDateTTL(data.tanggalRegister)),
            ),
          },
          {
            kind: 'paragraph',
            text:
              'Tanah tersebut di atas adalah benar milik / dikuasai oleh yang bersangkutan dengan ' +
              'batas-batas sebagaimana tersebut di atas, dan sampai saat ini tidak dalam sengketa ' +
              'dengan pihak mana pun.',
          },
        ],
        closing: CLOSING_SKT,
        leftSigner: { prefix: 'Mengetahui,', role: 'Camat Kasomalang' },
      };
    }

    case 'beda_luas': {
      return {
        ...base,
        intro: introKades,
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaLengkap, true),
              row('NIK', data.nik, true),
              row('Alamat', data.alamat, true),
              row('RT / RW', [data.rt, data.rw].filter(Boolean).join(' / ')),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Luas yang tertera di Sertifikat:',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Nomor Sertifikat', data.nomorSertifikat, true),
              row('Nomor NIB', data.nomorNIB),
              row('Luas Sertifikat', data.luasSertifikat ? `${data.luasSertifikat} m²` : '', true),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Dan luas yang tertera di SPPT:',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Nomor SPPT', data.nomorSPPT, true),
              row('Luas SPPT', data.luasSPPT ? `${data.luasSPPT} m²` : '', true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              'Orang tersebut adalah benar warga Desa kami, dan terdapat perbedaan luas antara ' +
              'Sertifikat dan SPPT, adapun pemilik tanah tersebut orangnya sama.',
          },
        ],
        closing: CLOSING_BEDA_LUAS,
        signer: sekdesAnSigner(header, desa),
      };
    }

    case 'beda_nama': {
      const dok1 = (data.dokumenPertama || 'IJAZAH').trim() || 'IJAZAH';
      const dok2 = (data.dokumenKedua || 'BUKU NIKAH & KK').trim() || 'BUKU NIKAH & KK';
      return {
        ...base,
        intro: introKades,
        blocks: [
          {
            kind: 'paragraph',
            text: `Keterangan data dari ${dok1}:`,
          },
          {
            kind: 'fields',
            rows: rows(
              row('NIK', data.nikDokumenPertama, true),
              row('Nama', data.namaDokumenPertama, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahirDokumenPertama, data.tanggalLahirDokumenPertama)),
              row('Alamat', data.alamatDokumenPertama),
            ),
          },
          {
            kind: 'paragraph',
            text: `Keterangan data dari ${dok2}:`,
          },
          {
            kind: 'fields',
            rows: rows(
              row('NIK', data.nikDokumenKedua, true),
              row('Nama', data.namaDokumenKedua, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahirDokumenKedua, data.tanggalLahirDokumenKedua)),
              row('Alamat', data.alamatDokumenKedua),
            ),
          },
          {
            kind: 'paragraph',
            text:
              `Terdapat perbedaan Nama/Tanggal/Tahun kelahiran yang tertulis di ${dok1} adalah ` +
              `${data.namaDokumenPertama || ''} dengan yang tertulis di ${dok2} adalah ` +
              `${data.namaDokumenKedua || ''} dan orangnya sama itu-itu juga.`,
          },
        ],
        closing: CLOSING_BEDA_NAMA,
        signer: sekdesAnSigner(header, desa),
      };
    }

    case 'belum_menikah': {
      return {
        ...base,
        intro: introKades,
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaLengkap, true),
              row('NIK', data.nik, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Jenis Kelamin', data.jenisKelamin),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
              row('RT / RW', [data.rt, data.rw].filter(Boolean).join(' / ')),
            ),
          },
          {
            kind: 'paragraph',
            text:
              'Adalah benar bahwa yang bersangkutan sampai saat ini belum pernah melakukan ' +
              `perkawinan yang tercatat pada kantor pencatat perkawinan yang berwenang. Surat ` +
              `keterangan ini diberikan untuk keperluan ${data.keperluan || ''}.`,
          },
        ],
        closing: CLOSING,
        signer: sekdesAnSigner(header, desa),
      };
    }

    case 'tidak_mampu': {
      const varian = data.varianSKTM || 'Umum';
      if (varian === 'Kolektif') {
        const daftar = tableFromData(data, 'daftarKolektif', [
          { name: 'nama', label: 'Nama' },
          { name: 'nik', label: 'NIK' },
          { name: 'alamat', label: 'Alamat' },
        ]);
        const keperluanKolektif = (data.keperluan || 'pengajuan bantuan sosial Program Jabar Caang').trim();
        return {
          ...base,
          title: 'SURAT KETERANGAN TIDAK MAMPU (DAFTAR KOLEKTIF)',
          intro: introKades,
          blocks: [
            {
              kind: 'paragraph',
              text:
                'Orang-orang yang tercantum dalam daftar terlampir adalah benar-benar warga Desa kami ' +
                'dan tergolong dalam kategori Keluarga Tidak Mampu. Daftar nama terlampir adalah warga ' +
                `RW 02 Desa ${desa}:`,
            },
            ...tableBlock(undefined, ['No.', 'Nama', 'NIK', 'Alamat'], daftar.map((r, i) => [String(i + 1), ...r]), [8, 30, 28, 34]),
            {
              kind: 'paragraph',
              text:
                'Surat keterangan ini dibuat sebagai salah satu syarat untuk keperluan ' +
                `${keperluanKolektif}.`,
            },
          ],
          closing: CLOSING_MISKIN_KOLEKTIF,
          signer: sekdesAnSigner(header, desa),
          leftSigner: { role: 'Ketua RW' },
        };
      }
      const keperluan = (data.keperluan || '').trim();
      const tujuan = (data.tujuan || '').trim();
      const tujuanText = tujuan ? ` di ${tujuan}` : '';
      if (varian === 'Sekolah') {
        const tujuanSekolah = tujuan || 'SMPN Negeri Kasomalang';
        return {
          ...base,
          title: 'SURAT KETERANGAN TIDAK MAMPU (SEKOLAH)',
          intro: introKades,
          blocks: [
            {
              kind: 'fields',
              rows: rows(
                row('Nama', data.namaLengkap, true),
                row('NIK', data.nik, true),
                row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
                row('Jenis Kelamin', data.jenisKelamin),
                row('Pekerjaan', data.pekerjaan),
                row('Alamat', data.alamat, true),
                row('RT / RW', [data.rt, data.rw].filter(Boolean).join(' / ')),
                row('Penghasilan Bulanan', data.penghasilanBulanan ? `Rp ${data.penghasilanBulanan}` : ''),
              ),
            },
            {
              kind: 'paragraph',
              text:
                `Orang tersebut adalah warga Desa ${desa} dan saat ini akan mendaftar ke ${tujuanSekolah}, ` +
                'namun menurut keterangan Ketua RT/RW setempat dan sepengetahuan kami keadaan ekonominya ' +
                'saat ini termasuk kategori keluarga kurang mampu (Miskin). Untuk itu kami mohon kepada pihak ' +
                `${tujuanSekolah} agar dapat memberikan keringanan/kebebasan biaya pendidikannya.`,
            },
          ],
          closing: CLOSING_MISKIN_SEKOLAH,
          signer: sekdesAnSigner(header, desa),
        };
      }
      return {
        ...base,
        title: 'SURAT KETERANGAN TIDAK MAMPU',
        intro: introKades,
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaLengkap, true),
              row('NIK', data.nik, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Jenis Kelamin', data.jenisKelamin),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
              row('RT / RW', [data.rt, data.rw].filter(Boolean).join(' / ')),
              row('Penghasilan Bulanan', data.penghasilanBulanan ? `Rp ${data.penghasilanBulanan}` : ''),
            ),
          },
          {
            kind: 'paragraph',
            text:
              'Adalah benar bahwa yang bersangkutan termasuk dalam kategori tidak mampu menurut ' +
              `kriteria yang berlaku di desa kami. Surat keterangan ini dibuat sebagai salah satu ` +
              `syarat untuk keperluan ${keperluan}${tujuanText}.`,
          },
        ],
        closing: CLOSING,
        signer: sekdesAnSigner(header, desa),
        leftSigner: { prefix: 'Mengetahui,', role: 'Ketua RW' },
      };
    }

    case 'ahli_waris': {
      // Ditulis ulang Fase 3 mengikuti contoh
      // "SURAT KETERANGAN AHLI WARIS 2021.docx" (bundle 9 contoh surat):
      // pembuka narasi (nama almarhum, hari/tanggal/tempat meninggal,
      // nama pasangan + jumlah anak, opsional nomor surat kematian),
      // tabel ahli waris dinamis (No, Nama, NIK, Tempat/Tgl Lahir,
      // Hubungan Keluarga, Tanda Tangan), saksi Ketua RT/RW, keperluan,
      // penutup khusus, Mengetahui Camat (kiri) + Kades (kanan).
      // Kolom NIK tidak ada di tabel utama contoh, tapi ada di daftar
      // ahli waris pada dokumen pendamping (Surat Kuasa) untuk orang
      // yang sama — dimasukkan sebagai kolom agar datanya lengkap.
      // NIK/alamat peninggal TIDAK ada di contoh → dihapus dari form.
      const sebutan = data.sebutanPeninggal === 'Almarhumah' ? 'almarhumah' : 'almarhum';
      const pasanganLabel = sebutan === 'almarhumah' ? 'Suami' : 'Istri';
      const namaPeninggal = (data.namaPeninggal || '').trim();
      const meninggalParts: string[] = [];
      if ((data.hariMeninggal || '').trim()) meninggalParts.push(`hari ${data.hariMeninggal.trim()}`);
      const tglMeninggal = formatDateTTL(data.tanggalMeninggal);
      if (tglMeninggal) meninggalParts.push(`tanggal ${tglMeninggal}`);
      if ((data.tempatMeninggal || '').trim()) meninggalParts.push(`di ${data.tempatMeninggal.trim()}`);
      let pembuka =
        `Yang bertanda tangan di bawah ini Kepala Desa ${desa}, dengan ini menerangkan bahwa ` +
        `${sebutan} ${namaPeninggal} yang telah meninggal dunia` +
        (meninggalParts.length ? ` pada ${meninggalParts.join(', ')}` : '') +
        (data.nomorSuratKematian ? ` sesuai Surat Kematian Nomor: ${data.nomorSuratKematian}` : '') +
        '.';
      if ((data.namaPasangan || '').trim()) {
        const jmlAnak = parseInt(String(data.jumlahAnak ?? ''), 10);
        pembuka +=
          ` Semasa hidupnya ${sebutan} telah menikah sah dengan Satu (1) orang ${pasanganLabel} ` +
          `bernama ${data.namaPasangan.trim()} yang masih hidup sampai sekarang.` +
          (Number.isFinite(jmlAnak) && jmlAnak > 0
            ? ` Dari hasil Perkawinan tersebut telah dilahirkan ${jmlAnak} orang anak.`
            : '');
      }

      const daftar = tableFromData(data, 'daftarAhliWaris', [
        { name: 'nama', label: 'Nama' },
        { name: 'nik', label: 'NIK' },
        { name: 'ttl', label: 'Tempat, Tgl Lahir' },
        { name: 'hubungan', label: 'Hubungan Keluarga' },
      ]).map((r) => [...r, '']); // kolom Tanda Tangan (kosong — ditandatangani)

      const saksiRows: string[][] = [];
      if ((data.namaSaksi1 || '').trim()) {
        saksiRows.push(['1', data.namaSaksi1.trim(), (data.jabatanSaksi1 || '').trim(), '']);
      }
      if ((data.namaSaksi2 || '').trim()) {
        saksiRows.push(['2', data.namaSaksi2.trim(), (data.jabatanSaksi2 || '').trim(), '']);
      }

      return {
        ...base,
        intro: pembuka,
        blocks: [
          {
            kind: 'paragraph',
            text: 'Dengan demikian susunan Ahli Waris adalah sebagai berikut:',
          },
          ...tableBlock(
            undefined,
            ['No.', 'Nama', 'NIK', 'Tempat, Tgl Lahir', 'Hubungan Keluarga', 'Tanda Tangan'],
            daftar.map((r, i) => [String(i + 1), ...r]),
            [7, 27, 20, 20, 16, 10],
          ),
          {
            kind: 'paragraph',
            text: `Nama-nama tersebut di atas merupakan ahli waris yang sah dari ${sebutan} ${namaPeninggal}.`,
          },
          {
            kind: 'paragraph',
            text: 'Tidak ada ahli waris selain yang tercantum di dalam surat keterangan ini, sesuai pengakuannya.',
          },
          ...(saksiRows.length
            ? tableBlock(
                'Saksi-saksi yang mengetahui keterangan tersebut adalah',
                ['No.', 'Nama', 'Jabatan', 'Tanda Tangan'],
                saksiRows,
                [8, 42, 30, 20],
              )
            : []),
          ...(data.keperluan
            ? [{ kind: 'paragraph' as const, text: `Untuk Keperluan : ${data.keperluan}` }]
            : []),
          ...(data.keterangan
            ? [{ kind: 'paragraph' as const, text: `Keterangan: ${data.keterangan}` }]
            : []),
        ],
        closing: CLOSING_AHLI_WARIS,
        leftSigner: { prefix: 'Mengetahui,', role: 'Camat Kasomalang' },
      };
    }

    case 'nikah_tidak_tercatat': {
      const wali = (data.namaWali || '').trim();
      const s1 = (data.saksi1 || '').trim();
      const s2 = (data.saksi2 || '').trim();
      const masKawin = (data.masKawin || 'Alat sholat').trim() || 'Alat sholat';
      let narasi =
        'Menurut sepengetahuan kami, orang tersebut di atas telah menikah pada tanggal ' +
        `${formatDateTTL(data.tanggalPernikahan)}`;
      if (wali) narasi += ` yang dilaksanakan oleh walinya Bapak ${wali}`;
      if (s1 || s2) {
        narasi += ` dengan saksi 2 orang, 1. Bapak ${s1} 2. Bapak ${s2}`;
      }
      narasi += ` dengan mas kawin ${masKawin}.`;
      return {
        ...base,
        intro:
          `Yang bertanda tangan di bawah ini, Kepala Desa ${desa}, Kecamatan ${kecamatan}, ` +
          `Kabupaten ${kabupaten}, menerangkan perihal perkawinan antara:`,
        blocks: [
          {
            kind: 'paragraph',
            text: 'Pihak Pertama (Suami):',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaPria, true),
              row('Bin', data.binSuami),
              row('NIK', data.nikPria, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahirPria, data.tanggalLahirPria)),
              row('Agama', data.agamaSuami),
              row('Status Perkawinan', data.statusPerkawinan),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Dengan seorang Wanita (Istri):',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaWanita, true),
              row('Bin', data.binIstri),
              row('NIK', data.nikWanita, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahirWanita, data.tanggalLahirWanita)),
              row('Agama', data.agamaIstri),
            ),
          },
          {
            kind: 'paragraph',
            text: narasi,
          },
        ],
        closing: CLOSING_NIKAH,
        signer: sekdesAnSigner(header, desa),
      };
    }

    case 'pemakaman': {
      return {
        ...base,
        intro: 'Yang bertanda tangan di bawah ini,',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Kepala Desa', desa, true),
              row('Kecamatan', kecamatan, true),
              row('Kabupaten', kabupaten, true),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Dengan ini menerangkan bahwa :',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Nama Almarhum/Almarhumah', data.namaAlmarhum, true),
              row('NIK', data.nikAlmarhum),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Telah meninggal dunia pada :',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Hari', data.hariMeninggal, true),
              row('Tanggal', formatDateTTL(data.tanggalMeninggal), true),
              row('Jam', data.jamMeninggal),
              row('Tempat', data.tempatMeninggal, true),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Dan telah disemayamkan pada :',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Hari', data.hariDisemayamkan, true),
              row('Tanggal', formatDateTTL(data.tanggalDisemayamkan), true),
              row('Tempat', data.tempatDisemayamkan, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              'Almarhum/almarhumah tersebut di atas telah meninggal dunia dan dimakamkan di ' +
              `${data.lokasiMakam || ''} atas permohonan keluarga, yaitu ${data.namaKeluarga || ''}.`,
          },
        ],
        closing: CLOSING_PEMAKAMAN,
        signer: sekdesAnSigner(header, desa),
      };
    }

    case 'tidak_memiliki_rumah': {
      return {
        ...base,
        intro: 'Yang bertanda tangan di bawah ini:',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaLengkap, true),
              row('NIK', data.nik, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat Tempat Tinggal', data.alamatTinggal, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              'Menyatakan dengan sebenarnya bahwa orang tersebut sampai saat ini benar-benar belum ' +
              'memiliki rumah tinggal sendiri dan masih menumpang / tinggal bersama keluarga di alamat ' +
              'tersebut di atas.',
          },
          ...(data.keterangan
            ? [{ kind: 'paragraph' as const, text: `Keterangan: ${data.keterangan}` }]
            : []),
        ],
        closing: CLOSING_PERMOHONAN_RUMAH,
        signer: sekdesAnSigner(header, desa),
      };
    }

    case 'undangan': {
      const hariTanggal = [data.hari, [data.tanggal, data.bulan, data.tahun].filter(Boolean).join(' ')]
        .filter(Boolean)
        .join(', ');
      const tembusan: string[] = (data.daftarTembusan || '')
        .split(/[\n,;]+/)
        .map((s: string) => s.trim())
        .filter(Boolean);
      return {
        ...base,
        intro: '',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Sifat', 'Penting', true),
              row('Lampiran', '-', true),
              row('Perihal', 'Undangan', true),
            ),
          },
          {
            kind: 'paragraph',
            text: `Kepada Yth.\n${data.tujuanYth || ''}\ndi tempat`,
          },
          {
            kind: 'paragraph',
            text:
              'Dengan hormat, kami mengundang Bapak/Ibu/Saudara/i untuk hadir dalam acara berikut:',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Hari / Tanggal', hariTanggal, true),
              row('Pukul', data.pukul ? `${data.pukul} WIB` : '', true),
              row('Tempat', data.tempat, true),
              row('Agenda', data.agendaAcara, true),
            ),
          },
          ...(tembusan.length
            ? [{ kind: 'list' as const, heading: 'Tembusan:', items: tembusan }]
            : []),
        ],
        closing: CLOSING_UNDANGAN,
        // Template tidak punya judul — judul disembunyikan (hideTitle).
        hideTitle: true,
      };
    }

    // ================= FASE 2 — SURAT DESA BARU =================

    case 'kematian': {
      // Fase 5 (brief FASE5_BRIEF.md): format khusus template Dukcapil —
      // TANPA kop (kopVariant 'tabel': tabel kecil 3 baris di kiri atas),
      // landscape A4, isi di area kiri ~62%, seksi meninggal + pelapor
      // sebagai field (bukan prosa), TANPA paragraf penutup, ttd
      // "Sekretaris Desa Kasomalang Kulon" TANPA a.n. (keputusan ikut
      // screenshot/xlsm; konflik: SURAT KEMATIAN.doc justru menulis a.n.).
      // Tanggal format DD-MM-YYYY khusus form ini; nomor
      // "No.: 460.3 / <reg|............> / KS / <tahun>".
      const nomorReg = (data.nomorSurat || '').trim();
      return {
        ...base,
        kopVariant: 'tabel',
        landscape: true,
        // placeDate khusus: "Kasomalang Kulon, DD-MM-YYYY".
        placeDate: `${desa}, ${formatDateTTL(data.tanggalSurat)}`,
        nomorDisplay: `No.: 460.3 / ${nomorReg || '............'} / KS / ${yearOf(data.tanggalSurat)}`,
        intro: 'Yang bertanda tangan di bawah ini menerangkan',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaAlmarhum, true),
              row('NIK', data.nikAlmarhum, true),
              row('Jenis Kelamin', data.jenisKelamin),
              row('Agama', data.agama),
              row('Tempat/Tanggal Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Telah meninggal dunia pada :',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Hari', data.hariMeninggal, true),
              row('Tanggal', formatDateTTL(data.tanggalMeninggal), true),
              row('Bertempat di', data.tempatMeninggal, true),
              row('Penyebab Kematian', data.penyebabMeninggal, true),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Surat Keterangan ini dibuat berdasarkan keterangan Pelapor :',
          },
          {
            kind: 'fields',
            rows: rows(
              row('NIK', data.nikPelapor, true),
              row('Nama', data.namaPelapor, true),
              row('Tempat/Tanggal Lahir', ttl(data.tempatLahirPelapor, data.tanggalLahirPelapor)),
              row('Pekerjaan', data.pekerjaanPelapor),
              row('Alamat', data.alamatPelapor, true),
            ),
          },
          {
            kind: 'paragraph',
            text: `Hubungan pelapor dengan yang meninggal adalah : ${data.hubunganPelapor || ''}`,
          },
        ],
        // Template tidak punya penutup — closing KOSONG (renderer melewatinya).
        closing: '',
        signer: {
          role: 'Sekretaris Desa Kasomalang Kulon',
          name: header.namaSekdes || 'APIPUDIN, S.IP',
          nip: '',
          kind: 'sekdes',
        },
      };
    }

    case 'usaha': {
      return {
        ...base,
        intro: introKades,
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaLengkap, true),
              row('NIK', data.nik, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              `Orang tersebut adalah warga Desa ${desa}, dan menurut pengakuannya, saat ini mempunyai ` +
              'usaha sebagai berikut:',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Usaha', data.namaUsaha, true),
              row('Alamat Usaha', data.alamatUsaha, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              `Adalah benar bahwa yang bersangkutan memiliki usaha ${data.namaUsaha || ''} ` +
              `yang bertempat di ${data.alamatUsaha || ''}. Surat keterangan ini diberikan untuk ` +
              'keperluan administrasi usaha.',
          },
        ],
        closing: CLOSING_USAHA,
      };
    }

    case 'duda_janda': {
      const status = data.statusDudaJanda || 'Duda';
      const rt = (data.rt || '').trim();
      const rw = (data.rw || '').trim();
      const narasi =
        rt || rw
          ? `Berdasarkan keterangan dari Ketua RT ${rt} RW ${rw}, orang tersebut saat ini benar warga Desa ${desa} ` +
            `dan sampai saat ini masih berstatus ${status.toLowerCase()} dan belum menikah lagi.`
          : `Adalah benar bahwa yang bersangkutan berstatus ${status.toLowerCase()} ` +
            'dan sampai saat ini belum menikah lagi.';
      return {
        ...base,
        title: `SURAT KETERANGAN ${status.toUpperCase()}`,
        intro: introKades,
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaLengkap, true),
              row('NIK', data.nik, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'paragraph',
            text: narasi,
          },
          ...(data.keteranganRT
            ? [{ kind: 'paragraph' as const, text: `Keterangan Ketua RT: ${data.keteranganRT}` }]
            : []),
        ],
        closing: CLOSING,
      };
    }

    // ================= FASE 2 — FORMULIR DUKCAPIL SERI F =================

    case 'f101': {
      const anggota = tableFromData(data, 'daftarAnggota', [
        { name: 'nama', label: 'Nama' },
        { name: 'nik', label: 'NIK' },
        { name: 'shdk', label: 'SHDK' },
        { name: 'tempatLahir', label: 'Tempat Lahir' },
        { name: 'tanggalLahir', label: 'Tgl Lahir', date: true },
        { name: 'agama', label: 'Agama' },
        { name: 'pendidikan', label: 'Pendidikan' },
        { name: 'pekerjaan', label: 'Pekerjaan' },
        { name: 'statusKawin', label: 'Status Kawin' },
        { name: 'namaAyah', label: 'Nama Ayah' },
        { name: 'namaIbu', label: 'Nama Ibu' },
      ]);
      return {
        ...base,
        intro: 'Dengan ini kami sampaikan biodata keluarga sebagai berikut:',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama Kepala Keluarga', data.namaKepalaKeluarga, true),
              row('NIK Kepala Keluarga', data.nikKepalaKeluarga, true),
              row('Nomor Kartu Keluarga', data.noKK, true),
              row('Alamat Keluarga', data.alamatKepalaKeluarga, true),
            ),
          },
          ...tableBlock(
            'Daftar Anggota Keluarga:',
            ['No.', 'Nama', 'NIK', 'SHDK', 'Tempat Lahir', 'Tgl Lahir', 'Agama', 'Pendidikan', 'Pekerjaan', 'Status Kawin', 'Nama Ayah', 'Nama Ibu'],
            anggota.map((r, i) => [String(i + 1), ...r]),
            [5, 15, 12, 7, 10, 9, 7, 9, 10, 7, 7, 7].map((w) => (w / 105) * 100),
          ),
        ],
        closing: CLOSING_F101,
        signer: { role: 'Kepala Keluarga', name: nameOrDots(data.namaKepalaKeluarga), nip: '' },
        leftSigner: { role: 'Kepala Dinas Kependudukan dan Pencatatan Sipil Kabupaten Subang' },
        // F-1.01 PDF template landscape; area isi tetap 62% seperti kematian (§1A).
        landscape: true,
      };
    }

    case 'f102': {
      return {
        ...base,
        intro: 'Dengan ini mengajukan permohonan sebagai berikut:',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaLengkap, true),
              row('NIK', data.nik, true),
              row('Nomor Kartu Keluarga', data.noKK, true),
              row('Jenis Permohonan', data.jenisPermohonan, true),
            ),
          },
          ...(data.daftarLampiran
            ? [{ kind: 'paragraph' as const, text: `PERSYARATAN YANG DILAMPIRKAN:\n${data.daftarLampiran}` }]
            : []),
        ],
        closing: CLOSING_FORM,
        signer: { role: 'Pemohon', name: nameOrDots(data.namaLengkap), nip: '' },
        leftSigner: { role: 'Petugas Pendaftaran' },
      };
    }

    case 'f103': {
      const anggota = tableFromData(data, 'daftarAnggotaPindah', [
        { name: 'nik', label: 'NIK' },
        { name: 'nama', label: 'Nama' },
        { name: 'shdk', label: 'SHDK' },
      ]);
      return {
        ...base,
        intro: 'Dengan ini mengajukan permohonan pindah penduduk sebagai berikut:',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nomor Kartu Keluarga', data.noKK, true),
              row('Nama Pemohon', data.namaPemohon, true),
              row('NIK', data.nik, true),
              row('Jenis Permohonan', data.jenisPermohonan, true),
              row('Alamat Asal', data.alamatAsal, true),
              row('RT / RW (alamat asal)', [data.rt, data.rw].filter(Boolean).join(' / ')),
              row('Klasifikasi Kepindahan', data.klasifikasiPindah, true),
              row('Alamat Pindah (tujuan)', data.alamatPindah, true),
              row('Alasan Pindah', data.alasanPindah, true),
              row('Jenis Kepindahan', data.jenisKepindahan),
            ),
          },
          ...tableBlock(
            'Daftar Anggota Keluarga yang Pindah:',
            ['No.', 'NIK', 'Nama', 'SHDK'],
            anggota.map((r, i) => [String(i + 1), ...r]),
            [8, 30, 42, 20],
          ),
          ...(data.anggotaTidakPindah
            ? [{ kind: 'paragraph' as const, text: `Anggota keluarga yang tidak pindah: ${data.anggotaTidakPindah}` }]
            : []),
        ],
        closing: CLOSING_FORM,
        signer: { role: 'Pemohon', name: nameOrDots(data.namaPemohon), nip: '' },
        leftSigner: { prefix: 'Mengetahui,', role: 'Kepala Dinas Kependudukan dan Pencatatan Sipil Kabupaten Subang' },
      };
    }

    case 'f104': {
      return {
        ...base,
        intro:
          'Yang bertanda tangan di bawah ini, dengan sesungguhnya menyatakan bahwa saya tidak memiliki dokumen kependudukan.',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaLengkap, true),
              row('Alamat', data.alamat, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Nama Ibu', data.namaIbu, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              'Dengan ini menyatakan bahwa saya tidak memiliki Dokumen Kependudukan dan apabila di kemudian ' +
              'hari ternyata pernyataan saya ini tidak benar, maka saya bersedia diproses secara hukum sesuai ' +
              'dengan peraturan perundang-undangan serta dokumen yang diterbitkan dari permohonan ini menjadi ' +
              'tidak sah.',
          },
        ],
        closing: CLOSING_SPTJM,
        signer: { role: 'Yang Menyatakan', name: nameOrDots(data.namaLengkap), nip: '' },
      };
    }

    case 'f105': {
      const anak = tableFromData(data, 'daftarAnak', [
        { name: 'nama', label: 'Nama Anak' },
        { name: 'noAktaKelahiran', label: 'No. Akta Kelahiran' },
        { name: 'shdk', label: 'SHDK' },
      ]);
      const tgl =
        data.tanggalPerkawinan
          ? formatDateTTL(data.tanggalPerkawinan)
          : [data.bulanPerkawinan, data.tahunPerkawinan].filter(Boolean).join(' ');
      return {
        ...base,
        intro:
          'Kami yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa perkawinan kami belum tercatat.',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Suami — Nama', data.namaSuami, true),
              row('Suami — NIK', data.nikSuami, true),
              row('Istri — Nama', data.namaIstri, true),
              row('Istri — NIK', data.nikIstri, true),
              row('Tanggal Perkawinan', tgl, true),
            ),
          },
          {
            kind: 'fields',
            rows: rows(
              row('Saksi 1 — Nama', data.namaSaksi1, true),
              row('Saksi 1 — NIK', data.nikSaksi1),
              row('Saksi 2 — Nama', data.namaSaksi2, true),
              row('Saksi 2 — NIK', data.nikSaksi2),
            ),
          },
          ...tableBlock(
            'Daftar Anak:',
            ['No.', 'Nama Anak', 'No. Akta Kelahiran', 'SHDK'],
            anak.map((r, i) => [String(i + 1), ...r]),
            [8, 40, 32, 20],
          ),
          {
            kind: 'paragraph',
            text: 'Pernyataan ini dibuat dengan bermaterai cukup.',
          },
        ],
        closing: CLOSING_F105,
        // Template: ttd 4 kolom — PIHAK KEDUA, PIHAK PERTAMA (+materai),
        // SAKSI II, SAKSI I — masing-masing dengan baris NIK di bawah nama.
        signers: [
          { role: 'PIHAK KEDUA', name: nameOrDots(data.namaIstri), nip: '', sub: nikSub(data.nikIstri) },
          { role: 'PIHAK PERTAMA', name: nameOrDots(data.namaSuami), nip: '', sub: nikSub(data.nikSuami) },
          { role: 'SAKSI II', name: nameOrDots(data.namaSaksi2), nip: '', sub: nikSub(data.nikSaksi2) },
          { role: 'SAKSI I', name: nameOrDots(data.namaSaksi1), nip: '', sub: nikSub(data.nikSaksi1) },
        ],
        signer: { role: 'Suami', name: nameOrDots(data.namaSuami), nip: '' },
      };
    }

    case 'f106': {
      const anggota = tableFromData(data, 'tabelAnggota', [
        { name: 'nama', label: 'Nama Anggota Keluarga' },
        { name: 'nik', label: 'NIK' },
        { name: 'shdk', label: 'SHDK' },
        { name: 'keterangan', label: 'Keterangan' },
        { name: 'perubahan', label: 'Perubahan Elemen Data' },
      ]);
      return {
        ...base,
        intro:
          'Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa telah terjadi perubahan elemen data kependudukan sebagai berikut:',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaLengkap, true),
              row('NIK', data.nik, true),
              row('Nomor Kartu Keluarga', data.noKK, true),
              row('Alamat', data.alamat, true),
            ),
          },
          ...tableBlock(
            'Tabel Anggota Keluarga:',
            ['No.', 'Nama', 'NIK', 'SHDK', 'Keterangan', 'Perubahan Elemen Data'],
            anggota.map((r, i) => [String(i + 1), ...r]),
            [6, 24, 18, 10, 17, 25],
          ),
          {
            kind: 'fields',
            rows: rows(
              row('Pendidikan — Semula', data.perubahanPendidikanSemula),
              row('Pendidikan — Menjadi', data.perubahanPendidikanMenjadi),
              row('Pendidikan — Dasar Perubahan', data.dasarPerubahanPendidikan),
              row('Pekerjaan — Semula', data.perubahanPekerjaanSemula),
              row('Pekerjaan — Menjadi', data.perubahanPekerjaanMenjadi),
              row('Pekerjaan — Dasar Perubahan', data.dasarPerubahanPekerjaan),
              row('Perubahan Agama', data.perubahanAgama),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Terlampir disampaikan fotocopi berkas-berkas yang terkait dengan perubahan elemen data tersebut.',
          },
        ],
        closing: CLOSING_F106,
        signer: { role: 'Yang Menyatakan', name: nameOrDots(data.namaLengkap), nip: '' },
      };
    }

    case 'f115': {
      const anggota = tableFromData(data, 'daftarAnggota', [
        { name: 'nik', label: 'NIK' },
        { name: 'nama', label: 'Nama' },
        { name: 'shdk', label: 'SHDK' },
      ]);
      return {
        ...base,
        intro: 'Dengan ini mengajukan permohonan Kartu Keluarga baru sebagai berikut:',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama Pemohon', data.namaPemohon, true),
              row('NIK', data.nik, true),
              row('Nomor KK Semula', data.noKKLama),
              row('Alamat', data.alamat, true),
              row('RT / RW', [data.rt, data.rw].filter(Boolean).join(' / ')),
              row('Alasan Permohonan', data.alasanPermohonan, true),
              row('Jumlah Anggota Keluarga', data.jumlahAnggota),
            ),
          },
          ...tableBlock(
            'Daftar Anggota Keluarga:',
            ['No.', 'NIK', 'Nama', 'SHDK'],
            anggota.map((r, i) => [String(i + 1), ...r]),
            [8, 30, 42, 20],
          ),
          {
            kind: 'list',
            heading: 'PERSYARATAN PENERBITAN KK',
            items: [
              'A. KK BARU: 1. Fotokopi kutipan akta kawin/nikah; 2. Fotokopi kutipan akta perceraian; 3. Fotokopi kutipan akta kelahiran kepala keluarga & seluruh anggota; 4. Surat keterangan pindah/datang; 5. KK yang rusak (apabila KK rusak).',
              'B. PERUBAHAN KK: 1. Penambahan anggota karena kelahiran (KK lama, kutipan akta kelahiran); 2. Pengurangan anggota (KK lama, surat keterangan kematian, surat keterangan pindah); 3. Penambahan anggota karena menumpang dalam KK (KK yang akan ditumpangi, surat keterangan pindah datang).',
            ],
          },
        ],
        closing: CLOSING_FORM,
        // Template: baris 1 [Camat, Pemohon], baris 2 [Kades/Lurah, Petugas].
        signers: [
          { prefix: 'Mengetahui,', role: 'Camat Kasomalang', name: '........................', nip: '' },
          { role: 'Pemohon', name: nameOrDots(data.namaPemohon), nip: '' },
          { role: 'Kepala Desa / Lurah', name: header.namaPejabat || 'H. AMIRUDIN, S.Pd.I', nip: '' },
          { role: 'Petugas', name: '........................', nip: '', sub: 'Tanggal Pemasukan Data: ....................\nParaf Petugas: ....................' },
        ],
        signer: { role: 'Pemohon', name: nameOrDots(data.namaPemohon), nip: '' },
      };
    }

    case 'f201': {
      const jenis = data.jenisPelaporan || '';
      // Urutan blok (template): Jenis+Pelapor → Subjek Akta → Saksi-saksi →
      // Orang Tua → seksi kondisional TERAKHIR.
      const blocks: ContentBlock[] = [
        {
          kind: 'fields',
          rows: rows(
            row('Jenis Pelaporan', jenis, true),
            row('Nama Pelapor', data.namaPelapor, true),
            row('NIK Pelapor', data.nikPelapor, true),
            row('Alamat Pelapor', data.alamatPelapor, true),
          ),
        },
        {
          kind: 'paragraph',
          text: 'Subjek Akta:',
        },
        {
          kind: 'fields',
          rows: rows(
            row('Subjek Akta I — Nama', data.namaSubjek1, true),
            row('Subjek Akta I — NIK', data.nikSubjek1),
            row('Subjek Akta II — Nama', data.namaSubjek2),
            row('Subjek Akta II — NIK', data.nikSubjek2),
          ),
        },
        {
          kind: 'paragraph',
          text: 'Saksi-saksi:',
        },
        {
          kind: 'fields',
          rows: rows(
            row('Saksi I — Nama', data.namaSaksi1, true),
            row('Saksi I — NIK', data.nikSaksi1),
            row('Saksi II — Nama', data.namaSaksi2, true),
            row('Saksi II — NIK', data.nikSaksi2),
          ),
        },
        {
          kind: 'paragraph',
          text: 'Orang Tua:',
        },
        {
          kind: 'fields',
          rows: rows(
            row('Nama Ayah', data.namaAyah),
            row('NIK Ayah', data.nikAyah),
            row('Nama Ibu', data.namaIbu),
            row('NIK Ibu', data.nikIbu),
          ),
        },
      ];
      // --- Seksi kondisional (TERAKHIR): hanya seksi jenis yang dipilih yang tampil ---
      if (jenis === 'Kelahiran') {
        blocks.push(
          { kind: 'paragraph', text: 'Data Anak:' },
          {
            kind: 'fields',
            rows: rows(
              row('Nama Anak', data.namaAnak, true),
              row('Tempat Lahir', data.tempatLahirAnak),
              row('Tanggal Lahir', formatDateTTL(data.tanggalLahirAnak), true),
              row('Pukul Lahir', data.pukulLahirAnak),
              row('Jenis Kelamin', data.jenisKelaminAnak),
              row('Anak Ke-', data.anakKe),
            ),
          },
        );
      } else if (jenis === 'Lahir Mati') {
        blocks.push(
          { kind: 'paragraph', text: 'Data Lahir Mati:' },
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaLahirMati, true),
              row('Tanggal', formatDateTTL(data.tanggalLahirMati), true),
              row('Pukul', data.pukulLahirMati),
              row('Tempat', data.tempatLahirMati),
              row('Jenis Kelamin', data.jenisKelaminLahirMati),
              row('Sebab', data.sebabLahirMati),
              row('Keterangan', data.keteranganLahirMati),
            ),
          },
        );
      } else if (jenis === 'Kematian') {
        blocks.push(
          { kind: 'paragraph', text: 'Data Kematian:' },
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaMeninggal, true),
              row('NIK', data.nikMeninggal),
              row('Tanggal Meninggal', formatDateTTL(data.tanggalMeninggal), true),
              row('Pukul', data.pukulMeninggal),
              row('Sebab Kematian', data.sebabMeninggal),
              row('Tempat Meninggal', data.tempatMeninggal),
              row('Yang Menerangkan', data.yangMenerangkan),
            ),
          },
        );
      } else if (jenis) {
        const pasangan = ['Perkawinan', 'Pembatalan Perkawinan', 'Perceraian', 'Pembatalan Perceraian'].includes(jenis);
        blocks.push(
          { kind: 'paragraph', text: `Data ${jenis}:` },
          {
            kind: 'fields',
            rows: rows(
              ...(pasangan
                ? [
                    row('Nama Suami', data.namaSuamiPeristiwa, true),
                    row('Nama Istri', data.namaIstriPeristiwa, true),
                  ]
                : []),
              row('Tanggal Peristiwa', formatDateTTL(data.tanggalPeristiwa), true),
              row('Tempat Peristiwa', data.tempatPeristiwa),
              row('Keterangan', data.keteranganPeristiwa),
            ),
          },
        );
      }
      return {
        ...base,
        intro: 'Dengan ini melaporkan peristiwa pencatatan sipil sebagai berikut:',
        blocks,
        closing: CLOSING_FORM,
        // Kiri: Mengetahui Kades/Lurah (+ baris Pejabat Dukcapil); kanan: Pelapor.
        signers: [
          { prefix: 'Mengetahui,', role: 'Kepala Desa / Lurah', name: header.namaPejabat || 'H. AMIRUDIN, S.Pd.I', nip: '', sub: 'Pejabat Dukcapil yang Membidangi' },
          { role: 'Pelapor', name: nameOrDots(data.namaPelapor), nip: '' },
        ],
        signer: { role: 'Pelapor', name: nameOrDots(data.namaPelapor), nip: '' },
      };
    }

    case 'f203': {
      return {
        ...base,
        intro:
          'Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya kebenaran data kelahiran sebagai berikut:',
        blocks: [
          {
            kind: 'paragraph',
            text: 'Yang Menyatakan:',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaYangMenyatakan, true),
              row('NIK', data.nik, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Data Anak:',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Nama Anak', data.namaAnak, true),
              row('NIK Anak', data.nikAnak),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahirAnak, data.tanggalLahirAnak)),
              row('Jenis Kelamin', data.jenisKelaminAnak),
              row('Anak Ke-', data.anakKe),
              row('Alamat Anak', data.alamatAnak),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Data Ibu:',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Nama Ibu', data.namaIbu, true),
              row('NIK Ibu', data.nikIbu),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahirIbu, data.tanggalLahirIbu)),
              row('Pekerjaan Ibu', data.pekerjaanIbu),
              row('Alamat Ibu', data.alamatIbu),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Penolong Kelahiran:',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Nama Penolong', data.namaPenolong),
              row('NIK Penolong', data.nikPenolong),
              row('Pekerjaan Penolong', data.pekerjaanPenolong),
              row('Alamat Penolong', data.alamatPenolong),
            ),
          },
          {
            kind: 'fields',
            rows: rows(
              row('Saksi 1 — Nama', data.namaSaksi1, true),
              row('Saksi 1 — NIK', data.nikSaksi1),
              row('Saksi 2 — Nama', data.namaSaksi2, true),
              row('Saksi 2 — NIK', data.nikSaksi2),
            ),
          },
        ],
        closing: CLOSING_F203,
        // Template: Saksi I/II (kiri, +NIK) + Saya yang menyatakan (kanan, +NIK).
        signers: [
          { role: 'Saksi I', name: nameOrDots(data.namaSaksi1), nip: '', sub: nikSub(data.nikSaksi1) },
          { role: 'Saya yang Menyatakan', name: nameOrDots(data.namaYangMenyatakan), nip: '', sub: nikSub(data.nik) },
          { role: 'Saksi II', name: nameOrDots(data.namaSaksi2), nip: '', sub: nikSub(data.nikSaksi2) },
        ],
        signerColumns: 3,
        signer: { role: 'Yang Menyatakan', name: nameOrDots(data.namaYangMenyatakan), nip: '' },
      };
    }

    case 'f204': {
      return {
        ...base,
        intro:
          'Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa kami adalah pasangan suami istri yang sah.',
        blocks: [
          {
            kind: 'paragraph',
            text: 'Yang Menyatakan:',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaYangMenyatakan, true),
              row('NIK', data.nik, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Pasangan (Suami/Istri):',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Nama Pasangan', data.namaPasangan, true),
              row('NIK Pasangan', data.nikPasangan, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahirPasangan, data.tanggalLahirPasangan)),
              row('Pekerjaan Pasangan', data.pekerjaanPasangan),
              row('Alamat Pasangan', data.alamatPasangan),
              row('Nomor Kartu Keluarga', data.noKK),
            ),
          },
          {
            kind: 'fields',
            rows: rows(
              row('Saksi 1 — Nama', data.namaSaksi1, true),
              row('Saksi 1 — NIK', data.nikSaksi1),
              row('Saksi 2 — Nama', data.namaSaksi2, true),
              row('Saksi 2 — NIK', data.nikSaksi2),
            ),
          },
          {
            kind: 'paragraph',
            text: '*) Coret yang tidak perlu.',
          },
        ],
        closing: CLOSING_F204,
        // Template: Pasangan TIDAK menandatangani — hanya Saksi I/II + Saya.
        signers: [
          { role: 'Saksi I', name: nameOrDots(data.namaSaksi1), nip: '', sub: nikSub(data.nikSaksi1) },
          { role: 'Saya yang Menyatakan', name: nameOrDots(data.namaYangMenyatakan), nip: '' },
          { role: 'Saksi II', name: nameOrDots(data.namaSaksi2), nip: '', sub: nikSub(data.nikSaksi2) },
        ],
        signerColumns: 3,
        signer: { role: 'Yang Menyatakan', name: nameOrDots(data.namaYangMenyatakan), nip: '' },
      };
    }

    // ================= FASE 2 — SPTJM BARU =================

    case 'sptjm_kematian': {
      return {
        ...base,
        intro:
          'Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya kebenaran data kematian sebagai berikut:',
        blocks: [
          {
            kind: 'paragraph',
            text: 'Yang Menyatakan:',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaYangMenyatakan, true),
              row('NIK', data.nik, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Data Almarhum/Almarhumah:',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaAlmarhum, true),
              row('NIK', data.nikAlmarhum),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahirAlmarhum, data.tanggalLahirAlmarhum)),
              row('Alamat Terakhir (sesuai KTP)', data.alamatAlmarhum),
              row('Hari Meninggal', data.hariMeninggal, true),
              row('Tanggal Meninggal', formatDateTTL(data.tanggalMeninggal), true),
              row('Jam Meninggal', data.jamMeninggal),
              row('Tempat Meninggal', data.tempatMeninggal, true),
            ),
          },
          {
            kind: 'fields',
            rows: rows(
              row('Saksi 1 — Nama', data.namaSaksi1, true),
              row('Saksi 1 — NIK', data.nikSaksi1),
              row('Saksi 2 — Nama', data.namaSaksi2, true),
              row('Saksi 2 — NIK', data.nikSaksi2),
            ),
          },
          {
            kind: 'paragraph',
            text: 'Pernyataan ini dibuat dengan bermaterai cukup.',
          },
        ],
        closing: CLOSING_SPTJM,
        // Grid 3 kolom: baris 1 [Saksi 1, Yang Menyatakan, Saksi 2],
        // baris 2 [Ketua RT, Ketua RW, Kepala Desa / Lurah].
        signers: [
          { role: 'Saksi 1', name: nameOrDots(data.namaSaksi1), nip: '', sub: nikSub(data.nikSaksi1) },
          { role: 'Yang Menyatakan', name: nameOrDots(data.namaYangMenyatakan), nip: '' },
          { role: 'Saksi 2', name: nameOrDots(data.namaSaksi2), nip: '', sub: nikSub(data.nikSaksi2) },
          { role: 'Ketua RT', name: '........................', nip: '' },
          { role: 'Ketua RW', name: '........................', nip: '' },
          { role: 'Kepala Desa / Lurah', name: header.namaPejabat || 'H. AMIRUDIN, S.Pd.I', nip: '' },
        ],
        signerColumns: 3,
        signer: { role: 'Yang Menyatakan', name: nameOrDots(data.namaYangMenyatakan), nip: '' },
      };
    }

    case 'sptjm_perkawinan_perceraian': {
      const jenis = data.jenisSPTJM || 'Perkawinan';
      const anak = tableFromData(data, 'daftarAnak', [
        { name: 'nama', label: 'Nama Anak' },
        { name: 'noAktaKelahiran', label: 'No. Akta Kelahiran' },
        { name: 'shdk', label: 'SHDK' },
      ]);
      return {
        ...base,
        title: `SURAT PERNYATAAN TANGGUNG JAWAB MUTLAK ${jenis.toUpperCase()} BELUM TERCATAT`,
        intro:
          `Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa ${jenis.toLowerCase()} ` +
          'kami belum tercatat.',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Suami — Nama', data.namaSuami, true),
              row('Suami — NIK', data.nikSuami, true),
              row('Istri — Nama', data.namaIstri, true),
              row('Istri — NIK', data.nikIstri, true),
              row(`Tanggal ${jenis}`, formatDateTTL(data.tanggalPeristiwa), true),
            ),
          },
          {
            kind: 'fields',
            rows: rows(
              row('Saksi 1 — Nama', data.namaSaksi1, true),
              row('Saksi 1 — NIK', data.nikSaksi1),
              row('Saksi 2 — Nama', data.namaSaksi2, true),
              row('Saksi 2 — NIK', data.nikSaksi2),
            ),
          },
          ...tableBlock(
            'Daftar Anak:',
            ['No.', 'Nama Anak', 'No. Akta Kelahiran', 'SHDK'],
            anak.map((r, i) => [String(i + 1), ...r]),
            [8, 40, 32, 20],
          ),
          {
            kind: 'paragraph',
            text: 'Pernyataan ini dibuat dengan bermaterai cukup.',
          },
        ],
        closing: CLOSING_SPTJM_PERKAWINAN,
        // Template: ttd 4 kolom + NIK tiap penandatangan.
        signers: [
          { role: 'PIHAK KEDUA', name: nameOrDots(data.namaIstri), nip: '', sub: nikSub(data.nikIstri) },
          { role: 'PIHAK PERTAMA', name: nameOrDots(data.namaSuami), nip: '', sub: nikSub(data.nikSuami) },
          { role: 'SAKSI II', name: nameOrDots(data.namaSaksi2), nip: '', sub: nikSub(data.nikSaksi2) },
          { role: 'SAKSI I', name: nameOrDots(data.namaSaksi1), nip: '', sub: nikSub(data.nikSaksi1) },
        ],
        signer: { role: 'Suami', name: nameOrDots(data.namaSuami), nip: '' },
      };
    }

    // ================= FASE 2 — BANSOS PBI-JKN =================

    case 'pbi_asesmen': {
      const qa: [string, string | undefined][] = [
        ['Jaminan kesehatan yang dimiliki', data.jaminanKesehatan],
        ['Alasan jaminan kesehatan non-aktif', data.alasanNonAktif],
        ['Status rumah', data.statusRumah],
        ['Kondisi rumah', data.kondisiRumah],
        ['Ukuran rumah', data.ukuranRumah],
        ['Dinding rumah', data.dindingRumah],
        ['Penerangan rumah', data.peneranganRumah],
        ['Jumlah keluarga dalam satu rumah', data.jumlahKeluarga],
        ['Jumlah anggota keluarga', data.jumlahAnggota],
        ['Bansos yang diterima saat ini', data.bansosDiterima],
        ['Usaha / penghasilan', data.usahaPenghasilan],
      ];
      return {
        ...base,
        intro:
          'Instrumen asesmen calon penerima bansos KIS-PBI / SKM. Identitas dan kondisi umum calon penerima sebagai berikut:',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaLengkap, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Jenis Kelamin', data.jenisKelamin),
              row('Status Kawin', data.statusKawin),
              row('NIK', data.nik, true),
              row('Nomor Kartu Keluarga', data.noKK),
              row('Nama Ibu Kandung', data.namaIbuKandung),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'list',
            heading: 'Hasil Asesmen:',
            // Nomor jawaban PAKAI nomor asli 1–11 (template); jawaban kosong
            // dilewati TAPI nomornya tetap asli (jangan menomori ulang).
            items: qa
              .map(([q, v], idx) => ({ q, v, n: idx + 1 }))
              .filter(({ v }) => (v || '').toString().trim())
              .map(({ q, v, n }) => `${n}. ${q}: ${v}`),
          },
          ...(data.catatanPetugas
            ? [{ kind: 'paragraph' as const, text: `Catatan petugas: ${data.catatanPetugas}` }]
            : []),
        ],
        closing: CLOSING_FORM,
        // Baris "TAHUN <tahun>" di bawah judul (dari tanggal surat).
        subtitle: `TAHUN ${yearOf(data.tanggalSurat)}`,
        // Grid 2 kolom: Pemohon, Fasilitator, Sekdes, Ketua RW, Ketua RT,
        // Camat, a.n. Kades/Sekdes.
        signers: [
          { role: 'Pemohon', name: nameOrDots(data.namaLengkap), nip: '' },
          { role: 'Fasilitator', name: nameOrDots(data.namaFasilitator), nip: '' },
          { role: 'Sekretaris Desa', name: header.namaSekdes || 'APIPUDIN, S.IP', nip: '' },
          { role: 'Ketua RW', name: nameOrDots(data.namaKetuaRW), nip: '' },
          { role: 'Ketua RT', name: nameOrDots(data.namaKetuaRT), nip: '' },
          { role: 'Camat Kasomalang', name: nameOrDots(data.namaCamat), nip: '' },
          { prefix: 'a.n. Kepala Desa Kasomalang Kulon', role: 'Sekretaris Desa', name: header.namaSekdes || 'APIPUDIN, S.IP', nip: '', kind: 'sekdes-an' },
        ],
        signer: { role: 'Pemohon', name: nameOrDots(data.namaLengkap), nip: '' },
      };
    }

    case 'pbi_pernyataan': {
      return {
        ...base,
        intro: 'Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya:',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('No KK', data.noKK, true),
              row('NIK', data.nik, true),
              row('Nama', data.namaLengkap, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Jenis Kelamin', data.jenisKelamin),
              row('Warga Negara', data.wargaNegara || 'Indonesia'),
              row('Status Kawin', data.statusKawin),
              row('Agama', data.agama || 'Islam'),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              '1. Saya benar-benar termasuk Keluarga Miskin;\n' +
              '2. Apabila pernyataan saya ini tidak sesuai dengan kenyataan yang sebenarnya, saya siap ' +
              'mempertanggung jawabkan dihadapan hukum sesuai dengan ketentuan yang berlaku.',
          },
          // Isi pernyataan lama (bila pernah terisi) tetap tampil sebagai
          // paragraf tambahan — jangan hapus data pengguna.
          ...(data.isiPernyataan
            ? [{ kind: 'paragraph' as const, text: data.isiPernyataan }]
            : []),
        ],
        closing: CLOSING_PBI_PERNYATAAN,
        signers: [
          { role: 'Yang Bersangkutan', name: nameOrDots(data.namaLengkap), nip: '', sub: 'Materai Rp10.000' },
          { role: 'Saksi Ketua RW', name: nameOrDots(data.namaKetuaRW), nip: '' },
          { role: 'Saksi Ketua RT', name: nameOrDots(data.namaKetuaRT), nip: '' },
          { prefix: 'Mengetahui,', role: 'Camat Kasomalang', name: nameOrDots(data.namaCamat), nip: '' },
          { prefix: 'a.n. Kepala Desa Kasomalang Kulon', role: 'Sekretaris Desa', name: header.namaSekdes || 'APIPUDIN, S.IP', nip: '', kind: 'sekdes-an' },
        ],
        signer: { role: 'Yang Menyatakan', name: nameOrDots(data.namaLengkap), nip: '' },
      };
    }

    case 'pbi_tidak_punya_rekening': {
      return {
        ...base,
        intro:
          'Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa saya tidak memiliki rekening bank.',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama', data.namaLengkap, true),
              row('NIK', data.nik, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
            ),
          },
        ],
        closing: CLOSING_SPTJM,
        signer: { role: 'Yang Menyatakan', name: nameOrDots(data.namaLengkap), nip: '' },
      };
    }

    // ================= FASE 5 — TIPE BARU (total 33) =================

    case 'sktm_pbi': {
      const tujuan = (data.tujuan || '').trim();
      const keperluanTujuan: Record<string, string> = {
        'RSUD Subang': 'pembebasan biaya rawat inap di RSUD SUBANG',
        'Institut Pertanian Bogor (IPB)': 'Pembebasan/keringanan biaya pendidikan di Institut Pertanian Bogor (IPB)',
        'Pengajuan PBI KIS': 'Pengajuan PBI KIS',
        'RSUD Ciereng Subang': 'pembebasan biaya rawat inap di RSUD Ciereng Subang',
      };
      const anak = tableFromData(data, 'daftarAnak', [
        { name: 'noKK', label: 'No KK' },
        { name: 'nik', label: 'NIK' },
        { name: 'nama', label: 'Nama' },
        { name: 'ttl', label: 'TTL' },
        { name: 'jenisKelamin', label: 'JK' },
        { name: 'wargaNegara', label: 'WN' },
        { name: 'statusPerkawinan', label: 'Status' },
        { name: 'agama', label: 'Agama' },
        { name: 'pekerjaan', label: 'Pekerjaan' },
        { name: 'alamat', label: 'Alamat' },
      ]);
      return {
        ...base,
        kopVariant: 'dua-logo',
        intro:
          `Yang bertanda tangan di bawah ini Kepala Desa ${desa} Kecamatan ${kecamatan} Kabupaten ${kabupaten}, ` +
          'menerangkan dengan sesungguhnya bahwa :',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('No KK', data.noKK, true),
              row('NIK', data.nik, true),
              row('Nama', data.namaLengkap, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Jenis Kelamin', data.jenisKelamin),
              row('Warga Negara', data.wargaNegara || 'Indonesia'),
              row('Status Kawin', data.statusKawin),
              row('Agama', data.agama || 'Islam'),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
            ),
          },
          // Blok anak (tabel) hanya bila ada baris terisi.
          ...(anak.length
            ? tableBlock(
                'Daftar Anggota Keluarga:',
                ['No.', 'No KK', 'NIK', 'Nama', 'TTL', 'JK', 'WN', 'Status', 'Agama', 'Pekerjaan', 'Alamat'],
                anak.map((r, i) => [String(i + 1), ...r]),
                [4, 10, 12, 14, 10, 5, 6, 8, 7, 10, 14],
              )
            : []),
          // Kalimat tujuan hanya bila tujuan dipilih (bila kosong, jangan
          // render "untuk keperluan ." — perbaikan koordinator Fase 5).
          ...(tujuan
            ? [
                {
                  kind: 'paragraph' as const,
                  text: `Surat keterangan ini dibuat untuk keperluan ${keperluanTujuan[tujuan] || tujuan}.`,
                },
              ]
            : []),
        ],
        closing: CLOSING_SKTM_PBI,
        signers: [
          { role: 'Ketua RW', name: nameOrDots(data.namaKetuaRW), nip: '' },
          { role: 'Ketua RT', name: nameOrDots(data.namaKetuaRT), nip: '' },
          { prefix: 'Mengetahui,', role: 'Camat Kasomalang', name: nameOrDots(data.namaCamat), nip: '' },
          { prefix: 'a.n. Kepala Desa Kasomalang Kulon', role: 'Sekretaris Desa', name: header.namaSekdes || 'APIPUDIN, S.IP', nip: '', kind: 'sekdes-an' },
        ],
        signer: { role: 'Pemohon', name: nameOrDots(data.namaLengkap), nip: '', kind: 'pemohon' },
      };
    }

    case 'pbi_rekening_listrik': {
      return {
        ...base,
        kopVariant: 'dua-logo',
        intro:
          `Yang bertanda tangan di bawah ini Kepala Desa ${desa} Kecamatan ${kecamatan} Kabupaten ${kabupaten}, ` +
          'menerangkan dengan sesungguhnya bahwa :',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('No KK', data.noKK, true),
              row('NIK', data.nik, true),
              row('Nama', data.namaLengkap, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Jenis Kelamin', data.jenisKelamin),
              row('Warga Negara', data.wargaNegara || 'Indonesia'),
              row('Status Kawin', data.statusKawin),
              row('Agama', data.agama || 'Islam'),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              'Menurut pengakuannya belum mempunyai rekening listrik dan menumpang aliran listrik ke ' +
              `${data.namaTetangga || ''} dengan nomor pelanggan ${data.nomorPelanggan || ''}.`,
          },
        ],
        closing: CLOSING_SKTM_PBI,
        signers: [
          { role: 'Ketua RW', name: nameOrDots(data.namaKetuaRW), nip: '' },
          { role: 'Ketua RT', name: nameOrDots(data.namaKetuaRT), nip: '' },
          { prefix: 'Mengetahui,', role: 'Camat Kasomalang', name: nameOrDots(data.namaCamat), nip: '' },
          { prefix: 'a.n. Kepala Desa Kasomalang Kulon', role: 'Sekretaris Desa', name: header.namaSekdes || 'APIPUDIN, S.IP', nip: '', kind: 'sekdes-an' },
        ],
        signer: { role: 'Pemohon', name: nameOrDots(data.namaLengkap), nip: '', kind: 'pemohon' },
      };
    }

    case 'pernyataan_akta_kelahiran': {
      return {
        ...base,
        intro: 'Yang bertanda tangan di bawah ini :',
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('NIK', data.nik, true),
              row('Nama', data.namaLengkap, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Nama Ayah', data.namaAyah, true),
              row('Nama Ibu', data.namaIbu, true),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              'Menyatakan dengan sebenarnya bahwa orang tersebut sedang membuat AKTA KELAHIRAN dan sampai ' +
              'saat ini masih menunggu selesai dibuat.',
          },
        ],
        closing: CLOSING_PERMOHONAN_RUMAH,
        signer: sekdesAnSigner(header, desa),
      };
    }
    }
  })();

  return { ...out, kopVariant: out.kopVariant ?? kopFor(out) };
}
