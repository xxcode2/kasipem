import { jsPDF } from 'jspdf';
import { HeaderData, LetterData, LetterType } from '../types';
import {
  ContentBlock,
  LetterContent,
  SignerBlock,
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
 *
 * Fase 5 (§1A): geometri landscape — semua fungsi menerima `Geom`
 * (bukan konstanta module-level). Landscape: halaman 297x210, isi di
 * area kiri ~62% lebar (contentW ≈ 164 mm); judul/nomor/ttd rata tengah
 * DALAM area isi. Kop 'tabel' (kematian): tabel kecil 3 baris tanpa
 * logo dan tanpa garis ganda. Grid penandatangan (§1C) + penutup
 * dilewati bila kosong (§1B).
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

/** Geometri halaman (Fase 5 §1A). Portrait = konstanta lama persis. */
export interface Geom {
  pageW: number;
  pageH: number;
  marginL: number;
  contentW: number;
  pageBottom: number;
  labelX: number;
  colonX: number;
  valueX: number;
  valueW: number;
}

const PORTRAIT_GEOM: Geom = {
  pageW: 210,
  pageH: 297,
  marginL: 20,
  contentW: 170,
  pageBottom: 282,
  labelX: 20,
  colonX: 72,
  valueX: 76,
  valueW: 114,
};

const LANDSCAPE_GEOM: Geom = {
  pageW: 297,
  pageH: 210,
  marginL: 20,
  // Area isi kiri ~62% lebar halaman (0.62*297 - 20 ≈ 164 mm).
  contentW: 0.62 * 297 - 20,
  pageBottom: 195,
  labelX: 20,
  colonX: 72,
  valueX: 76,
  valueW: 0.62 * 297 - 20 - 56,
};

const MARGIN_TOP = 15;

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

function ensureSpace(doc: jsPDF, g: Geom, y: number, needed: number): number {
  if (y + needed > g.pageBottom) {
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

function addParagraph(doc: jsPDF, g: Geom, text: string, y: number): number {
  doc.setFont('Times', 'normal');
  doc.setFontSize(12);
  const lines = doc.splitTextToSize(text, g.contentW);
  y = ensureSpace(doc, g, y, lines.length * 5.5 + 2);
  return addTextLines(doc, lines, g.marginL, y, { align: 'justify', maxWidth: g.contentW }) + 2;
}

function addFields(doc: jsPDF, g: Geom, rows: { label: string; value: string }[], y: number): number {
  doc.setFont('Times', 'normal');
  doc.setFontSize(12);
  for (const r of rows) {
    const valueLines = doc.splitTextToSize(r.value || '', g.valueW);
    y = ensureSpace(doc, g, y, valueLines.length * 5.5);
    doc.text(r.label, g.labelX, y);
    doc.text(':', g.colonX, y);
    doc.text(valueLines, g.valueX, y);
    y += valueLines.length * 5.5;
  }
  return y + 2;
}

/** Render blok tabel (jsPDF): header tebal + garis grid, font kecil, header berulang tiap halaman. */
function addTable(
  doc: jsPDF,
  g: Geom,
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
  const colWidths = weights.map((w) => (w / totalW) * g.contentW);
  const colX: number[] = [];
  colWidths.reduce((acc, w) => {
    colX.push(acc);
    return acc + w;
  }, g.marginL);

  const drawHeader = (yy: number): number => {
    doc.setFont('Times', 'bold');
    doc.setFontSize(fontSize);
    const headerLines = block.headers.map((h, i) =>
      doc.splitTextToSize(h, colWidths[i] - cellPad * 2),
    );
    const h = Math.max(...headerLines.map((ls) => ls.length)) * lineHeight + cellPad * 2;
    doc.setFillColor(240, 240, 240);
    doc.rect(g.marginL, yy, g.contentW, h, 'F');
    doc.setDrawColor(0, 0, 0);
    doc.setLineWidth(0.3);
    doc.rect(g.marginL, yy, g.contentW, h, 'S');
    block.headers.forEach((_, i) => {
      doc.text(headerLines[i], colX[i] + cellPad, yy + cellPad + lineHeight * 0.8);
      if (i > 0) doc.line(colX[i], yy, colX[i], yy + h);
    });
    return yy + h;
  };

  // Header butuh ruang; bila tidak muat, pindah halaman dulu.
  y = ensureSpace(doc, g, y, lineHeight + cellPad * 2 + 2);
  y = drawHeader(y);

  doc.setFont('Times', 'normal');
  doc.setFontSize(fontSize);
  for (const r of block.rows) {
    const cellLines = r.map((cell, i) =>
      doc.splitTextToSize(cell || '', colWidths[i] - cellPad * 2),
    );
    const rowH = Math.max(...cellLines.map((ls) => ls.length)) * lineHeight + cellPad * 2;
    if (y + rowH > g.pageBottom) {
      doc.addPage();
      y = MARGIN_TOP;
      y = drawHeader(y);
      doc.setFont('Times', 'normal');
      doc.setFontSize(fontSize);
    }
    doc.setDrawColor(0, 0, 0);
    doc.setLineWidth(0.3);
    doc.rect(g.marginL, y, g.contentW, rowH, 'S');
    r.forEach((_, i) => {
      doc.text(cellLines[i], colX[i] + cellPad, y + cellPad + lineHeight * 0.8);
      if (i > 0) doc.line(colX[i], y, colX[i], y + rowH);
    });
    y += rowH;
  }
  return y + 3;
}

function addBlock(doc: jsPDF, g: Geom, block: ContentBlock, y: number): number {
  if (block.kind === 'fields') return addFields(doc, g, block.rows, y);
  if (block.kind === 'table') return addTable(doc, g, block, y);
  if (block.kind === 'list') {
    if (block.heading) y = addParagraph(doc, g, block.heading, y);
    for (const item of block.items) {
      y = addParagraph(doc, g, `- ${item}`, y);
    }
    return y;
  }
  // paragraph — dukung baris baru (\n) seperti "Kepada Yth.\n... \ndi tempat"
  let yy = y;
  for (const part of block.text.split('\n')) {
    yy = addParagraph(doc, g, part, yy);
  }
  return yy;
}

/** Teks kop desa (dipakai semua varian kop desa): baris desa 14pt, baris di atasnya 12pt. */
function addKopText(doc: jsPDF, g: Geom, header: HeaderData, y: number): number {
  const centerX = g.marginL + g.contentW / 2;
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
  g: Geom,
  header: HeaderData,
  content: LetterContent,
  logos: { garuda: LogoImage | null; subang: LogoImage | null },
): number {
  let y = MARGIN_TOP;
  const centerX = g.marginL + g.contentW / 2;

  if (content.kopVariant === 'tabel') {
    // Kop kematian (Fase 5): TANPA logo, tabel kecil 3 baris di kiri atas
    // dengan garis vertikal di kiri. Tanpa garis ganda di bawahnya.
    doc.setFont('Times', 'normal');
    doc.setFontSize(12);
    const rows: [string, string][] = [
      ['Pemerintah/Kota/Kab', header.namaKabupaten || 'Subang'],
      ['Kecamatan', header.namaKecamatan || 'Kasomalang'],
      ['Desa/Kelurahan', header.namaDesa || 'Kasomalang Kulon'],
    ];
    for (const [label, value] of rows) {
      doc.text(label, g.labelX, y + 5);
      doc.text(':', g.colonX, y + 5);
      doc.text(value, g.valueX, y + 5);
      y += 5.5;
    }
    // Garis vertikal di kiri tabel.
    doc.setDrawColor(0, 0, 0);
    doc.setLineWidth(0.8);
    doc.line(g.marginL - 3, MARGIN_TOP, g.marginL - 3, y);
    return y + 6;
  }

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
    y = addKopText(doc, g, header, y);
  } else if (content.kopVariant === 'subang-left') {
    // Ttd a.n. Kades oleh Sekdes / ttd langsung Sekdes: logo Subang di
    // kiri, tanpa Garuda. Bila logo gagal dimuat → teks kop saja.
    const logoH = 20; // mm
    if (logos.subang) {
      const w = (logoH * logos.subang.width) / logos.subang.height;
      doc.addImage(logos.subang.dataUrl, logos.subang.format, g.marginL, y, w, logoH);
    }
    y = addKopText(doc, g, header, y);
  } else {
    // Penandatangan warga (SPTJM/PBI/dll.): dua logo seperti sebelumnya.
    const logoH = 20; // mm
    // Logo Garuda (kiri) dan logo Subang (kanan), proporsional.
    if (logos.garuda) {
      const w = (logoH * logos.garuda.width) / logos.garuda.height;
      doc.addImage(logos.garuda.dataUrl, logos.garuda.format, g.marginL, y, w, logoH);
    }
    if (logos.subang) {
      const w = (logoH * logos.subang.width) / logos.subang.height;
      doc.addImage(logos.subang.dataUrl, logos.subang.format, g.marginL + g.contentW - w, y, w, logoH);
    }
    y = addKopText(doc, g, header, y);
  }

  // Garis ganda: tebal + tipis
  doc.setDrawColor(0, 0, 0);
  doc.setLineWidth(1.0);
  doc.line(g.marginL, y, g.marginL + g.contentW, y);
  y += 1.2;
  doc.setLineWidth(0.4);
  doc.line(g.marginL, y, g.marginL + g.contentW, y);

  return y + 8;
}

/** Tinggi (mm) satu sel tanda tangan: prefix? + role + spasi ttd 22mm + nama + sub? + NIP? */
function signerCellHeight(doc: jsPDF, g: Geom, cellW: number, s: SignerBlock): number {
  const maxW = Math.max(10, cellW - 4);
  doc.setFontSize(12);
  let h = 0;
  if (s.prefix) h += doc.splitTextToSize(s.prefix, maxW).length * 5.5;
  h += doc.splitTextToSize(s.role, maxW).length * 5.5;
  h += 22; // ruang tanda tangan
  h += doc.splitTextToSize(s.name, maxW).length * 5.5;
  if (s.sub) h += s.sub.split('\n').flatMap((part) => doc.splitTextToSize(part, maxW)).length * 5.5;
  if (s.nip) h += 5.5;
  return h;
}

/** Gambar satu sel tanda tangan pada titik tengah cx, mulai y. */
function drawSignerCell(doc: jsPDF, g: Geom, cx: number, cellW: number, s: SignerBlock, y: number): number {
  const maxW = Math.max(10, cellW - 4);
  doc.setFont('Times', 'normal');
  doc.setFontSize(12);
  let yy = y;
  if (s.prefix) {
    const lines = doc.splitTextToSize(s.prefix, maxW);
    doc.text(lines, cx, yy, { align: 'center', maxWidth: maxW });
    yy += lines.length * 5.5;
  }
  const roleLines = doc.splitTextToSize(s.role, maxW);
  doc.text(roleLines, cx, yy, { align: 'center', maxWidth: maxW });
  yy += roleLines.length * 5.5;
  yy += 22;
  doc.setFont('Times', 'bold');
  const nameLines = doc.splitTextToSize(s.name, maxW);
  doc.text(nameLines, cx, yy, { align: 'center', maxWidth: maxW });
  yy += nameLines.length * 5.5;
  doc.setFont('Times', 'normal');
  if (s.sub) {
    for (const part of s.sub.split('\n')) {
      const subLines = doc.splitTextToSize(part, maxW);
      doc.text(subLines, cx, yy, { align: 'center', maxWidth: maxW });
      yy += subLines.length * 5.5;
    }
  }
  // NIP hanya dirender bila ada isinya — pejabat desa ini tidak punya NIP.
  if (s.nip) {
    doc.text(`NIP: ${s.nip}`, cx, yy, { align: 'center' });
    yy += 5.5;
  }
  return yy;
}

function addSignature(doc: jsPDF, g: Geom, content: LetterContent, y: number): number {
  const rightCenter = g.marginL + g.contentW * 0.75;
  const leftCenter = g.marginL + g.contentW * 0.25;
  const blockW = g.contentW * 0.45;

  // ---- Grid penandatangan (Fase 5 §1C) ----
  if (content.signers && content.signers.length > 0) {
    const cols = content.signerColumns || 2;
    const cellW = g.contentW / cols;
    y = ensureSpace(doc, g, y, 55);
    y += 4;
    // Baris tempat/tanggal SATU kali di atas grid, rata kanan area isi.
    doc.setFont('Times', 'normal');
    doc.setFontSize(12);
    doc.text(content.placeDate, g.marginL + g.contentW, y, { align: 'right' });
    y += 5.5;
    for (let r = 0; r < content.signers.length; r += cols) {
      const rowSigners = content.signers.slice(r, r + cols);
      const rowH = Math.max(...rowSigners.map((s) => signerCellHeight(doc, g, cellW, s)));
      y = ensureSpace(doc, g, y, rowH + 4);
      rowSigners.forEach((s, i) => {
        const cx = g.marginL + cellW * (i + 0.5);
        drawSignerCell(doc, g, cx, cellW, s, y);
      });
      y += rowH + 8;
    }
    return y;
  }

  doc.setFont('Times', 'normal');
  doc.setFontSize(12);

  y = ensureSpace(doc, g, y, 55);
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

  const content = getLetterContent(letterType, headerData, letterData);
  const landscape = content.landscape === true;
  const g: Geom = landscape ? LANDSCAPE_GEOM : PORTRAIT_GEOM;

  const doc = new jsPDF({ orientation: landscape ? 'landscape' : 'portrait', unit: 'mm', format: 'a4' });

  // Muat logo hanya bila varian kop membutuhkannya; formulir Disdukcapil
  // kop-nya teks saja, kematian kop-nya tabel. Logo gagal dimuat → kop
  // teks saja (jangan crash).
  let garuda: LogoImage | null = null;
  let subang: LogoImage | null = null;
  const variant = content.kopVariant;
  if (variant === 'garuda-center' || variant === 'dua-logo') {
    garuda = await loader(headerData.logoGaruda || '/kop/kades.jpg');
  }
  if (variant === 'subang-left' || variant === 'dua-logo') {
    subang = await loader(headerData.logoSubang || '/kop/sekdes.jpg');
  }

  let y = addHeader(doc, g, headerData, content, { garuda, subang });

  // Judul (+ subtitle + nomor, kecuali formulir yang memang tidak bernomor).
  // Rata tengah DALAM area isi (bukan tengah halaman penuh) — penting
  // untuk landscape yang isinya hanya 62% lebar.
  const centerX = g.marginL + g.contentW / 2;
  if (!content.hideTitle) {
    doc.setFont('Times', 'bold');
    doc.setFontSize(15);
    const titleLines = doc.splitTextToSize(content.title, g.contentW);
    doc.text(titleLines, centerX, y, { align: 'center' });
    y += titleLines.length * 6;
  }
  if (content.subtitle) {
    doc.setFont('Times', 'bold');
    doc.setFontSize(12);
    doc.text(content.subtitle, centerX, y, { align: 'center' });
    y += 6;
  }
  if (!content.hideNomor) {
    if (content.nomorDisplay) {
      // Format nomor khusus (kematian): bold + underline.
      doc.setFont('Times', 'bold');
      doc.setFontSize(12);
      doc.text(content.nomorDisplay, centerX, y, { align: 'center' });
      const w = doc.getTextWidth(content.nomorDisplay);
      doc.setDrawColor(0, 0, 0);
      doc.setLineWidth(0.4);
      doc.line(centerX - w / 2, y + 1, centerX + w / 2, y + 1);
      y += 6;
    } else {
      doc.setFont('Times', 'normal');
      doc.setFontSize(12);
      doc.text(`Nomor : ${content.nomor || '............................................'}`, centerX, y, {
        align: 'center',
      });
      y += 6;
    }
  }
  y += 4;

  // Pembuka
  if (content.intro) y = addParagraph(doc, g, content.intro, y);

  // Isi
  for (const block of content.blocks) {
    y = addBlock(doc, g, block, y);
  }

  // Penutup — dilewati bila kosong (kematian: template tidak punya penutup).
  if (content.closing) y = addParagraph(doc, g, content.closing, y);

  // Tanda tangan
  addSignature(doc, g, content, y + 4);

  return doc.output('blob');
}

/** Re-export untuk kebutuhan pengujian / pemakaian lain. */
export { formatDateSurat };
