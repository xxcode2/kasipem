'use client';

import { useState } from 'react';
import { useLetterStore } from '../lib/store';
import { generatePdf } from '../lib/pdf-generator';
import { LetterBody } from './LetterBody';
import { Download, Printer, X, FileText, RotateCcw } from 'lucide-react';

interface LetterPreviewProps {
  isOpen: boolean;
  onClose: () => void;
  onNewLetter: () => void;
}

/**
 * Modal preview A4. Cetak memakai window.print() dari halaman yang sama +
 * print CSS di globals.css (.print-area) — bukan window.open + innerHTML.
 */
export function LetterPreview({ isOpen, onClose, onNewLetter }: LetterPreviewProps) {
  const { letterType, headerData, letterData } = useLetterStore();
  const [isGenerating, setIsGenerating] = useState(false);
  const [error, setError] = useState('');

  if (!isOpen) return null;

  const handleDownloadPdf = async () => {
    setIsGenerating(true);
    setError('');
    try {
      const blob = await generatePdf({ letterType, headerData, letterData });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `Surat-${letterType}-${new Date().toISOString().slice(0, 10)}.pdf`;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (err) {
      console.error('Error generating PDF:', err);
      setError('Gagal membuat PDF. Coba lagi.');
    } finally {
      setIsGenerating(false);
    }
  };

  const handlePrint = () => {
    window.print();
  };

  return (
    <div
      className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4 no-print-container"
      onClick={onClose}
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="bg-white rounded-xl shadow-2xl max-w-3xl w-full max-h-[90vh] overflow-hidden flex flex-col"
      >
        {/* Header modal — disembunyikan saat cetak */}
        <div className="no-print bg-teal-700 text-white px-6 py-4 flex items-center justify-between shrink-0">
          <div className="flex items-center gap-3">
            <FileText className="w-5 h-5" />
            <h2 className="text-lg font-semibold">Pratinjau Surat</h2>
          </div>
          <button onClick={onClose} className="text-white/80 hover:text-white" aria-label="Tutup">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Area surat — INI yang dicetak */}
        <div className="flex-1 overflow-y-auto p-4 sm:p-6 bg-stone-200">
          <div className="print-area">
            <LetterBody letterType={letterType} headerData={headerData} letterData={letterData} />
          </div>
          {error && (
            <p className="no-print mt-4 text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
              {error}
            </p>
          )}
        </div>

        {/* Aksi — disembunyikan saat cetak */}
        <div className="no-print bg-white border-t border-stone-200 px-6 py-4 flex flex-wrap gap-3 justify-end shrink-0">
          <button
            onClick={onNewLetter}
            className="px-5 py-2 text-stone-700 border border-stone-300 rounded-lg hover:bg-stone-100 font-medium flex items-center gap-2"
          >
            <RotateCcw className="w-4 h-4" />
            Buat Surat Baru
          </button>
          <button
            onClick={handlePrint}
            className="px-5 py-2 bg-white text-stone-700 border border-stone-300 rounded-lg hover:bg-stone-100 font-medium flex items-center gap-2"
          >
            <Printer className="w-4 h-4" />
            Cetak
          </button>
          <button
            onClick={handleDownloadPdf}
            disabled={isGenerating}
            className="px-5 py-2 bg-teal-700 text-white rounded-lg hover:bg-teal-800 font-medium disabled:opacity-50 flex items-center gap-2"
          >
            <Download className="w-4 h-4" />
            {isGenerating ? 'Membuat PDF...' : 'Unduh PDF'}
          </button>
        </div>
      </div>
    </div>
  );
}
