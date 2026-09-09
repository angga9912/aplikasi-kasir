package com.nada.kasir.feature.pengguna

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nada.kasir.core.data.local.entity.UserEntity
import com.nada.kasir.core.data.local.entity.UserRole
import com.nada.kasir.core.data.repository.UserRepository
import com.nada.kasir.core.paket.PaketAplikasi
import com.nada.kasir.core.paket.PaketRepository
import com.nada.kasir.core.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PenggunaViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val paketRepository: PaketRepository
) : ViewModel() {

    val daftarPengguna: StateFlow<List<UserEntity>> = userRepository.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paketAktif: StateFlow<PaketAplikasi> = paketRepository.observePaketAktif()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PaketAplikasi.BASIC)

    private val _showUpgradePrompt = MutableStateFlow<Pair<Boolean, String>?>(null)  // Pair(show, feature)
    val showUpgradePrompt: StateFlow<Pair<Boolean, String>?> = _showUpgradePrompt

    fun tambahPengguna(nama: String, username: String, password: String, role: UserRole, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val result = userRepository.buatPengguna(nama, username, password, role)) {
                is Result.Failure -> {
                    val errorMsg = result.error.pesan
                    // Cek apakah error adalah limit paket - kalau iya, show upgrade dialog
                    if (errorMsg.contains("Basic", ignoreCase = true) || 
                        errorMsg.contains("upgrade", ignoreCase = true)) {
                        _showUpgradePrompt.value = Pair(true, "Tambah Pengguna")
                    }
                    onError(errorMsg)
                }
                is Result.Success -> Unit
            }
        }
    }

    fun clearUpgradePrompt() { _showUpgradePrompt.value = null }
}
