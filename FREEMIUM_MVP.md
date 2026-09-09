# NADA POS - Freemium MVP Documentation

## Overview

MVP Freemium telah diimplementasikan untuk mendukung model bisnis gratis (Basic) dengan upgrade berbayar (Custom/Pro) yang dipopulerkan oleh Qasir, Pawoon, dan Kasir Pintar di Indonesia.

**Default paket untuk user baru: BASIC (Gratis)**

---

## Architecture

### 1. **Core Layer** (`core/paket/`)

#### `PaketAplikasi.kt` (Enum)
- **BASIC** (Gratis): Kasir, Produk, Barcode, Printer — kebutuhan inti
- **CUSTOM** (Rp50-75k/bulan): Basic + Laporan, Import/Export, Backup, Branding custom
- **PRO** (Rp99k+/bulan): Custom + Multi User (Admin/Kasir terpisah)

#### `PaketConfig.kt` (Limit Configuration)
```kotlin
BASIC_MAX_PRODUK = 50          // Warning saat 90%, error saat 100%
BASIC_MAX_KASIR = 0             // Hanya 1 Admin, tidak bisa tambah Kasir
CUSTOM_MAX_PRODUK = 9999        // Unlimited
CUSTOM_MAX_KASIR = 9999         // Unlimited
PRO_MAX_PRODUK = 9999
PRO_MAX_KASIR = 9999
```

#### `PaketValidator.kt` (Validation Logic)
- `validateTambahProduk()` — Cek limit produk sebelum insert
- `validateTambahKasir()` — Cek user limit sebelum insert
- `validateAksesFitur()` — Cek akses fitur berdasarkan paket

#### `PaketRepository.kt` (Data Persistence)
- Default BASIC untuk user baru (sebelumnya PRO untuk testing)
- Simpan paket aktif di `settings` table
- Observable: `observePaketAktif()` (Flow) + `getPaketAktif()` (suspend)

#### `PaketFeatureGate.kt` (UI Helper)
```kotlin
isPaketFeatureEnabled(paket, feature)  // Cek feature availability
canAddUser(paket)                       // Cek bisa tambah user
```

### 2. **Data Layer** (Repository + DAO)

#### `ProductRepository.kt`
- Integrate `PaketValidator.validateTambahProduk()` di `simpan()`
- Return `Result.Failure` jika limit tercapai
- Error message berisi informasi upgrade

#### `UserRepository.kt`
- Integrate `PaketValidator.validateTambahKasir()` di `buatPengguna()`
- Hanya CUSTOM/PRO bisa tambah Kasir
- BASIC hanya boleh 1 Admin (password default: admin/admin123)

#### `SettingDao.kt` (Update)
- Tambah method: `findByKey(key: String): SettingEntity?`

#### `UserDao.kt` (Update)
- Tambah method: `countByRole(role: UserRole): Int`

### 3. **UI Layer** (Composable Components)

#### `UpgradePromptDialog.kt`
- Bottom sheet upgrade dengan tabel perbandingan paket
- Tombol "Upgrade via WhatsApp Admin" → Open WhatsApp link
- Format: `https://wa.me/[NOMOR]?text=[PESAN_TERENCOD]`
- **TODO: Update `whatsappAdminPhone` di line 32**

#### `PaketBadge.kt`
- Badge kecil menampilkan paket saat ini
- Warning indicator (🔔) jika sudah dekat limit

#### `PaketFeatureGate.kt`
- Helper function untuk show/hide UI element

#### Feature Screens (Update)

**ProdukScreen.kt**
- Show PaketBadge di header
- Feature gate: Import/Export hanya untuk CUSTOM/PRO
- Trigger UpgradePromptDialog saat user coba tambah produk (limit terlampaui)
- ProdukViewModel.kt: Tambah state `showUpgradePrompt`

**PenggunaScreen.kt**
- Show PaketBadge di header
- FAB "Tambah Pengguna" hanya muncul untuk CUSTOM/PRO
- Warning card untuk BASIC: "Fitur tidak tersedia, upgrade untuk menambah pengguna"
- Trigger UpgradePromptDialog saat user coba tambah Kasir di BASIC
- PenggunaViewModel.kt: Tambah state `showUpgradePrompt`

---

## Workflow: User Flow Saat Coba Exceed Limit

### Scenario 1: User BASIC coba tambah produk ke 51
1. User buka layar Produk
2. Klik tombol "+" atau "Tambah Produk"
3. Isi form, klik "Simpan"
4. `ProdukViewModel.simpan()` → `ProductRepository.simpan()` → `PaketValidator.validateTambahProduk()`
5. Validator return `Error`: "Paket Basic terbatas 50 produk..."
6. ViewModel detect error & set `showUpgradePrompt = Pair(true, "Tambah Produk")`
7. Screen render `UpgradePromptDialog`
8. User klik "Upgrade via WhatsApp Admin" → Open WhatsApp template message
9. Admin terima pesan, process upgrade, ubah paket user dari BASIC ke CUSTOM
10. Saat user buka app lagi, `PaketRepository.observePaketAktif()` return CUSTOM
11. Feature gate otomatis enable Import/Export, FAB "Tambah Produk" tetap aktif

### Scenario 2: User BASIC coba tambah Kasir
1. User (Admin) buka Manajemen Pengguna
2. FAB "Tambah Pengguna" tidak muncul (hidden via feature gate)
3. Warning card tampil: "Fitur tidak tersedia di paket Basic"
4. (Opsional) User lihat pesan & klik link upgrade → WhatsApp Admin

---

## Testing Checklist

### Unit Test (Sudah ada)
- ✅ `PaketValidator` logic untuk setiap case
- ✅ Limit calculation (50 produk, 0 kasir di BASIC)

### Manual Testing (di HP)

#### Test 1: Default BASIC
- [ ] Install APK
- [ ] Login dengan `admin` / `admin123`
- [ ] Cek Dashboard: Paket harus "BASIC" (di Pengaturan Toko atau badge)
- [ ] Import/Export button NOT visible
- [ ] Manajemen Pengguna: FAB tidak visible, warning card tampil

#### Test 2: Tambah Produk Limit (BASIC)
- [ ] Tambah 50 produk dengan barcode unik (script bisa pakai Excel import)
- [ ] Produk ke-45: Warning badge tampil (90% threshold)
- [ ] Produk ke-51: Dialog error muncul + UpgradePromptDialog tampil
- [ ] Klik "Upgrade via WhatsApp Admin": WhatsApp terbuka dengan template message
- [ ] Admin process upgrade, ubah `paket_aktif` setting ke CUSTOM
- [ ] User close & re-open app
- [ ] Import/Export button sekarang visible
- [ ] Bisa tambah produk tanpa limit

#### Test 3: Tambah Kasir (BASIC)
- [ ] Manajemen Pengguna: FAB tidak visible
- [ ] (Jika somehow FAB terclick) Try call `tambahPengguna()` untuk Kasir
- [ ] Error dialog + UpgradePromptDialog muncul
- [ ] Upgrade ke CUSTOM, FAB sekarang visible
- [ ] Bisa tambah Kasir unlimited

#### Test 4: Upgrade ke PRO
- [ ] Setelah upgrade ke CUSTOM
- [ ] Ubah setting `paket_aktif` ke PRO (via admin query)
- [ ] Fitur Multi User sepenuhnya enabled

#### Test 5: Feature Gate
- [ ] BASIC: No Export, No Laporan, No Backup
- [ ] CUSTOM: Export ✓, Laporan ✓, Backup ✓, No Hutang/Piutang
- [ ] PRO: Semua fitur ✓

---

## Deployment Instructions

### 1. **Update WhatsApp Admin Number**

File: `app/src/main/java/com/nada/kasir/feature/upgrade/UpgradePromptDialog.kt`

Line 32:
```kotlin
val whatsappAdminPhone = "62812345678"  // ← UBAH KE NOMOR ANDA
```

Format:
- Gunakan nomor WhatsApp Business atau personal
- Awali dengan kode negara: `62` (Indonesia)
- Tanpa `+` atau `0` di awal, contoh: `6281234567890`

### 2. **Set Default Paket untuk Customer**

Jika ingin deploy untuk pelanggan tertentu dengan paket non-BASIC:

**Option A: Via Admin Panel** (Recommended)
- User login sebagai Admin
- Buka Pengaturan Toko
- Dropdown "Paket Aplikasi" → pilih CUSTOM atau PRO
- Simpan
- Setting otomatis disimpan ke database

**Option B: Via Database Query**
```sql
INSERT INTO settings (key, value) VALUES ('paket_aktif', 'CUSTOM')
  ON CONFLICT(key) DO UPDATE SET value = 'CUSTOM';
```

### 3. **Build & Release APK**

```bash
# Branch feature/freemium-mvp
git checkout feature/freemium-mvp

# Update nomor WhatsApp
# (edit UpgradePromptDialog.kt line 32)

# Build
./gradlew assembleDebug  # atau assembleRelease

# Hasilnya:
# app/build/outputs/apk/demo/debug/app-demo-debug.apk
```

Or via GitHub Actions:
- Push ke feature/freemium-mvp
- GitHub Actions workflow otomatis trigger
- Download APK dari Artifacts

### 4. **Production Rollout**

```bash
# Setelah testing selesai & semua OK
git checkout main
git merge feature/freemium-mvp

# Tag release
git tag -a v1.1.0-freemium -m "MVP Freemium: BASIC/CUSTOM/PRO tier"
git push origin v1.1.0-freemium
```

---

## Payment & Upgrade Flow (Manual via WhatsApp)

### Admin Flow (Backend)
1. Customer hubungi via WhatsApp dengan template message
   - "Saya mau upgrade dari paket [CURRENT] ke [TARGET] untuk menggunakan fitur: [FEATURE]"
2. Admin confirm & request pembayaran via transfer/e-wallet
3. Setelah pembayaran masuk, admin:
   - Buka app NADA POS (harus install di device Admin)
   - Login sebagai Admin
   - Buka Pengaturan Toko
   - Ubah "Paket Aplikasi" dari BASIC ke CUSTOM/PRO
   - Simpan
4. Atau via database:
   ```sql
   UPDATE settings SET value='CUSTOM' WHERE key='paket_aktif';
   ```
5. Customer notif upgrade berhasil → app mereka auto-sync paket

### Future Enhancement (Out of Scope MVP)
- Backend payment gateway integration (Midtrans, DOKU)
- Automatic license activation via API
- Expiration tracking (trial, monthly subscription)

---

## Troubleshooting

### Q: User coba upgrade tapi WhatsApp tidak buka
**A:** Pastikan:
- WhatsApp terinstall di device
- Nomor admin valid & sudah disave di Contacts
- Gunakan URL format: `https://wa.me/62812345678?text=Pesan%20URL%20encoded`

### Q: Paket tidak berubah setelah admin upgrade di database
**A:** Gunakan `UPDATE` bukan `INSERT`:
```sql
-- ❌ SALAH (duplikat key)
INSERT INTO settings (key, value) VALUES ('paket_aktif', 'CUSTOM');

-- ✅ BENAR (update existing)
UPDATE settings SET value='CUSTOM' WHERE key='paket_aktif';
```

### Q: Import/Export button masih tidak muncul setelah upgrade
**A:** Trigger recomposition:
1. Close app completely (force stop)
2. Re-open app
3. Atau swipe refresh dashboard jika ada pull-to-refresh

### Q: User BASIC tidak bisa lihat limit warning (45 produk)
**A:** Warning hanya tampil saat:
- User try **simpan** produk ke-45+
- Badge warning muncul SESUDAH produk tersimpan (next UI render)
- Produk belum tersimpan = warning belum muncul

---

## Migration Path (dari PRO ke BASIC default)

Existing installations sebelumnya default PRO (untuk testing).

Setelah merge ke main, new users akan:
- ✅ Default BASIC
- ✅ Existing users tetap PRO (tidak ada breaking change)
- ✅ Admin bisa manual downgrade ke BASIC di Pengaturan Toko jika perlu

---

## Files Overview

```
app/src/main/java/com/nada/kasir/
├── core/
│   └── paket/
│       ├── PaketAplikasi.kt          ← Enum: BASIC/CUSTOM/PRO
│       ├── PaketConfig.kt            ← Limit config
│       ├── PaketValidator.kt         ← Validation logic
│       ├── PaketRepository.kt        ← Persistence (default BASIC)
│       └── PaketFeatureGate.kt       ← UI helper functions
│   ├── data/
│   │   ├── local/dao/
│   │   │   ├── SettingDao.kt        ← ✏️ Tambah findByKey()
│   │   │   └── UserDao.kt           ← ✏️ Tambah countByRole()
│   │   └── repository/
│   │       ├── ProductRepository.kt  ← ✏️ Integrate validator
│   │       └── UserRepository.kt     ← ✏️ Integrate validator
│
└── feature/
    ├── upgrade/
    │   ├── UpgradePromptDialog.kt    ← Dialog upgrade + WhatsApp link
    │   └── PaketBadge.kt            ← Badge component
    ├── produk/
    │   ├── ProdukScreen.kt          ← ✏️ Feature gate + upgrade prompt
    │   └── ProdukViewModel.kt       ← ✏️ Tambah showUpgradePrompt state
    └── pengguna/
        ├── PenggunaScreen.kt        ← ✏️ Feature gate + upgrade prompt
        └── PenggunaViewModel.kt     ← ✏️ Tambah showUpgradePrompt state
```

---

## Next Steps (Phase 5+)

- **Payment Integration**: Midtrans/DOKU API untuk auto-activation
- **Hutang/Piutang Module**: PRO tier exclusive
- **Supplier Management**: PRO tier exclusive
- **Multi Cabang Support**: PRO tier exclusive
- **Analytics & Dashboard**: PRO tier exclusive

---

## Support & Questions

Contact Admin via:
- **WhatsApp**: [Update nomor Anda]
- **Email**: support@nadapos.id (jika ada)

