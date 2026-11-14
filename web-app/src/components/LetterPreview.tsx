'use client';

import { motion, AnimatePresence } from 'framer-motion';
import { useLetterStore } from '../lib/store';
import { generatePdf } from '../lib/pdf-generator';
import { Download, FileText, X } from 'lucide-react';
import { useRef, useState } from 'react';

interface LetterPreviewProps {
  isOpen: boolean;
  onClose: () => void;
  onNewLetter: () => void;
}

export function LetterPreview({ isOpen, onClose, onNewLetter }: LetterPreviewProps) {
  const { letterType, headerData, letterData } = useLetterStore();
  const previewRef = useRef<HTMLDivElement>(null);
  const [isGenerating, setIsGenerating] = useState(false);

  const handleDownloadPdf = async () => {
    setIsGenerating(true);
    try {
      const blob = generatePdf({ letterType, headerData, letterData });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `Surat-${letterType}-${Date.now()}.pdf`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Error generating PDF:', error);
    } finally {
      setIsGenerating(false);
    }
  };

  const handlePrint = () => {
    if (previewRef.current) {
      const printWindow = window.open('', '', 'width=800,height=600');
      if (printWindow) {
        printWindow.document.write(previewRef.current.innerHTML);
        printWindow.document.close();
        printWindow.print();
      }
    }
  };

  const handleNewLetter = () => {
    onNewLetter();
    onClose();
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
          onClick={onClose}
        >
          <motion.div
            initial={{ scale: 0.95, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.95, opacity: 0 }}
            onClick={(e) => e.stopPropagation()}
            className="bg-white rounded-xl shadow-2xl max-w-3xl w-full max-h-[90vh] overflow-hidden flex flex-col"
          >
            {/* Header */}
            <div className="sticky top-0 bg-gradient-to-r from-blue-600 to-teal-600 text-white p-6 flex items-center justify-between">
              <div className="flex items-center gap-3">
                <FileText className="w-6 h-6" />
                <h2 className="text-2xl font-bold">Pratinjau Surat</h2>
              </div>
              <button
                onClick={onClose}
                className="text-white/80 hover:text-white transition-colors"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            {/* Preview Content */}
            <div className="flex-1 overflow-y-auto p-6 bg-gray-50">
              <div
                ref={previewRef}
                className="bg-white p-8 rounded-lg shadow-sm max-w-2xl mx-auto"
                style={{
                  fontFamily: 'Arial, sans-serif',
                  lineHeight: '1.5',
                  fontSize: '12px',
                }}
              >
                <PreviewContent />
              </div>
            </div>

            {/* Actions */}
            <div className="sticky bottom-0 bg-gray-50 border-t px-6 py-4 flex gap-3 justify-end">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={handleNewLetter}
                className="px-6 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-100 transition-colors font-medium"
              >
                Buat Surat Baru
              </motion.button>
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={handlePrint}
                className="px-6 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors font-medium"
              >
                Cetak
              </motion.button>
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={handleDownloadPdf}
                disabled={isGenerating}
                className="px-6 py-2 bg-gradient-to-r from-blue-600 to-teal-600 text-white rounded-lg hover:shadow-lg transition-shadow font-medium disabled:opacity-50 flex items-center gap-2"
              >
                <Download className="w-4 h-4" />
                {isGenerating ? 'Mengunduh...' : 'Unduh PDF'}
              </motion.button>
            </div>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}

function PreviewContent() {
  const { letterType, headerData, letterData } = useLetterStore();

  if (letterType === 'undangan') {
    return <UndanganPreview data={letterData as any} header={headerData} />;
  }

  return (
    <div>
      {/* Header */}
      <div className="text-center mb-4 border-b-2 pb-4">
        <div className="font-bold text-sm">
          PEMERINTAH {headerData.namaKabupaten.toUpperCase()}
        </div>
        <div className="font-bold text-sm">{headerData.namaKecamatan.toUpperCase()}</div>
        <div className="font-bold text-sm">DESA/KELURAHAN {headerData.namaDesa.toUpperCase()}</div>
        <div className="text-xs">{headerData.alamatKantor}</div>
      </div>

      {/* Title */}
      <div className="text-center font-bold mb-2 text-sm underline">
        {getLetterTitle(letterType)}
      </div>

      {/* Classification */}
      <div className="text-xs text-right mb-4">
        Nomor: {getClassification(letterType, letterData)}
      </div>

      {/* Content */}
      <div className="text-justify space-y-2 text-xs leading-relaxed mb-6">
        <p>
          Yang bertanda tangan di bawah ini adalah Kepala Desa/Lurah{' '}
          {headerData.namaDesa}, menerangkan bahwa:
        </p>

        {renderPreviewContent(letterType, letterData)}
      </div>

      {/* Footer */}
      <div className="mt-8 grid grid-cols-2 gap-8">
        <div className="text-center text-xs">
          <div>Diterbitkan di: {headerData.namaDesa}</div>
          <div>Tanggal: {formatDate()}</div>
        </div>
        <div className="text-center text-xs">
          <div>{headerData.atnPejabat ? 'A.n. ' : ''}{headerData.jabatanPejabat}</div>
          <div className="h-12"></div>
          <div className="font-bold">{headerData.namaPejabat}</div>
          <div>NIP: {headerData.nipPejabat}</div>
        </div>
      </div>
    </div>
  );
}

function UndanganPreview({ data, header }: { data: any; header: any }) {
  return (
    <div>
      {/* Header */}
      <div className="text-center mb-4 border-b-2 pb-4">
        <div className="font-bold text-sm">
          PEMERINTAH {header.namaKabupaten.toUpperCase()}
        </div>
        <div className="font-bold text-sm">{header.namaKecamatan.toUpperCase()}</div>
        <div className="font-bold text-sm">DESA/KELURAHAN {header.namaDesa.toUpperCase()}</div>
        <div className="text-xs">{header.alamatKantor}</div>
      </div>

      {/* Title */}
      <div className="text-center font-bold mb-4 text-sm">SURAT UNDANGAN RESMI</div>

      {/* Meta Info */}
      <div className="text-xs space-y-1 mb-4">
        <div>Sifat: Penting</div>
        <div>Lampiran: -</div>
        <div>Perihal: Undangan</div>
      </div>

      {/* Content */}
      <div className="text-xs space-y-3">
        <div>
          <div>Kepada Yth.</div>
          <div className="ml-4">{data.tujuanYth}</div>
        </div>

        <p className="text-justify">
          Dengan hormat, kami mengundang Bapak/Ibu untuk hadir dalam acara berikut:
        </p>

        <div className="ml-4 space-y-1">
          <div>Hari/Tanggal: {data.hari}, {data.tanggal}</div>
          <div>Pukul: {data.pukul} WIB</div>
          <div>Tempat: {data.tempat}</div>
          <div>Agenda: {data.agendaAcara}</div>
        </div>

        <p className="text-justify">
          Atas perhatian dan kehadiran Bapak/Ibu diucapkan terima kasih.
        </p>
      </div>

      {/* Footer */}
      <div className="mt-8 grid grid-cols-2 gap-8">
        <div></div>
        <div className="text-center text-xs">
          <div>Diterbitkan di: {header.namaDesa}</div>
          <div>Tanggal: {formatDate()}</div>
          <div className="h-8"></div>
          <div className="font-bold">{header.namaPejabat}</div>
          <div>NIP: {header.nipPejabat}</div>
        </div>
      </div>
    </div>
  );
}

function getLetterTitle(type: string): string {
  const titles: Record<string, string> = {
    domisili: 'SURAT KETERANGAN DOMISILI',
    belum_menikah: 'SURAT KETERANGAN BELUM MENIKAH',
    tidak_mampu: 'SURAT KETERANGAN TIDAK MAMPU',
    beda_luas: 'SURAT KETERANGAN BEDA LUAS TANAH',
    undangan: 'SURAT UNDANGAN RESMI',
  };
  return titles[type] || '';
}

function getClassification(type: string, data: any): string {
  const classifications: Record<string, string> = {
    domisili: '140/.../.../TAHUN',
    belum_menikah: '460.2/.../.../TAHUN',
    tidak_mampu: '460/.../.../TAHUN',
    beda_luas: '141/.../.../TAHUN',
    undangan: '.../.../.../TAHUN',
  };
  return classifications[type] || '';
}

function renderPreviewContent(type: string, data: any): React.ReactNode {
  switch (type) {
    case 'domisili':
      return (
        <>
          <div>
            <div>Nama: {data.namaLengkap}</div>
            <div>NIK: {data.nik}</div>
            {data.jenisKelamin && <div>Jenis Kelamin: {data.jenisKelamin}</div>}
            {data.tempatLahir && <div>Tempat Lahir: {data.tempatLahir}</div>}
            {data.tanggalLahir && <div>Tanggal Lahir: {data.tanggalLahir}</div>}
            {data.pekerjaan && <div>Pekerjaan: {data.pekerjaan}</div>}
            <div>Alamat Asal: {data.alamatAsal}</div>
            <div>RT/RW: {data.rt}/{data.rw}</div>
            <div>Alamat Domisili: {data.alamatDomisili}</div>
          </div>
          <p>
            Adalah benar bahwa orang tersebut di atas adalah penduduk sah Desa/Kelurahan dan
            bertempat tinggal di alamat tersebut di atas. Surat keterangan ini diberikan untuk keperluan{' '}
            {data.keperluanSurat}.
          </p>
        </>
      );
    case 'belum_menikah':
      return (
        <>
          <div>
            <div>Nama: {data.namaLengkap}</div>
            <div>NIK: {data.nik}</div>
            <div>Alamat: {data.alamatLengkap}</div>
            <div>RT/RW: {data.rt}/{data.rw}</div>
          </div>
          <p>
            Adalah benar bahwa yang bersangkutan sampai saat ini belum pernah melakukan perkawinan yang
            tercatat. Surat keterangan ini diberikan untuk keperluan {data.keperluan}.
          </p>
        </>
      );
    case 'tidak_mampu':
      return (
        <>
          <div>
            <div>Nama: {data.namaLengkap}</div>
            <div>NIK: {data.nik}</div>
            <div>Alamat: {data.alamatLengkap}</div>
            <div>RT/RW: {data.rt}/{data.rw}</div>
          </div>
          <p>
            Adalah benar bahwa yang bersangkutan termasuk dalam kategori tidak mampu. Surat keterangan
            ini diberikan untuk keperluan {data.keperluan} di {data.tujuanSekolah}.
          </p>
        </>
      );
    case 'beda_luas':
      return (
        <>
          <div>
            <div>Nama: {data.namaLengkap}</div>
            <div>NIK: {data.nik}</div>
            <div>Alamat: {data.alamatLengkap}</div>
            <div>RT/RW: {data.rt}/{data.rw}</div>
            <div>Nomor Sertifikat: {data.nomorSertifikat}</div>
            <div>Luas Sertifikat: {data.luasSertifikat} m²</div>
            <div>Nomor SPPT: {data.nomorSPPT}</div>
            <div>Luas SPPT: {data.luasSPPT} m²</div>
          </div>
          <p>
            Adalah benar bahwa terdapat perbedaan luas tanah antara Sertifikat dan SPPT yang dimiliki.
          </p>
        </>
      );
    default:
      return null;
  }
}

function formatDate(): string {
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  };
  return new Date().toLocaleDateString('id-ID', options);
}
