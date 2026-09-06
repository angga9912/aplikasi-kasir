package com.nada.kasir.feature.pengaturan_toko

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nada.kasir.branding.ThemeConfig
import com.nada.kasir.core.data.local.entity.StoreEntity

/** PENGATURAN TOKO (poin 1) + branding warna (poin 2) + custom struk (poin 10). */
@Composable
fun PengaturanTokoScreen(viewModel: PengaturanTokoViewModel = hiltViewModel()) {
    val storeDb by viewModel.store.collectAsState()
    var tersimpanPesan by remember { mutableStateOf(false) }

    // State form lokal, diisi dari data toko begitu tersedia
    var nama by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var telepon by remember { mutableStateOf("") }
    var pemilik by remember { mutableStateOf("") }
    var slogan by remember { mutableStateOf("") }
    var footer by remember { mutableStateOf("") }
    var mataUang by remember { mutableStateOf("Rp") }
    var ukuranKertas by remember { mutableStateOf("58mm") }
    var warnaUtama by remember { mutableStateOf("#2E7D32") }
    var tampilkanLogo by remember { mutableStateOf(true) }
    var tampilkanAlamat by remember { mutableStateOf(true) }
    var tampilkanWa by remember { mutableStateOf(true) }
    var tampilkanDiskon by remember { mutableStateOf(true) }
    var sudahDiisi by remember { mutableStateOf(false) }

    LaunchedEffect(storeDb) {
        val s = storeDb ?: return@LaunchedEffect
        if (!sudahDiisi) {
            nama = s.nama; alamat = s.alamat; whatsapp = s.whatsapp; telepon = s.telepon
            pemilik = s.pemilik; slogan = s.slogan; footer = s.footerStruk; mataUang = s.mataUang
            ukuranKertas = s.ukuranKertas; warnaUtama = s.warnaUtama
            tampilkanLogo = s.tampilkanLogoStruk; tampilkanAlamat = s.tampilkanAlamatStruk
            tampilkanWa = s.tampilkanWaStruk; tampilkanDiskon = s.tampilkanDiskonStruk
            sudahDiisi = true
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScrollFallback().padding(16.dp)) {
        Text("Pengaturan Toko", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(nama, { nama = it }, label = { Text("Nama Toko") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(alamat, { alamat = it }, label = { Text("Alamat") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(whatsapp, { whatsapp = it }, label = { Text("Nomor WhatsApp") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(telepon, { telepon = it }, label = { Text("Nomor Telepon") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(pemilik, { pemilik = it }, label = { Text("Nama Pemilik") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(slogan, { slogan = it }, label = { Text("Slogan") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(footer, { footer = it }, label = { Text("Footer Struk") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(mataUang, { mataUang = it }, label = { Text("Simbol Mata Uang") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))
        Text("Ukuran Kertas Struk", style = MaterialTheme.typography.titleMedium)
        Row {
            listOf("58mm", "80mm").forEach { ukuran ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 16.dp)) {
                    RadioButton(selected = ukuranKertas == ukuran, onClick = { ukuranKertas = ukuran })
                    Text(ukuran)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Warna Utama Aplikasi", style = MaterialTheme.typography.titleMedium)
        Row {
            ThemeConfig.PRESET_WARNA.forEach { (hex, _) ->
                val warna = try { Color(("FF" + hex.removePrefix("#")).toLong(16)) } catch (e: Exception) { Color.Gray }
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(warna)
                        .border(width = if (warnaUtama == hex) 3.dp else 0.dp, color = Color.Black, shape = CircleShape)
                        .clickable { warnaUtama = hex }
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Pengaturan Struk", style = MaterialTheme.typography.titleMedium)
        BarisToggle("Tampilkan Logo di Struk", tampilkanLogo) { tampilkanLogo = it }
        BarisToggle("Tampilkan Alamat di Struk", tampilkanAlamat) { tampilkanAlamat = it }
        BarisToggle("Tampilkan WhatsApp di Struk", tampilkanWa) { tampilkanWa = it }
        BarisToggle("Tampilkan Diskon di Struk", tampilkanDiskon) { tampilkanDiskon = it }
        Text(
            "Catatan: Harga Beli TIDAK PERNAH ditampilkan di struk pelanggan.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                val existing = storeDb ?: StoreEntity()
                viewModel.simpan(
                    existing.copy(
                        nama = nama, alamat = alamat, whatsapp = whatsapp, telepon = telepon,
                        pemilik = pemilik, slogan = slogan, footerStruk = footer, mataUang = mataUang,
                        ukuranKertas = ukuranKertas, warnaUtama = warnaUtama,
                        tampilkanLogoStruk = tampilkanLogo, tampilkanAlamatStruk = tampilkanAlamat,
                        tampilkanWaStruk = tampilkanWa, tampilkanDiskonStruk = tampilkanDiskon,
                        tampilkanHargaBeliStruk = false // dipaksa false, tidak bisa diubah dari UI (poin 10)
                    )
                ) { tersimpanPesan = true }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) { Text("Simpan Pengaturan") }
        Spacer(Modifier.height(24.dp))
    }

    if (tersimpanPesan) {
        AlertDialog(
            onDismissRequest = { tersimpanPesan = false },
            confirmButton = { TextButton(onClick = { tersimpanPesan = false }) { Text("OK") } },
            title = { Text("Tersimpan") },
            text = { Text("Pengaturan toko berhasil disimpan. Warna aplikasi akan langsung berubah.") }
        )
    }
}

@Composable
private fun BarisToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// Helper kecil supaya Column bisa discroll tanpa nambah dependency baru di sini
@Composable
private fun Modifier.verticalScrollFallback(): Modifier {
    val scrollState = rememberScrollState()
    return this.verticalScroll(scrollState)
}
