package com.nada.kasir.feature.pengguna

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nada.kasir.core.data.local.entity.UserRole
import com.nada.kasir.core.paket.canAddUser
import com.nada.kasir.feature.upgrade.PaketBadge
import com.nada.kasir.feature.upgrade.UpgradePromptDialog

/** MANAJEMEN PENGGUNA - khusus ADMIN (poin 18: "Mengelola" akun kasir/admin) dengan Freemium gating. */
@Composable
fun PenggunaScreen(viewModel: PenggunaViewModel = hiltViewModel()) {
    val daftar by viewModel.daftarPengguna.collectAsState()
    val paketAktif by viewModel.paketAktif.collectAsState()
    val upgradePrompt by viewModel.showUpgradePrompt.collectAsState()
    var showForm by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        floatingActionButton = {
            // Tombol hanya muncul jika user bisa tambah kasir (CUSTOM/PRO)
            if (canAddUser(paketAktif)) {
                FloatingActionButton(onClick = { showForm = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Pengguna")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Header dengan PaketBadge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Manajemen Pengguna",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                PaketBadge(paket = paketAktif)
            }

            // Warning untuk BASIC
            if (!canAddUser(paketAktif)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Fitur ini tidak tersedia di paket ${paketAktif.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            "Upgrade ke paket Custom atau Pro untuk menambah pengguna.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Daftar pengguna
            LazyColumn {
                items(daftar) { user ->
                    ListItem(
                        headlineContent = { Text(user.nama) },
                        supportingContent = { Text("${user.username} • ${user.role.name}${if (!user.aktif) " • NONAKTIF" else ""}") }
                    )
                    Divider()
                }
            }
        }
    }

    // Dialog tambah pengguna
    if (showForm) {
        TambahPenggunaDialog(
            onDismiss = { showForm = false },
            onSimpan = { nama, username, password, role ->
                viewModel.tambahPengguna(nama, username, password, role) { pesan -> errorMsg = pesan }
                showForm = false
            }
        )
    }

    // Dialog error
    errorMsg?.let { pesan ->
        AlertDialog(
            onDismissRequest = { errorMsg = null },
            confirmButton = { TextButton(onClick = { errorMsg = null }) { Text("OK") } },
            title = { Text("Perhatian") },
            text = { Text(pesan) }
        )
    }

    // Dialog upgrade (trigger saat user coba tambah kasir tapi BASIC)
    upgradePrompt?.let { (show, feature) ->
        if (show) {
            UpgradePromptDialog(
                currentPaket = paketAktif,
                attemptedFeature = feature,
                onDismiss = { viewModel.clearUpgradePrompt() }
            )
        }
    }
}

@Composable
private fun TambahPenggunaDialog(
    onDismiss: () -> Unit,
    onSimpan: (String, String, String, UserRole) -> Unit
) {
    var nama by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(UserRole.KASIR) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Pengguna") },
        text = {
            Column {
                OutlinedTextField(nama, { nama = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(username, { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(password, { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = role == UserRole.KASIR, onClick = { role = UserRole.KASIR })
                    Text("Kasir")
                    Spacer(Modifier.width(16.dp))
                    RadioButton(selected = role == UserRole.ADMIN, onClick = { role = UserRole.ADMIN })
                    Text("Admin")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSimpan(nama, username, password, role) }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}
