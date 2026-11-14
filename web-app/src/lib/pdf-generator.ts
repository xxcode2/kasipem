import { jsPDF } from 'jspdf';
import { HeaderData, LetterData, LetterType } from '../types';
import {
  ContentBlock,
  LetterContent,
  formatDateSurat,
  getLetterContent,
} from './letter-content';

/**
 * SATU-SATUNYA generator PDF (pdf-generator-v2.ts dihapus).
 * Merender struktur yang sama dengan preview (getLetterContent) — satu
 * sumber kebenaran, jadi PDF tidak bisa kosong untuk satu jenis pun.
 *
 * Fase 2: blok 'table' (daftar anggota/anak/kolektif, header berulang
 * tiap halaman), kop Disdukcapil teks saja untuk formulir seri F,
 * hideNomor untuk formulir yang tidak bernomor, multi-halaman untuk
 * formulir panjang (F-1.01, F-2.01, F-1.03, PBI Asesmen).
 *
 * Fase 3: kop DINAMIS per penandatangan (kopFor di letter-content.ts —
 * fungsi yang sama dengan preview); font A4: isi 12pt, judul 15pt bold,
 * teks kop 12pt (baris desa 14pt); logo hanya dimuat bila varian kop
 * membutuhkannya; bila logo gagal dimuat → kop teks saja (jangan crash,
 * jangan PDF kosong).
 */

export interface GeneratePdfOptions {
  letterType: LetterType;
  headerData: HeaderData;
  letterData: LetterData;
  /** Untuk pengujian: pemuat logo kustom. Default: fetch dari URL publik. */
  logoLoader?: (src: string) => Promise<LogoImage | null>;
}

export interface LogoImage {
  dataUrl: string;
  format: 'JPEG' | 'PNG';
  width: number;
  height: number;
}

const A4_WIDTH = 210;
const MARGIN_LEFT = 20;
const MARGIN_RIGHT = 20;
const MARGIN_TOP = 15;
const CONTENT_WIDTH = A4_WIDTH - MARGIN_LEFT - MARGIN_RIGHT;
const PAGE_BOTTOM = 282; // 297 - 15

const LABEL_X = MARGIN_LEFT;
const COLON_X = MARGIN_LEFT + 52;
const VALUE_X = MARGIN_LEFT + 56;
const VALUE_WIDTH = CONTENT_WIDTH - 56;

async function defaultLogoLoader(src: string): Promise<LogoImage | null> {
  try {
    const res = await fetch(src);
    if (!res.ok) return null;
    const blob = await res.blob();
    const dataUrl = await new Promise<string>((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => resolve(reader.result as string);
      reader.onerror = () => reject(new Error('FileReader failed'));
      reader.readAsDataURL(blob);
    });
    const img = await new Promise<HTMLImageElement | null>((resolve) => {
      const el = new Image();
      el.onload = () => resolve(el);
      el.onerror = () => resolve(null);
      el.src = dataUrl;
    });
    if (!img || !img.naturalWidth || !img.naturalHeight) return null;

    // Downscale ke maks 400px + re-encode JPEG: jsPDF menanam PNG mentah
    // sebagai bitmap (1920x2404 -> ~18 MB per PDF). Dengan JPEG kecil,
    // PDF jadi puluhan KB. Logo upload pengguna ikut diperkecil di sini.
    try {
      const maxDim = 400;
      const scale = Math.min(1, maxDim / Math.max(img.naturalWidth, img.naturalHeight));
      const w = Math.max(1, Math.round(img.naturalWidth * scale));
      const h = Math.max(1, Math.round(img.naturalHeight * scale));
      const canvas = document.createElement('canvas');
      canvas.width = w;
      canvas.height = h;
      const ctx = canvas.getContext('2d');
      if (ctx) {
        ctx.fillStyle = '#ffffff';
        ctx.fillRect(0, 0, w, h);
        ctx.drawImage(img, 0, 0, w, h);
        return { dataUrl: canvas.toDataURL('image/jpeg', 0.85), format: 'JPEG', width: w, height: h };
      }
    } catch {
      // Lanjut ke fallback di bawah.
    }
    const format: 'JPEG' | 'PNG' = blob.type.includes('png') ? 'PNG' : 'JPEG';
    return { dataUrl, format, width: img.naturalWidth, height: img.naturalHeight };
  } catch {
    // Fallback: kop teks saja, jangan crash.
    return null;
  }
}

function ensureSpace(doc: jsPDF, y: number, needed: number): number {
  if (y + needed > PAGE_BOTTOM) {
    doc.addPage();
    return MARGIN_TOP;
  }
  return y;
}

function addTextLines(
  doc: jsPDF,
  lines: string[],
  x: number,
  y: number,
  opts?: { align?: 'left' | 'center' | 'justify'; maxWidth?: number },
): number {
  const lineHeight = 5.5;
  doc.text(lines, x, y, {
    align: opts?.align ?? 'left',
    maxWidth: opts?.maxWidth,
  });
  return y + lines.length * lineHeight;
}

function addParagraph(doc: jsPDF, text: string, y: number): number {
  doc.setFont('Times', 'normal');
  doc.setFontSize(12);
  const lines = doc.splitTextToSize(text, CONTENT_WIDTH);
  y = ensureSpace(doc, y, lines.length * 5.5 + 2);
  return addTextLines(doc, lines, MARGIN_LEFT, y, { align: 'justify', maxWidth: CONTENT_WIDTH }) + 2;
}

function addFields(doc: jsPDF, rows: { label: string; value: string }[], y: number): number {
  doc.setFont('Times', 'normal');
  doc.setFontSize(12);
  for (const r of rows) {
    const valueLines = doc.splitTextToSize(r.value || '', VALUE_WIDTH);
    y = ensureSpace(doc, y, valueLines.length * 5.5);
    doc.text(r.label, LABEL_X, y);
    doc.text(':', COLON_X, y);
    doc.text(valueLines, VALUE_X, y);
    y += valueLines.length * 5.5;
  }
  return y + 2;
}

/** Render blok tabel (jsPDF): header tebal + garis grid, font kecil, header berulang tiap halaman. */
function addTable(
  doc: jsPDF,
  block: Extract<ContentBlock, { kind: 'table' }>,
  y: number,
): number {
  const fontSize = 8;
  const lineHeight = 4.2;
  const cellPad = 1.2;
  const weights = block.widths && block.widths.length === block.headers.length
    ? block.widths
    : block.headers.map(() => 100 / block.headers.length);
  const totalW = weights.reduce((a, b) => a + b, 0) || 1;
  const colWidths = weights.map((w) => (w / totalW) * CONTENT_WIDTH);
  const colX: number[] = [];
  colWidths.reduce((acc, w) => {
    colX.push(acc);
    return acc + w;
  }, MARGIN_LEFT);

  const drawHeader = (yy: number): number => {
    doc.setFont('Times', 'bold');
    doc.setFontSize(fontSize);
    const headerLines = block.headers.map((h, i) =>
      doc.splitTextToSize(h, colWidths[i] - cellPad * 2),
    );
    const h = Math.max(...headerLines.map((ls) => ls.length)) * lineHeight + cellPad * 2;
    doc.setFillColor(240, 240, 240);
    doc.rect(MARGIN_LEFT, yy, CONTENT_WIDTH, h, 'F');
    doc.setDrawColor(0, 0, 0);
    doc.setLineWidth(0.3);
    doc.rect(MARGIN_LEFT, yy, CONTENT_WIDTH, h, 'S');
    block.headers.forEach((_, i) => {
      doc.text(headerLines[i], colX[i] + cellPad, yy + cellPad + lineHeight * 0.8);
      if (i > 0) doc.line(colX[i], yy, colX[i], yy + h);
    });
    return yy + h;
  };

  // Header butuh ruang; bila tidak muat, pindah halaman dulu.
  y = ensureSpace(doc, y, lineHeight + cellPad * 2 + 2);
  y = drawHeader(y);

  doc.setFont('Times', 'normal');
  doc.setFontSize(fontSize);
  for (const r of block.rows) {
    const cellLines = r.map((cell, i) =>
      doc.splitTextToSize(cell || '', colWidths[i] - cellPad * 2),
    );
    const rowH = Math.max(...cellLines.map((ls) => ls.length)) * lineHeight + cellPad * 2;
    if (y + rowH > PAGE_BOTTOM) {
      doc.addPage();
      y = MARGIN_TOP;
      y = drawHeader(y);
      doc.setFont('Times', 'normal');
      doc.setFontSize(fontSize);
    }
    doc.setDrawColor(0, 0, 0);
    doc.setLineWidth(0.3);
    doc.rect(MARGIN_LEFT, y, CONTENT_WIDTH, rowH, 'S');
    r.forEach((_, i) => {
      doc.text(cellLines[i], colX[i] + cellPad, y + cellPad + lineHeight * 0.8);
      if (i > 0) doc.line(colX[i], y, colX[i], y + rowH);
    });
    y += rowH;
  }
  return y + 3;
}

function addBlock(doc: jsPDF, block: ContentBlock, y: number): number {
  if (block.kind === 'fields') return addFields(doc, block.rows, y);
  if (block.kind === 'table') return addTable(doc, block, y);
  if (block.kind === 'list') {
    if (block.heading) y = addParagraph(doc, block.heading, y);
    for (const item of block.items) {
      y = addParagraph(doc, `- ${item}`, y);
    }
    return y;
  }
  // paragraph — dukung baris baru (\n) seperti "Kepada Yth.\n... \ndi tempat"
  let yy = y;
  for (const part of block.text.split('\n')) {
    yy = addParagraph(doc, part, yy);
  }
  return yy;
}

/** Teks kop desa (dipakai semua varian kop desa): baris desa 14pt, baris di atasnya 12pt. */
function addKopText(doc: jsPDF, header: HeaderData, y: number): number {
  const centerX = A4_WIDTH / 2;
  doc.setFont('Times', 'bold');
  doc.setFontSize(12);
  doc.text(`PEMERINTAH ${(header.namaKabupaten || 'Subang').toUpperCase()}`, centerX, y + 5, { align: 'center' });
  doc.text(`KECAMATAN ${(header.namaKecamatan || 'Kasomalang').toUpperCase()}`, centerX, y + 10, { align: 'center' });
  doc.setFontSize(14);
  doc.text(`DESA ${(header.namaDesa || 'Kasomalang Kulon').toUpperCase()}`, centerX, y + 15, { align: 'center' });
  doc.setFont('Times', 'normal');
  doc.setFontSize(11);
  doc.text(`Alamat: ${header.alamatKantor}`, centerX, y + 20, { align: 'center' });
  return y + 26;
}

function addHeader(
  doc: jsPDF,
  header: HeaderData,
  content: LetterContent,
  logos: { garuda: LogoImage | null; subang: LogoImage | null },
): number {
  let y = MARGIN_TOP;
  const centerX = A4_WIDTH / 2;

  if (content.kopVariant === 'disdukcapil') {
    // Kop Disdukcapil Subang — TEKS saja, tanpa logo desa.
    doc.setFont('Times', 'bold');
    doc.setFontSize(12);
    doc.text('PEMERINTAH KABUPATEN SUBANG', centerX, y + 5, { align: 'center' });
    doc.text('DINAS KEPENDUDUKAN DAN PENCATATAN SIPIL', centerX, y + 10, { align: 'center' });
    doc.setFont('Times', 'normal');
    doc.setFontSize(11);
    doc.text('Jl. Mayjen Sutoyo S. No. 50 Subang — Telp. (0260) 411424', centerX, y + 15, { align: 'center' });
    y += 21;
  } else if (content.kopVariant === 'garuda-center') {
    // Ttd langsung Kepala Desa: logo Garuda SAJA di tengah paling atas
    // (lebar ~28 mm, proporsional), teks kop rata tengah di bawahnya.
    // Bila logo gagal dimuat → teks kop saja, jangan crash.
    if (logos.garuda) {
      const w = 28;
      const h = (w * logos.garuda.height) / logos.garuda.width;
      doc.addImage(logos.garuda.dataUrl, logos.garuda.format, centerX - w / 2, y, w, h);
      y += h + 2;
    }
    y = addKopText(doc, header, y);
  } else if (content.kopVariant === 'subang-left') {
    // Ttd a.n. Kades oleh Sekdes / ttd langsung Sekdes: logo Subang di
    // kiri, tanpa Garuda. Bila logo gagal dimuat → teks kop saja.
    const logoH = 20; // mm
    if (logos.subang) {
      const w = (logoH * logos.subang.width) / logos.subang.height;
      doc.addImage(logos.subang.dataUrl, logos.subang.format, MARGIN_LEFT, y, w, logoH);
    }
    y = addKopText(doc, header, y);
  } else {
    // Penandatangan warga (SPTJM/PBI/dll.): dua logo seperti sebelumnya.
    const logoH = 20; // mm
    // Logo Garuda (kiri) dan logo Subang (kanan), proporsional.
    if (logos.garuda) {
      const w = (logoH * logos.garuda.width) / logos.garuda.height;
      doc.addImage(logos.garuda.dataUrl, logos.garuda.format, MARGIN_LEFT, y, w, logoH);
    }
    if (logos.subang) {
      const w = (logoH * logos.subang.width) / logos.subang.height;
      doc.addImage(logos.subang.dataUrl, logos.subang.format, A4_WIDTH - MARGIN_RIGHT - w, y, w, logoH);
    }
    y = addKopText(doc, header, y);
  }

  // Garis ganda: tebal + tipis
  doc.setDrawColor(0, 0, 0);
  doc.setLineWidth(1.0);
  doc.line(MARGIN_LEFT, y, MARGIN_LEFT + CONTENT_WIDTH, y);
  y += 1.2;
  doc.setLineWidth(0.4);
  doc.line(MARGIN_LEFT, y, MARGIN_LEFT + CONTENT_WIDTH, y);

  return y + 8;
}

function addSignature(doc: jsPDF, content: LetterContent, y: number): number {
  const rightCenter = MARGIN_LEFT + CONTENT_WIDTH * 0.75;
  const leftCenter = MARGIN_LEFT + CONTENT_WIDTH * 0.25;
  const blockW = CONTENT_WIDTH * 0.45;
  doc.setFont('Times', 'normal');
  doc.setFontSize(12);

  y = ensureSpace(doc, y, 55);
  y += 4;

  // Blok kiri (khusus SKTM: Mengetahui, Ketua RW; F-1.05: Istri; dsb.)
  if (content.leftSigner) {
    const lx = leftCenter;
    let ly = y;
    if (content.leftSigner.prefix) {
      doc.text(content.leftSigner.prefix, lx, ly, { align: 'center' });
      ly += 5.5;
    }
    const roleLines = doc.splitTextToSize(content.leftSigner.role, blockW);
    doc.text(roleLines, lx, ly, { align: 'center', maxWidth: blockW });
    ly += roleLines.length * 5.5;
    ly += 22;
    doc.setFont('Times', 'bold');
    doc.text(content.leftSigner.name || '____________________', lx, ly, {
      align: 'center',
      maxWidth: blockW,
    });
    doc.setFont('Times', 'normal');
  }

  // Blok kanan (penandatangan)
  let ry = y;
  doc.text(content.placeDate, rightCenter, ry, { align: 'center' });
  ry += 5.5;
  if (content.signer.prefix) {
    const prefixLines = doc.splitTextToSize(content.signer.prefix, blockW);
    doc.text(prefixLines, rightCenter, ry, { align: 'center', maxWidth: blockW });
    ry += prefixLines.length * 5.5;
  }
  const roleLines = doc.splitTextToSize(content.signer.role, blockW);
  doc.text(roleLines, rightCenter, ry, { align: 'center', maxWidth: blockW });
  ry += roleLines.length * 5.5;
  ry += 22;
  doc.setFont('Times', 'bold');
  const nameLines = doc.splitTextToSize(content.signer.name, blockW);
  doc.text(nameLines, rightCenter, ry, { align: 'center', maxWidth: blockW });
  ry += (nameLines.length - 1) * 5.5;
  // NIP hanya dirender bila ada isinya — pejabat desa ini tidak punya NIP.
  if (content.signer.nip) {
    ry += 5.5;
    doc.setFont('Times', 'normal');
    doc.text(`NIP: ${content.signer.nip}`, rightCenter, ry, { align: 'center' });
  }

  return ry + 5;
}

export async function generatePdf(options: GeneratePdfOptions): Promise<Blob> {
  const { letterType, headerData, letterData } = options;
  const loader = options.logoLoader ?? defaultLogoLoader;

  const doc = new jsPDF({ orientation: 'portrait', unit: 'mm', format: 'a4' });
  const content = getLetterContent(letterType, headerData, letterData);

  // Muat logo hanya bila varian kop membutuhkannya; formulir Disdukcapil
  // kop-nya teks saja. Logo gagal dimuat → kop teks saja (jangan crash).
  let garuda: LogoImage | null = null;
  let subang: LogoImage | null = null;
  const variant = content.kopVariant;
  if (variant === 'garuda-center' || variant === 'dua-logo') {
    garuda = await loader(headerData.logoGaruda || '/kop/kades.jpg');
  }
  if (variant === 'subang-left' || variant === 'dua-logo') {
    subang = await loader(headerData.logoSubang || '/kop/sekdes.jpg');
  }

  let y = addHeader(doc, headerData, content, { garuda, subang });

  // Judul (+ nomor, kecuali formulir yang memang tidak bernomor)
  doc.setFont('Times', 'bold');
  doc.setFontSize(15);
  const titleLines = doc.splitTextToSize(content.title, CONTENT_WIDTH);
  doc.text(titleLines, A4_WIDTH / 2, y, { align: 'center' });
  y += titleLines.length * 6;
  if (!content.hideNomor) {
    doc.setFont('Times', 'normal');
    doc.setFontSize(12);
    doc.text(`Nomor : ${content.nomor || '............................................'}`, A4_WIDTH / 2, y, {
      align: 'center',
    });
    y += 6;
  }
  y += 4;

  // Pembuka
  if (content.intro) y = addParagraph(doc, content.intro, y);

  // Isi
  for (const block of content.blocks) {
    y = addBlock(doc, block, y);
  }

  // Penutup
  y = addParagraph(doc, content.closing, y);

  // Tanda tangan
  addSignature(doc, content, y + 4);

  return doc.output('blob');
}

/** Re-export untuk kebutuhan pengujian / pemakaian lain. */
export { formatDateSurat };
