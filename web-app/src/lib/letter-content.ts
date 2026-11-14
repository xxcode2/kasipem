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
}

export interface LetterContent {
  title: string;
  nomor: string;
  /** Kop yang dipakai: 'desa' (logo Garuda + Subang) atau 'disdukcapil' (teks saja). */
  kop: 'desa' | 'disdukcapil';
  /** Sembunyikan baris "Nomor :" (formulir yang memang tidak bernomor). */
  hideNomor?: boolean;
  intro: string;
  blocks: ContentBlock[];
  closing: string;
  placeDate: string;
  signer: SignerBlock;
  /** Blok tanda tangan kiri (khusus SKTM: Mengetahui, Ketua RW). */
  leftSigner?: { prefix?: string; role: string; name?: string };
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
  // a.n. Kepala Desa oleh Sekretaris Desa: pisah KK + kematian.
  if (type === 'pisah_kk' || type === 'kematian' || header.penandatanganDefault === 'sekdes') {
    return {
      prefix: `a.n. Kepala Desa ${desa}`,
      role: 'Sekretaris Desa',
      name: header.namaSekdes || 'APIPUDIN, S.IP',
      nip: (header.nipSekdes || '').trim(),
    };
  }
  // Sekretaris Desa (tanpa a.n.): usaha + duda/janda.
  if (type === 'usaha' || type === 'duda_janda') {
    return {
      role: 'Sekretaris Desa',
      name: header.namaSekdes || 'APIPUDIN, S.IP',
      nip: (header.nipSekdes || '').trim(),
    };
  }
  return {
    role: header.jabatanPejabat || 'Kepala Desa',
    name: header.namaPejabat || 'H. AMIRUDIN, S.Pd.I',
    nip: (header.nipPejabat || '').trim(),
  };
}

const CLOSING = 'Demikian surat keterangan ini dibuat dengan sebenarnya agar dapat dipergunakan sebagaimana mestinya.';
const CLOSING_FORM = 'Demikian formulir ini diisi dengan sebenarnya dan dapat dipertanggungjawabkan.';
const CLOSING_SPTJM =
  'Demikian surat pernyataan ini dibuat dengan sebenarnya dan apabila di kemudian hari ternyata tidak benar, saya bersedia dituntut sesuai dengan ketentuan peraturan perundang-undangan yang berlaku.';

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
              row('Nama (1)', data.nama1, true),
              row('NIK (1)', data.nik1, true),
              row('Nama (2)', data.nama2, true),
              row('NIK (2)', data.nik2, true),
              row('Nomor KK Lama', data.nomorKKLama, true),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              'Nama-nama tersebut di atas adalah benar warga Desa kami dan mengajukan pemisahan ' +
              `Kartu Keluarga dari KK Nomor ${data.nomorKKLama || ''} untuk membentuk Kartu Keluarga baru ` +
              `dengan alamat ${data.alamat || ''}.`,
          },
        ],
        closing: CLOSING,
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
              row('Batas Utara', data.batasUtara),
              row('Batas Timur', data.batasTimur),
              row('Batas Selatan', data.batasSelatan),
              row('Batas Barat', data.batasBarat),
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
        closing: CLOSING,
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
        closing: CLOSING,
      };
    }

    case 'beda_nama': {
      return {
        ...base,
        intro: introKades,
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama Saat Ini', data.namaLengkap, true),
              row('NIK', data.nik, true),
              row('Nama di Dokumen Pertama', data.namaDokumen1, true),
              row('Nama di Dokumen Kedua', data.namaDokumen2, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              `Terdapat perbedaan nama antara dokumen pertama (${data.namaDokumen1 || ''}) dan ` +
              `dokumen kedua (${data.namaDokumen2 || ''}). Yang bersangkutan adalah orang yang sama, ` +
              `dengan alasan: ${data.alasan || ''}.`,
          },
        ],
        closing: CLOSING,
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
        return {
          ...base,
          intro: introKades,
          blocks: [
            {
              kind: 'paragraph',
              text:
                'Nama-nama warga di bawah ini adalah benar warga Desa kami dan termasuk dalam ' +
                'kategori tidak mampu menurut kriteria yang berlaku di desa kami:',
            },
            ...tableBlock(undefined, ['No.', 'Nama', 'NIK', 'Alamat'], daftar.map((r, i) => [String(i + 1), ...r]), [8, 30, 28, 34]),
            {
              kind: 'paragraph',
              text:
                'Surat keterangan ini dibuat sebagai salah satu syarat untuk keperluan ' +
                `${data.keperluan || 'pengajuan bantuan sosial'}${data.tujuan ? ` di ${data.tujuan}` : ''}.`,
            },
          ],
          closing: CLOSING,
          leftSigner: { prefix: 'Mengetahui,', role: 'Ketua RW' },
        };
      }
      const keperluan = (data.keperluan || '').trim();
      const tujuan = (data.tujuan || '').trim();
      const tujuanText = tujuan ? ` di ${tujuan}` : '';
      return {
        ...base,
        title:
          varian === 'Sekolah'
            ? 'SURAT KETERANGAN TIDAK MAMPU (SEKOLAH)'
            : 'SURAT KETERANGAN TIDAK MAMPU',
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
        leftSigner: { prefix: 'Mengetahui,', role: 'Ketua RW' },
      };
    }

    case 'ahli_waris': {
      return {
        ...base,
        intro: introKades,
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama Peninggal', data.namaPeninggal, true),
              row('NIK Peninggal', data.nikPeninggal, true),
              row('Alamat Peninggal', data.alamatPeninggal, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              'Almarhum/almarhumah tersebut di atas telah meninggal dunia dan meninggalkan ahli ' +
              'waris sebagai berikut:',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Ahli Waris 1 — Nama', data.namaAhliWaris1, true),
              row('Ahli Waris 1 — NIK', data.nikAhliWaris1),
              row('Ahli Waris 1 — Hubungan', data.hubunganAhliWaris1),
              row('Ahli Waris 2 — Nama', data.namaAhliWaris2),
              row('Ahli Waris 2 — NIK', data.nikAhliWaris2),
              row('Ahli Waris 2 — Hubungan', data.hubunganAhliWaris2),
            ),
          },
          ...(data.keterangan
            ? [{ kind: 'paragraph' as const, text: `Keterangan: ${data.keterangan}` }]
            : []),
        ],
        closing: CLOSING,
      };
    }

    case 'nikah_tidak_tercatat': {
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
              row('NIK', data.nikPria, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahirPria, data.tanggalLahirPria)),
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
              row('NIK', data.nikWanita, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahirWanita, data.tanggalLahirWanita)),
            ),
          },
          {
            kind: 'paragraph',
            text:
              `Menurut sepengetahuan kami, orang tersebut di atas telah menikah pada tanggal ` +
              `${formatDateTTL(data.tanggalPernikahan)} yang dilaksanakan di ` +
              `${data.tempatPernikahan || ''}.`,
          },
        ],
        closing: CLOSING,
      };
    }

    case 'pemakaman': {
      return {
        ...base,
        intro: introKades,
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama Almarhum/Almarhumah', data.namaAlmarhum, true),
              row('NIK', data.nikAlmarhum),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Alamat', data.alamat, true),
              row('Tanggal Meninggal', formatDateTTL(data.tanggalMeninggal)),
              row('Lokasi Makam', data.lokasiMakam, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              `Almarhum/almarhumah tersebut di atas dimakamkan di ${data.lokasiMakam || ''} ` +
              `atas permohonan keluarga, yaitu ${data.namaKeluarga || ''}.`,
          },
        ],
        closing: CLOSING,
      };
    }

    case 'tidak_memiliki_rumah': {
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
              row('Alamat Tempat Tinggal', data.alamatTinggal, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              'Adalah benar bahwa yang bersangkutan sampai saat ini tidak memiliki rumah ' +
              'tinggal sendiri dan masih menumpang / tinggal bersama keluarga di alamat ' +
              'tersebut di atas.',
          },
          ...(data.keterangan
            ? [{ kind: 'paragraph' as const, text: `Keterangan: ${data.keterangan}` }]
            : []),
        ],
        closing: CLOSING,
      };
    }

    case 'undangan': {
      const hariTanggal = [data.hari, [data.tanggal, data.bulan, data.tahun].filter(Boolean).join(' ')]
        .filter(Boolean)
        .join(', ');
      const tembusan: string[] = (data.daftarTembusan || '')
        .split(/[\n,]+/)
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
        closing: 'Demikian undangan ini kami sampaikan, atas perhatian dan kehadiran Bapak/Ibu/Saudara/i diucapkan terima kasih.',
      };
    }

    // ================= FASE 2 — SURAT DESA BARU =================

    case 'kematian': {
      return {
        ...base,
        intro: introKades,
        blocks: [
          {
            kind: 'fields',
            rows: rows(
              row('Nama Almarhum/Almarhumah', data.namaAlmarhum, true),
              row('NIK', data.nikAlmarhum, true),
              row('Jenis Kelamin', data.jenisKelamin),
              row('Agama', data.agama),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'paragraph',
            text:
              `Telah meninggal dunia pada hari ${data.hariMeninggal || ''}, tanggal ` +
              `${formatDateTTL(data.tanggalMeninggal)} di ${data.tempatMeninggal || ''} ` +
              `disebabkan ${data.penyebabMeninggal || ''}.`,
          },
          {
            kind: 'paragraph',
            text: 'Keterangan ini berdasarkan laporan dari:',
          },
          {
            kind: 'fields',
            rows: rows(
              row('Nama Pelapor', data.namaPelapor, true),
              row('NIK Pelapor', data.nikPelapor, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahirPelapor, data.tanggalLahirPelapor)),
              row('Pekerjaan', data.pekerjaanPelapor),
              row('Alamat', data.alamatPelapor, true),
              row('Hubungan dengan Almarhum/Almarhumah', data.hubunganPelapor, true),
            ),
          },
        ],
        closing: CLOSING,
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
              `Adalah benar bahwa yang bersangkutan memiliki usaha ${data.namaUsaha || ''} ` +
              `yang bertempat di ${data.alamatUsaha || ''}.`,
          },
        ],
        closing: CLOSING,
      };
    }

    case 'duda_janda': {
      const status = data.statusDudaJanda || 'Duda';
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
            text:
              `Adalah benar bahwa yang bersangkutan berstatus ${status.toLowerCase()} ` +
              `dan sampai saat ini belum menikah lagi.`,
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
        closing: CLOSING_FORM,
        signer: { role: 'Kepala Keluarga', name: nameOrDots(data.namaKepalaKeluarga), nip: '' },
        leftSigner: { role: 'Kepala Dinas Kependudukan dan Pencatatan Sipil Kabupaten Subang' },
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
            ? [{ kind: 'paragraph' as const, text: `Daftar persyaratan / lampiran yang disertakan:\n${data.daftarLampiran}` }]
            : []),
        ],
        closing: CLOSING_FORM,
        signer: { role: 'Pemohon', name: nameOrDots(data.namaLengkap), nip: '' },
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
            text: 'Pernyataan ini saya buat dengan sebenarnya dan bermaterai cukup.',
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
          'Yang bertanda tangan di bawah ini menyatakan dengan sesungguhnya bahwa perkawinan kami belum tercatat.',
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
          ...tableBlock(
            'Daftar Anak:',
            ['No.', 'Nama Anak', 'No. Akta Kelahiran', 'SHDK'],
            anak.map((r, i) => [String(i + 1), ...r]),
            [8, 40, 32, 20],
          ),
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
        signer: { role: 'Suami', name: nameOrDots(data.namaSuami), nip: '' },
        leftSigner: { role: 'Istri', name: nameOrDots(data.namaIstri) },
      };
    }

    case 'f106': {
      const anggota = tableFromData(data, 'tabelAnggota', [
        { name: 'nama', label: 'Nama Anggota Keluarga' },
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
            ['No.', 'Nama Anggota Keluarga', 'Perubahan Elemen Data'],
            anggota.map((r, i) => [String(i + 1), ...r]),
            [8, 42, 50],
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
        ],
        closing: CLOSING_SPTJM,
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
        ],
        closing: CLOSING_FORM,
        signer: { role: 'Pemohon', name: nameOrDots(data.namaPemohon), nip: '' },
        leftSigner: { prefix: 'Mengetahui,', role: 'Kepala Desa / Lurah', name: header.namaPejabat || 'H. AMIRUDIN, S.Pd.I' },
      };
    }

    case 'f201': {
      const jenis = data.jenisPelaporan || '';
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
      ];
      // --- Seksi kondisional: hanya seksi jenis yang dipilih yang tampil ---
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
              row('Tanggal', formatDateTTL(data.tanggalLahirMati), true),
              row('Pukul', data.pukulLahirMati),
              row('Tempat', data.tempatLahirMati),
              row('Jenis Kelamin', data.jenisKelaminLahirMati),
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
        blocks.push(
          { kind: 'paragraph', text: `Data ${jenis}:` },
          {
            kind: 'fields',
            rows: rows(
              row('Tanggal Peristiwa', formatDateTTL(data.tanggalPeristiwa), true),
              row('Tempat Peristiwa', data.tempatPeristiwa),
              row('Keterangan', data.keteranganPeristiwa),
            ),
          },
        );
      }
      blocks.push(
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
      );
      return {
        ...base,
        intro: 'Dengan ini melaporkan peristiwa pencatatan sipil sebagai berikut:',
        blocks,
        closing: CLOSING_FORM,
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
        closing: CLOSING_SPTJM,
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
        ],
        closing: CLOSING_SPTJM,
        signer: { role: 'Yang Menyatakan', name: nameOrDots(data.namaYangMenyatakan), nip: '' },
        leftSigner: { role: 'Pasangan (Suami/Istri)', name: nameOrDots(data.namaPasangan) },
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
          {
            kind: 'list',
            heading: 'Mengetahui:',
            items: ['Ketua RT', 'Ketua RW', 'Kepala Desa / Lurah'],
          },
        ],
        closing: CLOSING_SPTJM,
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
        title: `SPTJM ${jenis.toUpperCase()} BELUM TERCATAT`,
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
          ...tableBlock(
            'Daftar Anak:',
            ['No.', 'Nama Anak', 'No. Akta Kelahiran', 'SHDK'],
            anak.map((r, i) => [String(i + 1), ...r]),
            [8, 40, 32, 20],
          ),
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
        signer: { role: 'Suami', name: nameOrDots(data.namaSuami), nip: '' },
        leftSigner: { role: 'Istri', name: nameOrDots(data.namaIstri) },
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
            items: qa
              .filter(([, v]) => (v || '').toString().trim())
              .map(([q, v], i) => `${i + 1}. ${q}: ${v}`),
          },
          ...(data.catatanPetugas
            ? [{ kind: 'paragraph' as const, text: `Catatan petugas: ${data.catatanPetugas}` }]
            : []),
        ],
        closing: CLOSING_FORM,
        signer: { role: 'Pemohon', name: nameOrDots(data.namaLengkap), nip: '' },
        leftSigner: { role: 'Fasilitator' },
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
              row('Nama', data.namaLengkap, true),
              row('NIK', data.nik, true),
              row('Tempat, Tgl Lahir', ttl(data.tempatLahir, data.tanggalLahir)),
              row('Pekerjaan', data.pekerjaan),
              row('Alamat', data.alamat, true),
            ),
          },
          {
            kind: 'paragraph',
            text: data.isiPernyataan || '',
          },
        ],
        closing: CLOSING_SPTJM,
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
  }
}
