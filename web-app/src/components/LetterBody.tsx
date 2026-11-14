'use client';

import { HeaderData, LetterData, LetterType } from '../types';
import { ContentBlock, getLetterContent } from '../lib/letter-content';

/**
 * Renderer isi surat (kop + judul + nomor + isi + tanda tangan) — SATU
 * komponen yang dipakai modal preview. Isinya berasal dari
 * getLetterContent(), sumber yang sama dengan generator PDF, jadi preview
 * dan PDF tidak bisa drift.
 *
 * Dibungkus .print-area oleh pemakai (LetterPreview) agar print CSS bisa
 * mencetak persis area ini.
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

  return (
    <div
      className="letter-sheet bg-white text-black mx-auto w-full max-w-[210mm]"
      style={{ fontFamily: "'Times New Roman', Times, serif", fontSize: '13px', lineHeight: 1.5 }}
    >
      {/* KOP — dua logo */}
      <div className="flex items-start gap-3">
        <img
          src={headerData.logoGaruda || '/kop/kades.jpg'}
          alt="Logo Garuda Pancasila"
          className="h-[62px] w-auto shrink-0 object-contain"
          onError={(e) => {
            (e.target as HTMLImageElement).style.display = 'none';
          }}
        />
        <div className="flex-1 text-center">
          <div className="font-bold text-[15px] leading-tight">
            PEMERINTAH {headerData.namaKabupaten.toUpperCase()}
          </div>
          <div className="font-bold text-[15px] leading-tight">
            KECAMATAN {headerData.namaKecamatan.toUpperCase()}
          </div>
          <div className="font-bold text-[15px] leading-tight">
            DESA {headerData.namaDesa.toUpperCase()}
          </div>
          <div className="text-[12px]">Alamat: {headerData.alamatKantor}</div>
        </div>
        <img
          src={headerData.logoSubang || '/kop/sekdes.jpg'}
          alt="Logo Kabupaten Subang"
          className="h-[62px] w-auto shrink-0 object-contain"
          onError={(e) => {
            (e.target as HTMLImageElement).style.display = 'none';
          }}
        />
      </div>

      {/* Garis ganda tebal + tipis */}
      <div className="mt-2 border-b-[3px] border-black" />
      <div className="mt-[2px] border-b border-black" />

      {/* Judul + nomor */}
      <div className="mt-4 text-center">
        <div className="font-bold text-[15px] underline underline-offset-2">{content.title}</div>
        <div className="mt-1 text-[13px]">
          Nomor : {content.nomor || '............................................'}
        </div>
      </div>

      {/* Isi */}
      <div className="mt-4 space-y-3 text-justify">
        {content.intro && <p>{content.intro}</p>}
        {content.blocks.map((block, i) => (
          <BlockView key={i} block={block} />
        ))}
        <p>{content.closing}</p>
      </div>

      {/* Tanda tangan */}
      <div className="mt-8 flex justify-between gap-4">
        {content.leftSigner ? (
          <div className="w-1/2 text-center">
            {content.leftSigner.prefix && <div>{content.leftSigner.prefix}</div>}
            <div>{content.leftSigner.role}</div>
            <div className="h-16" />
            <div className="font-bold underline underline-offset-2">____________________</div>
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
  return <p className="whitespace-pre-line">{block.text}</p>;
}
