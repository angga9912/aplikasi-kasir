package com.nada.kasir.core.data.repository

import com.nada.kasir.core.data.local.dao.ProductDao
import com.nada.kasir.core.data.local.entity.ProductEntity
import com.nada.kasir.core.util.AppError
import com.nada.kasir.core.util.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
    fun observeActive(): Flow<List<ProductEntity>> = productDao.observeActiveProducts()

    fun search(query: String): Flow<List<ProductEntity>> = productDao.search(query)

    fun observeStokMenipis(): Flow<List<ProductEntity>> = productDao.observeStokMenipis()

    fun observeStokHabis(): Flow<List<ProductEntity>> = productDao.observeStokHabis()

    suspend fun cariByBarcode(barcode: String): ProductEntity? = productDao.findByBarcode(barcode)

    suspend fun simpan(product: ProductEntity): Result<Long> {
        // Barcode tidak boleh duplikat (poin 5)
        if (!product.barcode.isNullOrBlank()) {
            val jumlah = productDao.countByBarcode(product.barcode)
            if (jumlah > 0 && product.id == 0L) {
                return Result.Failure(AppError.BarcodeDuplikat)
            }
        }
        return try {
            val id = if (product.id == 0L) {
                productDao.insert(product)
            } else {
                productDao.update(product)
                product.id
            }
            Result.Success(id)
        } catch (e: Exception) {
            Result.Failure(AppError.TransaksiGagalDisimpan)
        }
    }

    suspend fun hapus(id: Long) {
        productDao.softDelete(id, System.currentTimeMillis()) // soft delete, poin 26
    }
}
