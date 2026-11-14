'use client';

import { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { useLetterStore } from '../lib/store';
import { LetterTypeSelector } from '../components/LetterTypeSelector';
import { LetterForm } from '../components/LetterForm';
import { LetterPreview } from '../components/LetterPreview';
import { HeaderForm } from '../components/HeaderForm';
import { FileText, Zap } from 'lucide-react';

export default function Home() {
  const { loadHeaderFromStorage, reset } = useLetterStore();
  const [showPreview, setShowPreview] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    // Load header data from localStorage on mount
    loadHeaderFromStorage();
  }, [loadHeaderFromStorage]);

  const handleFormSubmit = async (data: any) => {
    setIsLoading(true);
    // Simulate slight delay for better UX
    setTimeout(() => {
      setShowPreview(true);
      setIsLoading(false);
    }, 500);
  };

  const handleNewLetter = () => {
    reset();
    setShowPreview(false);
  };

  return (
    <main className="min-h-screen">
      {/* Animated Header */}
      <div className="relative overflow-hidden bg-gradient-to-r from-blue-600 via-teal-500 to-emerald-500 text-white pt-8 pb-12">
        {/* Animated background elements */}
        <div className="absolute inset-0 opacity-10">
          <motion.div
            animate={{
              rotate: 360,
            }}
            transition={{
              duration: 20,
              repeat: Infinity,
              ease: 'linear',
            }}
            className="absolute top-0 left-0 w-96 h-96 rounded-full bg-white blur-3xl"
          />
          <motion.div
            animate={{
              rotate: -360,
            }}
            transition={{
              duration: 25,
              repeat: Infinity,
              ease: 'linear',
            }}
            className="absolute bottom-0 right-0 w-96 h-96 rounded-full bg-white blur-3xl"
          />
        </div>

        <div className="relative max-w-6xl mx-auto px-4">
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
            className="text-center mb-2"
          >
            <div className="flex items-center justify-center gap-3 mb-4">
              <motion.div
                whileHover={{ rotate: 12 }}
                className="inline-block"
              >
                <FileText className="w-8 h-8" />
              </motion.div>
              <h1 className="text-4xl md:text-5xl font-bold">KASIPEM</h1>
            </div>
            <p className="text-xl text-blue-50 mb-2">
              Pembuat Surat Administrasi Desa/Kelurahan
            </p>
            <div className="flex items-center justify-center gap-2 text-blue-100 text-sm">
              <Zap className="w-4 h-4" />
              <span>Cepat • Mudah • Gratis • Tanpa Data Tersimpan</span>
            </div>
          </motion.div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-6xl mx-auto px-4 py-12">
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Main Form Area */}
          <div className="lg:col-span-2 space-y-8">
            {/* Step 1: Select Letter Type */}
            <motion.section
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.1 }}
            >
              <div className="mb-4">
                <div className="inline-block bg-blue-100 text-blue-700 px-4 py-1 rounded-full text-sm font-semibold">
                  Langkah 1
                </div>
              </div>
              <LetterTypeSelector />
            </motion.section>

            {/* Step 2: Fill Letter Form */}
            <motion.section
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.2 }}
            >
              <div className="mb-4">
                <div className="inline-block bg-teal-100 text-teal-700 px-4 py-1 rounded-full text-sm font-semibold">
                  Langkah 2
                </div>
              </div>
              <LetterForm
                onSubmit={handleFormSubmit}
                isLoading={isLoading}
              />
            </motion.section>
          </div>

          {/* Info Sidebar */}
          <motion.aside
            initial={{ opacity: 0, x: 20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.3 }}
            className="lg:col-span-1"
          >
            <div className="sticky top-24 space-y-6">
              {/* Features Card */}
              <motion.div
                whileHover={{ y: -4 }}
                className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-blue-600"
              >
                <h3 className="font-bold text-gray-800 mb-4">Fitur Unggulan</h3>
                <ul className="space-y-3 text-sm text-gray-700">
                  <li className="flex gap-2">
                    <span className="text-blue-600 font-bold">✓</span>
                    <span>Stateless (Tanpa Basis Data)</span>
                  </li>
                  <li className="flex gap-2">
                    <span className="text-blue-600 font-bold">✓</span>
                    <span>Tanpa Login/Register</span>
                  </li>
                  <li className="flex gap-2">
                    <span className="text-blue-600 font-bold">✓</span>
                    <span>Ekspor PDF & DOCX</span>
                  </li>
                  <li className="flex gap-2">
                    <span className="text-blue-600 font-bold">✓</span>
                    <span>Cetak Langsung</span>
                  </li>
                  <li className="flex gap-2">
                    <span className="text-blue-600 font-bold">✓</span>
                    <span>Simpan Data Lokal</span>
                  </li>
                </ul>
              </motion.div>

              {/* Jenis Surat */}
              <motion.div
                whileHover={{ y: -4 }}
                className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-teal-600"
              >
                <h3 className="font-bold text-gray-800 mb-4">Jenis Surat</h3>
                <ul className="space-y-2 text-sm text-gray-700">
                  <li>📋 Keterangan Domisili</li>
                  <li>💍 Belum Menikah</li>
                  <li>💰 Tidak Mampu (SKTM)</li>
                  <li>🏠 Beda Luas Tanah</li>
                  <li>📮 Undangan Resmi</li>
                </ul>
              </motion.div>

              {/* Help Card */}
              <motion.div
                whileHover={{ y: -4 }}
                className="bg-gradient-to-br from-emerald-50 to-teal-50 rounded-xl shadow-lg p-6 border border-teal-200"
              >
                <h3 className="font-bold text-gray-800 mb-3">Tips & Trik</h3>
                <p className="text-xs text-gray-700 leading-relaxed mb-3">
                  Klik tombol ⚙️ di bawah kanan untuk mengatur data kop surat dan pejabat penandatangan secara permanen di perangkat Anda.
                </p>
                <p className="text-xs text-gray-700 leading-relaxed">
                  Data lokal Anda tidak akan pernah dikirim ke server manapun.
                </p>
              </motion.div>
            </div>
          </motion.aside>
        </div>
      </div>

      {/* Preview Modal */}
      <LetterPreview
        isOpen={showPreview}
        onClose={() => setShowPreview(false)}
        onNewLetter={handleNewLetter}
      />

      {/* Settings Button */}
      <HeaderForm />

      {/* Footer */}
      <motion.footer
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.5 }}
        className="mt-16 bg-gray-50 border-t"
      >
        <div className="max-w-6xl mx-auto px-4 py-8 text-center text-gray-600 text-sm">
          <p>
            <strong>KASIPEM</strong> - Aplikasi Pembuat Surat Administrasi Desa/Kelurahan Stateless
          </p>
          <p className="mt-2">
            Dibuat dengan ❤️ untuk memudahkan administrasi desa/kelurahan
          </p>
          <p className="mt-2 text-xs text-gray-500">
            v1.0 • Semua data diproses di perangkat Anda • Tidak ada data yang tersimpan di server
          </p>
        </div>
      </motion.footer>
    </main>
  );
}
