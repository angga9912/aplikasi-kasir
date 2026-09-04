package com.nada.kasir.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nada.kasir.feature.dashboard.DashboardScreen
import com.nada.kasir.feature.kasir.KasirScreen
import com.nada.kasir.feature.produk.ProdukScreen
import com.nada.kasir.feature.riwayat.RiwayatScreen

sealed class NadaRoute(val route: String) {
    object Dashboard : NadaRoute("dashboard")
    object Kasir : NadaRoute("kasir")
    object Produk : NadaRoute("produk")
    object Riwayat : NadaRoute("riwayat")
    // Stok, Laporan, PengaturanToko, PengaturanPrinter ditambahkan progresif per phase
}

@Composable
fun NadaNavGraph(navController: NavHostController = rememberNavController(), currentUserId: Long = 1L) {
    NavHost(navController = navController, startDestination = NadaRoute.Dashboard.route) {
        composable(NadaRoute.Dashboard.route) {
            DashboardScreen(
                onBukaKasir = { navController.navigate(NadaRoute.Kasir.route) },
                onBukaProduk = { navController.navigate(NadaRoute.Produk.route) },
                onBukaRiwayat = { navController.navigate(NadaRoute.Riwayat.route) }
            )
        }
        composable(NadaRoute.Kasir.route) { KasirScreen(currentUserId = currentUserId) }
        composable(NadaRoute.Produk.route) { ProdukScreen() }
        composable(NadaRoute.Riwayat.route) { RiwayatScreen() }
    }
}
