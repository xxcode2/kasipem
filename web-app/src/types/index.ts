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

export type LetterCategory =
  | 'Individu'
  | 'Properti'
  | 'Administrasi'
  | 'Sosial'
  | 'Hukum'
  | 'Lainnya';

/** Penandatangan default untuk surat yang tidak mengatur penandatangan khusus. */
export type PenandatanganDefault = 'kades' | 'sekdes';

export interface HeaderData {
  namaKabupaten: string;
  namaKecamatan: string;
  namaDesa: string;
  alamatKantor: string;
  /** Nama Kepala Desa — default asli: H. AMIRUDIN, S.Pd.I. */
  namaPejabat: string;
  /** NIP Kepala Desa — TIDAK ADA (kosong); baris NIP tidak dirender bila kosong. */
  nipPejabat: string;
  jabatanPejabat: string;
  /** Nama Sekretaris Desa — default asli: APIPUDIN, S.IP. */
  namaSekdes: string;
  /** NIP Sekretaris Desa — TIDAK ADA (kosong); baris NIP tidak dirender bila kosong. */
  nipSekdes: string;
  /** Penandatangan default bila jenis surat tidak mengatur khusus. */
  penandatanganDefault: PenandatanganDefault;
  /** Logo kop kiri (Garuda Pancasila): path di /public atau dataURL hasil upload. */
  logoGaruda: string;
  /** Logo kop kanan (Kabupaten Subang): path di /public atau dataURL hasil upload. */
  logoSubang: string;
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
