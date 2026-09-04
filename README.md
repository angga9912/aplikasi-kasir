# NADA KASIR CUSTOM

Aplikasi kasir Android yang dapat dikustomisasi per toko/UMKM. Dibangun dengan
Kotlin + Jetpack Compose + Room (MVVM + Repository, offline-first).

## Status: PHASE 1 (selesai — siap diuji)

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

**Phase 2** — Barcode scanner (CameraX+ML Kit sudah di dependency, tinggal
sambungkan ke `KasirViewModel.tambahDariBarcode()`), Printer Bluetooth
(`EscPosBuilder` + `BluetoothPrinterManager`), format & custom struk, logo toko.

**Phase 3** — Import/export Excel (Apache POI sudah di dependency), Backup/Restore.

**Phase 4** — Laporan (harian/bulanan/stok), User Admin/Kasir (login + role guard),
Custom branding penuh (`ThemeConfig` dinamis dari `StoreEntity.warnaUtama`).

**Phase 5** — Modul opsional per pelanggan: Hutang/Piutang, Supplier, Multi cabang
— ditambahkan sebagai modul baru di `feature/`, tanpa mengubah `core/`.

## Struktur Folder

Lihat dokumen arsitektur di pesan sebelumnya, atau jelajahi `app/src/main/java/com/nada/kasir/`:
- `core/` — data layer, repository, util (tidak boleh diubah besar-besaran per pelanggan)
- `feature/` — satu folder per halaman (kasir, produk, stok, riwayat, dashboard)
- `navigation/` — NavGraph
- `branding/` — (disiapkan untuk Phase 4) tema dinamis dari data toko
