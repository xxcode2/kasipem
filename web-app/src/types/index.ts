export type LetterType = 
  | 'domisili' 
  | 'pisah_kk'
  | 'skt_dayat'
  | 'beda_luas' 
  | 'beda_nama'
  | 'belum_menikah' 
  | 'tidak_mampu' 
  | 'ahli_waris'
  | 'nikah_tidak_tercatat'
  | 'pemakaman'
  | 'tidak_memiliki_rumah'
  | 'undangan';

export interface HeaderData {
  namaKabupaten: string;
  namaKecamatan: string;
  namaDesa: string;
  alamatKantor: string;
  namaPejabat: string;
  atnPejabat?: boolean;
  nipPejabat: string;
  jabatanPejabat: string;
}

export interface PersonalData {
  namaLengkap: string;
  nik: string;
  alamatLengkap?: string;
  rt?: string;
  rw?: string;
  jenisKelamin?: string;
  nomorKK?: string;
  tempatLahir?: string;
  tanggalLahir?: string;
  pekerjaan?: string;
}

export type LetterData = Record<string, any>;

export interface LetterStore {
  letterType: LetterType;
  headerData: HeaderData;
  letterData: LetterData;
  setLetterType: (type: LetterType) => void;
  setHeaderData: (data: Partial<HeaderData>) => void;
  setLetterData: (data: Record<string, any>) => void;
  reset: () => void;
  loadHeaderFromStorage: () => void;
  saveHeaderToStorage: () => void;
}
