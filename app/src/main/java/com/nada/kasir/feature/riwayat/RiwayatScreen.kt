package com.nada.kasir.feature.riwayat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.nada.kasir.core.data.local.entity.TransactionStatus
import com.nada.kasir.core.util.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

/** RIWAYAT PENJUALAN (poin 13). */
@Composable
fun RiwayatScreen(viewModel: RiwayatViewModel = hiltViewModel()) {
    val riwayat by viewModel.riwayat.collectAsState()
    var konfirmasiBatalId by remember { mutableStateOf<Long?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val sdf = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("id", "ID")) }

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("Riwayat Penjualan", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            items(riwayat) { trx ->
                ListItem(
                    headlineContent = { Text(trx.noTransaksi) },
                    supportingContent = { Text(sdf.format(Date(trx.tanggalWaktu))) },
                    trailingContent = {
                        Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                            Text(CurrencyFormatter.format(trx.total))
                            if (trx.status == TransactionStatus.CANCELLED) {
                                Text("DIBATALKAN", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
                            } else {
                                TextButton(onClick = { konfirmasiBatalId = trx.id }) { Text("Batalkan") }
                            }
                        }
                    }
                )
                Divider()
            }
        }
    }

    // Pembatalan transaksi WAJIB konfirmasi (poin 13)
    konfirmasiBatalId?.let { id ->
        AlertDialog(
            onDismissRequest = { konfirmasiBatalId = null },
            title = { Text("Batalkan Transaksi?") },
            text = { Text("Stok akan dikembalikan. Transaksi tidak akan dihapus permanen.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.batalkanTransaksi(id) { pesan -> errorMsg = pesan }
                    konfirmasiBatalId = null
                }) { Text("Ya, Batalkan") }
            },
            dismissButton = { TextButton(onClick = { konfirmasiBatalId = null }) { Text("Tidak") } }
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
