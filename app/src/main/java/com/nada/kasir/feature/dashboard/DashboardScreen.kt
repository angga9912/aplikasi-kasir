package com.nada.kasir.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nada.kasir.core.util.CurrencyFormatter

/**
 * Dashboard sederhana (poin 3), dengan menu yang menyesuaikan peran pengguna (poin 18):
 * ADMIN melihat semua menu; KASIR hanya melihat menu operasional harian.
 */
@Composable
fun DashboardScreen(
    isAdmin: Boolean,
    onBukaKasir: () -> Unit,
    onBukaProduk: () -> Unit,
    onBukaRiwayat: () -> Unit,
    onBukaPengaturanPrinter: () -> Unit,
    onBukaBackup: () -> Unit,
    onBukaLaporan: () -> Unit,
    onBukaPengaturanToko: () -> Unit,
    onBukaPengguna: () -> Unit,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("NADA KASIR CUSTOM", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = onLogout) { Text("Keluar") }
        }
        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { RingkasanCard("Penjualan Hari Ini", CurrencyFormatter.format(state.penjualanHariIni)) }
            item { RingkasanCard("Jumlah Transaksi", "${state.jumlahTransaksi}") }
            item { RingkasanCard("Stok Menipis", "${state.stokMenipis}") }
            item { RingkasanCard("Stok Habis", "${state.stokHabis}") }
        }

        Spacer(Modifier.height(24.dp))
        Text("Menu Cepat", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Button(onClick = onBukaKasir, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Transaksi Baru") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onBukaProduk, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text(if (isAdmin) "Tambah Produk" else "Lihat Produk")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onBukaRiwayat, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Riwayat Transaksi") }

        // Menu di bawah ini HANYA untuk ADMIN (poin 18)
        if (isAdmin) {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onBukaLaporan, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Laporan") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onBukaPengaturanPrinter, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Pengaturan Printer") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onBukaPengaturanToko, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Pengaturan Toko") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onBukaPengguna, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Manajemen Pengguna") }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onBukaBackup, modifier = Modifier.fillMaxWidth().height(56.dp)) { Text("Backup & Restore Data") }
        }
    }
}

@Composable
private fun RingkasanCard(label: String, value: String) {
    ElevatedCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge)
        }
    }
}
