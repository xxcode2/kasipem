/**
 * PDF Generator v2 - Sesuai format dokumen asli dari file DOCX
 * Format mengikuti standar surat dinas Indonesia yang sebenarnya
 */

import { jsPDF } from 'jspdf';
import { format, parseISO } from 'date-fns';
import { id as idLocale } from 'date-fns/locale';
import { HeaderData, LetterData, LetterType } from '@/types';

interface GeneratePdfOptions {
  letterType: LetterType;
  headerData: HeaderData;
  letterData: LetterData;
}

// Konfigurasi halaman
const PAGE = {
  width: 210,
  height: 297,
  marginLeft: 20,
  marginRight: 20,
  marginTop: 15,
  marginBottom: 20,
};

const CONTENT_WIDTH = PAGE.width - PAGE.marginLeft - PAGE.marginRight;

// Format tanggal sesuai standar:
// TTL: DD-MM-YYYY (contoh: 12-05-1994)
// Surat: DD MMMM YYYY (contoh: 06 Juli 2026)
function formatDateTTL(date?: string): string {
  if (!date) return '';
  try {
    const parsed = parseISO(date);
    return format(parsed, 'dd-MM-yyyy', { locale: idLocale });
  } catch {
    return '';
  }
}

function formatDateSurat(date?: string): string {
  if (!date) return format(new Date(), 'dd MMMM yyyy', { locale: idLocale });
  try {
    return format(parseISO(date), 'dd MMMM yyyy', { locale: idLocale });
  } catch {
    return format(new Date(), 'dd MMMM yyyy', { locale: idLocale });
  }
}

function addHeaderKopSurat(doc: jsPDF, headerData: HeaderData): number {
  let y = PAGE.marginTop;

  // Kotak header dengan border
  doc.setDrawColor(0, 0, 0);
  doc.setLineWidth(0.5);
  doc.rect(PAGE.marginLeft, y, CONTENT_WIDTH, 30);

  // Isi header
  doc.setFont('Arial', 'bold');
  doc.setFontSize(11);
  doc.text(`PEMERINTAH ${headerData.namaKabupaten.toUpperCase()}`, PAGE.width / 2, y + 4, { align: 'center' });
  
  doc.setFontSize(10);
  doc.text(`${headerData.namaKecamatan.toUpperCase()}`, PAGE.width / 2, y + 8.5, { align: 'center' });
  
  doc.setFont('Arial', 'bold');
  doc.setFontSize(11);
  doc.text(`DESA/KELURAHAN ${headerData.namaDesa.toUpperCase()}`, PAGE.width / 2, y + 13, { align: 'center' });
  
  doc.setFont('Arial', 'normal');
  doc.setFontSize(9);
  doc.text(`${headerData.alamatKantor}`, PAGE.width / 2, y + 17, { align: 'center' });

  // Garis pemisah ganda (tebal + tipis)
  y += 32;
  doc.setLineWidth(0.8);
  doc.line(PAGE.marginLeft, y, PAGE.marginLeft + CONTENT_WIDTH, y);
  y += 1.5;
  doc.setLineWidth(0.3);
  doc.line(PAGE.marginLeft, y, PAGE.marginLeft + CONTENT_WIDTH, y);

  return y + 4;
}

function addJudulSurat(doc: jsPDF, judul: string, y: number): number {
  doc.setFont('Arial', 'bold');
  doc.setFontSize(12);
  doc.text(judul, PAGE.width / 2, y, { align: 'center' });
  
  // Garis bawah judul
  y += 3;
  doc.setLineWidth(0.3);
  doc.line(PAGE.marginLeft + 35, y, PAGE.marginLeft + CONTENT_WIDTH - 35, y);
  
  return y + 4;
}

function addNomorSurat(doc: jsPDF, nomor: string, y: number): number {
  doc.setFont('Arial', 'normal');
  doc.setFontSize(10);
  doc.text(`Nomor : ${nomor}`, PAGE.marginLeft, y);
  return y + 6;
}

function addMultilineText(doc: jsPDF, text: string, x: number, y: number, maxWidth: number): number {
  doc.setFont('Arial', 'normal');
  doc.setFontSize(11);
  const lines = doc.splitTextToSize(text, maxWidth);
  doc.text(lines, x, y, { align: 'justify' });
  return y + lines.length * 4.5 + 2;
}

function addDataField(doc: jsPDF, label: string, value: string, x: number, y: number, width: number): number {
  doc.setFont('Arial', 'normal');
  doc.setFontSize(11);
  doc.text(`${label.padEnd(25)}: ${value}`, x, y);
  return y + 4;
}

function addSignatureBlock(doc: jsPDF, headerData: HeaderData, y: number): void {
  const leftX = PAGE.marginLeft;
  const rightX = PAGE.marginLeft + CONTENT_WIDTH / 2 + 10;
  
  // Kolom kiri: Mengetahui (opsional)
  doc.setFont('Arial', 'normal');
  doc.setFontSize(10);
  doc.text('Mengetahui,', leftX, y);
  doc.setLineWidth(0.3);
  doc.line(leftX, y + 18, leftX + 50, y + 18);
  doc.setFont('Arial', 'normal');
  doc.setFontSize(9);
  doc.text('Ketua RW / Sekretaris', leftX, y + 20);
  
  // Kolom kanan: Pejabat yang menandatangani
  doc.setFont('Arial', 'normal');
  doc.setFontSize(10);
  doc.text(`${formatDateSurat()}`, rightX, y - 2, { align: 'center' });
  
  doc.setFont('Arial', 'normal');
  doc.setFontSize(11);
  doc.text(`${headerData.atnPejabat ? 'a.n. ' : ''}${headerData.jabatanPejabat}`, rightX, y + 8, { align: 'center' });
  
  // Ruang tanda tangan
  doc.setLineWidth(0.3);
  doc.line(rightX - 25, y + 18, rightX + 25, y + 18);
  
  // Nama pejabat (bold dan underline)
  doc.setFont('Arial', 'bold');
  doc.setFontSize(11);
  doc.text(headerData.namaPejabat, rightX, y + 22, { align: 'center' });
  
  // Garis bawah nama
  doc.setLineWidth(0.3);
  doc.line(rightX - 30, y + 23.5, rightX + 30, y + 23.5);
  
  // NIP
  doc.setFont('Arial', 'normal');
  doc.setFontSize(9);
  doc.text(`NIP: ${headerData.nipPejabat}`, rightX, y + 26, { align: 'center' });
}

// ===== TEMPLATE UNTUK SETIAP JENIS SURAT =====

function generateDomisiliPdf(doc: jsPDF, headerData: HeaderData, data: any): void {
  let y = addHeaderKopSurat(doc, headerData);
  y = addJudulSurat(doc, 'SURAT KETERANGAN DOMISILI', y);
  
  // Nomor surat format: N/...../Pem/TAHUN
  const tahun = new Date().getFullYear();
  const nomorUrut = data.nomorSurat || '1';
  const nomorSurat = `${nomorUrut}/......../Pem/${tahun}`;
  y = addNomorSurat(doc, nomorSurat, y);

  // Pembuka surat
  y = addMultilineText(
    doc,
    `Yang bertanda tangan di bawah ini, ${headerData.jabatanPejabat} ${headerData.namaDesa}, dengan ini menerangkan bahwa:`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );
  y += 3;

  // Data pemohon
  y = addDataField(doc, 'Nama', data.namaLengkap, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'NIK', data.nik, PAGE.marginLeft, y, CONTENT_WIDTH);
  
  if (data.jenisKelamin) {
    y = addDataField(doc, 'Jenis Kelamin', data.jenisKelamin, PAGE.marginLeft, y, CONTENT_WIDTH);
  }

  if (data.tempatLahir && data.tanggalLahir) {
    const tglLahirFormatted = formatDateTTL(data.tanggalLahir);
    y = addDataField(doc, 'Tempat/Tanggal Lahir', `${data.tempatLahir}, ${tglLahirFormatted}`, PAGE.marginLeft, y, CONTENT_WIDTH);
  }

  if (data.pekerjaan) {
    y = addDataField(doc, 'Pekerjaan', data.pekerjaan, PAGE.marginLeft, y, CONTENT_WIDTH);
  }

  y = addDataField(doc, 'Alamat di KTP', data.alamatAsal, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'RT/RW', `${data.rt}/${data.rw}`, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'Alamat Domisili Saat Ini', data.alamatDomisili, PAGE.marginLeft, y, CONTENT_WIDTH);

  y += 3;

  // Pernyataan inti
  y = addMultilineText(
    doc,
    `Berdasarkan pengecekan lapangan dan arsip desa bahwa yang tersebut di atas benar adalah penduduk sah dan bertempat tinggal di Desa/Kelurahan ${headerData.namaDesa}. Surat keterangan ini diberikan untuk keperluan: ${data.keperluanSurat}`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );

  y += 5;

  // Penutup
  y = addMultilineText(
    doc,
    `Demikian surat keterangan ini dibuat untuk dipergunakan sesuai keperluannya.`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );

  // Tanda tangan
  addSignatureBlock(doc, headerData, y + 8);
}

function generateBelumMenikahPdf(doc: jsPDF, headerData: HeaderData, data: any): void {
  let y = addHeaderKopSurat(doc, headerData);
  y = addJudulSurat(doc, 'SURAT KETERANGAN BELUM MENIKAH', y);
  
  const tahun = new Date().getFullYear();
  const nomorUrut = data.nomorSurat || '1';
  const nomorSurat = `${nomorUrut}/......../Ks/${tahun}`;
  y = addNomorSurat(doc, nomorSurat, y);

  y = addMultilineText(
    doc,
    `Yang bertanda tangan di bawah ini, ${headerData.jabatanPejabat} ${headerData.namaDesa}, dengan ini menerangkan bahwa:`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );
  y += 3;

  // Data pemohon
  y = addDataField(doc, 'Nama', data.namaLengkap, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'NIK', data.nik, PAGE.marginLeft, y, CONTENT_WIDTH);
  
  if (data.jenisKelamin) {
    y = addDataField(doc, 'Jenis Kelamin', data.jenisKelamin, PAGE.marginLeft, y, CONTENT_WIDTH);
  }

  if (data.tempatLahir && data.tanggalLahir) {
    const tglLahirFormatted = formatDateTTL(data.tanggalLahir);
    y = addDataField(doc, 'Tempat/Tanggal Lahir', `${data.tempatLahir}, ${tglLahirFormatted}`, PAGE.marginLeft, y, CONTENT_WIDTH);
  }

  if (data.pekerjaan) {
    y = addDataField(doc, 'Pekerjaan', data.pekerjaan, PAGE.marginLeft, y, CONTENT_WIDTH);
  }

  y = addDataField(doc, 'Alamat', data.alamatLengkap, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'RT/RW', `${data.rt}/${data.rw}`, PAGE.marginLeft, y, CONTENT_WIDTH);

  y += 3;

  // Pernyataan
  y = addMultilineText(
    doc,
    `Adalah benar bahwa yang bersangkutan sampai dengan saat ini belum pernah melakukan perkawinan yang tercatat pada kantor pencatat perkawinan/pelaporan sipil yang berwenang. Surat keterangan ini diberikan untuk keperluan: ${data.keperluan}`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );

  y += 5;

  y = addMultilineText(
    doc,
    `Demikian surat keterangan ini dibuat untuk dipergunakan sesuai keperluannya.`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );

  addSignatureBlock(doc, headerData, y + 8);
}

function generateTidakMampuPdf(doc: jsPDF, headerData: HeaderData, data: any): void {
  let y = addHeaderKopSurat(doc, headerData);
  y = addJudulSurat(doc, 'SURAT KETERANGAN TIDAK MAMPU', y);
  
  const tahun = new Date().getFullYear();
  const nomorUrut = data.nomorSurat || '1';
  const nomorSurat = `${nomorUrut}/......../Ks/${tahun}`;
  y = addNomorSurat(doc, nomorSurat, y);

  y = addMultilineText(
    doc,
    `Yang bertanda tangan di bawah ini, ${headerData.jabatanPejabat} ${headerData.namaDesa}, dengan ini menerangkan bahwa:`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );
  y += 3;

  y = addDataField(doc, 'Nama', data.namaLengkap, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'NIK', data.nik, PAGE.marginLeft, y, CONTENT_WIDTH);
  
  if (data.jenisKelamin) {
    y = addDataField(doc, 'Jenis Kelamin', data.jenisKelamin, PAGE.marginLeft, y, CONTENT_WIDTH);
  }

  if (data.tempatLahir && data.tanggalLahir) {
    const tglLahirFormatted = formatDateTTL(data.tanggalLahir);
    y = addDataField(doc, 'Tempat/Tanggal Lahir', `${data.tempatLahir}, ${tglLahirFormatted}`, PAGE.marginLeft, y, CONTENT_WIDTH);
  }

  if (data.pekerjaan) {
    y = addDataField(doc, 'Pekerjaan', data.pekerjaan, PAGE.marginLeft, y, CONTENT_WIDTH);
  }

  y = addDataField(doc, 'Alamat', data.alamatLengkap, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'RT/RW', `${data.rt}/${data.rw}`, PAGE.marginLeft, y, CONTENT_WIDTH);

  y += 3;

  y = addMultilineText(
    doc,
    `Adalah benar bahwa yang bersangkutan termasuk dalam kategori tidak mampu menurut kriteria yang berlaku di Desa/Kelurahan ${headerData.namaDesa}. Surat keterangan ini diberikan untuk keperluan: ${data.keperluan} di ${data.tujuanSekolah}`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );

  y += 5;

  y = addMultilineText(
    doc,
    `Demikian surat keterangan ini dibuat untuk dipergunakan sesuai keperluannya.`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );

  addSignatureBlock(doc, headerData, y + 8);
}

function generateBedaLuasPdf(doc: jsPDF, headerData: HeaderData, data: any): void {
  let y = addHeaderKopSurat(doc, headerData);
  y = addJudulSurat(doc, 'SURAT KETERANGAN BEDA LUAS TANAH', y);
  
  const tahun = new Date().getFullYear();
  const nomorUrut = data.nomorSurat || '1';
  const nomorSurat = `${nomorUrut}/......../PEM/${tahun}`;
  y = addNomorSurat(doc, nomorSurat, y);

  y = addMultilineText(
    doc,
    `Yang bertanda tangan di bawah ini, ${headerData.jabatanPejabat} ${headerData.namaDesa}, dengan ini menerangkan bahwa:`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );
  y += 3;

  y = addDataField(doc, 'Nama', data.namaLengkap, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'NIK', data.nik, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'Alamat', data.alamatLengkap, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'RT/RW', `${data.rt}/${data.rw}`, PAGE.marginLeft, y, CONTENT_WIDTH);

  y += 3;

  y = addMultilineText(
    doc,
    `Berdasarkan pengecekan di lapangan dan data yang ada di kantor Desa/Kelurahan, terdapat perbedaan luas tanah antara Sertifikat dan SPPT (Surat Pemberitahuan Pajak Tahunan), dengan rincian sebagai berikut:`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );
  y += 4;

  y = addDataField(doc, 'Nomor Sertifikat', data.nomorSertifikat, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'Luas Sertifikat', `${data.luasSertifikat} m²`, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'Nomor SPPT', data.nomorSPPT, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'Luas SPPT', `${data.luasSPPT} m²`, PAGE.marginLeft, y, CONTENT_WIDTH);

  y += 3;

  y = addMultilineText(
    doc,
    `Surat keterangan ini diberikan untuk keperluan administrasi pertanahan dan dapat digunakan sebagai dasar untuk melakukan perubahan data perpajakan tanah sesuai dengan peraturan yang berlaku.`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );

  y += 5;

  y = addMultilineText(
    doc,
    `Demikian surat keterangan ini dibuat untuk dipergunakan sesuai keperluannya.`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );

  addSignatureBlock(doc, headerData, y + 8);
}

function generateUndanganPdf(doc: jsPDF, headerData: HeaderData, data: any): void {
  let y = addHeaderKopSurat(doc, headerData);
  y = addJudulSurat(doc, 'SURAT UNDANGAN RESMI', y);
  
  const tahun = new Date().getFullYear();
  const nomorUrut = data.nomorSurat || '1';
  const nomorSurat = `${nomorUrut}/......../PEM/${tahun}`;
  y = addNomorSurat(doc, nomorSurat, y);

  // Meta info
  y = addDataField(doc, 'Sifat', 'Penting', PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'Lampiran', '-', PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'Perihal', 'Undangan Acara', PAGE.marginLeft, y, CONTENT_WIDTH);

  y += 3;

  // Alamat tujuan
  doc.setFont('Arial', 'normal');
  doc.setFontSize(11);
  doc.text('Kepada Yth.', PAGE.marginLeft, y);
  y += 4;

  y = addMultilineText(doc, data.tujuanYth, PAGE.marginLeft + 5, y, CONTENT_WIDTH - 5);

  y += 2;

  // Pembuka
  y = addMultilineText(
    doc,
    `Dengan hormat, kami mengundang Bapak/Ibu untuk hadir dalam acara berikut:`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );

  y += 4;

  y = addDataField(doc, 'Hari/Tanggal', `${data.hari}, ${data.tanggal}`, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'Pukul', `${data.pukul} WIB`, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'Tempat', data.tempat, PAGE.marginLeft, y, CONTENT_WIDTH);
  y = addDataField(doc, 'Agenda', data.agendaAcara, PAGE.marginLeft, y, CONTENT_WIDTH);

  y += 3;

  y = addMultilineText(
    doc,
    `Atas perhatian dan kehadiran Bapak/Ibu diucapkan terima kasih.`,
    PAGE.marginLeft,
    y,
    CONTENT_WIDTH
  );

  y += 5;

  // Tanda tangan
  addSignatureBlock(doc, headerData, y);
}

// ===== EXPORT MAIN FUNCTION =====

export function generatePdf(options: GeneratePdfOptions): Blob {
  const doc = new jsPDF({
    orientation: 'portrait',
    unit: 'mm',
    format: 'a4',
  });

  const { letterType, headerData, letterData } = options;

  switch (letterType) {
    case 'domisili':
      generateDomisiliPdf(doc, headerData, letterData);
      break;
    case 'belum_menikah':
      generateBelumMenikahPdf(doc, headerData, letterData);
      break;
    case 'tidak_mampu':
      generateTidakMampuPdf(doc, headerData, letterData);
      break;
    case 'beda_luas':
      generateBedaLuasPdf(doc, headerData, letterData);
      break;
    case 'undangan':
      generateUndanganPdf(doc, headerData, letterData);
      break;
  }

  return doc.output('blob');
}
