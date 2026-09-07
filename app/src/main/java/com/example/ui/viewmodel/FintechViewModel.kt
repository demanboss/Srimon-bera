package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.local.MoonEliteDatabase
import com.example.data.repository.FintechRepository
import com.example.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class OnboardingStep {
    object Intro : OnboardingStep()
    object PhoneInput : OnboardingStep()
    object OtpVerification : OnboardingStep()
    object ProfileInput : OnboardingStep()
    object LegalConsent : OnboardingStep()
    object Completed : OnboardingStep()
}

enum class NavigationTab {
    HOME,
    PAY,
    REWARDS,
    REFERRALS,
    PROFILE
}

class FintechViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        MoonEliteDatabase::class.java,
        "moon_elite_fintech.db"
    ).fallbackToDestructiveMigration().build()

    val repository = FintechRepository(db)

    // Navigation and Onboarding
    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _onboardingStep = MutableStateFlow<OnboardingStep>(OnboardingStep.Completed)
    val onboardingStep: StateFlow<OnboardingStep> = _onboardingStep.asStateFlow()

    // Mode state
    private val _currentMode = MutableStateFlow(PaymentMode.SANDBOX)
    val currentMode: StateFlow<PaymentMode> = _currentMode.asStateFlow()

    // User Profile
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Streams from Room DB
    val transactions: StateFlow<List<Transaction>> = repository.getTransactionsStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cashbackLedger: StateFlow<List<CashbackRewardItem>> = repository.getCashbackLedgerStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supportTickets: StateFlow<List<SupportTicket>> = repository.getSupportTicketsStream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val referralInfo = MutableStateFlow(ReferralInfo())
    val staticOffers = repository.getStaticOffers()

    // Dialog & Flow States
    val showScanPaySheet = MutableStateFlow(false)
    val showUpiIdDialog = MutableStateFlow(false)
    val showMyQrSheet = MutableStateFlow(false)
    val showUpiLiteDialog = MutableStateFlow(false)
    val showRequestMoneyDialog = MutableStateFlow(false)
    val showSupportDialog = MutableStateFlow(false)
    val showModeSwitcherDialog = MutableStateFlow(false)
    val showPrivacyPolicyDialog = MutableStateFlow(false)
    val showTermsDialog = MutableStateFlow(false)
    val showAccountDeletionDialog = MutableStateFlow(false)
    val showNotificationsDialog = MutableStateFlow(false)

    // Transaction Result Modal State
    val activeTransactionResult = MutableStateFlow<Transaction?>(null)
    val isPaymentProcessing = MutableStateFlow(false)
    val paymentErrorMessage = MutableStateFlow<String?>(null)

    // OTP Onboarding Transient State
    val phoneInput = MutableStateFlow("+91 ")
    val otpInput = MutableStateFlow("")
    val nameInput = MutableStateFlow("")
    val referralInput = MutableStateFlow("")

    init {
        // Pre-seed baseline transactions if empty
        viewModelScope.launch {
            // Check if baseline transaction exists
            val existing = repository.getActiveProvider().getTransaction("SANDBOX_TXN_SEED_1").getOrNull()
            if (existing != null) {
                // Ensure room has baseline items
                repository.executePayment(
                    recipientVpa = "starbucks.demo@bankupi",
                    recipientName = "Starbucks Coffee (Sandbox)",
                    amount = 450.0,
                    note = "Latte & Croissant",
                    paymentType = PaymentType.P2M_MERCHANT
                )
            }
        }
    }

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun togglePaymentMode(mode: PaymentMode) {
        _currentMode.value = mode
        repository.setPaymentMode(mode)
    }

    fun executePayment(
        recipientVpa: String,
        recipientName: String,
        amount: Double,
        note: String
    ) {
        viewModelScope.launch {
            isPaymentProcessing.value = true
            paymentErrorMessage.value = null
            val result = repository.executePayment(recipientVpa, recipientName, amount, note)
            isPaymentProcessing.value = false

            result.onSuccess { txn ->
                activeTransactionResult.value = txn
                // Update user points if cashback awarded
                if (txn.cashbackEarned > 0) {
                    _userProfile.value = _userProfile.value.copy(
                        rewardPointsBalance = _userProfile.value.rewardPointsBalance + txn.cashbackEarned
                    )
                }
            }.onFailure { error ->
                paymentErrorMessage.value = error.message ?: "Payment initiation failed."
            }
        }
    }

    fun submitSupportTicket(category: String, subject: String, description: String) {
        viewModelScope.launch {
            repository.createSupportTicket(category, subject, description)
            showSupportDialog.value = false
        }
    }

    fun verifyOtpAndProceed() {
        if (otpInput.value.length == 6) {
            _onboardingStep.value = OnboardingStep.ProfileInput
        }
    }

    fun completeProfileAndProceed() {
        if (nameInput.value.isNotBlank()) {
            _userProfile.value = _userProfile.value.copy(
                fullName = nameInput.value,
                phoneNumber = phoneInput.value
            )
            _onboardingStep.value = OnboardingStep.LegalConsent
        }
    }

    fun acceptLegalConsentAndFinish() {
        _onboardingStep.value = OnboardingStep.Completed
    }

    fun resetOnboarding() {
        _onboardingStep.value = OnboardingStep.Intro
    }
}
