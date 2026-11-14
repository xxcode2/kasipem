import { create } from 'zustand';
import { LetterStore, HeaderData, LetterType } from '../types';

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

const defaultLetterData: Record<LetterType, Record<string, any>> = {
  domisili: {
    namaLengkap: '',
    nik: '',
    alamatLengkap: '',
    rt: '',
    rw: '',
    tempatLahir: '',
    tanggalLahir: '',
    pekerjaan: '',
    jenisKelamin: '',
  },
  pisah_kk: {
    nama1: '',
    nik1: '',
    nama2: '',
    nik2: '',
    nomorKKLama: '',
    alamat: '',
  },
  skt_dayat: {
    namaLengkap: '',
    nik: '',
    tempatLahir: '',
    tanggalLahir: '',
    alamat: '',
    luasTanah: '',
    luasBangunan: '',
    spptNop: '',
  },
  beda_luas: {
    namaLengkap: '',
    nik: '',
    alamat: '',
    nomorSertifikat: '',
    luasSertifikat: '',
    nomorSPPT: '',
    luasSPPT: '',
  },
  beda_nama: {
    namaLengkap: '',
    nik: '',
    namaDokumen1: '',
    namaDokumen2: '',
    alasan: '',
  },
  belum_menikah: {
    namaLengkap: '',
    nik: '',
    tempatLahir: '',
    tanggalLahir: '',
    pekerjaan: '',
    alamat: '',
    keperluan: '',
  },
  tidak_mampu: {
    namaLengkap: '',
    nik: '',
    tempatLahir: '',
    tanggalLahir: '',
    pekerjaan: '',
    alamat: '',
    penghasilanBulanan: '',
    keperluan: '',
  },
  ahli_waris: {
    namaPeninggal: '',
    nikPeninggal: '',
    alamatPeninggal: '',
    namaAhliWaris1: '',
    nikAhliWaris1: '',
    hubunganAhliWaris1: '',
    namaAhliWaris2: '',
    nikAhliWaris2: '',
    hubunganAhliWaris2: '',
    keterangan: '',
  },
  nikah_tidak_tercatat: {
    namaPria: '',
    nikPria: '',
    tempatLahirPria: '',
    tanggalLahirPria: '',
    namaWanita: '',
    nikWanita: '',
    tempatLahirWanita: '',
    tanggalLahirWanita: '',
    tanggalPernikahan: '',
    tempatPernikahan: '',
  },
  pemakaman: {
    namaAlmarhum: '',
    nikAlmarhum: '',
    tempatLahir: '',
    tanggalLahir: '',
    alamat: '',
    tanggalMeninggal: '',
    lokasiMakam: '',
    namaKeluarga: '',
  },
  tidak_memiliki_rumah: {
    namaLengkap: '',
    nik: '',
    tempatLahir: '',
    tanggalLahir: '',
    pekerjaan: '',
    alamatTinggal: '',
    keterangan: '',
  },
  undangan: {
    tujuanYth: '',
    hari: '',
    tanggal: '',
    bulan: '',
    tahun: '',
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

  setLetterData: (data: Record<string, any>) => {
    set((state) => ({
      letterData: { ...state.letterData, ...data },
    }));
  },

  reset: () => {
    const { letterType } = get();
    set({
      letterData: { ...defaultLetterData[letterType] },
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
