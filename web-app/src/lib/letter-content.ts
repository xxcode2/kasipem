import { format, parseISO } from 'date-fns';
import { id as idLocale } from 'date-fns/locale';
import { HeaderData, LetterType } from '../types';
import { LETTER_MAP } from './letter-definitions';

/**
 * SATU sumber kebenaran isi surat.
 * LetterBody (preview HTML) dan pdf-generator (jsPDF) sama-sama merender
 * struktur yang dikembalikan getLetterContent() — tidak ada dua template
 * yang bisa drift.
 */

export interface FieldRow {
  label: string;
  value: string;
}

export type ContentBlock =
  | { kind: 'fields'; rows: FieldRow[] }
  | { kind: 'paragraph'; text: string }
  | { kind: 'list'; heading?: string; items: string[] };

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
  intro: string;
  blocks: ContentBlock[];
  closing: string;
  placeDate: string;
  signer: SignerBlock;
  /** Blok tanda tangan kiri (khusus SKTM: Mengetahui, Ketua RW). */
  leftSigner?: { prefix?: string; role: string };
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

function resolveSigner(type: LetterType, header: HeaderData): SignerBlock {
  const desa = header.namaDesa || 'Kasomalang Kulon';
  // Pisah KK: DOCX ditandatangani a.n. Kepala Desa oleh Sekretaris Desa.
  if (type === 'pisah_kk' || header.penandatanganDefault === 'sekdes') {
    return {
      prefix: `a.n. Kepala Desa ${desa}`,
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
  const placeDate = `${desa}, ${formatDateSurat(data.tanggalSurat)}`;
  const signer = resolveSigner(type, header);
  const introKades =
    `Yang bertanda tangan di bawah ini Kepala Desa ${desa}, Kecamatan ${kecamatan}, ` +
    `Kabupaten ${kabupaten}, menerangkan dengan sesungguhnya bahwa:`;

  const base = {
    title: def.title,
    nomor,
    placeDate,
    signer,
  };

  switch (type) {
    case 'domisili': {
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
      const keperluan = (data.keperluan || '').trim();
      const tujuan = (data.tujuan || '').trim();
      const tujuanText = tujuan ? ` di ${tujuan}` : '';
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
  }
}
