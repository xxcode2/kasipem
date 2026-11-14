'use client';

import { useMemo, useState } from 'react';
import { LetterType } from '../types';
import { LETTER_CATEGORIES, LETTER_DEFINITIONS } from '../lib/letter-definitions';
import {
  FileText,
  Heart,
  DollarSign,
  Landmark,
  Mail,
  Home,
  Users,
  Building2,
  MapPin,
  Shield,
  Search,
  Scale,
  ScrollText,
} from 'lucide-react';

const ICONS: Record<LetterType, typeof FileText> = {
  domisili: Home,
  pisah_kk: Users,
  skt_dayat: Landmark,
  beda_luas: MapPin,
  beda_nama: FileText,
  belum_menikah: Heart,
  tidak_mampu: DollarSign,
  ahli_waris: Scale,
  nikah_tidak_tercatat: Heart,
  pemakaman: ScrollText,
  tidak_memiliki_rumah: Building2,
  undangan: Mail,
};

interface LetterTypeSelectorProps {
  onSelect: (type: LetterType) => void;
}

/**
 * Grid 12 kartu surat + pencarian + filter kategori.
 * Klik kartu -> modal form (ditangani parent lewat onSelect).
 * Palet kalem: putih/stone, aksen teal tua, tanpa animasi berputar.
 */
export function LetterTypeSelector({ onSelect }: LetterTypeSelectorProps) {
  const [query, setQuery] = useState('');
  const [category, setCategory] = useState<string>('Semua');

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    return LETTER_DEFINITIONS.filter((d) => {
      if (category !== 'Semua' && d.category !== category) return false;
      if (!q) return true;
      return (
        d.title.toLowerCase().includes(q) ||
        d.shortTitle.toLowerCase().includes(q) ||
        d.description.toLowerCase().includes(q)
      );
    });
  }, [query, category]);

  return (
    <div>
      {/* Pencarian + filter */}
      <div className="flex flex-col sm:flex-row gap-3 mb-6">
        <div className="relative flex-1">
          <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-stone-400" />
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Cari jenis surat..."
            className="w-full pl-9 pr-4 py-2.5 bg-white border border-stone-300 rounded-lg focus:ring-2 focus:ring-teal-700 focus:border-teal-700 text-sm"
          />
        </div>
        <div className="flex flex-wrap gap-2">
          {['Semua', ...LETTER_CATEGORIES].map((c) => (
            <button
              key={c}
              onClick={() => setCategory(c)}
              className={`px-3 py-2 rounded-lg text-sm font-medium border transition-colors ${
                category === c
                  ? 'bg-teal-700 text-white border-teal-700'
                  : 'bg-white text-stone-600 border-stone-300 hover:border-teal-700 hover:text-teal-800'
              }`}
            >
              {c}
            </button>
          ))}
        </div>
      </div>

      {filtered.length === 0 ? (
        <p className="text-center text-stone-500 py-10 bg-white rounded-xl border border-stone-200">
          Tidak ada surat yang cocok dengan pencarian Anda.
        </p>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {filtered.map((type) => {
            const Icon = ICONS[type.id];
            return (
              <button
                key={type.id}
                onClick={() => onSelect(type.id)}
                className="text-left bg-white rounded-xl border border-stone-200 p-5 shadow-sm hover:shadow-md hover:border-teal-700 transition-all"
              >
                <div className="inline-flex w-10 h-10 items-center justify-center rounded-lg bg-teal-50 text-teal-800 mb-3">
                  <Icon className="w-5 h-5" />
                </div>
                <h3 className="font-semibold text-stone-900 text-sm mb-1">{type.shortTitle}</h3>
                <p className="text-xs text-stone-600 mb-3 leading-relaxed">{type.description}</p>
                <div className="flex items-center justify-between pt-2 border-t border-stone-100">
                  <span className="text-xs font-medium text-teal-800 bg-teal-50 px-2 py-0.5 rounded-full">
                    {type.category}
                  </span>
                  <span className="text-xs font-mono text-stone-400">
                    {type.nomorPrefix ? `${type.nomorPrefix}/${type.nomorSuffix}` : 'Nomor manual'}
                  </span>
                </div>
              </button>
            );
          })}
        </div>
      )}
    </div>
  );
}
