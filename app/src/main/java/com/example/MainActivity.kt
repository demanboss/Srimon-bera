package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.PaymentMode
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.FintechViewModel
import com.example.ui.viewmodel.NavigationTab
import com.example.ui.viewmodel.OnboardingStep

class MainActivity : ComponentActivity() {
    private val viewModel: FintechViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MoonEliteApp(viewModel)
            }
        }
    }
}

@Composable
fun MoonEliteApp(viewModel: FintechViewModel) {
    val context = LocalContext.current
    val currentTab by viewModel.currentTab.collectAsState()
    val onboardingStep by viewModel.onboardingStep.collectAsState()
    val currentMode by viewModel.currentMode.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val cashbackLedger by viewModel.cashbackLedger.collectAsState()
    val referralInfo by viewModel.referralInfo.collectAsState()

    // Dialog state collections
    val showModeSwitcher by viewModel.showModeSwitcherDialog.collectAsState()
    val showUpiIdDialog by viewModel.showUpiIdDialog.collectAsState()
    val showScanPaySheet by viewModel.showScanPaySheet.collectAsState()
    val showMyQrSheet by viewModel.showMyQrSheet.collectAsState()
    val showUpiLiteDialog by viewModel.showUpiLiteDialog.collectAsState()
    val showSupportDialog by viewModel.showSupportDialog.collectAsState()
    val showAccountDeletion by viewModel.showAccountDeletionDialog.collectAsState()
    val showPrivacyPolicy by viewModel.showPrivacyPolicyDialog.collectAsState()
    val showTerms by viewModel.showTermsDialog.collectAsState()
    val activeTxnResult by viewModel.activeTransactionResult.collectAsState()

    // Transient Onboarding Inputs
    val phoneInput by viewModel.phoneInput.collectAsState()
    val otpInput by viewModel.otpInput.collectAsState()
    val nameInput by viewModel.nameInput.collectAsState()
    val referralInput by viewModel.referralInput.collectAsState()

    if (onboardingStep != OnboardingStep.Completed) {
        OnboardingScreen(
            currentStep = onboardingStep,
            phoneInput = phoneInput,
            onPhoneChange = { viewModel.phoneInput.value = it },
            otpInput = otpInput,
            onOtpChange = { viewModel.otpInput.value = it },
            nameInput = nameInput,
            onNameChange = { viewModel.nameInput.value = it },
            referralInput = referralInput,
            onReferralChange = { viewModel.referralInput.value = it },
            onGetOtp = { viewModel.verifyOtpAndProceed() },
            onVerifyOtp = { viewModel.verifyOtpAndProceed() },
            onCompleteProfile = { viewModel.completeProfileAndProceed() },
            onAcceptConsentAndFinish = { viewModel.acceptLegalConsentAndFinish() }
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = ElegantNavBg,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ElegantBorderSubtle)
                        .testTag("main_bottom_nav")
                ) {
                    val tabs = listOf(
                        Triple(NavigationTab.HOME, "Home", Icons.Default.Home),
                        Triple(NavigationTab.PAY, "Pay", Icons.Default.Send),
                        Triple(NavigationTab.REWARDS, "Rewards", Icons.Default.CardGiftcard),
                        Triple(NavigationTab.REFERRALS, "Refer", Icons.Default.GroupAdd),
                        Triple(NavigationTab.PROFILE, "Profile", Icons.Default.Person)
                    )

                    tabs.forEach { (tab, label, icon) ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tab) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) ElegantBlue else TextSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    color = if (isSelected) ElegantBlue else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0x262D60FF),
                                selectedIconColor = ElegantBlue,
                                unselectedIconColor = TextSecondary
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            },
            containerColor = ObsidianBg,
            contentWindowInsets = WindowInsets.systemBars
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (currentTab) {
                    NavigationTab.HOME -> {
                        HomeScreen(
                            userProfile = userProfile,
                            currentMode = currentMode,
                            transactions = transactions,
                            offers = viewModel.staticOffers,
                            onModeClick = { viewModel.showModeSwitcherDialog.value = true },
                            onNotificationClick = {
                                Toast.makeText(context, "No unread transaction alerts", Toast.LENGTH_SHORT).show()
                            },
                            onSupportClick = { viewModel.showSupportDialog.value = true },
                            onScanPayClick = { viewModel.showScanPaySheet.value = true },
                            onSendMoneyClick = { viewModel.showUpiIdDialog.value = true },
                            onUpiIdClick = { viewModel.showUpiIdDialog.value = true },
                            onMyQrClick = { viewModel.showMyQrSheet.value = true },
                            onRequestMoneyClick = {
                                Toast.makeText(context, "UPI Collect Request initiated", Toast.LENGTH_SHORT).show()
                            },
                            onUpiLiteClick = { viewModel.showUpiLiteDialog.value = true },
                            onViewRewardsClick = { viewModel.selectTab(NavigationTab.REWARDS) },
                            onShareReferralClick = { viewModel.selectTab(NavigationTab.REFERRALS) },
                            onTransactionClick = { txn -> viewModel.activeTransactionResult.value = txn }
                        )
                    }
                    NavigationTab.PAY -> {
                        PayScreen(
                            currentMode = currentMode,
                            onModeClick = { viewModel.showModeSwitcherDialog.value = true },
                            onScanPayClick = { viewModel.showScanPaySheet.value = true },
                            onUpiIdClick = { viewModel.showUpiIdDialog.value = true },
                            onMyQrClick = { viewModel.showMyQrSheet.value = true },
                            onUpiLiteClick = { viewModel.showUpiLiteDialog.value = true },
                            onPayContact = { vpa, name ->
                                viewModel.executePayment(vpa, name, 150.0, "Quick Transfer")
                            }
                        )
                    }
                    NavigationTab.REWARDS -> {
                        RewardsScreen(
                            currentMode = currentMode,
                            rewardBalance = userProfile.rewardPointsBalance,
                            pendingBalance = userProfile.pendingCashbackBalance,
                            cashbackLedger = cashbackLedger,
                            onModeClick = { viewModel.showModeSwitcherDialog.value = true }
                        )
                    }
                    NavigationTab.REFERRALS -> {
                        ReferralsScreen(
                            referralInfo = referralInfo,
                            currentMode = currentMode,
                            onModeClick = { viewModel.showModeSwitcherDialog.value = true },
                            onCopyCode = {
                                Toast.makeText(context, "Referral Code MOON777 copied!", Toast.LENGTH_SHORT).show()
                            },
                            onShareLink = {
                                Toast.makeText(context, "Sharing invite link via Apps...", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    NavigationTab.PROFILE -> {
                        ProfileScreen(
                            userProfile = userProfile,
                            currentMode = currentMode,
                            onModeClick = { viewModel.showModeSwitcherDialog.value = true },
                            onOpenUpiLite = { viewModel.showUpiLiteDialog.value = true },
                            onOpenSupport = { viewModel.showSupportDialog.value = true },
                            onOpenPrivacyPolicy = { viewModel.showPrivacyPolicyDialog.value = true },
                            onOpenTerms = { viewModel.showTermsDialog.value = true },
                            onOpenAccountDeletion = { viewModel.showAccountDeletionDialog.value = true },
                            onResetOnboarding = { viewModel.resetOnboarding() }
                        )
                    }
                }
            }
        }
    }

    // Interactive Dialogs
    if (showModeSwitcher) {
        ModeSwitcherDialog(
            currentMode = currentMode,
            onSelectMode = { newMode -> viewModel.togglePaymentMode(newMode) },
            onDismiss = { viewModel.showModeSwitcherDialog.value = false }
        )
    }

    if (showUpiIdDialog) {
        UpiIdPaymentDialog(
            onDismiss = { viewModel.showUpiIdDialog.value = false },
            onPay = { vpa, name, amount, note ->
                viewModel.executePayment(vpa, name, amount, note)
            }
        )
    }

    if (showScanPaySheet) {
        ScanPaySheet(
            onDismiss = { viewModel.showScanPaySheet.value = false },
            onPaymentConfirmed = { vpa, name, amount, note ->
                viewModel.executePayment(vpa, name, amount, note)
            }
        )
    }

    if (showMyQrSheet) {
        MyQrSheet(
            vpa = userProfile.primaryVpa,
            userName = userProfile.fullName,
            onDismiss = { viewModel.showMyQrSheet.value = false }
        )
    }

    if (showUpiLiteDialog) {
        UPILiteSheet(
            balance = 1200.0,
            onTopUp = { amount ->
                Toast.makeText(context, "UPI Lite topped up by ₹${amount.toInt()}", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { viewModel.showUpiLiteDialog.value = false }
        )
    }

    if (showSupportDialog) {
        SupportTicketDialog(
            onDismiss = { viewModel.showSupportDialog.value = false },
            onSubmitTicket = { category, subject, description ->
                viewModel.submitSupportTicket(category, subject, description)
                Toast.makeText(context, "Support ticket registered successfully", Toast.LENGTH_LONG).show()
            }
        )
    }

    if (showAccountDeletion) {
        AccountDeletionDialog(
            onConfirmRequest = {
                Toast.makeText(context, "Account deletion request submitted. PII will be anonymized.", Toast.LENGTH_LONG).show()
                viewModel.resetOnboarding()
            },
            onDismiss = { viewModel.showAccountDeletionDialog.value = false }
        )
    }

    if (showPrivacyPolicy) {
        LegalPolicyDialog(
            title = "Privacy Policy (DPDP Act)",
            content = """
                MOON Elite Compliance & Privacy Disclosures:
                1. Data Protection: We process your data in strict compliance with the Digital Personal Data Protection Act, 2023.
                2. UPI Security: We NEVER store, ask for, or process your secret UPI PIN.
                3. Camera Usage: Camera access is requested solely for live on-device optical QR scanning in 'Scan & Pay'.
                4. Statutory Retention: Under PMLA and RBI Master Directions, transaction logs are securely quarantined for 5 years.
                5. Grievance Redressal: Contact grievance@moonelite.com.
            """.trimIndent(),
            onDismiss = { viewModel.showPrivacyPolicyDialog.value = false }
        )
    }

    if (showTerms) {
        LegalPolicyDialog(
            title = "Terms & UPI Disclaimer",
            content = """
                MOON Elite Terms of Service & UPI Operations:
                1. TPAP Authorization: Payments operate via authorized PSP banking partners or in segregated Sandbox Mode.
                2. Non-Deposit Rewards: Cashback represents promotional credits, not savings bank deposits or escrowed funds.
                3. Anti-Abuse: Self-referrals and emulator spoofing are strictly prohibited and result in reward cancellation.
                4. Dispute Resolution: Dedicated 24x7 customer support with formal escalation paths.
            """.trimIndent(),
            onDismiss = { viewModel.showTermsDialog.value = false }
        )
    }

    activeTxnResult?.let { txn ->
        TransactionResultDialog(
            transaction = txn,
            onDismiss = { viewModel.activeTransactionResult.value = null }
        )
    }
}

// Backward compatibility helper for existing test cases
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
