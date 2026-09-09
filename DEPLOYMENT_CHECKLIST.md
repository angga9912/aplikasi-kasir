# Checklist: Sebelum Deploy MVP Freemium

## Pre-Deployment (Hari 7)

### 1. Update WhatsApp Admin Number ✓
- [ ] Edit file: `app/src/main/java/com/nada/kasir/feature/upgrade/UpgradePromptDialog.kt`
- [ ] Line 32: Ubah `whatsappAdminPhone` dari `"62812345678"` ke nomor Anda
- [ ] Format: `62` + nomor tanpa `0` (contoh: `6281234567890`)
- [ ] Commit perubahan

### 2. Code Review
- [ ] Review semua file di commit Phase A-C
- [ ] Pastikan tidak ada hardcoded test data selain default admin
- [ ] Pastikan error messages user-friendly (Bahasa Indonesia)

### 3. Local Testing
- [ ] Build APK: `./gradlew assembleDebug`
- [ ] Install di device Android
- [ ] Jalankan testing checklist (lihat FREEMIUM_MVP.md)

### 4. Merge ke Main
```bash
git checkout main
git merge feature/freemium-mvp
git push origin main
```

### 5. Create Release Tag
```bash
git tag -a v1.1.0-freemium -m "MVP Freemium: BASIC/CUSTOM/PRO tier dengan WhatsApp upgrade"
git push origin v1.1.0-freemium
```

## Production Rollout

### First Batch: Beta Users (5-10 users)
- [ ] Distribute APK dari main branch
- [ ] Collect feedback untuk 3-5 hari
- [ ] Monitor error logs (jika ada)

### Second Batch: Limited Release (50-100 users)
- [ ] Publish ke Google Play internal testing
- [ ] Collect feedback untuk 1 minggu

### Full Release: Public
- [ ] Publish ke Google Play Store
- [ ] Announce di komunitas/social media

## Post-Deployment Monitoring

### First Week
- [ ] Monitor WhatsApp messages dari users (upgrade requests)
- [ ] Respond upgrade requests dalam 24 jam
- [ ] Track error reports di issue tracker

### Second Week
- [ ] Analyze user engagement per paket
- [ ] Adjust limit config jika diperlukan (misal: BASIC_MAX_PRODUK)
- [ ] Plan Phase 5 features based on user feedback

## Support Documentation

- [ ] Share FREEMIUM_MVP.md dengan team
- [ ] Buat user guide untuk admin (cara upgrade via WhatsApp)
- [ ] Buat FAQ page untuk FAQ question umum

---

**Estimation**: 3-4 jam untuk pre-deployment + 1-2 hari untuk full rollout.
