# NADA KASIR CUSTOM

Aplikasi kasir Android yang dapat dikustomisasi per toko/UMKM. Dibangun dengan
Kotlin + Jetpack Compose + Room (MVVM + Repository, offline-first).

## Status: STRUKTUR PAKET BASIC / CUSTOM / PRO (selesai — siap diuji)

Satu source code sekarang bisa melayani 3 tingkat paket komersial (poin 29 brief
awal), diatur lewat 1 dropdown di **Pengaturan Toko** (Admin) — tanpa perlu
build APK terpisah per paket.

| Fitur | Basic | Custom | Pro |
|---|---|---|---|
| Kasir, Barcode, Printer, Cetak Struk + Preview | ✅ | ✅ | ✅ |
| Produk, Stok, Riwayat Transaksi | ✅ | ✅ | ✅ |
| Custom Warna Aplikasi (Branding) | ❌ | ✅ | ✅ |
| Import/Export Excel | ❌ | ✅ | ✅ |
| Laporan (Harian/Bulanan/Stok) | ❌ | ✅ | ✅ |
| Backup & Restore Data | ❌ | ✅ | ✅ |
| Manajemen Pengguna (Multi User Admin/Kasir) | ❌ | ❌ | ✅ |

- ✅ `PaketAplikasi` (enum BASIC/CUSTOM/PRO) + `PaketRepository` — disimpan di
  tabel `settings` yang sudah ada (mekanisme sama seperti feature flags poin 22).
- ✅ Pemilih paket di Pengaturan Toko, langsung menyembunyikan/menampilkan menu
  terkait di Dashboard, Produk, dan Pengaturan tanpa perlu restart aplikasi.
- ✅ Default paket = **PRO** (supaya instalasi yang sudah ada, termasuk yang
  sedang Anda uji, tidak kehilangan menu tiba-tiba) — ganti manual ke Basic/Custom
  saat deploy ke pelanggan sesuai paket yang mereka beli.
- 🔜 Slot **Pro** sudah disiapkan untuk fitur Phase 5 mendatang (Hutang/Piutang,
  Supplier, Multi Cabang) tanpa perlu restrukturisasi lagi.



Ditambahkan atas permintaan: struk ditampilkan dulu sebagai preview teks
sebelum benar-benar dikirim ke printer.

- ✅ `StrukFormatter.buatStrukPreviewText()` — versi teks biasa (bukan ESC/POS)
  dari struk, layout dibuat semirip mungkin dengan hasil cetak fisik (font
  monospace, lebar kolom mengikuti ukuran kertas 58mm/80mm).
- ✅ `StrukPreviewDialog` — dialog full-height menampilkan preview, tombol
  "Cetak Sekarang" (baru mengirim ke printer di sini) dan "Tutup" (batal cetak).
- ✅ Berlaku di DUA alur: setelah transaksi selesai di Kasir, dan "Cetak Ulang"
  dari Riwayat Transaksi.
- ✅ Kegagalan printer tetap tidak memengaruhi data transaksi (perilaku lama
  tidak berubah, hanya menambah langkah konfirmasi visual sebelum cetak).
- ✅ Unit test baru: `StrukFormatterPreviewTest` (memastikan preview memuat info
  yang benar dan tidak pernah menampilkan harga beli).



Redesign "Modern Minimalist POS Dashboard" untuk layar Home, TANPA mengubah
business logic/database/navigasi fungsional (hanya cara menyusun & menampilkan
data yang sudah ada):

- ✅ **Header baru** — logo/ikon toko, nama toko (dari Pengaturan Toko), sapaan
  "Selamat datang kembali, {nama pengguna}", tanggal hari ini, ikon notifikasi
  (placeholder, belum ada sistem notifikasi) dan menu (⋮) berisi "Keluar".
- ✅ **Summary cards** — 4 kartu dengan ikon, label, dan angka utama; tanpa data
  perbandingan palsu (sesuai instruksi, karena datanya memang belum tersedia).
- ✅ **Primary Action** — "Transaksi Baru" jadi kartu besar warna utama aplikasi,
  paling menonjol di layar.
- ✅ **Menu Utama** — grid 2 kolom dengan ikon (Produk, Riwayat, Laporan khusus Admin),
  menggantikan tumpukan 8 tombol outline.
- ✅ **Perhatian Stok** — otomatis menampilkan status Stok Habis (merah) →
  Stok Menipis (oranye) → Aman (hijau), sesuai prioritas.
- ✅ **Transaksi Terbaru** — 5 transaksi terakhir dengan preview produk, ada
  empty state profesional ("Belum ada transaksi..." + tombol) dan "Lihat Semua".
- ✅ **Bottom Navigation** — Home / Kasir / Produk / Transaksi / Pengaturan,
  selalu terlihat di layar utama. Menu administratif (Printer, Toko, Pengguna,
  Backup) dikelompokkan di tab **Pengaturan** (halaman baru `PengaturanHubScreen`),
  tetap dibatasi untuk Admin saja (role guard Phase 4 tidak berubah).
- ✅ Warna mengikuti **branding dinamis** (`ThemeConfig`) yang sudah dibangun di
  Phase 4 — bukan warna hijau hardcode, supaya kustomisasi per toko tetap jalan.
  Warna status (aman/menipis/habis) tetap konsisten terlepas dari warna branding.

### Catatan penting
- Redesign kali ini FOKUS di layar Home/Dashboard + Bottom Navigation shell,
  sesuai brief yang diberikan. Layar lain (Kasir, Produk, Riwayat, Laporan,
  Pengaturan Printer/Toko, Pengguna, Backup) masih tampilan lama - siap
  di-redesign berikutnya kalau diperlukan.
- Semua tombol & fitur lama tetap berfungsi sama persis, hanya berpindah posisi
  visual (mis. "Keluar" sekarang di menu ⋮ header ATAU di tab Pengaturan).



Sudah ditambahkan di atas Phase 1-3:

- ✅ **Login & Role Guard** — wajib login sebelum masuk aplikasi. **ADMIN** melihat
  semua menu; **KASIR** hanya melihat Transaksi, Lihat Produk (read-only), Riwayat
  (bisa cetak ulang, TIDAK BISA membatalkan transaksi) — sesuai poin 18.
  Akun pertama (`admin` / `admin123`) dibuat otomatis saat first-run, dengan
  pemberitahuan untuk segera diganti.
- ✅ **Manajemen Pengguna** (khusus Admin) — tambah akun Kasir/Admin baru, password
  selalu di-hash (poin 26), lihat status aktif/nonaktif.
- ✅ **Pengaturan Toko** (poin 1, akhirnya dibuatkan UI-nya) — nama, alamat, WA,
  telepon, pemilik, slogan, footer struk, mata uang, ukuran kertas, TERMASUK
  toggle tampilan struk (poin 10). Harga beli tetap dipaksa `false` dari UI ini.
- ✅ **Custom Branding penuh** (poin 2 & 21) — `ThemeConfig` menghasilkan warna
  aplikasi Compose secara RUNTIME dari `StoreEntity.warnaUtama`. Ganti warna di
  Pengaturan Toko → seluruh aplikasi langsung berubah warna, tanpa rebuild.
- ✅ **Laporan** (poin 14) — tab Hari Ini / Bulanan / Stok: total penjualan, jumlah
  transaksi, produk terjual, diskon, estimasi keuntungan, produk terlaris,
  breakdown metode pembayaran, semua bisa di-export ke Excel.

### Catatan Pengujian Phase 4

- Login pertama kali pakai `admin` / `admin123` (muncul otomatis di dialog saat
  akun ini baru dibuat). Segera buat akun Kasir baru lewat Manajemen Pengguna,
  lalu coba login sebagai Kasir untuk memverifikasi menu yang disembunyikan.
- Ubah warna di Pengaturan Toko, lalu kembali ke Dashboard — tombol-tombol
  harus langsung berubah warna tanpa perlu restart aplikasi.
- Cek Laporan Hari Ini setelah melakukan beberapa transaksi kasir — angka
  "Estimasi Keuntungan" dan "Produk Terjual" harus sesuai perhitungan manual.



Sudah ditambahkan di atas Phase 1 & 2:

- ✅ **Export Excel** — `ExcelExporter` menghasilkan `DATA_PRODUK.xlsx`, `PENJUALAN.xlsx`,
  `STOK_MASUK.xlsx`, `STOK_KELUAR.xlsx` sesuai kolom yang ditentukan (poin 15).
  Tombol "Export Excel" ada di halaman Produk; hasilnya bisa langsung dibagikan (share).
- ✅ **Import Excel** — `ExcelImporter` + `ProdukRowValidator` memvalidasi header &
  setiap baris (kode/nama wajib, harga & stok harus angka), barcode duplikat
  otomatis DILEWATI (bukan menimpa), semua baris valid disimpan dalam satu
  DB transaction. Pesan error tetap berbahasa manusia ("File Excel tidak sesuai format.")
- ✅ **Backup Data** — `BackupManager` mengekspor seluruh data penting (Produk, Stok,
  Transaksi, Pengaturan Toko, Pengguna, Printer) ke satu file `.json`, bisa dibagikan.
- ✅ **Restore Data** — validasi & parsing PENUH dilakukan dulu sebelum data lama
  disentuh sama sekali; penghapusan + penyisipan data baru dibungkus SATU
  `appDatabase.withTransaction{}` sehingga kalau ada error di tengah proses,
  Room otomatis rollback dan **data lama tetap utuh** (poin 17, tidak pernah ada
  kondisi "data lama sudah terhapus tapi data baru gagal masuk").
- ✅ Excel/backup HANYA untuk laporan & cadangan — database utama tetap Room/SQLite (poin 15).

### Catatan Pengujian Phase 3

- File export & backup disimpan di folder khusus aplikasi (tidak perlu izin
  penyimpanan tambahan), lalu dibagikan lewat Share Sheet Android (WhatsApp,
  Google Drive, email, dll) memakai `FileProvider`.
- Untuk uji Import Excel: buat file `.xlsx` dengan sheet bernama `DATA_PRODUK`
  dan header persis: `Kode Produk, Barcode, Nama Produk, Kategori, Satuan, Harga Beli, Harga Jual, Stok, Stok Minimum`
  (atau langsung pakai hasil Export Excel sebagai template, edit, lalu import lagi).
- Untuk uji Restore: coba restore file backup yang sengaja dirusak (hapus beberapa
  karakter) — aplikasi harus menampilkan "File Excel tidak sesuai format." dan
  **data yang ada saat ini tidak boleh hilang sama sekali**. Ini pengujian paling
  penting di Phase 3.



Sudah ditambahkan di atas Phase 1:

- ✅ **Barcode kamera** — CameraX + ML Kit, tombol scan di halaman Kasir, mendukung
  EAN-13/EAN-8/CODE-128/CODE-39/UPC-A/UPC-E/QR
- ✅ **Barcode scanner fisik (handheld)** — `HandheldScannerDetector` membedakan
  ketikan cepat dari alat scanner vs ketikan manual kasir di kolom pencarian yang sama,
  tanpa perlu mode/tombol terpisah
- ✅ **Printer thermal Bluetooth** — `BluetoothPrinterManager` (koneksi SPP ke
  perangkat yang sudah *paired*), `EscPosBuilder` (perintah ESC/POS generik,
  kompatibel mayoritas printer 58mm/80mm di pasaran)
- ✅ **Format & custom struk** — `StrukFormatter` mengikuti toggle tampilkan/sembunyikan
  logo, alamat, WA, diskon dari `StoreEntity` (harga beli TIDAK PERNAH ditampilkan)
- ✅ **Halaman Pengaturan Printer** — scan printer terpasang, pilih ukuran kertas,
  test print, simpan sebagai default
- ✅ **Cetak Struk** setelah bayar di Kasir, dan **Cetak Ulang** dari Riwayat Transaksi
- ✅ Kegagalan printer tidak pernah membatalkan transaksi yang sudah tersimpan

### Catatan Pengujian Phase 2

- Pasangkan (pair) printer thermal Bluetooth Anda dulu lewat **Pengaturan Bluetooth
  bawaan HP**, baru buka menu **Pengaturan Printer** di aplikasi → Scan → pilih
  ukuran kertas → Test Print → Jadikan Default.
- Karena kita hanya membaca daftar perangkat yang sudah *paired* (bukan discovery
  aktif), aplikasi tidak meminta izin lokasi — hanya izin Bluetooth (Android 12+).
- Scanner fisik (handheld) diuji dengan cara: buka Kasir, tap kolom pencarian
  (fokuskan kursor), lalu scan barcode dengan alat — kode otomatis masuk sebagai
  jika mengetik cepat + Enter, tanpa perlu tombol tambahan.



Sudah diimplementasikan sesuai brief:

- ✅ **Database** — Room, 11 tabel (stores, users, categories, products, transactions,
  transaction_items, payments, stock_movements, printers, settings, audit_logs)
- ✅ **Produk** — tambah/edit/hapus (soft delete), validasi barcode duplikat
- ✅ **Stok** — stok masuk/keluar dengan pencatatan mutasi, mencegah stok minus
- ✅ **Kasir** — cari produk, keranjang, hitung subtotal/diskon/total
- ✅ **Pembayaran** — tunai (validasi kembalian), QRIS/transfer/debit/kredit/lainnya
- ✅ **Transaksi atomik** — simpan transaksi + kurangi stok dibungkus satu
  `appDatabase.withTransaction { }` (lihat `TransactionRepository.kt`) sehingga
  tidak mungkin terjadi transaksi tersimpan tanpa stok berkurang, atau sebaliknya
- ✅ **Riwayat transaksi** — lihat, batalkan (dengan konfirmasi, stok dikembalikan,
  status → CANCELLED, tidak dihapus permanen)
- ✅ **Dashboard** — penjualan hari ini, jumlah transaksi, stok menipis/habis
- ✅ **Error handling** — semua pesan error dalam Bahasa Indonesia (`AppError.kt`),
  tidak ada exception teknis yang ditampilkan ke pengguna
- ✅ **Arsitektur custom-ready** — tidak ada nama toko/warna/logo yang di-hardcode;
  semua dari `StoreEntity` + `settings` (feature flags via `FeatureConfig.kt`);
  Gradle product flavors disiapkan untuk build APK berbeda per pelanggan

## Cara Menguji via GitHub (tanpa Android Studio)

Project ini sudah dilengkapi **GitHub Actions** (`.github/workflows/android-build.yml`)
yang otomatis: menjalankan unit test → build APK → upload APK sebagai artifact
yang bisa Anda download dan instal langsung ke HP.

### Langkah-langkah

1. **Buat repo baru** di GitHub (bisa private), kosongkan (jangan centang "Add README").
2. Extract ZIP ini, lalu dari terminal di folder `nada-kasir-custom/`:
   ```bash
   git init
   git add .
   git commit -m "Phase 1: kasir, produk, stok, transaksi, riwayat"
   git branch -M main
   git remote add origin https://github.com/USERNAME/NAMA-REPO.git
   git push -u origin main
   ```
3. Buka repo Anda di GitHub → tab **Actions**. Workflow "Build & Test NADA KASIR CUSTOM"
   akan berjalan otomatis (±3–5 menit untuk run pertama).
4. Kalau semua langkah hijau (✅), klik run tersebut → scroll ke bagian **Artifacts**
   → download **`nada-kasir-custom-demo-debug`** (file `.zip` berisi `.apk`).
5. Extract, pindahkan `app-demo-debug.apk` ke HP Android (via kabel USB, WhatsApp
   ke diri sendiri, Google Drive, dll).
6. Di HP: aktifkan "Izinkan instal dari sumber tidak dikenal" untuk aplikasi file
   manager/browser yang Anda pakai, lalu tap file APK-nya untuk instal.
7. Buka aplikasi, jalankan checklist pengujian di bawah.

Setiap kali Anda `git push` perubahan baru, workflow ini otomatis jalan lagi dan
menghasilkan APK terbaru — tidak perlu build manual.

### Kalau Anda tetap ingin pakai Android Studio nanti

Buka folder project seperti biasa — Gradle wrapper (`gradlew`) sudah disertakan
di ZIP ini, jadi tidak perlu setup tambahan.

> Catatan: mode DEMO otomatis (seed data dummy sesuai poin 23 brief) belum
> ditambahkan di Phase 1 — akan ditambahkan bersamaan dengan Phase 3/Backup,
> supaya tidak mengganggu pengujian data asli.

## Checklist Pengujian Otomatis (jalan di GitHub Actions)

- [x] `PembayaranCalculatorTest` — kembalian & validasi cukup/kurang bayar
- [x] `CurrencyFormatterTest` — format Rupiah dengan pemisah ribuan
- [x] `PasswordHasherTest` — password tidak pernah plain text

## Checklist Pengujian Manual di HP (sesuai poin 30 brief)

- [ ] Tambah produk baru, cek muncul di Kasir
- [ ] Tambah produk dengan barcode yang sudah ada → harus ditolak
- [ ] Transaksi kasir dengan 2+ produk, cek subtotal/diskon/total benar
- [ ] Bayar tunai dengan uang pas → kembalian Rp0
- [ ] Bayar tunai kurang dari total → muncul "Uang pembayaran belum mencukupi."
- [ ] Setelah bayar, cek stok produk berkurang sesuai qty
- [ ] Buka Riwayat, cek transaksi baru muncul
- [ ] Batalkan transaksi → konfirmasi muncul, stok kembali, status jadi CANCELLED
- [ ] Coba transaksi dengan qty > stok tersedia → ditolak dengan pesan "Stok tidak mencukupi."
- [ ] Matikan & nyalakan app (force close) → data tetap ada (Room persisten)

## Roadmap Selanjutnya

**Phase 2 (selesai)** — ~~Barcode scanner~~, ~~Printer Bluetooth~~, ~~format & custom struk~~, ~~logo toko (teks)~~.

**Phase 3 (selesai)** — ~~Import/export Excel~~, ~~Backup/Restore~~.

**Phase 4 (selesai)** — ~~Laporan~~, ~~User Admin/Kasir~~, ~~Custom Branding penuh~~.

**Phase 5 (berikutnya)** — Modul opsional per pelanggan: Hutang/Piutang, Supplier, Multi cabang
— ditambahkan sebagai modul baru di `feature/`, tanpa mengubah `core/`.

## Struktur Folder

Lihat dokumen arsitektur di pesan sebelumnya, atau jelajahi `app/src/main/java/com/nada/kasir/`:
- `core/` — data layer, repository, util (tidak boleh diubah besar-besaran per pelanggan)
- `feature/` — satu folder per halaman (kasir, produk, stok, riwayat, dashboard)
- `navigation/` — NavGraph
- `branding/` — (disiapkan untuk Phase 4) tema dinamis dari data toko
