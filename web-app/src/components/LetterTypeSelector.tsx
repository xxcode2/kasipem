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
} from 'lucide-react';

const letterTypes = [
  {
    id: 'domisili' as LetterType,
    title: 'Surat Keterangan Domisili',
    description: 'Untuk verifikasi tempat tinggal',
    icon: FileText,
    color: 'from-blue-500 to-blue-600',
    classification: '140',
  },
  {
    id: 'belum_menikah' as LetterType,
    title: 'Surat Belum Menikah',
    description: 'Untuk keperluan administrasi perkawinan',
    icon: Heart,
    color: 'from-pink-500 to-pink-600',
    classification: '460.2',
  },
  {
    id: 'tidak_mampu' as LetterType,
    title: 'Surat Tidak Mampu (SKTM)',
    description: 'Untuk keperluan bantuan sosial',
    icon: DollarSign,
    color: 'from-green-500 to-green-600',
    classification: '460',
  },
  {
    id: 'beda_luas' as LetterType,
    title: 'Surat Beda Luas Tanah',
    description: 'Untuk selisih sertifikat dan SPPT',
    icon: Landmark,
    color: 'from-amber-500 to-amber-600',
    classification: '141',
  },
  {
    id: 'undangan' as LetterType,
    title: 'Surat Undangan Resmi',
    description: 'Untuk acara resmi desa/kelurahan',
    icon: Mail,
    color: 'from-purple-500 to-purple-600',
    classification: 'Khusus',
  },
];

export function LetterTypeSelector() {
  const { letterType, setLetterType } = useLetterStore();

  return (
    <div className="space-y-6">
      <div className="text-center mb-8">
        <h2 className="text-3xl font-bold text-gray-800 mb-2">
          Pilih Jenis Surat
        </h2>
        <p className="text-gray-600">
          Pilih jenis surat yang ingin Anda buat
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {letterTypes.map((type, index) => {
          const Icon = type.icon;
          const isSelected = letterType === type.id;

          return (
            <motion.button
              key={type.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              onClick={() => setLetterType(type.id)}
              className={`relative p-6 rounded-xl transition-all duration-300 ${
                isSelected
                  ? 'ring-2 ring-blue-500 shadow-lg'
                  : 'hover:shadow-md'
              }`}
            >
              {/* Background gradient */}
              <div
                className={`absolute inset-0 rounded-xl bg-gradient-to-br ${type.color} opacity-0 transition-opacity duration-300 ${
                  isSelected ? 'opacity-10' : 'group-hover:opacity-5'
                }`}
              />

              {/* Card content */}
              <div className="relative z-10 text-left">
                <motion.div
                  animate={isSelected ? { scale: 1.2 } : { scale: 1 }}
                  className={`inline-block p-3 rounded-lg mb-3 transition-colors ${
                    isSelected
                      ? `bg-gradient-to-br ${type.color} text-white`
                      : 'bg-gray-100 text-gray-600'
                  }`}
                >
                  <Icon className="w-6 h-6" />
                </motion.div>

                <h3 className="font-bold text-gray-800 text-sm mb-1">
                  {type.title}
                </h3>
                <p className="text-xs text-gray-600 mb-2">
                  {type.description}
                </p>
                <div className="text-xs font-mono text-gray-500">
                  Klasifikasi: {type.classification}
                </div>
              </div>

              {/* Selection indicator */}
              {isSelected && (
                <motion.div
                  layoutId="selected-indicator"
                  className="absolute top-3 right-3 w-3 h-3 bg-blue-500 rounded-full"
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                />
              )}

              {/* Hover effect */}
              <div className="absolute inset-0 rounded-xl bg-gradient-to-br from-white/20 to-transparent opacity-0 hover:opacity-100 transition-opacity duration-300 pointer-events-none" />
            </motion.button>
          );
        })}
      </div>
    </div>
  );
}
