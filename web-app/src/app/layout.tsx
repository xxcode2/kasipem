import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'KASIPEM - Pembuat Surat Administrasi Desa/Kelurahan',
  description: 'Aplikasi stateless untuk membuat berbagai jenis surat administrasi desa/kelurahan secara cepat dan mudah.',
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
      <body className="bg-gradient-to-br from-blue-50 via-teal-50 to-emerald-50 min-h-screen">
        {children}
      </body>
    </html>
  );
}
