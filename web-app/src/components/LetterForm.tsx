'use client';

import { useMemo, useState } from 'react';
import { useLetterStore } from '../lib/store';
import { LETTER_MAP, isValidNik } from '../lib/letter-definitions';
import { formatDateSurat } from '../lib/letter-content';
import { Send, RotateCcw, X, AlertTriangle } from 'lucide-react';

interface LetterFormProps {
  isOpen: boolean;
  onClose: () => void;
  onPreview: () => void;
}

/**
 * Modal form per jenis surat. Field berasal dari LETTER_DEFINITIONS
 * (kanon yang sama dengan store/preview/PDF). Validasi inline — tanpa alert().
 * NIK bukan 16 digit hanya peringatan, bukan blokir keras.
 */
export function LetterForm({ isOpen, onClose, onPreview }: LetterFormProps) {
  const { letterType, letterData, setLetterData, reset } = useLetterStore();
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState('');

  const def = LETTER_MAP[letterType];
  const fields = def.fields;

  const nikWarnings = useMemo(() => {
    const warns: Record<string, string> = {};
    for (const f of fields) {
      if (f.name.toLowerCase().includes('nik')) {
        const v = (letterData[f.name] || '').trim();
        if (v && !isValidNik(v)) {
          warns[f.name] = 'NIK sebaiknya 16 digit angka.';
        }
      }
    }
    return warns;
  }, [fields, letterData]);

  if (!isOpen) return null;

  const handleChange = (field: string, value: string) => {
    setLetterData({ [field]: value });
    setErrors((prev) => {
      if (!prev[field]) return prev;
      const next = { ...prev };
      delete next[field];
      return next;
    });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const missing = fields.filter((f) => f.required && !(letterData[f.name] || '').trim());
    if (!letterData.nomorSurat?.trim() && def.nomorPrefix) {
      setFormError('Nomor surat wajib diisi.');
      return;
    }
    if (missing.length > 0) {
      const errs: Record<string, string> = {};
      for (const f of missing) errs[f.name] = `${f.label} wajib diisi.`;
      setErrors(errs);
      setFormError(`Masih ada ${missing.length} kolom wajib yang belum diisi: ${missing.map((f) => f.label).join(', ')}.`);
      return;
    }
    setFormError('');
    onPreview();
  };

  const handleReset = () => {
    reset();
    setErrors({});
    setFormError('');
  };

  return (
    <div
      className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
      onClick={onClose}
    >
      <div
        onClick={(e) => e.stopPropagation()}
        className="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-hidden flex flex-col"
      >
        <div className="bg-teal-700 text-white px-6 py-4 flex items-center justify-between shrink-0">
          <div>
            <h2 className="text-lg font-semibold">{def.title}</h2>
            <p className="text-sm text-teal-100">{def.description}</p>
          </div>
          <button onClick={onClose} className="text-white/80 hover:text-white" aria-label="Tutup">
            <X className="w-5 h-5" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-6 space-y-5">
          <p className="text-sm text-stone-600">
            Lengkapi kolom bertanda <span className="text-red-600">*</span>. Tanggal surat otomatis
            hari ini: <strong>{formatDateSurat(letterData.tanggalSurat)}</strong>.
          </p>

          {/* Nomor surat — editable, default per pola jenis surat */}
          <div>
            <label className="block text-sm font-semibold text-stone-700 mb-1.5">
              Nomor Surat <span className="text-red-600">*</span>
            </label>
            <input
              type="text"
              value={letterData.nomorSurat || ''}
              onChange={(e) => handleChange('nomorSurat', e.target.value)}
              placeholder={def.nomorPrefix ? `${def.nomorPrefix} / ... / ${def.nomorSuffix} / ${new Date().getFullYear()}` : 'Isi nomor surat secara manual'}
              className="w-full px-4 py-2.5 border border-stone-300 rounded-lg focus:ring-2 focus:ring-teal-700 focus:border-teal-700"
            />
          </div>

          {fields.map((field) => (
            <div key={field.name}>
              <label className="block text-sm font-semibold text-stone-700 mb-1.5">
                {field.label}
                {field.required && <span className="text-red-600 ml-1">*</span>}
              </label>

              {field.type === 'textarea' ? (
                <textarea
                  value={letterData[field.name] || ''}
                  onChange={(e) => handleChange(field.name, e.target.value)}
                  placeholder={field.placeholder}
                  rows={field.rows || 3}
                  className="w-full px-4 py-2.5 border border-stone-300 rounded-lg focus:ring-2 focus:ring-teal-700 focus:border-teal-700 resize-none"
                />
              ) : field.type === 'select' ? (
                <select
                  value={letterData[field.name] || ''}
                  onChange={(e) => handleChange(field.name, e.target.value)}
                  className="w-full px-4 py-2.5 border border-stone-300 rounded-lg focus:ring-2 focus:ring-teal-700 focus:border-teal-700 bg-white"
                >
                  {field.options?.map((opt) => (
                    <option key={opt.value} value={opt.value}>
                      {opt.label}
                    </option>
                  ))}
                </select>
              ) : (
                <input
                  type={field.type}
                  value={letterData[field.name] || ''}
                  onChange={(e) => handleChange(field.name, e.target.value)}
                  placeholder={field.placeholder}
                  maxLength={field.name.toLowerCase().includes('nik') ? 16 : undefined}
                  inputMode={field.name.toLowerCase().includes('nik') ? 'numeric' : undefined}
                  className="w-full px-4 py-2.5 border border-stone-300 rounded-lg focus:ring-2 focus:ring-teal-700 focus:border-teal-700"
                />
              )}

              {errors[field.name] && (
                <p className="mt-1 text-sm text-red-600">{errors[field.name]}</p>
              )}
              {nikWarnings[field.name] && !errors[field.name] && (
                <p className="mt-1 text-sm text-amber-700 flex items-center gap-1">
                  <AlertTriangle className="w-4 h-4 shrink-0" />
                  {nikWarnings[field.name]}
                </p>
              )}
            </div>
          ))}

          {formError && (
            <div className="text-sm text-red-700 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
              {formError}
            </div>
          )}

          <div className="flex gap-3 pt-4 border-t border-stone-200">
            <button
              type="button"
              onClick={handleReset}
              className="px-5 py-2.5 border border-stone-300 rounded-lg font-medium text-stone-700 hover:bg-stone-100 flex items-center gap-2"
            >
              <RotateCcw className="w-4 h-4" />
              Reset
            </button>
            <button
              type="submit"
              className="flex-1 px-5 py-2.5 bg-teal-700 rounded-lg font-medium text-white hover:bg-teal-800 flex items-center justify-center gap-2"
            >
              <Send className="w-4 h-4" />
              Lihat Preview
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
