package com.example.domain.provider

import com.example.domain.model.BankAccount
import com.example.domain.model.PaymentMode
import com.example.domain.model.PaymentType
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionStatus
import com.example.domain.model.UPILiteAccount
import kotlinx.coroutines.delay
import java.net.URLDecoder
import java.util.UUID
import kotlin.random.Random

/**
 * MODE A: SandboxPaymentProvider
 * Strictly segregated simulation layer for testing and demonstration.
 * Transparently labels all transactions as DEMO / SANDBOX.
 */
class SandboxPaymentProvider : PaymentProvider {

    private val inMemoryTransactions = mutableListOf<Transaction>()
    private var upiLiteAccount = UPILiteAccount(
        isEnabled = true,
        balance = 1200.0,
        maxLimit = 2000.0,
        singleTransactionLimit = 500.0
    )

    private val linkedBanks = mutableListOf(
        BankAccount(
            id = "bank_hdfc_01",
            bankName = "HDFC Bank (Sandbox)",
            ifscPrefix = "HDFC",
            maskedAccountNumber = "•••• 4812",
            accountHolderName = "Arjun Sharma",
            vpaAddress = "arjunsharma@okhdfcbank",
            isPrimary = true,
            isUpiLiteSupported = true
        ),
        BankAccount(
            id = "bank_sbi_02",
            bankName = "State Bank of India (Sandbox)",
            ifscPrefix = "SBIN",
            maskedAccountNumber = "•••• 9134",
            accountHolderName = "Arjun Sharma",
            vpaAddress = "arjunsharma@oksbi",
            isPrimary = false,
            isUpiLiteSupported = true
        )
    )

    init {
        // Seed baseline verified sandbox transactions
        inMemoryTransactions.add(
            Transaction(
                id = "SANDBOX_TXN_SEED_1",
                amount = 450.0,
                recipientVpa = "starbucks.demo@bankupi",
                recipientName = "Starbucks Coffee (Sandbox)",
                senderVpa = "arjunsharma@okhdfcbank",
                note = "Latte & Croissant",
                paymentType = PaymentType.P2M_MERCHANT,
                status = TransactionStatus.SUCCESS,
                paymentMode = PaymentMode.SANDBOX,
                providerTransactionId = "PSP_SB_881920",
                bankRrn = "426189918231",
                cashbackEarned = 22.50,
                timestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000)
            )
        )
        inMemoryTransactions.add(
            Transaction(
                id = "SANDBOX_TXN_SEED_2",
                amount = 1200.0,
                recipientVpa = "supermarket.demo@icici",
                recipientName = "Fresh Groceries (Sandbox)",
                senderVpa = "arjunsharma@okhdfcbank",
                note = "Weekly groceries",
                paymentType = PaymentType.QR_PAYMENT,
                status = TransactionStatus.SUCCESS,
                paymentMode = PaymentMode.SANDBOX,
                providerTransactionId = "PSP_SB_881921",
                bankRrn = "426189918232",
                cashbackEarned = 50.00,
                timestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
            )
        )
    }

    override fun getProviderName(): String = "MOON_ELITE_SANDBOX_ADAPTER"

    override fun getPaymentMode(): PaymentMode = PaymentMode.SANDBOX

    override suspend fun initiatePayment(
        senderVpa: String,
        recipientVpa: String,
        recipientName: String,
        amount: Double,
        note: String,
        paymentType: PaymentType
    ): Result<Transaction> {
        delay(800) // Simulate network latency

        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be greater than zero."))
        }

        // Calculate sample promotional cashback (5% capped at ₹50)
        val cashback = if (amount >= 100.0) {
            (amount * 0.05).coerceAtMost(50.0)
        } else 0.0

        val txn = Transaction(
            id = "SANDBOX_TXN_${System.currentTimeMillis()}",
            idempotencyKey = UUID.randomUUID().toString(),
            amount = amount,
            recipientVpa = recipientVpa,
            recipientName = recipientName,
            senderVpa = senderVpa,
            note = note.ifBlank { "Payment via MOON Elite Sandbox" },
            paymentType = paymentType,
            status = TransactionStatus.SUCCESS, // Sandbox completes deterministically
            paymentMode = PaymentMode.SANDBOX,
            providerTransactionId = "PSP_SB_${Random.nextLong(100000, 999999)}",
            bankRrn = "426${Random.nextLong(100000000L, 999999999L)}",
            cashbackEarned = Math.round(cashback * 100.0) / 100.0,
            timestamp = System.currentTimeMillis()
        )

        inMemoryTransactions.add(0, txn)
        return Result.success(txn)
    }

    override suspend fun checkPaymentStatus(transactionId: String): Result<TransactionStatus> {
        delay(400)
        val txn = inMemoryTransactions.find { it.id == transactionId }
        return if (txn != null) Result.success(txn.status)
        else Result.failure(NoSuchElementException("Transaction not found in Sandbox."))
    }

    override suspend fun getTransaction(transactionId: String): Result<Transaction> {
        val txn = inMemoryTransactions.find { it.id == transactionId }
        return if (txn != null) Result.success(txn)
        else Result.failure(NoSuchElementException("Transaction not found."))
    }

    override suspend fun refundPayment(transactionId: String, amount: Double, reason: String): Result<Boolean> {
        delay(600)
        val index = inMemoryTransactions.indexOfFirst { it.id == transactionId }
        return if (index != -1) {
            val original = inMemoryTransactions[index]
            inMemoryTransactions[index] = original.copy(status = TransactionStatus.REFUNDED)
            Result.success(true)
        } else {
            Result.failure(NoSuchElementException("Transaction not found."))
        }
    }

    override suspend fun createPaymentRequest(
        recipientVpa: String,
        amount: Double,
        note: String
    ): Result<String> {
        delay(400)
        val requestId = "REQ_${System.currentTimeMillis()}"
        return Result.success("Payment request of ₹$amount sent to $recipientVpa (Ref: $requestId)")
    }

    override fun generateQRCode(vpa: String, name: String, amount: Double?): String {
        val base = "upi://pay?pa=$vpa&pn=${name.replace(" ", "%20")}&cu=INR"
        return if (amount != null && amount > 0) "$base&am=${"%.2f".format(amount)}" else base
    }

    override suspend fun validateQRCode(rawQr: String): Result<Triple<String, String, Double?>> {
        delay(250)
        return try {
            if (!rawQr.startsWith("upi://pay")) {
                return Result.failure(IllegalArgumentException("Invalid UPI QR code format. Must begin with upi://pay"))
            }

            val query = rawQr.substringAfter("?", "")
            val params = query.split("&").associate {
                val parts = it.split("=")
                val key = parts.getOrNull(0) ?: ""
                val value = parts.getOrNull(1) ?: ""
                key to URLDecoder.decode(value, "UTF-8")
            }

            val pa = params["pa"] ?: return Result.failure(IllegalArgumentException("Missing payee VPA address."))
            if (!pa.contains("@")) {
                return Result.failure(IllegalArgumentException("Malformed payee VPA handle."))
            }

            val pn = params["pn"] ?: "Merchant (Verified Sandbox)"
            val am = params["am"]?.toDoubleOrNull()

            Result.success(Triple(pa, pn, am))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSupportedBanks(): Result<List<BankAccount>> {
        delay(300)
        return Result.success(linkedBanks.toList())
    }

    override suspend fun linkBankAccount(bankName: String, accountNumber: String): Result<BankAccount> {
        delay(700)
        val masked = if (accountNumber.length >= 4) "•••• " + accountNumber.takeLast(4) else "•••• 1234"
        val newAccount = BankAccount(
            id = "bank_${System.currentTimeMillis()}",
            bankName = "$bankName (Sandbox)",
            ifscPrefix = bankName.take(4).uppercase(),
            maskedAccountNumber = masked,
            accountHolderName = "Arjun Sharma",
            vpaAddress = "arjunsharma@${bankName.lowercase().filter { it.isLetter() }}",
            isPrimary = false,
            isUpiLiteSupported = true
        )
        linkedBanks.add(newAccount)
        return Result.success(newAccount)
    }

    override suspend fun unlinkBankAccount(bankAccountId: String): Result<Boolean> {
        delay(400)
        linkedBanks.removeAll { it.id == bankAccountId }
        return Result.success(true)
    }

    override suspend fun enableUPILite(bankAccountId: String): Result<UPILiteAccount> {
        delay(500)
        upiLiteAccount = upiLiteAccount.copy(isEnabled = true)
        return Result.success(upiLiteAccount)
    }

    override suspend fun disableUPILite(): Result<Boolean> {
        delay(400)
        upiLiteAccount = upiLiteAccount.copy(isEnabled = false)
        return Result.success(true)
    }

    override suspend fun getUPILiteBalance(): Result<Double> {
        return Result.success(upiLiteAccount.balance)
    }

    override suspend fun topUpUPILite(amount: Double): Result<Double> {
        delay(600)
        if (upiLiteAccount.balance + amount > upiLiteAccount.maxLimit) {
            return Result.failure(IllegalStateException("UPI Lite balance cannot exceed ₹${upiLiteAccount.maxLimit}."))
        }
        val newBalance = upiLiteAccount.balance + amount
        upiLiteAccount = upiLiteAccount.copy(balance = newBalance)
        return Result.success(newBalance)
    }

    override suspend fun getUPILiteTransactions(): Result<List<Transaction>> {
        return Result.success(inMemoryTransactions.filter { it.paymentType == PaymentType.UPI_LITE })
    }
}
