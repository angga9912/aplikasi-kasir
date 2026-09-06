package com.nada.kasir.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nada.kasir.core.session.SessionManager
import com.nada.kasir.feature.backup.BackupScreen
import com.nada.kasir.feature.dashboard.DashboardScreen
import com.nada.kasir.feature.kasir.KasirScreen
import com.nada.kasir.feature.laporan.LaporanScreen
import com.nada.kasir.feature.login.LoginScreen
import com.nada.kasir.feature.pengaturan_printer.PengaturanPrinterScreen
import com.nada.kasir.feature.pengaturan_toko.PengaturanTokoScreen
import com.nada.kasir.feature.pengguna.PenggunaScreen
import com.nada.kasir.feature.produk.ProdukScreen
import com.nada.kasir.feature.riwayat.RiwayatScreen

sealed class NadaRoute(val route: String) {
    object Login : NadaRoute("login")
    object Dashboard : NadaRoute("dashboard")
    object Kasir : NadaRoute("kasir")
    object Produk : NadaRoute("produk")
    object Riwayat : NadaRoute("riwayat")
    object PengaturanPrinter : NadaRoute("pengaturan_printer")
    object Backup : NadaRoute("backup")
    object Laporan : NadaRoute("laporan")
    object PengaturanToko : NadaRoute("pengaturan_toko")
    object Pengguna : NadaRoute("pengguna")
}

@Composable
fun NadaNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = NadaRoute.Login.route) {
        composable(NadaRoute.Login.route) {
            LoginScreen(onLoginBerhasil = {
                navController.navigate(NadaRoute.Dashboard.route) {
                    popUpTo(NadaRoute.Login.route) { inclusive = true }
                }
            })
        }
        composable(NadaRoute.Dashboard.route) {
            val sessionManager: SessionManager = hiltViewModelSession()
            DashboardScreen(
                isAdmin = sessionManager.isAdmin(),
                onBukaKasir = { navController.navigate(NadaRoute.Kasir.route) },
                onBukaProduk = { navController.navigate(NadaRoute.Produk.route) },
                onBukaRiwayat = { navController.navigate(NadaRoute.Riwayat.route) },
                onBukaPengaturanPrinter = { navController.navigate(NadaRoute.PengaturanPrinter.route) },
                onBukaBackup = { navController.navigate(NadaRoute.Backup.route) },
                onBukaLaporan = { navController.navigate(NadaRoute.Laporan.route) },
                onBukaPengaturanToko = { navController.navigate(NadaRoute.PengaturanToko.route) },
                onBukaPengguna = { navController.navigate(NadaRoute.Pengguna.route) },
                onLogout = {
                    sessionManager.logout()
                    navController.navigate(NadaRoute.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(NadaRoute.Kasir.route) {
            val sessionManager: SessionManager = hiltViewModelSession()
            KasirScreen(currentUserId = sessionManager.currentUser.value?.id ?: 1L)
        }
        composable(NadaRoute.Produk.route) {
            val sessionManager: SessionManager = hiltViewModelSession()
            ProdukScreen(isAdmin = sessionManager.isAdmin())
        }
        composable(NadaRoute.Riwayat.route) {
            val sessionManager: SessionManager = hiltViewModelSession()
            RiwayatScreen(isAdmin = sessionManager.isAdmin())
        }
        composable(NadaRoute.PengaturanPrinter.route) { PengaturanPrinterScreen() }
        composable(NadaRoute.Backup.route) { BackupScreen() }
        composable(NadaRoute.Laporan.route) { LaporanScreen() }
        composable(NadaRoute.PengaturanToko.route) { PengaturanTokoScreen() }
        composable(NadaRoute.Pengguna.route) { PenggunaScreen() }
    }
}

/**
 * SessionManager di-scope Singleton lewat Hilt, jadi instance yang sama bisa
 * diambil dari Composable manapun tanpa perlu diteruskan lewat parameter terus-menerus.
 */
@Composable
private fun hiltViewModelSession(): SessionManager {
    val holder: SessionHolderViewModel = hiltViewModel()
    return holder.sessionManager
}
