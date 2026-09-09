package com.nada.kasir.core.paket

import com.nada.kasir.core.data.local.dao.SettingDao
import com.nada.kasir.core.data.local.entity.SettingEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val KEY_PAKET_AKTIF = "paket_aktif"

@Singleton
class PaketRepository @Inject constructor(
    private val settingDao: SettingDao
) {
    /**
     * Default BASIC untuk MVP freemium (gratis untuk semua user baru).
     * Sebelumnya DEFAULT PRO hanya untuk testing, sekarang ubah ke BASIC.
     */
    fun observePaketAktif(): Flow<PaketAplikasi> = settingDao.observeAll().map { list ->
        val value = list.firstOrNull { it.key == KEY_PAKET_AKTIF }?.value
        value?.let { runCatching { PaketAplikasi.valueOf(it) }.getOrNull() } ?: PaketAplikasi.BASIC
    }

    suspend fun getPaketAktif(): PaketAplikasi {
        val setting = settingDao.findByKey(KEY_PAKET_AKTIF)
        return setting?.value?.let { runCatching { PaketAplikasi.valueOf(it) }.getOrNull() } ?: PaketAplikasi.BASIC
    }

    suspend fun setPaketAktif(paket: PaketAplikasi) {
        settingDao.upsert(SettingEntity(key = KEY_PAKET_AKTIF, value = paket.name))
    }
}
