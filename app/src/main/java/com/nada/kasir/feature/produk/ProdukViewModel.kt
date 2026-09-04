package com.nada.kasir.feature.produk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nada.kasir.core.data.local.entity.ProductEntity
import com.nada.kasir.core.data.repository.ProductRepository
import com.nada.kasir.core.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProdukViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    val daftarProduk: StateFlow<List<ProductEntity>> = productRepository.observeActive()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun simpan(product: ProductEntity, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val result = productRepository.simpan(product)) {
                is Result.Failure -> onError(result.error.pesan)
                is Result.Success -> Unit
            }
        }
    }

    fun hapus(id: Long) {
        viewModelScope.launch { productRepository.hapus(id) }
    }
}
