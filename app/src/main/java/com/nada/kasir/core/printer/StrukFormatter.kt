package com.nada.kasir.core.printer

import com.nada.kasir.core.data.local.entity.PaymentEntity
import com.nada.kasir.core.data.local.entity.StoreEntity
import com.nada.kasir.core.data.local.entity.TransactionEntity
import com.nada.kasir.core.data.local.entity.TransactionItemEntity
import com.nada.kasir.core.util.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Menyusun struk (poin 9) menjadi perintah ESC/POS siap cetak, mengikuti
 * pengaturan custom toko (poin 10): tampil/sembunyi logo, alamat, WA, diskon,
 * dan TIDAK PERNAH menampilkan harga beli.
 */
object StrukFormatter {

    fun buatStruk(
        store: StoreEntity,
        transaction: TransactionEntity,
        items: List<TransactionItemEntity>,
        payment: PaymentEntity?
    ): ByteArray {
        val lebar = if (store.ukuranKertas == "80mm") 48 else 32
        val sdfTanggal = SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID"))
        val sdfJam = SimpleDateFormat("HH:mm", Locale("id", "ID"))
        val tanggalTransaksi = Date(transaction.tanggalWaktu)

        val builder = EscPosBuilder().reset()

        // Header: nama toko, alamat, WA (logo raster print bisa ditambahkan belakangan
        // via GS v 0 bitmap command - untuk Phase 2 fokus dulu ke teks, cukup untuk mayoritas printer 58mm)
        builder.alignCenter()
        if (store.tampilkanLogoStruk) {
            builder.bold(true).textLine("[ ${store.nama} ]").bold(false)
        } else {
            builder.bold(true).textLine(store.nama).bold(false)
        }
        if (store.tampilkanAlamatStruk && store.alamat.isNotBlank()) builder.textLine(store.alamat)
        if (store.tampilkanWaStruk && store.whatsapp.isNotBlank()) builder.textLine("WA: ${store.whatsapp}")
        builder.garis(lebar)

        // Info transaksi
        builder.alignLeft()
        builder.textLine("No: ${transaction.noTransaksi}")
        builder.textLine("Tanggal: ${sdfTanggal.format(tanggalTransaksi)}")
        builder.textLine("Jam: ${sdfJam.format(tanggalTransaksi)}")
        builder.garis(lebar)

        // Daftar item
        items.forEach { item ->
            builder.textLine(item.namaProdukSnapshot)
            val kiri = "${item.qty} x ${CurrencyFormatter.format(item.harga).removePrefix(store.mataUang).trim()}"
            val kanan = CurrencyFormatter.format(item.subtotal).removePrefix(store.mataUang).trim()
            builder.baris2Kolom(kiri, kanan, lebar)
        }
        builder.garis(lebar)

        // Total, diskon, pembayaran, kembalian
        if (store.tampilkanDiskonStruk && transaction.diskon > 0) {
            builder.baris2Kolom("DISKON", CurrencyFormatter.format(transaction.diskon), lebar)
        }
        builder.bold(true)
        builder.baris2Kolom("TOTAL", CurrencyFormatter.format(transaction.total), lebar)
        builder.bold(false)
        payment?.let {
            builder.baris2Kolom(it.metode.name, CurrencyFormatter.format(it.jumlahDiterima), lebar)
            if (it.kembalian > 0) {
                builder.baris2Kolom("KEMBALI", CurrencyFormatter.format(it.kembalian), lebar)
            }
        }
        builder.garis(lebar)

        // Footer
        builder.alignCenter()
        if (store.footerStruk.isNotBlank()) builder.textLine(store.footerStruk)

        builder.feedAndCut()
        return builder.build()
    }
}
