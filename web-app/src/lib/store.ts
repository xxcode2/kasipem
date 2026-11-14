import { create } from 'zustand';
import { LetterStore, HeaderData, LetterType } from '../types';
import { defaultLetterData } from './letter-definitions';

/**
 * Default kop desa ASLI (dari analisis DOCX repo) — bukan placeholder.
 * Nama pejabat ASLI dari user 2026-11-14 ~23:28 WIB. TIDAK ADA NIP —
 * nipPejabat/nipSekdes dibiarkan kosong dan baris NIP tidak dirender
 * di preview/PDF/cetak selama kosong.
 */
export const defaultHeader: HeaderData = {
  namaKabupaten: 'Subang',
  namaKecamatan: 'Kasomalang',
  namaDesa: 'Kasomalang Kulon',
  alamatKantor: 'Jln. Raya Kasomalang No.01 Subang 41281',
  namaPejabat: 'H. AMIRUDIN, S.Pd.I',
  nipPejabat: '',
  jabatanPejabat: 'Kepala Desa',
  namaSekdes: 'APIPUDIN, S.IP',
  nipSekdes: '',
  penandatanganDefault: 'kades',
  logoGaruda: '/kop/kades.jpg',
  logoSubang: '/kop/sekdes.jpg',
};

export const useLetterStore = create<LetterStore>((set, get) => ({
  letterType: 'domisili',
  headerData: defaultHeader,
  letterData: defaultLetterData('domisili'),

  setLetterType: (type: LetterType) => {
    // Reset ke default field tipe itu — jangan bocor data tipe lain.
    set({
      letterType: type,
      letterData: defaultLetterData(type),
    });
  },

  setHeaderData: (data: Partial<HeaderData>) => {
    set((state) => ({
      headerData: { ...state.headerData, ...data },
    }));
  },

  setLetterData: (data: Record<string, any>) => {
    set((state) => ({
      letterData: { ...state.letterData, ...data },
    }));
  },

  reset: () => {
    const { letterType } = get();
    set({
      letterData: defaultLetterData(letterType),
    });
  },

  loadHeaderFromStorage: () => {
    if (typeof window !== 'undefined') {
      const stored = localStorage.getItem('kasipem_header');
      if (stored) {
        try {
          const data = JSON.parse(stored);
          set({ headerData: { ...defaultHeader, ...data } });
        } catch (e) {
          console.error('Failed to load header from storage:', e);
        }
      }
    }
  },

  saveHeaderToStorage: () => {
    if (typeof window !== 'undefined') {
      const { headerData } = get();
      localStorage.setItem('kasipem_header', JSON.stringify(headerData));
    }
  },
}));
