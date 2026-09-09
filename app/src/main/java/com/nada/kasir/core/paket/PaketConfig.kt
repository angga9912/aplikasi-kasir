package com.nada.kasir.core.paket

/**
 * Konfigurasi limit per paket untuk enforce freemium model.
 * BASIC (Gratis) - fitur dasar, terbatas jumlah produk & pengguna
 * CUSTOM - semua fitur BASIC + laporan, branding, import/export, backup
 * PRO - semua fitur CUSTOM + multi-user admin/kasir
 */
object PaketConfig {
    const val BASIC_MAX_PRODUK = 50
    const val BASIC_MAX_KASIR = 0  // hanya 1 Admin, tidak bisa tambah Kasir
    const val CUSTOM_MAX_PRODUK = 9999  // unlimited
    const val CUSTOM_MAX_KASIR = 9999   // unlimited
    const val PRO_MAX_PRODUK = 9999
    const val PRO_MAX_KASIR = 9999

    fun getMaxProduk(paket: PaketAplikasi): Int = when(paket) {
        PaketAplikasi.BASIC -> BASIC_MAX_PRODUK
        else -> CUSTOM_MAX_PRODUK  // CUSTOM & PRO
    }

    fun getMaxKasir(paket: PaketAplikasi): Int = when(paket) {
        PaketAplikasi.BASIC -> BASIC_MAX_KASIR
        else -> CUSTOM_MAX_KASIR  // CUSTOM & PRO
    }
}
