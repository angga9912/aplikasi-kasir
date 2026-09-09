package com.nada.kasir.core.paket

import androidx.compose.runtime.Composable

/**
 * Helper untuk feature gating di UI layer.
 * Gunakan @Composable function ini untuk show/hide menu, tombol, atau fitur berdasarkan paket.
 */
@Composable
fun isPaketFeatureEnabled(paket: PaketAplikasi, feature: PaketFeature): Boolean {
    return when {
        paket == PaketAplikasi.PRO -> true
        paket == PaketAplikasi.CUSTOM && feature in listOf(
            PaketFeature.LAPORAN,
            PaketFeature.IMPORT_EXPORT,
            PaketFeature.BACKUP,
            PaketFeature.CUSTOM_BRANDING
        ) -> true
        paket == PaketAplikasi.BASIC && feature in listOf(
            PaketFeature.KASIR,
            PaketFeature.PRODUK,
            PaketFeature.BARCODE,
            PaketFeature.PRINTER
        ) -> true
        else -> false
    }
}

/**
 * Cek apakah pengguna bisa tambah kasir (hanya CUSTOM & PRO).
 */
fun canAddUser(paket: PaketAplikasi): Boolean {
    return paket in listOf(PaketAplikasi.CUSTOM, PaketAplikasi.PRO)
}
