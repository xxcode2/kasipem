'use client';

import { useEffect, useState } from 'react';
import { useLetterStore } from '../lib/store';
import { LetterType } from '../types';
import { LetterTypeSelector } from '../components/LetterTypeSelector';
import { LetterForm } from '../components/LetterForm';
import { LetterPreview } from '../components/LetterPreview';
import { RiwayatSurat } from '../components/RiwayatSurat';
import { HeaderForm } from '../components/HeaderForm';
import { FileText, ShieldCheck, Printer, Download, History } from 'lucide-react';

export default function Home() {
  const { loadHeaderFromStorage, reset, setLetterType } = useLetterStore();
  const [showForm, setShowForm] = useState(false);
  const [showPreview, setShowPreview] = useState(false);
  const [showHistory, setShowHistory] = useState(false);

  useEffect(() => {
    loadHeaderFromStorage();
  }, [loadHeaderFromStorage]);

  const handleSelectType = (type: LetterType) => {
    setLetterType(type);
    setShowPreview(false);
    setShowForm(true);
  };

  const handleFormPreview = () => {
    setShowForm(false);
    setShowPreview(true);
  };

  const handleNewLetter = () => {
    reset();
    setShowPreview(false);
    setShowForm(false);
  };

  return (
    <main className="min-h-screen bg-stone-100 text-stone-800">
      {/* Header kalem */}
      <header className="bg-teal-800 text-white">
        <div className="max-w-6xl mx-auto px-4 py-10 text-center">
          <div className="flex items-center justify-center gap-3 mb-3">
            <FileText className="w-8 h-8" />
            <h1 className="text-4xl font-bold tracking-wide">KASIPEM</h1>
          </div>
          <p className="text-lg text-teal-100">Pembuat Surat Administrasi Desa Kasomalang Kulon</p>
          <p className="text-sm text-teal-200 mt-1">
            Kecamatan Kasomalang, Kabupaten Subang — 30 jenis surat &amp; formulir • Cepat • Gratis • Data hanya di perangkat Anda
          </p>
        </div>
      </header>

      {/* Konten */}
      <div className="max-w-6xl mx-auto px-4 py-10">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2">
            <h2 className="text-xl font-semibold text-stone-800 mb-1">Pilih Jenis Surat</h2>
            <p className="text-sm text-stone-600 mb-6">
              Klik salah satu kartu, isi formulir, lalu lihat preview dan unduh PDF atau cetak.
            </p>
            <LetterTypeSelector onSelect={handleSelectType} />
          </div>

          {/* Sidebar ringkas */}
          <aside className="lg:col-span-1">
            <div className="sticky top-6 space-y-6">
              <div className="bg-white rounded-xl shadow-sm border border-stone-200 p-6">
                <h3 className="font-semibold text-stone-800 mb-3 flex items-center gap-2">
                  <ShieldCheck className="w-5 h-5 text-teal-700" />
                  Cara Menggunakan
                </h3>
                <ol className="space-y-2 text-sm text-stone-600 list-decimal list-inside">
                  <li>Pilih jenis surat dari kartu di samping.</li>
                  <li>Isi formulir (kolom bertanda * wajib).</li>
                  <li>Klik &ldquo;Lihat Preview&rdquo; untuk memeriksa surat.</li>
                  <li>Unduh PDF atau cetak langsung.</li>
                </ol>
              </div>

              <div className="bg-white rounded-xl shadow-sm border border-stone-200 p-6">
                <h3 className="font-semibold text-stone-800 mb-3 flex items-center gap-2">
                  <History className="w-5 h-5 text-teal-700" />
                  Riwayat Surat
                </h3>
                <p className="text-sm text-stone-600 leading-relaxed">
                  Setiap surat yang diunduh atau dicetak tercatat di perangkat ini (tanggal, nomor,
                  jenis, nama pemohon). Lihat dan hapus riwayat kapan saja — tidak ada data yang
                  dikirim ke server.
                </p>
                <button
                  onClick={() => setShowHistory(true)}
                  className="mt-3 px-4 py-2 bg-teal-700 text-white rounded-lg hover:bg-teal-800 text-sm font-medium"
                >
                  Lihat Riwayat Surat
                </button>
              </div>

              <div className="bg-white rounded-xl shadow-sm border border-stone-200 p-6">
                <h3 className="font-semibold text-stone-800 mb-3">Pengaturan Kop Surat</h3>
                <p className="text-sm text-stone-600 leading-relaxed">
                  Klik tombol ⚙️ di pojok kanan bawah untuk mengatur nama desa, nama &amp; NIP Kepala
                  Desa dan Sekretaris Desa, penandatangan, serta logo kop. Tersimpan hanya di perangkat ini.
                </p>
              </div>

              <div className="bg-white rounded-xl shadow-sm border border-stone-200 p-6">
                <h3 className="font-semibold text-stone-800 mb-3 flex items-center gap-2">
                  <Printer className="w-5 h-5 text-teal-700" />
                  Cetak &amp; PDF
                </h3>
                <p className="text-sm text-stone-600 leading-relaxed">
                  Hasil cetak menggunakan ukuran A4 dengan kop, isi, dan tanda tangan yang sama seperti
                  preview. Unduh PDF tersedia di jendela preview.
                </p>
                <p className="text-sm text-stone-600 leading-relaxed mt-2 flex items-center gap-1">
                  <Download className="w-4 h-4 text-teal-700" />
                  Format PDF A4 siap arsip.
                </p>
              </div>
            </div>
          </aside>
        </div>
      </div>

      {/* Modal form + preview */}
      <LetterForm
        isOpen={showForm}
        onClose={() => setShowForm(false)}
        onPreview={handleFormPreview}
      />
      <LetterPreview
        isOpen={showPreview}
        onClose={() => setShowPreview(false)}
        onNewLetter={handleNewLetter}
      />
      <RiwayatSurat isOpen={showHistory} onClose={() => setShowHistory(false)} />

      <HeaderForm />

      <footer className="mt-8 bg-white border-t border-stone-200">
        <div className="max-w-6xl mx-auto px-4 py-6 text-center text-stone-500 text-sm">
          <p>
            <strong className="text-stone-700">KASIPEM</strong> — Aplikasi Pembuat Surat Administrasi Desa
            Kasomalang Kulon, Kecamatan Kasomalang, Kabupaten Subang
          </p>
          <p className="mt-1 text-xs">
            Stateless • Semua data diproses di perangkat Anda • Tidak ada data yang dikirim ke server
          </p>
        </div>
      </footer>
    </main>
  );
}
