import { create } from 'zustand';
import { LetterStore, HeaderData, LetterData, LetterType } from '../types';

const defaultHeader: HeaderData = {
  namaKabupaten: 'KABUPATEN',
  namaKecamatan: 'KECAMATAN',
  namaDesa: 'DESA/KELURAHAN',
  alamatKantor: 'Jl. Raya No. 123',
  namaPejabat: 'H. Nama Kepala Desa',
  atnPejabat: false,
  nipPejabat: '1234567890',
  jabatanPejabat: 'Kepala Desa',
};

const defaultLetterData: Record<LetterType, LetterData> = {
  domisili: {
    namaLengkap: '',
    nik: '',
    alamatLengkap: '',
    rt: '',
    rw: '',
    alamatAsal: '',
    alamatDomisili: '',
    keperluanSurat: '',
  },
  belum_menikah: {
    namaLengkap: '',
    nik: '',
    alamatLengkap: '',
    rt: '',
    rw: '',
    tujuanSekolah: '',
    keperluan: '',
  },
  tidak_mampu: {
    namaLengkap: '',
    nik: '',
    alamatLengkap: '',
    rt: '',
    rw: '',
    tujuanSekolah: '',
    keperluan: '',
  },
  beda_luas: {
    namaLengkap: '',
    nik: '',
    alamatLengkap: '',
    rt: '',
    rw: '',
    nomorSertifikat: '',
    luasSertifikat: '',
    nomorSPPT: '',
    luasSPPT: '',
  },
  undangan: {
    tujuanYth: '',
    hari: '',
    tanggal: '',
    pukul: '',
    tempat: '',
    agendaAcara: '',
    daftarTembusan: '',
  },
};

export const useLetterStore = create<LetterStore>((set, get) => ({
  letterType: 'domisili',
  headerData: defaultHeader,
  letterData: defaultLetterData.domisili,

  setLetterType: (type: LetterType) => {
    set({
      letterType: type,
      letterData: defaultLetterData[type],
    });
  },

  setHeaderData: (data: Partial<HeaderData>) => {
    set((state) => ({
      headerData: { ...state.headerData, ...data },
    }));
  },

  setLetterData: (data: Partial<LetterData>) => {
    set((state) => ({
      letterData: { ...state.letterData, ...data },
    }));
  },

  reset: () => {
    const { letterType } = get();
    set({
      letterData: defaultLetterData[letterType],
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
