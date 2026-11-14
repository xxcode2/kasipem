'use client';

import { useEffect, useState } from 'react';
import { HistoryEntry, clearHistory, deleteHistoryEntry, loadHistory } from '../lib/history';
import { formatDateSurat } from '../lib/letter-content';
import { History, Trash2, X } from 'lucide-react';

interface RiwayatSuratProps {
  isOpen: boolean;
  onClose: () => void;
}

/**
 * Modal Riwayat Surat (localStorage 'kasipem_riwayat').
 * Menampilkan surat yang pernah diunduh/dicetak di perangkat ini
 * (tanggal, nomor, jenis, nama pemohon); pengguna bisa menghapus
 * satu entri atau semuanya. Tidak ada server.
 */
export function RiwayatSurat({ isOpen, onClose }: RiwayatSuratProps) {
  const [entries, setEntries] = useState<HistoryEntry[]>([]);

  useEffect(() => {
    if (isOpen) setEntries(loadHistory());
  }, [isOpen]);

  if (!isOpen) return null;

  const handleDelete = (id: string) => {
    setEntries(deleteHistoryEntry(id));
  };

  const handleClear = () => {
    setEntries(clearHistory());
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
          <div className="flex items-center gap-3">
            <History className="w-5 h-5" />
            <div>
              <h2 className="text-lg font-semibold">Riwayat Surat</h2>
              <p className="text-sm text-teal-100">
                Surat yang pernah diunduh / dicetak di perangkat ini ({entries.length} entri)
              </p>
            </div>
          </div>
          <button onClick={onClose} className="text-white/80 hover:text-white" aria-label="Tutup">
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="flex-1 overflow-y-auto p-6">
          {entries.length === 0 ? (
            <p className="text-center text-stone-500 py-10">
              Belum ada surat yang diunduh atau dicetak.
            </p>
          ) : (
            <ul className="divide-y divide-stone-200">
              {entries.map((e) => (
                <li key={e.id} className="py-3 flex items-start justify-between gap-3">
                  <div className="min-w-0">
                    <p className="font-medium text-stone-800 truncate">{e.namaPemohon}</p>
                    <p className="text-sm text-stone-600">{e.judul}</p>
                    <p className="text-xs text-stone-500 mt-0.5">
                      {formatDateSurat(e.tanggal.slice(0, 10))}
                      {e.nomor ? ` • Nomor: ${e.nomor}` : ' • Nomor: —'}
                    </p>
                  </div>
                  <button
                    onClick={() => handleDelete(e.id)}
                    className="shrink-0 p-2 text-stone-400 hover:text-red-600 hover:bg-red-50 rounded-lg"
                    aria-label={`Hapus riwayat ${e.namaPemohon}`}
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>

        {entries.length > 0 && (
          <div className="bg-white border-t border-stone-200 px-6 py-4 flex justify-end shrink-0">
            <button
              onClick={handleClear}
              className="px-5 py-2 text-red-700 border border-red-300 rounded-lg hover:bg-red-50 font-medium flex items-center gap-2"
            >
              <Trash2 className="w-4 h-4" />
              Hapus Semua Riwayat
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
