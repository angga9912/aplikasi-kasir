package com.nada.kasir.feature.kasir

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nada.kasir.core.data.local.entity.MetodePembayaran
import com.nada.kasir.core.util.CurrencyFormatter

/**
 * Halaman Kasir - fitur utama aplikasi (poin 4).
 * Layout: kiri = pencarian & grid produk, kanan/bawah = keranjang + tombol BAYAR besar.
 */
@Composable
fun KasirScreen(
    currentUserId: Long,
    viewModel: KasirViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showPembayaranDialog by remember { mutableStateOf(false) }

    if (state.transaksiBerhasilId != null) {
        TransaksiBerhasilDialog(
            onTransaksiBaru = { viewModel.mulaiTransaksiBaru() },
            onCetak = { /* TODO Phase 2: panggil BluetoothPrinterManager */ },
            onBagikan = { /* TODO Phase 2: share struk via FileProvider */ }
        )
        return
    }

    state.errorPesan?.let { pesan ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            confirmButton = { TextButton(onClick = { viewModel.clearError() }) { Text("OK") } },
            title = { Text("Perhatian") },
            text = { Text(pesan) }
        )
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Kolom kiri: pencarian & daftar produk
        Column(modifier = Modifier.weight(1.4f).padding(12.dp)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("Cari produk / kode") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    // TODO Phase 2: ganti dengan IconButton kamera untuk scan barcode
                }
            )
            Spacer(Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.produk) { produk ->
                    ProdukKasirCard(
                        nama = produk.nama,
                        harga = produk.hargaJual,
                        stok = produk.stok,
                        onClick = { viewModel.tambahKeKeranjang(produk) }
                    )
                }
            }
        }

        // Kolom kanan: keranjang & pembayaran
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(12.dp)
        ) {
            Text("Keranjang", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.keranjang) { item ->
                    KeranjangRow(
                        nama = item.nama,
                        qty = item.qty,
                        harga = item.harga,
                        subtotal = item.subtotal,
                        onQtyChange = { qtyBaru -> viewModel.ubahQty(item.productId, qtyBaru) }
                    )
                    Divider()
                }
            }

            Spacer(Modifier.height(8.dp))
            RingkasanBaris("Subtotal", state.subtotal)
            RingkasanBaris("Diskon", state.diskonTotal)
            RingkasanBaris("Total", state.total, tebal = true)

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { showPembayaranDialog = true },
                enabled = state.keranjang.isNotEmpty() && !state.isProsesBayar,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(if (state.isProsesBayar) "Memproses..." else "BAYAR", style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    if (showPembayaranDialog) {
        PembayaranDialog(
            total = state.total,
            onDismiss = { showPembayaranDialog = false },
            onKonfirmasi = { metode, jumlahDiterima ->
                showPembayaranDialog = false
                viewModel.bayar(currentUserId, metode, jumlahDiterima)
            }
        )
    }
}

@Composable
private fun ProdukKasirCard(nama: String, harga: Double, stok: Int, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(nama, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
            Spacer(Modifier.height(4.dp))
            Text(CurrencyFormatter.format(harga), style = MaterialTheme.typography.bodyLarge)
            Text("Stok: $stok", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun KeranjangRow(nama: String, qty: Int, harga: Double, subtotal: Double, onQtyChange: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(nama, style = MaterialTheme.typography.bodyMedium)
            Text(CurrencyFormatter.format(harga), style = MaterialTheme.typography.labelSmall)
        }
        Row {
            IconButtonQty(label = "-", onClick = { onQtyChange(qty - 1) })
            Text("$qty", modifier = Modifier.padding(horizontal = 8.dp))
            IconButtonQty(label = "+", onClick = { onQtyChange(qty + 1) })
        }
        Text(CurrencyFormatter.format(subtotal), modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun IconButtonQty(label: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.size(36.dp), contentPadding = PaddingValues(0.dp)) {
        Text(label)
    }
}

@Composable
private fun RingkasanBaris(label: String, nilai: Double, tebal: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = if (tebal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium)
        Text(
            CurrencyFormatter.format(nilai),
            style = if (tebal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PembayaranDialog(total: Double, onDismiss: () -> Unit, onKonfirmasi: (MetodePembayaran, Double) -> Unit) {
    var metode by remember { mutableStateOf(MetodePembayaran.TUNAI) }
    var uangDiterimaText by remember { mutableStateOf("") }
    val uangDiterima = uangDiterimaText.toDoubleOrNull() ?: 0.0
    val kembalian = uangDiterima - total

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pembayaran") },
        text = {
            Column {
                Text("Total: ${CurrencyFormatter.format(total)}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                MetodePembayaran.values().forEach { m ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = metode == m, onClick = { metode = m })
                        Text(m.name)
                    }
                }
                if (metode == MetodePembayaran.TUNAI) {
                    OutlinedTextField(
                        value = uangDiterimaText,
                        onValueChange = { uangDiterimaText = it },
                        label = { Text("Uang diterima") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Kembalian: ${CurrencyFormatter.format(if (kembalian > 0) kembalian else 0.0)}")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val jumlah = if (metode == MetodePembayaran.TUNAI) uangDiterima else total
                onKonfirmasi(metode, jumlah)
            }) { Text("Konfirmasi") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
private fun TransaksiBerhasilDialog(onTransaksiBaru: () -> Unit, onCetak: () -> Unit, onBagikan: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("TRANSAKSI BERHASIL") },
        text = { Text("Transaksi telah tersimpan.") },
        confirmButton = { TextButton(onClick = onTransaksiBaru) { Text("Transaksi Baru") } },
        dismissButton = {
            Row {
                TextButton(onClick = onCetak) { Text("Cetak Struk") }
                TextButton(onClick = onBagikan) { Text("Bagikan") }
            }
        }
    )
}
