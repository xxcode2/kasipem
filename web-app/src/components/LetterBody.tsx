'use client';

import { HeaderData, LetterData, LetterType } from '../types';
import { ContentBlock, SignerBlock, getLetterContent } from '../lib/letter-content';

/**
 * Renderer isi surat (kop + judul + nomor + isi + tanda tangan) — SATU
 * komponen yang dipakai modal preview. Isinya berasal dari
 * getLetterContent(), sumber yang sama dengan generator PDF, jadi preview
 * dan PDF tidak bisa drift.
 *
 * Dibungkus .print-area oleh pemakai (LetterPreview) agar print CSS bisa
 * mencetak persis area ini.
 *
 * Fase 2: kop Disdukcapil (teks saja, tanpa logo desa) untuk formulir
 * seri F; blok 'table' untuk daftar anggota/anak/kolektif; baris nomor
 * bisa disembunyikan (hideNomor) untuk formulir yang tidak bernomor.
 *
 * Fase 3: kop DINAMIS per penandatangan (kopFor() di letter-content.ts)
 * — Garuda tengah untuk ttd Kades, Subang kiri untuk ttd a.n. Kades /
 * Sekdes, dua logo untuk penandatangan warga; ukuran font pt (isi 12pt,
 * judul 15pt, kop 12/14pt); nomor kosong → garis titik-titik.
 *
 * Fase 5: halaman landscape (kematian, f101) — lembar 297mm, isi dibungkus
 * div lebar 62%; kop 'tabel' (kematian) tanpa logo dan tanpa garis ganda;
 * grid penandatangan (§1C); judul bisa disembunyikan (undangan);
 * subtitle di bawah judul (pbi_asesmen); nomorDisplay khusus (kematian);
 * paragraf penutup tidak dirender bila closing kosong (kematian).
 */
export function LetterBody({
  letterType,
  headerData,
  letterData,
}: {
  letterType: LetterType;
  headerData: HeaderData;
  letterData: LetterData;
}) {
  const content = getLetterContent(letterType, headerData, letterData);
  const landscape = content.landscape === true;

  return (
    <div
      className={`letter-sheet bg-white text-black mx-auto w-full ${landscape ? 'max-w-[297mm]' : 'max-w-[210mm]'}`}
      style={{ fontFamily: "'Times New Roman', Times, serif", fontSize: '12pt', lineHeight: 1.5 }}
    >
      {/* Isi landscape menempati area kiri ~62% lebar halaman (Fase 5 §1A). */}
      <div className={landscape ? 'w-[62%]' : undefined}>
        {/* KOP — varian dinamis per penandatangan (kopFor): */}
        {content.kopVariant === 'disdukcapil' ? (
          <div className="text-center">
            <div className="font-bold text-[12pt] leading-tight">PEMERINTAH KABUPATEN SUBANG</div>
            <div className="font-bold text-[12pt] leading-tight">
              DINAS KEPENDUDUKAN DAN PENCATATAN SIPIL
            </div>
            <div className="text-[11pt]">Jl. Mayjen Sutoyo S. No. 50 Subang — Telp. (0260) 411424</div>
          </div>
        ) : content.kopVariant === 'tabel' ? (
          /* Surat kematian (Fase 5): TANPA kop logo — tabel kecil 3 baris
             dengan garis vertikal di kiri. */
          <div className="border-l-2 border-black pl-2">
            <div className="flex gap-1">
              <span className="w-40 shrink-0">Pemerintah/Kota/Kab</span>
              <span className="shrink-0">:</span>
              <span className="flex-1">{headerData.namaKabupaten}</span>
            </div>
            <div className="flex gap-1">
              <span className="w-40 shrink-0">Kecamatan</span>
              <span className="shrink-0">:</span>
              <span className="flex-1">{headerData.namaKecamatan}</span>
            </div>
            <div className="flex gap-1">
              <span className="w-40 shrink-0">Desa/Kelurahan</span>
              <span className="shrink-0">:</span>
              <span className="flex-1">{headerData.namaDesa}</span>
            </div>
          </div>
        ) : content.kopVariant === 'garuda-center' ? (
          /* Ttd langsung Kepala Desa: logo Garuda SAJA di tengah paling atas. */
          <div className="text-center">
            <img
              src={headerData.logoGaruda || '/kop/kades.jpg'}
              alt="Logo Garuda Pancasila"
              className="mx-auto object-contain"
              style={{ width: '28mm', height: 'auto' }}
              onError={(e) => {
                (e.target as HTMLImageElement).style.display = 'none';
              }}
            />
            <KopText headerData={headerData} />
          </div>
        ) : content.kopVariant === 'subang-left' ? (
          /* Ttd a.n. Kades oleh Sekdes / ttd langsung Sekdes: logo Subang di kiri. */
          <div className="flex items-start gap-3">
            <img
              src={headerData.logoSubang || '/kop/sekdes.jpg'}
              alt="Logo Kabupaten Subang"
              className="shrink-0 object-contain"
              style={{ height: '20mm', width: 'auto' }}
              onError={(e) => {
                (e.target as HTMLImageElement).style.display = 'none';
              }}
            />
            <div className="flex-1">
              <KopText headerData={headerData} />
            </div>
          </div>
        ) : (
          /* Penandatangan warga (SPTJM/PBI/dll.): dua logo seperti sebelumnya. */
          <div className="flex items-start gap-3">
            <img
              src={headerData.logoGaruda || '/kop/kades.jpg'}
              alt="Logo Garuda Pancasila"
              className="shrink-0 object-contain"
              style={{ height: '20mm', width: 'auto' }}
              onError={(e) => {
                (e.target as HTMLImageElement).style.display = 'none';
              }}
            />
            <div className="flex-1">
              <KopText headerData={headerData} />
            </div>
            <img
              src={headerData.logoSubang || '/kop/sekdes.jpg'}
              alt="Logo Kabupaten Subang"
              className="shrink-0 object-contain"
              style={{ height: '20mm', width: 'auto' }}
              onError={(e) => {
                (e.target as HTMLImageElement).style.display = 'none';
              }}
            />
          </div>
        )}

        {/* Garis ganda tebal + tipis — TIDAK untuk kop tabel (kematian). */}
        {content.kopVariant !== 'tabel' && (
          <>
            <div className="mt-2 border-b-[3px] border-black" />
            <div className="mt-[2px] border-b border-black" />
          </>
        )}

        {/* Judul + subtitle + nomor */}
        <div className="mt-4 text-center">
          {!content.hideTitle && (
            <div className="font-bold text-[15pt] underline underline-offset-2">{content.title}</div>
          )}
          {content.subtitle && <div className="mt-1 font-bold text-[12pt]">{content.subtitle}</div>}
          {!content.hideNomor && (
            <div className="mt-1 text-[12pt]">
              {content.nomorDisplay ? (
                <span className="font-bold underline underline-offset-2">{content.nomorDisplay}</span>
              ) : (
                <>Nomor : {content.nomor || '............................................'}</>
              )}
            </div>
          )}
        </div>

        {/* Isi */}
        <div className="mt-4 space-y-3 text-justify">
          {content.intro && <p>{content.intro}</p>}
          {content.blocks.map((block, i) => (
            <BlockView key={i} block={block} />
          ))}
          {content.closing && <p>{content.closing}</p>}
        </div>

        {/* Tanda tangan */}
        {content.signers && content.signers.length > 0 ? (
          /* Grid penandatangan (Fase 5 §1C) — placeDate SATU kali di atas
             grid, rata kanan dalam area isi. */
          <div className="mt-8">
            <div className="text-right">{content.placeDate}</div>
            <div
              className="mt-2 grid gap-6"
              style={{
                gridTemplateColumns: `repeat(${content.signerColumns || 2}, minmax(0, 1fr))`,
              }}
            >
              {content.signers.map((s, i) => (
                <SignerCell key={i} signer={s} />
              ))}
            </div>
          </div>
        ) : (
          <div className="mt-8 flex justify-between gap-4">
            {content.leftSigner ? (
              <div className="w-1/2 text-center">
                {content.leftSigner.prefix && <div>{content.leftSigner.prefix}</div>}
                <div>{content.leftSigner.role}</div>
                <div className="h-16" />
                <div className="font-bold underline underline-offset-2">
                  {content.leftSigner.name || '____________________'}
                </div>
              </div>
            ) : (
              <div className="w-1/2" />
            )}
            <div className="w-1/2 text-center">
              <div>{content.placeDate}</div>
              {content.signer.prefix && <div>{content.signer.prefix}</div>}
              <div>{content.signer.role}</div>
              <div className="h-16" />
              <div className="font-bold underline underline-offset-2">{content.signer.name}</div>
              {/* NIP hanya dirender bila ada isinya — pejabat desa ini tidak punya NIP. */}
              {content.signer.nip ? <div>NIP: {content.signer.nip}</div> : null}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

/** Satu sel tanda tangan: prefix?, role, spasi, nama bold-underline, sub?, NIP bila ada. */
function SignerCell({ signer }: { signer: SignerBlock }) {
  return (
    <div className="text-center">
      {signer.prefix && <div>{signer.prefix}</div>}
      <div>{signer.role}</div>
      <div className="h-16" />
      <div className="font-bold underline underline-offset-2">{signer.name}</div>
      {signer.sub && <div className="whitespace-pre-line">{signer.sub}</div>}
      {signer.nip ? <div>NIP: {signer.nip}</div> : null}
    </div>
  );
}

/** Teks kop desa (dipakai semua varian kop desa): baris desa 14pt, baris di atasnya 12pt. */
function KopText({ headerData }: { headerData: HeaderData }) {
  return (
    <div className="text-center">
      <div className="font-bold text-[12pt] leading-tight">
        PEMERINTAH {headerData.namaKabupaten.toUpperCase()}
      </div>
      <div className="font-bold text-[12pt] leading-tight">
        KECAMATAN {headerData.namaKecamatan.toUpperCase()}
      </div>
      <div className="font-bold text-[14pt] leading-tight">
        DESA {headerData.namaDesa.toUpperCase()}
      </div>
      <div className="text-[11pt]">Alamat: {headerData.alamatKantor}</div>
    </div>
  );
}

function BlockView({ block }: { block: ContentBlock }) {
  if (block.kind === 'fields') {
    return (
      <div className="space-y-1">
        {block.rows.map((r, i) => (
          <div key={i} className="flex gap-1">
            <span className="w-40 shrink-0">{r.label}</span>
            <span className="shrink-0">:</span>
            <span className="flex-1">{r.value}</span>
          </div>
        ))}
      </div>
    );
  }
  if (block.kind === 'list') {
    return (
      <div>
        {block.heading && <div>{block.heading}</div>}
        <ul className="list-none space-y-0.5">
          {block.items.map((item, i) => (
            <li key={i}>- {item}</li>
          ))}
        </ul>
      </div>
    );
  }
  if (block.kind === 'table') {
    return (
      <div className="overflow-x-auto">
        <table className="w-full border-collapse border border-black text-[10pt]">
          <thead>
            <tr>
              {block.headers.map((h, i) => (
                <th key={i} className="border border-black px-1.5 py-1 text-left font-bold">
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {block.rows.map((r, i) => (
              <tr key={i}>
                {r.map((cell, j) => (
                  <td key={j} className="border border-black px-1.5 py-1 align-top">
                    {cell}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    );
  }
  return <p className="whitespace-pre-line">{block.text}</p>;
}
