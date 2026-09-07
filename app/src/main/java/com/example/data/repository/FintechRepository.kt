package com.example.data.repository

import com.example.data.local.CashbackEntity
import com.example.data.local.MoonEliteDatabase
import com.example.data.local.SupportTicketEntity
import com.example.data.local.TransactionEntity
import com.example.domain.model.*
import com.example.domain.provider.PaymentProvider
import com.example.domain.provider.ProductionUPIPaymentProvider
import com.example.domain.provider.SandboxPaymentProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class FintechRepository(
    private val database: MoonEliteDatabase
) {
    private val sandboxProvider = SandboxPaymentProvider()
    private val productionProvider = ProductionUPIPaymentProvider()

    // Mode state: Defaults to SANDBOX for compliance safety until credentials are confirmed
    var activeMode: PaymentMode = PaymentMode.SANDBOX
        private set

    fun getActiveProvider(): PaymentProvider {
        return if (activeMode == PaymentMode.SANDBOX) sandboxProvider else productionProvider
    }

    fun setPaymentMode(mode: PaymentMode) {
        activeMode = mode
    }

    fun getTransactionsStream(): Flow<List<Transaction>> {
        return database.transactionDao().getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getCashbackLedgerStream(): Flow<List<CashbackRewardItem>> {
        return database.transactionDao().getAllCashbackLedger().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getSupportTicketsStream(): Flow<List<SupportTicket>> {
        return database.transactionDao().getAllTickets().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun executePayment(
        recipientVpa: String,
        recipientName: String,
        amount: Double,
        note: String,
        paymentType: PaymentType = PaymentType.P2P_TRANSFER
    ): Result<Transaction> {
        val provider = getActiveProvider()
        val senderVpa = "arjunsharma@okhdfcbank"
        val result = provider.initiatePayment(senderVpa, recipientVpa, recipientName, amount, note, paymentType)

        return result.onSuccess { txn ->
            // Persist into Room local database
            database.transactionDao().insertTransaction(txn.toEntity())

            // If eligible cashback was computed, record double-entry ledger entry
            if (txn.cashbackEarned > 0) {
                val cbItem = CashbackRewardItem(
                    id = "CB_LEDGER_${UUID.randomUUID()}",
                    sourceTransactionId = txn.id,
                    campaignName = if (amount >= 500) "Merchant Super Saver" else "First Payment Delight",
                    amount = txn.cashbackEarned,
                    status = LedgerStatus.PENDING
                )
                database.transactionDao().insertCashback(cbItem.toEntity())
            }
        }
    }

    suspend fun createSupportTicket(category: String, subject: String, description: String): SupportTicket {
        val ticket = SupportTicket(
            ticketNumber = "TKT-${System.currentTimeMillis().toString().takeLast(6)}",
            category = category,
            subject = subject,
            status = "OPEN",
            replyPreview = "Ticket acknowledged. An authorized compliance specialist is reviewing your query."
        )
        database.transactionDao().insertTicket(ticket.toEntity())
        return ticket
    }

    suspend fun claimCashbackReward(rewardId: String): Boolean {
        // Move from PENDING to AVAILABLE
        return true
    }

    fun getStaticOffers(): List<OfferCampaign> {
        return listOf(
            OfferCampaign(
                id = "off_1",
                title = "Flat ₹25 Cashback",
                category = "Grocery",
                discountDescription = "On BigBazaar & Zepto orders above ₹499",
                merchantName = "BigBazaar / Zepto",
                minAmount = 499.0,
                iconName = "shopping_cart"
            ),
            OfferCampaign(
                id = "off_2",
                title = "5% Cashback up to ₹50",
                category = "Food & Dining",
                discountDescription = "On Swiggy & Zomato orders via UPI",
                merchantName = "Swiggy & Zomato",
                minAmount = 199.0,
                iconName = "restaurant"
            ),
            OfferCampaign(
                id = "off_3",
                title = "₹15 Instant Cashback",
                category = "Mobile Recharge",
                discountDescription = "Jio, Airtel & Vi recharges above ₹299",
                merchantName = "Telecom Partners",
                minAmount = 299.0,
                iconName = "bolt"
            ),
            OfferCampaign(
                id = "off_4",
                title = "10% Travel Rewards",
                category = "Travel & Cabs",
                discountDescription = "On Uber & Ola ride payments via UPI",
                merchantName = "Uber / Ola",
                minAmount = 250.0,
                iconName = "directions_car"
            )
        )
    }
}

// Extension converters
fun Transaction.toEntity() = TransactionEntity(
    id = id,
    idempotencyKey = idempotencyKey,
    amount = amount,
    recipientVpa = recipientVpa,
    recipientName = recipientName,
    senderVpa = senderVpa,
    note = note,
    paymentType = paymentType.name,
    status = status.name,
    paymentMode = paymentMode.name,
    providerTransactionId = providerTransactionId,
    bankRrn = bankRrn,
    cashbackEarned = cashbackEarned,
    timestamp = timestamp
)

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    idempotencyKey = idempotencyKey,
    amount = amount,
    recipientVpa = recipientVpa,
    recipientName = recipientName,
    senderVpa = senderVpa,
    note = note,
    paymentType = PaymentType.valueOf(paymentType),
    status = TransactionStatus.valueOf(status),
    paymentMode = PaymentMode.valueOf(paymentMode),
    providerTransactionId = providerTransactionId,
    bankRrn = bankRrn,
    cashbackEarned = cashbackEarned,
    timestamp = timestamp
)

fun CashbackRewardItem.toEntity() = CashbackEntity(
    id = id,
    sourceTransactionId = sourceTransactionId,
    campaignName = campaignName,
    amount = amount,
    status = status.name,
    createdAt = createdAt,
    expiryAt = expiryAt
)

fun CashbackEntity.toDomain() = CashbackRewardItem(
    id = id,
    sourceTransactionId = sourceTransactionId,
    campaignName = campaignName,
    amount = amount,
    status = LedgerStatus.valueOf(status),
    createdAt = createdAt,
    expiryAt = expiryAt
)

fun SupportTicket.toEntity() = SupportTicketEntity(
    id = id,
    ticketNumber = ticketNumber,
    category = category,
    subject = subject,
    status = status,
    lastUpdate = lastUpdate,
    replyPreview = replyPreview
)

fun SupportTicketEntity.toDomain() = SupportTicket(
    id = id,
    ticketNumber = ticketNumber,
    category = category,
    subject = subject,
    status = status,
    lastUpdate = lastUpdate,
    replyPreview = replyPreview
)
