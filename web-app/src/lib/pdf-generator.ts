import { jsPDF } from 'jspdf';
import { format, parseISO } from 'date-fns';
import { id as idLocale } from 'date-fns/locale';
import { HeaderData, LetterData, LetterType } from '../types';

interface GeneratePdfOptions {
  letterType: LetterType;
  headerData: HeaderData;
  letterData: LetterData;
}

const FONTS = {
  title: { size: 12, bold: true },
  normal: { size: 11, bold: false },
  normalBold: { size: 11, bold: true },
  small: { size: 10, bold: false },
};

const A4_WIDTH = 210;
const A4_HEIGHT = 297;
const MARGIN_LEFT = 20;
const MARGIN_RIGHT = 20;
const MARGIN_TOP = 15;
const MARGIN_BOTTOM = 20;
const CONTENT_WIDTH = A4_WIDTH - MARGIN_LEFT - MARGIN_RIGHT;

// Format tanggal sesuai standar:
// TTL: DD-MM-YYYY
// Surat: DD MMMM YYYY
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

function addHeader(doc: jsPDF, headerData: HeaderData) {
  let y = MARGIN_TOP;

  // Header box
  doc.setDrawColor(0, 0, 0);
  doc.setLineWidth(0.5);
  doc.rect(MARGIN_LEFT, y, CONTENT_WIDTH, 35);

  // Keterangan desa
  doc.setFont('Arial', 'bold');
  doc.setFontSize(FONTS.title.size);
  doc.text(`PEMERINTAH ${headerData.namaKabupaten.toUpperCase()}`, A4_WIDTH / 2, y + 5, { align: 'center' });
  doc.text(`${headerData.namaKecamatan.toUpperCase()}`, A4_WIDTH / 2, y + 10, { align: 'center' });
  
  doc.setFont('Arial', 'bold');
  doc.setFontSize(FONTS.normalBold.size);
  doc.text(`DESA/KELURAHAN ${headerData.namaDesa.toUpperCase()}`, A4_WIDTH / 2, y + 15, { align: 'center' });
  
  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.small.size);
  doc.text(`${headerData.alamatKantor}`, A4_WIDTH / 2, y + 20, { align: 'center' });

  // Garis pemisah
  y += 40;
  doc.setDrawColor(0, 0, 0);
  doc.setLineWidth(0.8);
  doc.line(MARGIN_LEFT, y, MARGIN_LEFT + CONTENT_WIDTH, y);
  y += 1;
  doc.setLineWidth(0.3);
  doc.line(MARGIN_LEFT, y, MARGIN_LEFT + CONTENT_WIDTH, y);

  return y + 5;
}

function addTitle(doc: jsPDF, title: string, startY: number) {
  doc.setFont('Arial', 'bold');
  doc.setFontSize(FONTS.title.size);
  doc.text(title, A4_WIDTH / 2, startY, { align: 'center' });

  const titleY = startY + 5;
  doc.setDrawColor(0, 0, 0);
  doc.setLineWidth(0.3);
  doc.line(MARGIN_LEFT + 40, titleY, MARGIN_LEFT + CONTENT_WIDTH - 40, titleY);

  return titleY + 5;
}

function addLetterNumber(doc: jsPDF, number: string, startY: number) {
  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.small.size);
  doc.text(`Nomor : ${number}`, MARGIN_LEFT, startY);
  return startY + 5;
}

function formatDate(date?: string): string {
  if (!date) return format(new Date(), 'dd MMMM yyyy', { locale: idLocale });
  try {
    return format(parseISO(date), 'dd MMMM yyyy', { locale: idLocale });
  } catch {
    return format(new Date(), 'dd MMMM yyyy', { locale: idLocale });
  }
}

function wrapText(doc: jsPDF, text: string, maxWidth: number): string[] {
  return doc.splitTextToSize(text, maxWidth);
}

function addMultilineText(doc: jsPDF, text: string, x: number, startY: number, maxWidth: number): number {
  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.normal.size);
  const lines = wrapText(doc, text, maxWidth);
  doc.text(lines, x, startY, { align: 'left' });
  return startY + lines.length * 5 + 3;
}

function generateDomisiliLetter(doc: jsPDF, headerData: HeaderData, data: any): void {
  let y = addHeader(doc, headerData);
  y = addTitle(doc, 'SURAT KETERANGAN DOMISILI', y);
  
  const nomorSurat = data.nomorSurat || `${Date.now()}`;
  const klasifikasi = data.klasifikasiSurat || `140/.../${Date.now().toString().slice(-2)}/${new Date().getFullYear()}`;
  y = addLetterNumber(doc, klasifikasi, y);
  y += 5;

  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.normal.size);

  // Pembuka
  y = addMultilineText(
    doc,
    `Yang bertanda tangan di bawah ini adalah Kepala Desa/Lurah ${headerData.namaDesa}, menerangkan bahwa:`,
    MARGIN_LEFT,
    y,
    CONTENT_WIDTH
  );
  y += 5;

  // Data pemohon
  doc.text(`Nama                       : ${data.namaLengkap}`, MARGIN_LEFT, y);
  y += 5;
  doc.text(`NIK                          : ${data.nik}`, MARGIN_LEFT, y);
  y += 5;
  
  if (data.jenisKelamin) {
    doc.text(`Jenis Kelamin         : ${data.jenisKelamin}`, MARGIN_LEFT, y);
    y += 5;
  }

  if (data.tempatLahir) {
    const tglLahir = data.tanggalLahir ? formatDate(data.tanggalLahir) : '';
    doc.text(`Tempat/Tanggal Lahir : ${data.tempatLahir}, ${tglLahir}`, MARGIN_LEFT, y);
    y += 5;
  }

  if (data.pekerjaan) {
    doc.text(`Pekerjaan                : ${data.pekerjaan}`, MARGIN_LEFT, y);
    y += 5;
  }

  doc.text(`Alamat (KTP)            : ${data.alamatAsal}`, MARGIN_LEFT, y);
  y += 5;
  doc.text(`RT/RW                    : ${data.rt}/${data.rw}`, MARGIN_LEFT, y);
  y += 5;

  doc.text(`Alamat Domisili Saat Ini : ${data.alamatDomisili}`, MARGIN_LEFT, y);
  y += 5;
  y += 3;

  // Pernyataan
  y = addMultilineText(
    doc,
    `Adalah benar bahwa orang tersebut di atas adalah penduduk sah Desa/Kelurahan ${headerData.namaDesa} dan bertempat tinggal di alamat tersebut di atas. Surat keterangan ini diberikan untuk keperluan ${data.keperluanSurat}.`,
    MARGIN_LEFT,
    y,
    CONTENT_WIDTH
  );
  y += 8;

  // Tempat dan tanggal
  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.normal.size);
  doc.text(`Diterbitkan di : ${headerData.namaDesa}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y);
  y += 5;
  doc.text(`Tanggal           : ${formatDate()}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y);
  y += 15;

  // Tanda tangan
  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.normal.size);
  doc.text(`${headerData.atnPejabat ? 'A.n. ' : ''}${headerData.jabatanPejabat}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });
  y += 20;
  doc.text(headerData.namaPejabat, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });
  y += 3;
  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.small.size);
  doc.text(`NIP: ${headerData.nipPejabat}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });
}

function generateBelumMenikahLetter(doc: jsPDF, headerData: HeaderData, data: any): void {
  let y = addHeader(doc, headerData);
  y = addTitle(doc, 'SURAT KETERANGAN BELUM MENIKAH', y);
  
  const klasifikasi = data.klasifikasiSurat || `460.2/.../${Date.now().toString().slice(-2)}/${new Date().getFullYear()}`;
  y = addLetterNumber(doc, klasifikasi, y);
  y += 5;

  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.normal.size);

  y = addMultilineText(
    doc,
    `Yang bertanda tangan di bawah ini adalah Kepala Desa/Lurah ${headerData.namaDesa}, menerangkan bahwa:`,
    MARGIN_LEFT,
    y,
    CONTENT_WIDTH
  );
  y += 5;

  doc.text(`Nama                       : ${data.namaLengkap}`, MARGIN_LEFT, y);
  y += 5;
  doc.text(`NIK                          : ${data.nik}`, MARGIN_LEFT, y);
  y += 5;
  
  if (data.jenisKelamin) {
    doc.text(`Jenis Kelamin         : ${data.jenisKelamin}`, MARGIN_LEFT, y);
    y += 5;
  }

  if (data.tempatLahir) {
    const tglLahir = data.tanggalLahir ? formatDate(data.tanggalLahir) : '';
    doc.text(`Tempat/Tanggal Lahir : ${data.tempatLahir}, ${tglLahir}`, MARGIN_LEFT, y);
    y += 5;
  }

  if (data.pekerjaan) {
    doc.text(`Pekerjaan                : ${data.pekerjaan}`, MARGIN_LEFT, y);
    y += 5;
  }

  doc.text(`Alamat                    : ${data.alamatLengkap}`, MARGIN_LEFT, y);
  y += 5;
  doc.text(`RT/RW                    : ${data.rt}/${data.rw}`, MARGIN_LEFT, y);
  y += 8;

  y = addMultilineText(
    doc,
    `Adalah benar bahwa yang bersangkutan sampai saat ini belum pernah melakukan perkawinan yang tercatat pada kantor pencatat perkawinan yang berwenang. Surat keterangan ini diberikan untuk keperluan ${data.keperluan}.`,
    MARGIN_LEFT,
    y,
    CONTENT_WIDTH
  );
  y += 8;

  doc.text(`Diterbitkan di : ${headerData.namaDesa}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y);
  y += 5;
  doc.text(`Tanggal           : ${formatDate()}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y);
  y += 15;

  doc.text(`${headerData.atnPejabat ? 'A.n. ' : ''}${headerData.jabatanPejabat}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });
  y += 20;
  doc.text(headerData.namaPejabat, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });
  y += 3;
  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.small.size);
  doc.text(`NIP: ${headerData.nipPejabat}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });
}

function generateTidakMampuLetter(doc: jsPDF, headerData: HeaderData, data: any): void {
  let y = addHeader(doc, headerData);
  y = addTitle(doc, 'SURAT KETERANGAN TIDAK MAMPU', y);
  
  const klasifikasi = data.klasifikasiSurat || `460/.../${Date.now().toString().slice(-2)}/${new Date().getFullYear()}`;
  y = addLetterNumber(doc, klasifikasi, y);
  y += 5;

  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.normal.size);

  y = addMultilineText(
    doc,
    `Yang bertanda tangan di bawah ini adalah Kepala Desa/Lurah ${headerData.namaDesa}, menerangkan bahwa:`,
    MARGIN_LEFT,
    y,
    CONTENT_WIDTH
  );
  y += 5;

  doc.text(`Nama                       : ${data.namaLengkap}`, MARGIN_LEFT, y);
  y += 5;
  doc.text(`NIK                          : ${data.nik}`, MARGIN_LEFT, y);
  y += 5;
  
  if (data.jenisKelamin) {
    doc.text(`Jenis Kelamin         : ${data.jenisKelamin}`, MARGIN_LEFT, y);
    y += 5;
  }

  if (data.tempatLahir) {
    const tglLahir = data.tanggalLahir ? formatDate(data.tanggalLahir) : '';
    doc.text(`Tempat/Tanggal Lahir : ${data.tempatLahir}, ${tglLahir}`, MARGIN_LEFT, y);
    y += 5;
  }

  if (data.pekerjaan) {
    doc.text(`Pekerjaan                : ${data.pekerjaan}`, MARGIN_LEFT, y);
    y += 5;
  }

  doc.text(`Alamat                    : ${data.alamatLengkap}`, MARGIN_LEFT, y);
  y += 5;
  doc.text(`RT/RW                    : ${data.rt}/${data.rw}`, MARGIN_LEFT, y);
  y += 8;

  y = addMultilineText(
    doc,
    `Adalah benar bahwa yang bersangkutan termasuk dalam kategori tidak mampu menurut kriteria yang berlaku di desa kami. Surat keterangan ini diberikan untuk keperluan ${data.keperluan} di ${data.tujuanSekolah}.`,
    MARGIN_LEFT,
    y,
    CONTENT_WIDTH
  );
  y += 8;

  doc.text(`Diterbitkan di : ${headerData.namaDesa}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y);
  y += 5;
  doc.text(`Tanggal           : ${formatDate()}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y);
  y += 15;

  doc.text(`${headerData.atnPejabat ? 'A.n. ' : ''}${headerData.jabatanPejabat}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });
  y += 20;
  doc.text(headerData.namaPejabat, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });
  y += 3;
  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.small.size);
  doc.text(`NIP: ${headerData.nipPejabat}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });
}

function generateBedaLuasLetter(doc: jsPDF, headerData: HeaderData, data: any): void {
  let y = addHeader(doc, headerData);
  y = addTitle(doc, 'SURAT KETERANGAN BEDA LUAS TANAH', y);
  
  const klasifikasi = data.klasifikasiSurat || `141/.../${Date.now().toString().slice(-2)}/${new Date().getFullYear()}`;
  y = addLetterNumber(doc, klasifikasi, y);
  y += 5;

  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.normal.size);

  y = addMultilineText(
    doc,
    `Yang bertanda tangan di bawah ini adalah Kepala Desa/Lurah ${headerData.namaDesa}, menerangkan bahwa:`,
    MARGIN_LEFT,
    y,
    CONTENT_WIDTH
  );
  y += 5;

  doc.text(`Nama                       : ${data.namaLengkap}`, MARGIN_LEFT, y);
  y += 5;
  doc.text(`NIK                          : ${data.nik}`, MARGIN_LEFT, y);
  y += 5;
  doc.text(`Alamat                    : ${data.alamatLengkap}`, MARGIN_LEFT, y);
  y += 5;
  doc.text(`RT/RW                    : ${data.rt}/${data.rw}`, MARGIN_LEFT, y);
  y += 8;

  y = addMultilineText(
    doc,
    `Adalah benar bahwa terdapat perbedaan luas tanah antara Sertifikat dan SPPT (Surat Pemberitahuan Pajak Tahunan) yang dimiliki oleh yang bersangkutan, dengan rincian sebagai berikut:`,
    MARGIN_LEFT,
    y,
    CONTENT_WIDTH
  );
  y += 6;

  doc.text(`Nomor Sertifikat          : ${data.nomorSertifikat}`, MARGIN_LEFT, y);
  y += 4;
  doc.text(`Luas Sertifikat           : ${data.luasSertifikat} m²`, MARGIN_LEFT, y);
  y += 4;
  doc.text(`Nomor SPPT                : ${data.nomorSPPT}`, MARGIN_LEFT, y);
  y += 4;
  doc.text(`Luas SPPT                 : ${data.luasSPPT} m²`, MARGIN_LEFT, y);
  y += 8;

  y = addMultilineText(
    doc,
    `Surat keterangan ini diberikan untuk keperluan administrasi pertanahan dan dapat digunakan sebagai dasar untuk melakukan perubahan data tanah sesuai dengan dokumen yang berlaku.`,
    MARGIN_LEFT,
    y,
    CONTENT_WIDTH
  );
  y += 8;

  doc.text(`Diterbitkan di : ${headerData.namaDesa}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y);
  y += 5;
  doc.text(`Tanggal           : ${formatDate()}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y);
  y += 15;

  doc.text(`${headerData.atnPejabat ? 'A.n. ' : ''}${headerData.jabatanPejabat}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });
  y += 20;
  doc.text(headerData.namaPejabat, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });
  y += 3;
  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.small.size);
  doc.text(`NIP: ${headerData.nipPejabat}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });
}

function generateUndanganLetter(doc: jsPDF, headerData: HeaderData, data: any): void {
  let y = addHeader(doc, headerData);
  y = addTitle(doc, 'SURAT UNDANGAN RESMI', y);
  
  const klasifikasi = data.klasifikasiSurat || `${Date.now().toString().slice(-3)}/.../${Date.now().toString().slice(-2)}/${new Date().getFullYear()}`;
  y = addLetterNumber(doc, klasifikasi, y);
  y += 5;

  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.normal.size);

  // Sifat dan Lampiran
  doc.text(`Sifat          : Penting`, MARGIN_LEFT, y);
  y += 4;
  doc.text(`Lampiran       : -`, MARGIN_LEFT, y);
  y += 4;
  doc.text(`Perihal        : Undangan`, MARGIN_LEFT, y);
  y += 8;

  // Tujuan
  doc.text(`Kepada Yth.`, MARGIN_LEFT, y);
  y += 4;
  y = addMultilineText(doc, data.tujuanYth, MARGIN_LEFT, y, CONTENT_WIDTH);
  y += 5;

  // Isi surat
  y = addMultilineText(
    doc,
    `Dengan hormat, kami mengundang Bapak/Ibu untuk hadir dalam acara berikut:`,
    MARGIN_LEFT,
    y,
    CONTENT_WIDTH
  );
  y += 6;

  doc.text(`Hari/Tanggal   : ${data.hari}, ${data.tanggal}`, MARGIN_LEFT, y);
  y += 4;
  doc.text(`Pukul           : ${data.pukul} WIB`, MARGIN_LEFT, y);
  y += 4;
  doc.text(`Tempat          : ${data.tempat}`, MARGIN_LEFT, y);
  y += 4;
  doc.text(`Agenda          : ${data.agendaAcara}`, MARGIN_LEFT, y);
  y += 8;

  y = addMultilineText(
    doc,
    `Atas perhatian dan kehadiran Bapak/Ibu diucapkan terima kasih.`,
    MARGIN_LEFT,
    y,
    CONTENT_WIDTH
  );
  y += 8;

  doc.text(`Diterbitkan di : ${headerData.namaDesa}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y);
  y += 5;
  doc.text(`Tanggal           : ${formatDate()}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y);
  y += 15;

  doc.text(`${headerData.atnPejabat ? 'A.n. ' : ''}${headerData.jabatanPejabat}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });
  y += 20;
  doc.text(headerData.namaPejabat, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });
  y += 3;
  doc.setFont('Arial', 'normal');
  doc.setFontSize(FONTS.small.size);
  doc.text(`NIP: ${headerData.nipPejabat}`, MARGIN_LEFT + CONTENT_WIDTH / 2, y, { align: 'center' });

  if (data.daftarTembusan && data.daftarTembusan.trim()) {
    y += 10;
    doc.text(`Tembusan: ${data.daftarTembusan}`, MARGIN_LEFT, y);
  }
}

export function generatePdf(options: GeneratePdfOptions): Blob {
  const doc = new jsPDF({
    orientation: 'portrait',
    unit: 'mm',
    format: 'a4',
  });

  const { letterType, headerData, letterData } = options;

  switch (letterType) {
    case 'domisili':
      generateDomisiliLetter(doc, headerData, letterData);
      break;
    case 'belum_menikah':
      generateBelumMenikahLetter(doc, headerData, letterData);
      break;
    case 'tidak_mampu':
      generateTidakMampuLetter(doc, headerData, letterData);
      break;
    case 'beda_luas':
      generateBedaLuasLetter(doc, headerData, letterData);
      break;
    case 'undangan':
      generateUndanganLetter(doc, headerData, letterData);
      break;
  }

  return doc.output('blob');
}
