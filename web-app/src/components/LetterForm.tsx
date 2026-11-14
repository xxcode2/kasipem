'use client';

import { motion } from 'framer-motion';
import { useLetterStore } from '../lib/store';
import { LetterType } from '../types';

interface FormField {
  name: string;
  label: string;
  type: string;
  placeholder?: string;
  required?: boolean;
  rows?: number;
  optional?: boolean;
}

const formConfigs: Record<LetterType, FormField[]> = {
  domisili: [
    { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', placeholder: 'Contoh: Budi Santoso', required: true },
    { name: 'nik', label: 'NIK (Nomor Identitas Kependudukan)', type: 'text', placeholder: '16XXXXXXXXXX', required: true },
    { name: 'jenisKelamin', label: 'Jenis Kelamin', type: 'select', optional: true },
    { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text', placeholder: 'Contoh: Bandung', optional: true },
    { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date', optional: true },
    { name: 'pekerjaan', label: 'Pekerjaan', type: 'text', placeholder: 'Contoh: Wiraswasta', optional: true },
    { name: 'alamatAsal', label: 'Alamat Asal (KTP)', type: 'textarea', placeholder: 'Alamat lengkap di KTP', required: true, rows: 2 },
    { name: 'rt', label: 'RT', type: 'text', placeholder: '001', required: true },
    { name: 'rw', label: 'RW', type: 'text', placeholder: '001', required: true },
    { name: 'alamatDomisili', label: 'Alamat Domisili Saat Ini', type: 'textarea', placeholder: 'Alamat saat ini', required: true, rows: 2 },
    { name: 'keperluanSurat', label: 'Keperluan Surat', type: 'text', placeholder: 'Contoh: Pembuatan Rekening Bank', required: true },
  ],
  belum_menikah: [
    { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', placeholder: 'Contoh: Budi Santoso', required: true },
    { name: 'nik', label: 'NIK', type: 'text', placeholder: '16XXXXXXXXXX', required: true },
    { name: 'jenisKelamin', label: 'Jenis Kelamin', type: 'select', optional: true },
    { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text', optional: true },
    { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date', optional: true },
    { name: 'pekerjaan', label: 'Pekerjaan', type: 'text', optional: true },
    { name: 'alamatLengkap', label: 'Alamat Lengkap', type: 'textarea', placeholder: 'Alamat lengkap', required: true, rows: 2 },
    { name: 'rt', label: 'RT', type: 'text', placeholder: '001', required: true },
    { name: 'rw', label: 'RW', type: 'text', placeholder: '001', required: true },
    { name: 'tujuanSekolah', label: 'Tujuan/Nama Instansi', type: 'text', placeholder: 'Contoh: Universitas Indonesia', required: true },
    { name: 'keperluan', label: 'Keperluan', type: 'text', placeholder: 'Contoh: Pendaftaran Mahasiswa Baru', required: true },
  ],
  tidak_mampu: [
    { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
    { name: 'nik', label: 'NIK', type: 'text', required: true },
    { name: 'jenisKelamin', label: 'Jenis Kelamin', type: 'select', optional: true },
    { name: 'tempatLahir', label: 'Tempat Lahir', type: 'text', optional: true },
    { name: 'tanggalLahir', label: 'Tanggal Lahir', type: 'date', optional: true },
    { name: 'pekerjaan', label: 'Pekerjaan', type: 'text', optional: true },
    { name: 'alamatLengkap', label: 'Alamat Lengkap', type: 'textarea', required: true, rows: 2 },
    { name: 'rt', label: 'RT', type: 'text', required: true },
    { name: 'rw', label: 'RW', type: 'text', required: true },
    { name: 'tujuanSekolah', label: 'Tujuan/Nama Sekolah/Instansi', type: 'text', required: true },
    { name: 'keperluan', label: 'Keperluan', type: 'text', required: true },
  ],
  beda_luas: [
    { name: 'namaLengkap', label: 'Nama Lengkap', type: 'text', required: true },
    { name: 'nik', label: 'NIK', type: 'text', required: true },
    { name: 'alamatLengkap', label: 'Alamat Lengkap', type: 'textarea', required: true, rows: 2 },
    { name: 'rt', label: 'RT', type: 'text', required: true },
    { name: 'rw', label: 'RW', type: 'text', required: true },
    { name: 'nomorSertifikat', label: 'Nomor Sertifikat', type: 'text', required: true },
    { name: 'luasSertifikat', label: 'Luas Sertifikat (m²)', type: 'text', required: true },
    { name: 'nomorSPPT', label: 'Nomor SPPT', type: 'text', required: true },
    { name: 'luasSPPT', label: 'Luas SPPT (m²)', type: 'text', required: true },
  ],
  undangan: [
    { name: 'tujuanYth', label: 'Tujuan Yth. (Nama & Jabatan)', type: 'textarea', required: true, rows: 2 },
    { name: 'hari', label: 'Hari', type: 'text', placeholder: 'Contoh: Senin', required: true },
    { name: 'tanggal', label: 'Tanggal', type: 'text', placeholder: 'Contoh: 15 Januari 2024', required: true },
    { name: 'pukul', label: 'Pukul (Waktu)', type: 'text', placeholder: 'Contoh: 09:00', required: true },
    { name: 'tempat', label: 'Tempat', type: 'textarea', placeholder: 'Lokasi acara', required: true, rows: 2 },
    { name: 'agendaAcara', label: 'Agenda Acara', type: 'textarea', placeholder: 'Uraian acara', required: true, rows: 3 },
    { name: 'daftarTembusan', label: 'Tembusan (opsional)', type: 'textarea', placeholder: 'Nama penerima tembusan', rows: 2, optional: true },
  ],
};

interface LetterFormProps {
  onSubmit: (data: any) => void;
  isLoading?: boolean;
}

export function LetterForm({ onSubmit, isLoading = false }: LetterFormProps) {
  const { letterType, letterData, setLetterData } = useLetterStore();
  const fields = formConfigs[letterType];

  const handleChange = (field: string, value: string) => {
    setLetterData({ [field]: value });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(letterData);
  };

  return (
    <motion.form
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      onSubmit={handleSubmit}
      className="space-y-5 bg-white rounded-xl shadow-lg p-8"
    >
      <h3 className="text-2xl font-bold text-gray-800 mb-6">
        Isi Data Surat
      </h3>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        {fields.map((field, index) => {
          const value = (letterData as any)[field.name] || '';
          const isRequired = field.required && !field.optional;

          return (
            <motion.div
              key={field.name}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.05 }}
              className={field.type === 'textarea' ? 'md:col-span-2' : ''}
            >
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                {field.label}
                {field.optional && (
                  <span className="text-xs text-gray-500 font-normal">
                    {' '}
                    (opsional)
                  </span>
                )}
              </label>

              {field.type === 'select' ? (
                <select
                  value={value}
                  onChange={(e) => handleChange(field.name, e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                >
                  <option value="">-- Pilih --</option>
                  <option value="Laki-laki">Laki-laki</option>
                  <option value="Perempuan">Perempuan</option>
                </select>
              ) : field.type === 'textarea' ? (
                <textarea
                  value={value}
                  onChange={(e) => handleChange(field.name, e.target.value)}
                  rows={field.rows || 3}
                  placeholder={field.placeholder}
                  required={isRequired}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none transition-all"
                />
              ) : (
                <input
                  type={field.type}
                  value={value}
                  onChange={(e) => handleChange(field.name, e.target.value)}
                  placeholder={field.placeholder}
                  required={isRequired}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                />
              )}
            </motion.div>
          );
        })}
      </div>

      <div className="flex gap-3 pt-6 border-t">
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          type="submit"
          disabled={isLoading}
          className="flex-1 bg-gradient-to-r from-blue-600 to-teal-600 text-white font-semibold py-3 rounded-lg hover:shadow-lg transition-shadow disabled:opacity-50"
        >
          {isLoading ? 'Memproses...' : 'Pratinjau & Unduh Surat'}
        </motion.button>
      </div>
    </motion.form>
  );
}
