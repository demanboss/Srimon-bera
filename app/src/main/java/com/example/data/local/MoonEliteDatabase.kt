package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "local_transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val idempotencyKey: String,
    val amount: Double,
    val recipientVpa: String,
    val recipientName: String,
    val senderVpa: String,
    val note: String,
    val paymentType: String,
    val status: String,
    val paymentMode: String,
    val providerTransactionId: String?,
    val bankRrn: String?,
    val cashbackEarned: Double,
    val timestamp: Long
)

@Entity(tableName = "local_cashback_ledger")
data class CashbackEntity(
    @PrimaryKey val id: String,
    val sourceTransactionId: String,
    val campaignName: String,
    val amount: Double,
    val status: String,
    val createdAt: Long,
    val expiryAt: Long
)

@Entity(tableName = "local_support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val id: String,
    val ticketNumber: String,
    val category: String,
    val subject: String,
    val status: String,
    val lastUpdate: Long,
    val replyPreview: String
)

@Dao
interface TransactionDao {
    @Query("SELECT * FROM local_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM local_transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): TransactionEntity?

    @Query("UPDATE local_transactions SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("SELECT * FROM local_cashback_ledger ORDER BY createdAt DESC")
    fun getAllCashbackLedger(): Flow<List<CashbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCashback(cashback: CashbackEntity)

    @Query("SELECT * FROM local_support_tickets ORDER BY lastUpdate DESC")
    fun getAllTickets(): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicketEntity)
}

@Database(
    entities = [TransactionEntity::class, CashbackEntity::class, SupportTicketEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MoonEliteDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
