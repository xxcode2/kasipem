export type LetterType = 
  | 'domisili' 
  | 'belum_menikah' 
  | 'tidak_mampu' 
  | 'beda_luas' 
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
  alamatLengkap: string;
  rt: string;
  rw: string;
  jenisKelamin?: string;
  nomorKK?: string;
  tempatLahir?: string;
  tanggalLahir?: string;
  pekerjaan?: string;
}

export interface DomisiliData extends PersonalData {
  alamatAsal: string;
  alamatDomisili: string;
  keperluanSurat: string;
  nomorSurat?: string;
  klasifikasiSurat?: string;
}

export interface BelumMenikahData extends PersonalData {
  tujuanSekolah: string;
  keperluan: string;
  nomorSurat?: string;
  klasifikasiSurat?: string;
}

export interface TidakMampuData extends PersonalData {
  tujuanSekolah: string;
  keperluan: string;
  nomorSurat?: string;
  klasifikasiSurat?: string;
}

export interface BedaLuasData extends PersonalData {
  nomorSertifikat: string;
  luasSertifikat: string;
  nomorSPPT: string;
  luasSPPT: string;
  nomorSurat?: string;
  klasifikasiSurat?: string;
}

export interface UndanganData {
  tujuanYth: string;
  hari: string;
  tanggal: string;
  pukul: string;
  tempat: string;
  agendaAcara: string;
  daftarTembusan: string;
  nomorSurat?: string;
  klasifikasiSurat?: string;
}

export type LetterData = 
  | DomisiliData 
  | BelumMenikahData 
  | TidakMampuData 
  | BedaLuasData 
  | UndanganData;

export interface LetterStore {
  letterType: LetterType;
  headerData: HeaderData;
  letterData: LetterData;
  setLetterType: (type: LetterType) => void;
  setHeaderData: (data: Partial<HeaderData>) => void;
  setLetterData: (data: Partial<LetterData>) => void;
  reset: () => void;
  loadHeaderFromStorage: () => void;
  saveHeaderToStorage: () => void;
}
