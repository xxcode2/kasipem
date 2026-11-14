'use client';

import { motion } from 'framer-motion';
import { useLetterStore } from '../lib/store';
import { Settings } from 'lucide-react';
import { useState } from 'react';

export function HeaderForm() {
  const { headerData, setHeaderData, saveHeaderToStorage } = useLetterStore();
  const [isOpen, setIsOpen] = useState(false);

  const handleChange = (field: string, value: string | boolean) => {
    setHeaderData({ [field]: value });
  };

  const handleSave = () => {
    saveHeaderToStorage();
    setIsOpen(false);
  };

  return (
    <>
      <motion.button
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
        onClick={() => setIsOpen(true)}
        className="fixed bottom-6 right-6 bg-gradient-to-r from-blue-600 to-teal-600 text-white p-4 rounded-full shadow-lg hover:shadow-xl transition-shadow z-40"
      >
        <Settings className="w-6 h-6" />
      </motion.button>

      {isOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
          onClick={() => setIsOpen(false)}
        >
          <motion.div
            initial={{ scale: 0.95, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            onClick={(e) => e.stopPropagation()}
            className="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
          >
            <div className="sticky top-0 bg-gradient-to-r from-blue-600 to-teal-600 text-white p-6 flex items-center justify-between">
              <h2 className="text-2xl font-bold">Data Kop Surat & Pejabat</h2>
              <button
                onClick={() => setIsOpen(false)}
                className="text-white/80 hover:text-white"
              >
                ✕
              </button>
            </div>

            <div className="p-6 space-y-5">
              {/* Nama Kabupaten */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Nama Kabupaten/Kota
                </label>
                <input
                  type="text"
                  value={headerData.namaKabupaten}
                  onChange={(e) =>
                    handleChange('namaKabupaten', e.target.value)
                  }
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Contoh: KABUPATEN BANDUNG"
                />
              </div>

              {/* Nama Kecamatan */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Nama Kecamatan
                </label>
                <input
                  type="text"
                  value={headerData.namaKecamatan}
                  onChange={(e) =>
                    handleChange('namaKecamatan', e.target.value)
                  }
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Contoh: KECAMATAN RANCAEKEK"
                />
              </div>

              {/* Nama Desa */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Nama Desa/Kelurahan
                </label>
                <input
                  type="text"
                  value={headerData.namaDesa}
                  onChange={(e) => handleChange('namaDesa', e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Contoh: DESA RANCAMANYAR"
                />
              </div>

              {/* Alamat Kantor */}
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  Alamat Kantor Desa/Kelurahan
                </label>
                <textarea
                  value={headerData.alamatKantor}
                  onChange={(e) =>
                    handleChange('alamatKantor', e.target.value)
                  }
                  rows={2}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  placeholder="Contoh: Jl. Raya Desa No. 123"
                />
              </div>

              <div className="border-t pt-5">
                <h3 className="font-bold text-gray-800 mb-4">Penandatangan Surat</h3>

                {/* Nama Pejabat */}
                <div className="mb-4">
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    Nama Pejabat
                  </label>
                  <input
                    type="text"
                    value={headerData.namaPejabat}
                    onChange={(e) =>
                      handleChange('namaPejabat', e.target.value)
                    }
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Contoh: H. Nama Kepala Desa"
                  />
                </div>

                {/* Jabatan */}
                <div className="mb-4">
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    Jabatan
                  </label>
                  <select
                    value={headerData.jabatanPejabat}
                    onChange={(e) =>
                      handleChange('jabatanPejabat', e.target.value)
                    }
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  >
                    <option>Kepala Desa</option>
                    <option>Sekretaris Desa</option>
                    <option>Lurah</option>
                    <option>Sekretaris Kelurahan</option>
                  </select>
                </div>

                {/* A.n. Checkbox */}
                <div className="mb-4 flex items-center">
                  <input
                    type="checkbox"
                    id="atn"
                    checked={headerData.atnPejabat || false}
                    onChange={(e) =>
                      handleChange('atnPejabat', e.target.checked)
                    }
                    className="w-4 h-4 text-blue-600 rounded"
                  />
                  <label htmlFor="atn" className="ml-2 text-sm text-gray-700">
                    Atas Nama (A.n.) Kepala Desa/Lurah
                  </label>
                </div>

                {/* NIP */}
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    NIP
                  </label>
                  <input
                    type="text"
                    value={headerData.nipPejabat}
                    onChange={(e) =>
                      handleChange('nipPejabat', e.target.value)
                    }
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    placeholder="Contoh: 1234567890"
                  />
                </div>
              </div>
            </div>

            <div className="sticky bottom-0 bg-gray-50 border-t px-6 py-4 flex justify-end gap-3">
              <button
                onClick={() => setIsOpen(false)}
                className="px-6 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-100 transition-colors"
              >
                Batal
              </button>
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={handleSave}
                className="px-6 py-2 bg-gradient-to-r from-blue-600 to-teal-600 text-white rounded-lg hover:shadow-lg transition-shadow"
              >
                Simpan & Tutup
              </motion.button>
            </div>
          </motion.div>
        </motion.div>
      )}
    </>
  );
}
