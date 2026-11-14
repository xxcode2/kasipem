'use client';

import { useRef, useState } from 'react';
import { useLetterStore, defaultHeader } from '../lib/store';
import { Settings, X, Upload, RotateCcw } from 'lucide-react';

/**
 * Pengaturan data kop surat & pejabat (stateless — localStorage saja).
 * Default desa asli Kasomalang Kulon; pejabat asli (user 2026-11-14):
 * Kades H. AMIRUDIN, S.Pd.I, Sekdes APIPUDIN, S.IP. TIDAK ADA NIP —
 * field NIP opsional kosong dan tidak dirender di surat bila kosong.
 */
export function HeaderForm() {
  const { headerData, setHeaderData, saveHeaderToStorage } = useLetterStore();
  const [isOpen, setIsOpen] = useState(false);
  const [savedMsg, setSavedMsg] = useState('');
  const garudaInputRef = useRef<HTMLInputElement>(null);
  const subangInputRef = useRef<HTMLInputElement>(null);

  const handleChange = (field: string, value: string) => {
    setHeaderData({ [field]: value });
  };

  const handleSave = () => {
    saveHeaderToStorage();
    setSavedMsg('Tersimpan di perangkat ini.');
    setTimeout(() => setIsOpen(false), 400);
  };

  const handleLogoUpload = (field: 'logoGaruda' | 'logoSubang', file: File | undefined) => {
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      setHeaderData({ [field]: reader.result as string });
    };
    reader.readAsDataURL(file);
  };

  const handleResetLogo = (field: 'logoGaruda' | 'logoSubang') => {
    setHeaderData({ [field]: defaultHeader[field] });
  };

  const inputCls =
    'w-full px-4 py-2 border border-stone-300 rounded-lg focus:ring-2 focus:ring-teal-700 focus:border-teal-700';

  return (
    <>
      <button
        onClick={() => setIsOpen(true)}
        className="fixed bottom-6 right-6 bg-teal-700 text-white p-4 rounded-full shadow-lg hover:bg-teal-800 transition-colors z-40"
        aria-label="Pengaturan kop surat"
      >
        <Settings className="w-6 h-6" />
      </button>

      {isOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4"
          onClick={() => setIsOpen(false)}
        >
          <div
            onClick={(e) => e.stopPropagation()}
            className="bg-white rounded-xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto"
          >
            <div className="sticky top-0 bg-teal-700 text-white px-6 py-4 flex items-center justify-between">
              <h2 className="text-lg font-semibold">Data Kop Surat & Pejabat</h2>
              <button onClick={() => setIsOpen(false)} className="text-white/80 hover:text-white" aria-label="Tutup">
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="p-6 space-y-5">
              <div>
                <label className="block text-sm font-semibold text-stone-700 mb-1.5">Nama Kabupaten</label>
                <input type="text" value={headerData.namaKabupaten} onChange={(e) => handleChange('namaKabupaten', e.target.value)} className={inputCls} />
              </div>
              <div>
                <label className="block text-sm font-semibold text-stone-700 mb-1.5">Nama Kecamatan</label>
                <input type="text" value={headerData.namaKecamatan} onChange={(e) => handleChange('namaKecamatan', e.target.value)} className={inputCls} />
              </div>
              <div>
                <label className="block text-sm font-semibold text-stone-700 mb-1.5">Nama Desa</label>
                <input type="text" value={headerData.namaDesa} onChange={(e) => handleChange('namaDesa', e.target.value)} className={inputCls} />
              </div>
              <div>
                <label className="block text-sm font-semibold text-stone-700 mb-1.5">Alamat Kantor Desa</label>
                <textarea value={headerData.alamatKantor} onChange={(e) => handleChange('alamatKantor', e.target.value)} rows={2} className={inputCls} />
              </div>

              <div className="border-t border-stone-200 pt-5">
                <h3 className="font-semibold text-stone-800 mb-4">Kepala Desa</h3>
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-semibold text-stone-700 mb-1.5">Nama Kepala Desa</label>
                    <input type="text" value={headerData.namaPejabat} onChange={(e) => handleChange('namaPejabat', e.target.value)} placeholder="H. AMIRUDIN, S.Pd.I" className={inputCls} />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-stone-700 mb-1.5">
                      NIP Kepala Desa <span className="font-normal text-stone-500">(opsional — kosongkan bila tidak ada)</span>
                    </label>
                    <input type="text" value={headerData.nipPejabat} onChange={(e) => handleChange('nipPejabat', e.target.value)} placeholder="Tidak ada NIP" className={inputCls} />
                  </div>
                </div>
              </div>

              <div className="border-t border-stone-200 pt-5">
                <h3 className="font-semibold text-stone-800 mb-4">Sekretaris Desa</h3>
                <div className="space-y-4">
                  <div>
                    <label className="block text-sm font-semibold text-stone-700 mb-1.5">Nama Sekretaris Desa</label>
                    <input type="text" value={headerData.namaSekdes} onChange={(e) => handleChange('namaSekdes', e.target.value)} placeholder="APIPUDIN, S.IP" className={inputCls} />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-stone-700 mb-1.5">
                      NIP Sekretaris Desa <span className="font-normal text-stone-500">(opsional — kosongkan bila tidak ada)</span>
                    </label>
                    <input type="text" value={headerData.nipSekdes} onChange={(e) => handleChange('nipSekdes', e.target.value)} placeholder="Tidak ada NIP" className={inputCls} />
                  </div>
                  <p className="text-xs text-stone-500">
                    Surat Pisah KK selalu ditandatangani a.n. Kepala Desa oleh Sekretaris Desa.
                  </p>
                </div>
              </div>

              <div className="border-t border-stone-200 pt-5">
                <h3 className="font-semibold text-stone-800 mb-4">Penandatangan Default</h3>
                <select
                  value={headerData.penandatanganDefault}
                  onChange={(e) => handleChange('penandatanganDefault', e.target.value)}
                  className={`${inputCls} bg-white`}
                >
                  <option value="kades">Kepala Desa (langsung)</option>
                  <option value="sekdes">Sekretaris Desa (a.n. Kepala Desa)</option>
                </select>
                <p className="text-xs text-stone-500 mt-1">
                  Berlaku untuk surat yang tidak mengatur penandatangan khusus.
                </p>
              </div>

              <div className="border-t border-stone-200 pt-5">
                <h3 className="font-semibold text-stone-800 mb-4">Logo Kop Surat</h3>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <LogoField
                    label="Logo kiri — Garuda Pancasila"
                    src={headerData.logoGaruda}
                    inputRef={garudaInputRef}
                    onUpload={(f) => handleLogoUpload('logoGaruda', f)}
                    onReset={() => handleResetLogo('logoGaruda')}
                  />
                  <LogoField
                    label="Logo kanan — Kabupaten Subang"
                    src={headerData.logoSubang}
                    inputRef={subangInputRef}
                    onUpload={(f) => handleLogoUpload('logoSubang', f)}
                    onReset={() => handleResetLogo('logoSubang')}
                  />
                </div>
                <p className="text-xs text-stone-500 mt-2">
                  Logo tersimpan di perangkat ini (localStorage). Kosongkan dengan mengembalikan ke bawaan.
                </p>
              </div>

              {savedMsg && <p className="text-sm text-teal-700">{savedMsg}</p>}
            </div>

            <div className="sticky bottom-0 bg-stone-50 border-t border-stone-200 px-6 py-4 flex justify-end gap-3">
              <button
                onClick={() => setIsOpen(false)}
                className="px-5 py-2 text-stone-700 border border-stone-300 rounded-lg hover:bg-stone-100"
              >
                Batal
              </button>
              <button
                onClick={handleSave}
                className="px-5 py-2 bg-teal-700 text-white rounded-lg hover:bg-teal-800"
              >
                Simpan & Tutup
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

function LogoField({
  label,
  src,
  inputRef,
  onUpload,
  onReset,
}: {
  label: string;
  src: string;
  inputRef: React.RefObject<HTMLInputElement>;
  onUpload: (file: File | undefined) => void;
  onReset: () => void;
}) {
  return (
    <div className="border border-stone-200 rounded-lg p-3">
      <p className="text-sm font-medium text-stone-700 mb-2">{label}</p>
      <div className="h-20 flex items-center justify-center bg-stone-50 rounded mb-2 overflow-hidden">
        {/* eslint-disable-next-line @next/next/no-img-element */}
        <img
          src={src}
          alt={label}
          className="max-h-20 w-auto object-contain"
          onError={(e) => {
            (e.target as HTMLImageElement).style.display = 'none';
          }}
        />
      </div>
      <input
        ref={inputRef}
        type="file"
        accept="image/*"
        className="hidden"
        onChange={(e) => onUpload(e.target.files?.[0])}
      />
      <div className="flex gap-2">
        <button
          type="button"
          onClick={() => inputRef.current?.click()}
          className="flex-1 px-3 py-1.5 text-xs border border-stone-300 rounded-lg hover:bg-stone-100 flex items-center justify-center gap-1"
        >
          <Upload className="w-3.5 h-3.5" />
          Upload
        </button>
        <button
          type="button"
          onClick={onReset}
          className="px-3 py-1.5 text-xs border border-stone-300 rounded-lg hover:bg-stone-100 flex items-center gap-1"
          title="Kembalikan ke logo bawaan"
        >
          <RotateCcw className="w-3.5 h-3.5" />
          Bawaan
        </button>
      </div>
    </div>
  );
}
