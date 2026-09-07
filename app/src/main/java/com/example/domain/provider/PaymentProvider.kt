package com.example.domain.provider

import com.example.domain.model.BankAccount
import com.example.domain.model.PaymentMode
import com.example.domain.model.PaymentType
import com.example.domain.model.Transaction
import com.example.domain.model.TransactionStatus
import com.example.domain.model.UPILiteAccount

/**
 * Compliance-First UPI Payment Provider Contract.
 * Strict segregation between Mode A (SANDBOX) and Mode B (PRODUCTION).
 */
interface PaymentProvider {
    fun getProviderName(): String
    fun getPaymentMode(): PaymentMode

    suspend fun initiatePayment(
        senderVpa: String,
        recipientVpa: String,
        recipientName: String,
        amount: Double,
        note: String,
        paymentType: PaymentType
    ): Result<Transaction>

    suspend fun checkPaymentStatus(transactionId: String): Result<TransactionStatus>

    suspend fun getTransaction(transactionId: String): Result<Transaction>

    suspend fun refundPayment(transactionId: String, amount: Double, reason: String): Result<Boolean>

    suspend fun createPaymentRequest(
        recipientVpa: String,
        amount: Double,
        note: String
    ): Result<String>

    fun generateQRCode(vpa: String, name: String, amount: Double? = null): String

    suspend fun validateQRCode(rawQr: String): Result<Triple<String, String, Double?>> // (VPA, Name, SuggestedAmount)

    suspend fun getSupportedBanks(): Result<List<BankAccount>>

    suspend fun linkBankAccount(bankName: String, accountNumber: String): Result<BankAccount>

    suspend fun unlinkBankAccount(bankAccountId: String): Result<Boolean>

    suspend fun enableUPILite(bankAccountId: String): Result<UPILiteAccount>

    suspend fun disableUPILite(): Result<Boolean>

    suspend fun getUPILiteBalance(): Result<Double>

    suspend fun topUpUPILite(amount: Double): Result<Double>

    suspend fun getUPILiteTransactions(): Result<List<Transaction>>
}
