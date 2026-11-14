'use client';

import { useMemo, useState } from 'react';
import { useLetterStore } from '../lib/store';
import { LETTER_MAP, isValidNik, nomorPlaceholder, visibleFields, FormField } from '../lib/letter-definitions';
import { formatDateSurat } from '../lib/letter-content';
import { Send, RotateCcw, X, AlertTriangle, Plus, Trash2 } from 'lucide-react';

interface LetterFormProps {
  isOpen: boolean;
  onClose: () => void;
  onPreview: () => void;
}

/**
 * Modal form per jenis surat. Field berasal dari LETTER_DEFINITIONS
 * (kanon yang sama dengan store/preview/PDF). Validasi inline — tanpa alert().
 * NIK bukan 16 digit hanya peringatan, bukan blokir keras.
 *
 * Fase 2: field kondisional (visibleWhen — mis. seksi F-2.01 per jenis
 * pelaporan, varian domisili/SKTM) dan field tabel (daftar anggota/anak/
 * kolektif) dengan tambah/hapus baris.
 */
export function LetterForm({ isOpen, onClose, onPreview }: LetterFormProps) {
  const { letterType, letterData, setLetterData, reset } = useLetterStore();
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState('');

  const def = LETTER_MAP[letterType];
  const fields = visibleFields(def, letterData);

  const nikWarnings = useMemo(() => {
    const warns: Record<string, string> = {};
    for (const f of fields) {
      if (f.type !== 'table' && f.name.toLowerCase().includes('nik')) {
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
    const missing = fields.filter((f) => {
      if (!f.required) return false;
      if (f.type === 'table') {
        const rows = Array.isArray(letterData[f.name]) ? letterData[f.name] : [];
        const filled = rows.filter((r: Record<string, string>) =>
          Object.values(r || {}).some((v) => (v || '').toString().trim()),
        );
        return filled.length === 0;
      }
      return !(letterData[f.name] || '').toString().trim();
    });
    // Fase 3: nomor surat BOLEH kosong (default kosong; diisi saat
    // registrasi). Preview/PDF menampilkan garis titik-titik bila kosong.
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

          {/* Nomor surat — editable, default KOSONG (Fase 3); placeholder
              menampilkan pola sebagai contoh. Boleh kosong. */}
          {!def.hideNomor && (
            <div>
              <label className="block text-sm font-semibold text-stone-700 mb-1.5">
                Nomor Surat{' '}
                <span className="font-normal text-stone-500">
                  (opsional — boleh kosong, diisi saat registrasi)
                </span>
              </label>
              <input
                type="text"
                value={letterData.nomorSurat || ''}
                onChange={(e) => handleChange('nomorSurat', e.target.value)}
                placeholder={nomorPlaceholder(def)}
                className="w-full px-4 py-2.5 border border-stone-300 rounded-lg focus:ring-2 focus:ring-teal-700 focus:border-teal-700"
              />
            </div>
          )}

          {fields.map((field) => (
            <div key={field.name}>
              <label className="block text-sm font-semibold text-stone-700 mb-1.5">
                {field.label}
                {field.required && <span className="text-red-600 ml-1">*</span>}
              </label>

              {field.type === 'table' ? (
                <TableFieldEditor
                  field={field}
                  rows={Array.isArray(letterData[field.name]) ? letterData[field.name] : []}
                  onChange={(rows) => handleChange(field.name, rows as unknown as string)}
                />
              ) : field.type === 'textarea' ? (
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

/** Editor tabel sederhana: baris bisa ditambah/dihapus, maks. field.maxRows. */
function TableFieldEditor({
  field,
  rows,
  onChange,
}: {
  field: FormField;
  rows: Record<string, string>[];
  onChange: (rows: Record<string, string>[]) => void;
}) {
  const columns = field.columns || [];
  const maxRows = field.maxRows ?? 10;

  const emptyRow = () => Object.fromEntries(columns.map((c) => [c.name, '']));

  const setCell = (rowIdx: number, col: string, value: string) => {
    const next = rows.map((r, i) => (i === rowIdx ? { ...r, [col]: value } : r));
    onChange(next);
  };

  const addRow = () => {
    if (rows.length >= maxRows) return;
    onChange([...rows, emptyRow()]);
  };

  const removeRow = (rowIdx: number) => {
    if (rows.length <= 1) {
      onChange([emptyRow()]);
      return;
    }
    onChange(rows.filter((_, i) => i !== rowIdx));
  };

  return (
    <div className="overflow-x-auto border border-stone-300 rounded-lg">
      <table className="w-full text-sm">
        <thead>
          <tr className="bg-stone-100">
            <th className="px-2 py-2 text-left font-semibold text-stone-700 w-8">#</th>
            {columns.map((c) => (
              <th key={c.name} className="px-2 py-2 text-left font-semibold text-stone-700 min-w-[110px]">
                {c.label}
              </th>
            ))}
            <th className="px-2 py-2 w-10" />
          </tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr key={i} className="border-t border-stone-200">
              <td className="px-2 py-1.5 text-stone-500">{i + 1}</td>
              {columns.map((c) => (
                <td key={c.name} className="px-1 py-1.5">
                  <input
                    type={c.name.toLowerCase().includes('tanggal') ? 'date' : 'text'}
                    value={r[c.name] || ''}
                    onChange={(e) => setCell(i, c.name, e.target.value)}
                    maxLength={c.name.toLowerCase().includes('nik') ? 16 : undefined}
                    className="w-full px-2 py-1.5 border border-stone-200 rounded focus:ring-1 focus:ring-teal-700 focus:border-teal-700"
                  />
                </td>
              ))}
              <td className="px-1 py-1.5">
                <button
                  type="button"
                  onClick={() => removeRow(i)}
                  className="p-1.5 text-stone-400 hover:text-red-600 rounded"
                  aria-label={`Hapus baris ${i + 1}`}
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="p-2 border-t border-stone-200 bg-stone-50 flex items-center justify-between">
        <span className="text-xs text-stone-500">
          {rows.length} / {maxRows} baris
        </span>
        <button
          type="button"
          onClick={addRow}
          disabled={rows.length >= maxRows}
          className="px-3 py-1.5 text-sm bg-teal-700 text-white rounded-lg hover:bg-teal-800 disabled:opacity-40 flex items-center gap-1"
        >
          <Plus className="w-4 h-4" />
          Tambah Baris
        </button>
      </div>
    </div>
  );
}
