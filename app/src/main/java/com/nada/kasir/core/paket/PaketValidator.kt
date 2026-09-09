package com.nada.kasir.core.paket

import com.nada.kasir.core.util.AppError

/**
 * Validator untuk enforce limit paket.
 * Dipanggil sebelum tambah produk, tambah kasir, atau akses fitur tertentu.
 */
object PaketValidator {

    /**
     * Cek apakah user boleh tambah produk baru berdasarkan paket & jumlah produk sekarang.
     * Return: PaketValidationResult.OK() jika boleh, atau Error dengan pesan.
     */
    fun validateTambahProduk(
        paket: PaketAplikasi,
        jumlahProdukSekarang: Int
    ): PaketValidationResult {
        val maxProduk = PaketConfig.getMaxProduk(paket)
        return if (jumlahProdukSekarang >= maxProduk) {
            PaketValidationResult.Error(
                message = if (paket == PaketAplikasi.BASIC) {
                    "Paket Basic Anda terbatas $maxProduk produk. Sudah mencapai batas maksimal.\n\nUpgrade ke paket Custom atau Pro untuk menambah lebih banyak produk."
                } else {
                    "Sudah mencapai batas maksimal produk."
                },
                paket = paket,
                needsUpgrade = paket == PaketAplikasi.BASIC
            )
        } else {
            // Warn jika sudah dekat limit (90% dari batas)
            val threshold = (maxProduk * 0.9).toInt()
            if (jumlahProdukSekarang >= threshold) {
                PaketValidationResult.Warning(
                    message = "Anda sudah menggunakan $jumlahProdukSekarang dari $maxProduk produk. Segera upgrade jika ingin menambah lebih banyak.",
                    paket = paket
                )
            } else {
                PaketValidationResult.OK()
            }
        }
    }

    /**
     * Cek apakah user boleh tambah Kasir baru berdasarkan paket.
     * BASIC hanya boleh 1 Admin, tidak bisa tambah Kasir.
     */
    fun validateTambahKasir(
        paket: PaketAplikasi,
        jumlahKasirSekarang: Int
    ): PaketValidationResult {
        return if (paket == PaketAplikasi.BASIC && jumlahKasirSekarang > 0) {
            PaketValidationResult.Error(
                message = "Paket Basic hanya mendukung 1 Admin. Tidak bisa menambah pengguna Kasir.\n\nUpgrade ke paket Custom atau Pro untuk menambah pengguna.",
                paket = paket,
                needsUpgrade = true
            )
        } else {
            PaketValidationResult.OK()
        }
    }

    /**
     * Cek apakah fitur tertentu tersedia di paket ini.
     */
    fun validateAksesFitur(
        paket: PaketAplikasi,
        fitur: PaketFeature
    ): PaketValidationResult {
        val allowed = when(fitur) {
            // Fitur BASIC tersedia di semua paket
            PaketFeature.KASIR,
            PaketFeature.PRODUK,
            PaketFeature.BARCODE,
            PaketFeature.PRINTER -> true

            // Fitur CUSTOM tersedia di CUSTOM & PRO
            PaketFeature.LAPORAN,
            PaketFeature.IMPORT_EXPORT,
            PaketFeature.BACKUP,
            PaketFeature.CUSTOM_BRANDING -> paket in listOf(PaketAplikasi.CUSTOM, PaketAplikasi.PRO)

            // Fitur PRO hanya di PRO
            PaketFeature.MANAJEMEN_PENGGUNA -> paket == PaketAplikasi.PRO
        }

        return if (allowed) {
            PaketValidationResult.OK()
        } else {
            PaketValidationResult.Error(
                message = "Fitur ${fitur.label} tidak tersedia di paket ${paket.label}.\n\nUpgrade ke paket yang lebih tinggi untuk mengakses fitur ini.",
                paket = paket,
                needsUpgrade = true
            )
        }
    }
}

/**
 * Model hasil validasi paket.
 */
sealed class PaketValidationResult {
    data class OK : PaketValidationResult() {
        val isValid: Boolean = true
    }

    data class Warning(
        val message: String,
        val paket: PaketAplikasi
    ) : PaketValidationResult() {
        val isValid: Boolean = true
    }

    data class Error(
        val message: String,
        val paket: PaketAplikasi,
        val needsUpgrade: Boolean = false
    ) : PaketValidationResult() {
        val isValid: Boolean = false
    }
}

/**
 * Daftar fitur yang dapat di-gate berdasarkan paket.
 */
enum class PaketFeature(val label: String) {
    KASIR("Kasir"),
    PRODUK("Produk"),
    BARCODE("Barcode"),
    PRINTER("Printer"),
    LAPORAN("Laporan"),
    IMPORT_EXPORT("Import/Export Excel"),
    BACKUP("Backup & Restore"),
    CUSTOM_BRANDING("Custom Branding"),
    MANAJEMEN_PENGGUNA("Manajemen Pengguna")
}
