package com.nada.kasir.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nada.kasir.core.data.repository.ProductRepository
import com.nada.kasir.core.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

data class DashboardUiState(
    val penjualanHariIni: Double = 0.0,
    val jumlahTransaksi: Int = 0,
    val stokMenipis: Int = 0,
    val stokHabis: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    productRepository: ProductRepository
) : ViewModel() {

    private val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
    }
    private val startMillis = cal.timeInMillis
    private val endMillis = startMillis + 24 * 60 * 60 * 1000L

    val uiState: StateFlow<DashboardUiState> = combine(
        transactionRepository.observeTotalPenjualanHariIni(startMillis, endMillis),
        transactionRepository.observeJumlahTransaksiHariIni(startMillis, endMillis),
        productRepository.observeStokMenipis(),
        productRepository.observeStokHabis()
    ) { total, jumlah, menipis, habis ->
        DashboardUiState(total, jumlah, menipis.size, habis.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())
}
