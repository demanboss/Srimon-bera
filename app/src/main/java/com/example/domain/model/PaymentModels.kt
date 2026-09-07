package com.example.domain.model

import java.util.UUID

enum class PaymentMode {
    SANDBOX,
    PRODUCTION
}

enum class TransactionStatus {
    INITIATED,
    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED,
    EXPIRED,
    REFUNDED,
    REVERSED
}

enum class PaymentType {
    P2P_TRANSFER,
    P2M_MERCHANT,
    QR_PAYMENT,
    UPI_LITE,
    BILL_PAY
}

enum class LedgerStatus {
    PENDING,
    AVAILABLE,
    EXPIRED,
    REVERSED,
    CANCELLED
}

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val idempotencyKey: String = UUID.randomUUID().toString(),
    val amount: Double,
    val recipientVpa: String,
    val recipientName: String,
    val senderVpa: String,
    val note: String = "",
    val paymentType: PaymentType = PaymentType.P2P_TRANSFER,
    val status: TransactionStatus = TransactionStatus.INITIATED,
    val paymentMode: PaymentMode = PaymentMode.SANDBOX,
    val providerTransactionId: String? = null,
    val bankRrn: String? = null,
    val cashbackEarned: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

data class BankAccount(
    val id: String,
    val bankName: String,
    val ifscPrefix: String,
    val maskedAccountNumber: String,
    val accountHolderName: String,
    val vpaAddress: String,
    val isPrimary: Boolean = false,
    val isUpiLiteSupported: Boolean = true
)

data class UPILiteAccount(
    val isEnabled: Boolean = false,
    val balance: Double = 0.0,
    val maxLimit: Double = 2000.0,
    val singleTransactionLimit: Double = 500.0
)

data class CashbackRewardItem(
    val id: String = UUID.randomUUID().toString(),
    val sourceTransactionId: String,
    val campaignName: String,
    val amount: Double,
    val status: LedgerStatus = LedgerStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val expiryAt: Long = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000)
)

data class ReferralInfo(
    val referralCode: String = "MOON777",
    val inviteUrl: String = "https://moonelite.com/invite/MOON777",
    val totalInvited: Int = 5,
    val registered: Int = 4,
    val qualifiedCount: Int = 3,
    val totalRewardsEarned: Double = 1500.0,
    val pendingRewards: Double = 500.0,
    val maxRewardPerReferral: Double = 500.0,
    val minQualifyingTxn: Double = 199.0
)

data class OfferCampaign(
    val id: String,
    val title: String,
    val category: String,
    val discountDescription: String,
    val merchantName: String,
    val minAmount: Double,
    val iconName: String
)

data class SupportTicket(
    val id: String = UUID.randomUUID().toString(),
    val ticketNumber: String,
    val category: String,
    val subject: String,
    val status: String = "OPEN", // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    val lastUpdate: Long = System.currentTimeMillis(),
    val replyPreview: String = "Our payments team is investigating this transaction."
)

data class NotificationMessage(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "TRANSACTION",
    val isRead: Boolean = false
)

data class UserProfile(
    val id: String = "usr_demo_01",
    val fullName: String = "Arjun Sharma",
    val phoneNumber: String = "+91 98765 43210",
    val email: String = "arjun.sharma@example.com",
    val primaryVpa: String = "arjunsharma@bankupi",
    val rewardPointsBalance: Double = 1250.0,
    val pendingCashbackBalance: Double = 75.0,
    val isKycVerified: Boolean = true,
    val referralCode: String = "MOON777"
)
