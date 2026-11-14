import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'KASIPEM - Pembuat Surat Administrasi Desa Kasomalang Kulon',
  description:
    'Aplikasi stateless untuk membuat 12 jenis surat administrasi Desa Kasomalang Kulon, Kecamatan Kasomalang, Kabupaten Subang — cepat, gratis, dan data hanya tersimpan di perangkat Anda.',
  icons: {
    icon: '/favicon.ico',
  },
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="id">
      <body className="bg-stone-100 min-h-screen text-stone-800">{children}</body>
    </html>
  );
}
