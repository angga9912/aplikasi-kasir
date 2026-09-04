package com.nada.kasir.feature.produk

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nada.kasir.core.data.local.entity.ProductEntity
import com.nada.kasir.core.util.CurrencyFormatter

/** Halaman DATA PRODUK (poin 6). Import/Export Excel ditambahkan di Phase 3. */
@Composable
fun ProdukScreen(viewModel: ProdukViewModel = hiltViewModel()) {
    val produkList by viewModel.daftarProduk.collectAsState()
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<ProductEntity?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { editing = null; showForm = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Produk")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
            items(produkList) { produk ->
                ListItem(
                    headlineContent = { Text(produk.nama) },
                    supportingContent = {
                        Text("${produk.kodeProduk} • Stok: ${produk.stok} • ${CurrencyFormatter.format(produk.hargaJual)}")
                    },
                    trailingContent = {
                        Row {
                            TextButton(onClick = { editing = produk; showForm = true }) { Text("Edit") }
                            TextButton(onClick = { viewModel.hapus(produk.id) }) { Text("Hapus") }
                        }
                    }
                )
                Divider()
            }
        }
    }

    if (showForm) {
        ProdukFormDialog(
            initial = editing,
            onDismiss = { showForm = false },
            onSimpan = { produk ->
                viewModel.simpan(produk) { pesan -> errorMsg = pesan }
                showForm = false
            }
        )
    }

    errorMsg?.let { pesan ->
        AlertDialog(
            onDismissRequest = { errorMsg = null },
            confirmButton = { TextButton(onClick = { errorMsg = null }) { Text("OK") } },
            title = { Text("Perhatian") },
            text = { Text(pesan) }
        )
    }
}

@Composable
private fun ProdukFormDialog(
    initial: ProductEntity?,
    onDismiss: () -> Unit,
    onSimpan: (ProductEntity) -> Unit
) {
    var kode by remember { mutableStateOf(initial?.kodeProduk ?: "") }
    var barcode by remember { mutableStateOf(initial?.barcode ?: "") }
    var nama by remember { mutableStateOf(initial?.nama ?: "") }
    var hargaBeli by remember { mutableStateOf(initial?.hargaBeli?.toString() ?: "") }
    var hargaJual by remember { mutableStateOf(initial?.hargaJual?.toString() ?: "") }
    var stok by remember { mutableStateOf(initial?.stok?.toString() ?: "0") }
    var stokMin by remember { mutableStateOf(initial?.stokMinimum?.toString() ?: "5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Tambah Produk" else "Edit Produk") },
        text = {
            Column {
                OutlinedTextField(kode, { kode = it }, label = { Text("Kode Produk") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(barcode, { barcode = it }, label = { Text("Barcode") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(nama, { nama = it }, label = { Text("Nama Produk") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(hargaBeli, { hargaBeli = it }, label = { Text("Harga Beli") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(hargaJual, { hargaJual = it }, label = { Text("Harga Jual") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(stok, { stok = it }, label = { Text("Stok") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(stokMin, { stokMin = it }, label = { Text("Stok Minimum") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSimpan(
                    ProductEntity(
                        id = initial?.id ?: 0,
                        kodeProduk = kode,
                        barcode = barcode.ifBlank { null },
                        nama = nama,
                        categoryId = initial?.categoryId,
                        hargaBeli = hargaBeli.toDoubleOrNull() ?: 0.0,
                        hargaJual = hargaJual.toDoubleOrNull() ?: 0.0,
                        stok = stok.toIntOrNull() ?: 0,
                        stokMinimum = stokMin.toIntOrNull() ?: 5
                    )
                )
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}
