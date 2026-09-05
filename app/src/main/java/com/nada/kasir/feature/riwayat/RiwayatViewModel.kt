package com.nada.kasir.feature.riwayat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nada.kasir.core.data.local.entity.TransactionEntity
import com.nada.kasir.core.data.repository.PrinterRepository
import com.nada.kasir.core.data.repository.StoreRepository
import com.nada.kasir.core.data.repository.TransactionRepository
import com.nada.kasir.core.printer.BluetoothPrinterManager
import com.nada.kasir.core.printer.StrukFormatter
import com.nada.kasir.core.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RiwayatViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val storeRepository: StoreRepository,
    private val printerRepository: PrinterRepository,
    private val bluetoothPrinterManager: BluetoothPrinterManager
) : ViewModel() {

    private val queryFlow = MutableStateFlow("")

    // Default: filter hari ini. Filter tanggal custom bisa ditambahkan di UI (poin 13).
    val riwayat: StateFlow<List<TransactionEntity>> = queryFlow.flatMapLatest { q ->
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        val end = start + 24 * 60 * 60 * 1000L
        transactionRepository.observeRiwayat(q, start, end)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChange(q: String) { queryFlow.value = q }

    fun batalkanTransaksi(id: Long, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val r = transactionRepository.batalkanTransaksi(id)) {
                is Result.Failure -> onError(r.error.pesan)
                is Result.Success -> Unit
            }
        }
    }

    /** Cetak Ulang struk dari riwayat (poin 13). */
    fun cetakUlang(transactionId: Long, onError: (String) -> Unit) {
        viewModelScope.launch {
            val printerDefault = printerRepository.getDefault()
            if (printerDefault == null) {
                onError("Belum ada printer default. Atur di menu Pengaturan Printer.")
                return@launch
            }
            val store = storeRepository.getOrCreateDefault()
            val (transaksi, items, payment) = transactionRepository.getDetail(transactionId)
            if (transaksi == null) { onError("Transaksi tidak ditemukan."); return@launch }
            val struk = StrukFormatter.buatStruk(store, transaksi, items, payment)
            when (val result = bluetoothPrinterManager.cetak(printerDefault.macAddress, struk)) {
                is Result.Failure -> onError(result.error.pesan)
                is Result.Success -> Unit
            }
        }
    }
}
