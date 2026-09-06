package com.nada.kasir.core.data.local.dao

import androidx.room.*
import com.nada.kasir.core.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert
    suspend fun insertItems(items: List<TransactionItemEntity>)

    @Insert
    suspend fun insertPayment(payment: PaymentEntity)

    @Query("SELECT COUNT(*) FROM transactions WHERE noTransaksi = :noTransaksi")
    suspend fun countByNoTransaksi(noTransaksi: String): Int

    @Query("SELECT COUNT(*) FROM transactions WHERE noTransaksi LIKE :prefix || '%'")
    suspend fun countTodayTransactions(prefix: String): Int

    @Query("""
        SELECT * FROM transactions 
        WHERE (:query = '' OR noTransaksi LIKE '%' || :query || '%')
        AND tanggalWaktu BETWEEN :startMillis AND :endMillis
        ORDER BY tanggalWaktu DESC
    """)
    fun observeRiwayat(query: String, startMillis: Long, endMillis: Long): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun findById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transaction_items WHERE transactionId = :transactionId")
    suspend fun getItems(transactionId: Long): List<TransactionItemEntity>

    @Query("SELECT * FROM payments WHERE transactionId = :transactionId LIMIT 1")
    suspend fun getPayment(transactionId: Long): PaymentEntity?

    @Query("UPDATE transactions SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: TransactionStatus)

    @Query("SELECT COALESCE(SUM(total),0) FROM transactions WHERE status = 'COMPLETED' AND tanggalWaktu BETWEEN :startMillis AND :endMillis")
    fun observeTotalPenjualan(startMillis: Long, endMillis: Long): Flow<Double>

    @Query("SELECT COUNT(*) FROM transactions WHERE status = 'COMPLETED' AND tanggalWaktu BETWEEN :startMillis AND :endMillis")
    fun observeJumlahTransaksi(startMillis: Long, endMillis: Long): Flow<Int>

    // === Untuk Backup/Restore & Export Excel (Phase 3) ===
    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactionsForBackup(): List<TransactionEntity>

    @Query("SELECT * FROM transaction_items")
    suspend fun getAllItemsForBackup(): List<TransactionItemEntity>

    @Query("SELECT * FROM payments")
    suspend fun getAllPaymentsForBackup(): List<PaymentEntity>

    @Insert
    suspend fun insertAllTransactions(transactions: List<TransactionEntity>): List<Long>

    @Insert
    suspend fun insertAllItems(items: List<TransactionItemEntity>)

    @Insert
    suspend fun insertAllPayments(payments: List<PaymentEntity>)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM transaction_items")
    suspend fun clearItems()

    @Query("DELETE FROM payments")
    suspend fun clearPayments()

}
