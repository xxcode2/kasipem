'use client';

import { motion } from 'framer-motion';
import { useLetterStore } from '../lib/store';
import { LetterType } from '../types';
import { Send, RotateCcw } from 'lucide-react';

interface FormField {
  name: string;
  label: string;
  type: string;
  placeholder?: string;
  required?: boolean;
  rows?: number;
  optional?: boolean;
  options?: { value: string; label: string }[];
}

const formConfigs: Record<LetterType, FormField[]> = {
  domisili: [
    { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
    { name: 'nik', label: 'NIK', type: 'text', required: true },
    { name: 'jenisKelamin', label: 'Jenis Kelamin', type: 'select', options: [
      { value: '', label: 'Pilih...' },
      { value: 'Laki-laki', label: 'Laki-laki' },
      { value: 'Perempuan', label: 'Perempuan' }
    ] },
    { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
    { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
    { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
    { name: 'alamatAsal', label: 'Alamat Asal (KTP)', type: 'textarea', required: true, rows: 2 },
    { name: 'rt', label: 'RT', type: 'text', required: true },
    { name: 'rw', label: 'RW', type: 'text', required: true },
    { name: 'alamatDomisili', label: 'Alamat Domisili Saat Ini', type: 'textarea', required: true, rows: 2 },
    { name: 'keperluanSurat', label: 'Keperluan Surat', type: 'text', required: true },
  ],
  pisah_kk: [
    { name: 'nama1', label: 'Nama Pemisah Pertama', type: 'text', required: true },
    { name: 'nik1', label: 'NIK Pemisah Pertama', type: 'text', required: true },
    { name: 'nama2', label: 'Nama Pemisah Kedua', type: 'text', required: true },
    { name: 'nik2', label: 'NIK Pemisah Kedua', type: 'text', required: true },
    { name: 'nomorKKLama', label: 'Nomor KK Lama', type: 'text', required: true },
    { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
  ],
  skt_dayat: [
    { name: 'namaLengkap', label: 'Nama Pemilik Tanah', type: 'text', required: true },
    { name: 'nik', label: 'NIK', type: 'text', required: true },
    { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
    { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
    { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
    { name: 'luasTanah', label: 'Luas Tanah (m²)', type: 'number', required: true },
    { name: 'luasBangunan', label: 'Luas Bangunan (m²)', type: 'number' },
    { name: 'spptNop', label: 'SPPT/NOP', type: 'text' },
  ],
  beda_luas: [
    { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
    { name: 'nik', label: 'NIK', type: 'text', required: true },
    { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
    { name: 'nomorSertifikat', label: 'Nomor Sertifikat', type: 'text', required: true },
    { name: 'luasSertifikat', label: 'Luas Sertifikat (m²)', type: 'text', required: true },
    { name: 'nomorSPPT', label: 'Nomor SPPT', type: 'text', required: true },
    { name: 'luasSPPT', label: 'Luas SPPT (m²)', type: 'text', required: true },
  ],
  beda_nama: [
    { name: 'namaLengkap', label: 'Nama Saat Ini', type: 'text', required: true },
    { name: 'nik', label: 'NIK', type: 'text', required: true },
    { name: 'namaDokumen1', label: 'Nama di Dokumen Pertama', type: 'text', required: true },
    { name: 'namaDokumen2', label: 'Nama di Dokumen Kedua', type: 'text', required: true },
    { name: 'alasan', label: 'Alasan Perbedaan Nama', type: 'textarea', required: true, rows: 2 },
  ],
  belum_menikah: [
    { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
    { name: 'nik', label: 'NIK', type: 'text', required: true },
    { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
    { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
    { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
    { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
    { name: 'keperluan', label: 'Keperluan', type: 'text', required: true },
  ],
  tidak_mampu: [
    { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
    { name: 'nik', label: 'NIK', type: 'text', required: true },
    { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
    { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
    { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
    { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
    { name: 'penghasilanBulanan', label: 'Penghasilan Bulanan', type: 'text' },
    { name: 'keperluan', label: 'Keperluan', type: 'text', required: true },
  ],
  ahli_waris: [
    { name: 'namaPeninggal', label: 'Nama Peninggal (Mayit)', type: 'text', required: true },
    { name: 'nikPeninggal', label: 'NIK Peninggal', type: 'text', required: true },
    { name: 'alamatPeninggal', label: 'Alamat Peninggal', type: 'textarea', required: true, rows: 2 },
    { name: 'namaAhliWaris1', label: 'Nama Ahli Waris 1', type: 'text', required: true },
    { name: 'nikAhliWaris1', label: 'NIK Ahli Waris 1', type: 'text' },
    { name: 'hubunganAhliWaris1', label: 'Hubungan dengan Peninggal', type: 'text' },
    { name: 'namaAhliWaris2', label: 'Nama Ahli Waris 2', type: 'text' },
    { name: 'nikAhliWaris2', label: 'NIK Ahli Waris 2', type: 'text' },
    { name: 'hubunganAhliWaris2', label: 'Hubungan dengan Peninggal', type: 'text' },
    { name: 'keterangan', label: 'Keterangan Tambahan', type: 'textarea', rows: 2 },
  ],
  nikah_tidak_tercatat: [
    { name: 'namaPria', label: 'Nama Pria', type: 'text', required: true },
    { name: 'nikPria', label: 'NIK Pria', type: 'text', required: true },
    { name: 'tempatLahirPria', label: 'Tempat Lahir Pria', type: 'text' },
    { name: 'tanggalLahirPria', label: 'Tanggal Lahir Pria', type: 'date' },
    { name: 'namaWanita', label: 'Nama Wanita', type: 'text', required: true },
    { name: 'nikWanita', label: 'NIK Wanita', type: 'text', required: true },
    { name: 'tempatLahirWanita', label: 'Tempat Lahir Wanita', type: 'text' },
    { name: 'tanggalLahirWanita', label: 'Tanggal Lahir Wanita', type: 'date' },
    { name: 'tanggalPernikahan', label: 'Tanggal Pernikahan', type: 'date', required: true },
    { name: 'tempatPernikahan', label: 'Tempat Pernikahan', type: 'text', required: true },
  ],
  pemakaman: [
    { name: 'namaAlmarhum', label: 'Nama Almarhum/Almarhumah', type: 'text', required: true },
    { name: 'nikAlmarhum', label: 'NIK Almarhum', type: 'text' },
    { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
    { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
    { name: 'alamat', label: 'Alamat', type: 'textarea', required: true, rows: 2 },
    { name: 'tanggalMeninggal', label: 'Tanggal Meninggal', type: 'date', required: true },
    { name: 'lokasiMakam', label: 'Lokasi Makam', type: 'text', required: true },
    { name: 'namaKeluarga', label: 'Nama Keluarga yang Memohon', type: 'text', required: true },
  ],
  tidak_memiliki_rumah: [
    { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
    { name: 'nik', label: 'NIK', type: 'text', required: true },
    { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text' },
    { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date' },
    { name: 'pekerjaan', label: 'Pekerjaan', type: 'text' },
    { name: 'alamatTinggal', label: 'Alamat Tempat Tinggal Saat Ini', type: 'textarea', required: true, rows: 2 },
    { name: 'keterangan', label: 'Keterangan Tambahan', type: 'textarea', rows: 2 },
  ],
  undangan: [
    { name: 'tujuanYth', label: 'Tujuan Yth. (Nama & Jabatan)', type: 'textarea', required: true, rows: 2 },
    { name: 'hari', label: 'Hari', type: 'text', required: true },
    { name: 'tanggal', label: 'Tanggal', type: 'text', required: true },
    { name: 'bulan', label: 'Bulan', type: 'text', required: true },
    { name: 'tahun', label: 'Tahun', type: 'text', required: true },
    { name: 'pukul', label: 'Pukul', type: 'text', required: true },
    { name: 'tempat', label: 'Tempat', type: 'textarea', required: true, rows: 2 },
    { name: 'agendaAcara', label: 'Agenda Acara', type: 'textarea', required: true, rows: 3 },
    { name: 'daftarTembusan', label: 'Tembusan (opsional)', type: 'textarea', rows: 2 },
  ],
};

interface LetterFormProps {
  onSubmit: (data: any) => void;
  isLoading?: boolean;
}

export function LetterForm({ onSubmit, isLoading = false }: LetterFormProps) {
  const { letterType, letterData, setLetterData, reset } = useLetterStore();
  const fields = formConfigs[letterType];

  const handleChange = (field: string, value: string) => {
    setLetterData({ [field]: value });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const requiredFields = fields.filter(f => f.required);
    const missing = requiredFields.filter(f => !letterData[f.name]);
    if (missing.length > 0) {
      alert(`Kolom wajib diisi: ${missing.map(f => f.label).join(', ')}`);
      return;
    }
    onSubmit(letterData);
  };

  return (
    <motion.form
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      onSubmit={handleSubmit}
      className="bg-white rounded-2xl shadow-lg p-8 space-y-6"
    >
      <div className="mb-6 pb-6 border-b border-gray-200">
        <h3 className="text-xl font-bold text-gray-800">Isi Data Surat</h3>
        <p className="text-sm text-gray-600 mt-1">
          Lengkapi semua kolom yang ditandai dengan <span className="text-red-500">*</span>
        </p>
      </div>

      {/* Form fields grid */}
      <div className="space-y-6">
        {fields.map((field, index) => (
          <motion.div
            key={field.name}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: index * 0.02 }}
            className="group"
          >
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              {field.label}
              {field.required && <span className="text-red-500 ml-1">*</span>}
              {field.optional && <span className="text-gray-400 text-xs ml-1">(opsional)</span>}
            </label>

            {field.type === 'textarea' ? (
              <textarea
                value={letterData[field.name] || ''}
                onChange={(e) => handleChange(field.name, e.target.value)}
                placeholder={field.placeholder}
                rows={field.rows || 3}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all resize-none"
              />
            ) : field.type === 'select' ? (
              <select
                value={letterData[field.name] || ''}
                onChange={(e) => handleChange(field.name, e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
              >
                {field.options?.map(opt => (
                  <option key={opt.value} value={opt.value}>{opt.label}</option>
                ))}
              </select>
            ) : (
              <input
                type={field.type}
                value={letterData[field.name] || ''}
                onChange={(e) => handleChange(field.name, e.target.value)}
                placeholder={field.placeholder}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all"
              />
            )}
          </motion.div>
        ))}
      </div>

      {/* Action buttons */}
      <div className="flex gap-3 pt-6 border-t border-gray-200">
        <motion.button
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.98 }}
          type="button"
          onClick={reset}
          className="px-6 py-3 border border-gray-300 rounded-lg font-semibold text-gray-700 hover:bg-gray-50 transition-all flex items-center gap-2"
        >
          <RotateCcw className="w-4 h-4" />
          Reset
        </motion.button>
        <motion.button
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.98 }}
          type="submit"
          disabled={isLoading}
          className="flex-1 px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-700 rounded-lg font-semibold text-white hover:from-blue-700 hover:to-blue-800 disabled:opacity-50 transition-all flex items-center justify-center gap-2"
        >
          <Send className="w-4 h-4" />
          {isLoading ? 'Memproses...' : 'Lihat Preview & Unduh'}
        </motion.button>
      </div>
    </motion.form>
  );
}
