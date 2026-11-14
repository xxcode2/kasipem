import { LetterType } from '../types';
import { LETTER_MAP } from './letter-definitions';

/**
 * Riwayat Surat — pengganti macro Arsip di APLIKASI SURAT.xlsm lama.
 * Lokal saja (localStorage, kunci 'kasipem_riwayat'): setiap surat yang
 * diunduh/dicetak tercatat (tanggal, nomor, jenis, nama pemohon) dan bisa
 * dilihat & dihapus pengguna. TIDAK ada server, TIDAK ada data warga
 * yang dimuat sebelumnya — hanya surat yang dibuat di perangkat ini.
 */

export interface HistoryEntry {
  id: string;
  /** ISO timestamp saat surat diunduh/dicetak. */
  tanggal: string;
  nomor: string;
  jenis: LetterType;
  judul: string;
  namaPemohon: string;
}

const STORAGE_KEY = 'kasipem_riwayat';
const MAX_ENTRIES = 500;

function isBrowser(): boolean {
  return typeof window !== 'undefined' && typeof window.localStorage !== 'undefined';
}

export function loadHistory(): HistoryEntry[] {
  if (!isBrowser()) return [];
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw);
    if (!Array.isArray(parsed)) return [];
    return parsed.filter(
      (e) =>
        e && typeof e.id === 'string' && typeof e.tanggal === 'string' && typeof e.jenis === 'string',
    ) as HistoryEntry[];
  } catch {
    return [];
  }
}

function saveHistory(entries: HistoryEntry[]): void {
  if (!isBrowser()) return;
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(entries.slice(0, MAX_ENTRIES)));
  } catch {
    // localStorage penuh / tidak tersedia — riwayat dilewati, jangan crash.
  }
}

/** Nama pemohon dari data isian (field kanon per jenis surat). */
export function getApplicantName(type: LetterType, data: Record<string, any>): string {
  const def = LETTER_MAP[type];
  const field = def?.applicantField;
  if (field) {
    const v = (data[field] || '').toString().trim();
    if (v) return v;
  }
  // Fallback: field nama umum pertama yang terisi.
  for (const f of ['namaLengkap', 'namaPemohon', 'namaYangMenyatakan', 'nama1', 'namaPelapor']) {
    const v = (data[f] || '').toString().trim();
    if (v) return v;
  }
  return '-';
}

/** Catat satu surat ke riwayat (dipanggil saat unduh PDF / cetak). */
export function recordHistory(
  type: LetterType,
  data: Record<string, any>,
): HistoryEntry | null {
  const def = LETTER_MAP[type];
  if (!def) return null;
  const entry: HistoryEntry = {
    id: `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
    tanggal: new Date().toISOString(),
    nomor: (data.nomorSurat || '').toString().trim(),
    jenis: type,
    judul: def.title,
    namaPemohon: getApplicantName(type, data),
  };
  const entries = loadHistory();
  entries.unshift(entry);
  saveHistory(entries);
  return entry;
}

export function deleteHistoryEntry(id: string): HistoryEntry[] {
  const entries = loadHistory().filter((e) => e.id !== id);
  saveHistory(entries);
  return entries;
}

export function clearHistory(): HistoryEntry[] {
  saveHistory([]);
  return [];
}
