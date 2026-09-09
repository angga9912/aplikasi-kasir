package com.nada.kasir.feature.upgrade

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.nada.kasir.core.paket.PaketAplikasi

/**
 * Dialog upgrade untuk freemium model.
 * Tampil saat user coba exceed limit atau akses fitur yang tidak tersedia di paket mereka.
 */
@Composable
fun UpgradePromptDialog(
    currentPaket: PaketAplikasi,
    attemptedFeature: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val whatsappAdminPhone = "62812345678"  // TODO: Ubah ke nomor admin Anda
    val whatsappMessage = "Halo, saya mau upgrade dari paket ${currentPaket.label} ke paket yang lebih tinggi untuk menggunakan fitur: $attemptedFeature"
    val whatsappUrl = "https://wa.me/$whatsappAdminPhone?text=${Uri.encode(whatsappMessage)}"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header dengan close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Upgrade Paket",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Pesan utama
                Text(
                    "Fitur '$attemptedFeature' tidak tersedia di paket ${currentPaket.label}",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(24.dp))

                // Tabel perbandingan paket
                PaketComparisonTable(currentPaket)

                Spacer(Modifier.height(24.dp))

                // Tombol upgrade via WhatsApp
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
                        context.startActivity(intent)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366)  // WhatsApp green
                    )
                ) {
                    Text("Upgrade via WhatsApp Admin", color = androidx.compose.ui.graphics.Color.White)
                }

                Spacer(Modifier.height(12.dp))

                // Tombol batal
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Lanjutkan dengan Paket Ini")
                }

                Spacer(Modifier.height(12.dp))

                // Footnote
                Text(
                    "Admin akan memproses upgrade Anda dalam 24 jam kerja.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PaketComparisonTable(currentPaket: PaketAplikasi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Fitur", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
            Text("Basic", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("Custom", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text("Pro", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Fitur rows
        val features = listOf(
            Triple("Kasir & Produk", true, true),
            Triple("Laporan", false, true),
            Triple("Import/Export", false, true),
            Triple("Backup & Restore", false, true),
            Triple("Multi User", false, true)
        )

        features.forEach { (feature, customPro, pro) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(feature, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodySmall)
                Text("✓", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(if (customPro) "✓" else "", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(if (pro) "✓" else "", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }
    }
}

// Tambahkan import untuk Color jika belum ada
import androidx.compose.ui.graphics.Color
