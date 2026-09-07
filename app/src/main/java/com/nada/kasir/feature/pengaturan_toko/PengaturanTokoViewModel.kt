package com.nada.kasir.feature.pengaturan_toko

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nada.kasir.core.data.local.entity.StoreEntity
import com.nada.kasir.core.data.repository.StoreRepository
import com.nada.kasir.core.paket.PaketAplikasi
import com.nada.kasir.core.paket.PaketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PengaturanTokoViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val paketRepository: PaketRepository
) : ViewModel() {

    val store: StateFlow<StoreEntity?> = storeRepository.observeStore()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val paketAktif: StateFlow<PaketAplikasi> = paketRepository.observePaketAktif()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PaketAplikasi.PRO)

    fun simpan(store: StoreEntity, onSelesai: () -> Unit) {
        viewModelScope.launch {
            storeRepository.simpan(store)
            onSelesai()
        }
    }

    fun ubahPaket(paket: PaketAplikasi) {
        viewModelScope.launch { paketRepository.setPaketAktif(paket) }
    }
}
