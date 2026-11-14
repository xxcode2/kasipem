'use client';

import { motion } from 'framer-motion';
import { useLetterStore } from '../lib/store';
import { LetterType } from '../types';
import {
  FileText,
  Heart,
  DollarSign,
  Landmark,
  Mail,
  Home,
  Users,
  Building2,
  AlertCircle,
  MapPin,
  Shield,
} from 'lucide-react';

const letterTypes = [
  // Category 1: Individu
  {
    id: 'domisili' as LetterType,
    title: 'Surat Keterangan Domisili',
    description: 'Untuk verifikasi tempat tinggal',
    icon: Home,
    color: 'from-blue-500 to-cyan-600',
    category: 'Individu',
    classification: '140',
  },
  {
    id: 'pisah_kk' as LetterType,
    title: 'Surat Keterangan Pisah KK',
    description: 'Untuk pemisahan Kartu Keluarga',
    icon: Users,
    color: 'from-indigo-500 to-blue-600',
    category: 'Individu',
    classification: 'PEM',
  },
  {
    id: 'skt_dayat' as LetterType,
    title: 'Surat Keterangan Tanah',
    description: 'Untuk sertifikat tanah dan properti',
    icon: Landmark,
    color: 'from-amber-500 to-orange-600',
    category: 'Properti',
    classification: 'PEM',
  },
  {
    id: 'beda_luas' as LetterType,
    title: 'Surat Beda Luas Tanah',
    description: 'Untuk selisih sertifikat dan SPPT',
    icon: MapPin,
    color: 'from-yellow-500 to-amber-600',
    category: 'Properti',
    classification: '141',
  },
  {
    id: 'beda_nama' as LetterType,
    title: 'Surat Beda Nama',
    description: 'Untuk perbedaan nama di dokumen',
    icon: FileText,
    color: 'from-slate-500 to-gray-600',
    category: 'Administrasi',
    classification: 'PEM',
  },
  {
    id: 'belum_menikah' as LetterType,
    title: 'Surat Belum Menikah',
    description: 'Untuk keperluan administrasi perkawinan',
    icon: Heart,
    color: 'from-pink-500 to-rose-600',
    category: 'Administrasi',
    classification: '460.2',
  },
  {
    id: 'tidak_mampu' as LetterType,
    title: 'Surat Tidak Mampu (SKTM)',
    description: 'Untuk keperluan bantuan sosial',
    icon: DollarSign,
    color: 'from-green-500 to-emerald-600',
    category: 'Sosial',
    classification: '460',
  },
  {
    id: 'ahli_waris' as LetterType,
    title: 'Surat Keterangan Ahli Waris',
    description: 'Untuk proses kewarisan dan pengurusan harta',
    icon: Shield,
    color: 'from-violet-500 to-purple-600',
    category: 'Hukum',
    classification: 'PEM',
  },
  {
    id: 'nikah_tidak_tercatat' as LetterType,
    title: 'Surat Nikah Tidak Tercatat',
    description: 'Untuk pengakuan perkawinan tidak tercatat',
    icon: Heart,
    color: 'from-red-500 to-pink-600',
    category: 'Administrasi',
    classification: 'KS',
  },
  {
    id: 'pemakaman' as LetterType,
    title: 'Surat Keterangan Pemakaman',
    description: 'Untuk izin dan proses pemakaman',
    icon: AlertCircle,
    color: 'from-gray-600 to-slate-700',
    category: 'Lainnya',
    classification: 'PEM',
  },
  {
    id: 'tidak_memiliki_rumah' as LetterType,
    title: 'Surat Pernyataan Tidak Memiliki Rumah',
    description: 'Untuk keterangan tidak memiliki properti',
    icon: Building2,
    color: 'from-cyan-500 to-blue-600',
    category: 'Administrasi',
    classification: 'KS',
  },
  {
    id: 'undangan' as LetterType,
    title: 'Surat Undangan Resmi',
    description: 'Untuk acara resmi desa/kelurahan',
    icon: Mail,
    color: 'from-purple-500 to-indigo-600',
    category: 'Administrasi',
    classification: 'Khusus',
  },
];

export function LetterTypeSelector() {
  const { letterType, setLetterType } = useLetterStore();
  
  // Group by category
  const categories = Array.from(new Set(letterTypes.map(t => t.category)));

  return (
    <div className="space-y-8">
      <div className="text-center mb-8">
        <h2 className="text-4xl font-bold text-gray-900 mb-3">
          Pembuat Surat Administrasi Desa
        </h2>
        <p className="text-lg text-gray-600 max-w-2xl mx-auto">
          Pilih jenis surat yang ingin Anda buat. Tersedia 12 jenis surat untuk berbagai keperluan administrasi.
        </p>
      </div>

      {/* Letter types by category */}
      {categories.map((category) => {
        const typesInCategory = letterTypes.filter(t => t.category === category);
        return (
          <div key={category} className="space-y-4">
            <div className="flex items-center gap-3">
              <div className="w-1 h-6 bg-gradient-to-b from-blue-500 to-purple-500 rounded-full" />
              <h3 className="text-xl font-semibold text-gray-800">{category}</h3>
              <span className="ml-auto px-3 py-1 bg-gray-100 text-gray-600 text-sm font-medium rounded-full">
                {typesInCategory.length} surat
              </span>
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {typesInCategory.map((type, index) => {
                const Icon = type.icon;
                const isSelected = letterType === type.id;

                return (
                  <motion.button
                    key={type.id}
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: index * 0.05 }}
                    onClick={() => setLetterType(type.id)}
                    className={`relative p-5 rounded-2xl transition-all duration-300 overflow-hidden group ${
                      isSelected
                        ? 'ring-2 ring-blue-500 shadow-xl scale-[1.02]'
                        : 'hover:shadow-lg hover:scale-[1.01]'
                    }`}
                  >
                    {/* Background gradient */}
                    <div
                      className={`absolute inset-0 bg-gradient-to-br ${type.color} transition-opacity duration-300 ${
                        isSelected ? 'opacity-5' : 'opacity-0 group-hover:opacity-3'
                      }`}
                    />

                    {/* Card content */}
                    <div className="relative z-10 text-left h-full flex flex-col">
                      <motion.div
                        animate={isSelected ? { scale: 1.15, rotate: 5 } : { scale: 1, rotate: 0 }}
                        className={`inline-flex w-10 h-10 items-center justify-center rounded-xl mb-3 transition-all ${
                          isSelected
                            ? `bg-gradient-to-br ${type.color} text-white shadow-lg`
                            : `bg-gradient-to-br ${type.color} text-white opacity-80 group-hover:opacity-100`
                        }`}
                      >
                        <Icon className="w-5 h-5" />
                      </motion.div>

                      <h3 className="font-semibold text-gray-900 text-sm mb-1 line-clamp-2">
                        {type.title}
                      </h3>
                      <p className="text-xs text-gray-600 mb-3 line-clamp-2">
                        {type.description}
                      </p>
                      
                      <div className="mt-auto flex items-center justify-between pt-2 border-t border-gray-200">
                        <div className="text-xs font-mono text-gray-500">
                          {type.classification}
                        </div>
                        {isSelected && (
                          <motion.div
                            layoutId="selected-badge"
                            className="px-2 py-1 bg-blue-100 text-blue-700 text-xs font-semibold rounded-full"
                            initial={{ scale: 0 }}
                            animate={{ scale: 1 }}
                          >
                            ✓ Dipilih
                          </motion.div>
                        )}
                      </div>
                    </div>

                    {/* Border effect */}
                    <div className={`absolute inset-0 rounded-2xl border-2 transition-all duration-300 pointer-events-none ${
                      isSelected ? `border-blue-400` : `border-gray-200 group-hover:border-gray-300`
                    }`} />
                  </motion.button>
                );
              })}
            </div>
          </div>
        );
      })}
    </div>
  );
}
